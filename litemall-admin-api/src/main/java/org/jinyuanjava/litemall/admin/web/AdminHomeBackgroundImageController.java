package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallHomeBackgroundImage;
import org.jinyuanjava.litemall.db.service.LitemallHomeBackgroundImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/homebackgroundimage")
@Validated
@Api(description = "后台管理/商城设置/首页背景:/admin/homebackgroundimage")
public class AdminHomeBackgroundImageController {
    private final Log logger = LogFactory.getLog(AdminHomeBackgroundImageController.class);

    @Autowired
    private LitemallHomeBackgroundImageService homeBackgroundImageService;

    @RequiresPermissions("admin:homebackgroundimage:list")
    @RequiresPermissionsDesc(menu={"商城设置","首页背景"},button = "查询")
    @GetMapping("list")
    public Object List(String name,String remark,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        List<LitemallHomeBackgroundImage> homeIconList=homeBackgroundImageService.querySelective(name,remark,page,limit,sort);
        return ResponseUtil.okList(homeIconList);
    }


    private Object validate(LitemallHomeBackgroundImage homeBackgroundImage){
        String name=homeBackgroundImage.getName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        return null;
    }


    @RequiresPermissions("admin:homebackgroundimage:create")
    @RequiresPermissionsDesc(menu ={"商城设置","首页背景"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallHomeBackgroundImage homeBackgroundImage){
        Object error=validate(homeBackgroundImage);
        if(error!=null){
            return error;
        }
        homeBackgroundImageService.add(homeBackgroundImage);
        return ResponseUtil.ok(homeBackgroundImage);
    }


    @RequiresPermissions("admin:homebackgroundimage:read")
    @RequiresPermissionsDesc(menu ={"商城设置","首页背景"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallHomeBackgroundImage litemallHomeBackgroundImage=homeBackgroundImageService.findById(id);
        return ResponseUtil.ok(litemallHomeBackgroundImage);
    }

    @RequiresPermissions("admin:homebackgroundimage:update")
    @RequiresPermissionsDesc(menu = {"商城设置","首页背景"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallHomeBackgroundImage homeBackgroundImage){
        Object error=validate(homeBackgroundImage);
        if(error!=null){
            return error;
        }
        if(homeBackgroundImageService.updateById(homeBackgroundImage)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(homeBackgroundImage);
    }


    @RequiresPermissions("admin:homebackgroundimage:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","首页背景"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallHomeBackgroundImage homeBackgroundImage){
        Integer id=homeBackgroundImage.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        homeBackgroundImageService.deleteById(id);
        return ResponseUtil.ok();
    }

}
