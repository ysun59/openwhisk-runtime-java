#!/bin/bash

docker rm test_hello
docker pull rfbpb/java8action
docker run -i -t -m 128m --cpus=0.8 -p 8080:8080 --name test_hello  rfbpb/java8action
