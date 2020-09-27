package org.jinyuanjava.litemall.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "litemall.alipay")
public class AliPayProperties {

    private String appId;

    private String rsaPrivateKey;

    private String alipayPublicKey;

    private String notifyPublicKey;

    private String url;

    private String notifyUrl;

    private String returnUrl;

    private String jumpUrl;
    private String sellerId;

	public String getNotifyPublicKey() {
		return notifyPublicKey;
	}

	public void setNotifyPublicKey(String notifyPublicKey) {
		this.notifyPublicKey = notifyPublicKey;
	}

	public String getJumpUrl() {
		return jumpUrl;
	}

	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(String rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}

	public String getAlipayPublicKey() {
		return alipayPublicKey;
	}

	public void setAlipayPublicKey(String alipayPublicKey) {
		this.alipayPublicKey = alipayPublicKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}


}
