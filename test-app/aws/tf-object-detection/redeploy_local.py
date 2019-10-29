import os
import sys
import re

docker_image_name = 'tf-object-detection'
docker_path = '/home/voja/Development/work/docker_build/%s/'%docker_image_name

os.system('cp -rf . %s/code/'%(docker_path,))
os.system('cp -rf Dockerfile %s/Dockerfile'%(docker_path,))

#stop all docker
os.system('docker rm $(docker ps -a -q)')
os.system('cd  %s && docker build --tag %s .'%(docker_path, docker_image_name))

#run the code
#docker run --cpus=".5"  tf-object-detection 2