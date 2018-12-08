### 压缩命令
#### 1.命令格式：tar  -zcvf   压缩文件名.tar.gz   被压缩文件名

      可先切换到当前目录下。压缩文件名和被压缩文件名都可加入路径。

 

#### 2.解压缩命令：

　　命令格式：tar  -zxvf   压缩文件名.tar.gz

　　解压缩后的文件只能放在当前的目录。


#### 3.查询系统时间命令
    date

#### 4.开启ssh命令
    注意的地方 如果xshell等工具连接Linux系统无法连接，Linux与本机可以ping的通，Linux可以连接网络 并且已经关闭防火墙，Linux设置的桥接 时，可以检查一下ssh服务是否开启。
    1.查看ssh服务的状态
    输入以下命令：

    sudo service sshd status

    如果出现

    Loaded: error (Reason: No such file or directory)

    提示的话，说名没有安装ssh服务，按照第二步：安装ssh服务。

    如果出现

    Active: inactive (dead)

    说明已经安装了ssh服务，但是没有开启。按照第三步：开启ssh服务。


    2安装ssh服务
    安装ssh命令：

    如果你用的是redhat，fedora，centos等系列linux发行版，那么敲入以下命令：

    sudo yum install sshd

    或者

    sudo yum install openssh-server（由osc网友 火耳提供）

    如果你使用的是debian，ubuntu，linux mint等系列的linux发行版，那么敲入以下命令：

    sudo apt-get install sshd

    或者

    sudo apt-get install openssh-server（由osc网友 火耳提供）



    然后按照提示，安装就好了。


    3.开启ssh服务
    在终端敲入以下命令：

    sudo service sshd start



#### 5.防火墙关闭命令
/etc/init.d/iptables status   //查看防火墙的状态

暂时关闭防火墙：
/etc/init.d/iptables stop

禁止防火墙在系统启动时启动
/sbin/chkconfig --level 2345 iptables off

重启iptables:
/etc/init.d/iptables restart


重启后生效 
开启： chkconfig iptables on 
关闭： chkconfig iptables off   或者 /sbin/chkconfig --level 2345 iptables off


即时生效，重启后失效 

使用 service 方式
开启： service iptables start 
关闭： service iptables stop 

使用iptables方式

查看防火墙状态：
/etc/init.d/iptables status 


暂时关闭防火墙：
/etc/init.d/iptables stop


重启iptables:
/etc/init.d/iptables restart

在Fedora中有时候我们想关闭SELinux，因为有时候本是合法的操作也总是弹出窗口阻止我们的操作。下面介绍三种方法来关闭/禁用SELinux。 
1.在安装Fedora时选择开启或者关闭SeLinux。当然相信大多数来到这里的不会是为了这种方法来到这里。
2.临时关闭SELinux。如果你仅仅只是想临时关闭，可以输入
setenforce 0
3.禁用SELinux。在 /etc 下可以看到一个SELinux文件夹，进入后，里面有个config文件，在终端进入到文件夹，输入
gedit config
更改其中的SELINUX项的值就可以关闭和启用SELinux服务了。
修改成 SELINUX=disable     禁用SeLinux
修改成 SELINUX=enforcing 使用SeLinux




#### 6.使系统同步的命令
详细内容参考：
https://www.cnblogs.com/ibnode/p/3573302.html

vi /etc/ntp.conf   //查看ntp配置
[root@node1 ~]# /etc/init.d/ntpd start   //启动ntp服务

[root@node1 ~]# ntpstat  //确认我们的NTP服务器已经更新了时间

#### 7.解决Disk space is low  的问题，
1.最简单的方法 是关机后增加centos的内存和硬盘空间，但是关机之后，之前设置的临时改变的环境，都会失效，就是没有写入配置文件的那些配置。
2.挂载分区  还没有弄清楚。。。。

linux系统格式化分区
```
#mkfs.xfs /dev/sda1
```
将设备文件挂载到点上（一个文件）
```
mount /dev/sda /tmp
```
取消挂载
```
umount /tmp
```

#### cannot execute binary file 报错
产生的原因是linux系统的版本（32位） 无法执行64位的程序
查询系统版本的命令：
```
cat /proc/version
```

#### WARNING: UNPROTECTED PRIVATE KEY FILE!的解决办法
图片展示了报错信息
https://img-blog.csdn.net/20160629104732765?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center
这里是说密钥文件权限不能为0644,0644权限太开放了，要求你的密钥文件不能被其它用户读取。
所以我们现在需要修改一下密钥文件权限。
在命令行输入chmod 700 parkcloud-new.pem即可。

这里“parkcloud-new.pem”就是warning里给出的密钥文件名，

所以你需要换成你的warning信息里给出的秘钥文件名。

格式如下：chmod 权限码 密钥文件名

### 搭建莱特币的测试配置

在LitecoinData的文件目录下litecoin.conf配置文件中，
rpc参数配置如下：
```
server=1
testnet=1
rpcuser=superbigfu 
rpcpassword=123456
rpcallowip=127.0.0.1
rpcport=8334
txindex=1

```

### linux系统部署莱特币
1.莱特币子系统无法连接到linux系统上的 莱特币客户端

下载适合系统环境的版本
解压
新建一个文件夹  /usr/litecoinDate
将配置文件litecoin.conf 拷到文件夹中
在莱特币客户端的bin目录下 执行
```
./litecoind --datadir=/usr/litecoinDate --daemon
```
回车后：
```
Litecoin server starting
```
执行：是否有返回数据，如果没有则没有成功。
```
./litecoin-cli getdifficulty 
```
如果执行了
```
./litecoin-cli stop
```
停止了客户端，就需要重新运行
```
./litecoind --datadir=/usr/litecoinDate --daemon
```


2.