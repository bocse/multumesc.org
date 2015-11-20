package com.bocse.multumesc.parser;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;


import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by bocse on 20.11.2015.
 */
public class SenatorPresenceParser {
    private final static Logger logger = Logger.getLogger(DeputyPresenceParser.class.toString());
    final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
    public SenatorPresenceParser()
    {
        //webClient.setJavaScriptEngine(JavaScriptEngine.);
         webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
    }

    public void initParser(String url) throws IOException {

        try
        {
            new WebConnectionWrapper(webClient) {

                public WebResponse getResponse(WebRequest request) throws IOException {
                    WebResponse response = super.getResponse(request);
                    if (request.getHttpMethod().name().equals("POST"))
                    {
                        logger.info("POST DETECTED");
                        //logger.info(response.getContentAsString());
                        //HtmlPage secondLevelPage = HTMLParser.parseHtml(response, webClient.getCurrentWindow());

                    }

                    return response;
                }
            };
            final HtmlPage page = webClient.getPage(url);

            logger.info(page.getTitleText());


            String javaScriptCode =
                    "[\"Banana\", \"Orange\", \"Apple\", \"Mango\"].indexOf(\"Apple\");";

            Object result = page.executeJavaScript(javaScriptCode).getJavaScriptResult();
            logger.info("Sample:"+result);
            javaScriptCode="__doPostBack('ctl00$B_Center$meniu$ctl02$Voturi','')";
            result = page.executeJavaScript(javaScriptCode).getJavaScriptResult();
            logger.info("Sample:"+result);
            logger.info("Waiting for JS");
            webClient.waitForBackgroundJavaScript(30000);

            final String pageAsXml = webClient.getCurrentWindow().getEnclosedPage().getWebResponse().getContentAsString();
            logger.info(pageAsXml);

            //final String pageAsText = page.asText();
            //logger.info(pageAsText);

        } finally {

        }
    }
}
