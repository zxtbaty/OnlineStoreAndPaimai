package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallLog;
import org.jinyuanjava.litemall.db.service.LitemallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/log")
@Validated
@Api(description = "后台管理/系统管理/登陆日志:/admin/log")
public class AdminLogController {
    private final Log logger = LogFactory.getLog(AdminLogController.class);

    @Autowired
    private LitemallLogService logService;

    @RequiresPermissions("admin:log:list")
    @RequiresPermissionsDesc(menu={"系统管理" , "登陆日志"}, button="查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallLog> logList = logService.querySelective(name, page, limit, sort);
        return ResponseUtil.okList(logList);
    }
}
