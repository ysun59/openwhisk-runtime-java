#!/bin/bash

datadir="/tmp/minio-data/files"
mkdir -p $datadir

# for the filehashing test
for i in {1..100}
do
	openssl rand 1048576 > $datadir/file-$i.dat
	#openssl rand 1024 > $datadir/file-$i.dat
done

# for the video, image classification test, and thumbnail
cp res/* $datadir

#~/local/mc mb minio/mydata
#~/local/mc cp ~/local/minio-data/* minio/mydata
