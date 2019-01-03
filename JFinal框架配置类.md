### 配置类中即
```
public class SystemConfig extends JFinalConfig {

```
的核心配置方法
```
 public void configPlugin(Plugins plugins) {
```
中；plugins.add(你的插件);add方法只是添加到Plugins的集合中，
调用各自的start方法是configPlugin执行完，jfinal框架的核心Config.java来统一调用的。

### 在配置类中读取数据库信息
```
 String sql = Db.getSql("dappTab.all");
        List<DappTab> list = DappTab.dao.find(sql);
        List<DAppTab> tabs = new ArrayList<>();
        DAppTab dApp = new DAppTab();
        for(DappTab dappTab : list){
            DAppTab dAppTab = new DAppTab();
            dAppTab.setCode(dappTab.getCode());
            dAppTab.setName(dappTab.getName());
            tabs.add(dAppTab);
        }
        dApp.setList(tabs);
```

还有更适合的方法是到DAO层中增加方法 然后在配置类中调用
DAO层：
```
public class DappTab extends BaseDappTab<DappTab> {
	public static final DappTab dao = new DappTab().dao();

	public void resetDAppTab(){
		List<DAppTab> tabList = DAppTab.getList();
		tabList.clear();
		Kv para = Kv.by("status", 1);
		SqlPara sqlPara = Db.getSqlPara("dappTab.all", para);
		List<DappTab> list = DappTab.dao.find(sqlPara);

		DAppTab dApp = new DAppTab();
		if(null != list || list.size() != 0){
			for(DappTab dapp : list){
				DAppTab dAppTab = new DAppTab();
				dAppTab.setCode(dapp.getCode());
				dAppTab.setName(dapp.getName());
				tabList.add(dAppTab);
			}
		}
		dApp.setList(tabList);
	}

}
```

配置类中：
```
@Override
    public void afterJFinalStart() {
        /*===== 读取dappTab ===*/
       DappTab.dao.resetDAppTab();

    }
```

不能将该方法放在service层，这样的话配置类中调用时@Inject 注入service层；这样调用方法会报错。
