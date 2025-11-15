DROP TABLE IF EXISTS orders_mr;
DROP TABLE IF EXISTS datasource4;
DROP TABLE IF EXISTS final_result;

CREATE EXTERNAL TABLE orders_mr (
  order_id STRING,
  customer_id STRING,
  product_id STRING,
  amount DOUBLE,
  order_date STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '${mr_output_path}';

CREATE EXTERNAL TABLE datasource4 (
  product_id STRING,
  product_name STRING,
  category STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '${ds4_path}';

CREATE TABLE final_result (
    json_line STRING
)
STORED AS TEXTFILE
LOCATION '${final_output_path}';


INSERT INTO TABLE final_result
SELECT
    CONCAT(
        '{',
        '"order_id":"', o.order_id, '",',
        '"customer_id":"', o.customer_id, '",',
        '"amount":', o.amount, ',',
        '"order_date":"', o.order_date, '",',
        '"product_name":"', d.product_name, '",',
        '"category":"', d.category, '"',
        '}'
    ) AS json_line
FROM orders_mr o
JOIN datasource4 d
ON o.product_id = d.product_id;
