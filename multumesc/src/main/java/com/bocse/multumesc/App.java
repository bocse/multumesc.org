package com.bocse.multumesc;


import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.VoteTypes;
import com.bocse.multumesc.parser.PresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import com.bocse.multumesc.statistics.StatsProcessor;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class App {
    final static Logger logger = Logger.getLogger(App.class.toString());
    public final static FileConfiguration configuration = new PropertiesConfiguration();
    public final static FileConfiguration state=new PropertiesConfiguration();
    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        state.load(args[1]);
        state.setFileName(args[1]);
        state.setAutoSave(true);
        state.setProperty("finalizedCrawls.startTimestamp", System.currentTimeMillis());

        configuration.load(args[0]);
        logger.info(configuration.toString());


        JsonSerializer jser = new JsonSerializer();

        PresenceParser pp = new PresenceParser();
        Long firstPerson = configuration.getLong("assumptions.firstPerson", 1);
        if (configuration.getBoolean("working.mode.resumeLastCrawl"))
            firstPerson=Math.max(firstPerson, 1+state.getLong("partialCrawls.lastProfile", 1L));
        Long maxPerson = configuration.getLong("assumptions.lastPerson", 418);

        SortedMap<Long, String> subjectMatters = new TreeMap<>();
        for (Long personId = firstPerson; personId <= maxPerson; personId++) {

            try {
                Person person = new Person();
                person.setPersonId(personId);
                pp.getPersonVotes(person, subjectMatters);
                pp.getPersonProfile(person);
                //List<Map<String, Object>> map = pp.getPerson(person, );
                //object.put("voteData", map);
                StatsProcessor stats=new StatsProcessor(person);
                Map<VoteTypes, AtomicLong> globalStats =stats.process();
                Map<VoteTypes, AtomicLong> last30Stats =stats.process(new DateTime().minusDays(30), new DateTime());
                Map<VoteTypes, AtomicLong> last90Stats =stats.process(new DateTime().minusDays(90), new DateTime());
                Map<VoteTypes, AtomicLong> last365Stats =stats.process(new DateTime().minusDays(365), new DateTime());

                person.setStatsAllTerm(globalStats);
                person.setStatsLast30Days(last30Stats);
                person.setStatsLast90Days(last90Stats);
                person.setStatsLast365Days(last365Stats);
                jser.serialize(configuration.getString("output.profile.path"), personId, person);
                jser.serialize(configuration.getString("output.subject.path"), 0L, subjectMatters);
                state.setProperty("partialCrawls.lastProfile", personId);
                Thread.sleep((int) (1 + System.nanoTime() % 1000));
            } finally {

            }

        }

        state.setProperty("finalizedCrawls.lastTimestamp", System.currentTimeMillis());
        state.setProperty("partialCrawls.lastProfile", 0L);
    }
}
