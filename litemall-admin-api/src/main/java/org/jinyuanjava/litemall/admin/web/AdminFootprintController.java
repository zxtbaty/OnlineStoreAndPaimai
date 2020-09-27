package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallFootprint;
import org.jinyuanjava.litemall.db.service.LitemallFootprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/footprint")
@Validated
@Api(description = "后台管理/用户管理/用户足迹:/admin/footprint")
public class AdminFootprintController {
    private final Log logger = LogFactory.getLog(AdminFootprintController.class);

    @Autowired
    private LitemallFootprintService footprintService;

    @RequiresPermissions("admin:footprint:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "用户足迹"}, button="查询")
    @GetMapping("/list")
    public Object list(String userId, String goodsId,
                       String wxNickname, String weixinOpenid,
                       String crmId, String goodsSn, String goodsName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallFootprint> footprintList = footprintService.querySelective(userId,
                goodsId,wxNickname,weixinOpenid,crmId,goodsSn,goodsName,page, limit, sort);
        return ResponseUtil.okList(footprintList);
    }
}
