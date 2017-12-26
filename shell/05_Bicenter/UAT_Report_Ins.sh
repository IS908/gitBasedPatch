#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **          Fintelligen Report  Ins Build  Shell        **
echo **              http://www.dcits.com                    **
echo **            author:zhangjig@dcits.com                 **
echo **                                                      **
echo **********************************************************

# 脚本思路描述：
# 1、根据开发人员提交txt增量清单描述文件，逐行读取拷贝到增量文件夹
# 2、将增量文件夹压缩
#
echo "开始Fintelligen Report增量版本构建"
######## Var Setting START ########
FILE_PATH=`pwd`
endfix=".xls"
DOCUMENT=UAT_${TAG_NAME}
TARGET=${DOCUMENT}.zip
BUILD_PATH=${FILE_PATH}
INCFILE=${BUILD_PATH}/reportPatch/report_${INSFILE_NAME}.txt
MSG_NOT_EXIST_INCFILE='不存在增量清单文件，不需要进行增量版本抽取'
######## Var Setting END ########

cd ${BUILD_PATH}
##删除同名zip增量包
if [ -e "$TARGET" ]; then 
	rm "$TARGET"
fi
##删除增量文件夹
if [[ -d ${DOCUMENT} ]];then
    rm -rf ${DOCUMENT}
fi
mkdir ${DOCUMENT}

##根据增量清单进行抽取
if [ -e "$INCFILE" ]; then 
	echo "增量清单名称是："report_${INSFILE_NAME}.txt
    sed -i 's/\r//g'  ${INCFILE}
    for line in $(cat ${INCFILE})
    do 
        echo "增量文件名称是："$line
        if [[ "$line" =~ "${endfix}" ]]
        then
            echo "包含xls文件"
            cp -r ${BUILD_PATH}/report/"$line" ${BUILD_PATH}/${DOCUMENT}
        else
            echo "不包含xls文件"
        fi
    done
else
    echo ${MSG_NOT_EXIST_INCFILE}
    exit 1
fi

echo "创建deleteList.txt"
cd ${BUILD_PATH}/${DOCUMENT}
echo > deleteList.txt

# 进行增量report压缩
cd ${BUILD_PATH}
zip -q -r ${TARGET}  ${DOCUMENT}

echo "结束Fintelligen Report增量版本构建。。。"
