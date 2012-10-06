/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.calculationservice.hadoop.mapreduce.AverageMapper;
import nl.rug.calculationservice.hadoop.mapreduce.AverageReducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/**
 *
 * @author frbl
 */
public class Hadoop implements IHadoop{

    String input = "";

    String output = "";
    
    public static void main(String[] args) {

        Hadoop hadoopApp = new Hadoop(args[0], args[1]);

    }
    
    // Empty constructor is needed for hadoop/jar
    public Hadoop() {}
    
    public Hadoop(String input, String output) {

        this.input = input;
        
        this.output = output;

    }

    @Override
    public void performCalculation() {
        
        JobConf conf = new JobConf(Hadoop.class);
        conf.setJobName("wordcount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(AverageMapper.class); //map.class
        conf.setCombinerClass(AverageReducer.class); //reduce.class
        conf.setReducerClass(AverageReducer.class); //reduce.class

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));
        
        try {
            
            JobClient.runJob(conf);
            
        } catch (IOException ex) {
            
            Logger.getLogger(Hadoop.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
    }
    
}
