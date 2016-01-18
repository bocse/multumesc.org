package com.bocse.multumesc.crawlers;


import com.bocse.multumesc.MultumescDeputyParallelMain;
import com.bocse.multumesc.data.*;
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
public class CircumscriptionCrawler {
    private final static Logger logger = Logger.getLogger(MultumescDeputyParallelMain.class.toString());
    public final FileConfiguration configuration = new PropertiesConfiguration();

    private final String configFile;

    public CircumscriptionCrawler(String configFile) {
        this.configFile = configFile;

    }


    public void init() throws ConfigurationException, IOException {

        configuration.load(configFile);
        logger.info(configuration.toString());

    }

    public void crawl() throws IOException, InterruptedException, ConfigurationException {
        DeputyPresenceParser pp=new DeputyPresenceParser();
        //List<String> counties=new ArrayList<>();
        //counties.add("CLUJ");
        Counties counties=new Counties();
        List<String> countiesFlattened=counties.getCountiesFlattened();
        countiesFlattened.add("STRAINATATE");
        Map<String, Map<Long, Map<String, Set<String>>>> fullMap=pp.getCircumscriptionsHierarchy(countiesFlattened);
        JsonSerializer jsonSerializer=new JsonSerializer();
        JsonSerializer.serialize("C:\\Temp\\countyList" + ".txt", fullMap);
    }
}
