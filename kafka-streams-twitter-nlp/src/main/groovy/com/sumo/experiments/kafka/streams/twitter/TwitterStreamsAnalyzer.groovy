package com.sumo.experiments.kafka.streams.twitter

import com.sumo.experiments.kafka.streams.twitter.utils.GenericAvroDeserializer
import com.sumo.experiments.kafka.streams.twitter.utils.GenericAvroSerializer
import groovy.util.logging.Slf4j
import com.sumo.experiments.kafka.streams.twitter.domain.Tweet
import com.sumo.experiments.ml.nlp.Classifier
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.streams.kstream.ValueMapper
import org.apache.kafka.streams.processor.WallclockTimestampExtractor

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

@Slf4j
//@CompileStatic
public class TwitterStreamsAnalyzer {

    private KafkaStreams kafkaStreams;

    private static isNotBlank(String sing) {
        return sing != null && !sing.isAllWhitespace()
    }

    public static void main(String[] args) throws  IOException {
        TwitterStreamsAnalyzer streamsAnalyzer = new TwitterStreamsAnalyzer();
        streamsAnalyzer.run2();
    }

    public void run()  {
        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        JsonSerializer<Tweet> tweetJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Tweet> tweetJsonDeserializer = new JsonDeserializer<>();
        Serde<Tweet> tweetSerde = Serdes.serdeFrom(tweetJsonSerializer, tweetJsonDeserializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();

        Classifier classifier = new Classifier();
        classifier.train('twitterTrainingData_clean.csv')

        KeyValueMapper<String, Tweet, String> languageToKey = { k, v ->   isNotBlank(v.text) ? classifier.classify(v.text):"unknown" }

        Predicate<String, Tweet> isEnglish = { k, v -> k.equals("english")} as Predicate
        Predicate<String, Tweet> isFrench =  { k, v -> k.equals("french")} as Predicate
        Predicate<String, Tweet> isSpanish = { k, v -> k.equals("spanish")} as Predicate

        KStream<String, Tweet> tweetKStream = kStreamBuilder.stream(Serdes.String(), tweetSerde, "twitter");

        KStream<String, Tweet>[] filteredStreams = tweetKStream.selectKey(languageToKey).branch(isEnglish, isFrench, isSpanish);

        filteredStreams[0].to(Serdes.String(), tweetSerde, "english");
        filteredStreams[1].to(Serdes.String(), tweetSerde, "french");
        filteredStreams[2].to(Serdes.String(), tweetSerde, "spanish");

        kafkaStreams = new KafkaStreams(kStreamBuilder, streamsConfig);
        System.out.println("Starting twitter analysis streams");
        kafkaStreams.start();
        System.out.println("Started");

    }

    private static Properties getProperties() {
        Properties props = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Twitter-Streams-Analysis");
        // Where to find Kafka broker(s).
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Where to find the corresponding ZooKeeper ensemble.
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        // Where to find the Confluent schema registry instance(s)
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // Deserialize using the specific Avro reader to ensure we receive proper pojos instead of GenericRecord
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        // Specify default (de)serializers for record keys and for record values.
//        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        return props;
    }

    public void run2()  {
        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Classifier classifier = new Classifier();
        classifier.train('twitterTrainingData_clean.csv')

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, GenericRecord> feeds = builder.stream("twitter");
        feeds.mapValues({record -> println  record });
//        feeds.mapValues(new ValueMapper<GenericRecord, GenericRecord>() {
//            @Override
//            public GenericRecord apply(GenericRecord value) {
//                System.out.println(value.get("namespace"));
//                return value;
//            }
//        });
        KafkaStreams streams = new KafkaStreams(builder, streamsConfig);
        streams.start();
    }

}
