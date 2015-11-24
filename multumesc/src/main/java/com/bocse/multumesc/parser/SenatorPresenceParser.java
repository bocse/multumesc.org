package com.bocse.multumesc.parser;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.font.Script;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bocse on 20.11.2015.
 */
public class SenatorPresenceParser {
    private final static Logger logger = Logger.getLogger(DeputyPresenceParser.class.toString());
    private static final DateTime reference=new DateTime().withYear(2000).withDayOfMonth(1).withMonthOfYear(1).withTime(0, 0, 0, 1);
    private DateTime setDate=new DateTime().withTime(0, 0, 0, 1);
    private static final int forcedJsWaitMs = 1;
    private static final int beforeJsMaxWaitMs = 10000;
    private static final int afterJsMaxWaitMs = 10000;
    private static final String baseURL="http://senat.ro/FisaSenator.aspx?ParlamentarID=";
    //final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
    private WebClient webClient;
    //final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    public boolean interceptMode = false;
    private String lastPayload;
    private DateTime lastDate;
    private HtmlPage profilePage;
    private HtmlPage votePage;
    private HtmlPage votePageNoPaging;
    private String senatorId;
    private Integer setYear;
    private Integer setMonth;
    private Integer setDay;
    private Person person;
    private ScriptException caughtScriptException;
    private InteractivePage caughtInteractivePage;
    //private final CollectingJavaScriptErrorListener javaScriptErrorListener = new CollectingJavaScriptErrorListener();
    public SenatorPresenceParser(Person person) {
        setYear=new DateTime().getYear();
        setMonth=new DateTime().getMonthOfYear();
        setDay=new DateTime().getDayOfMonth();
        this.person=person;
    }

    public void init() {
        initClient();
        initConnectionWrapper();
    }

    private void initClient() {
        String applicationName = "Netscape";
        String applicationVersion = "5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        int browserVersionNumeric = 46;
        BrowserVersion browser = new BrowserVersion(applicationName, applicationVersion, userAgent, browserVersionNumeric) {
            public boolean hasFeature(BrowserVersionFeatures property) {

                // change features here
                return BrowserVersion.CHROME.hasFeature(property);
            }
        };
        webClient = new WebClient(browser);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(true);

        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(InteractivePage interactivePage, ScriptException e) {
                caughtScriptException=e;
                caughtInteractivePage=interactivePage;
            }

            @Override
            public void timeoutError(InteractivePage interactivePage, long l, long l1) {

            }

            @Override
            public void malformedScriptURL(InteractivePage interactivePage, String s, MalformedURLException e) {

            }

            @Override
            public void loadScriptError(InteractivePage interactivePage, URL url, Exception e) {

            }
        });
    }


    //pagination starts 1 jan 2000 -> day 0
    private void initConnectionWrapper() {

        new WebConnectionWrapper(webClient) {

            public WebResponse getResponse(WebRequest request)  {

                try {
                    //Don't let the bastards know

                    if (request.getUrl().toString().contains("http://www.google-analytics.com/"))
                        request.setUrl(new URL("http://devnull-as-a-service.com/dev/null"));
                    WebResponse response = super.getResponse(request);
                    logger.info("Request: " + request.getUrl());
                    if (request.getHttpMethod().name().equals("POST")) {
                        logger.info("POST DETECTED: " + request.getUrl());
                        if (interceptMode) {
                            lastPayload = response.getContentAsString();
                            handlePayload(lastPayload);
                            //logger.info(lastPayload);

                        }

                    }

                    return response;
                }
                catch (Exception ex)

                {
                    logger.warning("Caught a wrapped rabbit.");
                }
                return null;
            }

        };

    }

    public void handlePayload(String payload) throws IOException {
        //#ctl00_B_Center_VoturiPlen1_GridVoturi > tbody > tr
        Document doc= Jsoup.parse(payload);
        Elements elements=doc.select("#ctl00_B_Center_VoturiPlen1_GridVoturi > tbody > tr");
        if (elements.size()==0)
            return;
        //PrintWriter writer = new PrintWriter("/home/bocse/senat/"+person.getPersonId()+"_"+person.getFullName()+"_"+setYear+"_"+setMonth+"_"+setDate.getDayOfMonth()+".txt", "UTF-8");
        PrintWriter writer = new PrintWriter("C://Temp//Senat//"+person.getPersonId()+"_"+person.getFullName()+"_"+setYear+"_"+setMonth+"_"+setDate.getDayOfMonth()+".txt", "UTF-8");


        for (Element element: elements)
        {
            writer.println(element.html());
            writer.println();
            writer.println();
            writer.println();
            writer.println();
        }
        writer.close();
    }

    public void initProfilePage(String senatorId) throws IOException, InterruptedException {
        this.senatorId=senatorId;
        profilePage = webClient.getPage(baseURL+senatorId);


        //TODO: Investigate if can be handled with form submit
        final String javaScriptCode = "__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')";
        Object result = profilePage.executeJavaScript(javaScriptCode).getJavaScriptResult();
        logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(beforeJsMaxWaitMs);
        webClient.waitForBackgroundJavaScript(afterJsMaxWaitMs);

        votePage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        List<DomElement> paginationElement = votePage.getElementsByName("ctl00$B_Center$VoturiPlen1$chkPaginare");
        paginationElement.get(0).click();
        HtmlForm form = votePage.getForms().get(0);
        votePageNoPaging = (HtmlPage) form.fireEvent(Event.TYPE_SUBMIT).getNewPage();
        logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(beforeJsMaxWaitMs);
        webClient.waitForBackgroundJavaScript(afterJsMaxWaitMs);
    }

    public void setMonthPage(int monthIndex) throws InterruptedException, IOException {
        List<DomElement> monthElements = votePage.getElementsByName("ctl00$B_Center$VoturiPlen1$drpMonthCal");
        DomElement monthElement = monthElements.get(0);
        for (DomElement particularMonth : monthElement.getChildElements()) {
            particularMonth.removeAttribute("selected");
        }
        for (DomElement particularMonth : monthElement.getChildElements()) {
            if (particularMonth.getAttribute("value").equals(String.valueOf(monthIndex))) {
                //particularMonth.setAttribute("selected","selected");
                particularMonth.click();
                logger.info(particularMonth.getAttribute("selected"));
            }
        }
        //final String javaScriptCodeMonth ="__doPostBack('ctl00$B_Center$VoturiPlen1$drpMonthCal','');";
        logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(beforeJsMaxWaitMs);
        webClient.waitForBackgroundJavaScript(afterJsMaxWaitMs);
        Thread.sleep(forcedJsWaitMs);
        setMonth=monthIndex;
    }

    public void setYearPage(int yearIndex) throws IOException, InterruptedException {
        List<DomElement> yearElements = votePage.getElementsByName("ctl00$B_Center$VoturiPlen1$drpYearCal");
        DomElement yearElement = yearElements.get(0);
        for (DomElement particularMonth : yearElement.getChildElements()) {
            particularMonth.removeAttribute("selected");
        }
        for (DomElement particularYear : yearElement.getChildElements()) {
            if (particularYear.getAttribute("value").equals(String.valueOf(yearIndex))) {

                particularYear.click();
                logger.info(particularYear.getAttribute("selected"));
            }
        }
        logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(beforeJsMaxWaitMs);
        webClient.waitForBackgroundJavaScript(afterJsMaxWaitMs);
        Thread.sleep(forcedJsWaitMs);
        setYear=yearIndex;
    }

    public void getVoteList(DateTime setDate) throws InterruptedException {
        boolean mustRetry=true;
        while(mustRetry) {
            try {
                caughtScriptException = null;
                if (setDate.getMonthOfYear() != setMonth)
                    throw new IllegalStateException("Month " + setDate.getMonthOfYear() + " has not been navigated to.");
                if (setDate.getYear() != setYear)
                    throw new IllegalStateException("Year " + setDate.getYear() + " has not been navigated to.");

                int dayIndex = (int) ((setDate.getMillis() - reference.getMillis()) / 1000 / 3600 / 24);
                this.setDate = setDate;
                setDay = dayIndex;
                final String javaScriptCodePageX = "__doPostBack('ctl00$B_Center$VoturiPlen1$calVOT','" + dayIndex + "');";
                interceptMode = true;
                Object result = votePageNoPaging.executeJavaScript(javaScriptCodePageX).getJavaScriptResult();
                logger.info("Waiting for JS");
                webClient.waitForBackgroundJavaScriptStartingBefore(beforeJsMaxWaitMs);
                webClient.waitForBackgroundJavaScript(afterJsMaxWaitMs);
                Thread.sleep(forcedJsWaitMs);

                if (caughtScriptException != null) {
                    logger.info(caughtScriptException.getMessage());
                    //logger.info(caughtScriptException.getPage().asXml());
                    logger.info("Javascript exception");
                    mustRetry=true;
                    continue;

                }

                votePageNoPaging = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
                interceptMode = false;
                logger.info("Successfully parsed "+person.getPersonId()+" "+setDate.toString()+" "+dayIndex + " "+person.getFullName());
                mustRetry=false;
            } catch (Exception ex) {
                logger.warning("We caught it!");
                ex.printStackTrace();
                mustRetry=true;
            }
        }
    }

}
