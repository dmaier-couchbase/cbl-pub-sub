package com.couchbase.demo.pubsub;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.replicator.Replication;
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
public class Subscriber {

    private static final Logger LOG = Logger.getLogger(Subscriber.class.getName());
    
    public static final String DATA_DIR = "data";
    public static final String DB_NAME = "messages-in";

    public static final String SUB_USER = "sync_user";
    public static final String SUB_PWD = "pwd";    
    public static final String SUB_GW_HOST = "ubuntu-local";
    public static final int SUB_GW_PORT = 4984;
    public static final String SUB_GW_DB = "sync_gateway";
    
    
    private Manager mgr;
    private Database db;
    private boolean isReady = false;
    
    public Subscriber() {
        
        try {
    
                this.mgr = new Manager(new JavaContext(DATA_DIR), Manager.DEFAULT_OPTIONS);
                this.db = mgr.getDatabase(DB_NAME);

                LOG.log(Level.INFO, "db = {0}", db.getName());
                LOG.log(Level.INFO, "open = {0}", db.isOpen());

                URL url = Helper.makeURL("http", SUB_USER, SUB_PWD, SUB_GW_HOST, SUB_GW_PORT, "/" + SUB_GW_DB + "/");
                
                LOG.log(Level.INFO, "url = {0}", url);
                
                Replication pull = this.db.createPullReplication(url);
                pull.setContinuous(true);
                pull.start();
                
                LOG.log(Level.INFO, "user = {0}", pull.getUsername());
                LOG.log(Level.INFO, "pull = {0}", pull.getStatus().toString());
                
                this.isReady = true;
                
        } catch (Exception ex) {
            
            LOG.severe("Could not initialize subscriber!");
            //ex.printStackTrace();
        }
    }
    
    public boolean subscribe() {

        boolean result = false;
        
        if (isReady) {

            List<String> channels = new ArrayList<>();
            channels.add("messages");

            Map<String, Object> props = new HashMap<String, Object>();
            props.put("type", "subscription");
            props.put("user", SUB_USER);
            props.put("channels", channels);

            //TODO: Solve conflict if already there
            Document document = db.getDocument("subscription::" + SUB_USER);

            try {

                document.putProperties(props);
                result = true;

            } catch (Exception ex) {

                LOG.warning("Could not subscribe to the 'messages' channel. User might be already bound.");
                //ex.printStackTrace();
            }
            
        } 
        
        return result;
    }
    
    public void subscribeDemo()  {
        
        this.db.addChangeListener(new Database.ChangeListener() {
            @Override
            public void changed(Database.ChangeEvent ce) {
        
                
                for (DocumentChange change : ce.getChanges()) {
                    
                    String id = change.getDocumentId();
                    String rev = change.getRevisionId();
                    
                    LOG.log(Level.INFO, "doc = {0}, rev = {1}", new Object[]{id, rev});
                }
   
            }
        });
          
    }
    
  
    
}
