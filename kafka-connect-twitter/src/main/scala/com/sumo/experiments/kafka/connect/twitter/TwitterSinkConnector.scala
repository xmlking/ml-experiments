package com.sumo.experiments.kafka.connect.twitter

import java.util

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.utils.AppInfoParser
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.sink.SinkConnector

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

class TwitterSinkConnector extends SinkConnector with Logging {
  private var configProps : util.Map[String, String] = null

  /**
    * States which SinkTask class to use
    * */
  override def taskClass(): Class[_ <: Task] = classOf[TwitterSinkTask]

  /**
    * Set the configuration for each work and determine the split
    *
    * @param maxTasks The max number of task workers be can spawn
    * @return a List of configuration properties per worker
    * */
  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    log.info(s"Setting task configurations for $maxTasks workers.")
    (1 to maxTasks).map(c => configProps).toList.asJava
  }

  /**
    * Start the sink and set to configuration
    *
    * @param props A map of properties for the connector and worker
    * */
  override def start(props: util.Map[String, String]): Unit = {
    log.info(s"Starting Twitter sink task with ${props.toString}.")
    configProps = props
    Try(new TwitterSinkConfig(props)) match {
      case Failure(f) => throw new ConnectException("Couldn't start TwitterSinkConnector due to configuration error.", f)
      case _ =>
    }
  }

  override def stop(): Unit = {}

    override def version(): String =  {
        AppInfoParser.getVersion()
    }

    // dont need till 0.10.1.0
    override def config(): ConfigDef = {
        return new ConfigDef()
            .define("twitter.consumerkey", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter consumer key")
            .define("twitter.consumersecret", ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Twitter consumer secret")
            .define("twitter.token", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter token")
            .define("twitter.secret", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter secret")
    }

}
