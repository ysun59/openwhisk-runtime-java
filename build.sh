#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
export OPENWHISK_HOME=~/git/openwhisk
./gradlew core:java8:distDocker
