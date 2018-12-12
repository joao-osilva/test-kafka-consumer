package br.com.semantix.testkafkaconsumer

import com.typesafe.config.ConfigFactory
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils

object KafkaService {

  val Conf = ConfigFactory.load()

  val Params = Map[String, String](
    "bootstrap.servers" -> Conf.getString("conf.kafka.brokers"),
    "group.id" -> Conf.getString("conf.kafka.group"),
    "auto.offset.reset" -> Conf.getString("conf.kafka.offset"),
    "security.protocol" -> Conf.getString("conf.kafka.security_protocol")
  )

  def getStream(streamCtx: StreamingContext, topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](streamCtx, Params, topics)
  }

}
