package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信分享服务
 */
@RestController
@RequestMapping("/wx/share")
@Validated
@Api(description = "微信前端/分享管理:/wx/share")
public class WxShareController {
//	private final Log logger = LogFactory.getLog(WxShareController.class);

	@Autowired
    private org.jinyuanjava.litemall.wx.service.WechatService wechatService;

	/**
	 * 获取微信jssdk签名信息
	 *
	 * @param url 签名地址
	 * @return 签名信息
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("info")
	public Object save( HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String url="";
		try {
			String parameter = request.getParameter("url");
			url = java.net.URLDecoder.decode(parameter,"UTF-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

//		String queryString = request.getQueryString();
//		if (queryString != null && !"".equals(queryString)) {
//			if(url.indexOf("?")>0){
//				url += "&" + queryString;
//			}else{
//				url += "?" + queryString;
//			}
//		}
		System.out.println(url);
		Map<String, String> wechatParam = null;
		try {
			wechatParam =  wechatService.getWechatParam(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseUtil.ok(wechatParam);
	}

}
