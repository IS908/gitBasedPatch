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
# 全量包：~/backup/ModelBank/App_${TAG_NAME}.tar.gz
# 增量包：~/backup/ModelBank/App_${TAG_NAME}.zip
# 其中 ${TAG_NAME} 与 GitLab 上的 Tag 保持一致
# 
# 1、停止当前应用服务
# 2、备份增量包到指定目录
# 3、解压增量包并安装增量包
# 4、启动服务

######## Var Setting START ########
#run_status=`netstat -anp|grep 9001|awk '{printf $7}'|cut -d/ -f1`
# 应用端口号，注意需加单引号
PORT_APP='9001'
# 启动应用检查时间间隔设定(单位：秒)
CHECK_TIME=24

# 应用状态 APP_RUN_STATUS - 0：停止状态；1：启动状态
APP_RUN_STATUS=-10
MSG_START_SUCCESS='APP应用启动状态'
MSG_STOP_SUCCESS='APP应用停止状态'
MSG_STOP_FAILD='APP应用停止失败，请人工停止原应用并部署'
MSG_STATUS_ERROR='APP应用状态未知,请人工确认当前状态'

#################################
### 来自Jenkins的变量TAG_NAME ###
#################################

APP_NAME=ModelBank
DCITS_HOME=/app/dcits
ENSEMBLE_HOME=${DCITS_HOME}
BACKUP_HOME=${DCITS_HOME}/backup/${APP_NAME}
TAG_NAME=ModelBank_Ins_${TAG_NO}
BACKUP_TEMP=${BACKUP_HOME}/${TAG_NAME}
ZIP_HOME=${BACKUP_TEMP}/modules/modelBank-all-integration/target
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

# 间隔状态检查
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

# 新应用发布成功后，备份被替换的旧应用（作为增量替换前的全量状态，以便增量发布后可回退上一个版本）
BACKUP_OLD_APP() {
    versionNum=`cat ${ENSEMBLE_HOME}/${APP_NAME}-old/VERSIONID`
    cd ${DCITS_HOME}
    tar -czf ${BACKUP_HOME}/${versionNum}-end.tar.gz ${APP_NAME}-old
    rm -rf ${ENSEMBLE_HOME}/${APP_NAME}-old

    # 部署成功，更新VERSIONID，并在VERSION_LIST中追加增量版本号
    echo App_${TAG_NAME} > ${DCITS_HOME}/${APP_NAME}/VERSIONID
    echo App_${TAG_NAME} >> ${DCITS_HOME}/${APP_NAME}/VERSION_LIST
}
######## Function END ########

# 移动增量包到相应备份目录下
mv  ${ZIP_HOME}/app_modelbank_ins.zip  ${BACKUP_HOME}/App_${TAG_NAME}.zip
cd ${BACKUP_TEMP}
unzip ${BACKUP_HOME}/App_${TAG_NAME}.zip
mv modelBank-integration ${APP_NAME}

# 检查并停止应用，以备部署新应用
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo 'App stopping ...'
    sh ${ENSEMBLE_HOME}/${APP_NAME}/bin/stop.sh
	CHECK_INTERVAL 1
    for i in `seq 3`
    do   
        CheckStopState
        if [ ${APP_RUN_STATUS} -eq 0 ];then
            break
        fi
        CHECK_INTERVAL 6
    done
    if [ ${APP_RUN_STATUS} -ne 0 ];then
        # 停止失败
        echo ${MSG_STOP_FAILD}
        exit
    fi
fi

# 备份原应用包
cd ${ENSEMBLE_HOME}
if [[ -d ${ENSEMBLE_HOME}/${APP_NAME}-old/ ]];then
    rm -rf ${ENSEMBLE_HOME}/${APP_NAME}-old
fi

if [[ -d ${ENSEMBLE_HOME}/${APP_NAME}/ ]];then
    cp -r ${ENSEMBLE_HOME}/${APP_NAME} ${ENSEMBLE_HOME}/${APP_NAME}-old
fi

# 部署增量应用包，并启动应用
mv -f ${BACKUP_TEMP}/${APP_NAME}/lib/* ${ENSEMBLE_HOME}/${APP_NAME}/lib/
rm -rf ${BACKUP_TEMP}
echo 'App starting ...'
sh ${ENSEMBLE_HOME}/${APP_NAME}/bin/start.sh
CHECK_INTERVAL ${CHECK_TIME}

# 检查新部署应用是否启动成功
CheckStartState
if [ ${APP_RUN_STATUS} -eq 1 ];then
    # 新应用启动，备份并删除旧应用
    BACKUP_OLD_APP
    echo ${MSG_START_SUCCESS}
else
    for i in `seq 5`
    do   
        CheckStartState
        if [ ${APP_RUN_STATUS} -eq 1 ];then
            # 新应用启动，备份并删除旧应用
            BACKUP_OLD_APP
            echo ${MSG_START_SUCCESS}
            break
        else
            echo 'Retry App starting ...'
            sh ${ENSEMBLE_HOME}/${APP_NAME}/bin/start.sh
        fi
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi
