package com.neidetcher.httpcompare;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.FutureEventListener;
import com.twitter.util.Throw;
import com.twitter.util.Try;
//import scala.Option;
import scala.None;
import com.twitter.finagle.http.codec.ChannelBufferUsageTracker;


import org.junit.Test;

/**
 * This requires a live connection
 */
public class FinagleTest {
	
	private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

	/**
	 * Note that we can't set the pool size in the java.net implementation.
	 */
	private Service<HttpRequest, HttpResponse> createClient(String url, int poolSize, int timeout) {
		
		 Http http = Http.get().decompressionEnabled(false);
		 
		 // where do we give it a URL?
		
		
		return ClientBuilder.safeBuild(
	    	        ClientBuilder.get()
	    	                     .codec(http)
	    	                     .hosts(new InetSocketAddress(80))
	    	                     .tcpConnectTimeout(Duration.apply(timeout, TimeUnit.MILLISECONDS))
	    	                     .hostConnectionLimit(poolSize));
	}
	
	@Test public void testFoo() {
		Service<HttpRequest, HttpResponse> client = createClient("", 200, 1000);

	    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "example.foo");
	    
	    try {
	        HttpResponse response1 = client.apply(request).apply();
	        System.out.println("response: " + response1);
	        
	      } catch (Exception e) {
	    	  e.printStackTrace();
	         System.out.println("e: " + e.getMessage());
	      }

		
		System.out.println("hello world");
	}
}
