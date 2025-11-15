package org.example;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrdersMapper extends Mapper<LongWritable, Text, Text, Text> {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final Text outKey = new Text();
    private final Text outValue = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();

        if (line == null || line.isEmpty() || line.startsWith("order_id")) {
            return;
        }

        String[] cols = line.split("\t", -1);
        if (cols.length != 7) {
            context.getCounter("ORDERS_MAPPER", "MALFORMED_LINE").increment(1);
            return;
        }

        String orderId = cols[0].trim();
        String restaurantId = cols[1].trim();
        String orderDateStr = cols[2].trim();
        String itemsCountStr = cols[3].trim();
        String totalPriceStr = cols[4].trim();
        String paymentType = cols[5].trim();
        String status = cols[6].trim();

        // Basic validation
        if (restaurantId.isEmpty() || orderId.isEmpty()) {
            context.getCounter("ORDERS_MAPPER", "MISSING_ID").increment(1);
            return;
        }

        // Parse numeric fields and date; if parse fails -> skip line
        int itemsCount;
        double totalPrice;
        LocalDateTime orderDate;
        try {
            itemsCount = Integer.parseInt(itemsCountStr);
        } catch (NumberFormatException e) {
            context.getCounter("ORDERS_MAPPER", "BAD_ITEMS_COUNT").increment(1);
            return;
        }
        try {
            totalPrice = Double.parseDouble(totalPriceStr);
        } catch (NumberFormatException e) {
            context.getCounter("ORDERS_MAPPER", "BAD_TOTAL_PRICE").increment(1);
            return;
        }
        try {
            orderDate = LocalDateTime.parse(orderDateStr, INPUT_DATE_FORMAT);
        } catch (Exception e) {
            context.getCounter("ORDERS_MAPPER", "BAD_DATE").increment(1);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(orderId).append(",");
        sb.append(orderDate.format(INPUT_DATE_FORMAT)).append(",");
        sb.append(itemsCount).append(",");
        sb.append(String.format("%.2f", totalPrice)).append(",");
        sb.append(paymentType).append(",");
        sb.append(status);

        outKey.set(restaurantId);
        outValue.set(sb.toString());
        context.write(outKey, outValue);
    }
}
