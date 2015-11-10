package com.bocse.multumesc;




import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    final static Logger logger = Logger.getLogger(App.class.toString());
    public static void main( String[] args ) throws IOException, InterruptedException {
        //System.out.println( "Hello World!" );
        //Document doc2= Jsoup.

        PresenceParser pp=new PresenceParser();
        //Document doc2=pp.getDocument(417L, 170L);
        //Long personId=2L;
        Long maxPerson=418L;
        for (Long personId=1L; personId<=maxPerson; personId++) {
            List<Map<String, Object>> map = pp.getPerson(personId);
            JSONObject object = new JSONObject();
            object.put("personId", personId);
            object.put("voteData", map);
            JsonSerializer jser = new JsonSerializer();
            jser.serialize("C:\\Temp\\Cdep\\", personId, object);
            Thread.sleep((int)(5000+System.nanoTime()%2000));
        }
//        System.exit(0);
//        Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (int i = 0; i < newsHeadlines.size(); i++) {
//            logger.info(newsHeadlines.get(i).text());
//        }
//        logger.info(newsHeadlines.last().text());
    }
}
