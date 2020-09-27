package org.jinyuanjava.litemall.wx.dto;

import org.jinyuanjava.litemall.core.express.dao.UserTicket;

import java.math.BigDecimal;
import java.util.List;

public class YudingOrderAllInOne {
    //预定单的结构，为了避免将来增减字段，修改接口，将信息汇总在一个结构里

    //多张预订单购物车明细id
    String cartIds;

    //多预订单的时间
    String times;

    //多预订单的店铺
    String storeIds;

    //多预订单积分抵现金额
    String integralPrices;

    //多预订单使用抵现积分
    String integralBonuss;

    //多预订单的优惠券
    List<UserTicket> coupons;

    //客户留言 未使用
    String message;

    //单商品预订Id
    Integer goodId;

    //单商品的预订数量
    Short number;

    //单商品的预订时间
    String time;

    //单商品的预订店铺
    Integer storeId;


    //订单来源的商贸类型
    Integer comId;

    //积分抵现金额
    BigDecimal integralPrice;

    //使用抵现积分
    BigDecimal integralBonus;

    //订单的优惠券
    UserTicket coupon;

    //订单总金额
    BigDecimal orderTotalPrice;


    public String getCartIds() {
        return cartIds;
    }

    public void setCartIds(String cartIds) {
        this.cartIds = cartIds;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
    }

    public String getIntegralPrices() {
        return integralPrices;
    }

    public void setIntegralPrices(String integralPrices) {
        this.integralPrices = integralPrices;
    }

    public String getIntegralBonuss() {
        return integralBonuss;
    }

    public void setIntegralBonuss(String integralBonuss) {
        this.integralBonuss = integralBonuss;
    }

    public List<UserTicket> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<UserTicket> coupons) {
        this.coupons = coupons;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public Short getNumber() {
        return number;
    }

    public void setNumber(Short number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getComId() {
        return comId;
    }

    public void setComId(Integer comId) {
        this.comId = comId;
    }

    public BigDecimal getIntegralPrice() {
        return integralPrice;
    }

    public void setIntegralPrice(BigDecimal integralPrice) {
        this.integralPrice = integralPrice;
    }

    public BigDecimal getIntegralBonus() {
        return integralBonus;
    }

    public void setIntegralBonus(BigDecimal integralBonus) {
        this.integralBonus = integralBonus;
    }

    public UserTicket getCoupon() {
        return coupon;
    }

    public void setCoupon(UserTicket coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }
}
