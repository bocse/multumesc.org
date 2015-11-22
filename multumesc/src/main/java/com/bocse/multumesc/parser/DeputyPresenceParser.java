package com.bocse.multumesc.parser;

import com.bocse.multumesc.data.*;
import com.bocse.multumesc.utils.NameUtils;
import com.bocse.multumesc.utils.TextUtils;
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
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bocse on 10.11.2015.
 */
public class DeputyPresenceParser {


    private final static Long maxAttempts=10L;
    private final static Long initialDelay=1300L;
    private final static Double backoffExponent=1.9;

    private final static Logger logger = Logger.getLogger(DeputyPresenceParser.class.toString());
    private final static String pattern = "dd.MM.yyyy HH:mm";
    private final static DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(pattern);
    //private HashMap<Long, SubjectMatter> subjectMatters;

    public Document getProfileDocument(final Long personId) throws IOException, InterruptedException {
        return getDocument(createProfileRequestDocument(personId));
    }

    private HttpUriRequest createCircumscriptionRequestDocument(final String county, final Long colegiu, final String prefix)
    {
        final String url ="http://www.becparlamentare2012.ro/"+prefix+colegiu.toString()+"-"+county+".html";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(9000).setConnectTimeout(9000).setSocketTimeout(9000).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine() + " county " + county + " colegiu " + colegiu  );
        return httpGet;

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
    public Document getCircumscriptionDocument(final String county, final Long colegiu, final String prefix) throws IOException, InterruptedException {
        return getDocument(createCircumscriptionRequestDocument(county, colegiu, prefix),false);
    }

    private Document getDocument(HttpUriRequest httpRequest) throws IOException, InterruptedException {
        return getDocument(httpRequest, true);
    }

    private Document getDocument(HttpUriRequest httpRequest, final Boolean throwOnEmptyDocument) throws IOException, InterruptedException {
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
                                if (throwOnEmptyDocument)
                                throw new ClientProtocolException("Unexpected response status: " + status);
                                else
                                    return null;
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

    public List<Location> parseCircumscriptionDocument(Document doc, String county, Long circumscription, Long colegiu) {
        List<Location> locations = new ArrayList<>();
        if (doc == null)
            return locations;
        Elements elements = doc.select("body > div > div > table > tbody > tr:eq(4) > td > p");
        for (int elementIndex=1; elementIndex<elements.size(); elementIndex++){
            Element element=elements.get(elementIndex);
            String text = TextUtils.flattenToAscii(element.text()).toLowerCase();

            String[] parts = text.split(":");
            if (parts.length > 1) {
                if (parts[0].contains("sat")) {

                    String[] sate = parts[1].split(",");
                    for (String sat : sate) {
                        Location location = new Location();
                        location.setCounty(county);
                        location.setCircumscription(circumscription);
                        location.setColegiu(colegiu);
                        location.setName(sat);
                        location.setLocationType(LocationType.SAT);
                    }
                } else if (parts[0].contains("localitate componenta") || parts[0].contains("localitati componente")) {

                    String[] comune = parts[1].split(",");
                    for (String comuna : comune) {
                        Location location = new Location();
                        location.setCounty(county);
                        location.setCircumscription(circumscription);
                        location.setColegiu(colegiu);
                        location.setName(comuna);
                        location.setLocationType(LocationType.COMUNA);
                        locations.add(location);
                    }
                } else {
                    Location location = new Location();
                    location.setCounty(county);
                    location.setCircumscription(circumscription);
                    location.setColegiu(colegiu);
                    location.setName(text);
                    location.setLocationType(LocationType.NA);
                    locations.add(location);
                    logger.warning("Unknown location type" + text);
                }
            } else {
                if (parts[0].contains("comuna ")) {
                    Location location = new Location();
                    location.setCounty(county);
                    location.setCircumscription(circumscription);
                    location.setColegiu(colegiu);
                    location.setName(parts[0].replace("comuna ", ""));
                    location.setLocationType(LocationType.COMUNA);
                    locations.add(location);

                }
                else
                if (parts[0].contains("municipiul ")) {
                    Location location = new Location();
                    location.setCounty(county);
                    location.setCircumscription(circumscription);
                    location.setColegiu(colegiu);
                    location.setName(parts[0].replace("municipiul ", ""));
                    location.setLocationType(LocationType.MUNICIPIU);
                    locations.add(location);
                }
                else {
                    if (parts[0]!=null && !parts[0].isEmpty()) {
                        Location location = new Location();
                        location.setCounty(county);
                        location.setCircumscription(circumscription);
                        location.setColegiu(colegiu);
                        location.setName(parts[0]);
                        if (county.equals("STRAINATATE"))
                            location.setLocationType(LocationType.TARA);
                        else
                        location.setLocationType(LocationType.STRADA);
                        locations.add(location);
                    }
                }
            }


        }
        return locations;
    }
    public Long parserProfileDocument(Person person, Document doc)
    {
        Long foundElements=0L;
        Long parsingErrors=0L;
        Counties counties=new Counties();
        Elements elements=doc.select(".headline");
        String name=elements.first().text();
        name=name.substring(0, name.indexOf("Sinteza")).trim();
        //logger.info(elements.first().text());
        //logger.getFullName();
        person.setFullName(name);
        person.setFirstName(NameUtils.getFirstNames(name));
        person.setLastName(NameUtils.getLastName(name));

        elements=doc.select("#itm974 > td > a ");
        if (elements.size()>=1)
            person.setPictureURL("http://www.cdep.ro"+elements.get(0).attr("href"));
        else
            person.setPictureURL("");

        elements=doc.select("table:eq(3) > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr");//> tr > td:eq(2) > p:eq(3) > table  > tr > td > p:eq(2) > table > tr:eq(2) > td:eq(2) > table");

        for (int elementIndex=0; elementIndex<elements.size(); elementIndex++)
        {
            String partyString=elements.get(elementIndex).text();
            String[] partyParts=partyString.split("[\\n*\\s*\\t*]+" + "-" + "[\\n*\\s*\\t*]+");
            String partyInitials=partyParts[0].trim().replaceAll("[\\n*\\t*\\n*\\r*\\n*]+", "").replaceAll("\u00A0", "").trim().toUpperCase();

            //TODO: replace this workaround with something smarter
            if (partyInitials.contains("INDEPENDENT"))
                partyInitials="INDEPENDENT";

            person.setCurrentParty(partyInitials);
            person.getAllPartyList().add(partyInitials);
        }

        elements=doc.select("html >body > table >tbody >tr > td:eq(1) > table:eq(1) > tbody > tr > td:eq(2) > table > tbody > tr > td > table > tbody > tr:eq(1)  > td:eq(1) > table > tbody ");

        person.setContactInformation(elements.get(elements.size()-1).text());

        elements=doc.select("html >body > table >tbody >tr > td:eq(1) > table:eq(1) > tbody > tr > td:eq(2) > table > tbody > tr > td > table > tbody > tr:eq(1)  > td:eq(1)");
        //ales deputat în circumscripţia electorală nr.22 HUNEDOARA, colegiul uninominal nr.3 data încetarii mandatului: 29 aprilie 2013 - înlocuit de: Petru-Sorin Marica
        String presentationText=elements.get(0).text().toLowerCase();
        String presentationTextFlattened= TextUtils.flattenToAscii(presentationText);
        person.setDescription(presentationText);


        if ( presentationTextFlattened.contains("ales la nivel national") || presentationTextFlattened.contains("aleasa la nivel national"))
        {
            person.setCounty("NATIONAL");
            person.setCircumscription(-1L);
            person.setColegiu(-1L);
        }
        else {
            {
                String markerString = "circumscripţia electorală nr.";
                int index1 = presentationText.indexOf(markerString, 0) + markerString.length();
                int index2 = presentationText.indexOf(",", index1);
                String countyString = presentationText.substring(index1, index2);
                person.setCounty(TextUtils.flattenToAscii(countyString.split(" ")[1]));;
            }

            {
                String markerString = "circumscripţia electorală nr.";
                int index1 = presentationText.indexOf(markerString, 0) + markerString.length();
                int index2 = presentationText.indexOf(" ", index1);
                String circumscriptionString = presentationText.substring(index1, index2);
                person.setCircumscription(Long.valueOf(circumscriptionString));
            }
            {
                String markerString = "colegiul uninominal nr.";
                int index1 = presentationText.indexOf(markerString, 0) + markerString.length();
                int index2 = presentationText.indexOf(" ", index1);
                if (index2 == -1)
                    index2 = presentationText.length();
                String colegiuString = presentationText.substring(index1, index2);
                person.setColegiu(Long.valueOf(colegiuString));
            }
        }
        if (presentationText.contains("deces") || presentationText.contains("data încetarii mandatului") || presentationText.contains("demis") || presentationText.contains("demisie") )
        {
            person.setActive(false);
        }
        else
        {
            person.setActive(true);
        }
        logger.info(""+elements.size());
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
        person.setLastUpdateTimestamp(System.currentTimeMillis());
    }

    public void getPersonProfile(Person person) throws IOException, InterruptedException {
        parserProfileDocument(person, getProfileDocument(person.getPersonId()));
        person.setLastUpdateTimestamp(System.currentTimeMillis());
    }

    public List<Location> getAllCircumscriptions() throws IOException, InterruptedException {
        Counties counties=new Counties();

        List<String> countiesFlattened=counties.getCountiesFlattened();
        countiesFlattened.add("STRAINATATE");
        return getAllCircumscriptions(countiesFlattened);

    }
    public List<Location> getAllCircumscriptions(List<String> countiesFlattened) throws IOException, InterruptedException {
        Counties counties=new Counties();
        List<Location> locations=new ArrayList<>();
        Set<String> countiesReceived=new HashSet<String>();
        Set<String> emptyCounty=new HashSet<String>();
        for (String county : countiesFlattened) {
            Long circumscription = counties.getCircumscription(county);
            boolean somethingSet=false;
            for (long colegiu = 1L; colegiu < 50; colegiu++) {
                List<Location> partialLocations = new ArrayList<>();
                partialLocations.addAll(parseCircumscriptionDocument(getCircumscriptionDocument(county.toUpperCase().replace("-","%20"), colegiu, "CD"), county, circumscription, colegiu));
                partialLocations.addAll(parseCircumscriptionDocument(getCircumscriptionDocument(county.toUpperCase().replace("-","%20"), colegiu, "C"), county, circumscription, colegiu));
                if (partialLocations.size() == 0) {

                    break;
                }
                else {
                    countiesReceived.add(county);
                    somethingSet=true;
                    locations.addAll(partialLocations);
                }
            }
            if (!somethingSet)
            {
                emptyCounty.add(county);
            }
        }
        logger.info("Received size " + countiesReceived.size());
        logger.info("Empty size "+emptyCounty.size());
        logger.info("Received " + Arrays.toString(countiesReceived.toArray()));
        logger.info("Empty " + Arrays.toString(emptyCounty.toArray()));
        return locations;
    }
}
