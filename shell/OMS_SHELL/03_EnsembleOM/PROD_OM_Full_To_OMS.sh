#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **             PROD OM To OMS Shell                     **
echo **              http://www.dcits.com                    **
echo **            author:zhangjig@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 脚本思路描述：
# 1、将编译的应用包解压，更名：更名逻辑是zip名称和zip内文件夹名称一致，在压缩
# 2、将更名后应用包mv到指定目录
# 3、执行GET请求，调用OMS接口，通知OMS部署应用包的具体信息
#

######## Var Setting START ########
#HOST_IP=57.25.2.111
#ID=`echo ${HOST_IP##*.}`
TYPE=Prod_A
FILE_TYPE=Full
APP_NAME=EnsembleOM
TAG_NAME=${APP_NAME}_${FILE_TYPE}_${TAG_NO}
VERSION_NO=${TYPE}_App_${TAG_NAME}
TARGET=${VERSION_NO}
TMP_APP_NAME=ensemble-om-1.0.4-SNAPSHOT
TEMP_DOCUMENT=${WORKSPACE}/target/${TMP_APP_NAME}-assembly/
######## Var Setting END ########
CHECK_RESULT() {
    if [[ "$?" != "0" ]]
    then
        echo "执行失败..."
        exit 1    
    fi
}
echo "增量版本包更名......"
cd ${TEMP_DOCUMENT}
mv ${TMP_APP_NAME} ${TARGET}
zip -q -r ${TARGET}.zip ${TARGET}
CHECK_RESULT

echo "增量版本包移动到指定目录"
mv ${TARGET}.zip ${OMS_HOME}
CHECK_RESULT

echo "编译成功,通知OMS....."
RESULT=`curl -G -i -S ${OMS_URL}?hostIp=${HOST_IP}\&moType=${APP_NAME}\&versionNo=${VERSION_NO}\&fileType=${FILE_TYPE}\&fileName=${TARGET}.zip\&userId=${PROD_USER}`
if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    echo "失败原因："${RESULT}
    exit 1
fi
