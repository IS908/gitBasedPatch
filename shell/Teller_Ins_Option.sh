#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **            Teller9 Ins Deploy Shell                  **
echo **              http://www.dcits.com                    **
echo **            author:zhangjig@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 原bat增量出去脚本思路描述：
# 1、根据开发人员提交的txt增量描述文件生成要编译的交易配置文件bulid.properties
# 2、开始进行增量交易的编译
# 3、进行增量抽取工作
# 4、将增量目标码打成压缩包待部署

#################### Var Setting START ####################
SIGN_FLAG=Y
SIGN_PWD=
FILE_PATH=`pwd`
RUNDATE=`date +%Y%m%d`
TAG_NO=02
BUILD_PATH=${FILE_PATH}
SIGN_PATH=${FILE_PATH}/SmartTeller9/InteractiveFrame_ClientResource/application
SIGN_JAR=SmartTeller9/InteractiveFrame_ClientResource/application/*.jar
BUILD_PROPERTIES=${BUILD_PATH}/build.properties
INCFILE=${FILE_PATH}/RUNALL/app_${RUNDATE}${TAG_NO}.txt
INCFILE_NEW=${FILE_PATH}/RUNALL/app_${RUNDATE}${TAG_NO}.txt~
TARGET=app_SmartTeller9_${RUNDATE}_${TAG_NO}_patch.zip
ANT_HOME=${FILE_PATH}/tools/ant/

MSG_NOT_EXIST_PROPERTIES='不存在增量执行文件build.properties，不可以进行打增量版本'
MSG_NOT_EXIST_INCFILE='不存在增量清单文件，不可以进行打增量版本'
PREFIX="SmartTeller9\trans"
SURFIX=".jar"
VENUS_JAR="VENUS"
FIRST=0
BUILD=""
TEMP=""
TEMP2=""
#################### Var Setting END ####################

export ANT_HOME=${ANT_HOME}
cd ${BUILD_PATH}
##检查增量执行文件是否存在
if [ ! -e "build.properties" ]; then 
	echo $MSG_NOT_EXIST_PROPERTIES
	exit 0
fi
##检查增量清单是否存在
if [ ! -e "$INCFILE" ]; then 
	echo $MSG_NOT_EXIST_INCFILE
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

cp ${BUILD_PROPERTIES} temp.properties

# 读取增量描述文件，进行增量配置文件的处理
##将增量清单中需要编译的交易提取出来,将不编译写入过度清单文件
for line in $(cat ${INCFILE})
do 
    TEMP2=$line
    if [[ "$line" =~ "${PREFIX}" ]]
    then
#        echo "包含SmartTeller9\trans"
        echo ${TEMP2//\\/\/} >>${INCFILE_NEW}
        if [[ "$line" =~ "${SURFIX}" ]]
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
	echo "="$TEMP2
        if [[ "${TEMP2}" =~ "${VENUS_JAR}" ]]
        then
#	        echo "不包含SmartTeller9\trans，包含VENUS"
            TEMP2=${TEMP2//VENUS/SmartTeller9\/trans}
            echo -e $TEMP2 >> ${INCFILE_NEW}
        else
            echo -e $TEMP2 >> ${INCFILE_NEW}
        fi
    fi
done

#筛选需要编译的交易
BUILD=${BUILD//SmartTeller9\\trans\\/}
BUILD=${BUILD//.jar/}
echo "需要打版本的交易为："$BUILD
sed -i "/sourceBase=/s/=.*/=${BUILD//\\/\\/\\}/" temp.properties

# 进行增量交易的编译
echo "开始编译交易"
ant -buildfile build_ins.xml

##签名
if [[ ${SIGN_FLAG}="Y" ]]
then
    echo "开始进行签名"
    cd ${SIGN_PATH}
    ant -f sign.xml
fi

# 进行增量抽取
##增量清单文件逐个压缩到增量目标zip
cd ${BUILD_PATH}
if [ -e "$INCFILE_NEW" ]; then
    echo "增量版本开始打包..."
	for line in $(cat ${INCFILE_NEW})
    do
        zip -q -r ${TARGET}  $line
    done
fi

##签名jar包压缩到增量目标zip
if [[ ${SIGN_FLAG}="Y" ]]
then
    zip -q -r ${TARGET} ${SIGN_JAR}
fi

##将SmartTeller9_1.0.0.jar公共包压缩到增量目标zip包
zip -q -r ${TARGET} SmartTeller9/trans/SmartTeller9_1.0.0.jar

# 进行增量包的发布
