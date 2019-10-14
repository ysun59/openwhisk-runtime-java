#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

$JAVA_HOME/bin/javac -cp ../../gson-2.8.5.jar FileHashing.java
$JAVA_HOME/bin/jar cvf filehashing.jar FileHashing.class
