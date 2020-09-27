package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.GoodsRuleAllinone;
import org.jinyuanjava.litemall.admin.service.AdminGoodsRuleService;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRule;
import org.jinyuanjava.litemall.db.service.LitemallPromotionGoodsRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/userGoodsRule")
@Validated
@Api(description = "后台管理/营销管理/会员专享活动规则:/admin/userGoodsRule")
public class AdminPromotionUserGoodsRuleController {
    private final Log logger = LogFactory.getLog(AdminPromotionUserGoodsRuleController.class);


    @Autowired
    private AdminGoodsRuleService rulesService;

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService;



    @RequiresPermissions("admin:userGoodsRule:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动规则"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, Integer expireFlag,
                       String goodsSn, String goodsName,
                       String username,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return rulesService.list(
                name,expireFlag,
                goodsSn, goodsName,username,page, limit, sort);
    }
    @RequiresPermissions("admin:userGoodsRule:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsRuleAllinone goodsRuleAllinone) {
        return rulesService.create(goodsRuleAllinone);

    }

    @RequiresPermissions("admin:userGoodsRule:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsRuleAllinone goodsRuleAllinone) {
        return rulesService.update(goodsRuleAllinone);
    }

    @RequiresPermissions("admin:userGoodsRule:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionGoodsRule goodsRule) {
        return rulesService.delete(goodsRule);
    }

    @RequiresPermissions("admin:userGoodsRule:read")
    @RequiresPermissionsDesc(menu ={"营销管理","会员专享活动规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){

        return rulesService.detail(id);
    }

    @GetMapping("/queryByName")
    public Object queryByName(String ruleName){
        LitemallPromotionGoodsRule goodsRule= promotionGoodsRuleService.queryByName(ruleName);
        return ResponseUtil.ok(goodsRule);
    }

    @RequiresPermissions("admin:userGoodsRule:addGoodsStoreNum")
    @RequiresPermissionsDesc(menu ={"营销管理","会员专享活动规则"},button = "增加库存")
    @GetMapping("/addGoodsStoreNum")
    public Object addGoodsStoreNum(Integer id, Integer addStoreNum){
         Boolean result=rulesService.addGoodsStoreNum(id,addStoreNum);
         if(result){
             return ResponseUtil.ok();
         } else
         {
             return ResponseUtil.fail(502,"增加活动库存失败");
         }
    }



}
