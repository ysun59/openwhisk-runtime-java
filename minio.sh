#!/bin/bash

datadir="/mnt/local/frodrigo/minio-data"
mkdir -p $datadir

docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" -v $datadir:/mnt/data  minio/minio server /mnt/data
