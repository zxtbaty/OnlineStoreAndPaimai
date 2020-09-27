package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangGoodsCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangRuleCurrent;

public class AuctionZhuanchangRuleCurrentAllinone {
    LitemallAuctionZhuanchangRuleCurrent zhuanchangRuleCurrent;
    LitemallAuctionZhuanchangGoodsCurrent[] zhuanchangGoodsCurrents;

    public LitemallAuctionZhuanchangRuleCurrent getZhuanchangRuleCurrent() {
        return zhuanchangRuleCurrent;
    }

    public void setZhuanchangRuleCurrent(LitemallAuctionZhuanchangRuleCurrent zhuanchangRuleCurrent) {
        this.zhuanchangRuleCurrent = zhuanchangRuleCurrent;
    }

    public LitemallAuctionZhuanchangGoodsCurrent[] getZhuanchangGoodsCurrents() {
        return zhuanchangGoodsCurrents;
    }

    public void setZhuanchangGoodsCurrents(LitemallAuctionZhuanchangGoodsCurrent[] zhuanchangGoodsCurrents) {
        this.zhuanchangGoodsCurrents = zhuanchangGoodsCurrents;
    }
}
