#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **           FAT Teller9 To OMS Shell                   **
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
OMS=http://57.25.2.137:9991/oms/jenkinsPostVersion
TARGET_PATH=/app/dcits/oms/jenkins_space
HOST_IP=57.25.2.111
GOAL=113
MO_TYPE=SmartTeller9
FILE_TYPE=Incr
TAG_NAME=SmartTeller9_Ins_${TAG_NO}
VERSION_NO=App_${TAG_NAME}
TARGET=FAT_${GOAL}_${VERSION_NO}
TEMP_DOCUMENT=TEMP_TELLER9
######## Var Setting END ########
CHECK_RESULT() {
    if [[ "$?" != "0" ]]
    then
        echo "执行失败..."
        exit 1    
    fi
}

if [[ -d ${TEMP_DOCUMENT} ]];then
    rm -rf ${TEMP_DOCUMENT}
fi
mkdir ${TEMP_DOCUMENT}

echo "增量版本包更名......"
unzip -o -d ${TEMP_DOCUMENT}  ${VERSION_NO}.zip
CHECK_RESULT

cd ${TEMP_DOCUMENT}
mv SmartTeller9 ${TARGET}
zip -q -r ${TARGET}.zip ${TARGET}
CHECK_RESULT

echo "增量版本包移动到指定目录"
mv ${TARGET}.zip ../
CHECK_RESULT

echo "编译成功,通知OMS....."
RESULT=`curl -G -i ${OMS}?hostIp=${HOST_IP}\&moType=${MO_TYPE}\&versionNo=${VERSION_NO}\&fileType=${FILE_TYPE}\&fileName=${TARGET}.zip`
if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    exit 1
fi
