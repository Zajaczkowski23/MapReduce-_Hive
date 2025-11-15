#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <input_dir1> <output_dir3>"
    exit 1
fi

INPUT_DIR=$1
OUTPUT_DIR=$2

JAR_PATH="/home/hadoop/Project-1.0-SNAPSHOT.jar"

MAIN_CLASS="org.example.Main"

echo "=== RUN MAPREDUCE JOB ==="
echo "Input:  $INPUT_DIR"
echo "Output: $OUTPUT_DIR"

hdfs dfs -rm -r -skipTrash $OUTPUT_DIR 2>/dev/null

hadoop jar $JAR_PATH $MAIN_CLASS $INPUT_DIR $OUTPUT_DIR

if [ $? -eq 0 ]; then
    echo "MapReduce finished successfully!"
else
    echo "MapReduce failed."
    exit 1
fi
