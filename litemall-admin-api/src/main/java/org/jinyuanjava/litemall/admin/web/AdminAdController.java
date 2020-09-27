package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAd;
import org.jinyuanjava.litemall.db.service.LitemallAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/ad")
@Validated
@Api(description = "后台管理/营销管理/首页Banner:/admin/ad")
public class AdminAdController {
    private final Log logger = LogFactory.getLog(AdminAdController.class);

    @Autowired
    private LitemallAdService adService;

    @RequiresPermissions("admin:ad:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "首页Banner"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallAd> adList = adService.querySelective(name, content, page, limit, sort);
        return ResponseUtil.okList(adList);
    }

    private Object validate(LitemallAd ad) {
        String name = ad.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String content = ad.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:ad:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "首页Banner"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:read")
    @RequiresPermissionsDesc(menu={"营销管理" , "首页Banner"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallAd ad = adService.findById(id);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "首页Banner"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (adService.updateById(ad) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "首页Banner"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        adService.deleteById(id);
        return ResponseUtil.ok();
    }

}
