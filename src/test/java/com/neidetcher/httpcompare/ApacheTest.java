package com.neidetcher.httpcompare;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
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
public class ApacheTest {
	
	private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";
	
	private HttpClient createClient(int poolSize, int timeout) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
		         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setDefaultMaxPerRoute(poolSize);
		HttpClient httpClient = new DefaultHttpClient(cm);
		httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, timeout);
		httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, timeout);
		
		return httpClient;
	}
	
	@Test public void testFoo() throws Throwable, IOException {
		HttpClient client = createClient(200, 1000);
		HttpUriRequest request = new HttpGet(URL);
		HttpResponse response = client.execute(request);
		System.out.println("response: " + response.getStatusLine());
		System.out.println(EntityUtils.toString(response.getEntity()));
	}
}