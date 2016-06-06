Kafka Connect Twitter
=====================
A Kafka Connect for Twitter. Both a source (from Twitter to Kafka) and sink (from Kafka to Twitter) are provided:

* The *sink* receives plain strings from Kafka, which are tweeted using [Twitter4j](http://twitter4j.org/);
* The *source* receives tweets from the [Twitter Streaming API](https://dev.twitter.com/streaming/overview) using [Hosebird](https://github.com/twitter/hbc), which are fed into Kafka as a `TwitterStatus` structure.


### Build

build the fatJar:
```bash
gradle kafka-connect-twitter:shadowJar
# copy to $CONFLUENT_HOME/share/java/ ?
```

### Run

##### 1. Start Zookeeper, Kafka, Schema Registry as per: [instructions](../infrastructure/kafka/)

##### 2. Put the JAR file location into your CLASSPATH:

```bash
cd kafka-connect-twitter
export CONFLUENT_HOME=/Developer/Applications/confluent-3.0.0
export CLASSPATH=`pwd`/build/libs/kafka-connect-twitter-0.1.0-SNAPSHOT-all.jar
```

##### 3. To start a Kafka Connect Source instance:

*Structured output mode*

```bash
$CONFLUENT_HOME/bin/connect-standalone connect-source-standalone.properties twitter-source.properties
```

*Watch Avro TwitterStatus tweets come in represented as JSON*

```bash
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic twitter \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic english \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic french \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic spanish \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic german \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
         --topic positive \
         --property print.key=true \
         --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic negative \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 \
        --topic neutral \
        --property print.key=true \
        --property schema.registry.url=http://localhost:8081
```

*Simple (plain strings) output mode*

```bash
$CONFLUENT_HOME/bin/connect-standalone connect-simple-source-standalone.properties twitter-source.properties
```

*And watch tweets come in, with the key the user, and the value the tweet text*

```bash
$CONFLUENT_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 \
      --topic twitter \
      --formatter kafka.tools.DefaultMessageFormatter \
      --property print.key=true \
      --property schema.registry.url=http://localhost:8081
      --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
      --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```






