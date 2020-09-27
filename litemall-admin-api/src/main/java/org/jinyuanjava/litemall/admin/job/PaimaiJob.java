package org.jinyuanjava.litemall.admin.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 拍卖活动启动和停止
 */
@Component
public class PaimaiJob {
    private final Log logger = LogFactory.getLog(PaimaiJob.class);

    @Autowired
    private LitemallAuctionZhuanchangRuleCurrentService zhuanchangRuleService;

    @Autowired
    private LitemallAuctionDajiaRuleCurrentService dajiaRuleService;

    @Autowired
    private LitemallPrivateMakeRuleService privateMakeRuleService;



    /**
     * 每隔一分钟检查
     * 检查秒杀活动
     * 注意，因为是相隔一分钟检查，因此导致优惠券真正开始可能会迟一分钟，超时时间可能比设定时间延迟1分钟
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void checkSecKillExpired() {
        logger.info("系统开启任务检查拍卖活动是否已经开始或已经过期");

        //专场拍卖活动更新过期时间
        List<LitemallAuctionZhuanchangRuleCurrent> zhuanchangRuleCurrents = zhuanchangRuleService.queryHaveExpiredButStateError();
        for(LitemallAuctionZhuanchangRuleCurrent zhuanchangRuleCurrent : zhuanchangRuleCurrents){
            zhuanchangRuleCurrent.setExpireFlag(true);
            zhuanchangRuleCurrent.setRemark("专场拍卖时间到");
            zhuanchangRuleService.updateById(zhuanchangRuleCurrent);
        }
        //大家拍活动过期
        List<LitemallAuctionDajiaRuleCurrent> dajiaRuleCurrentList = dajiaRuleService.queryHaveExpiredButStateError();
        for(LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent : dajiaRuleCurrentList){
            dajiaRuleCurrent.setRemark("大家拍时间到");
            dajiaRuleCurrent.setExpireFlag(true);
            dajiaRuleService.updateById(dajiaRuleCurrent);
        }
        //私人定制活动过期
        List<LitemallPrivateMakeRule> privateMakeRules = privateMakeRuleService.queryHaveExpiredButStateError();
        for(LitemallPrivateMakeRule privateMakeRule : privateMakeRules){
            privateMakeRule.setRemark("私人定制设置时间到");
            privateMakeRule.setExpireFlag(true);
            privateMakeRuleService.updateById(privateMakeRule);
        }


    }



}
