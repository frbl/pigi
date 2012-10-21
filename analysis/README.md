Information needed to perform the analysis of the code complexity metrics. 

The Cassandra.txt file shows what our Cassandra datamodel looks like. The commands used to create the keyspace and column families are listed here. It also shows some example queries to show how the datamodel works.

The job.pig is used to analyze average code complexity per revision of a software repository. This script assumes that the Cassandra database has the correct column families described in the Cassandra.txt file.
