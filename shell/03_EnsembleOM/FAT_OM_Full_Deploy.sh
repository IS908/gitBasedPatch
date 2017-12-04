#!/bin/bash
source ~/.bashrc

# Jenkins 配置：
# Source files: **/ensemble-om-1.0.4-SNAPSHOT-assembly.zip
# Remote directory: backup/EnsembleOM/


echo **********************************************************
echo **                                                      **
echo **           EnsembleOM Full Deploy Shell               **
echo **              http://www.dcits.com                    **
echo **            author:zhngjig@dcits.com                  **
echo **                                                      **
echo **********************************************************
# 脚本说明：
# 部署包备份，在部署的相应服务器上进行备份，其它位置不做备份
# 其中 ${TAG_NO} 与 GitLab 上的 Tag 保持一致
# 
# 1、停止当前应用服务
# 2、备份全量包到指定目录并解压
# 3、重命名旧的应用包，部署新的应用包
# 4、启动服务：
# 5、若启动成功，则删除旧的应用包
# 6、若启动失败，保留旧的应用包

######## Var Setting START ########
#run_status=`netstat -anp|grep 18889|awk '{printf $7}'|cut -d/ -f1`
# 应用端口号，注意需加单引号
PORT_APP='18889'
# 启动应用检查时间间隔设定(单位：10秒)
CHECK_TIME=30

# 应用状态 APP_RUN_STATUS - 0：停止状态；1：启动状态
APP_RUN_STATUS=-10
MSG_START_SUCCESS='APP应用启动状态'
MSG_STOP_SUCCESS='APP应用停止状态'
MSG_STOP_FAILD='APP应用停止失败，请人工停止原应用并部署'
MSG_STATUS_ERROR='APP应用状态未知,请人工确认当前状态'

###############################
### 来自Jenkins的变量TAG_NO ###
###############################
DCITS_HOME=/app/dcits
APP_NMAE=EnsembleOM
UNZIP_NAME=ensemble-om-1.0.4-SNAPSHOT
TAG_NAME=App_EnsembleOM_Full_${TAG_NO}
SOURCE=ensemble-om-1.0.4-SNAPSHOT-assembly.zip
NEW_SOURCE=${TAG_NAME}.zip
BACKUP_HOME=${DCITS_HOME}/backup/${APP_NMAE}
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
    echo "tar备份开始"
    cd ${DCITS_HOME}
    versionNum=`cat ${DCITS_HOME}/${APP_NMAE}-old/versionid.txt`
    tar -czf ${BACKUP_HOME}/${versionNum}-end.tar.gz ${APP_NMAE}-old
    rm -rf ${DCITS_HOME}/${APP_NMAE}-old
}
######## Function END ########

#更新原应用包名称，加TAG_NAME到应用包
echo "更改zip名称并创建versionid.txt"
cd ${BACKUP_HOME}/target 
mv  ${SOURCE}   ${BACKUP_HOME}/${NEW_SOURCE}
cd ../
rm -rf target
unzip -q ${NEW_SOURCE}
mv ${UNZIP_NAME} ${TAG_NAME}
# 创建versionid.txt到部署包，与源码的zip相对应
echo "创建versionid.txt到部署包"
echo ${TAG_NAME} > ${BACKUP_HOME}/${TAG_NAME}/versionid.txt
echo ${TAG_NAME} > ${BACKUP_HOME}/${TAG_NAME}/version_list.txt
#将应用包更名
echo "将应用包更名:"${TAG_NAME}
mv ${TAG_NAME} ${APP_NMAE}

# 检查并停止应用，以备部署新应用
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo 'App stopping ...'
    sh ${DCITS_HOME}/${APP_NMAE}/bin/stop.sh
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

# 原应用包文件夹重命名
cd ${DCITS_HOME}
if [[ -d ${DCITS_HOME}/${APP_NMAE}-old/ ]];then
    rm -rf ${DCITS_HOME}/${APP_NMAE}-old
fi

echo "备份原应用..."
if [[ -d ${DCITS_HOME}/${APP_NMAE}/ ]];then
    mv ${DCITS_HOME}/${APP_NMAE} ${DCITS_HOME}/${APP_NMAE}-old
fi

# 部署新的应用包到指定目录，并删除临时文件夹
echo "移动应用到部署目录"
mv ${BACKUP_HOME}/${APP_NMAE} ${DCITS_HOME}

cd $DCITS_HOME
echo replace conf
tar -zxvf ~/backup/Template/omconf.tar.gz

# 新部署应用启动
echo 'App starting ...'
sh ${DCITS_HOME}/${APP_NMAE}/bin/start.sh
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
            sh ${DCITS_HOME}/${APP_NMAE}/bin/stop.sh
            echo 'Retry App starting ...'
            sh ${DCITS_HOME}/${APP_NMAE}/bin/start.sh
        fi
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi
