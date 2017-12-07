#!/bin/bash
source ~/.bashrc

PATCH_TOOL=${JENKINS_HOME}/../tools/patchTool

java -jar ${PATCH_TOOL}/dcits-ci.jar xml ${WORKSPACE} ${gitDir} ${sourceDir} ${targetDir} ${resultDir}
java -jar ${PATCH_TOOL}/dcits-ci.jar zip ${WORKSPACE} ${gitDir} ${sourceDir} ${targetDir} ${resultDir}

# WORKSPACE ：Jenkins的job目录
# gitDir    ：项目的.git日志文件夹
# sourceDir ：项目源码根路径
# clazzDir ：项目目标码（未压缩的）根路径
# resultDir ：增量描述文件及增量包存放路径（建议在target目录下，与全量包路径一致）
