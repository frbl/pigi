package nl.rug {
	package model {
		import me.prettyprint.cassandra.service.StringKeyIterator
		import me.prettyprint.cassandra.service.ColumnSliceIterator
		import me.prettyprint.cassandra.service.template._
		import me.prettyprint.cassandra.serializers._

		import nl.rug.database._

		import me.prettyprint.hector.api._
		import me.prettyprint.hector.api.beans.HColumn
		import me.prettyprint.hector.api.factory.HFactory

		import scala.collection.JavaConverters._

		class AverageComplexities {
	
		 // get the serializers we need, putting these in all of the calls would make them too long
		  private val stringSerializer: Serializer[String] = StringSerializer.get()

		  // For some reason these need an explicit cast, weird
		  private val longSerializer: Serializer[Long] = LongSerializer.get().asInstanceOf[Serializer[Long]]
		  private val doubleSerializer: Serializer[Double] = DoubleSerializer.get().asInstanceOf[Serializer[Double]]

			// [String, Long] corresponds with key and column name
	  

		  private val cassandra = new Cassandra("Pigi Cluster","127.0.0.1:9160")

		  def getRepositories: List[RepositoryCassandra] = {
		
				val columnFamily :String = "Repositories"
		
		    val javaIterable = new StringKeyIterator(cassandra.keyspace, columnFamily)

		    val rep: Iterator[String] = javaIterable.asScala.iterator
		
				var repositories :List[RepositoryCassandra] = List[RepositoryCassandra]()
	
				while(rep.hasNext) repositories ::= findByUrl(rep.next)
		
				return repositories
		
		  }

  		def findByUrl(url :String): RepositoryCassandra = {
		
				val columnFamily :String = "Repositories"
		
				val template = new ThriftColumnFamilyTemplate[String, String](cassandra.keyspace, columnFamily, stringSerializer, stringSerializer)
		
		    var repository = new RepositoryCassandra

		    val result = template.queryColumns(url)

		    repository.url = result.getKey()
		    repository.name = result.getString("name")
		    repository.description = result.getString("description")
				return repository
      
  		}

  		def getAverageComplexities(repository: String, from: Long, to: Long): Iterator[HColumn[Long, Long]] = {
		
				val columnFamily :String = "AverageComplexities"

		    val query = HFactory.createSliceQuery(cassandra.keyspace, stringSerializer, longSerializer, longSerializer)
		    query.setKey(repository).setColumnFamily(columnFamily)


    		val javaIterator = new ColumnSliceIterator[String, Long, Long](query, from, to, false)
    		// if null problem do:
    		//javaIterator.next

    		javaIterator.asInstanceOf[java.util.Iterator[HColumn[Long, Long]]].asScala

  		}
		}
	}
}