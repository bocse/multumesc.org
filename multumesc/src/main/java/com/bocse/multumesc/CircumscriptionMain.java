package com.bocse.multumesc;


import com.bocse.multumesc.crawlers.CircumscriptionCrawler;
import com.bocse.multumesc.crawlers.DeputyCrawler;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;

/**
 * Multumesc Main app.
 */
public class CircumscriptionMain {

    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        CircumscriptionCrawler circumscriptionCrawler=new CircumscriptionCrawler(args[0]);
        circumscriptionCrawler.init();
        circumscriptionCrawler.crawl();

    }
}
