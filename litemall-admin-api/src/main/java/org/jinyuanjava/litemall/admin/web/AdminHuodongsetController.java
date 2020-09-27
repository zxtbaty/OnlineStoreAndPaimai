package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;

import org.jinyuanjava.litemall.admin.dto.HuodongsetAllinone;
import org.jinyuanjava.litemall.admin.service.AdminHuodongsetService;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/huodongset")
@Validated
@Api(description = "后台管理/商城设置/商贸类型:/admin/huodongset")
public class AdminHuodongsetController {
    private final Log logger = LogFactory.getLog(AdminHuodongsetController.class);


    @Autowired
    private AdminHuodongsetService adminHuodongsetService;


    @RequiresPermissions("admin:huodongset:list")
    @RequiresPermissionsDesc(menu={"商城设置","活动设置"},button = "查询")
    @GetMapping("list")
    public Object List(String name,Boolean expireFlag,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        return adminHuodongsetService.list(name,expireFlag,page,limit,sort);

    }

    @RequiresPermissions("admin:huodongset:create")
    @RequiresPermissionsDesc(menu ={"商城设置","活动设置"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody HuodongsetAllinone huodongsetAllinone){

        return adminHuodongsetService.create(huodongsetAllinone);

    }


    @RequiresPermissions("admin:huodongset:read")
    @RequiresPermissionsDesc(menu ={"商城设置","活动设置"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return adminHuodongsetService.detail(id);

    }

    @RequiresPermissions("admin:huodongset:update")
    @RequiresPermissionsDesc(menu = {"商城设置","活动设置"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody HuodongsetAllinone huodongsetAllinone){
        return adminHuodongsetService.update(huodongsetAllinone);

    }


    @RequiresPermissions("admin:huodongset:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","活动设置"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallHuodongMain company){
        return adminHuodongsetService.delete(company);

    }



}
