package org.jinyuanjava.litemall.core.system;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统设置
 */
public class SystemConfig {
    // 小程序相关配置
    public final static String LITEMALL_WX_INDEX_NEW = "litemall_wx_index_new";
    public final static String LITEMALL_WX_INDEX_HOT = "litemall_wx_index_hot";
    public final static String LITEMALL_WX_INDEX_BRAND = "litemall_wx_index_brand";
    public final static String LITEMALL_WX_INDEX_TOPIC = "litemall_wx_index_topic";
    public final static String LITEMALL_WX_INDEX_CATLOG_LIST = "litemall_wx_catlog_list";
    public final static String LITEMALL_WX_INDEX_CATLOG_GOODS = "litemall_wx_catlog_goods";
    public final static String LITEMALL_WX_SHARE = "litemall_wx_share";

    // 微商城相关配置
    public final static String LITEMALL_WEISHANG_INDEX_HUIYUAN = "litemall_weishang_index_huiyuan";
    public final static String LITEMALL_WEISHANG_INDEX_MIAOSHA = "litemall_weishang_index_miaosha";
    public final static String LITEMALL_WEISHANG_INDEX_TUAN = "litemall_weishang_index_tuan";
    public final static String LITEMALL_WEISHANG_INDEX_BUY = "litemall_weishang_index_buy";
    public final static String LITEMALL_WEISHANG_INDEX_ACTSTORE = "litemall_weishang_index_actstore";
    public final static String LITEMALL_WEISHANG_INDEX_DAXINGSTORE = "litemall_weishang_index_daxingstore";
    public final static String LITEMALL_WEISHANG_INDEX_GUESSYOULIKE = "litemall_weishang_index_guessyoulike";

    // 运费相关配置
    public final static String LITEMALL_EXPRESS_FREIGHT_VALUE = "litemall_express_freight_value";
    public final static String LITEMALL_EXPRESS_FREIGHT_MIN = "litemall_express_freight_min";
    //北京市内发货
    public final static String LITEMALL_EXPRESS_BEIJINGCITY_MIN = "litemall_express_beijingcity_min";
    public final static String LITEMALL_EXPRESS_OTHERCITY_MIN = "litemall_express_othercity_min";

    // 订单相关配置
    //客服配置
    public final static String LITEMALL_ORDER_SERVICE_YUNQUE = "litemall_order_service_yunque";
    public final static String LITEMALL_ORDER_SERVICE_PHONE = "litemall_order_service_phone";

    public final static String LITEMALL_ORDER_UNPAID = "litemall_order_unpaid";
    public final static String LITEMALL_ORDER_UNCONFIRM = "litemall_order_unconfirm";
    public final static String LITEMALL_ORDER_COMMENT = "litemall_order_comment";
    public final static String LITEMALL_ORDER_TIHUO_BEIHUO_HOUR = "litemall_order_tihuo_beihuo_hour";
    public final static String LITEMALL_ORDER_TIHUO_MAX_DAYS = "litemall_order_tihuo_max_days";
    public final static String LITEMALL_ORDER_TIHUO_HINT_FIRST_HOUR = "litemall_order_tihuo_hint_first_hour";
    public final static String LITEMALL_ORDER_TIHUO_SECOND_HOUR = "litemall_order_tihuo_second_hour";

    public final static String LITEMALL_ORDER_YUYUE_BEIHUO_HOUR = "litemall_order_yuyue_beihuo_hour";
    public final static String LITEMALL_ORDER_YUYUE_MAX_DAYS = "litemall_order_yuyue_max_days";
    public final static String LITEMALL_ORDER_YUYUE_DELAY_HOUR = "litemall_order_yuyue_delay_hour";
    public final static String LITEMALL_ORDER_YUYUE_HINT_FIRST_HOUR = "litemall_order_yuyue_hint_first_hour";
    public final static String LITEMALL_ORDER_YUYUE_SECOND_HOUR = "litemall_order_yuyue_second_hour";

    // 商场相关配置
    public final static String LITEMALL_MALL_NAME = "litemall_mall_name";
    public final static String LITEMALL_MALL_ADDRESS = "litemall_mall_address";
    public final static String LITEMALL_MALL_PHONE = "litemall_mall_phone";
    public final static String LITEMALL_MALL_QQ = "litemall_mall_qq";




    //所有的配置均保存在该 HashMap 中
    private static Map<String, String> SYSTEM_CONFIGS = new HashMap<>();

    private static String getConfig(String keyName) {
        return SYSTEM_CONFIGS.get(keyName);
    }

    private static Integer getConfigInt(String keyName) {
        return Integer.parseInt(SYSTEM_CONFIGS.get(keyName));
    }

    private static Boolean getConfigBoolean(String keyName) {
        return Boolean.valueOf(SYSTEM_CONFIGS.get(keyName));
    }

    private static BigDecimal getConfigBigDec(String keyName) {
        return new BigDecimal(SYSTEM_CONFIGS.get(keyName));
    }

    public static Integer getNewLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_NEW);
    }

    public static Integer getHotLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_HOT);
    }

    public static Integer getBrandLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_BRAND);
    }

    public static Integer getTopicLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_TOPIC);
    }

    public static Integer getCatlogListLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_CATLOG_LIST);
    }

    public static Integer getCatlogMoreLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_CATLOG_GOODS);
    }

    public static Integer getHunyuanLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_HUIYUAN);
    }

    public static Integer getMiaoshaLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_MIAOSHA);
    }

    public static Integer getTuanLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_TUAN);
    }

    public static Integer getBuyLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_BUY);
    }

    public static Integer getActstoreLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_ACTSTORE);
    }

    public static Integer getDaxingstoreLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_DAXINGSTORE);
    }

    public static Integer getGuessyoulikeLimit() {
        return getConfigInt(LITEMALL_WEISHANG_INDEX_GUESSYOULIKE);
    }

    public static boolean isAutoCreateShareImage() {
        return getConfigBoolean(LITEMALL_WX_SHARE);
    }

    public static BigDecimal getFreight() {
        return getConfigBigDec(LITEMALL_EXPRESS_FREIGHT_VALUE);
    }

    public static BigDecimal getBeiJingFreight() {
        return getConfigBigDec(LITEMALL_EXPRESS_BEIJINGCITY_MIN);
    }
    public static BigDecimal getOtherCityFreight() {
        return getConfigBigDec(LITEMALL_EXPRESS_OTHERCITY_MIN);
    }


    public static BigDecimal getFreightLimit() {
        return getConfigBigDec(LITEMALL_EXPRESS_FREIGHT_MIN);
    }

    public static String getOrderServicePhone() {
        return getConfig(LITEMALL_ORDER_SERVICE_PHONE);
    }
    public static String getOrderServiceYueque() {
        return getConfig(LITEMALL_ORDER_SERVICE_YUNQUE);
    }

    public static Integer getOrderUnpaid() {
        return getConfigInt(LITEMALL_ORDER_UNPAID);
    }

    public static Integer getOrderUnconfirm() {
        return getConfigInt(LITEMALL_ORDER_UNCONFIRM);
    }

    public static Integer getOrderComment() {
        return getConfigInt(LITEMALL_ORDER_COMMENT);
    }

    public static Integer getOrderYuyueBeihuoHour() {
        return getConfigInt(LITEMALL_ORDER_YUYUE_BEIHUO_HOUR);
    }

    public static Integer getOrderTihuoBeihuoHour() {
        return getConfigInt(LITEMALL_ORDER_TIHUO_BEIHUO_HOUR);
    }

    public static Integer getYuyueMaxDays() {
        return getConfigInt(LITEMALL_ORDER_YUYUE_MAX_DAYS);
    }
    public static Integer getTihuoMaxDays() {
        return getConfigInt(LITEMALL_ORDER_TIHUO_MAX_DAYS);
    }


    public static Integer getOrderYuyueDelayHour() {
        return getConfigInt(LITEMALL_ORDER_YUYUE_DELAY_HOUR);
    }

    public static Integer getOrderYuyueHintFirstHour() {
        return getConfigInt(LITEMALL_ORDER_YUYUE_HINT_FIRST_HOUR);
    }

    public static Integer getOrderTihuoHintFirstHour() {
        return getConfigInt(LITEMALL_ORDER_TIHUO_HINT_FIRST_HOUR);
    }

    public static Integer getOrderYuyueSecondHour() {
        return getConfigInt(LITEMALL_ORDER_YUYUE_SECOND_HOUR);
    }
    public static Integer getOrderTihuoSecondHour() {
        return getConfigInt(LITEMALL_ORDER_TIHUO_SECOND_HOUR);
    }

    public static String getMallName() {
        return getConfig(LITEMALL_MALL_NAME);
    }

    public static String getMallAddress() {
        return getConfig(LITEMALL_MALL_ADDRESS);
    }

    public static String getMallPhone() {
        return getConfig(LITEMALL_MALL_PHONE);
    }

    public static String getMallQQ() {
        return getConfig(LITEMALL_MALL_QQ);
    }

    public static void setConfigs(Map<String, String> configs) {
        SYSTEM_CONFIGS = configs;
    }

    public static void updateConfigs(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            SYSTEM_CONFIGS.put(entry.getKey(), entry.getValue());
        }
    }
}
