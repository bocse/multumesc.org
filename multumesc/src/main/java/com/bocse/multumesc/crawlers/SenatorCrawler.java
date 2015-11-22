package com.bocse.multumesc.crawlers;

import com.bocse.multumesc.MultumescMain;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.parser.SenatorPresenceParser;
import com.bocse.multumesc.requester.HttpRequester;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by bocse on 22.11.2015.
 */
public class SenatorCrawler {
    private final static Logger logger = Logger.getLogger(SenatorCrawler.class.toString());
    public SenatorCrawler()
    {

    }

    private HttpUriRequest createSenatorListRequest()
    {
        final String url ="http://senat.ro/FisaSenatori.aspx";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(9000).setConnectTimeout(9000).setSocketTimeout(9000).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request for senator list" + httpGet.getRequestLine() );
        return httpGet;
    }

    public void getAllSenatorsList() throws IOException, InterruptedException {
        Document doc= HttpRequester.getDocument(createSenatorListRequest());
        Elements elements=doc.select("#ctl00_B_Center_faraPozeGrd2 > tbody > tr");
        for (int elementIndex=1; elementIndex<elements.size(); elementIndex++)
        {
            Element element=elements.get(elementIndex);
            String index=element.child(0).text().trim();
            String fullName=element.child(1).text().trim();
            String description=element.child(3).text().trim();
            String colegiu=element.child(4).text().trim();
            String party=element.child(5).text().trim().toUpperCase();
            Person person=new Person();
            person.setFullName(fullName);
            person.setPersonId(Long.valueOf(index));
            person.setDescription(description);
            person.setColegiu(Long.valueOf(colegiu));
            person.setCurrentParty(party);
            person.setAllPartyList(new ArrayList<String>());
            person.getAllPartyList().add(party);
            person.setLastUpdateTimestamp(new DateTime().getMillis());
            String[] names=fullName.split(" ");
            StringBuffer firstNameBuffer=new StringBuffer();
            for (int nameIndex=0; nameIndex<names.length-1; nameIndex++)
                firstNameBuffer.append(names[nameIndex]);
            person.setFirstName(firstNameBuffer.toString());
            person.setLastName(names[names.length - 1]);
            person.setActive(true);

            person.setContactInformation("");
            person.setPictureURL("");
            person.setCounty("");


        }
    }

    public void crawlAllSenators()
    {

    }

    public void crawlSenator(String senatorId) throws IOException, InterruptedException {
        DateTime dateCursor=new DateTime().minusDays(4).withTime(0,0,0,1);
        DateTime earliest=new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(19).withTime(0,0,0,1);


        int lastYear=dateCursor.getYear();
        int lastMonth=dateCursor.getMonthOfYear();

        SenatorPresenceParser spp=new SenatorPresenceParser();
        spp.init();
        //"9d0635d5-1743-4696-8acd-e97e070da503"
        spp.initProfilePage(senatorId);

        while (dateCursor.isAfter(earliest))
        {

            if (dateCursor.getYear()!=lastYear)
            {
                spp.setYearPage(dateCursor.getYear());
                lastYear=dateCursor.getYear();
            }
            if (dateCursor.getMonthOfYear()!=lastMonth){
                spp.setMonthPage(dateCursor.getMonthOfYear());
                lastMonth=dateCursor.getMonthOfYear();
            }

            spp.getVoteList(dateCursor);
            dateCursor=dateCursor.minusDays(1);
        }

    }
}
