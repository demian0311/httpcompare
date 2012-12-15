package com.neidetcher.httpcompare;

import java.io.IOException;
import java.io.InputStream;
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
        server.setExecutor(null); // creates a default executor
        server.start();
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
        	sendString(t.getRemoteAddress().toString(), t);
        }
    }
    static class HandlerHelper{
    	public void sendString(String response, HttpExchange t) throws IOException{
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();	
    	}
    }
}