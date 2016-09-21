# Publish-Subscribe with Couchbase SyncGateway and CouchbaseLite for Java

A publish subsribe Java example with Couchbase Sync Gateway

<img src="https://github.com/dmaier-couchbase/cbl-pub-sub/raw/master/pub-sub-architecture.png" width="300">

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

## Sync Gateway + Couchbase Server as persistent message queue

Sync Gateway takes not just care of the message routing (by mapping them to channels). Furthermore it ensures that the messages are stored in Couchbase Server. So a new and empty local database will get all the messages those were mapped to a specific channel and to which the user is subscribed to.

## Couchbase Lite as message cache

This example uses Couchbase Lite for Java. The advantage of using Couchbase Lite is that it provides a framework arroud the data synchronization. The local database also acts as a message cache (for sent and received messages). However, it might be that e.g. your publisher doesn't need to cache the messages, whereby your subscriber could benefit of having them cached. In this case the publisher can directly interact with the Sync Gateway database via a REST interface. A publishing would be to PUT a document to the database:

```
PUT /{db}/{doc}
```

Subscribing without caching would be access the changes feed for specific channels.

```
GET /{db}/_changes
```
