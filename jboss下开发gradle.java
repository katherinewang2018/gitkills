package com.eshanren.system;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.eshanren.constant.FileConst;
import com.eshanren.ext.api.ApiKit;
import com.eshanren.ext.plugin.ZbusMQPlugin;
import com.eshanren.ext.plugin.ZbusRpcServerPlugin;
import com.eshanren.handler.UrlSeoHandler;
import com.eshanren.interceptor.LoginSessionInterceptor;
import com.eshanren.model._MappingKit;
import com.eshanren.render.ErrorRenderFactory;
import com.eshanren.route.ApiRoutes;
import com.eshanren.route.ManageRoutes;
import com.eshanren.service.LoginService;
import com.jfinal.config.*;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderManager;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

import java.util.Properties;

/**
 * 系统配置
 *
 * @author WWF
 */
public class SystemConfig extends JFinalConfig {

    public static void main(String[] args) {

        UndertowServer.create(SystemConfig.class)
                .addHotSwapClassPrefix("com.eshanren.").setResourcePath("D:\\Workspace\\bepal-discover\\web")
                .start();

    }

    /**
     * 加载开发环境配置
     */
    private static Prop p = PropKit.use("database.properties");

    @Override
    public void configConstant(Constants me) {
        //加载配置文件
        me.setDevMode(p.getBoolean("dev", false));

        //全局错误回复
        RenderManager.me().setRenderFactory(new ErrorRenderFactory());

        //api工具类配置文件加载
        ApiKit.init();

        //域名
        FileConst.WEBSITE = p.get("website", "https://discover.bepal.pro");
    }

    @Override
    public void configRoute(Routes me) {
        me.add(new ApiRoutes());
        me.add(new ManageRoutes());
    }

    @Override
    public void configEngine(Engine me) {
        me.setDevMode(true);
        me.addSharedFunction("/WEB-INF/views/manage/common/common.layout.html");
        me.addSharedFunction("/WEB-INF/views/manage/common/common.paginate.html");
    }

    @Override
    public void configPlugin(Plugins plugins) {

        //database
        DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
        druidPlugin.setDriverClass(p.get("jdbcDriverClass"));
        // StatFilter提供JDBC层的统计信息
        druidPlugin.addFilter(new StatFilter());
        // WallFilter的功能是防御SQL注入攻击
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(JdbcUtils.MYSQL);
        //输出有sql注入疑问的sql
        wallFilter.setLogViolation(true);
        druidPlugin.addFilter(wallFilter);
        plugins.add(druidPlugin);

        // database model map
        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
        activeRecordPlugin.setDialect(new MysqlDialect());
        activeRecordPlugin.setShowSql(p.getBoolean("showSql", false));

        //添加sql模版
       /* String rootClassPath = PathKit.getRootClassPath();
        activeRecordPlugin.setBaseSqlTemplatePath(rootClassPath + "/sqls");
        activeRecordPlugin.addSqlTemplate("all.sql");*/

        activeRecordPlugin.getEngine().setToClassPathSourceFactory();
        activeRecordPlugin.setBaseSqlTemplatePath(null);
        activeRecordPlugin.addSqlTemplate("sqls/all.sql");

        plugins.add(activeRecordPlugin);
        _MappingKit.mapping(activeRecordPlugin);

        plugins.add(new EhCachePlugin());
        plugins.add(new Cron4jPlugin(PropKit.use("task.properties")));

        //zbus rpc
        Properties zbusProp = p.getProperties();
        ZbusRpcServerPlugin zbusRPCServerPlugin = new ZbusRpcServerPlugin(zbusProp);
        plugins.add(zbusRPCServerPlugin);

        //zbus mq 插件
        String serverAddress = zbusProp.getProperty("zbus.serverAddress");
        ZbusMQPlugin zbusMQPlugin = new ZbusMQPlugin(serverAddress);
        plugins.add(zbusMQPlugin);
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers me) {
        // index、detail 两类 action 的 url seo
        me.add(new UrlSeoHandler());
    }

    /**
     * 本方法会在 jfinal 启动过程完成之后被回调，详见 jfinal 手册
     */
    @Override
    public void afterJFinalStart() {
        // 调用不带参的 renderJson() 时，排除对 loginAccount、remind 的 json 转换
        JsonRender.addExcludedAttrs(
                LoginService.loginAccountCacheName,
                LoginSessionInterceptor.remindKey
        );

    }

}

/* *
 * *
 * * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━神兽出没━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * * 　　　┏┓　　　┏┓
 * * 　　┏┛┻━━━┛┻┓
 * * 　　┃　　　　　　　┃
 * * 　　┃　　　━　　　┃
 * * 　　┃　┳┛　┗┳　┃
 * * 　　┃　　　　　　　┃
 * * 　　┃　　　┻　　　┃
 * * 　　┃　　　　　　　┃
 * * 　　┗━┓　　　┏━┛Code is far away from bug with the god animal protecting
 * * 　　　　┃　　　┃      神兽保佑,代码无bug
 * * 　　　　┃　　　┃
 * * 　　　　┃　　　┗━━━┓
 * * 　　　　┃　　　　　　　┣┓
 * * 　　　　┃　　　　　　　┏┛
 * * 　　　　┗┓┓┏━┳┓┏┛
 * * 　　　　　┃┫┫　┃┫┫
 * * 　　　　　┗┻┛　┗┻┛
 * *
 * *
 * * 　　　　　　　　┏┓　　　┏┓
 * * 　　　　　　　┏┛┻━━━┛┻┓
 * * 　　　　　　　┃　　　　　　　┃
 * * 　　　　　　　┃　　　━　　　┃
 * * 　　　　　　　┃　＞　　　＜　┃
 * * 　　　　　　　┃　　　　　　　┃
 * * 　　　　　　　┃...　⌒　...　┃
 * * 　　　　　　　┃　　　　　　　┃
 * * 　　　　　　　┗━┓　　　┏━┛
 * * 　　　　　　　　　┃　　　┃　Code is far away from bug with the god animal protecting
 * * 　　　　　　　　　┃　　　┃     神兽保佑,代码无bug
 * * 　　　　　　　　　┃　　　┃
 * * 　　　　　　　　　┃　　　┃
 * * 　　　　　　　　　┃　　　┃
 * * 　　　　　　　　　┃　　　┃
 * * 　　　　　　　　　┃　　　┗━━━┓
 * * 　　　　　　　　　┃　　　　　　　┣┓
 * * 　　　　　　　　　┃　　　　　　　┏┛
 * * 　　　　　　　　　┗┓┓┏━┳┓┏┛
 * * 　　　　　　　　　　┃┫┫　┃┫┫
 * * 　　　　　　　　　　┗┻┛　┗┻┛
 * *
 * *
 * *
 * *　　　　　　　　┏┓　　　┏┓+ +
 * *　　　　　　　┏┛┻━━━┛┻┓ + +
 * *　　　　　　　┃　　　　　　　┃
 * *　　　　　　　┃　　　━　　　┃ ++ + + +
 * *　　　　　　 ████━████ ┃+
 * *　　　　　　　┃　　　　　　　┃ +
 * *　　　　　　　┃　　　┻　　　┃
 * *　　　　　　　┃　　　　　　　┃ + +
 * *　　　　　　　┗━┓　　　┏━┛
 * *　　　　　　　　　┃　　　┃
 * *　　　　　　　　　┃　　　┃ + + + +
 * *　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the god animal protecting
 * *　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * *　　　　　　　　　┃　　　┃
 * *　　　　　　　　　┃　　　┃　　+
 * *　　　　　　　　　┃　 　　┗━━━┓ + +
 * *　　　　　　　　　┃ 　　　　　　　┣┓
 * *　　　　　　　　　┃ 　　　　　　　┏┛
 * *　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * *　　　　　　　　　　┃┫┫　┃┫┫
 * *　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * *
 * *
 * *                       _ooOoo_
 * *                      o8888888o
 * *                      88" . "88
 * *                      (| -_- |)
 * *                      O\  =  /O
 * *                   ____/`---'\____
 * *                 .'  \\|     |//  `.
 * *                /  \\|||  :  |||//  \
 * *               /  _||||| -:- |||||-  \
 * *               |   | \\\  -  /// |   |
 * *               | \_|  ''\---/''  |   |
 * *               \  .-\__  `-`  ___/-. /
 * *             ___`. .'  /--.--\  `. . __
 * *          ."" '<  `.___\_<|>_/___.'  >'"".
 * *         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * *         \  \ `-.   \_ __\ /__ _/   .-` /  /
 * *    ======`-.____`-.___\_____/___.-`____.-'======
 * *                       `=---='
 * *    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * *             佛祖保佑       永无BUG
 * *
 */