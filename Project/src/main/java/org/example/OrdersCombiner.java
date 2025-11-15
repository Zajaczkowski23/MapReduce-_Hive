package org.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class OrdersCombiner extends Reducer<Text, Text, Text, Text> {

    private final Text outValue = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        int totalItems = 0;
        double totalPrice = 0.0;
        int count = 0;

        for (Text val : values) {
            String line = val.toString();

            if (line.startsWith("SUM:")) {
                String[] parts = line.substring(4).split(",");
                totalItems += Integer.parseInt(parts[0].split("=")[1]);
                totalPrice += Double.parseDouble(parts[1].split("=")[1]);
                count += Integer.parseInt(parts[2].split("=")[1]);
            } else {
                String[] cols = line.split(",");
                totalItems += Integer.parseInt(cols[2]);
                totalPrice += Double.parseDouble(cols[3]);
                count += 1;
            }
        }

        String output = String.format("SUM:items=%d,price=%.2f,count=%d",
                totalItems, totalPrice, count);

        outValue.set(output);
        context.write(key, outValue);
    }
}
