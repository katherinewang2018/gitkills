## JFinal 的if和for指令嵌套

1.指令嵌套
表达式嵌套时  不要用 #指令(#) 直接 #指令(值) 就可以了
不是 #if(#(for.size) != 0)  而是 #if(for.size != 0)

简单来说这种错误就是在该使用表达式的地方使用指令，在表达式中永远不要出现字符 '#'，而是直接使用 java 表达式。

```
#for(x : dappTabs) //复选的选项
    #for(y : codeList) //对象集合
        #if(for.size != 0) //codeList这个集合的大小
            
        #end
    #end
    
#end
				
```

2.页面再回显复选框，即将数据库查询的有的选项，与对应的 页面的复选的选项 勾选中的 效果，
```
 #for(y : codeList)  //遍历后端传来的选项的集合，将每个选项记录下来
    #if(y.tabCode == "exchange")
        #set(exchangeFlag = true)
    #end

    #if(y.tabCode == "game")
        #set(gameFlag = true)
    #end

    #if(y.tabCode == "cpu")
        #set(cpuFlag = true)
    #end

    #if(y.tabCode == "mining")
        #set(miningFlag = true)
    #end

    #if(y.tabCode == "tools")
        #set(toolsFlag = true)
    #end
#end

//列出所有选项值  被记录下的 才会被勾选中 
#if(codeList != null)
    <input type="checkbox" name="tabCode" value="exchange" #if( exchangeFlag ) checked="checked" #end>交易所</input>
    <input type="checkbox" name="tabCode" value="game" #if( gameFlag) checked="checked" #end>游戏</input>
    <input type="checkbox" name="tabCode" value="cpu" #if( cpuFlag) checked="checked" #end>CPU</input>
    <input type="checkbox" name="tabCode" value="mining" #if( miningFlag ) checked="checked" #end>挖矿</input>
    <input type="checkbox" name="tabCode" value="tools" #if( toolsFlag ) checked="checked" #end>工具</input>
#end
```

3.集合的交集 差集 并集
https://www.cnblogs.com/zt19994/p/9114868.html



4.集合的包含
```
 List<String> oldCode = new ArrayList<>();
        oldCode.add("game");
        oldCode.add("tools");
        oldCode.add("exchange");
        oldCode.add("mining");
        //oldCode.add("cpu");
        List<String> newCode = new ArrayList<>();
        newCode.add("tools");
        newCode.add("game");
        //newCode.add("exchange");
        //newCode.add("cpu");
        for(String s : newCode){
            if(oldCode.contains(s)){//
                System.out.println("1.1包含在newcode：");
                System.out.println(s);

            }else if(!oldCode.contains(s)){
                System.out.println("1.2不包含在newcode");
                System.out.println(s);
            }
        }
        System.out.println("=======================================");
        for(String s : oldCode){
            if(newCode.contains(s)){//
                System.out.println("2.1包含在newcode：");
                System.out.println(s);

            }else if(!newCode.contains(s)){
                System.out.println("2.2不包含在newcode");
                System.out.println(s);
            }
        }
```
结果：
```
1.1包含在newcode：
tools
1.1包含在newcode：
game
=======================================
2.1包含在newcode：
game
2.1包含在newcode：
tools
2.2不包含在newcode
exchange
2.2不包含在newcode
mining
```


5.数组的交集 差集 并集
js：
https://www.jianshu.com/p/e00161fe1daa


java：
https://www.cnblogs.com/wanying521/p/5179151.html
```
 private static String[] minus(String[] arr1, String[] arr2) {//差集
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> history = new LinkedList<String>();
        String[] longerArr = arr1;
        String[] shorterArr = arr2;

        if (arr1.length > arr2.length) {
            longerArr = arr2;
            shorterArr = arr1;
        }
        for (String str : longerArr) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        for (String str : shorterArr) {
            if (list.contains(str)) {
                history.add(str);
                list.remove(str);
            } else {
                if (!history.contains(str)) {
                    list.add(str);
                }
            }
        }

        String[] result = {};
        return list.toArray(result);
    }

    public static String[] intersect(String[] arr1, String[] arr2) { //交集
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        LinkedList<String> list = new LinkedList<String>();
        for (String str : arr1) {
            if (!map.containsKey(str)) {
                map.put(str, Boolean.FALSE);
            }
        }
        for (String str : arr2) {
            if (map.containsKey(str)) {
                map.put(str, Boolean.TRUE);
            }
        }

        for (Map.Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }

        String[] result = {};
        return list.toArray(result);
    }

```
### jfinal定义map
```
 #set(map = {})
#(map.put("d","code"))
    #(map.get("d"))
```

### 插播一下 jfinal的方法调用
```
#("ABCDE".substring(0, 3))
#(girl.getAge())
#(list.size())
#(map.get(key))
#(array.length)

```

### jfianl的属性访问
```
属性访问
    由于模板引擎的属性取值表达式极为常用，所以对其在用户体验上进行了符合直觉的扩展，field 表达式取值优先次序，以 user.name 为例：

如果 user.getName() 存在，则优先调用

如果 user 为 Model 子类，则调用 user.get("name")

如果 user 为 Record，则调用 user.get("name")

如果 user 为 Map，则调用 user.get("name")

如果 user 具有 public 修饰过的name 属性，则取 user.name 属性值
```

### jfinal获取集合大小
```
#(codeList.size()) //codeList是后台传入的集合（List）
```


解决两层嵌套 第一层嵌套是必须的五个复选框的选项；第二个嵌套是对象中所包含的对应的选项值
```
 <div class="col-sm-8">
                    #if(codeList == null)
                        #for(x : dappTabs)
                            <input type="checkbox" name="tabCode" value="#(x.code)">#(x.name)</input>
                        #end
                    #else
                        #set(map = {})
                        #for(x : dappTabs)
                            #for(y : codeList)
                                #if(y.tabCode == x.code)
                                    #set(exchangeFlag = true)
                                        <input type="checkbox" name="tabCode" value="#(x.code)" #if( exchangeFlag==true) checked="checked" #end>#(x.name)</input>
                                        #(map.put(y.tabCode,y.tabCode)) //把已经符合选项并且勾选中的值 保存到容器中
                                #end
                            #end

                            #if(x.code != map[x.code]) //判断如果五个选项中的选项与容器中的值不同时 就打印这个选项
                            <input type="checkbox" name="tabCode" value="#(x.code)">#(x.name)</input>
                            #end
                        #end
                    #end
                </div>
```

### 代码冗余问题
在dao层中：
```
 public boolean doUpdate(List<DappTabRelate> dappList, String[] tabCodes){

        boolean isOk = false;

        for(DappTabRelate dappTabRelate : dappList){ //dappList是查询的所有选项的记录对象
            dappTabRelate.setAddTime(null);//将所有记录对象的addTime先设置为null
            for(String code : tabCodes){
                if(dappTabRelate.getTabCode().equals(code)){ //当页面传入的code与从数据库查询的code一致时 才更新addTime  否则就存入的null值
                    dappTabRelate.setAddTime(new Date());
                }
            }

            isOk = dappTabRelate.update();
        }

        return isOk;
    }
```

### jfianl的数据操作

特别注意：User中定义的 public static final User dao对象是全局共享的，只能用于数据库查询，不能用于数据承载对象。数据承载需要使用new User().set(…)来实现。

### jfinal中的事务处理
以下两次数据库更新操作在一个事务中执行，如果执行过程中发生异常或者run()方法返回false，则自动回滚事务。
```
boolean succeed = Db.tx(new IAtom(){
    public boolean run() throws SQLException {
       int count = Db.update("update account set cash = cash - ? where id = ?", 100, 123);
       int count2 = Db.update("update account set cash = cash + ? where id = ?", 100, 456);
       return count == 1 && count2 == 1;
    }});

```

### 官方提示
User中定义的 public static final User dao对象是全局共享的，只能用于数据库查询，不能用于数据承载对象。数据承载需要使用new User().set(…)来实现。


### jfinal查询
jfinal与数据库的交互 使用Db或这model.dao;都需要先获的执行的sql语句，然后再执行
最好不要直接用sql语句去查询了。
```
 SqlPara sqlPara = Db.getSqlPara("dapp.findByTag", Kv.by("tag", tag));
        Record first = Db.findFirst(sqlPara);
        Integer count = first.getInt("count");//是我们写在sql中的别名

#sql("findByTag")
  SELECT count(*) count FROM dapp
  WHERE tag = ?
#end


Integer count = Db.queryInt("SELECT COUNT(*) FROM dapp WHERE tag = ?", tag);//不要用这种
```

```
String sql = Db.getSql("dapp.findByTag");
count = Db.queryInt(sql, tag);

#sql("findByTag")
  SELECT count(*) FROM dapp
  WHERE tag = ?
#end

```

```
String sqlStr = Db.getSql("dapp.findById");//先获取sql语句 
String strTag = Db.queryStr(sqlStr, dappId);

#sql("findById")
  SELECT tag tag FROM dapp
  WHERE dapp_id = ?
#end

```

```
SqlPara sqlPara = Db.getSqlPara("findByTag", tag);
Integer ret = Db.queryInt(sqlPara);

#sql("findById")
  SELECT tag tag FROM dapp
  WHERE dapp_id = #(para)
#end

```

或者
```
SqlPara sqlPara = Db.getSqlPara("findByTag", tag);
Integer ret = Db.queryInt(sqlPara);

#sql("findById")
  SELECT tag tag FROM dapp
  WHERE dapp_id = #(0)  

  //简单说，前者你为变量取了名，自然在用 #para 时是指定名字，后者你没取名，自然是指定下标值: 0 , 1, ...  n
#end
```

### 写sql时 能不要重复就不要重复 可以用判断#if() 来完成条件拼接

### mysql中 left Join 与right join 和inner join 
简单讲：
　left join(左联接) 返回包括左表中的所有记录和右表中联结字段相等的记录。
例如
```
select * from app01_publisher left join app01_book on app01_publisher.id = app01_book.publish_id
```
  right join(右联接) 返回包括右表中的所有记录和左表中联结字段相等的记录。
例如
```
select * from app01_publisher right join app01_book on app01_publisher.id = app01_book.publish_id
```

  inner join(等值连接) 只返回两个表中联结字段相等的行。
例如
```
select * from app01_publisher inner join app01_book on app01_publisher.id = app01_book.publish_id
```


### 关于 “A LEFT JOIN B ON 条件表达式” 的一点提醒

ON 条件（“A LEFT JOIN B ON 条件表达式”中的ON）用来决定如何从 B 表中检索数据行。

例如
```
SELECT e.*, e.event_id, ei.name, ei.cover, ei.intro, ei.lang
FROM event e
LEFT JOIN event_info ei ON e.event_id = ei.event_id AND ei.lang = 'ch'
WHERE status != 0
```

如果使用了where

先做了简单的LEFT JOIN然后使用 WHERE 子句从 LEFT JOIN的数据中过滤掉不符合条件的数据行。
```
SELECT e.*, e.event_id, ei.name, ei.cover, ei.intro, ei.lang
FROM event e
LEFT JOIN event_info ei ON e.event_id = ei.event_id 
WHERE status != 0 AND ei.lang = 'ch'
```



详细内容：
https://www.cnblogs.com/zjfjava/p/6041445.html

