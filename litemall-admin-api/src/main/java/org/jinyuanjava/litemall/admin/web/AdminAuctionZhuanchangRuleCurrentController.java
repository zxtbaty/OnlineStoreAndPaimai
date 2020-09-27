package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.AuctionZhuanchangRuleCurrentAllinone;
import org.jinyuanjava.litemall.admin.service.AdminAuctionZhuanchangRuleCurrentService;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangGoodsCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangRuleCurrent;
import org.jinyuanjava.litemall.db.service.LitemallAuctionZhuanchangGoodsCurrentService;
import org.jinyuanjava.litemall.db.service.LitemallUserChargeMoneyLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/auctionZhuanchangRuleCurrent")
@Validated
@Api(description = "后台管理/拍卖管理/专场拍规则:/admin/auctionZhuanchangRuleCurrent")
public class AdminAuctionZhuanchangRuleCurrentController {
    private final Log logger = LogFactory.getLog(AdminAuctionZhuanchangRuleCurrentController.class);


    @Autowired
    private AdminAuctionZhuanchangRuleCurrentService rulesService;


    @Autowired
    private LitemallAuctionZhuanchangGoodsCurrentService goodsService;

    @Autowired
    private LitemallUserChargeMoneyLockService userChargeMoneyLockService;


    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场活动规则"}, button="查询")
    @GetMapping("/list")
    public Object list(String zhuanchangName, Integer expireFlag,
                       String goodsSn, String goodsName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return rulesService.list(
                zhuanchangName,expireFlag,
                goodsSn, goodsName,page, limit, sort);

    }
    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:create")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场活动规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody AuctionZhuanchangRuleCurrentAllinone auctionZhuanchangRuleCurrentAllInOne) {

        return rulesService.create(auctionZhuanchangRuleCurrentAllInOne);

    }

    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:update")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场活动规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody AuctionZhuanchangRuleCurrentAllinone auctionZhuanchangRuleCurrentAllInOne) {
        return rulesService.update(auctionZhuanchangRuleCurrentAllInOne);
    }

    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:delete")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场活动规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAuctionZhuanchangRuleCurrent auctionZhuanchangRuleCurrent) {
        return rulesService.delete(auctionZhuanchangRuleCurrent);
    }

    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:read")
    @RequiresPermissionsDesc(menu ={"商城设置","专场活动规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return rulesService.detail(id);
    }

    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:getGoodsDetail")
    @RequiresPermissionsDesc(menu ={"商城设置","专场活动规则"},button = "获取专场商品详情")
    @GetMapping("/getGoodsDetail")
    public Object getGoodsDetail(@NotNull Integer id){
        LitemallAuctionZhuanchangGoodsCurrent goodsCurrent= goodsService.queryById(id);
        return ResponseUtil.ok(goodsCurrent);
    }
    @RequiresPermissions("admin:auctionZhuanchangRuleCurrent:updateGoodsDetail")
    @RequiresPermissionsDesc(menu ={"商城设置","专场活动规则"},button = "获取专场商品详情")
    @GetMapping("/updateGoodsDetail")
    public Object updateGoodsDetail(@RequestBody LitemallAuctionZhuanchangGoodsCurrent goodsCurrent){
        goodsService.updateById(goodsCurrent);
        return ResponseUtil.ok(goodsCurrent);
    }


    @RequiresPermissions("admin:dajiaPaimaiRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","大家拍设置"},button = "批量解锁保证金")
    @GetMapping("/unLockMoneyByRuleMxId")
    public Object unLockMoneyByRuleMxId(@NotNull Integer ruleMxId,Boolean ifAll){
        Boolean result= userChargeMoneyLockService.unLockMoneyBatch("专场拍",ruleMxId,ifAll);
        return ResponseUtil.ok(result);
    }

    @RequiresPermissions("admin:dajiaPaimaiRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","大家拍设置"},button = "批量解锁保证金")
    @GetMapping("/unLockMoneyByZhuanChangId")
    public Object unLockMoneyByZhuanChangId(@NotNull Integer zhuanChangId,Boolean ifAll){
        List<LitemallAuctionZhuanchangGoodsCurrent> goodsCurrentList= goodsService.queryByRuleId(zhuanChangId);
        if(goodsCurrentList!=null&&goodsCurrentList.size()>0){
            for(LitemallAuctionZhuanchangGoodsCurrent goodsCurrent:goodsCurrentList) {
                Boolean result = userChargeMoneyLockService.unLockMoneyBatch("专场拍", goodsCurrent.getId(), ifAll);
            }
        }
        return ResponseUtil.ok(true);
    }
}
