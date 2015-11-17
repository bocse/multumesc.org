package com.bocse.multumesc;


import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import com.bocse.multumesc.parser.PresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import com.bocse.multumesc.statistics.StatsProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class AppTestRun {
    final static Logger logger = Logger.getLogger(AppTestRun.class.toString());
    public final static FileConfiguration configuration = new PropertiesConfiguration();
    public final static FileConfiguration state = new PropertiesConfiguration();
    public final static Map<String, Map<VoteTypes, AtomicLong>> partyVotes = new ConcurrentHashMap<>();
    public final static Map<String, Map<VoteTypes, AtomicLong>> partyVotes2 = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        if (new File(args[1]).exists())
            state.load(args[1]);
        state.setFileName(args[1]);


        configuration.load(args[0]);
        logger.info(configuration.toString());


        JsonSerializer jser = new JsonSerializer();

        PresenceParser pp = new PresenceParser();
        Long firstPerson = configuration.getLong("assumptions.firstPerson", 1);
        if (configuration.getBoolean("working.mode.resumeLastCrawl"))
            firstPerson = Math.max(firstPerson, 1 + state.getLong("partialCrawls.lastProfile", 1L));
        Long maxPerson = configuration.getLong("assumptions.lastPerson", 418);
        Long personId = 7L;
        Person person = new Person();
        person.setPersonId(personId);

        try {
            pp.getPersonProfile(person);
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonString = gson.toJson(person);

            byte[] latin2JsonString = jsonString.getBytes("UTF-8");
            //byte[] utf8JsonString = new String(latin2JsonString, "ISO-8859-2").getBytes("UTF-8");
            System.out.println(jsonString);
        } finally {

        }
    }
}