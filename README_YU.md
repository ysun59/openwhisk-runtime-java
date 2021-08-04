
* [Prerequists](#Prerequists) (Install docker, java, nodejs, requests, matplotlib)
* Run openwhisk-runtime-java:
  * [Run individual functions](#Run-individual-functions) (run five individual functions: sleep, filehashing, video, image-classification, login)
  * [Run loadrunner-canrun.py](#Run-loadrunner-canrun-py) (run 100 experiments synchronously)


# Prerequists
## Install docker, java, nodejs, requests, matplotlib
```bash
sudo apt-get update
sudo apt install -y docker.io
sudo usermod -aG docker ${USER}
sudo reboot
```

```bash
sudo apt-get install -y openjdk-8-jdk
```

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.36.0/install.sh | bash
bash
nvm install 12
nvm use 12
```

```bash
sudo apt-get install python3-pip
apt install python3-requests

pip3 install matplotlib
```

## Install openwhisk
### [ysun59/openwhisk](https://github.com/ysun59/openwhisk)
```bash
git clone https://github.com/ysun59/openwhisk.git
cd openwhisk
git checkout master6.3.sy
```
### Statrt openwhisk
```bash
cd openwhisk
./gradlew core:standalone:bootRun 
```

## Install wsk
```bash
mkdir wsk-cli
cd wsk-cli
wget https://github.com/apache/openwhisk-cli/releases/download/1.2.0/OpenWhisk_CLI-1.2.0-linux-amd64.tgz
tar zxvf OpenWhisk_CLI-1.2.0-linux-amd64.tgz
sudo cp wsk-cli/wsk /usr/local/bin/
wsk property set --apihost 'http://172.17.0.1:3233'
wsk property set --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP
```

## Install openwhisk-runtime-java
```bash
git clone https://github.com/ysun59/openwhisk-runtime-java.git
cd openwhisk-runtime-java
git checkout develsy
```



# Run
## Run individual functions
Run five functions in openwhisk-runtime-java/test-app folder
sleep, filehashing, video, image-classification, login
### Run 'Sleep'
```bash
cd openwhisk-runtime-java/test-app/sleep
./build.sh
./init-openwhisk.sh
./run-openwhisk.sh
```
#### generate data needed for: filehashing, video, image-classification
```bash
cd openwhisk-runtime-java/
./minio-populate.sh
```

### Run 'filehashing'
```bash
cd openwhisk-runtime-java
./minio.sh
cd openwhisk-runtime-java/test-app/filehashing
```
**modify nano src/main/java/ch/ethz/systems/FileHashing.java**
Set the ip to `"http://172.17.0.1:9000"`or the ip of command `ifconfig`
```bash
./build.sh
./init-openwhisk.sh
./run-openwhisk.sh
```

### Run 'video'
```bash
cd openwhisk-runtime-java
./minio.sh
cd openwhisk-runtime-java/test-app/video
```
**modify nano src/main/java/ch/ethz/systems/FFMPEG.java**
Set the ip to "http://172.17.0.1:9000” or the ip of command "ifconfig"
```bash
./build.sh
./init-openwhisk.sh
./run-openwhisk.sh
```

### Run 'image-classification'
```bash
cd openwhisk-runtime-java
./minio.sh
cd openwhisk-runtime-java/test-app/image-classification
```
**modify nano src/main/java/ch/ethz/systems/InceptionImageClassifierDemo.java**
Set the ip to "http://172.17.0.1:9000” or the ip of command "ifconfig"
```bash
./build.sh
./init-openwhisk.sh
./run-openwhisk.sh
```

### Run 'login'
#### Install MongoDB
[mongodb install link](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu-tarball/)
#### start MongoDB
```bash
mongod  --dbpath /var/lib/mongo --logpath /var/log/mongodb/mongod.log --fork --bind_ip 0.0.0.0 
```
**Note** need `--bind_ip 0.0.0.0` when start the MongoDB so that containers can query the data in local MongoDB
**modify nano src/main/java/ch/ethz/systems/InceptionImageClassifierDemo.java**
Set the ip to `http://172.17.0.1:9000` or the ip of command `ifconfig`

#### generate data needed for login function
```bash
cd openwhisk-runtime-java
python3 mongodb-populate.py
```

```bash
cd test-app/login/
./build.sh
./init-openwhisk.sh
./run-openwhisk.sh
```



## Run loadrunner canrun py
run 100 experiments synchronously
```bash
cd openwhisk-runtime-java/test-app/sleep
./run-test2.sh
```
It will generate the data to the data folder
Can open "loadrunner-canrun.py", modify the line "deploy_command = 'wsk .....--docker openwhisk/java8action', to generate the data with default openwhisk/java8action or photon papters solution: --docker ysun59/java8action 
`cp -r data data-openwhisk` or `cp -r data data-pho`

### evaluation and plot
```bash
cd openwhisk-runtime-java/test-app/sleep
python3 plot.py
```
same as 'filehashing', 'video', 'image-classification', 'login' functions
