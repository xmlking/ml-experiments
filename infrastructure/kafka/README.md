### Install Kafka (one time)

```bash
# brew install kafka
cd /Developer/Applications/
curl http://packages.confluent.io/archive/3.0/confluent-3.0.0-2.11.tar.gz | tar xz
```

#### Start Kafka

*you will be running all commends below from* **infrastructure/kafka** *directory*

```
cd infrastructure/kafka
export CONFLUENT_HOME=/Developer/Applications/confluent-3.0.0
```

#### Start Zookeeper
```bash
$CONFLUENT_HOME/bin/zookeeper-server-start ./zookeeper.properties
```

#### Start Kafka
```bash
$CONFLUENT_HOME/bin/kafka-server-start ./server.properties
```

#### Start Schema Registry
```bash
$CONFLUENT_HOME/bin/schema-registry-start ./schema-registry.properties
```

#### Start Control Center
```bash
# http://localhost:9021/
$CONFLUENT_HOME/bin/control-center-start ./controlcenter.properties
```


#### Create Kafka Topic and partitioning (one time)
```bash
$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitter
```

#### List Kafka Topics
```bash
$CONFLUENT_HOME/bin/kafka-topics --list --zookeeper localhost:2181
```

#### Display messages on a topic
```bash
$CONFLUENT_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 --topic twitter --from-beginning --property print.key=true
# Show Avro data in JSON format in the console.
$CONFLUENT_HOME/bin/kafka-avro-console-consumer --zookeeper localhost:2181 --topic twitter --property print.key=true --property schema.registry.url=http://localhost:8081

$CONFLUENT_HOME/bin/kafka-console-consumer --topic english --zookeeper localhost:2181
$CONFLUENT_HOME/bin/kafka-console-consumer --topic french --zookeeper localhost:2181
$CONFLUENT_HOME/bin/kafka-console-consumer --topic spanish --zookeeper localhost:2181
```

#### Purge Kafka Topic
```bash
# Temporarily update the retention time on the topic to one second:
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --alter --topic twitter --config retention.ms=1000
# Then set it back to default
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --alter --topic twitter --config retention.ms=600000
```

*NOTE: stop Kafka first and then Zookeeper*
