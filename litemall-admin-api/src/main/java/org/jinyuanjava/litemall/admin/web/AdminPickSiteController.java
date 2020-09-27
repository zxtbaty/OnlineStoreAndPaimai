package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallPickSite;
import org.jinyuanjava.litemall.db.service.LitemallPickSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/picksite")
@Validated
@Api(description = "后台管理/商城设置/自提货点:/admin/picksite")
public class AdminPickSiteController {
    private final Log logger = LogFactory.getLog(AdminPickSiteController.class);

    @Autowired
    private LitemallPickSiteService pickSiteService;

    @RequiresPermissions("admin:picksite:list")
    @RequiresPermissionsDesc(menu={"商城设置","自提货点"},button = "查询")
    @GetMapping("list")
    public Object List(String name,String remark,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
    ){
        List<LitemallPickSite> pickSiteList=pickSiteService.querySelective(name,page,limit,sort);
        return ResponseUtil.okList(pickSiteList);
    }


    private Object validate(LitemallPickSite pickSite){
        String name=pickSite.getSiteName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.fail(502, "提货站点名称不能为空");

        }
        if(StringUtils.isEmpty(pickSite.getSitePickTime())){
            return ResponseUtil.fail(502, "提货站点允许提货时间不能为空");

        }
        if(pickSiteService.checkExistByNameAndId(name,pickSite.getId())){
            return ResponseUtil.fail(502, "提货站点名称已经存在");

        }
        return null;
    }


    @RequiresPermissions("admin:picksite:create")
    @RequiresPermissionsDesc(menu ={"商城设置","自提货点"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPickSite pickSite){
        Object error=validate(pickSite);
        if(error!=null){
            return error;
        }
        pickSiteService.add(pickSite);
        return ResponseUtil.ok(pickSite);
    }


    @RequiresPermissions("admin:picksite:read")
    @RequiresPermissionsDesc(menu ={"商城设置","自提货点"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallPickSite litemallPickSite=pickSiteService.query(id);
        return ResponseUtil.ok(litemallPickSite);
    }

    @RequiresPermissions("admin:picksite:update")
    @RequiresPermissionsDesc(menu = {"商城设置","自提货点"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPickSite pickSite){
        Object error=validate(pickSite);
        if(error!=null){
            return error;
        }
        if(pickSiteService.update(pickSite)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(pickSite);
    }


    @RequiresPermissions("admin:picksite:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","自提货点"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPickSite pickSite){
        Integer id=pickSite.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        pickSiteService.delete(id);
        return ResponseUtil.ok();
    }


}
