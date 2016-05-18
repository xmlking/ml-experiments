### Install Kafka (one time)

```bash
# brew install kafka
cd /Developer/Applications/
curl http://packages.confluent.io/archive/2.1/confluent-2.1.0-alpha1-2.11.7.tar.gz | tar xz
```

> Until Kafka 0.10.1.0 is officially released (ETA is May 2016) you must manually build Kafka 0.10.1.0.

1. Build [kafka](https://github.com/apache/kafka) version kafka_2.11-0.10.1.0-SNAPSHOT. See the section marked `Running a task on a particular version of Scala`
2. Extact the kafka_2.11-0.10.1.0-SNAPSHOT.tgz
```bash
cd /Developer/Applications/
tar -xvzf kafka_2.11-0.10.1.0-SNAPSHOT.tgz
```

#### Start Kafka Services

*you will be running all commends below from* **infrastructure/kafka** *directory*

```
cd infrastructure/kafka
export KAFKA_HOME=/Developer/Applications/kafka_2.11-0.10.1.0-SNAPSHOT
```

#### To Start Zookeeper
```bash
$KAFKA_HOME/bin/zookeeper-server-start.sh ./zookeeper.properties
```

#### To Start Kafka
```bash
$KAFKA_HOME/bin/kafka-server-start.sh ./server.properties
```

#### To Start Schema Registry
```bash
# Start Schema Registry and expose port 8081 for use by the host machine
# docker run -d --rm --name schema-registry -p 8081:8081 -e SR_KAFKASTORE_CONNECTION_URL=192.168.1.88:2181 confluent/schema-registry
/Developer/Applications/confluent-2.1.0-alpha1/bin/schema-registry-start ./schema-registry.properties
```

#### Create Kafka Topic and partitioning (one time)
```bash
$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitter
```

#### List Kafka Topics
```bash
$KAFKA_HOME/bin/kafka-topics --list --zookeeper localhost:2181
```

#### Display messages on a topic
```bash
$KAFKA_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 --topic twitter --from-beginning --property print.key=true
# Show Avro data in JSON format in the console.
$KAFKA_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 --topic twitter --property print.key=true --property schema.registry.url=http://localhost:8081

$KAFKA_HOME/bin/kafka-console-consumer.sh --topic [english|french|spanish] --zookeeper localhost:2181
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic english --zookeeper localhost:2181
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic french --zookeeper localhost:2181
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic spanish --zookeeper localhost:2181
```


*NOTE: stop Kafka first and then Zookeeper*
