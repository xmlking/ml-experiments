package com.sumo.experiments.kafka.connect.twitter

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.utils.AppInfoParser
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceConnector
import org.slf4j.LoggerFactory
import java.util.*


class TwitterSourceConnector : SourceConnector() {

    private lateinit var configProps : Map<String, String>

    companion object {
        private val log = LoggerFactory.getLogger(TwitterSourceConnector::class.java)
    }

    override fun version(): String = AppInfoParser.getVersion()

    override fun taskClass(): Class<out Task> = TwitterSourceTask::class.java

    override fun config(): ConfigDef {
        return TwitterSourceConfig.config
    }

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> {
        log.info("Setting task configurations for $maxTasks workers.")
        return Array<Map<String, String>>(maxTasks, {configProps}).asList()
        //return (1..maxTasks).map {configProps}
    }

    override fun start(props: Map<String, String>) {
        log.info("Starting Twitter source task with $props.")
        configProps = props
        try {
            TwitterSourceConfig(props)
        } catch(e: Exception) {
            throw ConnectException("Couldn't start Twitter source due to configuration error: ${e.message}", e)
        }
    }

    override fun stop() {

    }


}
