package br.com.semantix.testkafkaconsumer

import com.lucidworks.spark.util.{SolrQuerySupport, SolrSupport}
import com.typesafe.config.ConfigFactory
import org.apache.log4j.LogManager
import org.apache.spark.sql.{DataFrame, SQLContext}

object SolrService {
  private val Log = LogManager.getLogger(SolrService.getClass)

  private val Conf = ConfigFactory.load()

  val zkHost = Conf.getString("conf.solr.zookeeper")

  val solrCloudClient = SolrSupport.getCachedCloudClient(zkHost)

  /**
    * Insert dataframe in the given collection on Solr
    *
    * @param df
    * @param collection
    *
    * @return update response
    */
  def postDocument(df: DataFrame, collection: String) = {
    val options = Map(
      "zkhost" -> Conf.getString("conf.solr.zookeeper"),
      "collection" -> collection,
      "gen_uniq_key" -> "true" // Generate unique key if the 'id' field does not exist
    )

    df.write.format("solr").options(options).save
    solrCloudClient.commit(collection)
  }

  /**
    * Get all documents in the given collection that match the query
    *
    * @param spark
    * @param collection
    * @param query
    *
    * @return documents
    */
  def getDocuments(spark: SQLContext, collection: String, query: String) = {
    val options = Map(
      "zkHost" -> Conf.getString("conf.solr.zookeeper"),
      "collection" -> collection
    )

    val solrQuery = SolrQuerySupport.toQuery(query)
    solrCloudClient.query(collection, solrQuery).getResults
  }

}
