#!/bin/bash
source ~/.bashrc

echo **********************************************************
echo **                                                      **
echo **           EnsembleOM Ins Deploy Shell                **
echo **              http://www.dcits.com                    **
echo **            author:zhngjig@dcits.com                  **
echo **                                                      **
echo **********************************************************
# 脚本说明：
# 部署包备份，在部署的相应服务器上进行备份，其它位置不做备份
# 
# 1、将全量包在指定目录解压并创建versionid
# 2、停止当前应用服务
# 3、备份原应用包
# 4、启动服务：
echo "开始EnsembleOM版本部署..."
######## Var Setting START ########
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

DCITS_HOME=/app/dcits
APP_NAME=EnsembleOM
APP_HOME=${DCITS_HOME}/${APP_NAME}
UNZIP_NAME=ensemble-om-1.0.4-SNAPSHOT
VERSION_ID=App_${TAG_NAME}
SOURCE=EnsembleOM.zip
BACKUP_HOME=${DCITS_HOME}/backup/${APP_NAME}
ZIP_HOME=${BACKUP_HOME}/${TAG_NAME}/target
######## Var Setting END ########

######## Function START ########
# 检查应用当前状态
CheckAppState() {
    PID_APP=`/usr/sbin/lsof -n -P -t -i :${PORT_APP}`
    echo 'PID_APP: ' ${PID_APP}
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
    if [[ -d ${APP_NAME} ]];then
        versionNum=`cat ${DCITS_HOME}/${APP_NAME}/versionid.txt`
        tar -czf ${BACKUP_HOME}/${versionNum}-end.tar.gz ${APP_NAME}
    fi  
}

##删除更新前的jar包
DELETE_LIST_OPTION(){
cat $1 | while read line
do
echo 'remove' ${DCITS_HOME}/${APP_NAME}/${line}
rm  ${DCITS_HOME}/${APP_NAME}/${line}
done
}
######## Function END ########

# 检查并停止应用，以备部署新应用
echo "开始停止原应用....."
CheckStopState
if [ ${APP_RUN_STATUS} -ne 0 ];then
    echo 'App stopping ...'
    sh ${DCITS_HOME}/${APP_NAME}/bin/stop.sh
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
echo "备份原应用包"
BACKUP_OLD_APP

##将原始应用包更名，移动到backup，删除过渡文件夹
cd ${ZIP_HOME}
unzip ${SOURCE}
mv ${ZIP_HOME}/${APP_NAME}/deleteList.txt ${APP_HOME}
mv ${SOURCE} ${VERSION_ID}.zip
mv ${VERSION_ID}.zip ${BACKUP_HOME}
rm -rf ${BACKUP_HOME}/${TAG_NAME}


# 按照deleteList.txt列表进行删除jar包
DELETE_LIST_OPTION ${APP_HOME}/deleteList.txt

#解压应用包，并创建versionid.txt
echo "开始解压应用包并创建versionid.txt"
cd ${BACKUP_HOME}
unzip -o -d ${DCITS_HOME}  ${BACKUP_HOME}/${VERSION_ID}.zip
echo ${VERSION_ID} > ${APP_HOME}/versionid.txt
echo ${VERSION_ID} >> ${APP_HOME}/version_list.txt

#echo "替换配置模板......"
#cd $DCITS_HOME
#tar -zxvf ~/backup/Template/omconf.tar.gz

# 新部署应用启动
echo '启动应用，App starting ...'
sh ${DCITS_HOME}/${APP_NAME}/bin/start.sh
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
            echo ${MSG_START_SUCCESS}
            break
        else
            sh ${DCITS_HOME}/${APP_NAME}/bin/stop.sh
            echo 'Retry App starting ...'
            sh ${DCITS_HOME}/${APP_NAME}/bin/start.sh
        fi
        CHECK_INTERVAL ${CHECK_TIME}
    done
    if [ ${APP_RUN_STATUS} -eq 0 ];then
        # 新部署应用多次尝试启动失败，未知异常待人工检查状态
        echo ${MSG_STATUS_ERROR}
    fi
fi

echo "结束EnsembleOM版本部署..."