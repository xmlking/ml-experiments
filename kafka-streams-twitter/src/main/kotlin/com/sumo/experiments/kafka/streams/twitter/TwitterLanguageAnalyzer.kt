package com.sumo.experiments.kafka.streams.twitter

import com.sumo.experiments.kafka.streams.twitter.utils.GenericAvroSerde
import com.sumo.experiments.ml.nlp.LanguageClassifier
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.slf4j.LoggerFactory


class TwitterLanguageAnalyzer {

    private lateinit var kafkaStreams: KafkaStreams

    fun run() {

        val streamsConfig = StreamsConfig(properties)

        val kStreamBuilder = KStreamBuilder()

        val classifier = LanguageClassifier()
        classifier.train("twitterTrainingData_clean.csv")

        val languageToKey = { k: GenericRecord, v: GenericRecord ->
            if (v.get("text").toString().isNotBlank()) classifier.classify(v.get("text").toString()) else "unknown"
        }

        val tweetToKeyValue = { lang: String, tweet: GenericRecord -> KeyValue(tweet.get("user"), tweet) }

        val isEnglish = Predicate({ k: String, v: GenericRecord -> k.equals("english") })
        val isFrench = Predicate({ k: String, v: GenericRecord -> k.equals("french") })
        val isSpanish = Predicate({ k: String, v: GenericRecord -> k.equals("spanish") })
        val isGerman = Predicate({ k: String, v: GenericRecord -> k.equals("german") })

        val tweetStream = kStreamBuilder.stream<GenericRecord, GenericRecord>("twitter")

        val filteredStreams = tweetStream.selectKey(languageToKey).branch(isEnglish, isFrench, isSpanish, isGerman)

        filteredStreams[0].map(tweetToKeyValue).to("english")
        filteredStreams[1].map(tweetToKeyValue).to("french")
        filteredStreams[2].map(tweetToKeyValue).to("spanish")
        filteredStreams[3].map(tweetToKeyValue).to("german")

        kafkaStreams = KafkaStreams(kStreamBuilder, streamsConfig)
        log.debug("TwitterLanguageAnalyzer started")
        kafkaStreams.start()

    }

    fun run2() {
        val streamsConfig = StreamsConfig(properties)

        val builder = KStreamBuilder()
        val feeds = builder.stream<GenericRecord, GenericRecord>("twitter")

        feeds.mapValues({ record -> System.out.println(record) })

        kafkaStreams = KafkaStreams(builder, streamsConfig)
        kafkaStreams.start()
    }

    fun stop() {
        kafkaStreams.close()
        log.debug("TwitterLanguageAnalyzer stopped")
    }


    companion object {
        private val log = LoggerFactory.getLogger(TwitterLanguageAnalyzer::class.java)

        @JvmStatic fun main(args: Array<String>) {
            val tweetsAnalyzer = TwitterLanguageAnalyzer()

            Runtime.getRuntime().addShutdownHook(Thread {
                tweetsAnalyzer.stop()
            })

            tweetsAnalyzer.run()
        }

        val properties = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "Twitter-Language-Analysis",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            StreamsConfig.ZOOKEEPER_CONNECT_CONFIG to "localhost:2181",
            StreamsConfig.KEY_SERDE_CLASS_CONFIG to GenericAvroSerde::class.java,
            StreamsConfig.VALUE_SERDE_CLASS_CONFIG to GenericAvroSerde::class.java,
            AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081",
            StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG to WallclockTimestampExtractor::class.java
        )
    }

}
