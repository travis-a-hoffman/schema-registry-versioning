#!/usr/bin/env bash

CLASS=io.firkin.schemaregistry.versioning.simple.protobuf.evolution.
if [[ $1 == "-produce" ]]; then
  CLASS+=PersonProducerTest
  shift
elif [ $1 == "-consume" ]; then
  CLASS+=PersonConsumerTest
  shift
else
  echo "Please specify either -produce or -consume"
  exit 1;
fi

source .firkin_configs
# .firkin_configs needs to contain the following:
# export FIRKIN_KAFKA_BOOTSTRAP_URL=pkc-*****.us-east4.gcp.confluent.cloud:9092
# export FIRKIN_KAFKA_BOOTSTRAP_USERNAME=UTAB7**********
# export FIRKIN_KAFKA_BOOTSTRAP_PASSWORD=AUoVR***********************************************************
# export FIRKIN_SCHEMA_REGISTRY_URL=https://psrc-*****.us-central1.gcp.confluent.cloud
# export FIRKIN_SCHEMA_REGISTRY_USERNAME=NH73G***********
# export FIRKIN_SCHEMA_REGISTRY_PASSWORD=PspMy***********************************************************

FIRKIN_CLASSPATH=target/classes
FIRKIN_CLASSPATH+=:~/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar

AK=3.0.0
FIRKIN_CLASSPATH+=:~/.m2/repository/org/apache/kafka/kafka-clients/$AK/kafka-clients-$AK.jar

CFLT=7.2.1
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/common-utils/$CFLT/common-utils-$CFLT.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/kafka-protobuf-provider/$CFLT/kafka-protobuf-provider-$CFLT.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/kafka-protobuf-serializer/$CFLT/kafka-protobuf-serializer-$CFLT.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/kafka-protobuf-types/$CFLT/kafka-protobuf-types-$CFLT.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/kafka-schema-registry-client/$CFLT/kafka-schema-registry-client-$CFLT.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/io/confluent/kafka-schema-serializer/$CFLT/kafka-schema-serializer-$CFLT.jar

FIRKIN_CLASSPATH+=:~/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.13.2/jackson-annotations-2.13.2.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.12.5/jackson-core-2.12.5.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.13.2.2/jackson-databind-2.13.2.2.jar

# COMPRESSION
FIRKIN_CLASSPATH+=:~/.m2/repository/com/github/luben/zstd-jni/1.5.0-2/zstd-jni-1.5.0-2.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/org/lz4/lz4-java/1.7.1/lz4-java-1.7.1.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/org/xerial/snappy/snappy-java/1.1.8.1/snappy-java-1.1.8.1.jar

FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/guava/guava/30.1.1-jre/guava-30.1.1-jre.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/api/grpc/proto-google-common-protos/2.5.1/proto-google-common-protos-2.5.1.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/api/grpc/proto-google-common-protos/2.5.1/proto-google-common-protos-2.5.1.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/protobuf/protobuf-java/3.21.1/protobuf-java-3.21.1.jar
FIRKIN_CLASSPATH+=:~/.m2/repository/com/google/protobuf/protobuf-java-util/3.19.4/protobuf-java-3.19.4.jar

#echo "  FIRKIN_CLASSPATH=$FIRKIN_CLASSPATH"
java -cp $FIRKIN_CLASSPATH $CLASS -n 10
