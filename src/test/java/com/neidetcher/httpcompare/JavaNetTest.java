package com.neidetcher.httpcompare;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

/**
 * This requires a live connection
 */
public class JavaNetTest {
	
	private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

	/**
	 * Note that we can't set the pool size in the java.net implementation.
	 */
	private HttpURLConnection createClient(String url, int poolSize, int timeout) throws Throwable {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setConnectTimeout(timeout);
	
		return con;
	}
	
	@Test public void testFoo() throws Throwable, IOException {
		
		HttpURLConnection client = createClient(URL, 200, 1000);
		int responseCode = client.getResponseCode();
		System.out.println("response: " + responseCode);
		InputStream content = (InputStream)client.getContent();
	    Reader r = new InputStreamReader(content);
	    int c;
	    while ((c = r.read()) != -1) {
	      System.out.print((char) c);
	    }
	}
}