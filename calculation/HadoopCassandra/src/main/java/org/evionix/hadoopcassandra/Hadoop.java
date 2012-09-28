/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.evionix.hadoopcassandra;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/**
 *
 * @author frbl
 */
public class Hadoop extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>, Mapper<LongWritable, Text, Text, IntWritable> {

//    String input = "";
//    String output = "";

    private final static IntWritable one = new IntWritable(1);
    
    private Text word = new Text();
    
    
    public static void main(String[] args) {

        Hadoop hadoop = new Hadoop(args[0], args[1]);

    }
    
    public Hadoop() {}
    
    public Hadoop(String input, String output) {

        JobConf conf = new JobConf(Hadoop.class);
        conf.setJobName("wordcount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Hadoop.class); //map.class
        conf.setCombinerClass(Hadoop.class); //reduce.class
        conf.setReducerClass(Hadoop.class); //reduce.class

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

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        int sum = 0;
        while (values.hasNext()) {
            sum += values.next().get();
        }
        output.collect(key, new IntWritable(sum));
    }
    
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
            output.collect(word, one);
        }
    }
}
