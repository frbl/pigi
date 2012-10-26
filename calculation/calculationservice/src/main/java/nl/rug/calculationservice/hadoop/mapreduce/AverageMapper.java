/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop.mapreduce;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.StringTokenizer;
import org.apache.cassandra.db.IColumn;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author frbl
 */
public class AverageMapper extends Mapper<ByteBuffer, SortedMap<ByteBuffer, IColumn>, Text, IntWritable> {
    
    private static final Logger logger = LoggerFactory.getLogger(Mapper.class);
    
    private Text word = new Text();
    
    private final static IntWritable one = new IntWritable(1);
    
    public void map(ByteBuffer key, SortedMap<ByteBuffer, IColumn> columns, Context context) throws IOException, InterruptedException
        {
            for (IColumn column : columns.values())
            {
                String keyString = ByteBufferUtil.string(key);
                String name  = ByteBufferUtil.string(column.name());
                int value = ByteBufferUtil.toInt(column.value());
                //int value = ByteBufferUtil.toInt(column.value());
                
                System.out.println("Key: " + keyString +", Name: " + name + ", Value: " + value);
                

//                StringTokenizer itr = new StringTokenizer(value);
//                while (itr.hasMoreTokens())
//                {
                    word.set("total");
                    context.write(word, new IntWritable(value));
                    context.write(new Text("count"), one);
//                }
            }
            
            //reporter.incrCounter("counter", "counter", 1);
            System.out.println(word);
            
        }
    
}
