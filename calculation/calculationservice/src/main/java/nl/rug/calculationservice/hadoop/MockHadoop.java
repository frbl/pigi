/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop;

import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import nl.rug.calculationservice.database.cassandra.Cassandra;

/**
 *
 * @author frbl
 */
public class MockHadoop implements IHadoop {
    Cassandra cassandra;
    /**
     * Get data from Cassandra
     * Do something with the data
     * Push it to postgres.
     */
    public MockHadoop() {
    
        cassandra = new Cassandra();
        
    }

    @Override
    public void performCalculation() {
        
        cassandra.getTemplate();
        
        ColumnFamilyResult<String, String> res;// = template.queryColumns("1");
        String value;
        
        res = cassandra.getTemplate().queryColumns("3");
        
        value = res.getString("name");
        
        System.out.println("Value: " + value);
        
    }
    
    
}
