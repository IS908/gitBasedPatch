#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **          Teller9 Ins Deploy Shell                    **
echo **             http://www.dcits.com                     **
echo **            author zhangjig@dcits.com                 **
echo **********************************************************

#不同环境下脚本修改指南
#   Var Setting中修改：
#       1、PORT_APP 端口号
#       2、DCITS_HOME 应用部署主目录
#  非阜新银行项目，请注释掉START_TELLER函数中：sed -i 's/ssoindex/fxindex/g' ./configuration/config.ini

########## Var Setting START ##########
# 应用端口号，注意需加单引号
PORT_APP='9080'
# 启动应用检查时间间隔设定(单位：10秒)
CHECK_TIME=18

# 应用状态 APP_RUN_STATUS - 0：停止状态；1：启动状态
APP_RUN_STATUS=-10
MSG_START_SUCCESS='Teller应用启动状态'
MSG_STOP_SUCCESS='Teller应用停止状态'
MSG_STOP_FAILD='Teller应用停止失败，请人工停止原应用并部署'
MSG_STATUS_ERROR='Teller应用状态未知,请人工确认当前状态'

DCITS_HOME=/app/dcits
APP_HOME=${DCITS_HOME}
CACHE_HOME=${DCITS_HOME}/SmartTeller9/configuration
BACKUP_HOME=${DCITS_HOME}/backup/SmartTeller9
ZIP_HOME=${BACKUP_HOME}
TAG_NAME=SmartTeller9_Ins_${TAG_NO}
VERSION_ID=App_${TAG_NAME}
TARGET=${VERSION_ID}.zip
########## Var Setting END ##########

######## Function START ########
echo "开始SmartTeller9增量版本部署"
# 检查应用当前状态
CheckAppState() {
    PID_APP=`/usr/sbin/lsof -n -P -t -i :${PORT_APP}`
    echo 'PID_APP:' ${PID_APP}
    if [[ -z "${PID_APP}" ]] ; then
        APP_RUN_STATUS=0
    else 
        APP_RUN_STATUS=1
    fi
    echo 'APP_RUN_STATUS:' ${APP_RUN_STATUS}
}

# 检查应用是否停止 并返回状态码：停止成功:1；停止失败:0
CheckStopState(){
    CheckAppState
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 成功停止
        echo ${MSG_STOP_SUCCESS}
    fi
}

# 检查应用是否启动 并返回状态码：启动成功:1；启动失败:0
CheckStartState() {
    CheckAppState
    if [ ${APP_RUN_STATUS} -eq 1 ]
    then
        echo ${MSG_START_SUCCESS}
    else
        APP_RUN_STATUS=-1
        echo ${MSG_STATUS_ERROR}
    fi
}

CHECK_INTERVAL() {
    for i in `seq $1`
    do
        sleep 10s
        echo 'check' ${i}0s
        CheckAppState
        if [ ${APP_RUN_STATUS} -ne 0 ];then
            break
        fi
    done
}

# 启动teller应用
START_TELLER() {
    cd ${APP_HOME}/SmartTeller9
    sed -i 's/ssoindex/fxindex/g' ./configuration/config.ini
    chmod 755 ${APP_HOME}/SmartTeller9/*
    sh start
}

# 新应用发布成功后，备份被替换的旧应用（主要为日志备份）
BACKUP_OLD_APP() {
    cd ${APP_HOME}
    if [[ -d SmartTeller9 ]];then
        versionNum=`cat ${APP_HOME}/SmartTeller9/versionid.txt`
        tar -czf ${BACKUP_HOME}/${versionNum}-end.tar.gz  SmartTeller9
        # rm ${BACKUP_HOME}/${versionNum}.zip
    fi
}

TAR_TEMPLETE() {
    cd $DCITS_HOME
    echo replace conf
    tar -zxvf ~/backup/Template/telconf.tar.gz
}
######## Function END ########

# 检查并停止应用
echo "停止原应用......"
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo 'Teller stopping ...'
    sh ${APP_HOME}/SmartTeller9/stop.sh
	CHECK_INTERVAL 1
    for i in `seq 3`
    do   
        CheckStopState
        if [ ${APP_RUN_STATUS} -eq 0 ];then
            break
        else
            echo 'Retry Teller stopping ...'
            sh ${APP_HOME}/SmartTeller9/stop.sh skip
        fi
        CHECK_INTERVAL 3
    done
    if [ ${APP_RUN_STATUS} -ne 0 ];then
        # 停止失败
        echo ${MSG_STOP_FAILD}
        exit
    fi
fi

# 备份原应用包
echo "备份原应用......"
BACKUP_OLD_APP

# 备份全量包，并解压增量包 DONE
echo "开始解压增量压缩包..."
cd ${BACKUP_HOME}
unzip -o -d ${APP_HOME}  ${BACKUP_HOME}/${TARGET}
echo ${VERSION_ID} > ${APP_HOME}/SmartTeller9/versionid.txt
echo ${VERSION_ID} >> ${APP_HOME}/SmartTeller9/version_list.txt

# 部署新的应用包，并启动新应用
echo '新应用启动,Teller starting ...'
#TAR_TEMPLETE
START_TELLER
CHECK_INTERVAL ${CHECK_TIME}

# 检查新部署应用是否启动成功
CheckStartState
if [ ${APP_RUN_STATUS} -eq 1 ];then
    # 新应用启动
    echo ${MSG_START_SUCCESS}
else
    for i in `seq 5`
    do   
        CheckStartState
        if [ ${APP_RUN_STATUS} -eq 1 ];then
            # 新应用启动
            echo "Start successful ..."
            echo ${MSG_START_SUCCESS}
            break
        else
            echo 'Retry Teller starting ...'
            START_TELLER
        fi
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi

echo "结束SmartTeller9增量版本部署..."