package com.neidetcher.httpcompare;

import com.ning.http.client.*;

import java.io.IOException;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * https://github.com/AsyncHttpClient/async-http-client
 * 
 * Setting the connection pool size:
 * https://jfarcand.wordpress.com/2010/12/21/going-asynchronous-using-asynchttpclient-the-basic/
 */
public class AsyncHttpClientTest {
    
    private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

    
    @Test public void foo() throws Throwable {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Response> f = asyncHttpClient.prepareGet(URL).execute();
        Response r = f.get();
        
        System.out.println("response: " + r.getResponseBody());
        

    }

}
