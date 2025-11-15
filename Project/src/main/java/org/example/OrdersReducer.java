package org.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class OrdersReducer extends Reducer<Text, Text, Text, Text> {

    private final Text outValue = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Stream processing: emit one output line per input value for this key
        for (Text val : values) {
            // val is: order_id,order_date,items_count,total_price_usd,payment_type,status
            outValue.set(val);
            context.write(key, outValue);
        }
    }
}
