package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.UserinfoDefAllinone;
import org.jinyuanjava.litemall.admin.service.AdminUserinfoDefService;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/userinfodef")
@Validated
@Api(description = "后台管理/消息管理/前端消息定义:/admin/userinfodef")
public class AdminUserinfoDefController {
    private final Log logger = LogFactory.getLog(AdminUserinfoDefController.class);

    @Autowired
    private AdminUserinfoDefService userinfoDefService;

    @RequiresPermissions("admin:userinfodef:list")
    @RequiresPermissionsDesc(menu={"消息管理","前端消息定义"},button = "查询")
    @GetMapping("list")
    public Object List(
            String typeCode,String typeName,String title,Integer expireFlag,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        return userinfoDefService.list(typeCode,typeName,title,expireFlag,page,limit,sort);
    }



    @RequiresPermissions("admin:userinfodef:create")
    @RequiresPermissionsDesc(menu ={"消息管理","前端消息定义"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody UserinfoDefAllinone store){

        return userinfoDefService.create(store);
    }


    @RequiresPermissions("admin:userinfodef:read")
    @RequiresPermissionsDesc(menu ={"消息管理","前端消息定义"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return userinfoDefService.detail(id);
    }

    @RequiresPermissions("admin:userinfodef:update")
    @RequiresPermissionsDesc(menu = {"消息管理","前端消息定义"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody UserinfoDefAllinone store){

        return userinfoDefService.update(store);
    }


    @RequiresPermissions("admin:userinfodef:delete")
    @RequiresPermissionsDesc(menu = {"消息管理","前端消息定义"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallUserinfoDef store){

        return userinfoDefService.delete(store);
    }

    /**
     * 通知用户
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:userinfodef:userinfopubs")
    @RequiresPermissionsDesc(menu = {"消息管理", "前端消息定义"}, button = "通知用户")
    @GetMapping("/userinfopubs")
    public Object getUserinfoPubs(@NotNull Integer id) {
        return userinfoDefService.getUserinfoPubs(id);
    }



}
