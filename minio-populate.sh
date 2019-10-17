#!/bin/bash

datadir="/mnt/local/frodrigo/minio-data"
mkdir -p $datadir

for i in {1..10000}
do
	openssl rand 1048576 > $datadir/file-$i.dat
done

#~/local/mc mb minio/mydata
#~/local/mc cp ~/local/minio-data/* minio/mydata 
