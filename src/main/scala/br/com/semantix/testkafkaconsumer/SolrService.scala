package br.com.semantix.testkafkaconsumer

import com.typesafe.config.ConfigFactory
import org.apache.log4j.LogManager
import org.apache.spark.sql.{DataFrame, SaveMode}

object SolrService {
  val Log = LogManager.getLogger(SolrService.getClass)

  val Conf = ConfigFactory.load()

  def postDocument(df: DataFrame, collection: String) = {
    val options = Map(
      "zkhost" -> Conf.getString("conf.solr.zookeeper"),
      "collection" -> collection,
      "gen_uniq_key" -> "true" // Generate unique key if the 'id' field does not exist
    )

    df.write.format("solr").options(options).mode(SaveMode.Append).save
  }

}
