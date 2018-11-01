### 定时将数据库A中的表同步到另一个数据库

https://blog.csdn.net/l1028386804/article/details/80341251
转载自以上连接，详细内容请移步此连接

以下是核心配置 
需要注意的是 这里是一个表 一个表进行的同步，多张表时均需要在<job>标签中进行配置，另外目标数据库中的表结构需要建好  需要更新的字段都要再 <destTableFields> 和 <destTableUpdate>中；
另外注意数据库的地址和用户名以及密码都要改为自己使用的。

```

<?xml version="1.0" encoding="UTF-8"?>
<root>
    <code>4500000001</code>
<!--     <source>
        <url>jdbc:oracle:thin:@192.168.1.179:1521:XE</url>
        <username>test</username>
        <password>test</password>
        <dbtype>oracle</dbtype>
        <driver>oracle.jdbc.driver.OracleDriver</driver>
    </source>
    <dest>
        <url>jdbc:sqlserver://192.168.1.191:1433;DatabaseName=test</url>
        <username>test</username>
        <password>test</password>
        <dbtype>sqlserver</dbtype>
        <driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</driver>
    </dest> -->
    <source>
        <url>jdbc:mysql://192.168.209.121:3306/test</url>
        <username>root</username>
        <password>root</password>
        <dbtype>mysql</dbtype>
        <driver>com.mysql.jdbc.Driver</driver>
    </source>
    <dest>
        <url>jdbc:mysql://127.0.0.1:3306/test</url>
        <username>root</username>
        <password>root</password>
        <dbtype>mysql</dbtype>
        <driver>com.mysql.jdbc.Driver</driver>
    </dest>
    <jobs>
        <job>
            <name>1</name>
            <cron>0/10 * * * * ?</cron>
            <srcSql>select user_id, account,password from user</srcSql>
            <destTable>client_user</destTable>
            <destTableFields>user_id, account</destTableFields>
            <destTableKey>user_id</destTableKey>
            <destTableUpdate>account</destTableUpdate>
        </job>
    </jobs>
</root>


```