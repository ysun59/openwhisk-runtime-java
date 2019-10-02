#!/bin/bash

javac -cp ../../gson-2.8.5.jar Hello.java
jar cvf hello.jar Hello.class
