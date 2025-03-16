#!/bin/bash

# ------------------------------------------
# CheetahCutOrStitch (CROSS) - Launcher Script
# ------------------------------------------

# Java path
JAVA_EXEC="/opt/java21/bin/java"

# Path to the JAR
JAR_PATH="target/ccos-1.0.0-SNAPSHOT.jar"

# Check if at least operation, input, and output are provided
if [ "$#" -lt 3 ]; then
    echo "❌ Missing parameters!"
    echo "Usage for CUT:    $0 cut /path/to/input /path/to/output 50MB"
    echo "Usage for STITCH: $0 stitch /path/to/input /path/to/output"
    exit 1
fi

# Parse parameters
OPERATION=$1
INPUT=$2
OUTPUT=$3
SIZE=$4

# Execute the JAR based on the operation type
if [ "$OPERATION" == "cut" ]; then
    if [ -z "$SIZE" ]; then
        echo "❌ Size parameter is required for 'cut' operation."
        exit 1
    fi
    $JAVA_EXEC -jar $JAR_PATH --operation=$OPERATION --input="$INPUT" --size=$SIZE --output="$OUTPUT"
elif [ "$OPERATION" == "stitch" ]; then
    $JAVA_EXEC -jar $JAR_PATH --operation=$OPERATION --input="$INPUT" --output="$OUTPUT"
else
    echo "❌ Invalid operation. Use 'cut' or 'stitch'."
    exit 1
fi

# Capture exit status
status=$?

if [ $status -eq 0 ]; then
    echo "✅ Operation '$OPERATION' completed successfully!"
else
    echo "❌ Operation '$OPERATION' failed with status code: $status"
fi
