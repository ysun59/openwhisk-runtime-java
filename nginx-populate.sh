#!/bin/bash

# maximum number of dockers to create configurations for
dockers=320
host=r630-02
dest_dir=/tmp

for i in $(seq 1 $dockers)
do
	dest_file=$dest_dir/nginx.conf.$i
	echo "
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
        worker_connections 768;
        # multi_accept on;
}


http {
        upstream backend {
	" > $dest_file
    for j in $(seq 1 $i)
    do
        echo "server $host:$((8080+$j));" >> $dest_file
    done
	echo "

        }

        server {
                listen 8080;
                location / {
                        proxy_pass http://backend;
                }
        }
}
	" >> $dest_file
done
