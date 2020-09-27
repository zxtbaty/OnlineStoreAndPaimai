package org.jinyuanjava.litemall.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jinyuanjava.litemall.admin.util.GetCurrentUser;
import org.jinyuanjava.litemall.admin.util.PDFUtil;
import org.jinyuanjava.litemall.core.notify.NotifyService;
import org.jinyuanjava.litemall.core.notify.NotifyType;
import org.jinyuanjava.litemall.core.util.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.*;

@Service

public class AdminOrderService {
    private final Log logger = LogFactory.getLog(AdminOrderService.class);

    @Autowired
    private Environment environment;

    @Autowired
    private AlipayUtil alipayUtil;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallOrderFapiaoService orderFapiaoService;

    @Autowired
    private LitemallOrderTicketsService orderTicketsService;

    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private LogHelper logHelper;

    @Autowired
    private StatService statService;

    @Autowired
    private LitemallOpadminInfoService opadminInfoService;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private LitemallUserChargeMoneyService userChargeMoneyService;




    @Autowired
    private LitemallPromotionSeckillOrderService promotionSeckillOrderService;
    @Autowired
    private LitemallGrouponOrderService grouponOrderService;
    @Autowired
    private LitemallPromotionGoodsUserOrderService promotionGoodsUserOrderService; //会员专区商品订单


    public Object list(Integer userId, String orderSn, String  typeCode,
                       String sourceCode,String orderType,
                       String consignee,
                       List<Short> orderStatusArray,
                       List<Short> yuyueStatusArray,
                       Boolean ifFapiao,String fapiaoStatus,
                       String sendWay,
                       String phone,
                       String beginDate,
                       String endDate,
                       List<Integer> pickSiteIdArray,
                       Integer payMethod,
                       List<Integer> orderIds,
                       Integer page, Integer limit, String sort) {
        List<LitemallOrder> orderList = orderService.querySelective(userId, orderSn,typeCode,sourceCode,
                orderType,consignee,orderStatusArray,yuyueStatusArray,ifFapiao,fapiaoStatus,sendWay,
                phone,beginDate,endDate,pickSiteIdArray,payMethod,orderIds,page, limit, sort);
        //该List中将订单状态码转成订单状态名称

        return ResponseUtil.okList(orderList);
    }

    public Object listDetail(Integer userId, String orderSn, String  typeCode,
                       String sourceCode,String orderType,
                       String consignee,
                       List<Short> orderStatusArray,
                       List<Short> yuyueStatusArray,
                       Boolean ifFapiao,String fapiaoStatus,
                       String sendWay,
                       String phone,
                       String beginDate,
                       String endDate,
                       List<Integer> pickSiteIdArray,
                       Integer payMethod,
                       List<Integer> orderIds,
                       Integer page, Integer limit, String sort) {
        List<ViewOrderGoods> orderList = orderService.querySelectiveMx(userId, orderSn,typeCode,sourceCode,
                orderType,consignee,orderStatusArray,yuyueStatusArray,ifFapiao,fapiaoStatus,sendWay,
                phone,beginDate,endDate,pickSiteIdArray,payMethod,orderIds,page, limit, sort);
        return ResponseUtil.okList(orderList);
    }

    public Object detail(Integer id) {
        LitemallOrder order = orderService.findById(id);
        List<LitemallOrderGoods> orderGoods = orderGoodsService.queryByOid(id);
        UserVo user = userService.findUserVoById(order.getUserId());
        List<LitemallOrderFapiao> orderFapiaos=orderFapiaoService.queryByOid(id);
        List<LitemallOrderTickets> orderTickets= orderTicketsService.queryByOid(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order",order);
        data.put("orderGoods", orderGoods);
        data.put("user", user);
        data.put("fapiao", orderFapiaos);
        data.put("coupon", orderTickets);

        return ResponseUtil.ok(data);
    }


    /**
     * 取消订单,对于支付金额为0的虚拟商品订单，在退单时只是退还相应的优惠券和积分 并且加回数量
     * <p>
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * 4. TODO 优惠券；
     * 5. TODO 团购活动订单。
     *

     * @return 取消订单操作结果
     */
    @Transactional
    public Object refund_nomoney(String body,Integer orderId) {

        String returnReason = JacksonUtil.parseString(body, "returnReason");
        String returnType = JacksonUtil.parseString(body, "returnType");
        String returnOp = JacksonUtil.parseString(body, "returnOp");
        String returnPartRemark=JacksonUtil.parseString(body, "returnPartRemark");

        if (orderId == null) {
            return ResponseUtil.fail(502,"订单Id不能为空");
        }
        LitemallOrder order = orderService.findById(orderId);

        // 设置订单退款确认状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        order.setOrderStatusName(OrderUtil.STATUS_REFUND_CONFIRM_NAME);
        order.setReturnReason(returnReason);
        order.setReturnOp(returnOp);
        order.setReturnType(returnType);
        order.setReturnPartRemark(returnPartRemark);
        order.setReturnPayTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }
        // 商品货品数量增加
        //更新库存
        Boolean result=  orderGoodsService.AddStorNum(order.getId(),order.getTypeCode(),null,null);
        if(!result){
            return ResponseUtil.fail(502,"后台出现错误");
        }
//        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
//        for (LitemallOrderGoods orderGoods : orderGoodsList) {
//            Integer productId = orderGoods.getProductId();
//            Short number = orderGoods.getNumber();
//            if (productService.addStock(productId, number) == 0) {
//                LitemallGoodsProduct goodsProduct=  productService.findById(productId);
//                goodsProduct.setRemainNumber(CharUtil.objectConverToInteger(goodsProduct.getNumber())-CharUtil.objectConverToInteger(goodsProduct.getRemainNumber()) );
//                productService.update(goodsProduct);
//                throw new RuntimeException("商品货品库存增加失败");
//            }
//        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});
        Map<String, Object> valueMap=new HashMap<>();
        valueMap.put("orderSn",order.getOrderSn());
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, valueMap);

        logHelper.logOrderSucceed("退款", "订单编号 " + orderId);


        return ResponseUtil.ok();
    }



    /**
     * 订单退款
     * <p>
     * 1. 检测当前订单是否能够退款;
     * 2. 微信退款操作;
     * 3. 设置订单退款确认状态；
     * 4. 订单商品库存回库。
     * <p>
     * TODO
     * 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤：
     * 1. 管理员登录微信官方支付平台点击退款操作进行退款
     * 2. 管理员登录litemall管理后台点击退款操作进行订单状态修改和商品库存回库
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    public Object refund_wx(String body) throws Exception {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        String returnReason = JacksonUtil.parseString(body, "returnReason");
        String returnType = JacksonUtil.parseString(body, "returnType");
        String returnOp = JacksonUtil.parseString(body, "returnOp");
        String returnPartRemark=JacksonUtil.parseString(body, "returnPartRemark");
        if (orderId == null) {
            return ResponseUtil.fail(502,"订单ID不能为空");
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.fail(502,"退货金额不能为空");
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.fail(502,"订单不存在");
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.fail(502,"订单支付金额和退款金额不一致");
        }

        // 如果订单不是退款状态，则不能退款 如果是线下沟通，则也可以不是用户申请时主动发起退款
        // 如果订单不是强制退款，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            if(order.getAdminAllowRefund()==null||order.getAdminAllowRefund()==false) {
                return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能退款");
            }
        }

        // 微信退款
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
        // 元转成分
        Integer totalFee = order.getActualPrice().multiply(new BigDecimal(100)).intValue();
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(totalFee);


        WxPayRefundResult wxPayRefundResult = null;
        try {
            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
        } catch (WxPayException e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }
        if (!wxPayRefundResult.getReturnCode().equals("SUCCESS")) {
            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }
        if (!wxPayRefundResult.getResultCode().equals("SUCCESS")) {
            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }


        // 设置订单取消状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        order.setOrderStatusName(OrderUtil.STATUS_REFUND_CONFIRM_NAME);
        order.setReturnReason(returnReason);
        order.setReturnOp(returnOp);
        order.setReturnType(returnType);
        order.setReturnPartRemark(returnPartRemark);
        order.setReturnPayTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 扯单时商品数量同步增加
        Boolean result=  orderGoodsService.AddStorNum(order.getId(),order.getTypeCode(),null,null);
        if(!result){
            return ResponseUtil.fail(502,"后台出现错误");
        }
//        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
//        for (LitemallOrderGoods orderGoods : orderGoodsList) {
//            Integer productId = orderGoods.getProductId();
//            Short number = orderGoods.getNumber();
//            if (productService.addStock(productId, number) == 0) {
//                LitemallGoodsProduct goodsProduct=  productService.findById(productId);
//                goodsProduct.setRemainNumber(CharUtil.objectConverToInteger(goodsProduct.getNumber())-CharUtil.objectConverToInteger(goodsProduct.getRemainNumber()) );
//                productService.update(goodsProduct);
//
//                throw new RuntimeException("商品货品库存增加失败");
//            }
//        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});
        Map<String, Object> valueMap=new HashMap<>();
        valueMap.put("orderSn",order.getOrderSn());
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, valueMap);

        logHelper.logOrderSucceed("退款", "订单编号 " + orderId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object refund_wx_charge(LitemallUser user,LitemallUserChargeMoney chargeMoney) throws Exception {

        // 微信退款
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(chargeMoney.getChargeSheetNo());
        wxPayRefundRequest.setOutRefundNo("refund_" + chargeMoney.getChargeSheetNo());
        // 元转成分
        Integer totalFee = chargeMoney.getChargeMoney().multiply(new BigDecimal(100)).intValue();
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(totalFee);


        WxPayRefundResult wxPayRefundResult = null;
        try {
            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
        } catch (WxPayException e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }
        if (!wxPayRefundResult.getReturnCode().equals("SUCCESS")) {
            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }
        if (!wxPayRefundResult.getResultCode().equals("SUCCESS")) {
            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }

        // 设置订单取消状态
        chargeMoney.setDealRefundOp(GetCurrentUser.getCurrentUser().getUsername());
        chargeMoney.setDealRefundTime(LocalDateTime.now());
        chargeMoney.setPayReturn(true);

        userChargeMoneyService.updateById(chargeMoney);

        //更新用户的储值卡余额

        user.setChargeReturnMoney(user.getChargeReturnMoney().add(chargeMoney.getChargeMoney()));
        user.setChargeRemainMoney(user.getChargeRemainMoney().subtract(chargeMoney.getChargeMoney()));

        userService.updateById(user);

        return ResponseUtil.ok();
    }


    /**
     * 订单支付宝退款
     * <p>
     * 1. 检测当前订单是否能够退款;
     * 2. 微信退款操作;
     * 3. 设置订单退款确认状态；
     * 4. 订单商品库存回库。
     * <p>
     * TODO
     * 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤：
     * 1. 管理员登录微信官方支付平台点击退款操作进行退款
     * 2. 管理员登录litemall管理后台点击退款操作进行订单状态修改和商品库存回库
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    public Object refund_alipay(String body) throws Exception {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        String returnReason = JacksonUtil.parseString(body, "returnReason");
        String returnType = JacksonUtil.parseString(body, "returnType");
        String returnOp = JacksonUtil.parseString(body, "returnOp");
        String returnPartRemark=JacksonUtil.parseString(body, "returnPartRemark");
        if (orderId == null) {
            return ResponseUtil.fail(502,"订单ID不能为空");
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.fail(502,"退货金额不能为空");
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.fail(502,"订单不存在");
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.fail(502,"订单支付金额和退款金额不一致");
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            if(order.getAdminAllowRefund()==null||order.getAdminAllowRefund()==false) {
                return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能退款");
            }
        }

        // 支付宝退款

        JSONObject orderJson=new JSONObject();//订单实体
        orderJson.put("out_trade_no",order.getOrderSn());
        orderJson.put("trade_no",order.getPayId());
        orderJson.put("refund_amount",refundMoney);
//        orderJson.put("refund_reason","商品退款");
//        orderJson.put("refund_currency","cny");
//        orderJson.put("org_pid","null");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        //request.putOtherTextParam("app_auth_token", appAuthToken);
//        request.setBizContent(orderJson.toString());

        AlipayRefund alipayRefund= new AlipayRefund();
        alipayRefund.setOut_trade_no(order.getOrderSn());//这个是商户的订单号
        alipayRefund.setTrade_no(order.getPayId());//这个是支付宝的订单号
        alipayRefund.setRefund_amount(refundMoney);//退款金额
//        alipayRefund.setRefund_reason("正常退款");//退款说明
//        alipayRefund.setOut_request_no("HZ01RF001");
//        alipayRefund.setOperator_id("OP001");
//        alipayRefund.setStore_id("NJ_S_001");
//        alipayRefund.setTerminal_id("NJ_T_001");//request.setBizContent(JSONUtil.simpleObjectToJsonStr(alipayRefund));
//        AlipayTradeRefundResponse response=null;
//        request.setBizContent(JSONObject.toJSONString(alipayRefund));//2个都可以，这个参数的顺序 不影响退款
//        try {
//            response = alipayClient.execute(request);
//            if (response.isSuccess()) {
//                System.out.println("调用成功");
//            } else {
//                System.out.println("调用失败");
//            }
//        } catch (Exception ex){
////            if(response.isSuccess()==true){
////
////            } else {
//                ex.printStackTrace();
//                return ResponseUtil.fail(ORDER_REFUND_FAILED, "支付宝退款失败");
////            }
//        }


//        AlipayRefundInfo alidata= new AlipayRefundInfo();

        try {
            AlipayTradeRefundResponse refundResponse= alipayUtil.refund("",orderJson.toJSONString());
//            AlipayUtil alipayUtil = new AlipayUtil();
            //AlipayTradeRefundResponse refundResponse= alipayUtil.refund("",orderJson.toJSONString());
           if (refundResponse.isSuccess()) {
                System.out.println("调用成功");
            } else {
               return ResponseUtil.fail(502, "支付宝退款失败");
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "支付宝退款失败");
        }

        // 设置订单退款确认状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        order.setOrderStatusName(OrderUtil.STATUS_REFUND_CONFIRM_NAME);
        order.setReturnReason(returnReason);
        order.setReturnOp(returnOp);
        order.setReturnType(returnType);
        order.setReturnPartRemark(returnPartRemark);
        order.setReturnPayTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        // 扯单时商品数量同步增加
        Boolean result=  orderGoodsService.AddStorNum(order.getId(),order.getTypeCode(),null,null);
        if(!result){
            return ResponseUtil.fail(502,"后台出现错误");
        }
//        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
//        for (LitemallOrderGoods orderGoods : orderGoodsList) {
//            Integer productId = orderGoods.getProductId();
//            Short number = orderGoods.getNumber();
//            if (productService.addStock(productId, number) == 0) {
//                LitemallGoodsProduct goodsProduct=  productService.findById(productId);
//                goodsProduct.setRemainNumber(CharUtil.objectConverToInteger(goodsProduct.getNumber())-CharUtil.objectConverToInteger(goodsProduct.getRemainNumber()) );
//                productService.update(goodsProduct);
//                throw new RuntimeException("商品货品库存增加失败");
//            }
//        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});
        Map<String, Object> valueMap=new HashMap<>();
        valueMap.put("orderSn",order.getOrderSn());
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, valueMap);

        logHelper.logOrderSucceed("退款", "订单编号 " + orderId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object refund_alipay_charge(LitemallUser user,LitemallUserChargeMoney chargeMoney) throws Exception {

        // 支付宝退款
        JSONObject orderJson=new JSONObject();//订单实体
        orderJson.put("out_trade_no",chargeMoney.getChargeSheetNo());
        orderJson.put("trade_no",chargeMoney.getPayNo());
        orderJson.put("refund_amount",chargeMoney.getChargeMoney());


        try {
            AlipayTradeRefundResponse refundResponse= alipayUtil.refund("",orderJson.toJSONString());
            if (refundResponse.isSuccess()) {
                System.out.println("调用成功");
            } else {
                return ResponseUtil.fail(502, "支付宝退款失败");
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "支付宝退款失败");
        }

        // 设置订单取消状态
        chargeMoney.setDealRefundOp(GetCurrentUser.getCurrentUser().getUsername());
        chargeMoney.setDealRefundTime(LocalDateTime.now());
        chargeMoney.setPayReturn(true);

        userChargeMoneyService.updateById(chargeMoney);

        //更新用户的储值卡余额

        user.setChargeReturnMoney(user.getChargeReturnMoney().add(chargeMoney.getChargeMoney()));
        user.setChargeRemainMoney(user.getChargeRemainMoney().subtract(chargeMoney.getChargeMoney()));

        userService.updateById(user);

//        //TODO 发送邮件和短信通知，这里采用异步发送
//        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
//        // 注意订单号只发后6位
////        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});
//        Map<String, Object> valueMap=new HashMap<>();
//        valueMap.put("orderSn",order.getOrderSn());
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, valueMap);

        return ResponseUtil.ok();
    }


    /**
     * 预约单备货
     * 1. 检测当前订单是否已经备货
     * 2. 设置订单备货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object yuYueBeiHuo(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");

        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 设置预约单的为待取货
       Short statusFetch=101;
       order.setYuyueStatusCode(statusFetch);
       order.setYuyueStatusName("待取货");

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        return ResponseUtil.ok();
    }


    /**
     * 备货
     * 1. 检测当前订单是否已经备货
     * 2. 设置订单备货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object beihuo(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");

        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 如果订单不是已支付状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单已经支付才能确认备货");
        }

        order.setOrderStatus(OrderUtil.STATUS_BEIHUO);
        order.setOrderStatusName(OrderUtil.STATUS_BEIHUO_NAME);
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户:          *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
        //notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});

        //TODO 这里发邮件或发站内消息给相应的操作人员
        LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
        opadminInfo.setTypeName("备货完成");
        opadminInfo.setTitle("备货完成");
        opadminInfo.setSourceCode(order.getSourceCode());
        opadminInfo.setSourceName(order.getSourceName());
        Map<String,Object> parameters=new HashMap<>();
        parameters.put("orderSn",order.getOrderSn());
        parameters.put("orderAddress",order.getAddress());
        opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

        logHelper.logOrderSucceed("备货", "订单编号 " + orderId);
        return ResponseUtil.ok();
    }

    /**
     * 批量备货
     * 1. 检测当前订单是否已经备货
     * 2. 设置订单备货状态
     *
     * @param orderIds 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object beihuoBatchConfirm(Integer[] orderIds) {

        if (orderIds == null) {
            return ResponseUtil.fail(502,"要备货的订单ID不能为空");
        }

        List<LitemallOrder> orders = orderService.getOrderListByIds(orderIds);

        for(LitemallOrder order:orders){
            if(order.getOrderStatus().equals(OrderUtil.STATUS_PAY)){
                order.setOrderStatus(OrderUtil.STATUS_BEIHUO);
                order.setOrderStatusName(OrderUtil.STATUS_BEIHUO_NAME);
                if (orderService.updateWithOptimisticLocker(order) == 0) {
                    return ResponseUtil.updatedDateExpired();
                } else
                {
                    //TODO 这里发邮件或发站内消息给相应的操作人员
                    LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
                    opadminInfo.setTypeName("备货完成");
                    opadminInfo.setTitle("备货完成");
                    opadminInfo.setSourceCode(order.getSourceCode());
                    opadminInfo.setSourceName(order.getSourceName());
                    Map<String,Object> parameters=new HashMap<>();
                    parameters.put("orderSn",order.getOrderSn());
                    parameters.put("orderAddress",order.getAddress());
                    opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);
                    logHelper.logOrderSucceed("备货", "订单编号 " + order.getId());
                }
            }
        }
        return ResponseUtil.ok();
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object ship(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null || shipSn == null || shipChannel == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 如果订单不是已备货状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_BEIHUO)) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不是已备货状态，不能发货");
        } else
        {
            order.setShipSn(shipSn);
            order.setShipChannel(shipChannel);
            orderService.update(order);
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setOrderStatusName(OrderUtil.STATUS_SHIP_NAME);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户:          *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});
        Map<String, Object> valueMap=new HashMap<>();
        valueMap.put("orderSn",order.getOrderSn());
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, valueMap);

        logHelper.logOrderSucceed("发货", "订单编号 " + orderId);
        return ResponseUtil.ok();
    }


    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object reply(String body) {
        Integer commentId = JacksonUtil.parseInteger(body, "commentId");
        if (commentId == null || commentId == 0) {
            return ResponseUtil.badArgument();
        }
        // 目前只支持回复一次
        if (commentService.findById(commentId) != null) {
            return ResponseUtil.fail(ORDER_REPLY_EXIST, "订单商品已回复！");
        }
        String content = JacksonUtil.parseString(body, "content");
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        // 创建评价回复
        LitemallComment comment = new LitemallComment();
        comment.setType((byte) 2);
        comment.setValueId(commentId);
        comment.setContent(content);
        comment.setUserId(0);                 // 评价回复没有用
        comment.setStar((short) 0);           // 评价回复没有用
        comment.setHasPicture(false);        // 评价回复没有用
        comment.setPicUrls(new String[]{});  // 评价回复没有用
        commentService.save(comment);

        return ResponseUtil.ok();
    }



    public Object exportPDF(List<Integer> orderIds, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //文件名称
        String name = DateUtil.getDate();
//        response.reset();// 清空输出流
//        response.setHeader("Location", name);
//        response.setContentType("text/plain");
//        response.setContentType("application/octet-stream;charset=UTF-8");
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8") + ".pdf");

        response.setContentType("application/pdf;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8") + ".pdf");

        //查询已选择订单数据

//        Document document=new Document();

        ArrayList<List<Map>> arrayList=new ArrayList();
        for(Integer orderId:orderIds) {
            List<Map> list = statService.statGoodsOrderPDF(orderId);
            arrayList.add(list);
        }
        if(arrayList.size()>0) {
            PDFUtil.exportPdf(request, response, arrayList);
        }

//        Map objects = list.get(0);


        return ResponseUtil.ok(response);
    }






    /**
     * 导出选择的待发货订单
     * @param response
     * @param orderIds
     * @throws Exception
     */
    public void exportExcel( HttpServletResponse response, List<Integer> orderIds,List result) throws Exception {

        //查询已选择订单数据
        List<Map> list=result;
        if(!StringUtils.isEmpty(orderIds)){
            list=statService.statGoodsOrderSumAmount(orderIds);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        //样式
        HSSFCellStyle style = createStyle(workbook, "");
        HSSFCellStyle topStyle = createStyle(workbook, "top");
        HSSFCellStyle titleStyle = createStyle(workbook, "title");
        HSSFCellStyle rightStyle = createStyle(workbook, "right");
        HSSFCellStyle leftStyle = createStyle(workbook, "left");
        // sheet页
        String[] sheetName = {"店面", "电商"};
        for (int s = 0; s < sheetName.length; s++) {
            HSSFSheet sheet = workbook.createSheet(sheetName[s]);
            HSSFRow row = null;
            HSSFCell cell = null;

            //excel头部名称
            row = sheet.createRow(0);
            row.setHeightInPoints(20);
            createCell(row, cell, 10, new String[]{"商品外借单-电商专用 (" + sheetName[s] + "留存联）"}, topStyle);
            //合并单元格：参数说明：1：开始行 2：结束行  3：开始列 4：结束列
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            //头部附加信息
            row = sheet.createRow(1);
            row.setHeightInPoints(26);
            String currentYear = Calendar.getInstance().get(Calendar.YEAR) + "年      月      日";
            String[] subjoinData = {"", "时间:", currentYear, "外借店面:\n(店面编号&名称)", "", "", "", "编码:"};
            createCell(row, cell, 10, subjoinData, style);
            row.getCell(1).setCellStyle(rightStyle);
            row.getCell(2).setCellStyle(leftStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 6));

            //列名
            row = sheet.createRow(2);
            String[] titles = {"序号", "商品编码", "商品名称","规格型号", "单价(元)", "备注", "外借数量", "电商订单编号", "取货人员签字", "店面交接人员签字"};
            createCell(row, cell, 10, titles, titleStyle);

//            //默认内容 购物袋
//            row = sheet.createRow(3);
//            String[] defaultData = {"1", "9999980010", "航诚购物袋", "1.5"};
//            createCell(row, cell, 9, defaultData, style);

            //行数
            Integer rowIndex = 3;
            //总数量
            Integer total = 0;
            //临时变量,用于判断数据是否属于同一品牌
//            String brand = "";
            //临时变量,商品编码是否一致
//            String goodCode = "";
            //临时变量,商品编码合拼开始行、结束行
//            Integer beginRow = 0, endRow = 0;
            for (int i = 0; i < list.size(); i++, rowIndex++) {
                Map obj = list.get(i);
                //Object[] obj = list.get(i);
                total += Integer.parseInt(obj.get("amount").toString());
                // 品牌是否一致
//                if (!brand.equals(obj[1].toString())) {
//                    brand = obj[1].toString();
////                    goodCode = "";  //  防止品牌不一致,商品一致
//                    //品牌
//                    row = sheet.createRow(rowIndex);
//                    createCell(row, cell, 7, new String[]{obj[2].toString()}, titleStyle);
//                    //补全8、9列无边框
//                    row.createCell(7).setCellStyle(style);
//                    row.createCell(8).setCellStyle(style);
//                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 6));
//                    rowIndex++;
//                }
                //商品明细
                row = sheet.createRow(rowIndex);
                String[] data = {String.valueOf(i + 1), obj.get("code").toString(), obj.get("name").toString(),
                        obj.get("specifications").toString(),obj.get("price").toString() ,"",
                        obj.get("amount").toString(), obj.get("orderno").toString()};
                createCell(row, cell, 10, data, style);

                //商品编码是否一致
//                if (!goodCode.equals(obj[0].toString())) {
//                    //合并一致行数据
//                    if (beginRow != 0 && endRow != 0 && beginRow != endRow) {
//                        sheet.addMergedRegion(new CellRangeAddress(beginRow, endRow, 1, 1));
//                        endRow = 0;
//                    }
//                    goodCode = obj[0].toString();
//                    beginRow = rowIndex;
//                } else {
//                    endRow = rowIndex;
//                    //数据最后一行与之前数据一致合并
//                    if (i == list.size() - 1) {
//                        if (beginRow != 0 && endRow != 0 && beginRow != endRow) {
//                            sheet.addMergedRegion(new CellRangeAddress(beginRow, endRow, 1, 1));
//                        }
//                    }
//                }
            }
            //数量合计
            row = sheet.createRow(rowIndex);
            String[] data = {"数量合计:", "", "", "", "","", String.valueOf(total)};
            createCell(row, cell, 10, data, rightStyle);
            row.getCell(5).setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 4));

            //人员签字列合并
            sheet.addMergedRegion(new CellRangeAddress(3, rowIndex, 8, 8));
            sheet.addMergedRegion(new CellRangeAddress(3, rowIndex, 9, 9));

            //设置列宽度
            setColumnWidth(sheet);
        }
        String exportFileName = "test.xlsx";
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + exportFileName);
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //将excel写入到输出流中
            workbook.write(os);
            os.flush();
            os.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

//
//        try {
//            response.setHeader("Content-Disposition", "attachment;filename="+
//                    java.net.URLEncoder.encode(exportFileName,"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


//        response.reset();// 清空输出流
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Location", exportFileName);
//        response.setContentType("text/plain");
//        response.setContentType("application/octet-stream;charset=UTF-8");
//        response.setContentType("application/ms-excel");
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(exportFileName, "UTF-8") + ".xls");

//
//        workbook.write(response.getOutputStream());

//        // 将文件输出到客户端浏览器
//        ServletOutputStream out = response.getOutputStream();
//        try {
//            workbook.write(out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 创建单元格
     *
     * @param row
     * @param cell
     * @param cellNum
     * @param cellData
     * @param headStyle
     */
    private void createCell(HSSFRow row, HSSFCell cell, int cellNum, String[] cellData, HSSFCellStyle headStyle) {
        for (int i = 0; i < cellNum; i++) {
            cell = row.createCell(i);
            if (i < cellData.length) {
                cell.setCellValue(cellData[i]);
            } else {
                cell.setCellValue("");
            }
            cell.setCellStyle(headStyle);
        }
    }

    /**
     * 样式
     *
     * @param workbook
     * @param type
     * @return
     */
    private HSSFCellStyle createStyle(HSSFWorkbook workbook, String type) {
        //字体
        HSSFFont headFont = workbook.createFont();
        headFont.setFontName("微软雅黑");
        //样式
        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle = workbook.createCellStyle();

        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //水平居中
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headStyle.setWrapText(true);
        headStyle.setFont(headFont);
        // 设置为文本类型
        HSSFDataFormat format = workbook.createDataFormat();
        headStyle.setDataFormat(format.getFormat("@"));

        if ("top".equals(type)) {
            headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headFont.setFontHeightInPoints((short) 14);
        } else if ("title".equals(type)) {
            headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headFont.setFontHeightInPoints((short) 11);
            //设置背景色
            headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        } else if ("right".equals(type)) {
            headStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        } else if ("left".equals(type)) {
            headStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        }

        return headStyle;
    }

    /**
     * 设置指定列宽度
     *
     * @param sheet
     */
    private void setColumnWidth(HSSFSheet sheet) {

        sheet.setColumnWidth(0, 256 * 4 + 184);
        sheet.setColumnWidth(1, 256 * 11 + 184);
        sheet.setColumnWidth(2, 256 * 30 + 184);
        sheet.setColumnWidth(3, 256 * 8 + 184);
        sheet.setColumnWidth(6, 256 * 20 + 184);
    }




}
