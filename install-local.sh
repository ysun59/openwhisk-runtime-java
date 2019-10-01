#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

cd core/java8/proxy
./gradlew oneJar
cd -
