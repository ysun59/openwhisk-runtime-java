#!/bin/bash

doker_name="hello"

while true; do
    docker stats --no-stream | grep $doker_name | awk '{print $4}'
done
