#!/bin/bash
source .bashrc

# tags/${TAG_NO}

TELLER_HOME=/app/dcits/ensemble
DEPLOY_DIR=${TELLER_HOME}/SmartTeller9
DEPLOY_TIME=$(date +%Y%m%d_%H%M)

cd $TELLER_HOME

if [[ -d ./SmartTeller9/ ]];then

    echo Stop teller ...
    cd SmartTeller9
    ./stop.sh
    cd $TELLER_HOME

    echo Backup last version SmartTeller9......
    #zip -qr ~/backup/SmartTeller9/SmartTeller9_$DEPLOY_TIME.zip SmartTeller9/
    echo Backup last version finished!

    rm -rf ./SmartTeller9/*
fi

cd $DEPLOY_DIR
echo ${TAG_NO}
unzip ~/tags/${TAG_NO}/SmartTellerV9.4.5.zip

cd $TELLER_HOME
echo replace conf
tar -zxvf telconf.tar.gz

cd $DEPLOY_DIR/configuration
sed -i 's/ssoindex/fxindex/g' config.ini

cd $DEPLOY_DIR
echo Start SmartTeller9
chmod 755 *
./start