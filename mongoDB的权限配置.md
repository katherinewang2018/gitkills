## mongoDb的权限配置
1.从官网下载安装
```
mongodb-win32-x86_64-2008plus-ssl-3.2.10-signed.msi
```
点击安装 下一步即可(环境变量要配置好)

2.在bin的同级目录下新建mongod.conf 配置文件
```
systemLog:
    destination: file
    path: "D:/mongodbdata/log/mongod.log"
    logAppend: true
storage:
    journal:
        enabled: true
    dbPath: "D:/mongodbdata/db"
net:
    bindIp: 127.0.0.1
    port: 20111  //默认是27017 这里如果改了 连接时要输入：127.0.0.1:20111
security:
    //这个是启用权限管理的配置 disabled 是不启用
    //enable 是启用 
    authorization: disabled 

```

3.win+r 进入cmd 黑窗口后 输入：
```
 mongod --install -f  D:\software\mongodb\mongod.conf
```
以上是让mongodb在后台启动

4.回车后net start MongoDB
如果成功会有：MongoDB服务已经启动成功
如果不成功检查配置文件

5.另开一个cmd 黑窗口 输入：
```
mongo 127.0.0.1:20111
```
连接进去后有
```
>
```
这个符号 表示连接成功

6.添加用户
切换到admin
```
use admin
```
回车后输入：
```
db.createUser(
  {
    user: “your_user_name”,
    pwd: “your_password”,
    roles: [ { role: "userAdminAnyDatabase", db:"admin" } ]
  }
)
```
按照以上格式修改成自己需要的：
```
db.createUser(
  {
    user: "use",
    pwd: "123456",
    roles: [ { role: "readWrite", db: "test" } ]   //db 的值就是你要进行权限控制的数据库名
  }
)

```
那么以后use这个用户要连接mongodb时就需要有密码了。

### 其中碰到的问题
1.如果我在配置文件中把security 改为：
```
security:
    authorization: enabled 
```
那么在使用admin进行db.createUser时 总是会跳出
```
couldnt add user command createUser require authentication
```
这个报错，这里是需要授权还是什么的，但是要怎么给admin授权？最后没有得到解决，所以不得改为：
```
 authorization: disabled 
```
这样就可以用admin 来添加用户 并给用户设置密码；


7.命令行操作不太方便，所以用Navicat 可视化工具来连接
如果配置文件中是启用授权：
```
security:
    authorization: enabled 
```
如果这里启用授权后 需要先在没有启用情况下用admin添加用户，设置用户名和密码，再改变配置文件启用授权，在服务 中重启MongoDB 服务，黑窗口中连接到mongodb 切换到数据库 然后db.auth("用户名","密码")，即在没有启用授权时用admin添加的用户名和密码

