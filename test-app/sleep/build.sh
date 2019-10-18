#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
#export JAVA_HOME=/usr/lib/jvm/java-8-oracle
$JAVA_HOME/bin/javac -cp ../../gson-2.8.5.jar:../../minio-6.0.11-all.jar Sleep.java
$JAVA_HOME/bin/jar cvf sleep.jar Sleep.class
