package org.jinyuanjava.litemall.wx.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  '微信支付服务器签名支付请求请求类
 '============================================================================
 'api说明：
 'init(app_id, app_secret, partner_key, app_key);
 '初始化函数，默认给一些参数赋值，如cmdno,date等。
 'setKey(key_)'设置商户密钥
 'getLasterrCode(),获取最后错误号
 'GetToken();获取Token
 'getTokenReal();Token过期后实时获取Token
 'createMd5Sign(signParams);生成Md5签名
 'genPackage(packageParams);获取package包
 'createSHA1Sign(signParams);创建签名SHA1
 'sendPrepay(packageParams);提交预支付
 'getDebugInfo(),获取debug信息
 '============================================================================
 *
 * @Filename: RequestHandler.java
 * @Version: 1.0
 * @Author: zhaihl
 * @Email: zhaihl_0@126.com
 *
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class RequestHandler {
    /** Token获取网关地址地址 */
    private String    tokenUrl;
    /** 预支付网关url地址 */
    private String    gateUrl;
    /** 查询支付通知网关URL */
    private String    notifyUrl;
    /** 商户参数 */
    private String    appid;
    private String    appkey;
    private String    partnerkey;
    private String    appsecret;
    private String    key;
    /** 请求的参数 */
    private SortedMap parameters;
    /** Token */
    private String    Token;
    private String    charset;
    private String    last_errcode;

    private HttpServletRequest request;

    private HttpServletResponse response;

    /**
     * 初始构造函数。
     *
     * @return
     */
    public RequestHandler(HttpServletRequest request, HttpServletResponse response) {
        this.last_errcode = "0";
        this.request = request;
        this.response = response;
        //this.charset = "GBK";
        this.charset = "UTF-8";
        this.parameters = new TreeMap();
        // 验证notify支付订单网关
        notifyUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";

    }

    /**
     * 初始化函数。
     */
    public void init(String app_id, String app_secret, String partner_key) {
        this.last_errcode = "0";
        this.Token = "token_";
        this.appid = app_id;
        this.partnerkey = partner_key;
        this.appsecret = app_secret;
        this.key = partner_key;
    }

    public void init() {
    }

    /**
     * 获取最后错误号
     */
    public String getLasterrCode() {
        return last_errcode;
    }

    /**
     *获取入口地址,不包含参数值
     */
    public String getGateUrl() {
        return gateUrl;
    }

    /**
     * 获取参数值
     *
     * @param parameter
     *            参数名称
     * @return String
     */
    public String getParameter(String parameter) {
        String s = (String) this.parameters.get(parameter);
        return (null == s) ? "" : s;
    }

    //设置密钥

    public void setKey(String key) {
        this.partnerkey = key;
    }

    //设置微信密钥
    public void setAppKey(String key) {
        this.appkey = key;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public String createSign(SortedMap<String, String> packageParams) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + this.getKey());
        String sign = SignUtil.MD5Encode(sb.toString(), this.charset).toUpperCase();
        return sign;

    }

    //输出XML
    public String parseXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {

                sb.append("<" + k + ">" + getParameter(k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public void setPartnerkey(String partnerkey) {
        this.partnerkey = partnerkey;
    }

    public String getKey() {
        return partnerkey;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public SortedMap getParameters() {
        return parameters;
    }

    public void setParameters(SortedMap parameters) {
        this.parameters = parameters;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getLast_errcode() {
        return last_errcode;
    }

    public void setLast_errcode(String last_errcode) {
        this.last_errcode = last_errcode;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getPartnerkey() {
        return partnerkey;
    }

    public void setGateUrl(String gateUrl) {
        this.gateUrl = gateUrl;
    }

}
