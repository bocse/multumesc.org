package com.bocse.multumesc.crawlers;


import com.bocse.multumesc.MultumescDeputyParallelMain;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import com.bocse.multumesc.parser.DeputyPresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import com.bocse.multumesc.statistics.StatsProcessor;
import com.bocse.multumesc.uploader.FTPUploader;
import com.bocse.multumesc.uploader.S3Uploader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.StopWatch;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
    private final static Logger logger = Logger.getLogger(MultumescDeputyParallelMain.class.toString());
    public final FileConfiguration configuration = new PropertiesConfiguration();
    public final FileConfiguration state = new PropertiesConfiguration();
    //public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes = new ConcurrentHashMap<>();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes30 = new ConcurrentHashMap<>();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes90 = new ConcurrentHashMap<>();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotes365 = new ConcurrentHashMap<>();
    public final Map<String, Map<VoteTypes, AtomicLong>> partyVotesAll = new ConcurrentHashMap<>();
    public final Map<String, Person> persons = new ConcurrentHashMap<>();
    public final SortedMap<Long, String> subjectMatters = new ConcurrentSkipListMap<>();
    private  Map<Long, Long> personStartTimestampMap;
    private final String configFile;
    private final String stateFile;
    private final Object syncObject = new Object();
    public Long maxPerson;
    public Long firstPerson;
    public Long legislatureYear=2012L;
    public Integer threadNumber;
    public final static String fileSuffix=".json";
    public FTPUploader ftp;
    public S3Uploader s3;

    public DateTime lastUpdated;
    public DeputyCrawler(String configFile, String stateFile) {
        this.configFile = configFile;
        this.stateFile = stateFile;
    }

    public synchronized void setStateProperty(String key, Object value) {
        state.setProperty(key, value);
    }

    public void init() throws ConfigurationException, IOException {
        if (new File(stateFile).exists())
            state.load(stateFile);
        state.setFileName(stateFile);
        state.setAutoSave(true);
        state.setProperty("finalizedCrawls.startTimestamp", System.currentTimeMillis());

        configuration.load(configFile);
        logger.info(configuration.toString());
        maxPerson = configuration.getLong("assumptions.lastPerson", 417);
        threadNumber = configuration.getInt("working.mode.threadsNumber", 10);
        legislatureYear=configuration.getLong("assumptions.legislatureYear",2012);
        firstPerson = configuration.getLong("assumptions.firstPerson", 1);
        if (threadNumber == 1) {
            if (configuration.getBoolean("working.mode.resumeLastCrawl"))
                firstPerson = Math.max(firstPerson, 1 + state.getLong("partialCrawls.lastProfile", 1L));
        } else if (configuration.getBoolean("working.mode.resumeLastCrawl")) {
            logger.warning("Resume automatic crawl turned off.");
        }
        if (configuration.getBoolean("upload.ftp.enabled", false)) {
            ftp = new FTPUploader(
                    configuration.getString("upload.ftp.hostname"),
                    configuration.getInt("upload.ftp.port"),
                    configuration.getString("upload.ftp.username"),
                    configuration.getString("upload.ftp.password"));
            ftp.init();
        }
        if (configuration.getBoolean("upload.s3.enabled",false))
        {
            s3=new S3Uploader(configFile, configuration.getString("upload.s3.bucket"));
            //s3.upload("/data/testFile", new File("/home/bocse/testFile.txt" ));
        }
        lastUpdated=new DateTime();
    }

    public void crawl() throws IOException, InterruptedException, ConfigurationException {
        StopWatch crawlStopWatch=new StopWatch();
        DeputyPresenceParser startDateParser=new DeputyPresenceParser();
        startDateParser.setLegislatureYear(legislatureYear);
        personStartTimestampMap= startDateParser.getAllStartDates();
        crawlStopWatch.start();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        List<Future<Person>> futureList = Collections.synchronizedList(new ArrayList<Future<Person>>());
        for (Long personIdIndex = firstPerson; personIdIndex <= maxPerson; personIdIndex++) {
            final Long personId = new Long(personIdIndex.longValue());
            Future future = executorService.submit(new Callable() {
                public Object call() throws IOException, InterruptedException, ConfigurationException {
                    final DeputyPresenceParser deputyPresenceParser = new DeputyPresenceParser();
                    deputyPresenceParser.setLegislatureYear(legislatureYear);
                    Person person = new Person();
                    person.setPersonId(personId);
                    deputyPresenceParser.getPersonProfile(person);
                    Long investitureTimestamp=personStartTimestampMap.get(personId);
                    if (investitureTimestamp==null)
                    {
                        if (person.getActive()) {
                            logger.severe("SEVERE: failed to determine investiture date for active deputy " + personId);
                        }
                        else
                        {
                            logger.warning("Warning: failed to determine investiture date for non-active deputy " + personId);
                            investitureTimestamp=new DateTime(legislatureYear.intValue(),12,18,0,0).getMillis();
                        }
                    }
                    person.setInvestitureTimestamp(investitureTimestamp);

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

                        person.setAttendancePerWeek(stats.processWeeklyAttendence());
                        person.setAttendancePerWeekExcludingVacation(stats.processWeeklyAttendenceExclusingVacation());
                        SortedMap<Long, Vote> tempVote = person.getVoteMap();
                        person.setVoteMap(null);
                        File profileStatsFile=JsonSerializer.serialize(configuration.getString("output.profileStats.path")+ personId+fileSuffix, person);
                        if (configuration.getBoolean("upload.ftp.enabled", false))
                        {
                            ftp.uploadFileAsync(configuration.getString("upload.ftp.remotePath")+profileStatsFile.getName(), profileStatsFile);
                        }

                        person.setVoteMap(tempVote);
                        File profileFile=JsonSerializer.serialize(configuration.getString("output.profile.path")+ personId+fileSuffix, person);
//                        if (configuration.getBoolean("upload.ftp.enabled", false))
//                        {
//                            ftp.uploadFileAsync(configuration.getString("upload.ftp.remotePath")+profileFile, profileFile);
//                        }

                        //
                        if (personId % 10 == 1 || personId == maxPerson)
                            JsonSerializer.serialize(configuration.getString("output.subject.path")+fileSuffix, subjectMatters);
                        //
                        persons.put(person.getFullName(), person);
                        if (person.getActive()) {
                            //Compute party - version 1
                            List<Person> personWrapper = new ArrayList<>();
                            personWrapper.add(person);

                            /*
                            Map<Integer, Map<String, Map<VoteTypes, AtomicLong>>> partyResults = new HashMap<>();
                            partyResults.put(30, stats.processPartyFromPerson(partyVotes, personWrapper, 30));
                            partyResults.put(90, stats.processPartyFromPerson(partyVotes, personWrapper, 90));
                            partyResults.put(365, stats.processPartyFromPerson(partyVotes, personWrapper, 365));
                            partyResults.put(-1, stats.processPartyFromPerson(partyVotes, personWrapper, -1));
                            JsonSerializer.serialize(configuration.getString("output.partyStats.path")+"_1"+fileSuffix, partyResults);
                            */
                            //Compute party - version 2
                            Map<Integer, Map<String, Map<VoteTypes, AtomicLong>>> partyResults = new HashMap<>();
                            partyResults = new HashMap<>();
                            partyResults.put(30, stats.processPartyFromVotes(partyVotes30, personWrapper, new DateTime().minusDays(30), new DateTime()));
                            partyResults.put(90, stats.processPartyFromVotes(partyVotes90, personWrapper, new DateTime().minusDays(90), new DateTime()));
                            partyResults.put(365, stats.processPartyFromVotes(partyVotes365, personWrapper, new DateTime().minusDays(365), new DateTime()));
                            partyResults.put(-1, stats.processPartyFromVotes(partyVotesAll, personWrapper, new DateTime().minusDays(9999), new DateTime()));
                            JsonSerializer.serialize(configuration.getString("output.partyStats.path")+fileSuffix, partyResults);
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
                logger.warning(exex.getCause().toString());
                exex.printStackTrace();

            }
        }
        Boolean succesful=false;
        if (exceptionsFound > 0L) {
            logger.warning("Data may be corrupted. " + exceptionsFound + " found during execution.");
            succesful=false;
        }
        else {
            logger.info("No exceptions during execution.");
            succesful=true;
        }
        for (Person p : persons.values()) {
            p.setVoteMap(null);
        }
        crawlStopWatch.stop();
        Map<String, Object> profileStatsTogetherWrapper=new HashMap<>();
        profileStatsTogetherWrapper.put("lastUpdateTimestamp", lastUpdated.getMillis());
        profileStatsTogetherWrapper.put("lastUpdateDateTime", lastUpdated.toString());
        profileStatsTogetherWrapper.put("crawlTime", crawlStopWatch.getTime());
        profileStatsTogetherWrapper.put("successful", succesful);
        profileStatsTogetherWrapper.put("errorCount", exceptionsFound);
        profileStatsTogetherWrapper.put("lastEventTimestamp", subjectMatters.lastKey());
        profileStatsTogetherWrapper.put("firstEventTimestamp", subjectMatters.firstKey());
        profileStatsTogetherWrapper.put("lastEventTimestamp", new DateTime(subjectMatters.lastKey()).toString());
        profileStatsTogetherWrapper.put("firstEventTimestamp", new DateTime(subjectMatters.firstKey()).toString());

        profileStatsTogetherWrapper.put("payload", persons);
        File profileStatsTogetherFile=JsonSerializer.serialize(configuration.getString("output.profileStatsTogether.path")+fileSuffix, profileStatsTogetherWrapper);
        File partyStatsFile=new File(configuration.getString("output.partyStats.path")+fileSuffix);
        File subjectFile=new File(configuration.getString("output.subject.path")+fileSuffix);
        if (configuration.getBoolean("upload.ftp.enabled", false))
        {
            ftp.uploadFileAsync(configuration.getString("upload.ftp.remotePath")+profileStatsTogetherFile.getName(), profileStatsTogetherFile);
            ftp.uploadFileAsync(configuration.getString("upload.ftp.remotePath")+partyStatsFile.getName(), partyStatsFile);
            ftp.uploadFileAsync(configuration.getString("upload.ftp.remotePath")+subjectFile.getName(), subjectFile);
        }
        if (configuration.getBoolean("upload.s3.enabled", false)) {
            s3.upload( configuration.getString("upload.s3.remotePath")+profileStatsTogetherFile.getName(),profileStatsTogetherFile);
            s3.upload(configuration.getString("upload.s3.remotePath") + partyStatsFile.getName(), partyStatsFile);
            s3.upload(configuration.getString("upload.s3.remotePath") + subjectFile.getName(), subjectFile);

            DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYY-MM-dd");
            String strDateOnly = fmt.print(new DateTime());
            s3.upload( configuration.getString("upload.s3.remotePath")+"history/"+strDateOnly+"/"+ profileStatsTogetherFile.getName(),profileStatsTogetherFile);
            s3.upload(configuration.getString("upload.s3.remotePath") +"history/"+strDateOnly+"/"+ partyStatsFile.getName(), partyStatsFile);
            s3.upload(configuration.getString("upload.s3.remotePath") +"history/"+strDateOnly+"/"+ subjectFile.getName(), subjectFile);
            ;
        }

        if (configuration.getBoolean("upload.ftp.enabled", false))
        {
            ftp.disconnect();
        }
            setStateProperty("finalizedCrawls.lastTimestamp", System.currentTimeMillis());
        setStateProperty("partialCrawls.lastProfile", 0L);
    }

}
