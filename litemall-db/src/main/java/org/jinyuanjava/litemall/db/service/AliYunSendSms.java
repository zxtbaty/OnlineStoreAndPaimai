package org.jinyuanjava.litemall.db.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by lizeng on 2017/1/13.
 */
@Service
public class AliYunSendSms {

    @Autowired
    private LitemallSystemConfigService systemConfigService;
    @Autowired
    private LitemallOpadminInfoService opadminInfoService;
    @Autowired
    private LitemallAdminService adminService;

    public  enum SmsConfig{
        placeholder("",""),
        register("SMS_14265297","注册验证"),
        forgetPassword("SMS_14265295","变更验证"),
        login("SMS_14265299","登录验证"),
        bindPhone("SMS_14265301","身份验证"),
        huodong("SMS_90965071","活动验证");
        private String template;
        private String sign;

        private SmsConfig(String template, String sign) {
            this.template = template;
            this.sign = sign;
        }

        public String getTemplate() {
            return template;
        }

        public String getSign() {
            return sign;
        }
    }

    public void sendmessage(String phone, String jsonString){
        Map<String, String> configInfo= systemConfigService.listMall();
        Object object=configInfo.get("sys_para_sms_access_key_id");
        if(object==null){
            return;
        }
        object=configInfo.get("sys_para_sms_access_key_secret");
        if(object==null){
            return;
        }
        object=configInfo.get("sys_para_sms_template_code");
        if(object==null){
            return;
        }
        String AccessKeyId=configInfo.get("sys_para_sms_access_key_id").toString();
        String AccessKeySecret=configInfo.get("sys_para_sms_access_key_secret").toString();
        String TemplateCode=configInfo.get("sys_para_sms_template_code").toString();

//        jsonString= "{\"flowStartName\":\"21春点拨五年级英语R-精通下\"," +
//                "\"taskName\":\"定作者\",\"days\":\"2\"}";
        //传入手机号，一次调用支持发送一个手机号验证码
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");//日期别乱写
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);//手机号
        request.putQueryParameter("SignName", "国创荣德");//签名名称

        request.putQueryParameter("TemplateCode", TemplateCode);//模板code
        request.putQueryParameter("TemplateParam",jsonString);//这个里面填写验证码内容
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }
}
