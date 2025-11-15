#!/bin/bash

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <mr_output_path> <datasource4_path> <final_output_path>"
  exit 1
fi

MR_OUTPUT=$1
DS4_PATH=$2
FINAL_OUTPUT=$3

echo "=== RUN HIVE JOB ==="
echo "MR output: $MR_OUTPUT"
echo "Datasource4: $DS4_PATH"
echo "Final output: $FINAL_OUTPUT"

hdfs dfs -rm -r -f $FINAL_OUTPUT

beeline -u "jdbc:hive2://localhost:10000/default" -n hadoop -f process_hive.hql \
  --hivevar mr_output_path=$MR_OUTPUT \
  --hivevar ds4_path=$DS4_PATH \
  --hivevar final_output_path=$FINAL_OUTPUT
