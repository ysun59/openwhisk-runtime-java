#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

wsk action --result  -i invoke Login --param username "username1" --param password "password1"
#wsk action --result  -i invoke Login --param username "sy" --param password "123"
