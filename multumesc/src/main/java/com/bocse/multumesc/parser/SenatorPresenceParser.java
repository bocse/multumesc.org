package com.bocse.multumesc.parser;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.joda.time.DateTime;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bocse on 20.11.2015.
 */
public class SenatorPresenceParser {
    private final static Logger logger = Logger.getLogger(DeputyPresenceParser.class.toString());
    //final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
    final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
    //final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    public boolean debugMode=false;
    private String lastPayload;
    private DateTime lastDate;
    private HtmlPage profilePage;
    private HtmlPage votePage;
    private HtmlPage votePageNoPaging;

    public SenatorPresenceParser()
    {


    }

    public void init()
    {
        initClient();
        initConnectionWrapper();
    }
    private void initClient()
    {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);

    }

    //pagination starts 1 jan 2000 -> day 0
    private void initConnectionWrapper()
    {
        /*
        webClient.setAjaxController(new AjaxController(){
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            {
                return true;
            }
        });
        */
        new WebConnectionWrapper(webClient) {

            public WebResponse getResponse(WebRequest request) throws IOException {
                //Don't let the bastards know
                //if (request.getUrl().toString().contains("http://www.google-analytics.com/r/collect"))
                // request.setUrl(new URL("http://devnull-as-a-service.com/dev/null"));

                WebResponse response = super.getResponse(request);
                logger.info("Request: "+request.getUrl());
                if (request.getHttpMethod().name().equals("POST"))
                {
                    logger.info("POST DETECTED: "+request.getUrl());
                    if (debugMode)
                    logger.info(response.getContentAsString());
                    //HtmlPage secondLevelPage = HTMLParser.parseHtml(response, webClient.getCurrentWindow());

                }

                return response;
            }
        };

    }

    public void initProfilePage(String url) throws IOException, InterruptedException {
        profilePage = webClient.getPage(url);

        logger.info(profilePage.getTitleText());


        //javascript:__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')
        //javascript:__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')
        //javascript:__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')


        //TODO: Investigate if can be handled with form submit
        final String javaScriptCode = "__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')";
        Object result = profilePage.executeJavaScript(javaScriptCode).getJavaScriptResult();
        logger.info("Sample:" + result);
        logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(10000);
        webClient.waitForBackgroundJavaScript(10000);

        votePage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        final String pageAsXml = votePage.getWebResponse().getContentAsString();
        //logger.info(pageAsXml);
        List<DomElement> paginationElement = votePage.getElementsByName("ctl00$B_Center$VoturiPlen1$chkPaginare");
        paginationElement.get(0).click();
        HtmlForm form = votePage.getForms().get(0);
        votePageNoPaging = (HtmlPage) form.fireEvent(Event.TYPE_SUBMIT).getNewPage();
        webClient.waitForBackgroundJavaScriptStartingBefore(10000);
        webClient.waitForBackgroundJavaScript(10000);
        //logger.info(votePageNoPaging.getWebResponse().getContentAsString());

        //CLICK WORKS
        //paginationElement.get(0).click();
        //paginationElement.get(0).removeAttribute("checked");

        //final String javaScriptCodePagination ="setTimeout('__doPostBack(\\'ctl00$B_Center$VoturiPlen1$chkPaginare\\',\\'\\')', 0)";
        //JS NOT WORK
        //final String javaScriptCodePagination ="__doPostBack('ctl00$B_Center$VoturiPlen1$chkPaginare', '')'";
        //result = profilePage.executeJavaScript(javaScriptCodePagination).getJavaScriptResult();
        //logger.info("Waiting for JS");
        webClient.waitForBackgroundJavaScriptStartingBefore(10000);
        webClient.waitForBackgroundJavaScript(10000);


        //votePageNoPaging=(HtmlPage)webClient.getCurrentWindow().getEnclosedPage();
        //final String pageAsXml2 = votePageNoPaging.getWebResponse().getContentAsString();
        //logger.info(pageAsXml2);

        //javascript:__doPostBack('ctl00$B_Center$VoturiPlen1$calVOT','2860')


    }

    public void setMonthPage(int monthIndex)
    {
        final String javaScriptCodePageX ="__doPostBack('ctl00$B_Center$VoturiPlen1$calVOT','"+monthIndex+"');";
        //votePageNoPaging.executeJavaScript()
    }

    public void setYearPage()
    {

    }

    public void getVoteList(long dayIndex) throws InterruptedException {
            final String javaScriptCodePageX ="__doPostBack('ctl00$B_Center$VoturiPlen1$calVOT','"+dayIndex+"');";
            debugMode=true;
            Object result = votePageNoPaging.executeJavaScript(javaScriptCodePageX).getJavaScriptResult();
            logger.info("Waiting for JS");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            webClient.waitForBackgroundJavaScript(10000);
            Thread.sleep(1);


            votePageNoPaging = (HtmlPage)webClient.getCurrentWindow().getEnclosedPage();
            final String pageAsXmlPageX = votePageNoPaging.getWebResponse().getContentAsString();
            //logger.info(pageAsXmlPageX);
            logger.info("TL Windows"+webClient.getTopLevelWindows().size());
            logger.info("TL Windows"+webClient.getWebWindows().size());
            //<input id="ctl00_B_Center_VoturiPlen1_chkPaginare" type="checkbox" name="ctl00$B_Center$VoturiPlen1$chkPaginare" checked="checked" onclick="javascript:setTimeout(&#39;__doPostBack(\&#39;ctl00$B_Center$VoturiPlen1$chkPaginare\&#39;,\&#39;\&#39;)&#39;, 0)" /><label for="ctl00_B_Center_VoturiPlen1_chkPaginare">Cu Paginarea Rezultatului</label>
            debugMode=false;
    }

}
