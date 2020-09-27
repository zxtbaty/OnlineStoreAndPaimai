package org.jinyuanjava.litemall.core.config;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yichi.alipay.pay.inter.AlipayResultQuery;
import com.yichi.alipay.pay.util.AlipayConfig;

public class AlipaySonResultQuery extends AlipayResultQuery {
	private AlipayConfig alipayConfig;

	public AlipayConfig getAlipayConfig() {
		return this.alipayConfig;
	}

	public void setAlipayConfig(AlipayConfig alipayConfig) {
		this.alipayConfig = alipayConfig;
	}

	public AlipayTradeQueryResponse QueryOrder(String out_trade_no,
			String trade_no) throws AlipayApiException {
		System.out.println("------进入支付宝的查询接口-----");
		AlipayClient alipayClient = new DefaultAlipayClient(
				"https://openapi.alipay.com/gateway.do",
				this.alipayConfig.getApp_id(),
				this.alipayConfig.getPrivate_key(), "json", "UTF-8",
				this.alipayConfig.getAlipay_public_key(), "RSA2");

		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		Map param = new HashMap();
		if ((null != out_trade_no) && (!("".equalsIgnoreCase(out_trade_no)))) {
			param.put("out_trade_no", out_trade_no);
		}
		if ((null != trade_no) && (!("".equalsIgnoreCase(trade_no)))) {
			param.put("trade_no", trade_no);
		}
		request.setBizContent(JSON.toJSONString(param));
		System.out.println("-------查询接口的使用的请求参数为：" + request.toString());

		AlipayTradeQueryResponse response = (AlipayTradeQueryResponse) alipayClient
				.execute(request);
		String code = response.getCode();
		return response;
	}


}
