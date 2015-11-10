package com.bocse.multumesc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by bocse on 10.11.2015.
 */
public class PresenceParser {

    private final static Logger logger = Logger.getLogger(App.class.toString());
    public Document getDocument(final Long personId, final Long eventId ) throws IOException {
        final String url="http://www.cdep.ro/pls/steno/eVot.mp";
        Document doc;

        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(9000).setConnectTimeout(9000).setSocketTimeout(9000).build();



        try {
            //HttpGet httpget = new HttpGet("http://httpbin.org/");
            HttpPost httppost=new HttpPost(url);
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
            System.out.println("Executing request " + httppost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<Document> responseHandler = new ResponseHandler<Document>() {


                public Document handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        Document doc=Jsoup.parse(entity.getContent(), null, "");
                        //return entity != null ? EntityUtils.toString(entity) : null;
                        return doc;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
             doc = httpclient.execute(httppost, responseHandler);
             //logger.info(doc.text());
             //logger.info(doc.html());
        } finally {
            httpclient.close();
        }
        return doc;
    }

    public List<Map<String, Object>> parserDocument(Document doc)
    {
        List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();

        //#pageContent > p > table:nth-child(2) > tbody > tr:nth-child(2)
        //#pageContent > p > table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(1)

        //#pageContent > p > table:nth-child(2) > tbody > tr:nth-child(2)
        Elements elements=doc.select("#pageContent > table:last-child > tbody > tr");

        for (int elementIndex=1; elementIndex<elements.size(); elementIndex++ ) {
            Map<String, Object> map=new HashMap<String, Object>();
            Element element= elements.get(elementIndex);
            Elements parts=element.select("td");
            for (int partIndex=0; partIndex<parts.size(); partIndex++)
            {
                String partText=parts.get(partIndex).text();
                //logger.info(partText.trim());
            }
            if (parts.size()==5)
            {
                map.put("NrCrt", Long.valueOf(parts.get(0).text().replace(".","")));
                map.put("Timestamp", (parts.get(1).text().trim()));
                map.put("IdVot", (parts.get(2).text().trim()));
                map.put("Subject", (parts.get(3).text().trim()));
                map.put("Vote", (parts.get(4).text().trim()));
            }
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getPerson(Long personId) throws IOException {
        List<Map<String, Object>> personList=new ArrayList<Map<String, Object>>();
        boolean keepGoing=true;
        Long index=1L;
        do {
            List<Map<String, Object>> partialList=parserDocument(getDocument(personId, index));
            keepGoing=partialList.size()>0;
            personList.addAll(partialList);
            logger.info("Finished parsing page "+index);
            index++;
         } while (keepGoing);
        return personList;
    }

}
