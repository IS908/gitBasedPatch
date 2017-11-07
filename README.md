## ModelBank协助版本控制

#### TODO列表
- 根据 git log commitId 列出对应该次提交的文件提交列表
- 根据git status给出的路径，将其拆分为路径与包路径，示例如下：
    - git status显示路径：Ensemble/modules/ensemble-cd/api/src/main/java/com/dcits/ensemble/cd/model/mbsdcore/Core12006002In.java
        - path：Ensemble/modules/ensemble-cd/api/src/main/java
        - package：com/dcits/ensemble/cd/model/mbsdcore/Core12006002In.java
    - git status显示路径：Ensemble/modules/ensemble-cd/api/src/main/resources/META-INF/spring/galaxy-consumer.xml
        - path：Ensemble/modules/ensemble-cd/api/src/main/resources
        - package：META-INF/spring/galaxy-consumer.xml
    - 注：path + package 为开发人员便于定位具体文件位置；package为maven打包的jar包中的class路径
- git log中显示文件路径，确定该文件打包后所在的jar包名称
    - 找到该文件所在模块的pom.xml文件
    - 匹配该文件下的groupId + artifactId + version + packaging这四要素，拼接后便是maven打包的jar名称。
    
