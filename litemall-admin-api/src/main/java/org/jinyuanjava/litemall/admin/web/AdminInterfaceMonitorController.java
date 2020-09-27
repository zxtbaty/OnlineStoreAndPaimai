package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallInterfaceMonitor;
import org.jinyuanjava.litemall.db.service.LitemallInterfaceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/interfacemonitor")
@Validated
@Api(description = "后台管理/消息管理/接口消息监控:/admin/interfacemonitor")
public class AdminInterfaceMonitorController {
    private final Log logger = LogFactory.getLog(AdminInterfaceMonitorController.class);

    @Autowired
    private LitemallInterfaceMonitorService interfaceMonitorService;

    @RequiresPermissions("admin:interfacemonitor:list")
    @RequiresPermissionsDesc(menu={"消息管理","接口消息监控"},button = "查询")
    @GetMapping("list")
    public Object List( String logSourceCode,String logDirectionCode,String logTypeCode,String logContent,
                         Integer errorFlag,String startDate,String endDate,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        List<LitemallInterfaceMonitor> homeIconList=interfaceMonitorService.querySelective(
                logSourceCode,logDirectionCode,logTypeCode,logContent,errorFlag,startDate,endDate,page,limit,sort);
        return ResponseUtil.okList(homeIconList);
    }

    @RequiresPermissions("admin:interfacemonitor:read")
    @RequiresPermissionsDesc(menu={"消息管理","接口消息监控"},button = "详情")
    @GetMapping("read")
    public Object read(@NotNull Integer id){
        LitemallInterfaceMonitor interfaceMonitor=interfaceMonitorService.findById(id);
        return ResponseUtil.ok(interfaceMonitor);
    }

}
