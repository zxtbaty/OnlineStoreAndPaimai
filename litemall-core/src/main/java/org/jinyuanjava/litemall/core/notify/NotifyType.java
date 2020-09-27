package org.jinyuanjava.litemall.core.notify;

public enum NotifyType {
    PAY_SUCCEED("paySucceed"),
    SHIP("ship"),
    REFUND("refund"),
    CAPTCHA("captcha"),
    YUYUE_FIRST_WARN("yuyueFirstWarn"),
    YUYUE_SECOND_WARN("yuyueSecondWarn"),
    TIHUO_FIRST_WARN("tihuoFirstWarn"),
    TIHUO_SECOND_WARN("tihuoSecondWarn"),

    YUYUE_HAND_CANCEL_INFORM_CUSTOMER("yuyueHandCancelInformCustomer"),
    YUYUE_HAND_CANCEL_INFORM_SERVICE("yuyueHandCancelInformService"),
    YUYUE_AUTO_CANCEL_INFORM_CUSTOMER("yuyueAutoCancelInformCustomer"),
    YUYUE_AUTO_CANCEL_INFORM_SERVICE("yuyueAutoCancelInformService"),
    YUYUE_INFORM_SERVICE("yuyueInformService"),
    YUYUE_CREATE_INFORM_CUSTOMER("yuyueCreateInformCustomer"),
    YUYUE_CREATE_INFORM_SERVICE("yuyueCreateInformService");

    private String type;

    NotifyType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
