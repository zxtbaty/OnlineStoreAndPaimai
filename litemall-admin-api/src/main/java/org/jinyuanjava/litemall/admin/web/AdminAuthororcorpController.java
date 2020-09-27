package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.AuthororcorpAllinone;
import org.jinyuanjava.litemall.admin.service.AdminAuthorcorpService;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorp;
import org.jinyuanjava.litemall.db.service.LitemallAuthororcorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/authororcorp")
@Validated
@Api(description = "后台管理/商城设置/出品人:/admin/authororcorp")
public class AdminAuthororcorpController {
    private final Log logger = LogFactory.getLog(AdminAuthororcorpController.class);

    @Autowired
    private LitemallAuthororcorpService litemallAuthororcorpService;

    @Autowired
    private AdminAuthorcorpService adminAuthorcorpService;

    @RequiresPermissions("admin:authororcorp:list")
    @RequiresPermissionsDesc(menu={"商城设置","出品人"},button = "查询")
    @GetMapping("list")
    public Object List(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
    ){

        return adminAuthorcorpService.list(name,page,limit,sort);
    }


    @RequiresPermissions("admin:authororcorp:queryall")
    @RequiresPermissionsDesc(menu={"商城设置","出品人"},button = "查询全部")
    @GetMapping("queryall")
    public Object queryAll(){
        List<LitemallAuthororcorp> authororcorps=litemallAuthororcorpService.queryAll();
        return ResponseUtil.okList(authororcorps);
    }

    @RequiresPermissions("admin:authororcorp:create")
    @RequiresPermissionsDesc(menu ={"商城设置","出品人"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody AuthororcorpAllinone authororcorpAllinone){
        return adminAuthorcorpService.create(authororcorpAllinone);
    }


    @RequiresPermissions("admin:authororcorp:read")
    @RequiresPermissionsDesc(menu ={"商城设置","出品人"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return adminAuthorcorpService.detail(id);
    }

    @RequiresPermissions("admin:authororcorp:update")
    @RequiresPermissionsDesc(menu = {"商城设置","出品人"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody AuthororcorpAllinone authororcorpAllinone){
        return adminAuthorcorpService.update(authororcorpAllinone);

    }


    @RequiresPermissions("admin:authororcorp:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","出品人"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAuthororcorp authororcorp){

        return adminAuthorcorpService.delete(authororcorp);
}


}
