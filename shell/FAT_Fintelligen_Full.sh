#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **             ModelBank Deploy Shell                   **
echo **              http://www.dcits.com                    **
echo **            author:chenkunh@dcits.com                 **
echo **                                                      **
echo **********************************************************
# 脚本说明：
# 部署包备份，在部署的相应服务器上进行备份，其它位置不做备份，
# 备份目录：
# 全量包：~/backup/Fintelligen/Fintelligen_Full_${TAG_NO}/*.tar.gz 
# 增量包：~/backup/Fintelligen/Fintelligen_Ins_${TAG_NO}/*.zip
# 其中 ${TAG_NO} 与 GitLab 上的 Tag 保持一致
# 
# 1、停止当前应用服务
# 2、备份全量包到指定目录并解压
# 3、重命名旧的应用包，部署新的应用包
# 4、启动服务：
# 5、若启动成功，则删除旧的应用包
# 6、若启动失败，保留旧的应用包

#################### Var Setting START ####################
#run_status=`netstat -anp|grep 9001|awk '{printf $7}'|cut -d/ -f1`
# 应用端口号，注意需加单引号
PORT_APP=''
# 启动应用检查时间间隔设定(单位：秒)
CHECK_TIME=24

# 应用状态 APP_RUN_STATUS - 0：停止状态；1：启动状态
APP_RUN_STATUS=-10
MSG_START_SUCCESS='APP应用启动状态'
MSG_STOP_SUCCESS='APP应用停止状态'
MSG_STOP_FAILD='APP应用停止失败，请人工停止原应用并部署'
MSG_STATUS_ERROR='APP应用状态未知,请人工确认当前状态'

DCITS_HOME=/app/dcits
FINTELLIGEN_HOME=${DCITS_HOME}/ensemble
BACKUP_HOME=${DCITS_HOME}/backup/Fintelligen/Fintelligen_Full_${TAG_NO}
TAR_GZ_HOME=${BACKUP_HOME}/modules/fintelligen-integration/online-all-integration/target
#################### Var Setting END ####################

#################### Function START ####################
# 检查应用是否停止 并返回状态码：停止成功:1；停止失败:0
CheckStopState(){
    OLD_PID_APP=`/usr/sbin/lsof -n -P -t -i :${PORT_APP}`
	echo 'OLD_PID_APP:' ${OLD_PID_APP}
    APP_RUN_STATUS=`ps -ef | grep ${OLD_PID_APP} | grep -v 'grep' | wc -l`
	echo 'APP_RUN_STATUS:' ${APP_RUN_STATUS}

    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 成功停止
        echo ${MSG_STOP_SUCCESS}
    fi
}

# 检查应用是否启动 并返回状态码：启动成功:1；启动失败:0
CheckStartState() {
    PID_APP=`/usr/sbin/lsof -n -P -t -i :${PORT_APP}`
    echo 'PID_APP:' ${PID_APP}
    APP_RUN_STATUS=`ps -ef | grep ${PID_APP} | grep -v 'grep' | wc -l`
    echo 'APP_RUN_STATUS:' ${APP_RUN_STATUS}
    if [ ${APP_RUN_STATUS} -eq 1 ]
    then
        echo ${MSG_START_SUCCESS}
    else
        APP_RUN_STATUS=-1
        echo ${MSG_STATUS_ERROR}
    fi
}
# 睡眠时间设定
CHECK_INTERVAL() {
    for i in `seq $1`
    do
        sleep 10s
        echo 'check' ${i}
    done
}
#################### Function END ####################

# 备份全量包
cd ${BACKUP_HOME}
mv ${TAR_GZ_HOME}/fintelligen-integration-assembly.tar.gz ${BACKUP_HOME}
rm -rf ${BACKUP_HOME}/modules
tar -zxf ${BACKUP_HOME}/fintelligen-integration-assembly.tar.gz

# 检查并部署新应用，以备部署新应用
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo 'App stopping ...'
    sh ${FINTELLIGEN_HOME}/fintelligen-integration/bin/stop.sh
	CHECK_INTERVAL 1
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
cd ${FINTELLIGEN_HOME}
if [[ -d ${FINTELLIGEN_HOME}/fintelligen-integration-old/ ]];then
    rm -rf ${FINTELLIGEN_HOME}/fintelligen-integration-old
fi

if [[ -d ${FINTELLIGEN_HOME}/fintelligen-integration/ ]];then
    mv ${FINTELLIGEN_HOME}/fintelligen-integration ${FINTELLIGEN_HOME}/fintelligen-integration-old
fi

# 部署新的应用包，并启动新应用
mv ${BACKUP_HOME}/fintelligen-integration ${FINTELLIGEN_HOME}
echo 'App starting ...'
sh ${FINTELLIGEN_HOME}/fintelligen-integration/bin/start.sh
CHECK_INTERVAL ${CHECK_TIME}

# 检查新部署应用是否启动成功
CheckStartState
if [ ${APP_RUN_STATUS} -eq 1 ];then
    # 新应用启动，删除旧应用
    rm -rf ${FINTELLIGEN_HOME}/fintelligen-integration-old
    echo ${MSG_START_SUCCESS}
else
    for i in `seq 5`
    do   
        CheckStartState
        if [ ${APP_RUN_STATUS} -eq 1 ];then
            # 新应用启动，删除旧应用
            rm -rf ${FINTELLIGEN_HOME}/fintelligen-integration-old
            echo ${MSG_START_SUCCESS}
            break
        fi
        echo 'Retry App starting ...'
        sh ${FINTELLIGEN_HOME}/fintelligen-integration/bin/start.sh
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi
