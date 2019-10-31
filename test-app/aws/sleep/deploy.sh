#!/bin/bash

(cd code && mvn package)

aws lambda delete-function --function-name sleep-jvm

aws lambda create-function --function-name sleep-jvm \
--zip-file fileb://./code/target/sleep-1.0-SNAPSHOT.jar --handler Sleep::handleRequest --runtime java8 \
--role arn:aws:iam::612948648635:role/service-role/hpy_lambda --memory-size $1