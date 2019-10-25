#!/bin/bash

#docker_compose="/mnt/local/frodrigo/incubator-openwhisk-devtools/docker-compose"
docker_compose="/home/rbruno/git/incubator-openwhisk-devtools/docker-compose"

export WSK_CONFIG_FILE=$docker_compose/.wskprops
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

wsk=$docker_compose/openwhisk-src/bin/wsk

$wsk action --result  -i invoke FileHashing  --param seed 1234
