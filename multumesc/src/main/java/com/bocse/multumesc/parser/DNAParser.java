package com.bocse.multumesc.parser;

import com.bocse.multumesc.data.DNARecord;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.requester.HttpRequester;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bogdan.bocse on 3/16/2016.
 */
public class DNAParser {
    private final int connectionRequestTimeout=30000;
    private final int connectionTimeout=30000;
    private final int socketTimeout=99000;
    private final Logger logger = Logger.getLogger(this.getClass().toString());
    final HttpClient client = HttpClients.createDefault();
    final CookieStore cookieStore = new BasicCookieStore();
    final HttpContext httpContext = new BasicHttpContext();
    private String pnaSession=null;
    public DNAParser()
    {

    }

    public void init() throws IOException, InterruptedException {
        final String url ="http://www.pna.ro/";
        final HttpGet httpGet = new HttpGet(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine());

        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
        Document document = client.execute(httpGet, new ResponseHandler<Document>() {
            @Override
            public Document handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                HttpEntity entity = httpResponse.getEntity();
                Document doc = Jsoup.parse(entity.getContent(), "UTF-8","");
                        //String StringFromInputStream = IOUtils.toString(entity.getContent(), "UTF-8");
                //logger.info(StringFromInputStream);
                return doc;
            }
        }, httpContext);

        Document pnaRoot= document;
        Elements elements=pnaRoot.select("input[name=javax.faces.ViewState]");
        if (elements.size()>0)
        {
            pnaSession=elements.get(0).attr("value");
        }
        logger.info("Retrieved PNA session "+pnaSession);
    }

    public List<DNARecord> doSearch(Person person, Boolean deputat) throws IOException, InterruptedException {
        if (pnaSession == null)
            init();
        List<DNARecord> records=new ArrayList<>();
        String fullName=(person.getLastName()+" "+person.getFirstName()).toLowerCase();
        if (deputat)
            fullName=fullName+" deputat";
        final String domain="http://www.pna.ro";
        final String url = domain+"/faces/index.xhtml";
        final HttpPost httpPost = new HttpPost(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
        List<NameValuePair> nameValuePairs = new ArrayList<>(10);

        nameValuePairs.add(new BasicNameValuePair("j_idt13", "j_idt13"));
        nameValuePairs.add(new BasicNameValuePair("j_idt43", fullName));
        nameValuePairs.add(new BasicNameValuePair("j_idt44", "1"));
        nameValuePairs.add(new BasicNameValuePair("j_idt47", "Caută"));
        nameValuePairs.add(new BasicNameValuePair("j_idt61", null));
        nameValuePairs.add(new BasicNameValuePair("j_idt62", "2"));
        nameValuePairs.add(new BasicNameValuePair("javax.faces.ViewState", pnaSession));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        Document pnaResults = client.execute(httpPost, new ResponseHandler<Document>() {
            @Override
            public Document handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                HttpEntity entity = httpResponse.getEntity();
                Document doc = Jsoup.parse(entity.getContent(), "UTF-8", "");
                return doc;
            }
        }, httpContext);

        //Document pnaResults = HttpRequester.getDocument(httpPost);
        Elements elements = pnaResults.select("tr[class=w130], tr[class=odd]");

        for (Element element : elements)
        {
            String text=element.text();
            Elements parts=element.select("td");
            if (parts.size()==2)
            {
                String dateString=parts.get(0).text();
                String recordUrl=parts.get(1).select("a").attr("href");
                String title=parts.get(1).select("a").text();
                DNARecord dnaRecord=new DNARecord();
                dnaRecord.setLink(domain+recordUrl);
                dnaRecord.setDate(dateString);
                dnaRecord.setTitle(title);
                doValidation(person, dnaRecord);
                records.add(dnaRecord);
                //logger.info(dateString+"\t"+title+"\t"+recordUrl);
            }

        }
        return records;
    }

    public Boolean doValidation(Person person, DNARecord record) throws IOException {
        String firstName=person.getFirstName().replaceAll("[^a-zA-Z ]", "").toLowerCase();
        String lastName=person.getLastName().replaceAll("[^a-zA-Z ]", "").toLowerCase();

        final HttpGet httpGet = new HttpGet(record.getLink());
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        logger.info("Executing request " + httpGet.getRequestLine());

        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
        Document document = client.execute(httpGet, new ResponseHandler<Document>() {
            @Override
            public Document handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                HttpEntity entity = httpResponse.getEntity();
                Document doc = Jsoup.parse(entity.getContent(), "UTF-8","");
                //String StringFromInputStream = IOUtils.toString(entity.getContent(), "UTF-8");
                //logger.info(StringFromInputStream);
                return doc;
            }
        }, httpContext);
        Elements elements=document.select("span");
        for (Element paragraph : elements)
        {
            String paragraphText=paragraph.text().replaceAll("[^a-zA-Z ]", "").toLowerCase();
            //if (paragraphText.contains(firstName) && paragraphText.contains(lastName) && paragraphText.contains("deputat"))
            if (paragraphText.contains(lastName+" "+firstName)) {
                if (paragraphText.contains("deputat")) {
                    record.setStrongValidation(true);
                    record.setWeakValidation(true);
                    person.getConfirmedRecordList().add(record);
                }
                else
                {
                    record.setWeakValidation(true);
                    person.getOtherRecordList().add(record);
                }
                return true;
            }
        }
        return false;
    }

}
