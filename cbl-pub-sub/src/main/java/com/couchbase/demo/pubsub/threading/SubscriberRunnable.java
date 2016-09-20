package com.couchbase.demo.pubsub.threading;

import com.couchbase.demo.pubsub.Subscriber;

/**
 *
 * @author david
 */
public class SubscriberRunnable implements Runnable {

    @Override
    public void run() {
       
        Subscriber sub = new Subscriber();
        sub.subscribe();
        sub.subscribeDemo();
    }
    
}
