#!/bin/bash

FLAG_FULL=FULL
FLAG_INCR=INCR

hostIp=57.25.2.111
moType=ModelBank
versionNo=FAT_113_${TAG_NAME}
fileType=Full
fileName=${versionNo}.tar.gz
OMS_HOME=/app/dcits/oms/jenkins_space


DEPLOY_FULL() {
    cd ${WORKSPACE}/modules/modelBank-all-integration/target
    cd modelBank-integration-assembly
    rm -rf modelBank-integration
    tar -zxf ../modelBank-integration-assembly.tar.gz
    mv modelBank-integration ${versionNo}
    tar -czf ${fileName} ${versionNo}
    mv ${fileName} ${OMS_HOME}
}

DEPLOY_INCR() {
    cd ${WORKSPACE}/modules/modelBank-all-integration/target/PatchTmp
    mv ModelBank ${versionNo}
    tar -czf ${versionNo}.tar.gz ${versionNo}
    mv ${fileName} ${OMS_HOME}
}

# 进行全量增量判断
if [[ "${BUILD_FLAG}" == "${FLAG_INCR}" ]]
then
    DEPLOY_INCR
else if [[ "${BUILD_FLAG}" == "${FLAG_FULL}" ]]
then
    DEPLOY_FULL
    fi
fi

# 向OMS发送通知
RESULT=`curl -G -i http://57.25.2.137:9991/oms/jenkinsPostVersion?hostIp=${hostIp}&amp;moType=${moType}&amp;versionNo=${versionNo}&amp;fileType=${fileType}&amp;fileName=${fileName}`

if [[ "${RESULT}" =~ "success" ]]
then
    echo "调用OMS成功......"
else
    echo "调用OMS失败......"
    exit 1
fi