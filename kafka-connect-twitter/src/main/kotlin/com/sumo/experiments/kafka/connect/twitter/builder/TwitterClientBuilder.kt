package com.sumo.experiments.kafka.connect.twitter.builder

import com.sumo.experiments.kafka.connect.twitter.batch
import com.sumo.experiments.kafka.connect.twitter.config.TwitterSourceConfig
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.Location
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.OAuth1
import org.apache.kafka.connect.source.SourceTaskContext
import org.slf4j.LoggerFactory.getLogger
import java.util.concurrent.BlockingQueue

object TwitterClientBuilder {

    private val log = getLogger(TwitterClientBuilder::class.java)

    fun getTwitterClient(config: TwitterSourceConfig, context: SourceTaskContext, rawQueue: BlockingQueue<String>) : BasicClient {

        log.debug("offset: "+context.offsetStorageReader().offset(mapOf("lang" to "en")))

        //endpoints //DefaultStreamingEndpoint
        val endpoint = if (config.getString(TwitterSourceConfig.STREAM_TYPE) == TwitterSourceConfig.STREAM_TYPE_SAMPLE) {
            StatusesSampleEndpoint()
        } else {
            val trackEndpoint = StatusesFilterEndpoint()
            val terms = config.getList(TwitterSourceConfig.TRACK_TERMS)
            if (!terms.isEmpty()) {
                trackEndpoint.trackTerms(terms)
            }
            val locs = config.getList(TwitterSourceConfig.TRACK_LOCATIONS)
            if (!locs.isEmpty()) {
                val locations = locs.map({ x -> x.toDouble() }).batch(4)
                    .map({ l -> Location(Location.Coordinate(l[0], l[1]), Location.Coordinate(l[2], l[3])) })
                trackEndpoint.locations(locations)
            }
            val follow = config.getList(TwitterSourceConfig.TRACK_FOLLOW)
            if (!follow.isEmpty()) {
                val users = follow.map({ x -> x.trim().toLong() })
                trackEndpoint.followings(users)
            }
            trackEndpoint
        }

        endpoint.stallWarnings(false)
        val language = config.getList(TwitterSourceConfig.LANGUAGE)
        if (!language.isEmpty()) {
            // endpoint.languages(language) doesn't work as intended!
            endpoint.addQueryParameter(TwitterSourceConfig.LANGUAGE, language.joinToString(","))
        }

        //twitter auth stuff
        val auth = OAuth1(config.getString(TwitterSourceConfig.Companion.CONSUMER_KEY_CONFIG),
            config.getPassword(TwitterSourceConfig.Companion.CONSUMER_SECRET_CONFIG).value(),
            config.getString(TwitterSourceConfig.Companion.TOKEN_CONFIG),
            config.getPassword(TwitterSourceConfig.Companion.SECRET_CONFIG).value())

        //build basic client
        return ClientBuilder()
            .name(config.getString(TwitterSourceConfig.TWITTER_APP_NAME))
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(StringDelimitedProcessor(rawQueue))
            .build()
    }
}
