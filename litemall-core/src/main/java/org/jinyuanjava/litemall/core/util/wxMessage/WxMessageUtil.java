package org.jinyuanjava.litemall.core.util.wxMessage;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jinyuanjava.litemall.core.config.WxProperties;
import org.jinyuanjava.litemall.core.util.HttpClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
@Service
public class WxMessageUtil {

	 	@Autowired
	    private WxProperties properties;
//	 	private String cureentAppId ="wxa144890599ff3bdb";
//	 	private String cureentAppSecret ="b6030db2bc837f0faeba3ca9cb050925";
		/**
		 * 根据微信id，secret获取access_token
		 *
		 */
		public String getAccessToken() throws Exception{
//			String cureentAppId = properties.getAppId();
//			String cureentAppSecret = properties.getAppSecret();

//			String tmpUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + cureentAppId
//					+ "&secret=" + cureentAppSecret + "";

            //以下代码从科传后台CRM获取Token
			String url="https://actcrm.bdia.com.cn/DataHandler.ashx?method=GetToken";
			String result = HttpClientManager.getUrlData(url);
			JSONObject data = (JSONObject)JSONObject.parse(result);
			JSONObject dataTemp= (JSONObject)data.get("Data");
			String strToken= dataTemp.get("token").toString();
            return strToken;


//			CloseableHttpClient httpCilent = HttpClients.createDefault();
//			HttpGet httpGet = new HttpGet(tmpUrl);
//			try {
//				HttpResponse httpResponse = httpCilent.execute(httpGet);
//				if (httpResponse.getStatusLine().getStatusCode() == 200) {
//					String entity = EntityUtils.toString(httpResponse.getEntity());
//					AccessToken accessToken = JSONObject.parseObject(entity, AccessToken.class);
//					return accessToken.getAccess_token();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					// 释放资源
//					httpCilent.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			return "";
		}

		/**
		 * 发送微信消息模板
		 *
		 */
		public String sendTemplate(String touser, String formId, String templateId, String clickurl, String[] fillData) throws Exception {
			String tepUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="
					+ getAccessToken();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(tepUrl);
			// 装配post请求参数
			JSONObject json = new JSONObject();
			json.put("touser", touser);
			json.put("template_id", templateId);
			json.put("form_id", formId);
			json.put("emphasis_keyword", "keyword1.DATA");
			JSONObject dataJson = new JSONObject();
			for (int i = 0; i < fillData.length; i++) {
				JSONObject sonDateJson = new JSONObject();
				sonDateJson.put("value", fillData[i]);
				dataJson.put("keyword" + (i + 1), sonDateJson);
			}
			json.put("data", dataJson);
			String resultStr = "发送失败";
			try {
				StringEntity myEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);

				// 设置post求情参数
				httpPost.setEntity(myEntity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 发送成功
					String resutlEntity = EntityUtils.toString(httpResponse.getEntity());
					System.out.println(resutlEntity);
					ResultTemplateDate resultTemplateDate = JSONObject.parseObject(resutlEntity, ResultTemplateDate.class);
					if (resultTemplateDate.getErrcode().equals("40037")) {
						resultStr = "template_id不正确";
					}
					if (resultTemplateDate.getErrcode().equals("41028")) {
						resultStr = "form_id不正确，或者过期";
					}
					if (resultTemplateDate.getErrcode().equals("41029")) {
						resultStr = "form_id已被使用";
					}
					if (resultTemplateDate.getErrcode().equals("41030")) {
						resultStr = "page不正确";
					}
					if (resultTemplateDate.getErrcode().equals("45009")) {
						resultStr = "接口调用超过限额（目前默认每个帐号日调用限额为100万）";
					}
					resultStr = "ok";
					return resultStr;
				} else {
					// 发送失败
					return resultStr;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (httpClient != null) {
						// 释放资源
						httpClient.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return resultStr;
		}
		public static void main(String[] args) throws Exception {
			 String[] a = new String[3];
	            a[0]="0";
	            a[1]="1";
	            a[2]="2";
			String sendTemplate = new WxMessageUtil().sendTemplate("oEsEZ1smAq6MAAT-JuhrsY1UTXdw", "12345", "LmYvCfM7xPjsSA1m6b-WgoH1hEVw0UM_m0HOP4_ycbE", "http://www.baidu.com", a);
			System.out.println(sendTemplate);
		}

}
