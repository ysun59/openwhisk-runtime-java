#!/bin/bash
echo "c concurrency: $1, nt: $2"
datadir="/tmp/minio-data"

cd /home/yu/openwhisk
./gradlew core:standalone:bootRun > /dev/null 2>&1 &
#./gradlew core:standalone:bootRun&
OPENWHISK=$!
#cd /home/yu/openwhisk-runtime-java
#./minio.sh&
docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" -v $datadir:/data  minio/minio server /data > /dev/null 2>&1 &
#docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=keykey" -e "MINIO_SECRET_KEY=secretsecret" -v $datadir:/data  minio/minio server /data&
MINIO=$!
echo $OPENWHISK
echo $MINIO

#sleep 40
sleep 60


cd /home/yu/openwhisk-runtime-java/test-app/sleep
python3 loadrunner-canrun.py -c $1 -nt $2 -ne 100
#python3 loadrunner-canrun.py -c 1 -nt 16 -ne 100
#python3 loadrunner-canrun.py -c 1 -nt 16 -ne 100



sudo kill -s 9 $OPENWHISK
sudo kill -s 9 $MINIO

docker stop $(docker ps -aq)
docker rm $(docker ps -aq)

sudo sync
sudo sh  -c 'echo 3 > /proc/sys/vm/drop_caches'
