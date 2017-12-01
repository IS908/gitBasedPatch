## ModelBank脚本说明
> 基于Jenkins是核心部署脚本相关说明

- FAT_ModelBank_Full
    * 1、勾选参数化构建过程 --> String Parameter
    * 2、源码管理勾选Git
        - Pepositories
        - Branches to build
        - Additional Behaviours
    * 3、构建环境勾选 Delete wordspace before build starts
    * 4、Pre Steps
        - Execute shell
````bash

cd $WORKSPACE
git checkout -b develop origin/develop
git pull http://jenkins:digital1@57.25.2.187:8082/dcits/ModelBank.git

cd $WORKSPACE/SmartEnsemble
git checkout -b develop origin/develop
git pull http://jenkins:digital1@57.25.2.187:8082/dcits/SmartEnsemble.git
git reset --hard
git tag -a "SmartEnsemble_"${TAG_NO} -m "Jenkins Git plugin tagging with SmartEnsemble"
git push http://jenkins:digital1@57.25.2.187:8082/dcits/SmartEnsemble.git "SmartEnsemble_"${TAG_NO}

cd $WORKSPACE
git reset --hard

````

    * 5、Build --> maven配置
    * 6、Post Steps
        - Run only if build succeeds
        - SSH Server 设置
打发