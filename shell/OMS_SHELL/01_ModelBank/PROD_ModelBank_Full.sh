#!/bin/bash

#hostIp=57.25.2.111
type=Prod_A
moType=ModelBank
versionNo=${type}_${TAG_NAME}
fileType=Full
fileName=${versionNo}.tar.gz
#OMS_HOME=/app/dcits/oms/jenkins_space
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
RESULT=`curl -G -i ${OMS_URL}?hostIp=${hostIp}\&moType=${moType}\&versionNo=${versionNo}\&fileType=${fileType}\&fileName=${fileName}\&userId=${PROD_USER}`

if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    exit 1
fi
