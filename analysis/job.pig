-- Load all of the data from the Cassandra database
rows = LOAD 'cassandra://PIGI/Repository' USING CassandraStorage();
-- Flatten the values so that the column information can be accessed in the statements
line = FOREACH rows GENERATE key, flatten(columns);
-- Select the fields that we need
items = FOREACH line GENERATE key, (long)name.$0, (int)value;
-- Group the items based on repository ($0) and revision ($1)
grouped = group items by ($0, $1);
-- Calculate the average complexity for each repository/revision pair
values = FOREACH grouped GENERATE group.key as key, (long)$0.$1 as name, (double)SUM($1.$2)/COUNT($1.$2) as value;
-- Change the information to a format that can be stored into the Cassandra database
tostore = foreach values generate key, TOTUPLE(name, value);
-- Store the values into the Cassandra database
STORE tostore INTO 'cassandra://PIGI/AverageComplexities' USING CassandraStorage();
