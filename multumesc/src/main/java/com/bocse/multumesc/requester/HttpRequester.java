package com.bocse.multumesc.requester;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by bocse on 22.11.2015.
 */
public class HttpRequester {
    private final static Logger logger = Logger.getLogger(HttpRequester.class.toString());
    private final static Long maxAttempts=10L;
    private final static Long initialDelay=1300L;
    private final static Double backoffExponent=1.9;


    private final static String[] userAgents= {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/601.2.7 (KHTML, like Gecko) Version/9.0.1 Safari/601.2.",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko"
    };
    public static Document getDocument(HttpUriRequest httpRequest) throws IOException, InterruptedException {
        return getDocument(httpRequest, true);
    }

    public static Document getDocument(HttpUriRequest httpRequest, final Boolean throwOnEmptyDocument) throws IOException, InterruptedException {
        Document doc=null;
        Long delay = initialDelay;
        Long attemptIndex = 0L;
        Boolean isSuccess = false;
        httpRequest.addHeader("User-Agent", userAgents[(int) (System.nanoTime() % userAgents.length)] + (System.nanoTime() % 99+1));
        while (!isSuccess & attemptIndex < maxAttempts) {
            try {
                CloseableHttpClient httpclient =
                        HttpClients.custom().
                                setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))

                                .build();




                try {

                    // Create a custom response handler
                    ResponseHandler<Document> responseHandler = new ResponseHandler<Document>() {


                        public Document handleResponse(
                                final HttpResponse response) throws IOException {
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


}
