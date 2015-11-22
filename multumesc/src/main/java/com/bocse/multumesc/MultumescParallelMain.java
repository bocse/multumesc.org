package com.bocse.multumesc;


import com.bocse.multumesc.crawlers.DeputyCrawler;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;

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
