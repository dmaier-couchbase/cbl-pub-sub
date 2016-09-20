package com.couchbase.demo.pubsub.threading;

import com.couchbase.demo.pubsub.Publisher;

/**
 *
 * @author david
 */
public class PublisherRunnable implements Runnable {

    @Override
    public void run() {
       
        Publisher pub = new Publisher();
        pub.bind();
        pub.publishDemo();
    }
    
}
