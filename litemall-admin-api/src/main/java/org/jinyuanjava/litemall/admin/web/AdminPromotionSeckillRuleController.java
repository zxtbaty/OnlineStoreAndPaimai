package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallGrouponRules;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionSeckillRule;
import org.jinyuanjava.litemall.db.domain.ViewPromotionGoodsRebateRuleGoods;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/seckillrule")
@Validated
@Api(description = "后台管理/营销管理/秒杀活动规则:/admin/profile")
public class AdminPromotionSeckillRuleController {
    private final Log logger = LogFactory.getLog(AdminPromotionSeckillRuleController.class);

    @Autowired
    private LitemallPromotionSeckillRuleService rulesService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;


    @RequiresPermissions("admin:seckillrule:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动规则"}, button="查询")
    @GetMapping("/list")
    public Object list(String seckillName,Integer seckillExpireFlag, String goodsId,
                       String goodsName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallPromotionSeckillRule> rulesList = rulesService.querySelective(
                seckillName,seckillExpireFlag,
                goodsId, goodsName,page, limit, sort);
        return ResponseUtil.okList(rulesList);
    }

    private Object validate(LitemallPromotionSeckillRule seckillRule) {
        Integer goodsId = seckillRule.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.fail(401,"秒杀活动规则里没有设置商品");
        }
        if(StringUtils.isEmpty(seckillRule.getSeckillName()))
        {
            return ResponseUtil.fail(401,"秒杀活动名称不能为空");
        }
        BigDecimal sourcePrice = seckillRule.getSourcePrice();
        if (sourcePrice == null) {
            return ResponseUtil.fail(401,"秒杀活动规则里原价不能为空");
        }
        BigDecimal seckillPrice = seckillRule.getSeckillPrice();
        if (seckillPrice == null) {
            return ResponseUtil.fail(401,"秒杀活动规则里秒杀价不能为空");
        }
        LocalDateTime beginDate = seckillRule.getSeckillBeginDate();
        if (beginDate == null) {
            return ResponseUtil.fail(401,"秒杀活动规则里开始时间不能为空");
        }
        LocalDateTime endDate = seckillRule.getSeckillEndDate();
        if (endDate == null) {
            return ResponseUtil.fail(401,"秒杀活动规则里结束时间不能为空");
        }

        //判断该商品是否已经在团购、品项折扣里存在,不判断在会员里是否存在，因为会员是针对特定的人员设置的，如果已经设置了会员价，
        //则第一时间取会员价，其它的活动设置价格无效
        LitemallPromotionSeckillRule haveExistSec= rulesService.queryProductIdIsInSkill(seckillRule.getGoodsProductId());
        if(haveExistSec!=null&&!haveExistSec.getId().equals(seckillRule.getId())){
            return ResponseUtil.fail(401,"该商品已经在其它秒杀活动【"+haveExistSec.getSeckillName()+"】中设置");
        }

        LitemallGrouponRules grouponRules= grouponRulesService.queryProductIdIsInGroupon(seckillRule.getGoodsProductId());
        if(grouponRules!=null){
            return ResponseUtil.fail(401,"该商品已经在未结束的团购活动【"+grouponRules.getName()+"】中设置");
        }
        ViewPromotionGoodsRebateRuleGoods rebateRuleGoods= goodsRebateRuleService.getRuleByProductId(seckillRule.getGoodsProductId());
        if(rebateRuleGoods!=null){
            return ResponseUtil.fail(401,"该商品已经在未结束的品项折扣活动【"+rebateRuleGoods.getRuleName()+"】中设置");
        }
        return null;
    }

    @RequiresPermissions("admin:seckillrule:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPromotionSeckillRule seckillRule) {
        Object error = validate(seckillRule);
        if (error != null) {
            return error;
        }


        if (rulesService.updateById(seckillRule) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:seckillrule:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPromotionSeckillRule seckillRule) {
        Object error = validate(seckillRule);
        if (error != null) {
            return error;
        }

//        Integer goodsId = grouponRules.getGoodsId();
//        LitemallGoods goods = goodsService.findById(goodsId);
//        if (goods == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        grouponRules.setGoodsName(goods.getName());
//        grouponRules.setPicUrl(goods.getPicUrl());

        rulesService.createRules(seckillRule);

        return ResponseUtil.ok(seckillRule);
    }

    @RequiresPermissions("admin:seckillrule:read")
    @RequiresPermissionsDesc(menu ={"营销管理","秒杀活动规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallPromotionSeckillRule  seckillRule= rulesService.queryById(id);
        Integer unPayNum=orderGoodsService.getOrderSumNumByRuleId("秒杀",
                " and order_status="+ OrderUtil.STATUS_CREATE,id,seckillRule.getGoodsProductId());
        seckillRule.setUnPayNum(unPayNum);
        //去掉取消，已退款
//                101: '待支付',
//                102: '用户取消',
//                103: '超时取消',
//                201: '待备货',
//                250: '待发货',
//                202: '待退款',
//                203: '已退款',
//                301: '待收货',
//                401: '用户确认收货',
//                402: '超时确认收货'
        Integer havePayNum=orderGoodsService.getOrderSumNumByRuleId("秒杀",
                " and order_status in (201,250,202,301,401,402) ",id,seckillRule.getGoodsProductId());
        seckillRule.setHavePayNum(havePayNum);
        return ResponseUtil.ok(seckillRule);
    }

    @RequiresPermissions("admin:seckillrule:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionSeckillRule seckillRule) {
        Integer id = seckillRule.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        rulesService.delete(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:seckillrule:addGoodsStoreNum")
    @RequiresPermissionsDesc(menu ={"营销管理","秒杀活动规则"},button = "增加库存")
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
