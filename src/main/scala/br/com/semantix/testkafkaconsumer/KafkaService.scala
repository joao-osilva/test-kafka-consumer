package br.com.semantix.testkafkaconsumer

import com.typesafe.config.ConfigFactory
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import java.util.HashMap

import org.apache.log4j.LogManager

object KafkaService {
  val Log = LogManager.getLogger(KafkaService.getClass)

  val Conf = ConfigFactory.load()

  val consumerParams = Map[String, String](ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> Conf.getString("conf.kafka.brokers"),
                                           ConsumerConfig.GROUP_ID_CONFIG -> Conf.getString("conf.kafka.consumer.group"),
                                           ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> Conf.getString("conf.kafka.consumer.offset"))/*,
                                           "security.protocol" -> Conf.getString("conf.kafka.security_protocol"))*/

  val producerParams = new HashMap[String, Object]
  producerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Conf.getString("conf.kafka.brokers"))
  producerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  producerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  //producerParams.put("security.protocol", Conf.getString("conf.kafka.security_protocol"))

  val producer = new KafkaProducer[String, String](producerParams)

  def getStream(streamCtx: StreamingContext, topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](streamCtx, consumerParams, topics)
  }

  def sendToTopic(topic: String, value: String) = {
    val record = new ProducerRecord[String, String](topic, value)
    Log.info(s"[OUTPUT RECORD]-> ${record}")
    println(s"[OUTPUT RECORD]-> ${record}")
    val result = producer.send(record)
    Log.info(s"[RESULT]-> ${result}")
    println(s"[RESULT]-> ${result}")
  }
}
