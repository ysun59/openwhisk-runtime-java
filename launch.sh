#!/bin/bash

docker rm test_hello
docker run -i -t -p 8080:8080 --name test_hello  rfbpb/java8action
