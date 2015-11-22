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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class MultumescParallelMain {

    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        DeputyCrawler deputyCrawler=new DeputyCrawler(args[0],args[1]);
        deputyCrawler.init();
        deputyCrawler.crawl();
    }
}
