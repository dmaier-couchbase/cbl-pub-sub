/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.demo.pubsub;

import com.couchbase.demo.pubsub.threading.SubscriberRunnable;
import com.couchbase.demo.pubsub.threading.PublisherRunnable;

/**
 *
 * @author david
 */
public class Main {
    
    public static void main(String[] args) throws InterruptedException {

        //Run the publisher in the background
        new Thread(new PublisherRunnable()).start();
        
       //Run the subscriber in the background
       new Thread(new SubscriberRunnable()).start();
       
       //Terminate after 60s
       Thread.sleep(60000);
    }
    
}
