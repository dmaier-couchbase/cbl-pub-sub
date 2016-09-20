package com.couchbase.demo.pubsub;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author david
 */
public class Helper {
    
    public static URL makeURL(String protocol, String user, String pwd, String host, int port, String resource) throws MalformedURLException {
        
        return new URL(protocol + "://" + user + ":" + pwd + "@" + host + ":" + port + resource);
    }
    
}
