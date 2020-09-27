package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRebateGoods;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRebateRule;

public class GoodsRebateRuleAllinone {
    LitemallPromotionGoodsRebateRule goodsRebateRule;
    LitemallPromotionGoodsRebateGoods[] goodsRebateRuleGoods;

    public LitemallPromotionGoodsRebateRule getGoodsRebateRule() {
        return goodsRebateRule;
    }

    public void setGoodsRebateRule(LitemallPromotionGoodsRebateRule goodsRebateRule) {
        this.goodsRebateRule = goodsRebateRule;
    }

    public LitemallPromotionGoodsRebateGoods[] getGoodsRebateRuleGoods() {
        return goodsRebateRuleGoods;
    }

    public void setGoodsRebateRuleGoods(LitemallPromotionGoodsRebateGoods[] goodsRebateRuleGoods) {
        this.goodsRebateRuleGoods = goodsRebateRuleGoods;
    }
}
