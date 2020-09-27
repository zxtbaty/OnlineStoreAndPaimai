package org.jinyuanjava.litemall.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

@Configuration
public class AliPayConfig {
    @Autowired
    private AliPayProperties properties;

    @Bean
    public AlipayClient alipayClient() {
    	String appId = properties.getAppId();
    	String apiGetwayurl = properties.getUrl();
    	String rsaPrivateKey = properties.getRsaPrivateKey();
    	String alipayPublicKey = properties.getAlipayPublicKey();

    	AlipayClient alipayClient = new DefaultAlipayClient(apiGetwayurl,appId,rsaPrivateKey,"json","utf-8",alipayPublicKey,"RSA2");
		return alipayClient;
    }

    @Bean
    public String returnUrl() {
		return properties.getReturnUrl();
    }
    @Bean
    public String notifyUrl() {
		return properties.getNotifyUrl();
    }

	public String getPublicKey() {
		return properties.getAlipayPublicKey();
	}
	public String getNotifyPublickKey(){
		return properties.getNotifyPublicKey();
	}

	public String getAppId() {
		return properties.getAppId();
	}

	public String getPrivateKey() {
		return properties.getRsaPrivateKey();
	}



	public String getJumpUrl() {
		return properties.getJumpUrl();
	}
	public String getSellerId() {
		return properties.getSellerId();
	}



}
