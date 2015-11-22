package com.bocse.multumesc;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import com.bocse.multumesc.parser.DeputyPresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import com.bocse.multumesc.statistics.StatsProcessor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by bocse on 22.11.2015.
 */
public class DeputyCrawler {
    private final static Logger logger = Logger.getLogger(MultumescMain.class.toString());
    public final FileConfiguration configuration = new PropertiesConfiguration();
    public final FileConfiguration state = new PropertiesConfiguration();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes = new ConcurrentHashMap<>();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes2 = new ConcurrentHashMap<>();
    public final Map<String, Person> persons = new ConcurrentHashMap<String, Person>();
    public final SortedMap<Long, String> subjectMatters = new ConcurrentSkipListMap<>();
    private final String configFile;
    private final String stateFile;
    private final Object syncObject = new Object();
    public Long maxPerson;
    public Long firstPerson;
    public Integer threadNumber;

    public DeputyCrawler(String configFile, String stateFile) {
        this.configFile = configFile;
        this.stateFile = stateFile;
    }

    public synchronized void setStateProperty(String key, Object value) {
        state.setProperty(key, value);
    }

    public void init() throws ConfigurationException {
        if (new File(stateFile).exists())
            state.load(stateFile);
        state.setFileName(stateFile);
        state.setAutoSave(true);
        state.setProperty("finalizedCrawls.startTimestamp", System.currentTimeMillis());

        configuration.load(configFile);
        logger.info(configuration.toString());
        maxPerson = configuration.getLong("assumptions.lastPerson", 417);
        threadNumber = configuration.getInt("working.mode.threadsNumber", 10);
        firstPerson = configuration.getLong("assumptions.firstPerson", 1);
        if (threadNumber == 1) {
            if (configuration.getBoolean("working.mode.resumeLastCrawl"))
                firstPerson = Math.max(firstPerson, 1 + state.getLong("partialCrawls.lastProfile", 1L));
        } else if (configuration.getBoolean("working.mode.resumeLastCrawl")) {
            logger.warning("Resume automatic crawl turned off.");
        }
    }

    public void crawl() throws IOException, InterruptedException, ConfigurationException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        List<Future<Person>> futureList = Collections.synchronizedList(new ArrayList<Future<Person>>());
        for (Long personIdIndex = firstPerson; personIdIndex <= maxPerson; personIdIndex++) {
            final Long personId = new Long(personIdIndex.longValue());
            Future future = executorService.submit(new Callable() {
                public Object call() throws IOException, InterruptedException, ConfigurationException {
                    final DeputyPresenceParser deputyPresenceParser = new DeputyPresenceParser();

                    Person person = new Person();
                    person.setPersonId(personId);
                    deputyPresenceParser.getPersonProfile(person);
                    deputyPresenceParser.getPersonVotes(person, subjectMatters);

                    synchronized (syncObject) {
                        StatsProcessor stats = new StatsProcessor(person);
                        Map<VoteTypes, AtomicLong> globalStats = stats.process();
                        Map<VoteTypes, AtomicLong> last30Stats = stats.process(new DateTime().minusDays(30), new DateTime());
                        Map<VoteTypes, AtomicLong> last90Stats = stats.process(new DateTime().minusDays(90), new DateTime());
                        Map<VoteTypes, AtomicLong> last365Stats = stats.process(new DateTime().minusDays(365), new DateTime());

                        person.setStatsAllTerm(globalStats);
                        person.setStatsLast30Days(last30Stats);
                        person.setStatsLast90Days(last90Stats);
                        person.setStatsLast365Days(last365Stats);

                        SortedMap<Long, Vote> tempVote = person.getVoteMap();
                        person.setVoteMap(null);
                        JsonSerializer.serialize(configuration.getString("output.profileStats.path"), personId, person);

                        person.setVoteMap(tempVote);
                        JsonSerializer.serialize(configuration.getString("output.profile.path"), personId, person);
                        //
                        if (personId % 10 == 1 || personId == maxPerson)
                            JsonSerializer.serialize(configuration.getString("output.subject.path"), 0L, subjectMatters);
                        //
                        persons.put(person.getFullName(), person);
                        if (person.getActive()) {
                            //Compute party - version 1
                            List<Person> personWrapper = new ArrayList<>();
                            personWrapper.add(person);
                            Map<Integer, Map<String, Map<VoteTypes, AtomicLong>>> partyResults = new HashMap<>();
                            partyResults.put(30, stats.processPartyFromPerson(partyVotes, personWrapper, 30));
                            partyResults.put(90, stats.processPartyFromPerson(partyVotes, personWrapper, 90));
                            partyResults.put(365, stats.processPartyFromPerson(partyVotes, personWrapper, 365));
                            partyResults.put(-1, stats.processPartyFromPerson(partyVotes, personWrapper, -1));
                            JsonSerializer.serialize(configuration.getString("output.partyStats.path"), 1L, partyResults);

                            //Compute party - version 1
                            partyResults = new HashMap<>();
                            partyResults.put(30, stats.processPartyFromVotes(partyVotes2, personWrapper, new DateTime().minusDays(30), new DateTime()));
                            partyResults.put(90, stats.processPartyFromVotes(partyVotes2, personWrapper, new DateTime().minusDays(90), new DateTime()));
                            partyResults.put(365, stats.processPartyFromVotes(partyVotes2, personWrapper, new DateTime().minusDays(365), new DateTime()));
                            partyResults.put(-1, stats.processPartyFromVotes(partyVotes2, personWrapper, new DateTime().minusDays(9999), new DateTime()));
                            JsonSerializer.serialize(configuration.getString("output.partyStats.path"), 2L, partyResults);
                        } else {
                            logger.info("Person " + person.getPersonId() + " is not active, so it will not be added to party " + person.getCurrentParty() + " stats.");
                        }

                        state.setProperty("partialCrawls.lastProfile", personId);
                    }
                    Thread.sleep((int) (1 + System.nanoTime() % 1000));

                    return person;

                }
            });
            futureList.add(future);


        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.HOURS);

        Long exceptionsFound = 0L;
        for (Future<Person> future : futureList) {
            try {
                future.get();
            } catch (ExecutionException exex) {
                exceptionsFound++;
                exex.getCause().toString();
            }
        }

        if (exceptionsFound > 0L)
            logger.warning("Data may be corrupted. " + exceptionsFound + " found during execution.");
        else
            logger.info("No exceptions during execution.");
        for (Person p : persons.values()) {
            p.setVoteMap(null);
        }
        JsonSerializer.serialize(configuration.getString("output.profileStatsTogether.path"), 0L, persons);
        setStateProperty("finalizedCrawls.lastTimestamp", System.currentTimeMillis());
        setStateProperty("partialCrawls.lastProfile", 0L);
    }

}
