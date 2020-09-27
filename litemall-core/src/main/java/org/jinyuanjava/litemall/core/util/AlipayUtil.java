package org.jinyuanjava.litemall.core.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import org.jinyuanjava.litemall.core.config.AliPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by xiezhiwei on 17/8/14.
 */

@Service
public class AlipayUtil {

    @Autowired
    private Environment environment;

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AliPayConfig aliPayConfig;

//    /**
//     * appAuthToken
//     * 如ISV代替商家调用当面付接口，需将商户授权后获取的app_auth_token带上；如商家申请当面付自己调用，则传null bizContent
//     * JSON格式 商户的请求参数
//     */
//
//    // 手机网页支付 网站支付
//    public String ydAndPc_Pay(Map<String, String> maps)
//            throws AlipayApiException {
//        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
//        String bizCon = new JSONObject(maps).toString();
//        alipayRequest.setBizContent(bizCon);
//        alipayRequest.setReturnUrl(alipay_return_url + "?orderNo=" + maps.get("out_trade_no")+"&type=0");
//        alipayRequest.setNotifyUrl(alipay_notifyUrl);
//        String form = "";
//        try {
//            form = client
//                    .pageExecute(alipayRequest).getBody();
//        } catch (AlipayApiException e) {
//            form = "err";
//            e.printStackTrace();
//        } // 调用SDK生成表单
//        return form;
//    }
//
//    // 查询订单状态
//    public AlipayTradeQueryResponse query(String appAuthToken, String bizContent)
//            throws AlipayApiException {
//        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
//        request.putOtherTextParam("app_auth_token", appAuthToken);
//        request.setBizContent(bizContent);
//        return client.execute(request);
//    }
//
//    // 条码支付
//    public AlipayTradePayResponse pay(String appAuthToken, String bizContent)
//            throws AlipayApiException {
//        AlipayTradePayRequest request = new AlipayTradePayRequest();
//        request.putOtherTextParam("app_auth_token", appAuthToken);
//        request.setBizContent(bizContent);
//        return client.execute(request);
//    }
//
//    // 扫码支付
//    public AlipayTradePrecreateResponse precreate(String appAuthToken,
//                                                  String bizContent) throws AlipayApiException {
//        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
//        request.putOtherTextParam("app_auth_token", appAuthToken);
//        request.setBizContent(bizContent);
//        return client.execute(request);
//    }
//
//    // 订单撤销
//    public AlipayTradeCancelResponse cancel(String appAuthToken,
//                                            String bizContent) throws AlipayApiException {
//        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
//        request.putOtherTextParam("app_auth_token", appAuthToken);
//        request.setBizContent(bizContent);
//        return client.execute(request);
//    }

    // 申请退款
    public AlipayTradeRefundResponse refund(String appAuthToken,
                                            String bizContent) throws AlipayApiException {

         String url=environment.getProperty("litemall.alipay.url");
        String app_id=environment.getProperty("litemall.alipay.app-id");
        String alipay_private_key=environment.getProperty("litemall.alipay.rsa-private-key");
        String alipay_public_key=environment.getProperty("litemall.alipay.alipay-public-key");
        String alipay_return_url=environment.getProperty("litemall.alipay.return-url");
        String alipay_notifyUrl=environment.getProperty("litemall.alipay.notify-url");
//        DefaultAlipayClient client = new DefaultAlipayClient(
//                url, app_id, alipay_private_key,"json", "utf-8", alipay_public_key);

        AlipayClient client = new DefaultAlipayClient(url,
                app_id, alipay_private_key, "JSON",
                "utf-8", alipay_public_key, "RSA2");


        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        //request.putOtherTextParam("app_auth_token", appAuthToken);
        request.setBizContent(bizContent);
        return client.execute(request);
    }



}
