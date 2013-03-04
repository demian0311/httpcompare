package com.neidetcher.httpcompare;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.example.http.snoop.HttpClientPipelineFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

public class NettyHttpTest {

    private static String URL = "http://weather.yahooapis.com/forecastrss?w=12788310";

    @Test
    public void testFoo() throws Throwable {
        System.out.println("Hello world!");

        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new HttpClientPipelineFactory(false));

        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "localhost" : uri.getHost();
        int port = uri.getPort();

        ChannelFuture future = bootstrap.connect(new InetSocketAddress("weather.yahooapis.com",
                80));

        org.jboss.netty.channel.Channel channel = future.awaitUninterruptibly()
                .getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        // Prepare the HTTP request.
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.GET, uri.toASCIIString());
        request.setHeader(HttpHeaders.Names.HOST, host);
        request.setHeader(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.CLOSE);
        request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
                HttpHeaders.Values.GZIP);
        CookieEncoder httpCookieEncoder = new CookieEncoder(false);
        httpCookieEncoder.addCookie("my-cookie", "foo");
        httpCookieEncoder.addCookie("another-cookie", "bar");
        request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());
        channel.write(request);
        channel.getCloseFuture().awaitUninterruptibly();

        bootstrap.releaseExternalResources();
    }

}
