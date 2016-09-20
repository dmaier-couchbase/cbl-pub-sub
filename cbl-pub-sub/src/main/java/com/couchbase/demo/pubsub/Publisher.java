package com.couchbase.demo.pubsub;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.replicator.Replication;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class Publisher {

    private static final Logger LOG = Logger.getLogger(Publisher.class.getName());
    
    public static final String DATA_DIR = "data";
    public static final String DB_NAME = "messages-out";
    public static final String PUB_USER = "sync_user";
    public static final String PUB_PWD = "pwd";
    
    public static final String PUB_GW_HOST = "ubuntu-local";
    public static final int PUB_GW_PORT = 4984;
    public static final String PUB_GW_DB = "sync_gateway";
    
    
    private Manager mgr;
    private Database db;
    private boolean isReady = false;
    
    public Publisher() {
        
        try {
    
                this.mgr = new Manager(new JavaContext(DATA_DIR), Manager.DEFAULT_OPTIONS);
                this.db = mgr.getDatabase(DB_NAME);

                LOG.log(Level.INFO, "db = {0}", db.getName());
                LOG.log(Level.INFO, "open = {0}", db.isOpen());

                URL url = Helper.makeURL("http", PUB_USER, PUB_PWD, PUB_GW_HOST, PUB_GW_PORT, "/" + PUB_GW_DB + "/");
                
                LOG.log(Level.INFO, "url = {0}", url);
                
                Replication push = this.db.createPushReplication(url);
                push.setContinuous(true);
                push.start();
                
                LOG.log(Level.INFO, "user = {0}", push.getUsername());
                LOG.log(Level.INFO, "push = {0}", push.getStatus().toString());
                
                this.isReady = true;
                
        } catch (Exception ex) {
            
            LOG.severe("Could not initialize publisher!");
            //ex.printStackTrace();
        }
    }
    
    public boolean bind() {

        boolean result = false;
        
        if (isReady) {

            List<String> channels = new ArrayList<>();
            channels.add("messages");

            Map<String, Object> props = new HashMap<String, Object>();
            props.put("type", "subscription");
            props.put("user", PUB_USER);
            props.put("channels", channels);

            //TODO: Solve conflict if already there
            Document document = db.getDocument("subscription::" + PUB_USER);

            try {

                document.putProperties(props);
                result = true;

            } catch (Exception ex) {

                LOG.warning("Could not bind publisher to the 'messages' channel. It might be already bound.");
                //ex.printStackTrace();
            }
            
        } 
        
        return result;
    }
    
    public void publishDemo()  {
        
        for (int i = 0; i < 1000; i++) {

            List<String> channels = new ArrayList<>();
            channels.add("messages");
            
            Document d = db.createDocument();
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("data", "Hello world!");
            props.put("channels", channels);
            
            try {
            
                d.putProperties(props);
                
                //Wait a bit before the next message will be produced
                Thread.sleep(100);
            
            } catch (Exception ex) {
                
                LOG.severe("Could not create message!");
            }
            
        }
        
    }
        
}
