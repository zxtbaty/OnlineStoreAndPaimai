package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class GoodsRuleAllinone {
    LitemallPromotionGoodsRule goodsRule;
    LitemallPromotionGoodsRuleUser[] goodsRuleUsers;
    LitemallPromotionGoodsDetail[] goodsDetails;

    public LitemallPromotionGoodsRule getGoodsRule() {
        return goodsRule;
    }

    public void setGoodsRule(LitemallPromotionGoodsRule goodsRule) {
        this.goodsRule = goodsRule;
    }

    public LitemallPromotionGoodsRuleUser[] getGoodsRuleUsers() {
        return goodsRuleUsers;
    }

    public void setGoodsRuleUsers(LitemallPromotionGoodsRuleUser[] goodsRuleUsers) {
        this.goodsRuleUsers = goodsRuleUsers;
    }

    public LitemallPromotionGoodsDetail[] getGoodsDetails() {
        return goodsDetails;
    }

    public void setGoodsDetails(LitemallPromotionGoodsDetail[] goodsDetails) {
        this.goodsDetails = goodsDetails;
    }
}
