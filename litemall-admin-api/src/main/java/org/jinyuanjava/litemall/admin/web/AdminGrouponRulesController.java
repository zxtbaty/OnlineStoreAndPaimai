package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/grouponrules")
@Validated
@Api(description = "后台管理/营销管理/团购活动规则:/admin/grouponrules")
public class AdminGrouponRulesController {
    private final Log logger = LogFactory.getLog(AdminGrouponRulesController.class);

    @Autowired
    private LitemallGrouponRulesService rulesService;
    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;


    @Autowired
    private LitemallPromotionSeckillRuleService seckillRuleService;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;


    @RequiresPermissions("admin:grouponrules:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动规则"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, String goodsId, String goodsName,Integer expireFlag,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallGrouponRules> rulesList = rulesService.querySelective(name,goodsId,goodsName,
                expireFlag, page, limit, sort);
        return ResponseUtil.okList(rulesList);
    }

    private Object validate(LitemallGrouponRules grouponRules) {
        Integer goodsId = grouponRules.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.fail(401,"团购活动规则里没有设置商品");
        }
        BigDecimal discount = grouponRules.getGrouponPrice();
        if (discount == null) {
            return ResponseUtil.fail(401,"团购活动规则里团购价不能为空");

        }
        Integer discountMember = grouponRules.getGroupMinperson();
        if (discountMember == null) {
            return ResponseUtil.fail(401,"团购活动规则里最低人数不能为空");
        }

        LocalDateTime beginDate = grouponRules.getBeginDate();
        if (beginDate == null) {
            return ResponseUtil.fail(401,"团购活动规则里开始时间不能为空");
        }

        LocalDateTime expireTime = grouponRules.getExpireTime();
        if (expireTime == null) {
            return ResponseUtil.fail(401,"团购活动规则里结束时间不能为空");
        }

        //判断该商品是否已经在团购、品项折扣里存在,不判断在会员里是否存在，因为会员是针对特定的人员设置的，如果已经设置了会员价，
        //则第一时间取会员价，其它的活动设置价格无效
        LitemallPromotionSeckillRule seckillRule= seckillRuleService.queryProductIdIsInSkill(grouponRules.getGoodsProductId());
        if(seckillRule!=null){
            return ResponseUtil.fail(401,"该商品已经在秒杀活动【"+seckillRule.getSeckillName()+"】中设置");
        }
        LitemallGrouponRules haveExistGroupOn= rulesService.queryProductIdIsInGroupon(grouponRules.getGoodsProductId());
        if(haveExistGroupOn!=null&&!haveExistGroupOn.getId().equals(grouponRules.getId())){
            return ResponseUtil.fail(401,"该商品已经在其它团购活动【"+haveExistGroupOn.getName()+"】中设置");
        }
        ViewPromotionGoodsRebateRuleGoods rebateRuleGoods= goodsRebateRuleService.getRuleByProductId(grouponRules.getGoodsProductId());
        if(rebateRuleGoods!=null){
            return ResponseUtil.fail(401,"该商品已经在未结束的品项折扣活动【"+rebateRuleGoods.getRuleName()+"】中设置");
        }

        return null;
    }

    @RequiresPermissions("admin:grouponrules:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallGrouponRules grouponRules) {
        Object error = validate(grouponRules);
        if (error != null) {
            return error;
        }

        Integer goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        grouponRules.setGoodsName(goods.getName());


        if (rulesService.updateById(grouponRules) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:grouponrules:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallGrouponRules grouponRules) {
        Object error = validate(grouponRules);
        if (error != null) {
            return error;
        }
        Integer goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }
        grouponRules.setGoodsName(goods.getName());
        rulesService.createRules(grouponRules);

        return ResponseUtil.ok(grouponRules);
    }

    @RequiresPermissions("admin:grouponrules:read")
    @RequiresPermissionsDesc(menu ={"营销管理","团购活动规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallGrouponRules grouponRules= rulesService.queryById(id);

        Integer unPayNum=orderGoodsService.getOrderSumNumByRuleId("团购",
                " and order_status="+ OrderUtil.STATUS_CREATE,id,grouponRules.getGoodsProductId());
        grouponRules.setUnPayNum(unPayNum);
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
        Integer havePayNum=orderGoodsService.getOrderSumNumByRuleId("团购",
                " and order_status in (201,250,202,301,401,402) ",id,grouponRules.getGoodsProductId());
        grouponRules.setHavePayNum(havePayNum);
        return ResponseUtil.ok(grouponRules);
    }

    @RequiresPermissions("admin:grouponrules:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGrouponRules grouponRules) {
        Integer id = grouponRules.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        rulesService.delete(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:grouponrules:addGoodsStoreNum")
    @RequiresPermissionsDesc(menu ={"营销管理","团购活动规则"},button = "增加库存")
    @GetMapping("/addGoodsStoreNum")
    public Object addGoodsStoreNum(Integer id, Integer addStoreNum){
        Boolean result=rulesService.addGoodsStoreNum(id,addStoreNum);
        if(result){
            return ResponseUtil.ok();
        } else
        {
            return ResponseUtil.fail(502,"增加库存失败");
        }
    }
}
