#!/bin/bash

#docker_compose="/mnt/local/frodrigo/incubator-openwhisk-devtools/docker-compose"
#docker_compose="/home/rbruno/git/incubator-openwhisk-devtools/docker-compose"

#export WSK_CONFIG_FILE=$docker_compose/.wskprops
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

#wsk=$docker_compose/openwhisk-src/bin/wsk

#wsk action update -i FileHashing ./target/filehashing.jar --memory 512  --main ch.ethz.systems.FileHashing --docker openwhisk/java8action
wsk action update -i FileHashing ./target/filehashing.jar --memory 256  --main ch.ethz.systems.FileHashing --docker ysun59/java8action
#$wsk action update -i FileHashing filehashing.jar --memory 1024  --main FileHashing --docker rfbpb/java8action
