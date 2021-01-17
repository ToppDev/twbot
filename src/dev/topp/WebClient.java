package dev.topp;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WebClient implements AutoCloseable {

    private static final String[][] httpHeaders = {
            {"Connection", "keep-alive"},
            {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
            {"Accept-Encoding", "gzip, deflate, sdch"},
            {"Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"},
            {"User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36"},
            {"Content-Type", "text/html; charset=utf-8"}};

    private CloseableHttpClient httpClient;
    private static CookieStore cookieStore;

    public WebClient() {
        // Create a local instance of cookie store
        if (cookieStore == null)
            cookieStore = new BasicCookieStore();

        httpClient = HttpClients.custom()
                // Sets Cookie store in order to be able to login
                .setDefaultCookieStore(cookieStore)
                // Defines Redirect Strategy, because some pages redirect to others containing needed cookies
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
                        boolean isRedirect = false;
                        try {
                            isRedirect = super.isRedirected(request, response, context);
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                            LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, "Redirect ProtocolException: " + e.getMessage(), "WebClient()", "WebClient");
                        }
                        if (!isRedirect) {
                            int responseCode = response.getStatusLine().getStatusCode();
                            if (responseCode == 301 || responseCode == 302) {
                                return true;
                            }
                        }
                        return isRedirect;
                    }
                })
                .build();
    }

    @Override
    public void close() {
        try {
            if (httpClient != null)
                httpClient.close();
        } catch (IOException e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close httpClient: " + e.getMessage(), "close()", "WebClient");
        }
    }

    public String Get(String url) {
        String returnValue = "";

        // Create request with url.
        HttpGet request = new HttpGet(url);

        // Add header information to request.
        for (String[] httpHeader : httpHeaders)
            request.setHeader(httpHeader[0], httpHeader[1]);

        // Create response with request and wait till it's finished.
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, request.getRequestLine() + "->" + response.getStatusLine(), "Get(String)", "WebClient");

            // Get response entity and return its string value.
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                returnValue = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            } else
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Response is empty.", "Get(String)", "MyHttpClient");
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "Get(String)", "WebClient");
        }

        return returnValue;
    }

    public String Post(String url, List<NameValuePair> params) {
        String returnValue = "";

        // Create RequestBuilder to make Request
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);

        // Add header information
        for (String[] httpHeader : httpHeaders)
            requestBuilder.setHeader(httpHeader[0], httpHeader[1]);

        // Add post parameters
        for (NameValuePair param : params)
            requestBuilder.addParameter(param);

        // Build request
        HttpUriRequest request = requestBuilder.build();

        // Create response with request and wait till it's finished.
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, request.getRequestLine() + "->" + response.getStatusLine(), "Post(String, List<NameValuePair>)", "WebClient");

            // Get response entity and return its string value.
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                returnValue = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            } else
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Response is empty.", "Post(String, List<NameValuePair>)", "WebClient");
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "Post(String, List<NameValuePair>)", "WebClient");
        }

        return returnValue;
    }

    public boolean downloadFile(String url, String filename) {
        boolean returnValue = false;

        // Create request with url.
        HttpGet request = new HttpGet(url);

        // Add header information to request.
        for (String[] httpHeader : httpHeaders)
            request.setHeader(httpHeader[0], httpHeader[1]);

        // Create response with request and wait till it's finished.
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, request.getRequestLine() + "->" + response.getStatusLine(), "downloadFile(String, String)", "WebClient");

            // Get response entity and return its string value.
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (FileOutputStream fos = new FileOutputStream(filename)) {
                    entity.writeTo(fos);
                    fos.flush();
                    returnValue = true;
                }
                EntityUtils.consume(entity);
            } else
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Response is empty (" + url + ")", "downloadFile(String, String)", "WebClient");
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "downloadFile(String, String)", "WebClient");
        }

        return returnValue;
    }
}
