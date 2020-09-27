package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class Recommend {
    LitemallGoodsRecommend recommend;
    LitemallGoods goods;

    public LitemallGoodsRecommend getRecommend() {
        return recommend;
    }

    public void setRecommend(LitemallGoodsRecommend recommend) {
        this.recommend = recommend;
    }

    public LitemallGoods getGoods() {
        return goods;
    }

    public void setGoods(LitemallGoods goods) {
        this.goods = goods;
    }
}
