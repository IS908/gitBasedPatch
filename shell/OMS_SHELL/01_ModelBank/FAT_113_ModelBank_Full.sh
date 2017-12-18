#!/bin/bash

FLAG_FULL=Full
FLAG_INCR=Incr

# 文件存放目录

hostIp=57.25.2.111
moType=ModelBank
TAG_NAME=ModelBank_Full_${TAG_NO}
versionNo=FAT_113_${TAG_NAME}
fileType=Full
fileName=${versionNo}.tar.gz
OMS_HOME=/app/dcits/oms/jenkins_space
TARGET=${WORKSPACE}/modules/modelBank-all-integration/target


DEPLOY_FULL() {
    cd ${TARGET}/modelBank-integration-assembly
    rm -rf modelBank-integration
    tar -zxf ${TARGET}/modelBank-integration-assembly.tar.gz
    mv modelBank-integration ${versionNo}
    tar -czf ${fileName} ${versionNo}
    mv ${fileName} ${OMS_HOME}
}

DEPLOY_FULL

# 向OMS发送通知
RESULT=`curl -G -i http://57.25.2.137:9991/oms/jenkinsPostVersion?hostIp=${hostIp}\&moType=${moType}\&versionNo=${versionNo}\&fileType=${fileType}\&fileName=${fileName}`

if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    exit 1
fi
