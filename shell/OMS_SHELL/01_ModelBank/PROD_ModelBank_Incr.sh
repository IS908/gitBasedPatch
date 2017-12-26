#!/bin/bash

#hostIp=57.25.2.111
moType=ModelBank
versionNo=${HOST_IP}_${TAG_NAME}
fileType=Incr
fileName=${versionNo}.tar.gz
#OMS_HOME=/app/dcits/oms/jenkins_space
TARGET=${WORKSPACE}/modules/modelBank-all-integration/target

DEPLOY_INCR() {
    cd ${TARGET}/PatchTmp
    mv ModelBank ${versionNo}
    tar -czf ${fileName} ${versionNo}
    mv ${fileName} ${OMS_HOME}
}

# 进行全量增量判断
DEPLOY_INCR

# 向OMS发送通知
RESULT=`curl -G -i ${OMS_URL}?hostIp=${HOST_IP}\&moType=${moType}\&versionNo=${versionNo}\&fileType=${fileType}\&fileName=${fileName}\&userId=${PROD_USER}`

if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    exit 1
fi