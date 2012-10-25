package nl.rug

import me.prettyprint.cassandra.service.StringKeyIterator;
import me.prettyprint.cassandra.service.template._
import me.prettyprint.cassandra.serializers._

import me.prettyprint.hector.api._
import me.prettyprint.hector.api.factory.HFactory

import scala.collection.JavaConverters._

class AverageComplexities(clusterName: String, clusterAddress: String) {

  private val columnFamily = "AverageComplexities"

  private val cluster = HFactory.getOrCreateCluster(clusterName, clusterAddress)

  private val keyspace = HFactory.createKeyspace(columnFamily, cluster)

  // [String, Long] corresponds with key and column name
  private val stringSerializer: Serializer[String] = StringSerializer.get()
  // For some reason this one needs an explicit cast, weird
  private val longSerializer: Serializer[Long] = LongSerializer.get().asInstanceOf[Serializer[Long]]
  private val template = new ThriftColumnFamilyTemplate[String, Long](keyspace, columnFamily, stringSerializer, longSerializer)

  def getRepositories: Iterable[String] = {

    val javaIterator = new StringKeyIterator(keyspace, columnFamily)

    javaIterator.asScala

  }

  def getAverageComplexities(repository: String) {

    // TODO

  }

}