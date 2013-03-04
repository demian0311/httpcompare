package com.neidetcher.httpcompare;

import org.apache.http.client.HttpClient;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Response;

/**
 * https://github.com/AsyncHttpClient/async-http-client
 * 
 * Setting the connection pool size:
 * https://jfarcand.wordpress.com/2010/12/21/going-asynchronous-using-asynchttpclient-the-basic/
 */
public class AsyncHttpClientTest {
    
    private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

    private AsyncHttpClient createClient(int poolSize, int timeout) {
    	Builder builder = new AsyncHttpClientConfig.Builder();
    	AsyncHttpClientConfig config = builder
    			.setAllowPoolingConnection(true)
            	.setRequestTimeoutInMs(timeout)
            	.setMaximumConnectionsPerHost(poolSize)
            	.build();
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(config);
        
        return asyncHttpClient;
    }
    
    @Test public void foo() throws Throwable {
    	AsyncHttpClient client = createClient(200, 1000);

    	// this doesn't leverage the power of async
        Response r = client.prepareGet(URL).execute().get();
        System.out.println("response: " + r.getResponseBody());
    }
}
