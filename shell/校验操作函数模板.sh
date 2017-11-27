
#run_status=`netstat -anp|grep 9001|awk '{printf $7}'|cut -d/ -f1`
PORT_APP='9001'
PID_APP=`lsof -t -i:$PORT_APP`
APP_RUN_STATUS=-10
MSG_START_SUCCESS='   \t    APP应用启动状态'
MSG_STOP_SUCCESS='   \t    APP应用停止状态'
MSG_STATUS_ERROR='   \t    APP应用状态未知,请人工确认当前状态'
echo Galaxy App Pid: $PID_APP
echo $APP_RUN_STATUS

CheckFrontStat(){

     APP_RUN_STATUS=`ps -ef |grep $PID_APP |grep -v 'grep'|wc -l`

     if [ $APP_RUN_STATUS -eq 0 ]
     then
        echo $APP_RUN_STATUS
        echo $MSG_STOP_SUCCESS
     elif [ $APP_RUN_STATUS -eq 1 ]
     then
        echo $APP_RUN_STATUS
        echo $MSG_START_SUCCESS
     else
        APP_RUN_STATUS=-1
        echo $APP_RUN_STATUS
        echo $MSG_STATUS_ERROR
     fi

}
CheckFrontStat