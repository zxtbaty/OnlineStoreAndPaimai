package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallHomeIcon;
import org.jinyuanjava.litemall.db.service.LitemallHomeIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/homeicon")
@Validated
@Api(description = "后台管理/商城设置/首页导航:/admin/homeicon")
public class AdminHomeIconController {
    private final Log logger = LogFactory.getLog(AdminHomeIconController.class);

    @Autowired
    private LitemallHomeIconService homeIconService;

    @RequiresPermissions("admin:homeicon:list")
    @RequiresPermissionsDesc(menu={"商城设置","首页导航"},button = "查询")
    @GetMapping("list")
    public Object List(String name,String remark,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        List<LitemallHomeIcon> homeIconList=homeIconService.querySelective(name,remark,page,limit,sort);
        return ResponseUtil.okList(homeIconList);
    }


    private Object validate(LitemallHomeIcon homeIcon){
        String name=homeIcon.getName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        return null;
    }


    @RequiresPermissions("admin:homeicon:create")
    @RequiresPermissionsDesc(menu ={"商城设置","首页导航"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallHomeIcon homeIcon){
        Object error=validate(homeIcon);
        if(error!=null){
            return error;
        }
        homeIconService.add(homeIcon);
        return ResponseUtil.ok(homeIcon);
    }


    @RequiresPermissions("admin:homeicon:read")
    @RequiresPermissionsDesc(menu ={"商城设置","首页导航"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallHomeIcon litemallHomeIcon=homeIconService.findById(id);
        return ResponseUtil.ok(litemallHomeIcon);
    }

    @RequiresPermissions("admin:homeicon:update")
    @RequiresPermissionsDesc(menu = {"商城设置","首页导航"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallHomeIcon homeIcon){
        Object error=validate(homeIcon);
        if(error!=null){
            return error;
        }
        if(homeIconService.updateById(homeIcon)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(homeIcon);
    }


    @RequiresPermissions("admin:homeicon:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","首页导航"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallHomeIcon homeIcon){
        Integer id=homeIcon.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        homeIconService.deleteById(id);
        return ResponseUtil.ok();
    }

}
