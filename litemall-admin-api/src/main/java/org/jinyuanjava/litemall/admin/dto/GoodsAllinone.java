package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class GoodsAllinone {
    LitemallGoods goods; //商品主表
    LitemallGoodsSpecification[] specifications; //商品规格
    LitemallGoodsAttribute[] attributes; //商品属性
    LitemallGoodsProduct[] products; //商品属性规格表(商品货品表)
    //LitemallGoodsStore[] stores;//商品经营店铺表

    public LitemallGoods getGoods() {
        return goods;
    }

    public void setGoods(LitemallGoods goods) {
        this.goods = goods;
    }

    public LitemallGoodsProduct[] getProducts() {
        return products;
    }

    public void setProducts(LitemallGoodsProduct[] products) {
        this.products = products;
    }

    public LitemallGoodsSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(LitemallGoodsSpecification[] specifications) {
        this.specifications = specifications;
    }

    public LitemallGoodsAttribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(LitemallGoodsAttribute[] attributes) {
        this.attributes = attributes;
    }

//    public LitemallGoodsStore[] getStores() {
//        return stores;
//    }
//
//    public void setStores(LitemallGoodsStore[] stores) {
//        this.stores = stores;
//    }
}
