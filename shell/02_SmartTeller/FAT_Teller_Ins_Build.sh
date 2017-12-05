#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **            Teller9 Ins Build Shell                  **
echo **              http://www.dcits.com                    **
echo **            author:zhangjig@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 脚本思路描述：
# 1、根据开发人员提交txt增量清单描述文件，生成需要编译的交易配置文件patch.properties
# 2、进行增量交易的构建
# 3、进行签名
# 4、将增量目标码打成压缩包待部署
#

######## Var Setting START ########
SIGN_FLAG="N"
FILE_PATH=`pwd`
RUNDATE=`date +%Y%m%d`
BUILD_PATH=${FILE_PATH}
ANT_HOME=${FILE_PATH}/tools/ant/
TARGET=App_SmartTeller9_Ins_${TAG_NO}.zip
INCFILE=${FILE_PATH}/RUNALL/app_${INSFILE_NAME}.txt
BUILD_PROPERTIES=${BUILD_PATH}/build.properties
INCFILE_NEW=${INCFILE}~
SIGN_JAR=SmartTeller9/InteractiveFrame_ClientResource/application/*.jar
SIGN_FILES=SmartTeller9/InteractiveFrame_ClientResource/application/.*.jar
SIGN_PATH=${FILE_PATH}/SmartTeller9/InteractiveFrame_ClientResource/application

TEMP=""
TEMP2=""
BUILD=""
FIRST=0
surfix=".jar"
VENUS_JAR="VENUS"
prefix="SmartTeller9\trans"
MSG_NOT_EXIST_INCFILE='不存在增量清单文件，不可以进行打增量版本'
MSG_NOT_EXIST_PROPERTIES='不存在增量执行文件build.properties，不可以进行打增量版本'
######## Var Setting END ########

export ANT_HOME=${ANT_HOME}
cd ${BUILD_PATH}
echo "开始SmartTeller9增量版本构建"
##检查增量执行文件是否存在
if [ ! -e "build.properties" ]; then 
	echo ${MSG_NOT_EXIST_PROPERTIES}
	exit 0
fi
##检查增量清单是否存在
echo "增量清单名称是："app_${INSFILE_NAME}.txt
if [ ! -e "$INCFILE" ]; then 
	echo ${MSG_NOT_EXIST_INCFILE}
	exit 1
fi
##删除同名的过度增量清单
if [ -e "$INCFILE_NEW" ]; then 
	rm "$INCFILE_NEW"
fi
##删除同名zip增量包
if [ -e "$TARGET" ]; then 
	rm "$TARGET"
fi

cp ${BUILD_PROPERTIES} patch.properties

# 读取增量描述文件，进行增量配置文件的处理
##处理思路：1、筛选需要编译的交易，将需要编译的交易写入增量执行文件patch.properties
##         2、将不需要编译的文件写入过度增量清单
##         3、期间将windows中\路径替换为linux中/，将不编译的VENUS交易路径替换为SmartTeller9\trans
for line in $(cat ${INCFILE})
do 
    TEMP2=${line}
    if [[ "$line" =~ "${prefix}" ]]
    then
#        echo "包含SmartTeller9\trans"
        echo ${TEMP2//\\/\/} >>${INCFILE_NEW}
        if [[ "$line" =~ "${surfix}" ]]
        then
#   		echo "包含jar"
            if test ${FIRST} -ne 0;then
                BUILD=`echo ${TEMP},${line}`
            else
                BUILD=`echo ${line}`
                FIRST=1
            fi
            TEMP=${BUILD}
        fi
    else
	TEMP2=${TEMP2//\\/\/}
        if [[ "${TEMP2}" =~ "${VENUS_JAR}" ]]
        then
#	        echo "不包含SmartTeller9\trans，包含VENUS"
            TEMP2=${TEMP2//VENUS/SmartTeller9\/trans}
            echo -e ${TEMP2} >> ${INCFILE_NEW}
        else
            echo -e ${TEMP2} >> ${INCFILE_NEW}
        fi
    fi
done

#将需要编译的交易写入编译执行文件
BUILD=${BUILD//SmartTeller9\\trans\\/}
BUILD=${BUILD//.jar/}
echo "需要打版本的交易为："$BUILD
sed -i "/sourceBase=/s/=.*/=${BUILD//\\/\/}/" patch.properties

# 进行增量交易的编译
echo "开始构建交易"
ant -buildfile build_ins.xml -DtargetEnv=${TARGET_ENV}
if [[ "$?" = "0" ]]
then
    echo "编译成功"
else
    exit 1    
fi

# 检查是否需要签名操作
count=`grep -c "${SIGN_FILES}" ${INCFILE_NEW}` 
if [[ count -gt 0 ]]
then
    echo "有客户端jar包更新，需要进行签名操作"
    SIGN_FLAG="Y"
fi

##签名
if [[ ${SIGN_FLAG} = "Y" ]]
then
    echo "开始进行签名"
    cd ${SIGN_PATH}
    ant -f sign.xml
    if [[ "$?" = "0" ]]
    then
        echo "签名成功"
    else
        exit 1    
    fi
else
    echo "无客户端jar包更新，不需要签名"    
fi

# 进行增量交易压缩
##根据增量清单文件逐个将文件压缩到增量目标zip
cd ${BUILD_PATH}
if [ -e "${INCFILE_NEW}" ]; then
    echo "增量版本开始打包..."
	for line in $(cat ${INCFILE_NEW})
    do
        zip -q -r ${TARGET}  $line
    done
fi

##签名jar包压缩到增量目标zip
if [[ ${SIGN_FLAG} = "Y" ]]
then
    zip -q -r ${TARGET} ${SIGN_JAR}
else
    echo "不签名，不需要进行签名jar的压缩"
fi

##将SmartTeller9_1.0.0.jar公共包压缩到增量目标zip包
zip -q -r ${TARGET} SmartTeller9/trans/SmartTeller9_1.0.0.jar

echo "结束SmartTeller9增量版本构建。。。"
