### TODO - 列表

- 核心、柜面、OM、报表四个模块的全量脚本编写 - DONE
- 核心、报表、OM三个模块增量抽取工具的编写 - Doing
- 柜面增量抽取Shell脚本工具的编写 - Doing
- 核心、柜面、OM、报表四个模块的增量脚本编写 - Doing
- Jenkins中单独Job进行ZK、Redis相关的健康检查 - TODO

### 会议记录：要优化的点
- Jenkins备份、应用服务器备份，重复工作
    * 只在部署时在相应服务器上在指定位置备份一次
- Jenkins中配置shell比较乱
    * 规范Shell，添加相关事件检查校验
- OMS与Jenkins
    * 待定

- 分支基线的规划及管理
    * 协助周恒出文档化规范
- GIT中Tag与目标码
    * 短期在相应部署服务器端备份目标码部署包，长期目标将其在VPS-GIT上做备份，支持历史记录备注及下载
- GitLab安装规范化
    * RPM安装
    * 汉化
- Jenkins的jobs名称规范 ：环境\_IP\_应用名\_分支基线
