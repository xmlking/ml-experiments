package com.sumo.experiments.kafka.connect.twitter

import java.util
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}


class TwitterSourceTask extends SourceTask with Logging {
  private var reader : Option[TwitterStatusReader] = null

  override def poll(): util.List[SourceRecord] = {
    require(reader.isDefined, "Twitter client not initialized!")
    reader.get.poll()
  }

  override def start(props: util.Map[String, String]): Unit = {
    val sourceConfig = new TwitterSourceConfig(props)
    reader = Some(TwitterReader(config = sourceConfig, context = context))
  }

  override def stop() = {
    reader.foreach(r=>r.stop())
  }
  override def version(): String = ""
}
