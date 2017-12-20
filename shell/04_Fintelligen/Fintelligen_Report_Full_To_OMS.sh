#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **        FAT Fintelligen _Report To OMS Shell          **
echo **              http://www.dcits.com                    **
echo **            author:zhangjig@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 脚本思路描述：
# 1、将更名后应用包mv到指定目录
# 2、执行GET请求，调用OMS接口，通知OMS部署应用包的具体信息
#

######## Var Setting START ########
#HOST_IP=57.25.2.111
APP_NAME=BICENTER
FILE_TYPE=Full
TARGET=Report_Full_${TAG_NO}
######## Var Setting END ########
CHECK_RESULT() {
    if [[ "$?" != "0" ]]
    then
        echo "执行失败..."
        exit 1    
    fi
}
##全量文件更名
mv report ${TARGET}

zip -r ${TARGET}.zip ${TARGET}

echo "增量版本包移动到指定目录"
mv ${TARGET}.zip ${OMS_HOME}
CHECK_RESULT

echo "编译成功,通知OMS....."
RESULT=`curl -G -i -S ${OMS_URL}?hostIp=${HOST_IP}\&moType=${APP_NAME}\&versionNo=${TARGET}\&fileType=${FILE_TYPE}\&fileName=${TARGET}.zip`
if [[ "${RESULT}" =~ "success" ]]
then
     echo "调用OMS平台成功......"
else
    echo "调用OMS平台失败......"
    echo "失败原因："${RESULT}
    exit 1
fi
