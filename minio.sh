#!/bin/bash

datadir="/tmp/minio-data"

docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" -v $datadir:/data  minio/minio server /data
#docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" minio/minio server $datadir
