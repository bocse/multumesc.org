package com.bocse.multumesc;




import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    final static Logger logger = Logger.getLogger(App.class.toString());
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        //Document doc2= Jsoup.
        Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (int i = 0; i < newsHeadlines.size(); i++) {
            logger.info(newsHeadlines.get(i).text());
        }
        logger.info(newsHeadlines.last().text());
    }
}
