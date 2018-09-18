<p align="center">
    <img src="https://images.gitee.com/uploads/images/2018/0917/155220_c5663c74_804942.png" width="400">
</p>
<p align="center">
	<a target="_blank" href="https://mvnrepository.com/artifact/cn.simplifydb/simplifydb">
		<img src="https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/cn/simplifydb/simplifydb/maven-metadata.xml.svg" ></img>
	</a>
	<a target="_blank" href="http://www.apache.org/licenses/LICENSE-2.0.html">
		<img src="http://img.shields.io/:license-apache-blue.svg" ></img>
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-1.8+-green.svg" ></img>
	</a>
</p>

<p align="center">
	-- QQ群：<a href="//shang.qq.com/wpa/qunwpa?idkey=6ba1c95552b1e944c14d898f4ac625ec5cacf119ef9a9fd67c91f3aff2ff88f1">136715345</a> --
</p>

#  SimplifyDb
>在java 中我们有多种方式操作数据库，但是如果只是为了简单处理使用一些框架，在使用中还是显得麻烦。这里就整理一个简单快速操作数据库的一种方案



## 简介
SimplifyDb 是一个Java基于druid的一款简化写sql语句操作mysql的框架。本项目主要采用反射读写需要操作的实体和表，同时项目还是提供多种主键生成器和自定义主键生成器接口方便用户根据实际业务扩展主键生成器
> 项目特点
> 1. 快速配置和操作多数据库
> 2. 多数据源配置自动切换
> 3. 提供多种主键生成器以及自定义主键生成器
> 4. 全局统一记录数据创建人和修改人
> 5. 快速配置逻辑删除功能
> 6. 写操作支持异步执行

#### 使用示例：[https://gitee.com/jiangzeyin/simplifydb-demo](https://gitee.com/jiangzeyin/simplifydb-demo)


## 文档 

[参考API](https://apidoc.gitee.com/jiangzeyin/dbutil/)

[博客专栏](http://blog.csdn.net/column/details/17021.html)


## 安装

### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.simplifydb</groupId>
    <artifactId>simplifydb</artifactId>
    <version>version</version>
</dependency>
```
注：VERSION 请更换为公共maven库最新的版本号

## 版本变更

- [Release版本变更说明](https://gitee.com/jiangzeyin/dbutil/blob/master/CHANGELOG.md)

### 提供bug反馈或建议

- [码云](https://gitee.com/iangzeyin/dbutil/issues)
- [Github](https://github.com/jiangzeyin/dbutil/issues)


## 项目特点描述：(使用druid 连接池)

1.支持多数据源

2.多数据源支持读写分离 和 随机落取

3.接口形式记录日志

4.接口形式获取当前操作用户

5.增 删 改 可以使用异步执行

6.对外提供 增 删 改 执行过程中的接口调用

7.自动记录当前最后修改数据人 创建数据人和时间

8.自动记录数据最后修改数据时间（和第5点不冲突）

欢迎━(*｀∀´*)ノ亻!大家测评

cn.simplifydb.database.config.DataSourceConfig  工具使用配置类

cn.simplifydb.system.DbLog  工具日志为了各个项目适配，使用了接口形式来提供记录日志

cn.simplifydb.database.run.read.IsExists 判断是否存在

cn.simplifydb.database.run.read.Select 多种方式查询

cn.simplifydb.database.run.read.SelectFunction 查询函数

cn.simplifydb.database.run.read.SelectPage 分页查询

cn.simplifydb.database.run.write.Insert 添加数据

cn.simplifydb.database.run.write.Remove 删除数据

cn.simplifydb.database.run.write.Update 修改数据

cn.simplifydb.database.run.write.Transaction 事物操作

示例配置：(db.properties)

```
sourceTag=core
configPath=file:/test/read.properties
systemKey=com.yoke   
systemKeyColumn=url,username,password
#
lastModify.time=UNIX_TIMESTAMP(NOW())
lastModify.class=com.yoke.entity.EditOptBaseEntity,com.yoke.entity.AdminOptBaseEntity
lastModify.column.user=lastModifyUser
lastModify.column.time=lastModifyTime
#
create.class=com.yoke.entity.AdminOptBaseEntity
create.column.user=createUser
#
systemColumn.pwd=pwd
systemColumn.active=isDelete
systemColumn.active.value=0
systemColumn.inActive.value=1
#
systemColumn.modify.status=true
systemColumn.modify.column=modifyTime
systemColumn.modify.time=UNIX_TIMESTAMP(NOW())
#
systemColumn.notPutUpdate=modifyTime,isDelete,createTime,createUser,lastModifyUser,lastModifyTime,id
systemColumn.columnDefaultValue=modifyTime:UNIX_TIMESTAMP(NOW()),createTime:UNIX_TIMESTAMP(NOW())
systemColumn.writeDefaultRemove=createUser,lastModifyUser,lastModifyTime,id,isDelete
systemColumn.readDefaultRemove=pwd
systemColumn.selectDefaultColumns=*
#
systemColumn.defaultRefKeyName=id
systemColumn.defaultKeyName=id
```

**sourceTag,configPath 为必需字段**

其他字段均根据自己实际情况配置

systemKey 为configPath 中加密的密钥

systemKeyColumn  为configPath 中哪些字段是为加密的字段

实例配置：(file:/test/read.properties)

```
core.driver=com.mysql.jdbc.Driver
core.url=1953342c4b
core.username=12b0ecd
core.password=9085ada2469
core.init=true
core.maxActive=10000
core.validationQuery=SELECT 'x'
core.testOnReturn=false
core.testOnBorrow=false
core.testWhileIdle=true
core.minIdle=30
core.initialSize=1
core.removeAbandoned=true
core.removeAbandonedTimeout=60
core.logAbandoned=true
```
 
配置字段具体含义请 查看[https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8](https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8)

#**初始化**：

**`1.先设置日志接口`**

cn.simplifydb.system.DbLog.setDbLogInterface()

方法传入cn.simplifydb.system.DbLog.DbLogInterface 接口主要负责记录util 执行日志

**`2.开始初始化数据库连接`**

cn.simplifydb.database.config.DataSourceConfig.init()  参数为配置文件的路径

支持file: 、classpath:  

**`3.设置实体转换数据库接口（根据class 获取该实体存在的数据源中）`**

cn.simplifydb.database.DbWriteService.setWriteInterface()

方法传入cn.simplifydb.database.DbWriteService.WriteInterface 接口主要负责处理实体对应的数据源标记和实体数据库表名

