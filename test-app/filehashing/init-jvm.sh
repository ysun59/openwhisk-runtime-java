#!/bin/bash

for i in {1..1024}
do
    openssl rand 1024 > /tmp/file-$i.dat
done

../../tools/invoke.py init FileHashing filehashing.jar
