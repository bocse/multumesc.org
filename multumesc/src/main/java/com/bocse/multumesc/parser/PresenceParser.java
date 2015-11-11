package com.bocse.multumesc.parser;

import com.bocse.multumesc.App;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by bocse on 10.11.2015.
 */
public class PresenceParser {

    private final static Long maxAttempts=10L;
    private final static Long initialDelay=1300L;
    private final static Double backoffExponent=1.9;

    private final static Logger logger = Logger.getLogger(App.class.toString());
    private final static String pattern = "dd.mm.yyyy HH:mm";
    private final static DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(pattern);
    //private HashMap<Long, SubjectMatter> subjectMatters;

    public Document getProfileDocument(final Long personId) throws IOException, InterruptedException {
        return getDocument(createProfileRequestDocument(personId));
    }

    private HttpUriRequest createProfileRequestDocument(final Long personId)
    {

        final String url = "http://www.cdep.ro/pls/parlam/structura.mp?idm="+personId+"&cam=2&leg=2012&idl=1";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(9000).setConnectTimeout(9000).setSocketTimeout(9000).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine() + " person " + personId );
        return httpGet;

    }
    private HttpUriRequest createVoteRequestDocument(final Long personId, final Long eventId) throws UnsupportedEncodingException {
        final String url = "http://www.cdep.ro/pls/steno/eVot.mp";
        final HttpPost httppost = new HttpPost(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(9000).setConnectTimeout(9000).setSocketTimeout(9000).build();
        httppost.setConfig(requestConfig);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);

        nameValuePairs.add(new BasicNameValuePair("idm",
                personId.toString()));
        nameValuePairs.add(new BasicNameValuePair("prn",
                "1"));
        nameValuePairs.add(new BasicNameValuePair("pag",
                eventId.toString()));
        nameValuePairs.add(new BasicNameValuePair("cam",
                "2"));
        nameValuePairs.add(new BasicNameValuePair("idl",
                "1"));
        nameValuePairs.add(new BasicNameValuePair("sns",
                "D"));
        nameValuePairs.add(new BasicNameValuePair("tot",
                "1"));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        logger.info("Executing request " + httppost.getRequestLine() + " person " + personId + " page " + eventId);
        return httppost;
    }

    public Document getVoteDocument(final Long personId, final Long eventId) throws IOException, InterruptedException {
        return getDocument(createVoteRequestDocument(personId, eventId));
    }

    public Document getDocument(HttpUriRequest httpRequest) throws IOException, InterruptedException {
        Document doc=null;
        Long delay = initialDelay;
        Long attemptIndex = 0L;
        Boolean isSuccess = false;
        while (!isSuccess & attemptIndex < maxAttempts) {
            try {
                final String url = "http://www.cdep.ro/pls/steno/eVot.mp";


                CloseableHttpClient httpclient =
                        HttpClients.custom().
                                setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))

                                .build();




                try {
                    ////////////
//                    HttpUriRequest httppost = new HttpPost(url);
//
//                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
//
//                    nameValuePairs.add(new BasicNameValuePair("idm",
//                            personId.toString()));
//                    nameValuePairs.add(new BasicNameValuePair("prn",
//                            "1"));
//                    nameValuePairs.add(new BasicNameValuePair("pag",
//                            eventId.toString()));
//                    nameValuePairs.add(new BasicNameValuePair("cam",
//                            "2"));
//                    nameValuePairs.add(new BasicNameValuePair("idl",
//                            "1"));
//                    nameValuePairs.add(new BasicNameValuePair("sns",
//                            "D"));
//                    nameValuePairs.add(new BasicNameValuePair("tot",
//                            "1"));
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    ////////
                    // Create a custom response handler
                    ResponseHandler<Document> responseHandler = new ResponseHandler<Document>() {


                        public Document handleResponse(
                                final HttpResponse response) throws ClientProtocolException, IOException {
                            int status = response.getStatusLine().getStatusCode();
                            if (status >= 200 && status < 300) {
                                HttpEntity entity = response.getEntity();
                                Document doc = Jsoup.parse(entity.getContent(), "ISO-8859-2", "");
                                //return entity != null ? EntityUtils.toString(entity) : null;
                                return doc;
                            } else {
                                throw new ClientProtocolException("Unexpected response status: " + status);
                            }
                        }

                    };
                    doc = httpclient.execute(httpRequest, responseHandler);
                    //logger.info(doc.text());
                    //logger.info(doc.html());
                } finally {
                    httpclient.close();
                }
                isSuccess = true;

            } catch (IOException ex) {
                logger.warning("Error in performing request for person  " + ex.getMessage());
                if (attemptIndex < maxAttempts) {
                    logger.warning("Retry attempt " + attemptIndex + " / " + maxAttempts + ", waiting before retrying" + delay);
                } else {
                    logger.warning("Aborting process, after  " + maxAttempts + " attempts over " + delay + "ms");
                    throw ex;
                }
                Thread.sleep((int) (delay.longValue()));
                attemptIndex++;
                delay = (long) (delay * backoffExponent + System.nanoTime() % 1000);
            }
        }
        return doc;
    }


    public Long parserVoteDocument(Person person, Map<Long, String> subjectMatters, Document doc)
    {
        Long foundElements=0L;
        Long parsingErrors=0L;
        Elements elements=doc.select("#pageContent > table:last-child > tbody > tr");

        for (int elementIndex=1; elementIndex<elements.size(); elementIndex++ ) {

            Element element = elements.get(elementIndex);
            Elements parts = element.select("td");
//            for (int partIndex = 0; partIndex < parts.size(); partIndex++) {
//                String partText = parts.get(partIndex).text();
//                //logger.info(partText.trim());
//            }
            if (parts.size() == 5) {
                try {
                    Vote vote = new Vote();
                    vote.setPersonId(person.getPersonId());
                    DateTime dateTime = DateTime.parse(parts.get(1).text().trim(), dateTimeFormat);
                    vote.setTimestamp(dateTime.getMillis());
                    vote.setValue(parts.get(4).text().trim());
                    vote.setSubjectMatterId(Long.valueOf(parts.get(2).text().trim()));
                    if (!subjectMatters.containsKey(vote.getSubjectMatterId())) {
                        subjectMatters.put(vote.getSubjectMatterId(), parts.get(3).text().trim());
                    }
                    person.getVoteMap().put(vote.getSubjectMatterId(), vote);
                    foundElements++;
                }
                catch(Exception ex)
                {
                    logger.warning("Parsing error on "+element.text());
                    parsingErrors++;
                }
                finally{

                }
            }
            else {
                logger.warning("Failed to parse " + element.text());
                parsingErrors++;
            }
        }
        if (parsingErrors>0)
        logger.info("Parsing errors: "+parsingErrors);
        return foundElements;
    }


    public Long parserProfileDocument(Person person, Document doc)
    {
        Long foundElements=0L;
        Long parsingErrors=0L;
        Elements elements=doc.select(".headline");
        String name=elements.first().text();
        name=name.substring(0, name.indexOf("Sinteza")).trim();
        logger.info(elements.first().text());
        logger.getName();
        person.setName(name);
        elements=doc.select("body > table > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr > td:nth-child(3) > p:nth-child(3) > table > tbody > tr > td > p:nth-child(2) > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr > td:nth-child(2) > table > tbody > tr > td:nth-child(1)");
        return foundElements;
    }


    public void getPersonVotes(Person person,  Map<Long, String> subjectMatters) throws IOException, InterruptedException {

        boolean keepGoing=true;
        Long index=1L;
        do {
            Long foundElements= parserVoteDocument(person, subjectMatters, getVoteDocument(person.getPersonId(), index));
            keepGoing=foundElements>0;
            logger.info("Finished parsing page " + index);
            index++;
         } while (keepGoing);
    }

    public void getPersonProfile(Person person) throws IOException, InterruptedException {
        parserProfileDocument(person, getProfileDocument(person.getPersonId()));
    }

}
