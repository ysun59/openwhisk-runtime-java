#!/bin/bash
docker login
docker tag java8action rfbpb/java8action
docker push rfbpb/java8action
