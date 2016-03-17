package com.bocse.multumesc.parser;

import com.bocse.multumesc.data.*;
import com.bocse.multumesc.requester.HttpRequester;
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
import org.apache.http.client.utils.URLEncodedUtils;
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
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bocse on 10.11.2015.
 */
public class DeputyPresenceParser {




    private final static Logger logger = Logger.getLogger(DeputyPresenceParser.class.toString());
    private final static String pattern = "dd.MM.yyyy HH:mm";
    private final static DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(pattern);
    private final int connectionRequestTimeout=30000;
    private final int connectionTimeout=30000;
    private final int socketTimeout=99000;
    private Long legislatureYear;

    //private HashMap<Long, SubjectMatter> subjectMatters;

    public Document getProfileDocument(final Long personId) throws IOException, InterruptedException {
        return HttpRequester.getDocument(createProfileRequestDocument(personId));
    }

    private HttpUriRequest createCircumscriptionRequestDocument(final String county, final Long colegiu, final String prefix)
    {
        final String url ="http://www.becparlamentare2012.ro/"+prefix+colegiu.toString()+"-"+county+".html";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine() + " county " + county + " colegiu " + colegiu  );
        return httpGet;

    }

    private HttpUriRequest createProfileRequestDocument(final Long personId)
    {

        final String url = "http://www.cdep.ro/pls/parlam/structura.mp?idm="+personId+"&cam=2&leg="+legislatureYear+"&idl=1";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine() + " person " + personId );
        return httpGet;

    }
    //http://www.cdep.ro/pls/steno/evot2015.mp?idm=8&tot=1&pag=3

    private HttpUriRequest createStartDateRequestDocument()
    {
        final String url = "http://www.cdep.ro/pls/parlam/structura2015.de?idl=1";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine() );
        return httpGet;

    }
    private HttpUriRequest createVoteNewRequestDocument(final Long personId, final Long eventId) throws UnsupportedEncodingException {
        final String url = "http://www.cdep.ro/pls/steno/evot2015.mp?";
        List<NameValuePair> nameValuePairs = new ArrayList<>(10);

        nameValuePairs.add(new BasicNameValuePair("idm",
                personId.toString()));
//        nameValuePairs.add(new BasicNameValuePair("prn",
//                "1"));
        nameValuePairs.add(new BasicNameValuePair("pag",
                eventId.toString()));
//        nameValuePairs.add(new BasicNameValuePair("cam",
//                "2"));
//        nameValuePairs.add(new BasicNameValuePair("idl",
//                "1"));
//        nameValuePairs.add(new BasicNameValuePair("sns",
//                "D"));
        nameValuePairs.add(new BasicNameValuePair("tot",
                "1"));
        nameValuePairs.add(new BasicNameValuePair("leg", legislatureYear.toString()));
        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

        String newUrl = url+ paramString;
        final HttpGet httppost = new HttpGet(newUrl);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httppost.setConfig(requestConfig);

        logger.info("Executing request " + httppost.getRequestLine() + " person " + personId + " page " + eventId);
        return httppost;
    }

    @Deprecated
    private HttpUriRequest createVoteOldRequestDocument(final Long personId, final Long eventId) throws UnsupportedEncodingException {
        final String url = "http://www.cdep.ro/pls/steno/eVot.mp";
        final HttpPost httppost = new HttpPost(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httppost.setConfig(requestConfig);
        List<NameValuePair> nameValuePairs = new ArrayList<>(10);

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
        return HttpRequester.getDocument(createVoteNewRequestDocument(personId, eventId));
    }
    public Document getCircumscriptionDocument(final String county, final Long colegiu, final String prefix) throws IOException, InterruptedException {
        return HttpRequester.getDocument(createCircumscriptionRequestDocument(county, colegiu, prefix), false);
    }


    public Long parserVoteDocument(Person person, Map<Long, String> subjectMatters, Document doc)
    {
        Long foundElements=0L;
        Long parsingErrors=0L;
        //Elements elements=doc.select("#pageContent > table:last-child > tbody > tr");
        Long votesBeforeInvestiture=0L;
        //
        Elements elements=doc.select("#olddiv > table > tbody:eq(0) > tr");
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
                    if (person.getInvestitureTimestamp() == null)
                    {
                        throw new IllegalStateException("Unable to determine investiture timestamp for "+person.getPersonId());
                    }
                    if (person.getInvestitureTimestamp()<= vote.getTimestamp()) {
                        person.getVoteMap().put(vote.getSubjectMatterId(), vote);
                        foundElements++;
                    }
                    else
                    {
                        votesBeforeInvestiture++;
                    }
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
        if (votesBeforeInvestiture>0)
        {
            logger.warning("Ignored "+votesBeforeInvestiture+" votes before investiture.");
        }
        return foundElements;
    }

    public Map<String, SortedSet<String>> parseCircumscriptionDocumentToHierarchy(Document doc,  String county) {

        SortedMap<String, SortedSet<String>> localityMap=new TreeMap<>();
        if (doc == null)
            return localityMap;
        String locality="";
        if (county.toLowerCase().equals("bucuresti"))
            locality="sector 1 - bucuresti";
        Elements elements = doc.select("body > div > div > table > tbody > tr:eq(4) > td > p");
        for (int elementIndex=1; elementIndex<elements.size(); elementIndex++){
            Element element=elements.get(elementIndex);
            String text = TextUtils.flattenToAscii(element.text()).toLowerCase();

            String[] parts = text.split(":");
            if (parts.length > 1 && !text.contains("blocuri") && !text.contains("str.")) {
                if (parts[0].contains("sat")) {

                    String[] sate = parts[1].split(",");
                    for (String sat : sate) {
                        //locality=sat.trim();
                        localityMap.putIfAbsent(sat.trim(), new TreeSet<>());
                    }
                } else if (parts[0].contains("localitate componenta") || parts[0].contains("localitati componente")) {

                    String[] comune = parts[1].split(",");
                    for (String comuna : comune) {
                        //locality=
                        localityMap.putIfAbsent(comuna.trim(), new TreeSet<>());
                    }
                } else {
                    logger.warning("Unknown location type, assuming street level" + text);
                    localityMap.putIfAbsent(locality, new TreeSet<>());
                    localityMap.get(locality).add(parts[0].trim());
                }
            } else {
                if (parts[0].contains("comuna ")) {
                    //locality=
                    localityMap.putIfAbsent(parts[0].replace("comuna ", "").trim(), new TreeSet<>());
                }
                else
                if (parts[0].contains("municipiul ") || parts[0].contains("orasul") || parts[0].startsWith("sector")) {
                    locality=parts[0].replace("municipiul ", "").trim();
                    if (!locality.contains("sector"))
                        locality = locality.replaceAll("\\d","");
                    locality=locality.replaceAll("[\\*\\)\\(]","");
                    localityMap.putIfAbsent(locality, new TreeSet<>());
                }
                else {
                    if (parts[0]!=null && !parts[0].isEmpty()) {

                        if (county.equals("STRAINATATE")) {


                            localityMap.putIfAbsent(parts[0].trim(), new TreeSet<>());
                        }
                        else {
                            localityMap.putIfAbsent(locality.trim(), new TreeSet<>());
                            localityMap.get(locality).add(parts[0].trim());
                        }
                        //locations.add(location);

                    }
                }
            }


        }
        return localityMap;
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
        String name=elements.first().text().split(",")[0];
        if (name.contains("Sinteza"))
        name=name.substring(0, name.indexOf("Sinteza")).trim();
        else
        name=name.trim();
        //logger.info(elements.first().text());
        //logger.getFullName();
        person.setFullName(name);
        person.setFirstName(NameUtils.getFirstNames(name));
        person.setLastName(NameUtils.getLastName(name));
        elements=doc.select("#itm974 > td");
        if (elements.size()>0)
        {
            String birthdayElement=elements.text().trim().toLowerCase();
            int birthdayIndex=birthdayElement.indexOf("n. ");
            if (birthdayIndex>-1)
            {
                String birthdayText=birthdayElement.substring(birthdayIndex+3);
                Map<Long, String> monthNameMap = new HashMap<>();
                monthNameMap.put(1L, "ian. ");
                monthNameMap.put(2L, "feb. ");
                monthNameMap.put(3L, "mar. ");
                monthNameMap.put(4L, "apr. ");
                monthNameMap.put(5L, "mai ");
                monthNameMap.put(6L, "iun. ");
                monthNameMap.put(7L, "iul. ");
                monthNameMap.put(8L, "aug. ");
                monthNameMap.put(9L, "sep. ");
                monthNameMap.put(10L, "oct. ");
                monthNameMap.put(11L, "noi. ");
                monthNameMap.put(12L, "dec. ");
                java.time.format.DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                        .appendPattern("d ")
                        .appendText(ChronoField.MONTH_OF_YEAR, monthNameMap)
                        .appendPattern("yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .toFormatter();

                java.time.LocalDateTime dt = LocalDateTime.parse(birthdayText, fmt);
                Long birthdayTimestamp=dt.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
                DateTime dt2 = new DateTime(birthdayTimestamp);
                DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");
                String dtStr = fmt2.print(dt2);
                person.setBirthdayTimestamp(birthdayTimestamp/1000);
                person.setBirthday(dtStr);
                logger.info("Birthday: "+birthdayTimestamp);
            }
        }
        elements=doc.select("#itm974 > td > a ");
        if (elements.size()>=1)
            person.setPictureURL("http://www.cdep.ro"+elements.get(0).attr("href"));
        else
            person.setPictureURL("");
        elements=doc.select("a");
        for (int emailIndex=0; emailIndex<elements.size(); emailIndex++)
        {
            String text=elements.get(emailIndex).text();
            if (text.contains("@") && !text.contains("webmaster"))
                person.setEmail(text.trim());
        }

        int foundPartyElementIndex=-1;
        for (int partyElementIndex=5; partyElementIndex>=3; partyElementIndex-=2) {
            elements = doc.select("table:eq("+partyElementIndex+") ");
            if (elements.text().contains("Formaţiunea politică") || elements.text().contains("Organizaţia minorităţilor naţionale"))
            {
                foundPartyElementIndex=partyElementIndex;
                break;
            }
        }


        if (foundPartyElementIndex>-1) {
            elements=doc.select("table:eq("+foundPartyElementIndex+") > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr");//> tr > td:eq(2) > p:eq(3) > table  > tr > td > p:eq(2) > table > tr:eq(2) > td:eq(2) > table");
            for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
                String partyString = elements.get(elementIndex).text();
                String[] partyParts = partyString.split("[\\n*\\s*\\t*]+" + "-" + "[\\n*\\s*\\t*]+");
                String partyInitials = partyParts[0].trim().replaceAll("[\\n*\\t*\\n*\\r*\\n*]+", "").replaceAll("\u00A0", "").trim().toUpperCase();

                //TODO: replace this workaround with something smarter
                if (partyInitials.contains("INDEPENDENT"))
                    partyInitials = "INDEPENDENT";

                person.setCurrentParty(partyInitials);
                person.getAllPartyList().add(partyInitials);
            }
        }
        else
        {
            person.setCurrentParty("NECUNOSCUT");
            person.getAllPartyList().add("NECUNOSCUT");
            logger.severe("Cannot parser party for member "+person.getPersonId());
        }

        elements=doc.select("html >body > table >tbody >tr > td:eq(1) > table:eq(1) > tbody > tr > td:eq(2) > table > tbody > tr > td > table > tbody > tr:eq(1)  > td:eq(1) > table > tbody ");

        person.setContactInformation(elements.get(elements.size()-1).text());
        if (person.getContactInformation().contains("Luări de cuvânt"))
        {
            person.setContactInformation("");
        }

        for (int elementIndex=0;elementIndex<elements.size(); elementIndex++)
        {
            if (elements.get(elementIndex).text().contains("Luări de cuvânt"))
            {
                Elements subelements=elements.get(elementIndex).select("tr");
                for (int subelementIndex=0;subelementIndex<subelements.size(); subelementIndex++) {
                    Element subelement = subelements.get(subelementIndex);
                    if (subelement.text().contains("Luări de cuvânt") && !subelement.text().contains("Luări de cuvânt în BP")) {
                        int speeches = Integer.valueOf(subelement.children().last().text().split(" ")[0]);
                        person.setSpeeches(speeches);
                    } else if (subelement.text().contains("Declaraţii politice")) {
                        int statements = Integer.valueOf(subelement.children().last().text().split(" ")[0]);
                        person.setStatements(statements);
                    } else if (subelement.text().contains("Întrebari şi interpelari")) {
                        int inquiries = Integer.valueOf(subelement.children().last().text().split(" ")[0]);
                        person.setInquiries(inquiries);
                    } else if (subelement.text().contains("Motiuni")) {
                        int motions = Integer.valueOf(subelement.children().last().text().split(" ")[0]);
                        person.setMotions(motions);
                    } else if (subelement.text().contains("Propuneri legislative initiate")) {
                        Pattern p = Pattern.compile("-?\\d+");
                        Matcher m = p.matcher(subelement.children().last().text());

                        if (m.find()) {
                            int proposedLaw = Integer.valueOf(m.group());
                            person.setProposedLaw(proposedLaw);
                        }
                        if (m.find()) {
                            int passedLaw = Integer.valueOf(m.group());
                            person.setPassedLaw(passedLaw);
                        }
                    }
                }

                break;
            }
        }
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
                person.setCounty(TextUtils.flattenToAscii(countyString.split(" ")[1]));
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

    public Document getAllStartDateDocument() throws IOException, InterruptedException {
        return HttpRequester.getDocument(createStartDateRequestDocument());
    }
    public Map<Long,Long> getAllStartDates() throws IOException, InterruptedException {
        Document doc=getAllStartDateDocument();
        Map<Long, Long> personStartTimestamp=new HashMap<>();
        Elements elements=doc.select("#content > div > div.content-right > div > div.program-lucru-detalii.clearfix > div:nth-child(2) > table > tbody > tr");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        for(Element element:elements)
        {
            Elements subelements=element.select("td");
            String startDateString=subelements.get(subelements.size()-1).text().trim();
            //19.12.2012 13:41
            DateTime startDate=new DateTime(legislatureYear.intValue(),12,18,0,0);
            if (!startDateString.isEmpty())
            startDate=formatter.parseDateTime(startDateString);
            String url=subelements.get(1).child(0).child(0).attr("href").trim();
            if (url.isEmpty()) {
                logger.warning("Cannot find profile URL:"+element.text());
                continue;
            }
            Map<String, String> urlParts=TextUtils.splitQuery(new URL("http://cdep.ro"+url));
            Long deputyId=-1L;
            try{
                String idm=urlParts.get("idm");
                deputyId=Long.valueOf(idm);

            }
            catch (NumberFormatException nfex)
            {
                logger.warning("Unable to parser idm:"+element.text());
                continue;
            }
            personStartTimestamp.put(deputyId, startDate.getMillis());
            logger.info(deputyId+ " "+startDate);
        }
        return personStartTimestamp;
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


    public SortedMap<String, SortedMap<Long, SortedMap<String, SortedSet<String>>>> getCircumscriptionsHierarchy(List<String> countiesFlattened) throws IOException, InterruptedException {
        Counties counties=new Counties();
        //county -> circumscription -> town -> streets[]
        SortedMap<String, SortedMap<Long, SortedMap<String, SortedSet<String>>>> fullMap=new TreeMap<>();

        for (String county : countiesFlattened) {
            Long circumscription = counties.getCircumscription(county);
            //circumscription -> locality -> streets[]
            SortedMap<Long, SortedMap<String, SortedSet<String>>> circumscriptionMap=new TreeMap<>();

            for (long colegiu = 1L; colegiu < 50; colegiu++) {
                SortedMap<String, SortedSet<String>>  localityMap=new TreeMap<>();

                localityMap.putAll(parseCircumscriptionDocumentToHierarchy(getCircumscriptionDocument(county.toUpperCase().replace("-", "%20"), colegiu, "CD"), county));
                localityMap.putAll(parseCircumscriptionDocumentToHierarchy(getCircumscriptionDocument(county.toUpperCase().replace("-", "%20"), colegiu, "C"), county));

                if (localityMap.size()>0) {
                    circumscriptionMap.put(colegiu, localityMap);
                }
                else
                {
                    break;
                }
            }

            fullMap.put(county, circumscriptionMap);
        }
        return fullMap;
    }
    public List<Location> getAllCircumscriptions(List<String> countiesFlattened) throws IOException, InterruptedException {
        Counties counties=new Counties();
        List<Location> locations=new ArrayList<>();
        Set<String> countiesReceived=new HashSet<>();
        Set<String> emptyCounty=new HashSet<>();
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

    public void setLegislatureYear(Long legislatureYear) {
        this.legislatureYear = legislatureYear;
    }
}
