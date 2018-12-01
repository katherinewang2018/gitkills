## Ethereum tokens 以太坊代币
以太坊可以创建任何智能合约，包括可以表示数字资产的智能合约，而这些数字资产被称为以太坊代币。

数字货币的种类纷繁复杂，除了比特币、莱特币、Zcash、bitshares 等众多老牌币种, 都是独自的链，在自己的链上运行着自己特有的币外，另外有一种平台型代币，他们是依托以太坊而创建的，没有自己的链，而是运行在以太坊之上。

市面上，十有八九的数字货币都属于“代币”的类型，通过以太坊平台来发行的，这类数字货币通常是ERC-20代币。

简单地说，任何 ERC-20 代币都能立即兼容以太坊钱包（几乎所有支持以太币的钱包，包括Jaxx、MEW、imToken等，也支持 erc-20的代币），由于交易所已经知道这些代币是如何操作的，它们可以很容易地整合这些代币。这就意味着，在很多情况下，这些代币都是可以立即进行交易的。

代币标准是由一系列的函数构成的。

代码也很简单。

totalSupply   获得代币总供应量。
```
function totalSupply() constant returns (uint256 totalSupply)
```
 
balanceOf     获得账户所有者余额
```
function balanceOf(address _owner) constant returns (uint256 balance)
```
transfer  从某地址到另一个地址，通常是一个交易所提币的操作。
```
function transferFrom(address _from, address _to, uint256 _value) returns (bool success)
```
approve  批准花费代币。
```
function approve(address _spender, uint256 _value) returns (bool success)
```
allowance  是一个查询函数，返回交易所上的这个地址可以提多少币的结果。
```
function allowance(address _owner, address _spender) constant returns (uint256 remaining)
```
### 两个Events

Transfer   transfer或者transferFrom被调用的话，触发转账的动作。
```
event Transfer(address indexed _from, address indexed _to, uint256 _value)
```

Approval    表示相应的提币的请求被同意，可以正式提币了。
```
event Approval(address indexed _owner, address indexed _spender, uint256 _value)
```

