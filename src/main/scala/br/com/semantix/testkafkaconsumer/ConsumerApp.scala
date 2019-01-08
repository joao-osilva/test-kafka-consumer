package br.com.semantix.testkafkaconsumer

import java.util.Calendar
import java.util.Formatter.DateTime

import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SQLContext
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{compact, render}


object ConsumerApp extends App {
  val conf = new SparkConf().setAppName("test").setMaster("local[*]")
  Runner.run(conf)
}

object Runner {
  val Log = LogManager.getLogger(Runner.getClass)

  val Conf = ConfigFactory.load()

  def run(sparkConf: SparkConf) = {
    Log.info(s"Loading configuration at ${printConfig(Conf)(0)} =>\n${printConfig(Conf)(1)}")

    val streamingInterval = Conf.getLong("conf.spark.streaming.interval")
    val streamCtx = new StreamingContext(sparkConf, Seconds(streamingInterval))

    val sqlCtx = new SQLContext(streamCtx.sparkContext)

    val consumeTopics = Conf.getString("conf.kafka.consumer.topic").split(";").toSet
    val stream = KafkaService.getStream(streamCtx, consumeTopics)

    val produce1Topic = Conf.getString("conf.kafka.producer_1.topic")

    // fill topic with msgs
    /*var counter: Long = 0
    for (x <- 0 to 100) {
      var record = ("id" -> counter) ~
        ("timestamp" -> s"${Calendar.getInstance().getTimeInMillis}")

      val x = compact(render(record))
      counter += 1
      KafkaService.sendToTopic(produce1Topic, x)
    }*/

    // send to kafka
    /*stream.map(element => element._2)
      .foreachRDD(rdd => rdd.collect()
        .foreach(record =>
          try {
            Log.info(s"[INPUT RECORD]-> ${record}")
            println(s"[INPUT RECORD]-> ${record}")

            //KafkaService.sendToTopic(produce1Topic, record)
          } catch {
            case ex: Exception =>
              Log.error("[ERROR] " + record)
              Log.error(s"An error occurred while processing a record", ex)
          }))*/


    // read from socket and send to solr
    /*val stm = streamCtx.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    stm.foreachRDD(rdd =>
        try {
          val df = sqlCtx.read.json(rdd)

          SolrService.postDocument(df, Conf.getString("conf.solr.collection"))
        } catch {
          case ex: Exception =>
            Log.error("[ERROR] " + rdd)
            Log.error(s"An error occurred while processing a record", ex)
        }
    )*/

    streamCtx.start()
    streamCtx.awaitTermination()
  }

  def printConfig(conf: Config) = {
    List(conf.getConfig("conf").origin().filename(),
         conf.getConfig("conf").root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(true)))
  }
}