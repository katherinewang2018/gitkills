## JFinal 模板指令
### 核心只有#if、#for、#set、#include、#define、#(…)

注意，JFinal模板引擎指令的扩展是在词法分析、语法分析的层面进行扩展，与传统模板引擎的自定义标签类的扩展完全不是一个级别，前者可以极为全面和自由的利用模板引擎的基础设施，在更加基础的层面以极为简单直接的代码实现千变万化的功能。参考 Active Record的 sql 管理模块，则可知其强大与便利。（不太知、、、、）

### 输出指令#( )
  #(…)输出指令的使用极为简单，只需要为该指令传入前面6.4节中介绍的任何表达式即可，指令会将这些表达式的求值结果进行输出，特别注意，当表达式的值为null时没有任何输出，更不会报异常。所以，对于 #(value) 这类输出不需要对value进行null值判断，如下是代码示例：
```
#(value)
#(object.field)
#(object.field ??)
#(a > b ? x : y)
#(seoTitle ?? "JFinal 俱乐部")
#(object.method(), null)
```

如上图所示，只需要对输出指令传入表达式即可。注意上例中第一行代码value参数可以为null，而第二行代码中的object为null时将会报异常，此时需要使用第三行代码中的空合安全取值调用运算符：object.field ??

此外，注意上图最后一行代码中的输出指令参数为一个逗号表达式，逗号表达式的整体求值结果为最后一个表达式的值，而输出指令对于null值不做输出，所以这行代码相当于是仅仅调用了object.method()方法去实现某些操作。

输出指令可以自由定制，只需要继承 OutputDirectiveFactory 类并覆盖其中的 getOutputDirective 方法，然后在configEngine(Engine me)方法中，通过 me. setOutputDirectiveFactory(…) 切换即可。

### #if 指令
```
#if(cond)
  ...
#end
```
如上图所示，if指令需要一个cond表达式作为参数，并且以#end为结尾符，cond可以为6.3章节中介绍的所有表达式，包括逗号表达式，当cond求值为true时，执行if分支之中的代码。

if 指令必然支持#else if 与#else分支块结构，以下是示例：
```
#if(c1)
  ...
#else if(c2)
  ...
#else if (c3)
  ...
#else
  ...
#end
```

由于#else if、#else用法与java语法完全一样，在此不在赘述。（jf 3.3版添加了对# else if 风格的支持，也即else与if之间可以有空白字符）

### #for 指令
JFinal Template Engine 对for 指令进行了极为人性化的扩展，可以对任意类型数据进行迭代输出，包括支持null值迭代。以下是代码示例：
```
#for(x : list)
  #(x.field)
#end
 
#for(x : map)
  #(x.key)
  #(x.value)
#end
```
上图代码中展示了for指令迭代输出。第一个for指令是对list进行迭代输出，用法与java语法完全一样，第二个for指令是对map进行迭代，取值方式为item.key与item.value。

注意：当被迭代的目标为null时，不需要做null值判断，for指令会直接跳过null值，不进行迭代。

    for指令还支持对其状态进行获取，代码示例：
```
#for(x : listAaa)
  #(for.index)
  #(x.field)
  
  #for(x : listBbb)
     #(for.outer.index)
     #(for.index)
     #(x.field)
  #end
#end
```
以上代码中的#(for.index)、#(for.outer.index)是对for指令当前状态值进行获取，前者是获取当前for指令迭代的下标值(从0开始的整数)，后者是内层for指令获取上一层for指令的状态。这里注意for.outer这个固定的用法，专门用于在内层for指令中引用上层for指令状态。

    注意：for指令嵌套时，各自拥有自己的变量名作用域，规则与java语言一致，例如上例中的两个#(x.field)处在不同的for指令作用域内，会正确获取到所属作用域的变量值。

for指令支持的所有状态值如下示例：
```
#for(x : listAaa)
   #(for.size)    被迭代对象的 size 值
   #(for.index)   从 0 开始的下标值
   #(for.count)   从 1 开始的记数值
   #(for.first)   是否为第一次迭代
   #(for.last)    是否为最后一次迭代
   #(for.odd)     是否为奇数次迭代
   #(for.even)    是否为偶数次迭代
   #(for.outer)   引用上层 #for 指令状态
#end
```
具体用法在上面代码中用中文进行了说明，在此不再赘述。

除了Map、List以外，for指令还支持Collection、Iterator、array普通数组、Iterable、Enumeration、null值的迭代，用法在形式上与前面的List迭代完全相同，都是#for(id : target)的形式，对于null值，for指令会直接跳过不迭代。

此外，for指令还支持对任意类型进行迭代，此时仅仅是对该对象进行一次性迭代，如下所示：

```
#for(x : article)
   #(x.title)
#end
```

上例中的article为一个普通的java对象，而非集合类型对象，for循环会对该对象进行一次性迭代操作，for表达式中的x即为article对象本身，所以可以使用#(x.title)进行输出。

for 指令还支持#else分支语句，在for指令迭代次数为0时，将执行#else分支内部的语句，如下是示例：

```
#for(blog : blogList)
   #(blog.title)
#else
   您还没有写过博客，点击此处<a href="/blog/add">开博</a>
#end

```
以上代码中，当blogList.size() 为0或者blogList为null值时，也即迭代次数为0时，会执行#else分支，这种场景在web项目中极为常见。

最后，除了上面介绍的for指令迭代用法以外，还支持更常规的for语句形式，以下是代码示例：
```
#for(i = 0; i < 100; i++)
   #(i)
#end
```
与java语法基本一样，唯一的不同是变量声明不需要类型，直接用赋值语句即可，JFinal Template Engine中的变量是动态弱类型。

注意：以上这种形式的for语句，比前面的for迭代少了for.size与for.last两个状态，只支持如下几个状态：for.index、for.count、for.first、for.odd、for.even、for.outer

 #for 指令还支持 #continue、#break 指令，用法与java完全一致，在此不再赘述。

### #set 指令
set指令用于声明变量同时对其赋值，也可以是为已存在的变量进行赋值操作。set指令只接受赋值表达式，以及用逗号分隔的赋值表达式列表，如下是代码示例：
```
#set(x = 123)
#set(a = 1, b = 2, c = a + b)
#set(array[0] = 123)
#set(map["key"] = 456)
 
#(x)  #(c)  #(array[0])  #(map.key)  #(map["key"])

```
请注意，#for、#include、#define这三个指令会开启新的变量名作用域，#set指令会首先在本作用域中查找变量是否存在，如果存在则对本作用域中的变量进行操作，否则继续向上层作用域查找，找到则操作，如果找不到，则将变量定义在顶层作用域中，这样设计非常有利于在模板中传递变量的值。

当需要明确指定在本层作用域赋值时，可以使用#setLocal指令，该指令所需参数与用法与#set指令完全一样，只不过作用域被指定为当前作用域。#setLocal 指令通常用于#define、#include指令之内，用于实现模块化，从而希望其中的变量名不会与上层作用域发生命名上的冲突。








