package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.GoodsRebateRuleAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminGoodsRebateRuleService {
    private final Log logger = LogFactory.getLog(AdminGoodsRebateRuleService.class);

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;

    @Autowired
    private LitemallPromotionGoodsRebateGoodsService goodsRebateGoodsService;

    @Autowired
    private LitemallCartService litemallCartService;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallPromotionSeckillRuleService seckillRuleService;

    @Autowired
    private LitemallGrouponRulesService grouponRulesService;



    public Object list(String name, Integer expireFlag,
                       String goodsSn, String goodsName,
                       Integer page, Integer limit, String sort) {
        List<LitemallPromotionGoodsRebateRule> goodsRebateRulesList = goodsRebateRuleService.querySelective(
                name,expireFlag,goodsSn,goodsName, page, limit, sort);
        return ResponseUtil.okList(goodsRebateRulesList);
    }

    private Object validate(GoodsRebateRuleAllinone goodsRebateRuleAllinone) {
        LitemallPromotionGoodsRebateRule goodsRebateRule = goodsRebateRuleAllinone.getGoodsRebateRule();
        String name = goodsRebateRule.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "品项折扣活动名称不不能为空");
        }
        if (StringUtils.isEmpty(goodsRebateRule.getRebate())) {
            return ResponseUtil.fail(401, "品项折扣活动折扣不能为空");
        }
        if(goodsRebateRule.getRebate().compareTo(BigDecimal.ZERO)==-1||goodsRebateRule.getRebate().compareTo(BigDecimal.valueOf(1))==1)
        {
            return ResponseUtil.fail(401, "品项折扣值必须为0-1之间的数");
        }

        if (StringUtils.isEmpty(goodsRebateRule.getBeginDate())) {
            return ResponseUtil.fail(401, "品项折扣活动折扣起始日期不能为空");
        }
        if (StringUtils.isEmpty(goodsRebateRule.getEndDate())) {
            return ResponseUtil.fail(401, "品项折扣活动折扣截止日期不能为空");
        }
        LitemallPromotionGoodsRebateGoods[] goodsRebateRuleGoodsArray = goodsRebateRuleAllinone.getGoodsRebateRuleGoods();
        for (LitemallPromotionGoodsRebateGoods goodsRebateRuleGoods : goodsRebateRuleGoodsArray) {
            String goodsName=goodsRebateRuleGoods.getGoodsName();
            if (StringUtils.isEmpty(goodsName)) {
                return ResponseUtil.fail(401, "商品名称不能为空");
            }
            if(goodsRebateRuleGoods.getRebatePrice().compareTo(BigDecimal.ZERO)==-1){
                return ResponseUtil.fail(401, "商品【"+goodsRebateRuleGoods.getGoodsName()+"】单价不能小于0");
            }
            if(goodsRebateRuleGoods.getRebatePrice().
                    compareTo(goodsRebateRuleGoods.getSourcePrice())==1){
                return ResponseUtil.fail(401, "商品【"+goodsRebateRuleGoods.getGoodsName()+"】折扣价不能大于原价");
            }

            //判断该商品是否已经在团购、品项折扣里存在,不判断在会员里是否存在，因为会员是针对特定的人员设置的，如果已经设置了会员价，
            //则第一时间取会员价，其它的活动设置价格无效
            LitemallPromotionSeckillRule seckillRule= seckillRuleService.queryProductIdIsInSkill(goodsRebateRuleGoods.getGoodsProductId());
            if(seckillRule!=null){
                return ResponseUtil.fail(401,"商品【"+goodsRebateRuleGoods.getGoodsName()+"】已经在秒杀活动【"+seckillRule.getSeckillName()+"】中设置");
            }
            LitemallGrouponRules grouponRules= grouponRulesService.queryProductIdIsInGroupon(goodsRebateRuleGoods.getGoodsProductId());
            if(grouponRules!=null){
                return ResponseUtil.fail(401,"商品【"+goodsRebateRuleGoods.getGoodsName()+"】已经在团购活动【"+grouponRules.getName()+"】中设置");
            }
            ViewPromotionGoodsRebateRuleGoods rebateRuleGoods= goodsRebateRuleService.getRuleByProductId(goodsRebateRuleGoods.getGoodsProductId());
            if(rebateRuleGoods!=null&&!rebateRuleGoods.getId().equals(goodsRebateRuleGoods.getId())){
                return ResponseUtil.fail(401,"商品【"+goodsRebateRuleGoods.getGoodsName()+"】已经在未结束的其它品项折扣活动【"+rebateRuleGoods.getRuleName()+"】中设置");
            }

        }

        return null;
    }

    /**
     * 编辑品项折扣活动
     * <p>
     * TODO
     * 需要判断是否明细主键ID已经存在，如果存在，则更新信息，如果不存在，则插入数据
     */
    @Transactional
    public Object update(GoodsRebateRuleAllinone goodsRebateRuleAllinone) {
        Object error = validate(goodsRebateRuleAllinone);
        if (error != null) {
            return error;
        }
        LitemallPromotionGoodsRebateRule goodsRebateRule=goodsRebateRuleAllinone.getGoodsRebateRule();
        LitemallPromotionGoodsRebateGoods[] goodsRebateRuleGoodsArray = goodsRebateRuleAllinone.getGoodsRebateRuleGoods();

        Integer mainId = goodsRebateRule.getId();
        if(mainId==null){
            goodsRebateRuleService.createRules(goodsRebateRule);
        }else
        {
            goodsRebateRuleService.updateById(goodsRebateRule);
        }

        // 品项折扣列表
        for (LitemallPromotionGoodsRebateGoods promotionGoodsRebateGoods : goodsRebateRuleGoodsArray) {
            promotionGoodsRebateGoods.setRuleId(goodsRebateRule.getId());
            //此处可以前台用户自由设置商品单价，但单价不能小于0，
//            promotionGoodsRebateGoods.setRebatePrice(promotionGoodsRebateGoods.getSourcePrice().multiply(goodsRebateRule.getRebate()));
            Integer codeId=promotionGoodsRebateGoods.getId();
            if(codeId==null){
                goodsRebateGoodsService.createRulesGoods(promotionGoodsRebateGoods);
            } else
            {
                goodsRebateGoodsService.updateById(promotionGoodsRebateGoods);
                //如果折扣规则为过期，则清空购物车
                if(goodsRebateRule.getExpireFlag()){
                    litemallCartService.deleteByProductId("品项折扣",promotionGoodsRebateGoods.getRuleId(),promotionGoodsRebateGoods.getGoodsProductId());
//                    litemallCartService.deleteByProductId(promotionGoodsRebateGoods.getGoodsProductId());
                }
            }
        }


        return ResponseUtil.ok();
    }

    /**
     * 删除品项折扣活动
     * @param goodsRebateRule
     * @return
     */
    @Transactional
    public Object delete(LitemallPromotionGoodsRebateRule goodsRebateRule) {
        Integer goodsRebateRuleId= goodsRebateRule.getId();
        if (goodsRebateRuleId == null) {
            return ResponseUtil.badArgument();
        }
        goodsRebateRuleService.delete(goodsRebateRuleId);
        goodsRebateGoodsService.deleteByRuleId(goodsRebateRuleId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsRebateRuleAllinone goodsRebateRuleAllinone) {
        Object error = validate(goodsRebateRuleAllinone);
        if (error != null) {
            return error;
        }

        LitemallPromotionGoodsRebateRule goodsRebateRule = goodsRebateRuleAllinone.getGoodsRebateRule();
        LitemallPromotionGoodsRebateGoods[] goodsRebateRuleGoodsArray = goodsRebateRuleAllinone.getGoodsRebateRuleGoods();

        String name = goodsRebateRule.getName();
        if (goodsRebateRuleService.checkExistByName(name)) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "品项折扣活动名称已经存在");
        }
        //保存品项折扣活动主表
        goodsRebateRuleService.createRules(goodsRebateRule);
        // 保存品项折扣活动商品列表明细
        for (LitemallPromotionGoodsRebateGoods goodsRebateGoods : goodsRebateRuleGoodsArray) {
            goodsRebateGoods.setRuleId(goodsRebateRule.getId());
            goodsRebateGoodsService.createRulesGoods(goodsRebateGoods);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallPromotionGoodsRebateRule goodsRebateRule = goodsRebateRuleService.findById(id);
//        List<LitemallPromotionGoodsRebateGoods> goodsRebateGoodsList = goodsRebateGoodsService.queryByRuleId(id);
        //关联获取动态库存
        List<ViewPromotionGoodsRebateRuleGoods> goodsRebateGoodsList = goodsRebateGoodsService.getViewDetailsByRuleId(id);

        for(ViewPromotionGoodsRebateRuleGoods rebateRuleGoods:goodsRebateGoodsList){
            Integer unPayNum=orderGoodsService.getOrderSumNumByRuleId("品项折扣",
                    " and order_status="+ OrderUtil.STATUS_CREATE,id,rebateRuleGoods.getGoodsProductId());
            rebateRuleGoods.setUnPayNum(unPayNum);
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
            Integer havePayNum=orderGoodsService.getOrderSumNumByRuleId("品项折扣",
                    " and order_status in (201,250,202,301,401,402) ",id,rebateRuleGoods.getGoodsProductId());
            rebateRuleGoods.setHavePayNum(havePayNum);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goodsRebateRule", goodsRebateRule);
        data.put("goodsRebateGoodsList", goodsRebateGoodsList);

        return ResponseUtil.ok(data);
    }


}
