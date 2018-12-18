## EOS交易打包
1.记录几个比较有干货的网页
https://eospark.com   有关eos交易的情况

https://eostracker.io/accounts/ibepalchenyu  eos交易记录

https://github.com/BlockchainTranslator/EOS/blob/master/wiki/Database-Schema.md#3-transaction  可以以这个为主 数据结构介绍

https://www.jianshu.com/p/86fb466c9354 介绍eos交易的数据结构

http://nm1024.com/506.html eos学习交流

2.构建eos交易的数据结构 
eos没有java语言的交易实现，公司大佬写了sdk
```
//私钥用于加密交易数据
            EOSECKey eosecKey = EOSECKey.fromPrivateKey("私钥"); // EOSECKey 就是私钥

            // 创建交易并签名
            Transaction transaction = new Transaction();
            transaction.chainID = com.mypxq.bepal.base.crypto.Hex.fromHexString(chainId);
            transaction.blockNum = blockNum;
            transaction.blockPrefix = blockPrefix;
            transaction.netUsageWords = 0;
            transaction.kcpuUsage = 0;
            transaction.delaySec = 0;
            transaction.expiration = System.currentTimeMillis()/1000 + 600;  //以秒为单位 过期时间最长为一个小时 这里设置为10分钟

            String sendfrom = trans.getFromAddress();
            String code = "eosio.token";
            String sendto = trans.getToAddress();
            String toAmount = trans.getToAmount();
            StringBuffer amount = new StringBuffer();
            amount.append(toAmount).append(" ").append("EOS"); //eos主币交易使用EOS单位 代币使用代币的单位
            //System.out.println("金额："+amount.toString()); //注意eos是小数点后4位

            Action txmessage = new Action();
            txmessage.account = new AccountName(code);
            txmessage.name = new AccountName("transfer");
            txmessage.authorization.add(new AccountPermission(sendfrom, "active"));
            transaction.actions.add(txmessage);

            TxMessageData mdata = new TxMessageData();
            mdata.from = new AccountName(sendfrom);
            mdata.to = new AccountName(sendto);
            mdata.amount = new Asset(amount.toString());//Asset类中对amount进行处理
            mdata.data = trans.getInputData().getBytes("UTF-8");//eos交易中的MOME 即备注信息
            txmessage.data = mdata;

            /*交易txid*/
            String txid = Hex.toHexString(transaction.getTxID());
            System.out.println("txid:"+txid);

            /*签名*/  //ECSign 有点不知所措。。。
            ECSign sign = eosecKey.sign(transaction.getSignHash()); //用私钥 对交易数据进行签名
            byte[] bytes = sign.encoding(true); //eos交易参数为true
            transaction.signature.add(bytes); // 将签名 添加到交易的数据结构中

```

报错信息：
过期时间设置过长：
```
"{\"code\":500,\"message\":\"Internal Service Error\",\"error\":{\"code\":3040006,\"name\":\"tx_exp_too_far_exception\",\"what\":\"Transaction Expiration Too Far\",\"details\":[{\"message\":\"Transaction expiration is too far in the future relative to the reference time of 2018-12-18T06:14:12.000, expiration is 2072-01-29T07:57:31 and the maximum transaction lifetime is 3600 seconds\",\"file\":\"controller.cpp\",\"line_number\":2061,\"method\":\"validate_expiration\"},{\"message\":\"\",\"file\":\"controller.cpp\",\"line_number\":2062,\"method\":\"validate_expiration\"}]}}"}]
```

签名错误
```
id":"","remarks":"bepal-ee-api"},"error":"{\"code\":500,\"message\":\"Internal Service Error\",\"error\":{\"code\":0,\"name\":\"exception\",\"what\":\"unspecified\",\"details\":[{\"message\":\"unable to reconstruct public key from signature\",\"file\":\"elliptic_secp256k1.cpp\",\"line_number\":160,\"method\":\"public_key\"},{\"message\":\"\",\"file\":\"transaction.cpp\",\"line_number\":121,\"method\":\"get_signature_keys\"}]}}"}]
```

这个报错怀疑与金额和单位有关 eos一定是小数点后四位 eos交易和eos代币的单位不同
```
{\"code\":0,\"name\":\"exception\",\"what\":\"unspecified\",\"details\":[{\"message\":\"unable to reconstruct public key from signature\",\"file\":\"elliptic_secp256k1.cpp\",\"line_number\":160,\"method\":\"public_key\"},{\"message\":\"\",\"file\":\"transaction.cpp\",\"line_number\":121,\"method\":\"get_signature_keys\"}]}}"}
```

这个应该是账号的资源不够 eos转账不需要想比特币和以太坊需要旷工费，但是eos转账需要消耗cpu和RAM ，这些资源是通过质押eos来获得的。
```
{\"code\":3081001,\"name\":\"leeway_deadline_exception\",\"what\":\"Transaction reached the deadline set due to leeway on account CPU limits\",\"details\":[{\"message\":\"the transaction was unable to complete by deadline, but it is possible it could have succeeded if it were allowed to run to completion\",\"file\":\"transaction_context.cpp\",\"line_number\":461,\"method\":\"checktime\"},{\"message\":\"pending console output: \",\"file\":\"apply_context.cpp\",\"line_number\":72,\"method\":\"exec_one\"}]}}"}]
```

这个没有余额 的报错
```
{\"code\":500,\"message\":\"Internal Service Error\",\"error\":{\"code\":3050003,\"name\":\"eosio_assert_message_exception\",\"what\":\"eosio_assert_message assertion failure\",\"details\":[{\"message\":\"assertion failure with message: no balance object found\",\"file\":\"wasm_interface.cpp\",\"line_number\":934,\"method\":\"eosio_assert\"},{\"message\":\"pending console output: \",\"file\":\"apply_context.cpp\",\"line_number\":72,\"method\":\"exec_one\"}]}}"}
```

代币的单位错误
```
[{"msg":"交易失败","code":0,"data":{"isToken":1,"coinIndex":6012,"txid":"","remarks":"bepal-ee-api"},"error":"eos.saltblock.io: Name or service not known"}]
```

账户中的资源不足
```
{\"code\":3080004,\"name\":\"tx_cpu_usage_exceeded\",\"what\":\"Transaction exceeded the current CPU usage limit imposed on the transaction\",\"details\":[{\"message\":\"billed CPU time (293 us) is greater than the maximum billable CPU time for the transaction (26 us)\",\"file\":\"transaction_context.cpp\",\"line_number\":520,\"method\":\"validate_cpu_usage_to_bill\"}]}}"}
```

3.EOS数据结构
Transaction
```
public byte[] chainID;
    public long expiration;
    public int blockNum;
    public long blockPrefix;
    public int netUsageWords;
    public int kcpuUsage;
    public int delaySec;
    public List<Action> contextFreeActions = new ArrayList();
    public List<Action> actions = new ArrayList();
    public TreeMap<Integer, String> extensionsType = new TreeMap(new Transaction.DataComparator());
    public List<byte[]> signature = new ArrayList();
```
包括解析hash 生成hash等等方法

Action
```
 public AccountName account;
    public AccountName name;
    public List<AccountPermission> authorization = new ArrayList();
    public MessageData data;

```
TxMessageData
```
 public AccountName from;
    public AccountName to;
    public Asset amount;
    public byte[] data;
```


