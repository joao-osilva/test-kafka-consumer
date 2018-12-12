#!/bin/bash

# Configuracoes de ambiente
APP_NAME="NRTNXTC_AGREGADOR... Joao 10"


JAR_NAME=test-kafka-consumer-assembly-1.0.11.jar


KEYTAB=/data/semantix/enriquecer_categorias/categorizador_dev/dusrbgdt.krb5.keytab
KEY_CONF=/data/semantix/enriquecer_categorias/categorizador_dev/key.conf
MASTER=yarn-cluster
QUEUE=high
LOG_PROPERTIES=/data/semantix/enriquecer_categorias/categorizador_dev/log4j-spark.properties
CONFIG=/data/semantix/enriquecer_categorias/categorizador_dev/config_source.properties

# Configuracoes de alocacao do cluster e tolerancia a falhas
EXECUTOR_CORES=10
NUM_EXECUTORS=4 #2
EXECUTOR_MEMORY=8G #4
DRIVER_MEMORY_OVERHEAD=1024 #512
EXECUTOR_MEMORY_OVERHEAD=1024 #512
MAX_APP_ATTEMPTS=4
MAX_DRIVER_FAILURES=16
MIN_RATE_PARTITON=10
MAX_RATE_PARTITON=5000

spark-submit --verbose  \
            --name "$APP_NAME" \
            --master $MASTER \
            --queue $QUEUE \
            --num-executors $NUM_EXECUTORS \
            --executor-cores $EXECUTOR_CORES \
            --executor-memory $EXECUTOR_MEMORY \
            --conf spark.yarn.driver.memoryOverhead=$DRIVER_MEMORY_OVERHEAD \
            --conf spark.yarn.executor.memoryOverhead=$EXECUTOR_MEMORY_OVERHEAD \
            --conf spark.yarn.maxAppAttempts=$MAX_APP_ATTEMPTS \
            --conf spark.yarn.max.executor.failures=$MAX_DRIVER_FAILURES \
            --conf spark.streaming.backpressure.enabled=true \
            --conf spark.streaming.backpressure.pid.minRate=$MIN_RATE_PARTITON \
            --conf spark.streaming.kafka.maxRatePerPartition=$MAX_RATE_PARTITON \
            --files $KEY_CONF#key.conf,$KEYTAB,$LOG_PROPERTIES#log4j-spark.properties,$CONFIG#application.properties \
            --driver-java-options "-Djava.security.auth.login.config=key.conf" \
            --conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=./key.conf -Dsun.security.krb5.debug=true -Dlog4j.configuration=./log4j-spark.properties" $JAR_NAME