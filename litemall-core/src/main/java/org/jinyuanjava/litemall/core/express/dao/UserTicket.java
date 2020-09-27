package org.jinyuanjava.litemall.core.express.dao;

public class UserTicket {
    //本系统的会员Id
    private Integer userId;
    //CRM里的会员编码
    private String vipCode;
    //微信OpenId
    private String openId;
    //移动手机号
    private String mobile;
    //电子券Id
    private String ticketId;
    //电子券描述
    private String ticketDesci;
    //兑换码
    private String serialId;
    //电子券种类 0：现金券  1：折扣券  3：核销券  4：时效券  5：停车券
    private String ticketType;
    //最小能抵多少金额
    private Double minAmt;
    //增加多少钱能用这张券
    private Double increaseAmt;
    //最多能抵多少金额
    private Double maxAmt;
    //折扣比例
    private Double discount;
    //生效日期
    private String effectDate;
    //过期日期
    private String expiryDate;
    //备注
    private String remark;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketDesci() {
        return ticketDesci;
    }

    public void setTicketDesci(String ticketDesci) {
        this.ticketDesci = ticketDesci;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Double getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(Double minAmt) {
        this.minAmt = minAmt;
    }

    public Double getIncreaseAmt() {
        return increaseAmt;
    }

    public void setIncreaseAmt(Double increaseAmt) {
        this.increaseAmt = increaseAmt;
    }

    public Double getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(Double maxAmt) {
        this.maxAmt = maxAmt;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(String effectDate) {
        this.effectDate = effectDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
