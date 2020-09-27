package org.jinyuanjava.litemall.wx.web;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.config.AliPayConfig;
import org.jinyuanjava.litemall.core.express.dao.UserTicket;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.dto.OrderAllInOne;
import org.jinyuanjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;

@RestController
@RequestMapping("/wx/order")
@Validated
@Api(description = "微信前端/订单服务:/wx/order")
public class WxOrderController {
    private final Log logger = LogFactory.getLog(WxOrderController.class);

    @Autowired
    private WxOrderService wxOrderService;


    @Autowired
    private LitemallUserinfoDefService userinfoDefService;

    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private String returnUrl;
    @Autowired
    private String notifyUrl;
    @Autowired
    private LitemallSystemConfigService litemallSystemConfigService;
    @Autowired
    private LitemallOrderTicketsService litemallOrderTicketsService;

    @Autowired
    private LitemallUserChargeMoneyService userChargeMoneyService;

    @Autowired
    private LitemallUserChargeMoneyLockService litemallUserChargeMoneyLockService;

    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 订单信息
     * @param page     分页页数
     * @param limit     分页大小
     * @return 订单列表
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId,
                       @RequestParam(defaultValue = "0") Integer showType,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        return wxOrderService.list(userId, showType, page, limit);
    }


    /**
     * 在线订单各状态值 返回全部单、待付款、待发货、待收货、已完成、已取消 六种
     *
     */
    @GetMapping("onLinePayListCount")
    public Object getOnLinePayListCount(@LoginUser Integer userId) {
        JSONObject jsonObject= wxOrderService.getOnLinePayListCount(userId);
        return ResponseUtil.ok(jsonObject);
    }


    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer orderId) {
        return wxOrderService.detail(userId, orderId);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderSn 订单ID
     * @return 订单详情
     */
    @GetMapping("detailbysn")
    public Object detailBySn(@LoginUser Integer userId, @NotNull String orderSn) {
        return wxOrderService.detail(userId, orderSn);
    }

    /**
     * 提交订单
     * 为了保证将来字段扩展不会影响到接口的变化，将涉及到接口参数封装到一个结构里，以便将来扩展
     * @param userId 订单创建用户
     * @param orderStructData 提交订单的结构
     * @return
     */
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId, @RequestBody(required=false) OrderAllInOne orderStructData) {

        return wxOrderService.submit(userId, orderStructData);
    }

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
    /**
     * 取消订单
     * @param userId
     * @param orderId
     * @return
     */
    @PostMapping("cancel")
    public Object cancel(@LoginUser Integer userId, Integer orderId) {
        return wxOrderService.cancel(userId, orderId);
    }

    /**
     * 微信支付付款订单的预支付会话标识
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 支付订单ID
     */
    /**
     * 付款订单的预支付会话标识
     * @param userId
     * @param orderId
     * @param request
     * @param response
     * @return
     */
    @PostMapping("prepay")
    public Object prepay(@LoginUser Integer userId, Integer orderId,BigDecimal payMoney, HttpServletRequest request,HttpServletResponse response) {
        return wxOrderService.topay(userId, orderId,payMoney, request,response);
    }

    /**
     * 支付宝付款订单的预支付会话标识
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 支付订单ID
     * @throws IOException
     */
    /**
     * 付款订单的预支付会话标识 支付宝
     * @param userId
     * @param orderId
     * @param request
     * @param httpResponse
     * @throws IOException
     */
    @GetMapping("aliprepay")
    public void aliPrepay(Integer userId, Integer orderId,BigDecimal payMoney, HttpServletRequest request,HttpServletResponse httpResponse ) throws IOException {

       AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request

       alipayRequest.setNotifyUrl(notifyUrl);//在公共参数中设置回跳和通知地址

       //查询订单
       Map<String,Object> aliPrepay = (Map<String, Object>) wxOrderService.aliPrepay(userId, orderId,payMoney, request);
       if(aliPrepay!=null && aliPrepay.get("errno")!=null ){
    	   if((int)aliPrepay.get("errno")==0){
    		   alipayRequest.setBizContent(aliPrepay.get("data").toString());
    	   }
    	   LitemallOrder order = orderService.findById(orderId);
    	   alipayRequest.setReturnUrl(returnUrl);
       }else{

       }
//       alipayRequest.setBizContent("{" +
//   		"    \"out_trade_no\":\"20150320010101002\"," +
//   		"    \"total_amount\":88.88," +
//   		"    \"subject\":\"Iphone6 16G\"," +
//   		"    \"product_code\":\"QUICK_WAP_WAY\"" +
//   		"  }");//填充业务参数
       String form="";
       try {
		form = alipayClient.pageExecute(alipayRequest).getBody();
		System.out.println(form);
	} catch (AlipayApiException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //调用SDK生成表单
       httpResponse.setContentType("text/html;charset=utf-8");
       httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
       httpResponse.getWriter().flush();
//    	return null;
    }

    /**
     * 检验本地的订单是否已经在阿里平台上支付
     * @param outTradeNo
     * @return
     */
    @GetMapping("checkAlipay")
    public Object checkAlipay(String outTradeNo){
    	return wxOrderService.zfbQueryPayResult(outTradeNo);
    }

    /**
     * 微信付款成功或失败回调接口
     * <p>
     *  TODO
     *  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址
     *
     * @param request 请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @PostMapping("pay-notify")
    public Object payNotify(HttpServletRequest request, HttpServletResponse response) {
        return wxOrderService.payNotify(request, response);
    }

    /**
     * 支付宝付款成功或失败回调接口
     * <p>
     *  TODO
     *  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址
     *
     * @param request 请求内容
     * @param response 响应内容
     * @return 操作结果
     * @throws IOException
     */
    @GetMapping("ali-returnurl")
    public void retutnUrl(HttpServletRequest request, HttpServletResponse response,String out_trade_no,String total_amount ) throws IOException {
//    	 wxOrderService.checkAlipay(out_trade_no);
    	 response.sendRedirect(aliPayConfig.getJumpUrl()+"?price="+total_amount+"&orderId="+out_trade_no);
    }

    /**
     * 支付宝付款成功或失败回调接口
     * <p>
     *  TODO
     *  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址
     *
     * @param request 请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @PostMapping("alipay-notify")
    public Object alipayNotify(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("支付宝异步回调--------------------------------------------");
        Map<String, String> params = convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
        String paramsJson = JSON.toJSONString(params);
//        System.out.println("参数："+paramsJson);
        try {
        	System.out.println(aliPayConfig.getPublicKey());
            // 调用SDK验证签名
//            boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getPublicKey(), "utf-8", "RSA");
            boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getPublicKey(), "utf-8", "RSA2");
            System.out.println(signVerified);
//            if (signVerified) {
                logger.info("支付宝回调签名认证成功");
                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
//                this.check(params);
                // 另起线程处理业务

//                        AlipayNotifyParam param = buildAlipayNotifyParam(params);
                        String trade_status = params.get("trade_status");
                        System.out.println(trade_status);
                        // 支付成功
                        if (trade_status.equals("TRADE_SUCCESS")
                                || trade_status.equals("TRADE_FINISHED")) {
                            // 处理支付成功逻辑
                            try {
                            	String payId = params.get("trade_no");
                            	String ordersn = params.get("out_trade_no");
                            	wxOrderService.alipaySuccess(ordersn,payId);

                            } catch (Exception e) {
                                logger.error("支付宝回调业务处理报错,params:" + paramsJson, e);
                            }
                        } else {
//                            logger.error("没有处理支付宝回调业务，支付宝交易状态：{},params:{}",trade_status,paramsJson);
                        }


                // 如果签名验证正确，立即返回success，后续业务另起线程单独处理
                // 业务处理失败，可查看日志进行补偿，跟支付宝已经没多大关系。
                return "success";
//            } else {
//                return "failure";
//            }
        } catch (AlipayApiException e) {
        	e.printStackTrace();
//            logger.error("支付宝回调签名认证失败,paramsJson:{},errorMsg:{}", paramsJson, e.getMessage());
            return "failure";
        }
//        return wxOrderService.payNotify(request, response);
    }




    /**
     * 订单申请退款
     * @param userId
     * @param orderId
     * @return
     */
    @PostMapping("refund")
    public Object refund(@LoginUser Integer userId,  Integer orderId) {
        return wxOrderService.refund(userId, orderId);
    }



    /**
     * 确认收货
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    /**
     * 确认收货
     * @param userId
     * @param orderId
     * @return
     */
    @PostMapping("confirm")
    public Object confirm(@LoginUser Integer userId,  Integer orderId) {
        return wxOrderService.confirm(userId, orderId);
    }

    /**
     * 删除订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    /**
     * 删除订单
     * @param userId
     * @param orderId
     * @return
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, Integer orderId) {
        return wxOrderService.delete(userId, orderId);
    }

    /**
     * 待评价订单商品信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param goodsId 商品ID
     * @return 待评价订单商品信息
     */
    @GetMapping("goods")
    public Object goods(@LoginUser Integer userId,
                        @NotNull Integer orderId,
                        @NotNull Integer goodsId) {
        return wxOrderService.goods(userId, orderId, goodsId);
    }


    /**
     * 返回订单配置信息
     * @return
     */
    @GetMapping("getOrderSetList")
    public Object getOrderSetList() {
        Map<String, String>  returnMap= litemallSystemConfigService.listOrder();
        return ResponseUtil.ok(returnMap);
    }

    /**
     * 返回所有订单优惠券信息
     * @return
     */
    @GetMapping("getOrderTicketsList")
    public Object getOrderTicketsList(@LoginUser Integer userId) {
        List<LitemallOrderTickets> returnMap= litemallOrderTicketsService.getOrderTicketsByUserId(userId);
        return ResponseUtil.okList(returnMap);
    }

    /**
     * 评价订单商品
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("comment")
    public Object comment(@LoginUser Integer userId, @RequestBody String body) {
        return wxOrderService.comment(userId, body);
    }


    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<String, String>();

        Set<Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();

        for (Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;

            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }

    /**
     * 检查订单是否已经付过款
     *
     */
    @GetMapping("orderispay")
    public Object ordertest(String orderSn,HttpServletRequest request,HttpServletResponse response) {
        //处理微信支付平台查询结果
        String strHandleMessage="该订单未在支付平台查询到";
    	Boolean wxIfFind= wxOrderService.wxQueryPayResult(orderSn);
    	if(wxIfFind){
            strHandleMessage="该订单在微信平台已经支付";
            return ResponseUtil.ok(strHandleMessage);
        }
    	//todo 继续处理在支付宝平台上的查询结果
    	Boolean zfbIfFind= wxOrderService.zfbQueryPayResult(orderSn);
    	if(zfbIfFind){
            strHandleMessage="该订单在支付宝平台已经支付";
            return ResponseUtil.ok(strHandleMessage);
        }
    	//wxOrderService.zfbQueryPayResult(orderSn);
        //如果都不存在，则返回未查询到的提示
        return ResponseUtil.ok(strHandleMessage);
    }
    /**
     * 检查订单是否已经付过款
     *
     */
    @GetMapping("orderlisttest")
    public Object orderListTest(HttpServletRequest request,HttpServletResponse response) {
    	//检索出所有超时取消和待支付状态订单
        String handleNotGetPayId="";
    	List<LitemallOrder> allListWithoutPayed = wxOrderService.allListWithoutPayed();
    	if(allListWithoutPayed!=null && allListWithoutPayed.size()>0){
    		System.out.println("总订单："+allListWithoutPayed.size());
    		int i=0;
    		for (LitemallOrder litemallOrder : allListWithoutPayed) {
    			String orderSn = litemallOrder.getOrderSn();
    			try {
					Boolean wxIfFind = wxOrderService.wxQueryPayResult(orderSn);
					//如果已经在微信平台查询到，并且该订单的微信支付没有同步到，则将不一致信息返回到前台
                    if(wxIfFind){
                        if(litemallOrder.getPayMethod()==null||litemallOrder.getPayId()==null) {
                            handleNotGetPayId += "订单号:【" + litemallOrder + "】和微信平台支付信息不一致;";
                        }
                    } else
                    {
                        //todo 继续判断在支付宝平台上支付信息是否一致
                    	Boolean zfbIfFind = wxOrderService.zfbQueryPayResult(orderSn);
                    	if(zfbIfFind){
                    		if(litemallOrder.getPayMethod()==null||litemallOrder.getPayId()==null){
                    			 handleNotGetPayId += "订单号:【" + litemallOrder + "】和支付宝平台支付信息不一致;";
                    		}
                    	}
                    }
					i++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
    	logger.info("处理订单："+allListWithoutPayed.size());

    	//wxOrderService.zfbQueryPayResult(orderSn);
        return ResponseUtil.ok(handleNotGetPayId);
    }

    /**
     * 在创建订单之前查询出所有可用的优惠券,如果与第三方CRM接口对接，则需要更新这里的方法
     * @return
     */
    @GetMapping("getAvailableTicketsList")
    public Object getAvailableTicketsList(@LoginUser Integer userId, BigDecimal orderMoney) {
        List<UserTicket> availableTickets= wxOrderService.getOrderTicketsList(userId,orderMoney);
        if(availableTickets==null){
            return ResponseUtil.fail(502, "未能取到可用优惠券");
        } else {
            return ResponseUtil.okList(availableTickets);
        }
    }

    /**
     * 判断用户是否已经就某项拍卖提交过保证金，如果已经提交过，则允许用户出价，不再重复提交
     * @param userId
     * @param type
     * @param ruleMxId
     * @return
     */
    @GetMapping("ifUserChargeMoneyLock")
    public Object getIfUserChargeMoneyLock(@LoginUser Integer userId, String type,Integer ruleMxId) {
        String strType="";
        if(type.equals("dajia")){
            strType="大家拍";
        } else  if(type.equals("zhuanchang")){
            strType="专场拍";
        }
        LitemallUserChargeMoneyLock queryResult= litemallUserChargeMoneyLockService.queryByUserIdRuleMxIdType(userId,strType,ruleMxId);
        return ResponseUtil.ok(queryResult);
    }

}
