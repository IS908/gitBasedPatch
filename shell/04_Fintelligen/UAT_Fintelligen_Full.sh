#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **            Fintelligen Deploy Shell                  **
echo **              http://www.dcits.com                    **
echo **            author:chenkunh@dcits.com                 **
echo **                                                      **
echo **********************************************************

######## Var Setting START ########
#run_status=`netstat -anp|grep 9001|awk '{printf $7}'|cut -d/ -f1`
# 应用端口号，注意需加单引号
PORT_APP='8001'
# 启动应用检查时间间隔设定(单位：秒)
CHECK_TIME=9

# 应用状态 APP_RUN_STATUS - 0：停止状态；1：启动状态
APP_RUN_STATUS=-10
MSG_START_SUCCESS='APP应用启动状态'
MSG_STOP_SUCCESS='APP应用停止状态'
MSG_STOP_FAILD='APP应用停止失败，请人工停止原应用并部署'
MSG_STATUS_ERROR='APP应用状态未知,请人工确认当前状态'

APP_NAME=Fintelligen
APP_HOME=/app/dcits
BACKUP_HOME=${APP_HOME}/backup/${APP_NAME}
BACKUP_TEMP=${BACKUP_HOME}/${TAG_NAME}

APP_OLD_NAME=fintelligen-integration
TAR_GZ_FILE=${BACKUP_TEMP}/modules/fintelligen-integration/online-all-integration/target/fintelligen-integration-assembly.tar.gz
######## Var Setting END ########

######## Function START ########
# 检查应用当前状态
CheckAppState() {
    PID_APP=`/usr/sbin/lsof -n -P -t -i :${PORT_APP}`
    echo 'PID_APP: ' ${PID_APP}
    APP_RUN_STATUS=`ps -ef | grep ${PID_APP} | grep -v 'grep' | wc -l`
    echo 'APP_RUN_STATUS: ' ${APP_RUN_STATUS}
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
        echo 'check' ${i}0s
        sleep 10s
        CheckAppState
        if [ ${APP_RUN_STATUS} -ne 0 ];then
            break
        fi
    done
}

# 新应用发布成功后，备份被替换的旧应用（主要为日志备份）
BACKUP_OLD_APP() {
    versionNum=`cat ${APP_HOME}/${APP_NAME}-old/versionid.txt`
    cd ${APP_HOME}
    tar -czf ${BACKUP_HOME}/${versionNum}-end.tar.gz ${APP_NAME}-old/
#    需做判定压缩包是否生成相应的压缩包，确认后进行旧版文件夹删除
    rm -rf ${APP_HOME}/${APP_NAME}-old
}

START_APP() {
#    cd ${APP_HOME}
#    tar -zxf ${APP_HOME}/backup/Template/finmodel.tar.gz
    echo ${APP_NAME} starting ...
    sh ${APP_HOME}/${APP_NAME}/bin/start.sh
}
######## Function END ########

# 备份全量包
echo backup new App_${TAG_NAME}.tar.gz
mv ${TAR_GZ_FILE} ${BACKUP_HOME}/App_${TAG_NAME}.tar.gz
rm -rf ${BACKUP_TEMP}/modules
cd ${BACKUP_TEMP}
tar -zxf ${BACKUP_HOME}/App_${TAG_NAME}.tar.gz
mv ${BACKUP_TEMP}/${APP_OLD_NAME} ${BACKUP_TEMP}/${APP_NAME}
# 创建versionid.txt到部署包，与源码的Tag相对应
echo App_${TAG_NAME} > ${BACKUP_TEMP}/${APP_NAME}/versionid.txt
echo App_${TAG_NAME} > ${BACKUP_TEMP}/${APP_NAME}/version_list.txt

# 检查并部署新应用，以备部署新应用
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo ${APP_NAME} stopping ...
    sh ${APP_HOME}/${APP_NAME}/bin/stop.sh
	sleep 10s
    for i in `seq 3`
    do   
        CheckStopState
        if [ ${APP_RUN_STATUS} -eq 0 ];then
            break
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
echo backup old ${APP_NAME} ...
if [[ -d ${APP_HOME}/${APP_NAME}-old/ ]];then
    rm -rf ${APP_HOME}/${APP_NAME}-old
fi

if [[ -d ${APP_HOME}/${APP_NAME}/ ]];then
    mv ${APP_HOME}/${APP_NAME} ${APP_HOME}/${APP_NAME}-old
fi

# 部署新的应用包，并启动新应用
mv ${BACKUP_TEMP}/${APP_NAME} ${APP_HOME}
rm -rf ${BACKUP_TEMP}
START_APP
CHECK_INTERVAL ${CHECK_TIME}

# 检查新部署应用是否启动成功
echo check start state ...
CheckStartState
if [ ${APP_RUN_STATUS} -eq 1 ];then
    # 新应用启动，删除旧应用
    rm -rf ${APP_HOME}/${APP_NAME}-old
    echo ${MSG_START_SUCCESS}
else
    for i in `seq 5`
    do   
        CheckStartState
        if [ ${APP_RUN_STATUS} -eq 1 ];then
            # 新应用启动，删除旧应用
            rm -rf ${APP_HOME}/${APP_NAME}-old
            echo ${MSG_START_SUCCESS}
            break
        else
            echo 'Retry App starting ...'
            START_APP
        fi
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi
