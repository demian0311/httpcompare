package com.neidetcher.httpcompare;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/** 
 * Courtesy of
 * http://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
 */
public class Serve {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/time", new TimeHandler());
        server.createContext("/ip", new IpHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
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
            //String response = "ip: " + t.getRemoteAddress();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();	
    	}
    }
}