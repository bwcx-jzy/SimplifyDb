# dbutil
在java 中我们有多种方式操作数据库，但是如果只是为了简单处理使用一些框架，在使用中还是显得麻烦。这里就整理一个简单快速操作数据库的一种方案

并且也上maven 库了：[https://mvnrepository.com/artifact/cn.jiangzeyin/dbutil](https://mvnrepository.com/artifact/cn.jiangzeyin/dbutil)

博客专栏：[http://blog.csdn.net/column/details/17021.html](http://blog.csdn.net/column/details/17021.html)

项目特点：(使用druid 连接池)

1.支持多数据源

2.多数据源支持读写分离 和 随机落取

3.接口形式记录日志

4.接口形式获取当前操作用户

5.增 删 改 可以使用异步执行

6.对外提供 增 删 改 执行过程中的接口调用

7.自动记录当前最后修改数据人 创建数据人和时间

8.自动记录数据最后修改数据时间（和第5点不冲突）

欢迎━(*｀∀´*)ノ亻!大家测评

cn.jiangzeyin.database.config.DataSourceConfig  工具使用配置类

cn.jiangzeyin.system.DbLog  工具日志为了各个项目适配，使用了接口形式来提供记录日志

cn.jiangzeyin.database.run.read.IsExists 判断是否存在

cn.jiangzeyin.database.run.read.Select 多种方式查询

cn.jiangzeyin.database.run.read.SelectFunction 查询函数

cn.jiangzeyin.database.run.read.SelectPage 分页查询

cn.jiangzeyin.database.run.write.Insert 添加数据

cn.jiangzeyin.database.run.write.Remove 删除数据

cn.jiangzeyin.database.run.write.Update 修改数据

cn.jiangzeyin.database.run.write.Transaction 事物操作

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

`**1.先设置日志接口**`

cn.jiangzeyin.system.DbLog.setDbLogInterface()

方法传入cn.jiangzeyin.system.DbLog.DbLogInterface 接口主要负责记录util 执行日志

**`2.开始初始化数据库连接`**

cn.jiangzeyin.database.config.DataSourceConfig.init()  参数为配置文件的路径

支持file: 、classpath:  

**`3.设置实体转换数据库接口（根据class 获取该实体存在的数据源中）`**

cn.jiangzeyin.database.DbWriteService.setWriteInterface()

方法传入cn.jiangzeyin.database.DbWriteService.WriteInterface 接口主要负责处理实体对应的数据源标记和实体数据库表名

