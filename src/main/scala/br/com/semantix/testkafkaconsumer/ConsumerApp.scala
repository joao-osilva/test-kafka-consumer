package br.com.semantix.testkafkaconsumer

import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import org.apache.log4j.LogManager
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ConsumerApp extends App {
  Runner.run(new SparkConf())
}

object Runner {
  val Log = LogManager.getLogger(Runner.getClass)

  val Conf = ConfigFactory.load()

  def run(sparkConf: SparkConf) = {
    Log.info(s"Loading configuration at ${printConfig(Conf)(0)} =>\n${printConfig(Conf)(1)}")

    val streamingInterval = Conf.getLong("conf.spark.streaming.interval")
    val streamCtx = new StreamingContext(sparkConf, Seconds(streamingInterval))

    val topics = Set(Conf.getString("conf.kafka.topic"))
    val stream = KafkaService.getStream(streamCtx, topics)

    stream.map(element => element._2)
      .foreachRDD(rdd => rdd.collect()
        .foreach(record =>
          try {
            Log.info(s"[RECORD]-> ${record}")
            println(s"[RECORD]-> ${record}")

          } catch {
            case ex: Exception =>
              Log.error("[ERROR] " + record)
              Log.error(s"An error occurred while processing a record", ex)
          }))

    streamCtx.start()
    streamCtx.awaitTermination()
  }

  def printConfig(conf: Config) = {
    List(conf.getConfig("conf").origin().filename(),
         conf.getConfig("conf").root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(true)))
  }
}