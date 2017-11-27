
#run_status=`netstat -anp|grep 9001|awk '{printf $7}'|cut -d/ -f1`
PORT_APP='9001'

APP_RUN_STATUS=-10
MSG_START_SUCCESS='   \t    APP应用启动状态'
MSG_STOP_SUCCESS='   \t    APP应用停止状态'
MSG_STATUS_ERROR='   \t    APP应用状态未知,请人工确认当前状态'

CheckFrontStat(){
    PID_APP=`/usr/sbin/lsof -t -i:${PORT_APP}`
    echo Galaxy App Pid: ${PID_APP}
    APP_RUN_STATUS=`ps -ef |grep ${PID_APP} |grep -v 'grep'|wc -l`
    echo ${APP_RUN_STATUS}

     if [ ${APP_RUN_STATUS} -eq 0 ]
     then
        echo ${MSG_STOP_SUCCESS}
     elif [ ${APP_RUN_STATUS} -eq 1 ]
     then
        echo ${MSG_START_SUCCESS}
     else
        APP_RUN_STATUS=-1
        echo ${MSG_STATUS_ERROR}
     fi
}
CheckFrontStat