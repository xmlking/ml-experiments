package com.sumo.experiments.kafka.connect.twitter

import java.util

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.utils.AppInfoParser
import org.apache.kafka.connect.connector.{Connector, Task}
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceConnector

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}


class TwitterSourceConnector extends SourceConnector with Logging {
  private var configProps : util.Map[String, String] = null

  override def taskClass(): Class[_ <: Task] = classOf[TwitterSourceTask]

  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    log.info(s"Setting task configurations for $maxTasks workers.")
    (1 to maxTasks).map(c => configProps).toList.asJava
  }

  override def start(props: util.Map[String, String]): Unit = {
    log.info(s"Starting Twitter source task with ${props.toString}.")
    configProps = props
    Try(new TwitterSourceConfig(props)) match {
      case Failure(f) => throw new ConnectException("Couldn't start Twitter source due to configuration error: "
          + f.getMessage, f)
      case _ =>
    }
  }

  override def stop() = {}
  override def version(): String =  {
      AppInfoParser.getVersion()
  }


    override def config(): ConfigDef = {
        return new ConfigDef()
            .define("twitter.consumerkey", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter consumer key")
            .define("twitter.consumersecret", ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Twitter consumer secret")
            .define("twitter.token", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter token")
            .define("twitter.secret", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Twitter secret")
            .define("track.terms", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "coma-separated list of Twitter track terms")
    }
}
