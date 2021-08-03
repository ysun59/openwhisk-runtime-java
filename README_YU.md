# Preqeuists
## Install docker, java
```java
sudo apt  install -y docker.io
```

```bash
sudo apt-get install -y openjdk-8-jdk
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.36.0/install.sh | bash
bash
nvm install 12
nvm use 12
```

## Install openwhisk
```shell
git clone https://github.com/ysun59/openwhisk.git
cd openwhisk
git checkout master6.3.sy
```
`./gradlew core:standalone:bootRun`
## Install wsk
`mkdir wsk-cli`
`cd wsk-cli`
`wget https://github.com/apache/openwhisk-cli/releases/download/1.2.0/OpenWhisk_CLI-1.2.0-linux-amd64.tgz`
`tar zxvf OpenWhisk_CLI-1.2.0-linux-amd64.tgz `
`sudo cp wsk-cli/wsk /usr/local/bin/`
`wsk property set --apihost 'http://172.17.0.1:3233'`
`wsk property set --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP`


## Install openwhisk-runtime-java
`git clone https://github.com/ysun59/openwhisk-runtime-java.git`
`cd openwhisk-runtime-java`
`git checkout develsy`

##
