package org.jinyuanjava.litemall.admin.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.service.CommonService;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀活动启动和停止
 */
@Component
public class PromotionJob {
    private final Log logger = LogFactory.getLog(PromotionJob.class);

    @Autowired
    private LitemallPromotionSeckillRuleService seckillRuleService;

    @Autowired
    private LitemallGrouponRulesService grouponRulesService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService promotionGoodsRebateRuleService;
    @Autowired
    private LitemallPromotionGoodsRebateGoodsService promotionGoodsRebateGoodsService;
    @Autowired
    private LitemallCartService litemallCartService;
    @Autowired
    private LitemallPromotionGoodsRuleService goodsRuleService;

    @Autowired
    private LitemallCouponUserService couponUserService;

    @Autowired
    private LitemallCouponService couponService;

    @Autowired
    private LitemallUserinfoDefService userinfoDefService;

    @Autowired
    private LitemallPromotionGoodsDetailService promotionGoodsDetailService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private LitemallHuodongMainService litemallHuodongMainService;

    /**
     * 每隔一分钟检查
     * 检查秒杀活动
     * 注意，因为是相隔一分钟检查，因此导致优惠券真正开始可能会迟一分钟，超时时间可能比设定时间延迟1分钟
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void checkSecKillExpired() {
//    	Set<Integer> productIdSet= new HashSet<>();
        //如果不启动定时任务，则直接返回
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }
        logger.info("系统开启任务检查秒杀活动是否已经开始或已经过期");
        //秒杀任务更新过期时间
        List<LitemallPromotionSeckillRule> seckillRules = seckillRuleService.queryHaveExpiredButStateError();
        for(LitemallPromotionSeckillRule seckillRule : seckillRules){
            seckillRule.setSeckillExpireFlag(true);
            seckillRule.setRemark("秒杀时间到");
            seckillRuleService.updateById(seckillRule);
        }
        //秒杀数量库存为0时设置过期标志
        List<LitemallPromotionSeckillRule> seckillRulesStoreNumEqualZero = seckillRuleService.queryHaveExpiredButStoreNumEqualZero();
        for(LitemallPromotionSeckillRule seckillRule : seckillRulesStoreNumEqualZero){
            seckillRule.setSeckillExpireFlag(true);
            seckillRule.setRemark("库存抢光");
            seckillRuleService.updateById(seckillRule);
        }
        //团购任务更新过期时间
        List<LitemallGrouponRules> grouponRulesList = grouponRulesService.queryHaveExpiredButStateError();
        for(LitemallGrouponRules grouponRules : grouponRulesList){
            grouponRules.setRemark("团购时间到");
            grouponRules.setExpireFlag(true);
            grouponRulesService.updateById(grouponRules);
        }
        //团购数量库存为0时设置过期标志
        List<LitemallGrouponRules> grouponRulesStoreNumEqualZero = grouponRulesService.queryHaveExpiredButStoreNumEqualZero();
        for(LitemallGrouponRules grouponRules : grouponRulesStoreNumEqualZero){
            grouponRules.setRemark("库存抢光");
            grouponRules.setExpireFlag(true);
            grouponRulesService.updateById(grouponRules);
        }
        //会员专享活动更新过期时间
        List<LitemallPromotionGoodsRule> goodsRuleList = goodsRuleService.queryHaveExpiredButStateError();
        for(LitemallPromotionGoodsRule goodsRule : goodsRuleList){
            goodsRule.setRemark("会员专享活动时间到");
            goodsRule.setExpireFlag(true);
            goodsRuleService.updateById(goodsRule);
            //清空购物车
            List<LitemallPromotionGoodsDetail> productList= promotionGoodsDetailService.queryByRuleId(goodsRule.getId());
            List<Integer> productIdList=productList.stream().map(LitemallPromotionGoodsDetail::getGoodsProductId).collect(Collectors.toList());
            if(productIdList!=null&&productIdList.size()>0){
                litemallCartService.deleteByProductIds("会员",goodsRule.getId(), productIdList);
            }
        }

        //会员专享库存为0时设置过期标志
        List<LitemallPromotionGoodsRule> goodsRulesStoreNumEqualZero = goodsRuleService.queryHaveExpiredButStoreNumEqualZero();
        if(goodsRulesStoreNumEqualZero!=null&&goodsRulesStoreNumEqualZero.size()>0) {
            for (LitemallPromotionGoodsRule goodsRule : goodsRulesStoreNumEqualZero) {
                goodsRule.setRemark("库存抢光");
//            goodsRule.setExpireFlag(true);
                goodsRuleService.updateById(goodsRule);
            }
        }

        //品项折扣更新过期时间
        List<LitemallPromotionGoodsRebateRule> goodsRebateRules = promotionGoodsRebateRuleService.queryHaveExpiredButStateError();
        for(LitemallPromotionGoodsRebateRule goodsRebateRule : goodsRebateRules){
            goodsRebateRule.setRemark("品项折扣时间到");
            goodsRebateRule.setExpireFlag(true);
            promotionGoodsRebateRuleService.updateById(goodsRebateRule);
            //获得所有参加活动商品清空购物车
            List<LitemallPromotionGoodsRebateGoods> goodsRebateGoodsList = promotionGoodsRebateGoodsService.queryByRuleId(goodsRebateRule.getId());
            for (LitemallPromotionGoodsRebateGoods goodsRebateGoods : goodsRebateGoodsList) {
                litemallCartService.deleteByProductId("品项折扣",goodsRebateGoods.getRuleId(),goodsRebateGoods.getGoodsProductId());
//            	productIdSet.add(goodsRebateGoods.getGoodsProductId());
			}
        }

        //会员提醒消息是否过期
        List<LitemallUserinfoDef> userinfoDefs = userinfoDefService.queryHaveExpiredButStateError();
        for(LitemallUserinfoDef userinfoDef : userinfoDefs){
            userinfoDef.setExpireFlag(true);
            userinfoDefService.updateById(userinfoDef);
        }

        //活动承接页是否过期
        List<LitemallHuodongMain> litemallHuodongMains = litemallHuodongMainService.queryHaveExpiredButStateError();
        for(LitemallHuodongMain litemallHuodongMain : litemallHuodongMains){
            litemallHuodongMain.setExpireFlag(true);
            litemallHuodongMainService.update(litemallHuodongMain);
        }

    }



}
