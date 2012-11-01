package nl.rug
package model

import me.prettyprint.cassandra.service.StringKeyIterator
import me.prettyprint.cassandra.service.ColumnSliceIterator
import me.prettyprint.cassandra.service.template._
import me.prettyprint.cassandra.serializers._

import me.prettyprint.hector.api._
import me.prettyprint.hector.api.beans.HColumn
import me.prettyprint.hector.api.factory.HFactory

import scala.collection.JavaConverters._

class AverageComplexities(clusterName: String, clusterAddress: String) {

  // get the serializers we need, putting these in all of the calls would make them too long
  private val stringSerializer: Serializer[String] = StringSerializer.get()

  // For some reason these need an explicit cast, weird
  private val longSerializer: Serializer[Long] = LongSerializer.get().asInstanceOf[Serializer[Long]]
  private val doubleSerializer: Serializer[Double] = DoubleSerializer.get().asInstanceOf[Serializer[Double]]

  private val columnFamily = "Repositories"

  private val cluster = HFactory.getOrCreateCluster(clusterName, clusterAddress)

  private val keyspace = HFactory.createKeyspace("ComplexityAnalysis", cluster)

  // [String, Long] corresponds with key and column name
  private val template = new ThriftColumnFamilyTemplate[String, String](keyspace, columnFamily, stringSerializer, stringSerializer)

  def getRepositories: List[RepositoryCassandra] = {

    val javaIterable = new StringKeyIterator(keyspace, columnFamily)

    val rep: Iterator[String] = javaIterable.asScala.iterator
		
		var repositories :List[RepositoryCassandra] = List[RepositoryCassandra]()
	
		while(rep.hasNext) repositories ::= findByUrl(rep.next)
		
		return repositories
		
  }

  def findByUrl(url :String): RepositoryCassandra = {

      var repository = new RepositoryCassandra

      val result = template.queryColumns(url)

      repository.url = result.getKey()
      repository.name = result.getString("name")
      repository.description = result.getString("description")

			return repository
      
  }

  def getAverageComplexities(repository: String, from: Long, to: Long): Iterator[HColumn[Long, Double]] = {

    val query = HFactory.createSliceQuery(keyspace, stringSerializer, longSerializer, doubleSerializer)
    query.setKey(repository).setColumnFamily(columnFamily)

    val javaIterator = new ColumnSliceIterator[String, Long, Double](query, from, to, false)
    // if null problem do:
    //javaIterator.next

    javaIterator.asInstanceOf[java.util.Iterator[HColumn[Long, Double]]].asScala

  }

}