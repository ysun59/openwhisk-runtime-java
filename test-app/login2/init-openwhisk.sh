#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

#wsk action update -i Login ./target/login.jar --memory 512 --add-host=host.docker.internal:host-gateway --main ch.ethz.systems.Login --docker ysun59/java8action
#wsk action update -i Login ./target/login.jar --memory 512  --main Login --docker openwhisk/java8action

#wsk action update -i Login ./target/login.jar --memory 512  --main ch.ethz.systems.Login --docker openwhisk/java8action
wsk action update -i Login ./target/login.jar --memory 512  --main ch.ethz.systems.Login --docker ysun59/java8action

