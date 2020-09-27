package org.jinyuanjava.litemall.wx.dto;

import org.jinyuanjava.litemall.core.express.dao.UserTicket;
import org.jinyuanjava.litemall.db.domain.*;

import java.math.BigDecimal;

public class OrderAllInOne {
    //如果是秒杀商品、团购商品、虚拟商品必须单独下单，不能与其它商品一起加入购物车一起结帐，
    //如果是会员商品，则可以和其它商品一起加入购物车，此时商品价格要取会员商品价格

    //选择的商品购物车ids
    String cartIds;

    //用户的收货地址id
    Integer addressId;

    //用户的留言
    String message;

    //单一商品立即采购的参数
    Integer productId;

    //电商订单类型编码 10、正常单 20、秒杀单 30、团购单 40、折扣商品
    String mallOrderTypeCode;

    //秒杀、团购、虚拟商品的规则Id
    Integer ruleId;

    //用户购买数量
    Short number;

    //用户单价
    BigDecimal price;


    //订单来源的商贸类型
    Integer comId;

    //积分抵现金额
    BigDecimal integralPrice;

    //使用抵现积分
    BigDecimal integralBonus;

    //订单运费
    BigDecimal freightPrice;

    //订单的优惠券
    UserTicket coupon;

    //订单的发票信息
    LitemallOrderFapiao fapiao;

    //****************************************************
    //以下是门店取货的特殊参数
    //运输方式 快递 自提取货
    String sendWay;
     //自提货点ID
    Integer pickSiteId;
    //自提货点名称
    String pickSiteName;
    //自提货日期时间
    String pickTime;
    //取货人真实姓名
    String pickPerson;
    //取货人手机号码
    String pickMobile;
    //****************************************************

    //****************************************************
    //以下是预约单的特殊参数
    //预约店铺信息
    Integer yuyueStoreId;
    //预约取货时间
    String yuyueFetchTime;
    //****************************************************

    public UserTicket getCoupon() {
        return coupon;
    }

    public void setCoupon(UserTicket coupon) {
        this.coupon = coupon;
    }

    public LitemallOrderFapiao getFapiao() {
        return fapiao;
    }

    public void setFapiao(LitemallOrderFapiao fapiao) {
        this.fapiao = fapiao;
    }

    public String getCartIds() {
        return cartIds;
    }

    public void setCartIds(String cartIds) {
        this.cartIds = cartIds;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getMallOrderTypeCode() {
        return mallOrderTypeCode;
    }

    public void setMallOrderTypeCode(String mallOrderTypeCode) {
        this.mallOrderTypeCode = mallOrderTypeCode;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Short getNumber() {
        return number;
    }

    public void setNumber(Short number) {
        this.number = number;
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

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
    }

    public Integer getPickSiteId() {
        return pickSiteId;
    }

    public void setPickSiteId(Integer pickSiteId) {
        this.pickSiteId = pickSiteId;
    }

    public String getPickTime() {
        return pickTime;
    }

    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }

    public String getPickPerson() {
        return pickPerson;
    }

    public void setPickPerson(String pickPerson) {
        this.pickPerson = pickPerson;
    }

    public String getPickMobile() {
        return pickMobile;
    }

    public void setPickMobile(String pickMobile) {
        this.pickMobile = pickMobile;
    }

    public Integer getYuyueStoreId() {
        return yuyueStoreId;
    }

    public void setYuyueStoreId(Integer yuyueStoreId) {
        this.yuyueStoreId = yuyueStoreId;
    }

    public String getYuyueFetchTime() {
        return yuyueFetchTime;
    }

    public void setYuyueFetchTime(String yuyueFetchTime) {
        this.yuyueFetchTime = yuyueFetchTime;
    }

    public String getPickSiteName() {
        return pickSiteName;
    }

    public void setPickSiteName(String pickSiteName) {
        this.pickSiteName = pickSiteName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSendWay() {
        return sendWay;
    }

    public void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }
}
