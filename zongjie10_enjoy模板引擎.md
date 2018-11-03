## Enjoy模板引擎 
### 立即掌握 90% 的用法，只需要记住一句话：JFinal 模板引擎表达式与 java 是直接打通的。

###  jfinal 模板引擎核心概念只有指令与表达式这两个。而表达式是与 java 直接打通的，所以没有学习成本，剩下来只有 #if、#for、#define、#set、#include、#(...) 六个指令需要了解，而这六个指令的学习成本又极低。

### 共享模板函数配置
 如果模板中通过 #define 指令定义了 template function，并且希望这些 template function 可以在其它模板中直接调用的话，可以进行如下配置：
 ```
 @Override
    public void configEngine(Engine engine) {
        engine.addSharedFunction("/view/common/layout.html");
    }
 ```
 以上代码添加了一个共享函数模板文件 layout.html，这个文件中使用了#define指令定义了template function。通过上面的配置，可以在任意地方直接调用 layout.html 里头的 template function。

### 从 class path 和 jar 包加载模板配置
  如果模板文件在项目的 class path 路径或者 jar 包之内，可以通过me.setSourceFactory(new ClassPathSourceFactory()) 以及 me.setBaseTemplatePath(null) 来实现，以下是代码示例：
  ```
  public void configEngine(Engine me) {
   me.setDevMode(true);
 
   me.setBaseTemplatePath(null);
   me.setSourceFactory(new ClassPathSourceFactory());
 
   me.addSharedFunction("/view/common/layout.html");
}
  ```
通过上面的 me.setSourceFactory(...) 可以推测出来，还可以通过实现ISourceFactory、ISource 扩展出从任何地方加载模板文件的功能，目前已有用户实现 DbSource 来从数据库加载模板的功能。

### sql 管理模块的 Engine 配置
 JFinal Template Engine 被设计成为可以在单独的项目中同时使用多个 Engine 对象，这多个不同的 Engine 对象可分别进行不同的配置，用于不同的用途，独立配置、互不干扰。

例如，jfinal 中的 configEngine(Engine me) 中配置的 Engine 对象是用于 Controller.render(....) 方法的渲染，而 ActiveRecordPlugin.getEngine() 对象是用于 sql 管理功能模块，这两个 Engine 对象是两个不同的实例，互相之间没有干扰，配置方式也不同。

前面例子中的配置已介绍过了用于 Controller.render(...) 渲染的 Engine 对象的配置，而 ActiveRecordPlugin 的 sql 管理模块的 Engine 对象的配置方法如下：
```
    public void configPlugin(Plugins me) {
        ActiveRecordPlugin arp = new ActiveRecordPlugin(....);
        Engine engine = arp.getEngine();
        
        // 上面的代码获取到了用于 sql 管理功能的 Engine 对象，接着就可以开始配置了
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
        
        me.add(arp);
}
```
上面代码中通过 arp.getEngine() 获取到了 sql 管理模块专用的 Engine 对象，并对其进行了两项配置。这两项配置仅对 sql 管理功能的 Engine 对象有效，与 configEngine(Engine me) 中配置的 Engine 对象完全无关，她们自各拥有独立的配置。

## 表达式
### 与java规则基本相同的表达式
算术运算： +   -   *   /   %   ++   --

比较运算： >  >=   <   <=  ==   !=  (基本用法相同，后面会介绍增强部分)

逻辑运算： !   &&   ||

三元表达式： ? :

Null 值常量: null

字符串常量： "jfinal club"

布尔常量：true false

数字常量： 123  456F  789L  0.1D  0.2E10

数组存取：array[i](Map被增强为额外支持 map[key]的方式取值)

属性取值：object.field(Map被增强为额外支持map.key 的方式取值 public修饰的属性)

方法调用：object.method(p1, p2…, pn) (支持可变参数)

逗号表达式：123, 1>2, null, "abc", 3+6 (逗号表达式的值为最后一个表达式的值)

小技巧：如果从java端往map中传入一个key为中文的值，可以通过map["中文"] 的方式去访问到，而不能用 "map.中文" 访问。因为引擎会将之优先当成是object.field的访问形式，而目前引擎暂时还不支持中文作为变量名标识符。

### 属性访问
由于模板引擎的属性取值表达式极为常用，所以对其在用户体验上进行了符合直觉的扩展，field 表达式取值优先次序，以 user.name 为例：

如果 user.getName() 存在，则优先调用

如果 user 为 Model 子类，则调用 user.get("name")

如果 user 为 Record，则调用 user.get("name")

如果 user 为 Map，则调用 user.get("name")

如果 user 具有 public 修饰过的name 属性，则取 user.name 属性值


此外，还支持数组的length长度访问：array.length，与java语言一样
  
### 方法调用
模板引擎被设计成与 java 直接打通，可以在模板中直接调用对象上的任何public方法，使用规则与java中调用方式保持一致，以下代码示例：
```
#("ABCDE".substring(0, 3))
#(girl.getAge())
#(map.get(key))
```
 简单来说：模板表达式中可以直接调用对象所拥有的public方法，方法调用支持可变参数，例如支持这种方法被调用：obj.find(String sql, Object … args)。

 ### 静态属性访问
在模板中通常要访问java代码中定义的静态变量、静态常量，以下是代码示例：

```
#if(x.status == com.demo.common.model.Account::STATUS_LOCK_ID)
<span>(账号已锁定)</span>
#end
```
 如上所示，通过类名加双冒号再加静态属性名即为静态属性访问表达式，上例中静态属性在java代码中是一个int数值(如果不是int呢？字符串用equals？)，通过这种方式可以避免在模板中使用具体的常量值，从而有利于代码重构。

由于静态属性访问需要包名前缀，代码显得比较长，在实际使用时如果多次用到同一个值，可以用 #set(STATUS_LOCK_ID = ...) 指令将常量值先赋给一个变量，可以节省一定的代码。

注意，这里的属性必须是public static修饰过的才可以被访问。此外，这里的静态属性并非要求为final修饰。

### 静态方法调用
 JFinal Template Engine 可以以非常简单的方式调用静态方法，以下是代码示例：
```
 #if(com.jfinal.kit.StrKit::isBlank(title))
   ....
#end
```
使用方式与前面的静态属性访问保持一致，仅仅是将静态属性名换成静态方法名，并且后面多一对小括号与参数：类名 + :: + 方法名(参数)。静态方法调用支持可变参数。与静态属性相同，被调用的方法需要使用public static 修饰才可访问。

    如果觉得类名前方的包名书写很麻烦，可以使用后续即将介绍的me.addSharedMethod(…)方法将类中的方法添加为共享方法，调用的时候直接使用方法名即可，连类名都不再需要。

    此外，还可以调用静态属性上的方法(静态属性上的方法？？？？？)，以下是代码示例：
```
(com.jfinal.MyKit::me).method(paras)
```
上面代码中需要先用一对小扩号将静态属性取值表达式扩起来，然后再去调用它的方法，小括号在此仅是为了改变表达式的优先级。

### 空合并安全取值调用操作符
 JFinal Template Engine 引入了swift与C#语言中的空合操作符，并在其基础之上进行了极为自然的扩展，该表达式符号为两个紧靠的问号：??。代码示例：
```
seoTitle ?? "JFinal 社区"
object.field ??
object.method() ??

```
以上第一行代码的功能与swift语言功能完全一样，也即在seoTitle 值为null时整个表达式取后面表达式的值。而第二行代码表示对object.field进行空安全(Null Safe)属性取值，即在object为null时表达式不报异常，并且值为null。

第三行代码与第二行代码类似，仅仅是属性取值变成了方法调用，并称之为空安全(Null Safe)方法调用，表达式在object为null时不报异常，其值也为null。

当然，空合并与空安全可以极为自然地混合使用，如下是示例：
```
object.field ?? "默认值"
object.method() ?? value
```
以上代码中，第一行代码表示左侧null safe 属性取值为null时，整个表达式的值为后方的字符串中的值，而第二行代码表示值为null时整个表达式取value这个变量中的值。

特别注意：?? 操作符的优先级高于数学计算运算符：+、-、*、/、%，低于单目运算符：!、++、--。强制改变优先级使用小括号即可。

例子：a.b ?? && expr 表达式中，其 a.b ?? 为一个整体被求值，因为 ?? 优先级高于数学计算运算符，而数学计算运算符又高于 && 运算符，进而推导出 ?? 优先级高于&& (最终表达式结果为boolean值)

### 单引号字符串

针对Template Engine 经常用于html的应用场景，添加了单引号字符串支持，以下是代码示例：
```
<a href="/" class="#(menu == 'index' ? 'current' : 'normal')"
   首页
</a>
```
以上代码中的三元表达式中有三处使用了单引号字符串，好处是可以与最外层的双引号协同工作，也可以反过来，最外层用单引号字符串，而内层表达式用双引号字符串。

这个设计非常有利于在模板文件中已有的双引号或单引号内容之中书写字符串表达式。

### 相等与不等比较表达式增强
相等不等表达式 == 与 != 会对左右表达式进行left.equals(right)比较操作，所以可以对字符串进行直接比较，如下所示：
```
#if(nickName == "james")
  ....
#end
```
注意：Controller.keepPara(…) 方法会将任何数据转换成String后传递到view层，所以原本可以用相等表达式比较的两个Integer型数据，在keepPara(…)后变得不可比较，因为变为了String与Integer型的比较。解决方法见本章的Extionsion Method小节。

### 布尔表达式增强
 布尔表达式在原有java基础之下进行了增强，可以减少代码输入量，具体规则自上而下优先应用如下列表：

null 返回 false

boolean 类型，原值返回

String、StringBuilder等一切继承自 CharSequence 类的对象，返回 length > 0

其它返回 true

以上规则可以减少模板中的代码量，以下是示例：
```
#if(user && user.id == x.userId)
  ....
#end
```
以上代码中的 user 表达式实质上代替了java表达式的 user != null 这种写法，减少了代码量。当然，上述表达式如果使用 ?? 运算符，还可以更加简单顺滑：if (user.id ?? == x.userId)

### 范围数组定义表达式
直接举例：

```
#for(x : [1..10])
   #(x)
#end
```
 上图中的表达式 [1..10] 定义了一个范围数组，其值为从1到10的整数数组，该表达式通常用于在开发前端页面时，模拟迭代输出多条静态数据，而又不必从后端读取数据。

此外，还支持递减的范围数组，例如：[10..1] 将定义一个从10到1的整数数组。上例中的#for指令与#()输出指令后续会详细介绍。

### Map定义表达式
Map定义表达式的最实用场景是在调用方法或函数时提供极为灵活的参数传递方式，当方法或函数需要传递的参数名与数量不确定时极为有用，以下是基本用法：
```
#set(map = {k1:123, "k2":"abc", "k3":object})
#(map.k1)
#(map.k2)
#(map["k1"])
#(map["k2"])
#(map.get("k1"))
```
如上图所示，map的定义使用一对大括号，每个元素以key : value的形式定义，多个元素之间用逗号分隔。

key 只允许是合法的 java 变量名标识符或者 String 常量值（jfinal 3.4 起将支持 int、long、float、double、boolean、null 等等常量值），注意：上例中使用了标识符 k1 而非 String 常量值 "k1" 只是为了书写时的便利，与字符串是等价的，并不会对标识符 k1 进行表达式求值。

上图中通过#set指令将定义的变量赋值给了map变量，第二与第三行中以object.field的方式进行取值，第四第五行以 map[key] 的方式进行取值，第六行则是与 java 表达式打通式的用法。

特别注意：上例代码如果使用 map[k1] 来取值，则会对 k1 标识符先求值，得到的是 null，也即map[k1] 相当于 map[null]，因此上述代码中使用了 map["k1"] 这样的形式来取值。

 此外，map 取值还支持在定义的同时来取值，如下所示：
```
#({1:'自买', 2:'跟买'}.get(1))
#({1:'自买', 2:'跟买'}[2])

与双问号符联合使用支持默认值
#({1:'自买', 2:'跟买'}.get(999) ?? '其它')
```

### 逗号表达式
将多个表达式使用逗号分隔开来组合而成的表达式称为逗号表达式，逗号表达式整体求值的结果为最后一个表达式的值。例如：1+2, 3*4 这个逗号表达式的值为12。

### 从java中去除的运算符

### 表达式总结
以上各小节介绍的表达式用法，主要是在 java 表达式规则之上做的有利于开发体验的精心扩展，你也可以先无视这些用法，而是直接当成是 java 表达式去使用，则可以免除掉上面的学习成本。

上述这些在 java 表达式规则基础上做的精心扩展，一是基于模板引擎的实际使用场景而添加，例如单引号字符串。二是对过于啰嗦的 java 语法的改进，例如字符串的比较 str == "james" 取代 str.equals("james")，所以是十分值得和必要的。














