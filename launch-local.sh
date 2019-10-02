#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

$JAVA_HOME/bin/java -Dfile.encoding=UTF-8 -jar core/java8/proxy/build/libs/proxy-all.jar
