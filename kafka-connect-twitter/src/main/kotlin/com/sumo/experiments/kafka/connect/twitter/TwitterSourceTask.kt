package com.sumo.experiments.kafka.connect.twitter

import com.sumo.experiments.kafka.connect.twitter.domain.TwitterStatus
import com.sumo.experiments.kafka.connect.twitter.domain.TwitterUser
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.twitter4j.Twitter4jStatusClient
import org.apache.kafka.common.utils.AppInfoParser
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import org.slf4j.LoggerFactory
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


class TwitterSourceTask : SourceTask() {

    private lateinit var sourceConfig: TwitterSourceConfig
    private lateinit var twitterBasicClient: BasicClient
    //batch size to take from the queue
    private var batchSize = TwitterSourceConfig.BATCH_SIZE_DEFAULT
    private var batchTimeout = TwitterSourceConfig.BATCH_TIMEOUT_DEFAULT
    //The Kafka topic to append to
    private var topic = TwitterSourceConfig.TOPIC_DEFAULT
    private val rawQueue = LinkedBlockingQueue<String>(10000)
    private val statusQueue = LinkedBlockingQueue<Status>(10000)
    private var statusConverter = TwitterSourceConfig.OUTPUT_FORMAT_DEFAULT

    companion private object {
        val log = LoggerFactory.getLogger(TwitterSourceTask::class.java)
        val TWEET_SOURCE = "tweetSource";
        val TWEET_LANG = "lang";
        val TWEET_ID = "tweetId";

        fun statusToStringKeyValue(status: Status, topic: String): SourceRecord {
            return SourceRecord(
                mapOf(TWEET_SOURCE to status.source, TWEET_LANG to status.lang), //source partitions?
                mapOf(TWEET_ID to status.id), //source offsets?
                topic,
                null,
                Schema.STRING_SCHEMA,
                status.user.screenName,
                Schema.STRING_SCHEMA,
                status.text
            )
        }

        fun statusToTwitterStatusStructure(status: Status, topic: String): SourceRecord {
            val ts = TwitterStatus(status)
            return SourceRecord(
                mapOf(TWEET_SOURCE to status.source, TWEET_LANG to status.lang), //source partitions?
                mapOf(TWEET_ID to status.id), //source offsets?
                topic,
                null,
                TwitterUser.SCHEMA,
                ts.get("user"),
                ts.schema(),
                ts
            )
        }
    }

    override fun version(): String = AppInfoParser.getVersion()

    override fun start(props: Map<String, String>) {
        sourceConfig = TwitterSourceConfig(props)
        batchSize = sourceConfig.getInt(TwitterSourceConfig.BATCH_SIZE)
        batchTimeout = sourceConfig.getDouble(TwitterSourceConfig.BATCH_TIMEOUT)
        topic = sourceConfig.getString(TwitterSourceConfig.TOPIC)
        statusConverter = sourceConfig.getString(TwitterSourceConfig.OUTPUT_FORMAT)

        twitterBasicClient = TwitterReader.getTwitterClient(sourceConfig, context, rawQueue)

        val twitterStatusClient = Twitter4jStatusClient(
            twitterBasicClient,
            rawQueue,
            listOf(object : StatusListener {
                override fun onScrubGeo(userId: Long, upToStatusId: Long) {
                    log.debug("onScrubGeo $userId $upToStatusId")
                }

                override fun onTrackLimitationNotice(numberOfLimitedStatuses: Int) {
                    log.info("onTrackLimitationNotice $numberOfLimitedStatuses")
                }

                override fun onStallWarning(warning: StallWarning) {
                    log.warn("onStallWarning", warning)
                }

                override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
                    log.debug("onDeletionNotice", statusDeletionNotice)
                }

                override fun onException(ex: Exception) {
                    log.warn("onException: ", ex)
                }

                override fun onStatus(status: Status) {
//                    log.debug("------------StatusListener-----onStatus----Start-----------");
//                    log.debug("id: ${status.id}, createdAt: ${status.createdAt}, lang: ${status.lang}, ${status.place}");
//                    log.debug("text: ${status.text}");
//                    log.debug("user: ${status.user.id}, ${status.user.name}, ${status.user.screenName}");
//                    log.debug("------------StatusListener-----onStatus-----End-----------");
                    statusQueue.put(status)

                }

            }),
            Executors.newFixedThreadPool(1)
        )

        twitterStatusClient.connect()
        twitterStatusClient.process()
    }

    override fun stop() {
        log.info("Stopping Twitter client")
        // Print some stats
        log.info("""Twitter Client Stats:
        numMessages: ${twitterBasicClient.statsTracker.numMessages}
        numMessagesDropped: ${twitterBasicClient.statsTracker.numMessagesDropped}
        num200s: ${twitterBasicClient.statsTracker.num200s}
        num400s: ${twitterBasicClient.statsTracker.num400s}
        num500s: ${twitterBasicClient.statsTracker.num500s}
        numClientEventsDropped: ${twitterBasicClient.statsTracker.numClientEventsDropped}
        numConnectionFailures: ${twitterBasicClient.statsTracker.numConnectionFailures}
        numConnects: ${twitterBasicClient.statsTracker.numConnects}
        numDisconnects: ${twitterBasicClient.statsTracker.numDisconnects}
                    """)
        twitterBasicClient.stop()
        rawQueue.clear();
        statusQueue.clear();
    }

    override fun poll(): List<SourceRecord> {
        if (twitterBasicClient.isDone)
            log.warn("Client connection closed unexpectedly: ", twitterBasicClient.exitEvent.message) //TODO: what next?
        val l = mutableListOf<Status>()
        statusQueue.drainWithTimeoutTo(l, batchSize, (batchTimeout * 1E9).toLong(), TimeUnit.NANOSECONDS)
        return l.map({
            when (statusConverter) {
                TwitterSourceConfig.OUTPUT_FORMAT_ENUM_STRING -> statusToStringKeyValue(it, topic)
                else -> statusToTwitterStatusStructure(it, topic)
            }
        })
    }

}


