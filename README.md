# Publish-Subscribe with Couchbase SyncGateway and CouchbaseLite for Java

A publish subsribe Java example with Couchbase Sync Gateway

![alt tag](https://github.com/dmaier-couchbase/cbl-pub-sub/raw/master/pub-sub-architecture.png)


## Configuration

In order to allow users to subscribe to specific channels (a subscription means to be able to read/write from/to a channel), we are using the following sync function:

```
{
  "interface": ":4984",
  "adminInterface": ":4985",
  "log": ["REST"],
  "databases": {
    "sync_gateway": {
    "users" : {
        "sync_user" : { "password": "pwd" }
    },
    "server": "http://localhost:8091",
    "bucket": "sync_gateway",
    "sync": `function(doc) {

                if (doc.type == 'subscription') {

                    access( doc.user, doc.channels);
                }

                channel(doc.channels);
            }`
    }
  }
}
```

If a document of type 'subscription' is replicated over then give the user which is mentioned in the property 'user' access to the channels those are listed in the array 'channels'.

## Publisher

In this example, the publisher publishes via local CouchbaseLite database 'messages-out'. This local database has a push replication to SyncGateway set up. The used synchronization channel is 'messages'. Before a publisher can publish messages, it needs to bind to the channel to which it wants to publish. We do this by creating a subscription document for this channel:

```
"subscription::sync_user" : { 
  "channels": ["messages"],
  "type": "subscription",
  "user": "sync_user"
}
```

Afterwards, publishing just means to add a document to the local database which will be mapped to the channel 'messages'.

## Subscriber

The subscriber uses a local CouchbaseLite database 'messages-in'. This database has a pull replication from SyncGateway set up. The used synchronization channel is again 'messages'. Before a subscriber can receive messages, it needs to subscribe to the channel by sending a subscription document for this channel. A ChangeListener was attached to the database in order to vizualize the received messages better.
