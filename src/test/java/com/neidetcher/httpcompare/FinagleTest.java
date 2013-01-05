package com.neidetcher.httpcompare;



import org.junit.Test;

/**
 * This requires a live connection
 */
public class FinagleTest {
	
	private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

	/**
	 * Note that we can't set the pool size in the java.net implementation.
	 */
//	private HttpURLConnection createClient(String url, int poolSize, int timeout) throws Throwable {
//		return null;
//	}
	
	@Test public void testFoo() {
		
		System.out.println("hello world");
	}
}