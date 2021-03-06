package com.neidetcher.httpcompare;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/** 
 * Courtesy of
 * http://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
 * 
 * This shows how to do HTTPS
 * http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html
 */
public class Serve {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new TestHandler());
        server.createContext("/time", new TimeHandler());
        server.createContext("/ip", new IpHandler());
        //server.createContext("user-agent", new UserAgentHandler());
        server.createContext("/headers", new HeadersHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    
    static class HeadersHandler extends HandlerHelper implements HttpHandler{
    	public void handle(HttpExchange httpExchange) throws IOException{
    		StringBuffer sb = new StringBuffer("\t\"headers\": {");
        	
    		Headers headers = httpExchange.getRequestHeaders();
        	for(String currKey : headers.keySet()){
        		sb.append("\n\t\t\"" + currKey + "\": \"" + headers.get(currKey) + "\"");
        	}
        	sb.append("\t}");
        	sendString(sb.toString(), httpExchange);
    	}
    }
    
    static class TestHandler extends HandlerHelper implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
        	StringBuffer sb = new StringBuffer("=== test ===");
        	sb.append("\nprotocol       : " + httpExchange.getProtocol());
        	sb.append("\nrequest method : " + httpExchange.getRequestMethod());
        	sb.append("\nrequest URI    : " + httpExchange.getRequestURI());
        	sb.append("\nrequest headers: ");
        	
        	Headers headers = httpExchange.getRequestHeaders();
        	for(String currKey : headers.keySet()){
        		sb.append("\n\t" + currKey + ": " +  headers.get(currKey));
        	}
        	
        	sb.append("\nrequest body   : ");
        	sb.append(convertStreamToString(httpExchange.getRequestBody()));
        	
        	sendString(sb.toString(), httpExchange);
        }
    }
    
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    static class TimeHandler extends HandlerHelper implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	sendString("" +System.currentTimeMillis(), t);
        }
    }
    
    static class IpHandler extends HandlerHelper implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
        	sendString("\t\"origin\": \"" + t.getRemoteAddress().toString() + "\"", t);
        }
    }
    // TODO
    static class UserAgentHandler extends HandlerHelper implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			// TODO Auto-generated method stub
			/// "user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11"
			sendString("\tuser-agent: \"" + "TODO!", t);
		}
    	
    }
    
    static class HandlerHelper{
    	public void sendString(String responseIn, HttpExchange t) throws IOException{
    		String response = "{\n" + responseIn + "\n}";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();	
    	}
    }
    
    // user-agent
    // {
    // "user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11"
    // }

    
    // get
//    {
//    	  "url": "http://httpbin.org/get",
//    	  "headers": {
//    	    "Content-Length": "",
//    	    "Accept-Language": "en-US,en;q=0.8",
//    	    "Accept-Encoding": "gzip,deflate,sdch",
//    	    "Host": "httpbin.org",
//    	    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
//    	    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11",
//    	    "Accept-Charset": "ISO-8859-1,utf-8;q=0.7,*;q=0.3",
//    	    "Connection": "keep-alive",
//    	    "Referer": "http://httpbin.org/",
//    	    "Content-Type": ""
//    	  },
//    	  "args": {},
//    	  "origin": "70.188.122.53"
//    	}

    // http://httpbin.org/status/500
    // just returns status, no content
    
    // delay  http://httpbin.org/delay/3
//    {
//    	  "origin": "70.188.122.53",
//    	  "files": {},
//    	  "form": {},
//    	  "headers": {
//    	    "Content-Length": "",
//    	    "Accept-Language": "en-US,en;q=0.8",
//    	    "Accept-Encoding": "gzip,deflate,sdch",
//    	    "Host": "httpbin.org",
//    	    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
//    	    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11",
//    	    "Accept-Charset": "ISO-8859-1,utf-8;q=0.7,*;q=0.3",
//    	    "Connection": "keep-alive",
//    	    "Referer": "http://httpbin.org/",
//    	    "Content-Type": ""
//    	  },
//    	  "url": "http://httpbin.org/delay/3",
//    	  "args": {},
//    	  "data": ""
//    	}
    
//    /post Returns POST data.
//    /put Returns PUT data.
//    /delete Returns DELETE data
    
}