package org.jinyuanjava.litemall.wx.web;

import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_CAPTCHA_FREQUENCY;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_CAPTCHA_UNMATCH;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_INVALID_MOBILE;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_MOBILE_REGISTERED;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_MOBILE_UNREGISTERED;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.AUTH_OPENID_UNACCESS;
import io.swagger.annotations.Api;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.config.WxProperties;
import org.jinyuanjava.litemall.core.notify.NotifyService;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.jinyuanjava.litemall.core.util.HttpClientManager;
import org.jinyuanjava.litemall.core.util.InterfactMonitorUtil;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.RegexUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.service.AliYunSendSms;
import org.jinyuanjava.litemall.db.service.CouponAssignService;
import org.jinyuanjava.litemall.db.service.LitemallUserService;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.dto.UserInfo;
import org.jinyuanjava.litemall.wx.dto.UserToken;
import org.jinyuanjava.litemall.wx.dto.WxLoginInfo;
import org.jinyuanjava.litemall.wx.service.CaptchaCodeManager;
import org.jinyuanjava.litemall.wx.service.UserTokenManager;
import org.jinyuanjava.litemall.wx.util.EmojiFilter;
import org.jinyuanjava.litemall.wx.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;

import com.alibaba.fastjson.JSONObject;
import com.vdurmont.emoji.EmojiParser;

/**
 * 鉴权服务
 */
@RestController
@RequestMapping("/wx/auth")
@Validated
@Api(description = "微信前端/授权管理:/wx/auth")
public class WxAuthController {
    private final Log logger = LogFactory.getLog(WxAuthController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private WxMaService wxService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private CouponAssignService couponAssignService;
//    @Autowired
//    private WxLoginProperties wxLoginProperties;
    @Autowired
    private WxProperties wxLoginProperties;

    @Autowired
    private InterfactMonitorUtil interfactMonitorUtil;

    @Autowired
    private AliYunSendSms aliYunSendSms;

    /**
     * 账号登录
     *
     * @param body    请求内容，{ mobile: xxx, password: xxx }
     * @param request 请求对象
     * @return 登录结果
     */
    @PostMapping("login")
    public Object login(@LoginUser Integer userId,@RequestBody String body, HttpServletRequest request) {
        //        String username = JacksonUtil.parseString(body, "username");
        //去掉密码 通过验证码登陆并且绑定微信公众号
//        String password = JacksonUtil.parseString(body, "password");

        JSONObject paras= (JSONObject)JSONObject.parse(body);
        String mobile =CharUtil.objectConverToString(paras.get("mobile"));
        String code = CharUtil.objectConverToString(paras.get("code"));
        LitemallUser checkUser = userService.findById(userId);
        String openId = checkUser.getWeixinOpenid();


        if ( StringUtils.isEmpty(mobile)){
            return ResponseUtil.fail(502,"手机号不能为空");
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(AUTH_INVALID_MOBILE, "手机号格式不正确");
        }

//        if ( StringUtils.isEmpty(code)){
//            return ResponseUtil.fail(502,"验证码不能为空");
//        }
//        //判断验证码是否正确
        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() ) {
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "请输入验证码");
        } else
        {
            if(!cacheCode.equals(code)){
                return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误,请重新获取");
            }
        }

        if ( StringUtils.isEmpty(openId)){
            return ResponseUtil.fail(502,"微信号不能为空");
        }


        //客户都是通过微信号来登陆系统
//        String openId = null;
//        try {
//            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(wxCode);
//            openId = result.getOpenid();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "openid 获取失败");
//        }
//        LitemallUser checkUser  = userService.queryByOneOpenid("商城",openId);

        if (checkUser !=null) {
            //已经有该OPenID，需要绑定手机号
            checkUser.setMobile(mobile);
            userService.updateById(checkUser);
        }else{
        	 return ResponseUtil.fail(502,"请重新登录");
        }

        LitemallUser userInfo= userService.queryByOpenid(openId);

        // 返回注册成功的信息
//        UserInfo userInfo = new UserInfo();
//        userInfo.setNickName(checkUser.getUsername());
//        userInfo.setAvatarUrl(checkUser.getAvatar());

        // token 这里应该判断一下是否是新用户，如果是新用户返回相应的信息，如果是老用户则取系统原来的Token,前台不要刷新
        UserToken userToken = UserTokenManager.generateToken(checkUser.getId());
        userInfo.setToken(userToken.getToken());
        userInfo.setTokenExpire(userToken.getExpireTime().toString());

        System.out.println(userInfo.getNickname());
        System.out.println("成功"+userInfo);
        return ResponseUtil.ok(userInfo);


    }

    /**
     * 微信登录
     *
     * @param wxLoginInfo 请求内容，{ code: xxx, userInfo: xxx }
     * @param request     请求对象
     * @return 登录结果
     */
    @PostMapping("login_by_weixin")
    public Object loginByWeixin(@RequestBody WxLoginInfo wxLoginInfo, HttpServletRequest request) {
        String code = wxLoginInfo.getCode();
        UserInfo userInfo = wxLoginInfo.getUserInfo();
        if (code == null || userInfo == null) {
//            return ResponseUtil.badArgument();
            return ResponseUtil.fail(502,"微信参数错误");
        }

        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return ResponseUtil.fail(502,"没有取到微信的OpenId");
        }

        LitemallUser user = userService.queryByOid(openId);
        if (user == null) {
            user = new LitemallUser();
            user.setSourceId("商城");
            user.setUsername(openId);
            user.setPassword(openId);
            user.setWeixinOpenid(openId);
            user.setAvatar(userInfo.getAvatarUrl());
            String nickName = userInfo.getNickName();
            String removeAllEmojis = EmojiParser.removeAllEmojis(nickName);
            user.setNickname(removeAllEmojis);
            user.setGender(userInfo.getGender());
            user.setUserLevel((byte) 0);
            user.setStatus((byte) 0);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));

            userService.add(user);

            // 新用户发送注册优惠券
            couponAssignService.assignForRegister(user.getId());
        } else {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));
            if (userService.updateById(user) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }

        // token
        UserToken userToken = UserTokenManager.generateToken(user.getId());
        userToken.setSessionKey(sessionKey);

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", userToken.getToken());
        result.put("tokenExpire", userToken.getExpireTime().toString());
        result.put("userInfo", userInfo);

        return ResponseUtil.ok(result);
    }


    /**
     * 请求验证码
     *
     * @param body 手机号码{mobile}
     * @return
     */
    @PostMapping("regCaptcha")
    public Object registerCaptcha(@RequestBody String body) {
        String phoneNumber = JacksonUtil.parseString(body, "mobile");
        if (StringUtils.isEmpty(phoneNumber)) {

            return ResponseUtil.fail(502,"手机号不能为空");
        }
        if (!RegexUtil.isMobileExact(phoneNumber)) {

            return ResponseUtil.fail(502,"手机号格式不正确");
        }

//        if (!notifyService.isSmsEnable()) {
//            return ResponseUtil.fail(AUTH_CAPTCHA_UNSUPPORT, "小程序后台验证码服务不支持");
//        }

        String code = CharUtil.getRandomNum(6);



        boolean successful = CaptchaCodeManager.addToCache(phoneNumber, code);
        if (!successful) {
            return ResponseUtil.fail(AUTH_CAPTCHA_FREQUENCY, "验证码未超时5分钟，不能发送");
        }

//        Map<String, Object> valueMap=new HashMap<>();
//        valueMap.put("code",code);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",code);


//        notifyService.notifySmsTemplate(phoneNumber, NotifyType.CAPTCHA, valueMap);

        aliYunSendSms.sendmessage(phoneNumber,jsonObject.toJSONString());

        return ResponseUtil.ok();
    }

    /**
     * 账号注册
     *
     * @param body    请求内容
     *                {
     *                username: xxx,
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * token: xxx,
     * tokenExpire: xxx,
     * userInfo: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("register")
    public Object register(@RequestBody String body, HttpServletRequest request) {
//        String username = JacksonUtil.parseString(body, "username");
        //去掉密码 通过验证码登陆并且绑定微信公众号
//        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        String wxCode = JacksonUtil.parseString(body, "wxCode");

        if ( StringUtils.isEmpty(mobile)){
            return ResponseUtil.fail(502,"手机号不能为空");
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(AUTH_INVALID_MOBILE, "手机号格式不正确");
        }

        if ( StringUtils.isEmpty(code)){
            return ResponseUtil.fail(502,"验证码不能为空");
        }

        if ( StringUtils.isEmpty(wxCode)){
            return ResponseUtil.fail(502,"微信号不能为空");
        }

        //判断验证码是否正确
        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
        }

        //客户都是通过微信号来登陆系统
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(wxCode);
            openId = result.getOpenid();
        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "openid 获取失败");
        }
        List<LitemallUser> userList = userService.queryByOpenid("商城",openId);
        LitemallUser checkUser = userList.get(0);

        if (userList.size() > 1) {
            return ResponseUtil.fail(502,"航诚商贸存在多个相同的OpenId:"+openId);
        }
        if (userList.size() == 1) {
            //已经有该OPenID，需要绑定手机号
            checkUser= userList.get(0);
            checkUser.setMobile(mobile);
            userService.updateById(checkUser);
//            String checkUsername = checkUser.getUsername();
//            String checkPassword = checkUser.getPassword();
//            if (!checkUsername.equals(openId) || !checkPassword.equals(openId)) {
//                return ResponseUtil.fail(AUTH_OPENID_BINDED, "openid已绑定账号");
//            }
        } else
        {
            //新注册用户
            List<LitemallUser> mobileUser = userService.queryByMobile(mobile);
            if (mobileUser.size() > 0) {
                return ResponseUtil.fail(AUTH_MOBILE_REGISTERED, "手机号已注册");
            }
            LitemallUser user = null;
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            String encodedPassword = encoder.encode(password);
            user = new LitemallUser();
            user.setSourceId("商城");
            user.setUsername(mobile);
//            user.setPassword(encodedPassword);
            user.setMobile(mobile);
            user.setWeixinOpenid(openId);
            user.setAvatar("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64");
            user.setNickname(mobile);
            user.setGender((byte) 0);
            user.setUserLevel((byte) 0);
            user.setStatus((byte) 0);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));
            userService.add(user);
            checkUser=user;
            // 给新用户发送注册优惠券
//        couponAssignService.assignForRegister(user.getId());
        }

        // 返回注册成功的信息
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(checkUser.getUsername());
        userInfo.setAvatarUrl(checkUser.getAvatar());

        // token 这里应该判断一下是否是新用户，如果是新用户返回相应的信息，如果是老用户则取系统原来的Token,前台不要刷新
        UserToken userToken = UserTokenManager.generateToken(checkUser.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", userToken.getToken());
        result.put("tokenExpire", userToken.getExpireTime().toString());
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }

    /**
     * 账号密码重置
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("reset")
    public Object reset(@RequestBody String body, HttpServletRequest request) {
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");

        if (mobile == null || code == null || password == null) {
            return ResponseUtil.badArgument();
        }

        //判断验证码是否正确
        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code))
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");

        List<LitemallUser> userList = userService.queryByMobile(mobile);
        LitemallUser user = null;
        if (userList.size() > 1) {
            return ResponseUtil.serious();
        } else if (userList.size() == 0) {
            return ResponseUtil.fail(AUTH_MOBILE_UNREGISTERED, "手机号未注册");
        } else {
            user = userList.get(0);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    @PostMapping("bindPhone")
    public Object bindPhone(@LoginUser Integer userId, @RequestBody String body) {
        String sessionKey = UserTokenManager.getSessionKey(userId);
        String encryptedData = JacksonUtil.parseString(body, "encryptedData");
        String iv = JacksonUtil.parseString(body, "iv");
        WxMaPhoneNumberInfo phoneNumberInfo = this.wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        String phone = phoneNumberInfo.getPhoneNumber();
        LitemallUser user = userService.findById(userId);
        user.setMobile(phone);
        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("logout")
    public Object logout(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        UserTokenManager.removeToken(userId);
        return ResponseUtil.ok();
    }


    @RequestMapping("loginInit")
    public String loginInit(HttpServletRequest request) throws Exception  {
    	    //回调地址,要跟下面的地址能调通(getWechatGZAccessToken.do)
//    	    String backUrl=wxLoginProperties.getBackUrl();
    	    String appId = wxLoginProperties.getAppId();
    	    /**
    		*这儿一定要注意！！首尾不能有多的空格（因为直接复制往往会多出空格），其次就是参数的顺序不能变动
    	    **/
    	    String rurl = request.getParameter("rurl");
//    	    int length = rurl.length();
//    	    if(length>128){
//    	    	rurl=URLEncoder.encode( backUrl,"UTF-8");
//    	    }
    	    System.out.println(rurl);

             String url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId+
//                "&redirect_uri=" + URLEncoder.encode( backUrl,"UTF-8")+
                "&redirect_uri=" + rurl+
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state="+new Date().getTime()+
                "&connect_redirect=1#wechat_redirect";


    	    return url;
}


    @GetMapping("getwechataccesstoken")
    public void getWechatGZAccessToken(HttpServletRequest request,HttpServletResponse response) throws Exception{
    		System.out.println("----------------");
    		String appId = wxLoginProperties.getAppId();
    		String appSecret = wxLoginProperties.getAppSecret();

    		//微信公众号的APPID和APPSECRET
    		String code=request.getParameter("code");
    		String state=request.getParameter("state");
    		state = URLDecoder.decode(state, "UTF-8");

            String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId+
                    "&secret=" +appSecret+
                    "&code=" +code+
                    "&grant_type=authorization_code";
            String result = HttpClientManager.getUrlData(url);
            org.json.JSONObject data = new org.json.JSONObject(result);

            String openid=data.get("openid").toString();
            String token=data.get("access_token").toString();
            //获取信息
            String infoUrl="https://api.weixin.qq.com/sns/userinfo?access_token=" +token+
                    "&openid=" +openid+
                    "&lang=zh_CN";
            String infoResult = HttpClientManager.getUrlData(infoUrl);
            data = new org.json.JSONObject(infoResult);

            int sex=1;
			try {
				sex = data.getInt("sex");
			} catch (Exception e) {
//				e.printStackTrace();
			}
            String nickname = data.getString("nickname");
            String opid = data.getString("openid");
            List<LitemallUser> queryByOpenid = userService.queryByOpenid("商城",openid);
            LitemallUser user = null;
            if(queryByOpenid!=null && queryByOpenid.size()>0){
            	user = queryByOpenid.get(0);
            }else{
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode("111111");
                user = new LitemallUser();
                user.setSourceId("商城");
                user.setUsername(opid);
                user.setPassword(encodedPassword);
                user.setMobile("");
                user.setWeixinOpenid(opid);
                user.setAvatar(data.getString("headimgurl"));
                user.setNickname(nickname);
                user.setGender(Byte.parseByte(sex+""));
                user.setUserLevel((byte) 0);
                user.setStatus((byte) 0);
                user.setLastLoginTime(LocalDateTime.now());
                user.setLastLoginIp(IpUtil.client(request));
                userService.add(user);

                // 给新用户发送注册优惠券
//                couponAssignService.assignForRegister(user.getId());
            }


            // userInfo
            UserInfo userInfo = new UserInfo();
            userInfo.setNickName(nickname);
            userInfo.setAvatarUrl(user.getAvatar());

            // token
            UserToken userToken = UserTokenManager.generateToken(user.getId());
//            Map<Object, Object> resultMap = new HashMap<Object, Object>();
//            resultMap.put("token", userToken.getToken());
//            resultMap.put("tokenExpire", userToken.getExpireTime().toString());
//            resultMap.put("userInfo", userInfo);
            response.sendRedirect(state+"?token="+userToken.getToken()+"&data=dfolkhytjjk");

    	}

    @GetMapping("newgetwechataccesstoken")
    public Object newGetWechatGZAccessToken(HttpServletRequest request,HttpServletResponse response) throws Exception{
    		System.out.println("----------------");
    		String appId = wxLoginProperties.getAppId();
    		String appSecret = wxLoginProperties.getAppSecret();

    		//微信公众号的APPID和APPSECRET
    		String code=request.getParameter("code");
            String getCrm=request.getParameter("crm");
            Boolean ifGetCrm=true;
            if("false".equals(getCrm)){
                ifGetCrm=false;
            }

            String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId+
                    "&secret=" +appSecret+
                    "&code=" +code+
                    "&grant_type=authorization_code";
            String result = HttpClientManager.getUrlData(url);
            org.json.JSONObject data = new org.json.JSONObject(result);

            System.out.println(data.toString());

            String openid=data.get("openid").toString();
            String token=data.get("access_token").toString();
            //获取信息
            String infoUrl="https://api.weixin.qq.com/sns/userinfo?access_token=" +token+
                    "&openid=" +openid+
                    "&lang=zh_CN";
            String infoResult = HttpClientManager.getUrlData(infoUrl);
            data = new org.json.JSONObject(infoResult);

            int sex=1;
			try {
				sex = data.getInt("sex");
			} catch (Exception e) {
//				e.printStackTrace();
			}
			String string = data.getString("nickname");
			String filterEmoji = EmojiFilter.filterEmoji(string);
            String nickname =EmojiParser.removeAllEmojis( filterEmoji);
            System.out.println(nickname);
            String opid = data.getString("openid");
            List<LitemallUser> queryByOpenid = userService.queryByOpenid("商城",openid);
            LitemallUser user = null;

            if(queryByOpenid!=null && queryByOpenid.size()>0){
            	user = queryByOpenid.get(0);
            }else{
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode("111111");

                //CrmUserInfo userInfo=this.getLoginUserInfo(opid,true);
                List<LitemallUser> qByOpenid = userService.queryByOpenid("商城",openid);
                if(qByOpenid!=null && qByOpenid.size()>0){
                	user=qByOpenid.get(0);
                	 user.setSourceId("商城");
                     user.setUsername(opid);
                     user.setPassword(encodedPassword);
                     user.setMobile("");
                     user.setWeixinOpenid(opid);
                     user.setAvatar(data.getString("headimgurl"));
                     user.setNickname(nickname);
                     user.setGender(Byte.parseByte(sex+""));
                     user.setUserLevel((byte) 0);
                     user.setStatus((byte) 0);
                     user.setLastLoginTime(LocalDateTime.now());
                     user.setLastLoginIp(IpUtil.client(request));
                     userService.updateById(user);
                }else{
                	 user = new LitemallUser();
                	 user.setSourceId("商城");
                     user.setUsername(opid);
                     user.setPassword(encodedPassword);
                     user.setMobile("");
                     user.setWeixinOpenid(opid);
                     user.setAvatar(data.getString("headimgurl"));
                     user.setNickname(nickname);
                     user.setGender(Byte.parseByte(sex+""));
                     user.setUserLevel((byte) 0);
                     user.setStatus((byte) 0);
                     user.setLastLoginTime(LocalDateTime.now());
                     user.setLastLoginIp(IpUtil.client(request));
                     userService.add(user);
                }

                // 给新用户发送注册优惠券
//                couponAssignService.assignForRegister(user.getId());
            }

            // 返回注册成功的信息
//            UserInfo userInfo = new UserInfo();
//            userInfo.setNickName(checkUser.getUsername());
//            userInfo.setAvatarUrl(checkUser.getAvatar());

            // token 这里应该判断一下是否是新用户，如果是新用户返回相应的信息，如果是老用户则取系统原来的Token,前台不要刷新
            UserToken userToken = UserTokenManager.generateToken(user.getId());
            user.setToken(userToken.getToken());
            user.setTokenExpire(userToken.getExpireTime().toString());

            System.out.println(user.getNickname());
            System.out.println("成功"+user);
            return ResponseUtil.ok(user);
    	}



    @PostMapping("checkToken")
    public Object checkToken(@LoginUser Integer userId) {
        //因为用户在进入的时候已经保存了用户信息，由于CRM网络未能及时连能，
        // 可能会出现点我的时候出这个，这时提示正在与CRM连接
    	if(userId==null || userId==0){
    		return ResponseUtil.fail(502,"用户未登陆");
    	}

    	 LitemallUser checkUser = userService.queryByUserId(userId);
    	 if(checkUser!=null){
    	        // token 这里应该判断一下是否是新用户，如果是新用户返回相应的信息，如果是老用户则取系统原来的Token,前台不要刷新
    	       UserToken userToken = UserTokenManager.generateToken(checkUser.getId());
               checkUser.setToken(userToken.getToken());
               checkUser.setTokenExpire(userToken.getExpireTime().toString());
           return ResponseUtil.ok(checkUser);
    	 }else{
    		 return ResponseUtil.fail();
    	 }
    }


}
