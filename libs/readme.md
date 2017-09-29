##阿里云短信说明
###1. 如果你们公司有自己搭建仓库，可以把SDK里面提供的本地jar上传上去即可
- 或者你走你的本地jar依赖方式
- 如：
``` xml
<dependency>
<groupId>aliyun.com</groupId>
<artifactId>dysmsapi</artifactId>
<version>1.0.0</version>
<systemPath>${project.basedir}/lib/*.jar</systemPath>
</dependency>
```
###2.添加到本地maven
mvn install:install-file -Dfile=lib\aliyun-java-sdk-core-3.2.3.jar -DgroupId=aliyun.com -DartifactId=aliyun -Dversion=3.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=lib\aliyun-java-sdk-dysmsapi-1.0.0.jar -DgroupId=aliyun.com -DartifactId=dysmsapi -Dversion=1.0.0 -Dpackaging=jar


