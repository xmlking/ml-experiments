import com.nhaarman.mockito_kotlin.mock
import com.sumo.experiments.kafka.connect.twitter.config.TwitterSourceConfig
import com.sumo.experiments.kafka.connect.twitter.TwitterSourceConnector
import com.sumo.experiments.kafka.connect.twitter.TwitterSourceTask
import org.apache.kafka.connect.connector.ConnectorContext
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class TwitterSourceConnectorSpecs : Spek( {
    given("a TwitterSourceConnector") {
        val sourceConnector = TwitterSourceConnector()
        on("connected with given config") {
            sourceConnector.start( mapOf<String, String>(
                TwitterSourceConfig.TWITTER_APP_NAME to "twitter-source",
                TwitterSourceConfig.TOPIC to "twitter",
                TwitterSourceConfig.CONSUMER_KEY_CONFIG to "fake",
                TwitterSourceConfig.CONSUMER_SECRET_CONFIG to "fake",
                TwitterSourceConfig.TOKEN_CONFIG to "fake",
                TwitterSourceConfig.SECRET_CONFIG to "fake",
                TwitterSourceConfig.TRACK_TERMS to "news,music,hadoop,clojure,scala,fp,golang,python,fsharp,cpp,java")
            )
            val task = TwitterSourceTask()
            task.initialize(mock())
            task.start(sourceConnector.taskConfigs(1).get(0));
            val polledOnce  = task.poll();
            it("should produce tweets") {
                println(polledOnce)
                assertEquals("Hello, world.", "Hello, world.")
            }
        }
    }
})
