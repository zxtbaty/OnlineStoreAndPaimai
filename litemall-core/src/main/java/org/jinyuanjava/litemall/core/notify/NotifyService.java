package org.jinyuanjava.litemall.core.notify;

import org.json.JSONObject;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.jinyuanjava.litemall.db.service.LitemallUserinfoDefService;
import org.jinyuanjava.litemall.db.util.NotifyPlaceholderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城通知服务类
 */
public class NotifyService {
    private MailSender mailSender;
    private String sendFrom;
    private String sendTo;

    private SmsSender smsSender;


    @Autowired
    private LitemallUserinfoDefService userinfoDefService;

    @Resource
    private NotifyPlaceholderResolver notifyPlaceholderResolver;

    private List<Map<String, String>> smsTemplate = new ArrayList<>();

    private WxTemplateSender wxTemplateSender;
    private List<Map<String, String>> wxTemplate = new ArrayList<>();

    public boolean isMailEnable() {
        return mailSender != null;
    }

    public boolean isSmsEnable() {
        return smsSender != null;
    }

    public boolean isWxEnable() {
        return wxTemplateSender != null;
    }

    /**
     * 短信消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param message     短消息内容，这里短消息内容必须已经在短信平台审核通过
     */
    @Async
    public void notifySms(String phoneNumber, String message) {

        smsSender.send(phoneNumber, message);

    }

//    /**
//     * 短信模版消息通知
//     *
//     * @param phoneNumber 接收通知的电话号码
//     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
//     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
//     */
//    @Async
//    public void notifySmsTemplate(String phoneNumber, NotifyType notifyType, String[] params) {
//        if (smsSender == null) {
//            return;
//        }
//        String templateIdStr = getTemplateId(notifyType, smsTemplate);
//        if (templateIdStr == null) {
//            return;
//        }
//        int templateId = Integer.parseInt(templateIdStr);
//        smsSender.sendWithTemplate(phoneNumber, templateId, params);
//    }


    /**
     * 短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param valueMap      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    @Async
    public void notifySmsTemplate(String phoneNumber, NotifyType notifyType, Map<String, Object> valueMap) {
//        if (shouduRemoteCrm == null) {
//            return;
//        }

        String strInfoType="系统消息";
        String strInfoTitle="";
        if(notifyType==NotifyType.PAY_SUCCEED){
            //支付成功
            strInfoTitle="支付成功";
            return;
        } else  if(notifyType==NotifyType.SHIP){
            //订单发货通知
            strInfoTitle="订单已发货";
        }  else if(notifyType==NotifyType.REFUND){
            //退款成功
            strInfoTitle="退款成功";
        } else if(notifyType==NotifyType.CAPTCHA){
            //手机验证码
            strInfoTitle="手机验证码";
        }else if(notifyType==NotifyType.YUYUE_FIRST_WARN){
            //预约单首次提醒
            strInfoTitle="预约单首次提醒";
        }else if(notifyType==NotifyType.YUYUE_SECOND_WARN){
            //预约单复提醒
            strInfoTitle="预约单复提醒";
        }else if(notifyType==NotifyType.TIHUO_FIRST_WARN){
            //提货单首次提醒
            strInfoTitle="提货单首次提醒";
        }else if(notifyType==NotifyType.TIHUO_SECOND_WARN){
            //提货单复提醒
            strInfoTitle="提货单复提醒";
        }else if(notifyType==NotifyType.YUYUE_HAND_CANCEL_INFORM_CUSTOMER){
            //预约单客户取消
            strInfoTitle="预约单客户取消";
        }else if(notifyType==NotifyType.YUYUE_HAND_CANCEL_INFORM_SERVICE){
            //预约单客户取消通知客服
            strInfoTitle="预约单客户取消通知客服";
        }else if(notifyType==NotifyType.YUYUE_AUTO_CANCEL_INFORM_CUSTOMER){
            //预约单超时取消
            strInfoTitle="预约单超时取消";
        }else if(notifyType==NotifyType.YUYUE_AUTO_CANCEL_INFORM_SERVICE){
            //预约单超时取消通知客服
            strInfoTitle="预约单超时取消通知客服";
        }else if(notifyType==NotifyType.YUYUE_INFORM_SERVICE){
            //预约单提醒通知客服
            strInfoTitle="预约单提醒通知客服";
        }else if(notifyType==NotifyType.YUYUE_CREATE_INFORM_CUSTOMER){
            //预约单创建成功
            strInfoTitle="预约单创建成功";
        }else if(notifyType==NotifyType.YUYUE_CREATE_INFORM_SERVICE){
            //预约单创建成功通知客服
            strInfoTitle="预约单创建成功通知客服";
        }
        //查询后端消息定义表
        List<LitemallUserinfoDef> userinfoDefs= userinfoDefService.querySelective(null,strInfoType,strInfoTitle,0,1,10,null);
        if(userinfoDefs.size()<=0){
            return ;
        }
        LitemallUserinfoDef firstDef=userinfoDefs.get(0);
        String content=firstDef.getContent();
        String replaceContent= notifyPlaceholderResolver.resolveByMap(content,valueMap);
        replaceContent=replaceContent.replace("<p>","").replace("</p>","");
        System.out.println(phoneNumber+":"+replaceContent);
        notifySms(phoneNumber,replaceContent);

    }

    /**
     * 短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param valueMap      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    @Async
    public void notifySmsTemplateByALi(String phoneNumber, NotifyType notifyType, Map<String, Object> valueMap) {
        String strInfoType="系统消息";
        String strInfoTitle="";
        if(notifyType==NotifyType.PAY_SUCCEED){
            //支付成功
            strInfoTitle="支付成功";
            return;
        } else  if(notifyType==NotifyType.SHIP){
            //订单发货通知
            strInfoTitle="订单已发货";
        }  else if(notifyType==NotifyType.REFUND){
            //退款成功
            strInfoTitle="退款成功";
        } else if(notifyType==NotifyType.CAPTCHA){
            //手机验证码
            strInfoTitle="手机验证码";
        }else if(notifyType==NotifyType.YUYUE_FIRST_WARN){
            //预约单首次提醒
            strInfoTitle="预约单首次提醒";
        }else if(notifyType==NotifyType.YUYUE_SECOND_WARN){
            //预约单复提醒
            strInfoTitle="预约单复提醒";
        }
        //查询后端消息定义表
        List<LitemallUserinfoDef> userinfoDefs= userinfoDefService.querySelective(null,strInfoType,strInfoTitle,0,1,10,null);
        if(userinfoDefs.size()<=0){
            return ;
        }
        LitemallUserinfoDef firstDef=userinfoDefs.get(0);
        String content=firstDef.getContent();
        String replaceContent= notifyPlaceholderResolver.resolveByMap(content,valueMap);
        replaceContent=replaceContent.replace("<p>","").replace("</p>","");
        /**
         * 将代金券信息 发送到用户手机上
         */

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("user","您对航诚商业的支持，您购买的线下代金券已经放到了您的商城账户，可以在北京首都机场三座航站楼内的航诚店面当现金使用，与店面活动优惠同享。您可以在“航诚商业-微商城-我的-线下代金券”中查看");
        jsonObject.put("user",replaceContent);

        try {
            AliSmsUtil.sendSms(phoneNumber,
                    AliSmsUtil.SmsConfig.values()[0], jsonObject
            );
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }


    }



    /**
     * 以同步的方式发送短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     * @return
     */
    public SmsResult notifySmsTemplateSync(String phoneNumber, NotifyType notifyType, String[] params) {
        if (smsSender == null)
            return null;

        int templateId = Integer.parseInt(getTemplateId(notifyType, smsTemplate));

        return smsSender.sendWithTemplate(phoneNumber, templateId, params);
    }



    /**
     * 微信模版消息通知,不跳转
     * <p>
     * 该方法会尝试从数据库获取缓存的FormId去发送消息
     *
     * @param touser     接收者openId
     * @param notifyType 通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params     通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    @Async
    public void notifyWxTemplate(String touser, NotifyType notifyType, String[] params) {
        if (wxTemplateSender == null)
            return;

        String templateId = getTemplateId(notifyType, wxTemplate);
        wxTemplateSender.sendWechatMsg(touser, templateId, params);
    }

    /**
     * 微信模版消息通知，带跳转
     * <p>
     * 该方法会尝试从数据库获取缓存的FormId去发送消息
     *
     * @param touser     接收者openId
     * @param notifyType 通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params     通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     * @param page       点击消息跳转的页面
     */
    @Async
    public void notifyWxTemplate(String touser, NotifyType notifyType, String[] params, String page) {
        if (wxTemplateSender == null)
            return;

        String templateId = getTemplateId(notifyType, wxTemplate);
        wxTemplateSender.sendWechatMsg(touser, templateId, params, page);
    }

    /**
     * 邮件消息通知,
     * 接收者在spring.mail.sendto中指定
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void notifyMail(String subject, String content) {
        if (mailSender == null)
            return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(sendTo);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private String getTemplateId(NotifyType notifyType, List<Map<String, String>> values) {
        for (Map<String, String> item : values) {
            String notifyTypeStr = notifyType.getType();

            if (item.get("name").equals(notifyTypeStr))
                return item.get("templateId");
        }
        return null;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void setSmsTemplate(List<Map<String, String>> smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public void setWxTemplateSender(WxTemplateSender wxTemplateSender) {
        this.wxTemplateSender = wxTemplateSender;
    }

    public void setWxTemplate(List<Map<String, String>> wxTemplate) {
        this.wxTemplate = wxTemplate;
    }
}
