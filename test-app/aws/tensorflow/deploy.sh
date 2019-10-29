#!/bin/bash

(cd code && mvn package)

aws lambda delete-function --function-name tensorflow-jvm

aws s3 cp code/target/tensorflow-1.0-SNAPSHOT.jar s3://hpyindex/bmk/

aws lambda create-function --function-name tensorflow-jvm \
--code S3Bucket=hpyindex,S3Key=bmk/tensorflow-1.0-SNAPSHOT.jar \
--handler Main::handleRequest --runtime java8 \
--role arn:aws:iam::612948648635:role/service-role/hpy_lambda --memory-size 512 \
--timeout 60

#--zip-file fileb://./code/target/tensorflow-1.0-SNAPSHOT.jar \
#--code S3Bucket=hpyindex,S3Key=bmk/tensorflow-1.0-SNAPSHOT.jar \
