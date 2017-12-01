#!/bin/bash

# Teller签名脚本

export ANT_HOME=${WORKSPACE}/tools/ant/
cd ${WORKSPACE}
mkdir SmartTeller9
unzip -o -d ${WORKSPACE}/SmartTeller9 ${WORKSPACE}/SmartTellerV9.4.5.zip

SIGN_PATH=${WORKSPACE}/SmartTeller9/InteractiveFrame_ClientResource/application
cd ${SIGN_PATH}
ant -f sign.xml

cd ${WORKSPACE}/SmartTeller9
zip -r ${WORKSPACE}/App_${TAG_NAME}.zip ./*
