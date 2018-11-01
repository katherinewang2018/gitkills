### 登陆时后端获取token的问题

客户端每次向服务器请求API接口时候,都要带上Token. 

服务端不需要保存Token，只需要对Token中携带的信息进行验证即可；
无论客户端访问后台的那台服务器，只要可以通过用户信息的验证即可。

我们可以看到，JWT适合用于向Web应用传递一些非敏感信息。例如在上面提到的完成加好友的操作，还有诸如下订单的操作等等。

其实JWT还经常用于设计用户认证和授权系统，甚至实现Web应用的单点登录。


```
public class DemoInterceptor extends Interceptor {  //登陆时的拦截器
    @Override
    public void intercept(Invocation inv) {
        Ret ret = Ret.ok();
        ret.set("state", "overdue");
        ret.set(MESSAGE, "登录超时,Token过期");
        BaseController controller = (BaseController) inv.getController();
        String token = controller.getHeader("xx-token");  //获取请求发起时要携带xx-token这个参数 即后端生成的token 
        Member member = null;
        try {

            member = J2CacheUtil.getMemberCache(token); // 根据这个token从缓存中获得对应的value即用户
        }catch (Exception e) {
            /*由于出现老数据为Record的形式，需要catch转换异常*/
            e.printStackTrace();
        }
        /*token有效期为1个月，cache缓存时间为2个小时，但cache中不存在的时候重新解析token去获取数据  token存储在ehcache中   */
        if (member == null) {
            String phone = SecurityUtil.decodeToken(token);
            if (phone != null) {
                MemberService memberService = new MemberService();
                member = memberService.searchMemberByPhone(phone);   
                J2CacheUtil.setMemberCache(token,member); // 存回缓存
            }
        }

        if (member == null) {
            controller.renderJson(ret);
        } else {
            controller.setAttr(MemberConstant.MEMBER_ATTR, member);
            inv.invoke();
        }
    }

}
```
登陆成功时 生成token 并设置有效期
```
 String token = SecurityUtil.generateToken(member.getPhone(), 30 * 24 * 60 * 60); 
```


为什么使用Token验证：

  在Web领域基于Token的身份验证随处可见。在大多数使用Web API的互联网公司中，tokens 是多用户下处理认证的最佳方式。

  以下几点特性会让你在程序中使用基于Token的身份验证

  1.无状态、可扩展  Token 可以是无状态的，可以在多个服务间共享。服务器生成的Token 返回给客户端，一般客户端将Token保存到本地浏览器，一般是cookie中。

  2.支持移动设备

  3.跨程序调用

  4.安全  Token 可以避免 CSRF 攻击

  5.Token 可以在服务端持久化（比如存入数据库）

  6.和cookie 类似 需要设置有效时间

  7.为了解决在操作过程不能让用户感到 Token 失效这个问题，有一种方案是在服务器端保存 Token 状态，用户每次操作都会自动刷新（推迟） Token 的过期时间——Session 就是采用这种策略来保持用户登录状态的。

  8.如果 Token 的过期时间被持久化到数据库或文件，代价就更大了。所以通常为了提升效率，减少消耗，会把 Token 的过期时保存在缓存或者内存中。（访问量很大时）

有关token更多资料
http://www.cnblogs.com/Ceri/p/7767586.html
