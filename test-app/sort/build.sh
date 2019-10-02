#!/bin/bash

javac -cp ../../gson-2.8.5.jar Sort.java
jar cvf sort.jar Sort.class
