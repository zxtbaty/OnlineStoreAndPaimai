package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.GoodsRebateRuleAllinone;
import org.jinyuanjava.litemall.admin.service.AdminGoodsRebateRuleService;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRebateRule;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/goodsRebateRule")
@Validated
@Api(description = "后台管理/营销管理/品项折扣规则:/admin/goodsRebateRule")
public class AdminPromotionGoodsRebateRuleController {
    private final Log logger = LogFactory.getLog(AdminPromotionGoodsRebateRuleController.class);


    @Autowired
    private AdminGoodsRebateRuleService rulesService;

    @Autowired
    private CommonDBService commonDBService;

    @RequiresPermissions("admin:goodsRebateRule:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动规则"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, Integer expireFlag,
                       String goodsSn, String goodsName,

                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return rulesService.list(
                name,expireFlag,
                goodsSn, goodsName,page, limit, sort);

    }


    @RequiresPermissions("admin:goodsRebateRule:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsRebateRuleAllinone goodsRebateRuleAllinone) {

        return rulesService.create(goodsRebateRuleAllinone);

    }

    @RequiresPermissions("admin:goodsRebateRule:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsRebateRuleAllinone goodsRebateRuleAllinone) {
        return rulesService.update(goodsRebateRuleAllinone);
    }

    @RequiresPermissions("admin:goodsRebateRule:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionGoodsRebateRule goodsRebateRule) {
        return rulesService.delete(goodsRebateRule);
    }

    @RequiresPermissions("admin:goodsRebateRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","品项折扣活动规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return rulesService.detail(id);
    }


}
