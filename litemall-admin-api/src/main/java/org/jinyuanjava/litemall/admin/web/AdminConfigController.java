package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.core.system.SystemInistService;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/config")
@Validated
@Api(description = "后台管理/配置管理/商场配置:/admin/config")
public class AdminConfigController {
    private final Log logger = LogFactory.getLog(AdminConfigController.class);

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    @Autowired
    private SystemInistService systemInistService;

    @RequiresPermissions("admin:config:mall:list")
    @RequiresPermissionsDesc(menu={"配置管理" , "商场配置"}, button="详情")
    @GetMapping("/mall")
    public Object listMall() {
        Map<String, String> data = systemConfigService.listMall();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:mall:updateConfigs")
    @RequiresPermissionsDesc(menu={"配置管理" , "商场配置"}, button="编辑")
    @PostMapping("/mall")
    public Object updateMall(@RequestBody String body ) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:express:list")
    @RequiresPermissionsDesc(menu={"配置管理" , "运费配置"}, button="详情")
    @GetMapping("/express")
    public Object listExpress() {
        Map<String, String> data = systemConfigService.listExpress();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:express:updateConfigs")
    @RequiresPermissionsDesc(menu={"配置管理" , "运费配置"}, button="编辑")
    @PostMapping("/express")
    public Object updateExpress(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:order:list")
    @RequiresPermissionsDesc(menu={"配置管理" , "订单配置"}, button="详情")
    @GetMapping("/order")
    public Object lisOrder() {
        Map<String, String> data = systemConfigService.listOrder();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:order:updateConfigs")
    @RequiresPermissionsDesc(menu={"配置管理" , "订单配置"}, button="编辑")
    @PostMapping("/order")
    public Object updateOrder(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        //后台重新加载配置
        systemInistService.initConfigs();
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:wx:list")
    @RequiresPermissionsDesc(menu={"配置管理" , "小程序配置"}, button="详情")
    @GetMapping("/wx")
    public Object listWx() {
        Map<String, String> data = systemConfigService.listWx();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:wx:updateConfigs")
    @RequiresPermissionsDesc(menu={"配置管理" , "小程序配置"}, button="编辑")
    @PostMapping("/wx")
    public Object updateWx(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:config:weishang:list")
    @RequiresPermissionsDesc(menu={"配置管理" , "首页配置"}, button="详情")
    @GetMapping("/weishang")
    public Object listWeiShang() {
        Map<String, String> data = systemConfigService.listWeiShang();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:weishang:updateConfigs")
    @RequiresPermissionsDesc(menu={"配置管理" , "首页配置"}, button="编辑")
    @PostMapping("/weishang")
    public Object updateWeiShang(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }


}
