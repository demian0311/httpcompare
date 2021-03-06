package com.neidetcher.httpcompare;

import java.io.IOException;
import java.util.concurrent.Callable;
import static java.util.concurrent.TimeUnit.MINUTES;

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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This requires a live connection
 */
public class ApacheInMemoryCacheTest {

    private static String URL_BASE = "http://weather.yahooapis.com/forecastrss?w=";
    private static int POOL_SIZE = 200;
    private static int TIMEOUT = 1000;

    private HttpClient client = null;
    private Cache<String, String> cache = null;

    // lazily create the cache
    private Cache<String, String> getCache() {
        if (cache == null) {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(10, MINUTES)
                    .maximumSize(1000)
                    .initialCapacity(100).build();
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

    /**
     * This callable is for Guava caching.  This is where we would 
     * do the real work to get the data over the network.
     */
    private class GetWeather implements Callable<String> {
        private HttpGet url = null;

        public GetWeather(String locationIn) {
            url = new HttpGet(URL_BASE + locationIn);
        }

        public String call() throws Exception {
            HttpResponse response = getClient().execute(url);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new Exception("got a bad status: " + statusCode);
            }

            String body = EntityUtils.toString(response.getEntity());
            if (body.contains("City not found")) {
                throw new Exception("couldn't find city");
            }

            return body;
        }
    }

    private String getWeather(String location) {
        try {
            return getCache().get(
                    location, 
                    new GetWeather(location));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testFoo() throws Throwable, IOException {
        for (int i = 0; i < 20; i++) {
            Timer timer = new Timer("YAHOO-WEATHER");

            String result = getWeather("12788310");
            System.out.println(i + "|" + timer.stop() + "|result: " 
                    + result.substring(0, 40) + "...");
        }
        
        System.out.println("hit rate: " + getCache().stats().hitRate());
    }
}