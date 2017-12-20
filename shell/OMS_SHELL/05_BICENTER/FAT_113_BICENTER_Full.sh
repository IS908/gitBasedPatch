#!/bin/bash

HOST_IP=57.25.2.113
GOAL=113
APP_NAME=BICENTER
FILE_TYPE=Full
TAG_NAME=${APP_NAME}_${FILE_TYPE}_${TAG_NO}
VERSION_NO=App_${TAG_NAME}
TARGET=FAT_${GOAL}_${VERSION_NO}
TEMP_DOCUMENT=${WORKSPACE}/modules/fintelligen-integration/online-all-integration/target

cd ${WORKSPACE}
tar -czf reprot.tar.gz report

CHECK_RESULT() {
    if [[ "$?" != "0" ]]
    then
        echo "执行失败..."
        exit 1
    fi
}
echo "增量版本包更名......"
cd ${TEMP_DOCUMENT}

mv ${APP_NAME} ${TARGET}
zip -q -r ${TARGET}.zip ${TARGET}
CHECK_RESULT

echo "增量版本包移动到指定目录"
mv ${TARGET}.zip ${OMS_HOME}
CHECK_RESULT

echo "编译成功,通知OMS....."
RESULT=`curl -G -i -S ${OMS_URL}?hostIp=${HOST_IP}\&moType=${APP_NAME}\&versionNo=${VERSION_NO}\&fileType=${FILE_TYPE}\&fileName=${TARGET}.zip`
if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    echo "失败原因："${RESULT}
    exit 1
fi

