#!/bin/bash

CONFIG=application.conf
JARFILE=./test-kafka-consumer-assembly-1.0.0.jar
APP_NAME="[test-consumer]"
MAIN_CLASS=br.com.semantix.testkafkaconsumer.ConsumerApp
KEYTAB=/home/enviotemip/enviotemip.keytab
JAAS=/home/enviotemip/jaas.conf

spark-submit --class $MAIN_CLASS \
--name $APP_NAME \
--master yarn \
--deploy-mode client \
--driver-memory 1G \
--executor-memory 1G \
--conf spark.executor.cores=1 \
--conf spark.driver.cores=1 \
--conf spark.driver.memoryOverhead=512 \
--conf spark.executor.memoryOverhead=512 \
--conf spark.streaming.backpressure.enabled=true \
--conf spark.streaming.backpressure.pid.minRate=10 \
--conf spark.streaming.backpressure.initialRate=10 \
--conf spark.streaming.kafka.maxRatePerPartition=10 \
--conf spark.streaming.receiver.maxRate=10 \
--conf spark.yarn.maxAppAttemps=4 \
--conf spark.task.maxFailures=4 \
--conf spark.dynamicAllocation.enabled=true \
--conf spark.dynamicAllocation.initialExecutors=3 \
--conf spark.dynamicAllocation.maxExecutors=6 \
--conf spark.dynamicAllocation.minExecutors=3 \
--conf spark.shuffle.service.enabled=true \
--conf spark.shuffle.service.port=7337 \
--files=$CONFIG,$KEYTAB,$JAAS \
--driver-java-options -Dconfig.file=$CONFIG \
--conf "spark.driver.extraJavaOptions=-Dconfig.file=$CONFIG \
-Djava.security.auth.login.config=$JAAS" \
--conf spark.driver.extraClassPath=./ \
--conf spark.executor.extraClassPath=./ \
--conf spark.executor.extraJavaOptions=-Djava.security.auth.login.config=$JAAS \
$JARFILE