package nl.rug {
	package database {
		
		import me.prettyprint.cassandra.service.StringKeyIterator
		import me.prettyprint.cassandra.service.ColumnSliceIterator
		import me.prettyprint.cassandra.service.template._
		import me.prettyprint.cassandra.serializers._

		import me.prettyprint.hector.api._
		import me.prettyprint.hector.api.beans.HColumn
		import me.prettyprint.hector.api.factory.HFactory
		
		class Cassandra(clusterName: String, clusterAddress: String) {

		  // get the serializers we need, putting these in all of the calls would make them too long
		  private val stringSerializer: Serializer[String] = StringSerializer.get()

		  // For some reason these need an explicit cast, weird
		  private val longSerializer: Serializer[Long] = LongSerializer.get().asInstanceOf[Serializer[Long]]
		  
			private val doubleSerializer: Serializer[Double] = DoubleSerializer.get().asInstanceOf[Serializer[Double]]

		  private val keyspace_name = "Pigi"

		  private val cluster = HFactory.getOrCreateCluster(clusterName, clusterAddress)

		  private val _keyspace = HFactory.createKeyspace(keyspace_name, cluster)
			def keyspace = _keyspace
			
		}
		
		
	}
	
}