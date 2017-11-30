#!/bin/bash
source .bashrc

cd ~/tags/${TAG_NO}/
mv modules/modelBank-all-integration/target/modelBank-integration-assembly.tar.gz ./
rm -rf modules

ENSEMBLE_HOME=/app/dcits
cd ${ENSEMBLE_HOME}
DEPLOY_TIME=$(date +%Y%m%d_%H%M)

if [[ -d ./modelBank-integration/ ]];then
    ./modelBank-integration/bin/stop.sh skip
    echo Start backup old application......
    zip -qr ~/backup/modelBank/modelBank-integration${DEPLOY_TIME}.zip modelBank-integration/

    echo Backup finished.
    rm -rf ./modelBank-integration
fi

tar -zxf ~/tags/${TAG_NO}/modelBank-integration-assembly.tar.gz
cd ${ENSEMBLE_HOME}

#echo replace modelbankconf
#tar -zxvf modelconf.tar.gz

#echo start app...
./modelBank-integration/bin/start.sh
