package org.jinyuanjava.litemall.wx.dto;

import java.math.BigDecimal;

public class PrivateMakOrderAllInOne {
    //私人定制结构，为了避免将来增减字段，修改接口，将信息汇总在一个结构里

    //收货地址
    Integer addressId;

    //私人定制规则ID
    Integer ruleMxId;

    //私人定制品项名称
    String privateItemName;

    //私人定制交付时间
    String privateDeliverDate;

    //私人定制内容描述
    String privateContentDesc;

    //私人定制特别备注
    String privateRemark;

    //私人定制上传图
    String privateUploadPic;

    //需要支付的保证金额
    BigDecimal prePayMoney;

    //需要支付的运费金额
    BigDecimal shipCostMoney;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getRuleMxId() {
        return ruleMxId;
    }

    public void setRuleMxId(Integer ruleMxId) {
        this.ruleMxId = ruleMxId;
    }

    public String getPrivateItemName() {
        return privateItemName;
    }

    public void setPrivateItemName(String privateItemName) {
        this.privateItemName = privateItemName;
    }

    public String getPrivateDeliverDate() {
        return privateDeliverDate;
    }

    public void setPrivateDeliverDate(String privateDeliverDate) {
        this.privateDeliverDate = privateDeliverDate;
    }

    public String getPrivateContentDesc() {
        return privateContentDesc;
    }

    public void setPrivateContentDesc(String privateContentDesc) {
        this.privateContentDesc = privateContentDesc;
    }

    public String getPrivateRemark() {
        return privateRemark;
    }

    public void setPrivateRemark(String privateRemark) {
        this.privateRemark = privateRemark;
    }

    public String getPrivateUploadPic() {
        return privateUploadPic;
    }

    public void setPrivateUploadPic(String privateUploadPic) {
        this.privateUploadPic = privateUploadPic;
    }

    public BigDecimal getPrePayMoney() {
        return prePayMoney;
    }

    public void setPrePayMoney(BigDecimal prePayMoney) {
        this.prePayMoney = prePayMoney;
    }

    public BigDecimal getShipCostMoney() {
        return shipCostMoney;
    }

    public void setShipCostMoney(BigDecimal shipCostMoney) {
        this.shipCostMoney = shipCostMoney;
    }
}
