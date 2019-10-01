#!/bin/bash

debug="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y"

java $debug -Dfile.encoding=UTF-8 -jar core/java8/proxy/build/libs/proxy-all.jar
