#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

debug="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y"

$JAVA_HOME/bin/java $debug -Dfile.encoding=UTF-8 -jar core/java8/proxy/build/libs/proxy-all.jar
