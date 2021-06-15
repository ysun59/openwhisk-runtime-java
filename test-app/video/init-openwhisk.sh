#!/bin/bash

#export WSK_CONFIG_FILE=/home/rbruno/git/incubator-openwhisk-devtools/docker-compose/.wskprops
#export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
#export OPENWHISK_HOME=/home/rbruno/git/openwhisk

# wsk action update -i FileHashing filehashing.jar --main FileHashing --docker rfbpb/java8action
#wsk --apihost https://129.132.102.71 --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP action --result  -i invoke Sort  --param seed 1234

# wsk --apihost https://129.132.102.71 --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP action create -i FileHashing filehashing.jar --main FileHashing --docker rfbpb/java8action -c 1

wsk action update -i TF ./target/video.jar --memory 512 --main ch.ethz.systems.FFMPEG --docker ysun59/java8action -c 1
#wsk --apihost https://129.132.102.71 --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP action create -i TF tf/target/image-classifier.jar --main com.github.chen0040.tensorflow.classifiers.images.InceptionImageClassifierDemo --docker rfbpb/java8action -c 1
