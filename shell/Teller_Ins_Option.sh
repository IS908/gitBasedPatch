#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **            Teller9 Ins Deploy Shell                  **
echo **              http://www.dcits.com                    **
echo **            author:chenkunh@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 原bat增量出去脚本思路描述：
# 1、根据开发人员提交的txt增量描述文件生成要编译的交易配置文件bulid.properties
# 2、开始进行增量交易的编译
# 3、进行增量抽取工作
# 4、将增量目标码打成压缩包待部署

#################### Var Setting START ####################
FILE_PATH=`pwd`
RUNDATE=`date +%Y%m%d`
TAG_NO=01
BUILD_PATH=${FILE_PATH}
BUILD_PROPERTIES=${BUILD_PATH}/build.properties
INCFILE=${FILE_PATH}/RUNALL/app_${RUNDATE}${TAG_NO}.txt

MSG_NOT_EXIST_PROPERTIES='不存在增量执行文件build.properties，不可以进行打增量版本'
MSG_NOT_EXIST_INCFILE='不存在增量清单文件，不可以进行打增量版本'
strA="SmartTeller9\trans"
strB=".jar"
BUILD=""
FIRST=0
#################### Var Setting END ####################

cd ${BUILD_PATH}
##检查增量执行文件是否存在
if [ ! -e "build.properties" ]; then 
	echo $MSG_NOT_EXIST_PROPERTIES
	exit 0
fi

echo $RUNDATE
##检查增量清单是否存在
if [ ! -e "$INCFILE" ]; then 
	echo $MSG_NOT_EXIST_INCFILE
	exit 1
fi

# 读取增量描述文件，进行增量配置文件的处理
##逐行读取增量清单文件，根据空格，制表符，换行符进行分割读取
##将SmartTeller9\\trans下需要编译的进行编译
for line in $(cat ${INCFILE})
do 
    if [[ "$line" =~ "${strA}" ]]
    then
	#echo "包含SmartTeller9\trans"
	if [[ "$line" =~ "${strB}" ]]
	then
		echo "包含jar"
		if test $FIRST -ne 0;then
			echo "不是第一次"
			BUILD=${BUILD},${line}
		else
			echo "是第一次"
			BUILD="$line"
			FIRST=1
			echo "第一次"$BUILD
		fi	
	else
		echo "不包含jar"
	fi
    else
	echo "不包含SmartTeller9\trans"
    fi
done

# 进行增量交易的编译

# 进行增量抽取工作

# 进行增量包的发布
