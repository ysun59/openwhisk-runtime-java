#!/bin/bash

datadir="/mnt/local/frodrigo/minio-data"

mkdir -p $datadir

#for i in {1..10000}
#do
#    openssl rand 1024 > $datadir/file-$i.dat
#done

docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" -v $datadir:/mnt/data  minio/minio server /mnt/data
