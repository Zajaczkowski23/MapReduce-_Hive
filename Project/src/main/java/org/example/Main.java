package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: OrdersDriver <input_dir> <output_dir>");
            System.exit(-1);
        }

        String inputDir = args[0];
        String outputDir = args[1];

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "FoodOrders - Preprocess Orders");
        job.setJarByClass(Main.class);

        job.setMapperClass(OrdersMapper.class);

        job.setCombinerClass(OrdersCombiner.class);

        job.setReducerClass(OrdersReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.addInputPath(job, new Path(inputDir));
        TextOutputFormat.setOutputPath(job, new Path(outputDir));

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}