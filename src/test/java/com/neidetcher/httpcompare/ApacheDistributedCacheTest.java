package com.neidetcher.httpcompare;

import java.io.IOException;
import java.util.concurrent.Callable;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * This requires a live connection
 */
public class ApacheDistributedCacheTest {

    private static String URL_BASE = "http://weather.yahooapis.com/forecastrss?w=";
    private static int POOL_SIZE = 200;
    private static int TIMEOUT = 1000;

    private HttpClient client = null;
    private MemcachedClient cache = null;
    
    private static String CACHE_HOST="localhost";
    private static int CACHE_PORT=11211;
    private static int CACHE_TTL=5; // seconds
    
    
    private MemcachedClient getCache() {
        if (cache == null) {
            try {
                cache = new XMemcachedClient(
                        CACHE_HOST, CACHE_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache;
    }
    
    
    // lazily create the http client
    private HttpClient getClient() {
        if (client == null) {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, 
                    PlainSocketFactory.getSocketFactory()));

            PoolingClientConnectionManager cm = new PoolingClientConnectionManager(
                    schemeRegistry);
            cm.setDefaultMaxPerRoute(POOL_SIZE);
            HttpClient httpClient = new DefaultHttpClient(cm);
            httpClient.getParams().setParameter(
                    HttpConnectionParams.CONNECTION_TIMEOUT, TIMEOUT);
            httpClient.getParams().setParameter(
                    HttpConnectionParams.SO_TIMEOUT, TIMEOUT);
            client = httpClient;
        }

        return client;
    }
    

    private String getWeather(String location) throws Exception {
        String body = getCache().get(location);

        if (body == null) {
            HttpGet url = new HttpGet(URL_BASE + location);
            HttpResponse response = getClient().execute(url);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new Exception("got a bad status: " + statusCode);
            }

            body = EntityUtils.toString(response.getEntity());

            // we think it's a good result so we'll store it
            System.out.println("adding to cache");
            getCache().set(location, CACHE_TTL, body);

            // we throw an exception here but we still wanted
            // the results cached
            if (body.contains("City not found")) {
                throw new Exception("couldn't find city");
            }
        }
        return body;
    }

    @Test
    public void testFoo() throws Throwable, IOException {
        for (int i = 0; i < 20; i++) {
            Timer timer = new Timer("YAHOO-WEATHER");

            String result = getWeather("12788310");
            System.out.println(i + "|" + timer.stop() + "|result: " 
                    + result.substring(0, 40) + "...");
        }
    }
}