package com.bocse.multumesc;


import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.parser.PresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class App {
    final static Logger logger = Logger.getLogger(App.class.toString());
    public final static FileConfiguration configuration = new PropertiesConfiguration();

    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {

        configuration.load(args[0]);


        JsonSerializer jser = new JsonSerializer();

        PresenceParser pp = new PresenceParser();
        Long firstPerson = configuration.getLong("assumptions.firstPerson", 1);
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

                jser.serialize(configuration.getString("output.profile.path"), personId, person);
                jser.serialize(configuration.getString("output.subject.path"), 0L, subjectMatters);
                Thread.sleep((int) (1 + System.nanoTime() % 1000));
            } finally {

            }

        }


    }
}
