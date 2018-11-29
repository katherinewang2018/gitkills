## Litecoin 钱包的窗口调试
1.跳过下载安装（教程网上有）
2.在钱包主界面有 帮助 选项，点击帮助-->调试窗口--> 控制台  在最底部有一个 > 符号，旁边就是一个输入框，这里就是用于输入命令的命令行。
3. 钱包的命令：
直接输入help 回车
```
== Blockchain ==
getbestblockhash
getblock "blockhash" ( verbosity ) 
getblockchaininfo  //查询区块链详情
getblockcount
getblockhash height
getblockheader "hash" ( verbose )
getchaintips
getchaintxstats ( nblocks blockhash )
getdifficulty
getmempoolancestors txid (verbose)
getmempooldescendants txid (verbose)
getmempoolentry txid
getmempoolinfo
getrawmempool ( verbose )
gettxout "txid" n ( include_mempool )
gettxoutproof ["txid",...] ( blockhash )
gettxoutsetinfo
preciousblock "blockhash"
pruneblockchain
savemempool
verifychain ( checklevel nblocks )
verifytxoutproof "proof"

== Control ==
getmemoryinfo ("mode")
help ( "command" )
logging ( <include> <exclude> )
stop
uptime

== Generating ==
generate nblocks ( maxtries )
generatetoaddress nblocks address (maxtries)

== Mining ==
getblocktemplate ( TemplateRequest )
getmininginfo
getnetworkhashps ( nblocks height )
prioritisetransaction <txid> <dummy value> <fee delta>
submitblock "hexdata"  ( "dummy" )

== Network ==
addnode "node" "add|remove|onetry"
clearbanned
disconnectnode "[address]" [nodeid]
getaddednodeinfo ( "node" )
getconnectioncount
getnettotals
getnetworkinfo
getpeerinfo
listbanned
ping
setban "subnet" "add|remove" (bantime) (absolute)
setnetworkactive true|false

== Rawtransactions ==
combinerawtransaction ["hexstring",...]
createrawtransaction [{"txid":"id","vout":n},...] {"address":amount,"data":"hex",...} ( locktime ) ( replaceable )
decoderawtransaction "hexstring" ( iswitness )
decodescript "hexstring"
fundrawtransaction "hexstring" ( options iswitness )
getrawtransaction "txid" ( verbose "blockhash" )
sendrawtransaction "hexstring" ( allowhighfees )
signrawtransaction "hexstring" ( [{"txid":"id","vout":n,"scriptPubKey":"hex","redeemScript":"hex"},...] ["privatekey1",...] sighashtype )

== Util ==
createmultisig nrequired ["key",...]
estimatefee nblocks
estimatesmartfee conf_target ("estimate_mode")
signmessagewithprivkey "privkey" "message"
validateaddress "address"  //验证 地址是否有效 返回详细信息
verifymessage "address" "signature" "message"

== Wallet == 钱包命令
abandontransaction "txid"
abortrescan
addmultisigaddress nrequired ["key",...] ( "account" "address_type" )
backupwallet "destination"
bumpfee "txid" ( options ) 
dumpprivkey "address"
dumpwallet "filename"
encryptwallet "passphrase"
getaccount "address"
getaccountaddress "account"
getaddressesbyaccount "account"
getbalance ( "account" minconf include_watchonly )
getnewaddress ( "account" "address_type" )
getrawchangeaddress ( "address_type" )
getreceivedbyaccount "account" ( minconf )
getreceivedbyaddress "address" ( minconf )
gettransaction "txid" ( include_watchonly )
getunconfirmedbalance
getwalletinfo  // 有关钱包的详细信息
importaddress "address" ( "label" rescan p2sh )
importmulti "requests" ( "options" )
importprivkey "privkey" ( "label" ) ( rescan )
importprunedfunds
importpubkey "pubkey" ( "label" rescan )
importwallet "filename"
keypoolrefill ( newsize )
listaccounts ( minconf include_watchonly)
listaddressgroupings
listlockunspent
listreceivedbyaccount ( minconf include_empty include_watchonly)
listreceivedbyaddress ( minconf include_empty include_watchonly)
listsinceblock ( "blockhash" target_confirmations include_watchonly include_removed )
listtransactions ( "account" count skip include_watchonly)
listunspent ( minconf maxconf  ["addresses",...] [include_unsafe] [query_options])
listwallets
lockunspent unlock ([{"txid":"txid","vout":n},...])
move "fromaccount" "toaccount" amount ( minconf "comment" )
removeprunedfunds "txid"
rescanblockchain ("start_height") ("stop_height")
sendfrom "fromaccount" "toaddress" amount ( minconf "comment" "comment_to" )
sendmany "fromaccount" {"address":amount,...} ( minconf "comment" ["address",...] replaceable conf_target "estimate_mode")
sendtoaddress "address" amount ( "comment" "comment_to" subtractfeefromamount replaceable conf_target "estimate_mode")
setaccount "address" "account"
settxfee amount
signmessage "address" "message"
walletlock
walletpassphrase "passphrase" timeout
walletpassphrasechange "oldpassphrase" "newpassphrase"

```

### 输入getblocktemplate 返回数据

```
￼
{
  "capabilities": [
    "proposal"
  ],
  "version": 536870912,
  "rules": [
    "csv",
    "segwit"
  ],
  "vbavailable": {
  },
  "vbrequired": 0,
  "previousblockhash": "627affb321eee28efdd7f3942840a98e2f4c51aade5fc6690280c79e9f3b0cc9",
  "transactions": [
    {
      "data": "0100000001346bee6b7084d6fa0de010032ecd857d6d6b56520bcaf39a01852f34ebf3b277010000006a47304402201f47399d368b19311a5ad714fc5c8093ef0c28af3c233fd30320ca0f2ed57a3a0220539bce2e48e6a9d608d9cabf66b80f64943958192c92cdc7e288ab7aa8ffacc10121034c8e07fe3772b56f1e81b4269d76b159865f2746e03b966acf4c30316bda64f6feffffff02c02163cd160000001976a9143f60a3a421494e84b722b90d30fb69386668300188ac203ed7290000000017a9144719cc4591afdb1434737617629d01b571884bab8716691700",
      "txid": "85b712c6d8497915542c5a2f71f04912ff820204737f9de8afc6b912dc94961a",
      "hash": "85b712c6d8497915542c5a2f71f04912ff820204737f9de8afc6b912dc94961a",
      "depends": [
      ],
      "fee": 224000,
      "sigops": 4,
      "weight": 892
    }
    
  ],
  "coinbaseaux": {
    "flags": ""
  },
  "coinbasevalue": 2501233461,
  "longpollid": "627affb321eee28efdd7f3942840a98e2f4c51aade5fc6690280c79e9f3b0cc91539461",
  "target": "00000000000002c1e50000000000000000000000000000000000000000000000",
  "mintime": 1543409435,
  "mutable": [
    "time",
    "transactions",
    "prevblock"
  ],
  "noncerange": "00000000ffffffff",
  "sigoplimit": 80000,
  "sizelimit": 4000000,
  "weightlimit": 4000000,
  "curtime": 1543410468,
  "bits": "1a02c1e5",
  "height": 1534232
}

```