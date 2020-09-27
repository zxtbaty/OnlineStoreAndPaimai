package org.jinyuanjava.litemall.admin.web;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.yichi.alipay.pay.util.AlipayConfig;
import com.yichi.weixin.pay.inter.WeixinPayResultQuery;
import com.yichi.weixin.pay.util.PayCommonUtil;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.service.AdminOrderService;
import org.jinyuanjava.litemall.admin.util.MapConvertCamel;
import org.jinyuanjava.litemall.core.config.AliPayConfig;
import org.jinyuanjava.litemall.core.config.AlipaySonResultQuery;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@CrossOrigin
@RestController
@RequestMapping("/admin/order")
@Validated
@Api(description = "后台管理/商场管理/线上订单:/admin/order")
public class AdminOrderController {
    private final Log logger = LogFactory.getLog(AdminOrderController.class);

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallOrderFapiaoService orderFapiaoService;

    @Autowired
    private CommonDBService commonDBService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private WxMaConfig wxMaConfig;

    @Autowired
    private AliPayConfig alipayConfig;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallPromotionSeckillOrderService promotionSeckillOrderService;

    @Autowired
    private LitemallPromotionSeckillRuleService promotionSeckillRuleService;

    @Autowired
    private LitemallAuctionDajiaOfferCurrentService dajiaOfferCurrentService; //大家拍出价
    @Autowired
    private LitemallAuctionDajiaRuleCurrentService dajiaRuleCurrentService; //大家拍规则

    @Autowired
    private LitemallAuctionZhuanchangOfferCurrentService zhuanchangOfferCurrentService; //专场拍出价
    @Autowired
    private LitemallAuctionZhuanchangGoodsCurrentService zhuanchangGoodsCurrentService; //专场拍商品拍卖规则
    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallUserChargeMoneyService userChargeMoneyService;


    /**
     * 查询订单 此方法废弃，采用以下的sql语句构建方式
     *
     * @param userId
     * @param orderSn
     * @param orderStatusArray
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    @RequiresPermissions("admin:order:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer userId, String orderSn,
                       String typeCode,String sourceCode,
                       String orderType,String consignee,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(required = false) List<Short> yuyueStatusArray,
                       Boolean ifFapiao,String fapiaoStatus,
                       String sendWay,
                       String phone,
                       String beginDate,
                       String endDate,
                       @RequestParam(required = false) List<Integer> pickSiteIdArray,
                       Integer payMethod,
                       @RequestParam(required = false) List<Integer> orderIds,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return adminOrderService.list(userId, orderSn,typeCode,sourceCode,orderType,consignee,orderStatusArray,
                yuyueStatusArray,ifFapiao,fapiaoStatus,sendWay,phone,beginDate,endDate,pickSiteIdArray,
                payMethod,orderIds, page, limit, sort);
    }

    private String getCondition(Integer userId, String orderSn,
       String typeCode,String sourceCode,String orderType,String consignee,
       List<Short> orderStatusArray,List<Short> yuyueStatusArray,
       Boolean ifFapiao,String fapiaoStatus,String sendWay,
       String phone, String beginDate,String endDate,
       List<Integer> pickSiteIdArray,Integer payMethod,
       List<Integer> orderIds){
        String strCondition="";
        if(!StringUtils.isEmpty(userId)){
            strCondition+=" and user_id='"+userId+"'";
        }
        if(!StringUtils.isEmpty(orderSn)){
            strCondition+=" and order_sn='"+orderSn+"'";
        }
        if(!StringUtils.isEmpty(typeCode)){
            strCondition+=" and type_code='"+typeCode+"'";
        }

        if(!StringUtils.isEmpty(sourceCode)){
            strCondition+=" and source_code='"+sourceCode+"'";
        }
        if(!StringUtils.isEmpty(orderType)){
            if (orderType.equals("电商")) {
                strCondition+=" and type_code='10'";
            } else {
                strCondition+=" and type_code<>'10'";
            }
        }
        if(!StringUtils.isEmpty(consignee)){
            strCondition+=" and consignee='"+consignee+"'";
        }
        if(!StringUtils.isEmpty(orderStatusArray)){
            if(orderStatusArray.size()==1&&orderStatusArray.indexOf(Short.parseShort("202"))>=0){
                strCondition+=" and (order_status in ("+commonDBService.listToShort(orderStatusArray)
                        +") or (order_status<>203 and admin_allow_refund=1))";
            } else {
                strCondition += " and order_status in (" + commonDBService.listToShort(orderStatusArray) + ")";
            }
        }
        if(!StringUtils.isEmpty(yuyueStatusArray)){
            strCondition+=" and yuyue_status_code in ("+commonDBService.listToShort(yuyueStatusArray)+")";
        }

        if(!StringUtils.isEmpty(orderIds)){
            strCondition+=" and order_id in ("+commonDBService.listToInteger(orderIds)+")";
        }

        if(!StringUtils.isEmpty(ifFapiao)){
            Integer booValue=0;
            if(ifFapiao){
                booValue=1;
            }
            strCondition+=" and if_fapiao='"+booValue+"'";
        }
        if(!StringUtils.isEmpty(fapiaoStatus)){

            strCondition+=" and fapiao_status='"+fapiaoStatus+"'";
        }
        if(!StringUtils.isEmpty(sendWay)){
            strCondition+=" and send_way='"+sendWay+"'";
        }
        if(!StringUtils.isEmpty(payMethod)){
            strCondition+=" and pay_method='"+payMethod+"'";
        }
        if(!StringUtils.isEmpty(phone)){
            strCondition+=" and (user_phone='"+phone+"' or mobile='"+phone+"')";
        }
        if(!StringUtils.isEmpty(beginDate)){
            strCondition+=" and DATE_FORMAT(add_time,'%Y-%m-%d %H:%m:%s')>='"+beginDate+"'";
        }
        if(!StringUtils.isEmpty(endDate)){
            strCondition+=" and DATE_FORMAT(add_time,'%Y-%m-%d %H:%m:%s')<='"+endDate+"'";
        }
        if(!StringUtils.isEmpty(pickSiteIdArray)){
            strCondition+=" and send_way='自提取货' and pick_site_id in ("+commonDBService.listToInteger(pickSiteIdArray)+")";
        }
        return strCondition;
    }

    @GetMapping("/listBySql")
    public Object listBySql(Integer userId, String orderSn,
                       String typeCode,String sourceCode,
                       String orderType,String consignee,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(required = false) List<Short> yuyueStatusArray,
                       Boolean ifFapiao,String fapiaoStatus,
                       String sendWay,
                       String phone,
                       String beginDate,
                       String endDate,
                       @RequestParam(required = false) List<Integer> pickSiteIdArray,
                       Integer payMethod,
                       @RequestParam(required = false) List<Integer> orderIds,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
    ) {
        String strCondition= getCondition(userId, orderSn, typeCode,sourceCode, orderType,consignee,
               orderStatusArray,yuyueStatusArray, ifFapiao,fapiaoStatus,
               sendWay, phone, beginDate, endDate, pickSiteIdArray, payMethod, orderIds);
        String strSql=" SELECT sql_calc_found_rows * " +
                "  FROM  litemall_order " +
                "  where deleted=0 "+strCondition;
        if(!StringUtils.isEmpty(sort)){
            strSql+=" order by "+sort;
        }

        //构造分页
        //如果传入的参数是999999,则表示不分页
        if(limit!=null&&limit<999999) {
            int fromIndex = (page-1) * limit;
//                int toIndex = fromIndex + limit;
            strSql+=  " limit " + fromIndex + "," + limit;
        }
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);

        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        Integer count=(Integer) param.get("total");

        List<Map<String, Object>> convertResult=new ArrayList<>();
        //转驼峰命名法
        for(Map<String, Object> row:result) {
            Map<String, Object> mapRow=  MapConvertCamel.toReplaceKeyLow(row);
            convertResult.add(mapRow);
        }
        return ResponseUtil.okList(convertResult,count,page,limit);

    }

    @RequiresPermissions("admin:order:listDetail")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "查询明细")
    @GetMapping("/listDetail")
    public Object listDetail(Integer userId, String orderSn,
                       String typeCode,String sourceCode,
                       String orderType,String consignee,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(required = false) List<Short> yuyueStatusArray,
                       Boolean ifFapiao,String fapiaoStatus,
                       String sendWay,
                       String phone,
                       String beginDate,
                       String endDate,
                       @RequestParam(required = false) List<Integer> pickSiteIdArray,
                       Integer payMethod,
                       @RequestParam(required = false) List<Integer> orderIds,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
    ) {
//        return adminOrderService.listDetail(userId, orderSn,typeCode,sourceCode,orderType,consignee,orderStatusArray,
//                yuyueStatusArray,ifFapiao,fapiaoStatus,sendWay,phone,beginDate,endDate,pickSiteIdArray,
//                payMethod,orderIds,page, limit, sort);

        String strCondition= getCondition(userId, orderSn, typeCode,sourceCode, orderType,consignee,
                orderStatusArray,yuyueStatusArray, ifFapiao,fapiaoStatus,
                sendWay, phone, beginDate, endDate, pickSiteIdArray, payMethod, orderIds);
        String strSql=" SELECT sql_calc_found_rows * " +
                "  FROM  view_order_goods " +
                "  where deleted=0 "+strCondition;
        if(!StringUtils.isEmpty(sort)){
            strSql+=" order by "+sort;
        }
        //构造分页
        //如果传入的参数是999999,则表示不分页
        if(limit!=null&&limit<999999) {
            int fromIndex = (page-1) * limit;
//                int toIndex = fromIndex + limit;
            strSql+=  " limit " + fromIndex + "," + limit;
        }
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);

        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        Integer count=(Integer) param.get("total");

        List<Map<String, Object>> convertResult=new ArrayList<>();
        //转驼峰命名法
        for(Map<String, Object> row:result) {
            Map<String, Object> mapRow=  MapConvertCamel.toReplaceKeyLow(row);
            convertResult.add(mapRow);
        }
        return ResponseUtil.okList(convertResult,count,page,limit);

    }


    @GetMapping("/updateOrderDealRemark")
    public Object updateOrderDealRemark(String orderId, String dealRemark) {
        String strSql="update litemall_order set deal_remark='"+dealRemark+"' where id='"+orderId+"'";
        commonDBService.procedureExec(strSql);
        return ResponseUtil.ok();
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:order:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminOrderService.detail(id);
    }

    /**
     * 设置订单可强制退款
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @RequiresPermissions("admin:order:setRefundStatus")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "订单退款状态")
    @PostMapping("/setRefundStatus")
    public Object setRefundStatus(@RequestBody String body) {
        try {
            Integer orderId = JacksonUtil.parseInteger(body, "orderId");
            LitemallOrder order= orderService.findById(orderId);
            Object result=null;
            //设置订单状态允许客户退款
            order.setAdminAllowRefund(true);
            orderService.update(order);
            return ResponseUtil.ok();

        } catch (Exception ex){
            return ResponseUtil.fail(502,"订单更新状态错误");
        }
    }

    /**
     * 修改订单开票状态
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单修改开票状态结果
     */
    @RequiresPermissions("admin:order:updateFapiaoStatus")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "修改开票状态")
    @PostMapping("/updateFapiaoStatus")
    public Object updateFapiaoStatus(@RequestBody String body) {
        try {
            Integer orderId = JacksonUtil.parseInteger(body, "orderId");
            LitemallOrder order= orderService.findById(orderId);
            Object result=null;
            //设置订单发票状态为已开
            order.setFapiaoStatus("已开");
            orderService.update(order);

            return ResponseUtil.ok();

        } catch (Exception ex){
            return ResponseUtil.fail(502,"修改开票状态错误");
        }
    }

    /**
     *获取订单发票信息
     *
     * @param orderId
     * @return
     */
    @RequiresPermissions("admin:order:getFapiaoDetail")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "获取订单发票")
    @GetMapping("/getFapiaoDetail")
    public Object getFapiaoDetail(@NotNull Integer orderId) {
        List<LitemallOrderFapiao> orderFapiaos= orderFapiaoService.queryByOid(orderId);
        if(orderFapiaos!=null&&orderFapiaos.size()>0){
            return ResponseUtil.ok(orderFapiaos.get(0));
        } else
        {
            return ResponseUtil.ok(null);
        }
    }

    /**
     * 订单退款
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @RequiresPermissions("admin:order:refund")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "订单退款")
    @PostMapping("/refund")
    public Object refund(@RequestBody String body) {
        try {
            Integer orderId = JacksonUtil.parseInteger(body, "orderId");
            LitemallOrder order= orderService.findById(orderId);
            Object result=null;
            //如果订单是微信支付，则调用微信退款
            if(order.getPayMethod()==2)
            {
                result=adminOrderService.refund_wx(body);
            } else  if(order.getPayMethod()==1){
                //如果订单是支付宝支付，则调用支付宝退款
                result=adminOrderService.refund_alipay(body);
            } else
            {
                //如果用积分支付，则只需要将积分/优惠券退还,金额无需退还
                result=adminOrderService.refund_nomoney(body,orderId);
            }

            return result;
        } catch (Exception ex){
            return ResponseUtil.fail(502,"退款发生错误");
        }
    }


    /**
     * 充值订单退款
     *
     * @param chargeId 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @RequiresPermissions("admin:order:refundCharge")
    @RequiresPermissionsDesc(menu = {"拍卖管理", "充值记录"}, button = "充值退款")
    @GetMapping("/refundCharge")
    public Object refundCharge(Integer chargeId) {
        try {
            LitemallUserChargeMoney userChargeMoney= userChargeMoneyService.findById(chargeId);
            if(userChargeMoney==null){
                return ResponseUtil.badArgumentValue();
            }
            if(userChargeMoney.getRefundStatus().equals("已处理")){
                return ResponseUtil.fail(502,"充值退款已经处理完毕");
            }
            LitemallUser user=userService.findById(userChargeMoney.getUserId());
            if(userChargeMoney.getChargeMoney().compareTo(user.getChargeRemainMoney())==-1){
                return ResponseUtil.fail(502,"有资金正在占用，余额不足");
            }

            Object result=null;
            //如果订单是微信支付，则调用微信退款
            if(userChargeMoney.getPayMethod().equals("2"))
            {
                result=adminOrderService.refund_wx_charge(user,userChargeMoney);
            } else  if(userChargeMoney.getPayMethod().equals("1")){
                //如果订单是支付宝支付，则调用支付宝退款
                result=adminOrderService.refund_alipay_charge(user,userChargeMoney);
            }
//            else
//            {
//                //如果用积分支付，则只需要将积分/优惠券退还,金额无需退还
//                result=adminOrderService.refund_nomoney(body,orderId);
//            }

//            //累计退款金额，重新计算可用余额
//            Integer userId= chargeMoney.getUserId();
//            LitemallUser litemallUser= litemallUserService.findById(userId);
//            litemallUser.setChargeReturnMoney(litemallUser.getChargeReturnMoney().add(chargeMoney.getChargeMoney()));
//            litemallUser.setChargeRemainMoney(
//                    litemallUser.getChargeSumMoney()
//                            .subtract(litemallUser.getChargeReturnMoney())
//                            .subtract(litemallUser.getChargeLockMoney())
//                            .subtract(litemallUser.getChargeLockToOrderMoney())
//            );
////        //此时申请退款，不能重新计算
////        //更新用户表的锁定金额转订单金额，这部分金额将不允许发起退款
////        litemallUser.setChargeLockToOrderMoney(litemallUser.getChargeLockToOrderMoney().add(chargeMoney.getChargeMoney()));
////        //同时减少押金金额
////        litemallUser.setChargeLockMoney(litemallUser.getChargeLockMoney().subtract(chargeMoney.getChargeMoney()));
//
//            litemallUserService.updateById(litemallUser);

            return result;
        } catch (Exception ex){
            return ResponseUtil.fail(502,"退款发生错误");
        }
    }

    /**
     * 驳回用户申请退款申请
     * @param orderId
     * @return
     */
    @RequiresPermissions("admin:order:notAllowRefund")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "驳回用户订单退款")
    @GetMapping("/notAllowRefund")
    public Object notAllowRefund(@NotNull Integer orderId) {
        try {
            //判断订单状态
            LitemallOrder order=orderService.findById(orderId);
            order.setOrderStatus(order.getOrderStatusRefund());

            order.setOrderStatusName(OrderUtil.orderStatusText(order.getOrderStatusRefund()));

            order.setReturnTime(null);
            order.setReturnReason(null);

            orderService.update(order);
            return ResponseUtil.ok(order);

        } catch (Exception ex){
            return ResponseUtil.fail(502,"退款发生错误");
        }
    }

    /**
     * 订单备货
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:order:beihuo")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "订单备货")
    @PostMapping("/beihuo")
    public Object beihuo(@RequestBody String body) {
        return adminOrderService.beihuo(body);
    }

    /**
     * 批量订单备货
     *
     * @param orderIds 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:order:beihuoBatchConfirm")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "线上订单批量备货")
    @GetMapping("/beihuoBatchConfirm")
    public Object beihuoBatchConfirm(Integer[]  orderIds) {
        return adminOrderService.beihuoBatchConfirm(orderIds);
    }

    /**
     * 预约订单备货
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:order:yuYueBeiHuo")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "预约单备货")
    @PostMapping("/yuYueBeiHuo")
    public Object yuYueBeiHuo(@RequestBody String body) {
        return adminOrderService.yuYueBeiHuo(body);
    }

    /**
     * 发货
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:order:ship")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "订单发货")
    @PostMapping("/ship")
    public Object ship(@RequestBody String body) {
        return adminOrderService.ship(body);
    }


    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:order:reply")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "订单商品回复")
    @PostMapping("/reply")
    public Object reply(@RequestBody String body) {
        return adminOrderService.reply(body);
    }


    /**
     * 导出EXCEL
     *
     * @param orderIds
     * @param response
     * @return
     */
    @RequiresPermissions("admin:order:excel")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "导出Excel")
    @GetMapping("/excel")
    @ResponseBody
    public void exportData(Integer[]  orderIds, HttpServletResponse response) {
        try {
            //Assert.notNull(orderIds, "导出的订单不能为空");
            adminOrderService.exportExcel(response,Arrays.asList(orderIds),null);
        } catch (Exception e) {
            e.printStackTrace();
            //return ResponseUtil.fail();
        }
    }

    @RequiresPermissions("admin:order:excelList")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "外借单")
    @GetMapping("/excelList")
    @ResponseBody
    public void exportExcelListData(Integer userId, String orderSn,
                             String typeCode,String sourceCode,
                             String orderType,String consignee,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             @RequestParam(required = false) List<Short> yuyueStatusArray,
                             Boolean ifFapiao,String fapiaoStatus,
                             String sendWay,
                             String phone,
                             String beginDate,
                             String endDate,
                             @RequestParam(required = false) List<Integer> pickSiteIdArray,
                             Integer payMethod,
                             @RequestParam(required = false) List<Integer> orderIds,
                             HttpServletResponse response
    ) {

        String strCondition= getCondition(userId, orderSn, typeCode,sourceCode, orderType,consignee,
                orderStatusArray,yuyueStatusArray, ifFapiao,fapiaoStatus,
                sendWay, phone, beginDate, endDate, pickSiteIdArray, payMethod, orderIds);
        String strSql=" SELECT goods_sn as code,goods_name as name,specifications,price,sum(number) as amount, " +
                "  GROUP_CONCAT(order_sn) as orderno " +
                "  FROM  view_order_goods " +
                "  where deleted=0 "+strCondition+
                " group by goods_sn,goods_name,specifications,price";

        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);

        List result= commonDBService.procedureDaoList(param);
        if(result.size()>0) {
            try {
                adminOrderService.exportExcel(response, null, result);
            } catch (Exception e) {
                e.printStackTrace();
                //return ResponseUtil.fail();
            }
        }

    }


    @RequiresPermissions("admin:order:pdf")
    @RequiresPermissionsDesc(menu = {"商场管理", "线上订单"}, button = "出货单")
    @GetMapping("/pdf")
    @ResponseBody
    public String exportPDFData(Integer[] orderIds, HttpServletRequest request, HttpServletResponse response) {
        try {

            Assert.notNull(orderIds, "未选中任何订单");
            //只支持单张打印

            adminOrderService.exportPDF(Arrays.asList(orderIds), request, response);

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "导出信息失败";
        }
    }

    /**
     * 获取预约备货的单据数量
     * @return
     */
    @RequiresPermissions("admin:order:getYuyueCountInfo")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "预约单")
    @GetMapping("/getYuyueCountInfo")
    public Object getYuyueCountInfo() {
        try {

            Long count=  orderService.getYuyueCountInfo();
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取预约备货的单据数量失败";
        }
    }


    /**
     * 获取电商单备货的数量
     * @return
     */
    @RequiresPermissions("admin:order:getBeihuoCountInfo")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "待备货")
    @GetMapping("/getBeihuoCountInfo")
    public Object getBeihuoCountInfo() {
        try {
            Long count=  orderService.getBeihuoCountInfo();
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取电商单备货的数量失败";
        }
    }

    /**
     * 获取电商单备货的数量_快递
     * @return
     */
    @RequiresPermissions("admin:order:getBeihuoCountInfoKuaiDi")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "快递待备货")
    @GetMapping("/getBeihuoCountInfoKuaiDi")
    public Object getBeihuoCountInfo_KuaiDi() {
        try {
            Long count=  orderService.getBeihuoCountInfo("快递");
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取电商单备货的数量失败";
        }
    }

    /**
     * 获取电商单备货的数量_快递
     * @return
     */
    @RequiresPermissions("admin:order:getBeihuoCountInfoHangZhanLou")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "自提取货待备货")
    @GetMapping("/getBeihuoCountInfoHangZhanLou")
    public Object getBeihuoCountInfo_HangZhanLou() {
        try {
            Long count=  orderService.getBeihuoCountInfo("自提取货");
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取电商单备货的数量失败";
        }
    }

    /**
     * 获取电商单退款单的数量
     * @param orderId
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("admin:order:getTuiKuanCountInfo")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "待退款")
    @GetMapping("/getTuiKuanCountInfo")
    public Object getTuiKuanCountInfo(Integer orderId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Long count=  orderService.getTuiKuanCountInfo();
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取电商单退款单的数量失败";
        }
    }


    /**
     * 获取电商单待开票的单据数量
     * @return
     */
    @RequiresPermissions("admin:order:getKaipiaoCountInfo")
    @RequiresPermissionsDesc(menu = {"首页", "消息"}, button = "待开票")
    @GetMapping("/getKaipiaoCountInfo")
    @ResponseBody
    public Object getKaipiaoCountInfo() {
        try {
            Long count=  orderService.getKaipiaoCountInfo();
            return ResponseUtil.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取电商单待开票的单据数量失败";
        }
    }


    /**
     * 预约单各状态值 返回全部单、待取货、已完成、已取消 四种
     *
     */
    @GetMapping("orderispay")
    public Object ordertest(String orderSn) {
        //处理微信支付平台查询结果
        String strHandleMessage="该订单未在支付平台查询到";
        Boolean wxIfFind=  wxQueryPayResult(orderSn);
        if(wxIfFind){
            strHandleMessage="该订单在微信平台已经支付";
            return ResponseUtil.ok(strHandleMessage);
        }

        //todo 继续处理在支付宝平台上的查询结果
        Boolean zfbIfFind= zfbQueryPayResult(orderSn);
        if(zfbIfFind){
            strHandleMessage="该订单在支付宝平台已经支付";
            return ResponseUtil.ok(strHandleMessage);
        }

        //wxOrderService.zfbQueryPayResult(orderSn);
        //如果都不存在，则返回未查询到的提示
        return ResponseUtil.ok(strHandleMessage);
    }
    /**
     * 预约单各状态值 返回全部单、待取货、已完成、已取消 四种
     *
     */
    @GetMapping("orderlisttest")
    public Object orderListTest() {
        //检索出所有超时取消和待支付状态订单
        String handleNotGetPayId="";
        List<LitemallOrder> allListWithoutPayed = orderService.allListWithoutPayed();
        if(allListWithoutPayed!=null && allListWithoutPayed.size()>0){
            System.out.println("总订单："+allListWithoutPayed.size());
            int i=0;
            for (LitemallOrder litemallOrder : allListWithoutPayed) {
                String orderSn = litemallOrder.getOrderSn();
                try {
                    Boolean wxIfFind =  wxQueryPayResult(orderSn);
                    //如果已经在微信平台查询到，并且该订单的微信支付没有同步到，则将不一致信息返回到前台
                    if(wxIfFind){
                        if(litemallOrder.getPayMethod()==null||litemallOrder.getPayId()==null) {
                            handleNotGetPayId += "订单号:【" + litemallOrder + "】和微信平台支付信息不一致;";
                        }
                    } else
                    {
                        //todo 继续判断在支付宝平台上支付信息是否一致
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("处理订单："+allListWithoutPayed.size());

        //wxOrderService.zfbQueryPayResult(orderSn);
        return ResponseUtil.ok(handleNotGetPayId);
    }


    /**
     * 微信支付结果查询方法-支付成功返回false,未支付返回true（判断订单是否可以调用统一下单接口）
     * @param orderSn
     * @return true:微信已经支付 false:微信未支付
     */
    private Boolean wxQueryPayResult(String orderSn){

        Boolean ifFind=false;

        PayCommonUtil payCommonUtil = new PayCommonUtil();
//        String appsecret = wxMaConfig.getSecret();
        payCommonUtil.setApiKey(wxPayConfig.getMchKey());
        payCommonUtil.setAppId(wxMaConfig.getAppid());
        payCommonUtil.setMchId(wxPayConfig.getMchId());
        payCommonUtil.setDeviceInfo("WEB");
        WeixinPayResultQuery wxPayRequestPayResultQuery = new WeixinPayResultQuery();
        wxPayRequestPayResultQuery.setPayCommonUtil(payCommonUtil);
        Map<String, String> map = wxPayRequestPayResultQuery.orderResultQuery("",
                orderSn);
        String returnCode = map.get("return_code");
        if (returnCode.equals("SUCCESS")) {
            String resultCode = map.get("result_code");
            if (resultCode.equals("SUCCESS")) {
                String tradeState = map.get("trade_state");
                // 支付成功
                if (tradeState.equals("SUCCESS")) {
                    // 业务逻辑实现
                    // 分转化成元
                    String totalFee = BaseWxPayResult.fenToYuan(Integer.parseInt(map.get("total_fee")));
                    this.operatOrder(orderSn, map.get("transaction_id"), totalFee, map.get("openid"));
                    ifFind=true;
                }
            }
        }
        return ifFind;
//        return WxPayNotifyResponse.success("处理成功!");
    }


    private Object operatOrder(String orderSn,String payId,String totalFee,String openid){
        LitemallOrder order = orderService.findBySn(orderSn);
        if (order == null) {
            return WxPayNotifyResponse.fail("订单不存在 sn=" + orderSn);
        }

        // 检查这个订单是否已经处理过
        if (OrderUtil.isPayStatus(order) && order.getPayId() != null) {
            return WxPayNotifyResponse.success("订单已经处理成功!");
        }
        // 检查支付订单金额
        if (!totalFee.equals(order.getActualPrice().toString())) {
            return WxPayNotifyResponse.fail(order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
        }
        order.setPayId(payId);
        order.setPayTime(LocalDateTime.now());
        order.setPayMethod(2);
        order.setPayMethodName("微信");
//        101: '待支付',
//        102: '用户取消',
//        103: '超时取消',
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            // 这里可能存在这样一个问题，用户支付和系统自动取消订单发生在同时
            // 如果数据库首先因为系统自动取消订单而更新了订单状态；
            // 此时用户支付完成回调这里也要更新数据库，而由于乐观锁机制这里的更新会失败
            // 因此，这里会重新读取数据库检查状态是否是订单自动取消，如果是则更新成支付状态。
            order = orderService.findBySn(orderSn);
            int updated = 0;
            if (OrderUtil.isAutoCancelStatus(order)) {
                order.setPayId(payId);
                order.setPayTime(LocalDateTime.now());
                if(order.getMallOrderIfXuni()==true||order.getMallOrderIfTicket()==true){
                    order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
                    order.setOrderStatusName(OrderUtil.STATUS_AUTO_CONFIRM_NAME);
                } else {
                    order.setOrderStatus(OrderUtil.STATUS_PAY);
                    order.setOrderStatusName(OrderUtil.STATUS_PAY_NAME);
                }

                updated = orderService.updateWithOptimisticLocker(order);
            }

            // 如果updated是0，那么数据库更新失败
            if (updated == 0) {
                return WxPayNotifyResponse.fail("更新数据已失效");
            }

        }
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        for (LitemallOrderGoods ordergoods : orderGoodsList) {
            Integer productId = ordergoods.getProductId();
            //有秒杀信息，更新秒杀信息
            List<LitemallPromotionSeckillRule> goodsInSkillList = promotionSeckillRuleService.queryGoodsIsInSkill(productId);
            if(goodsInSkillList!=null &&goodsInSkillList.size()>0){
                LitemallPromotionSeckillRule seckillRule = goodsInSkillList.get(0);
                Boolean onlyOne = seckillRule.getSeckillOnlyOne();
                boolean checkExistByUseridRuleid =false;
                if(onlyOne){
                    checkExistByUseridRuleid = promotionSeckillOrderService.checkExistByUseridRuleid(order.getUserId(), seckillRule.getId());
                }
                if(!checkExistByUseridRuleid){
                    LitemallPromotionSeckillOrder seckillOrder  = new LitemallPromotionSeckillOrder();
                    seckillOrder.setDeleted(false);
                    seckillOrder.setOrderId(order.getId());
                    seckillOrder.setRulesId(seckillRule.getId());
                    seckillOrder.setUserId(order.getUserId());
                    int createSeckill = promotionSeckillOrderService.createSeckill(seckillOrder);
                }
            }
        }

        return WxPayNotifyResponse.success("处理成功!");
    }

    /**
     * 支付宝支付结果查询方法-支付成功返回false,未支付返回true（判断订单是否可以调用下单接口）
     * @param orderSn
     * @return
     */
    public boolean zfbQueryPayResult(String orderSn){
        AlipayConfig alipayConf = new AlipayConfig();
        alipayConf.setApp_id(alipayConfig.getAppId());
        alipayConf.setPartner(alipayConfig.getSellerId());
        alipayConf.setAlipay_public_key(alipayConfig.getPublicKey());
        alipayConf.setPrivate_key(alipayConfig.getPrivateKey());
        alipayConf.setInput_charset("utf-8");
        alipayConf.setNotify_url(alipayConfig.getJumpUrl());
        alipayConf.setSign_type("RSA2");
        alipayConf.setSeller_id(alipayConfig.getSellerId());
        AlipaySonResultQuery alipayResultQuery = new AlipaySonResultQuery();
        alipayResultQuery.setAlipayConfig(alipayConf);
        try {
            AlipayTradeQueryResponse queryResponse = alipayResultQuery.QueryOrder(
                    orderSn, "");
            // 接口调用成功
            if (queryResponse.getCode().equals("10000")) {
                // 交易支付成功
                if (queryResponse.getTradeStatus().equals("TRADE_SUCCESS")) {
                    // 业务逻辑实现
                    this.alipaySuccess(queryResponse.getOutTradeNo(),queryResponse.getTradeNo());
                    return true;
                }
            }
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 支付宝异步回调习惯订单
     * @param ordersn
     * @param payId
     */
    public void alipaySuccess(String ordersn, String payId) {
        LitemallOrder order = orderService.findBySn(ordersn);
        if (order == null) {
            return ;
        }

        // 检查这个订单是否已经处理过
        if (!OrderUtil.isPayStatus(order) || order.getPayId() != null) {
            return ;
        }
        order.setPayId(payId);
        order.setPayTime(LocalDateTime.now());
        if(order.getMallOrderIfXuni()==true||order.getMallOrderIfTicket()==true){
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setOrderStatusName(OrderUtil.STATUS_AUTO_CONFIRM_NAME);
        } else {
            order.setOrderStatus(OrderUtil.STATUS_PAY);
            order.setOrderStatusName(OrderUtil.STATUS_PAY_NAME);
        }
        order.setPayMethod(1);
        order.setPayMethodName("支付宝");

        int locker = orderService.updateWithOptimisticLocker(order);

        System.out.println(locker);

        //秒杀
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        for (LitemallOrderGoods ordergoods : orderGoodsList) {
            Integer productId = ordergoods.getProductId();
            //有秒杀信息，更新秒杀信息
            List<LitemallPromotionSeckillRule> goodsInSkillList = promotionSeckillRuleService.queryGoodsIsInSkill(productId);
            if(goodsInSkillList!=null &&goodsInSkillList.size()>0){
                LitemallPromotionSeckillRule seckillRule = goodsInSkillList.get(0);
                Boolean onlyOne = seckillRule.getSeckillOnlyOne();
                boolean checkExistByUseridRuleid =false;
                if(onlyOne){
                    checkExistByUseridRuleid = promotionSeckillOrderService.checkExistByUseridRuleid(order.getUserId(), seckillRule.getId());
                }
                if(!checkExistByUseridRuleid){
                    LitemallPromotionSeckillOrder seckillOrder  = new LitemallPromotionSeckillOrder();
                    seckillOrder.setDeleted(false);
                    seckillOrder.setOrderId(order.getId());
                    seckillOrder.setRulesId(seckillRule.getId());
                    seckillOrder.setUserId(order.getUserId());
                    int createSeckill = promotionSeckillOrderService.createSeckill(seckillOrder);
                }
            }
        }

    }

    /**
     * 系统按照拍卖最高出价，生成订单
     * @return
     */
    @PostMapping("createPaiMaiOrder")
    public Object submitPaiMaiOrder(@RequestBody String body){

        //大家拍、专场拍
        String  auctionType= JacksonUtil.parseString(body, "auctionType");
        //出价ID
        Integer offerId= JacksonUtil.parseInteger(body, "offerId");
        //收货人
        String  consignee=JacksonUtil.parseString(body,"consignee");
        //收货人电话
        String  mobile=JacksonUtil.parseString(body,"mobile");
        //收货人详细地址
        String  address=JacksonUtil.parseString(body,"address");
        //运输方式
        String  sendWay=JacksonUtil.parseString(body,"sendWay");
        //邮费
        String  freightPrice=JacksonUtil.parseString(body,"freightPrice");

        //如果是大家拍，判断是否已经生成订单，如果已经生成订单，则应该先取消原来的订单
        //如果是专场拍，则判断专场下的商品明细是否已经生成订单，如果已经生成，则取消原来的订单，才可以继续创建
        Boolean ifExist=orderService.ifExistsOrder(auctionType,offerId);
        if(ifExist){
            return ResponseUtil.fail(502,"该【"+auctionType+"】类型订单已经生成,请先取消后再创建");
        }
        Integer userId=null;
        BigDecimal paimaiMoney=null;//出具的拍品价格
        Integer goodsId=null;//商品编码
        Integer goodsProductId=null;
        String goodsName=null;
        String goodsSn=null;

        String specifications=null;
        String picUrl=null;
        //创建订单
        LitemallOrder order=new LitemallOrder();
        order.setAuctionType(auctionType);
        LitemallAuctionDajiaOfferCurrent dajiaOfferCurrent=null;
        LitemallAuctionZhuanchangOfferCurrent zhuanchangOfferCurrent=null;
        if(auctionType.equals("大家拍")){
            dajiaOfferCurrent=
                    dajiaOfferCurrentService.queryById(offerId);
            userId=dajiaOfferCurrent.getUserId();
            paimaiMoney=dajiaOfferCurrent.getOfferMoney();
            order.setDajiapaiOfferId(offerId);
            LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent=
                    dajiaRuleCurrentService.findById(dajiaOfferCurrent.getRulesId());
            goodsId=dajiaRuleCurrent.getGoodsId();
            goodsProductId=dajiaRuleCurrent.getGoodsProductId();
            goodsName=dajiaRuleCurrent.getGoodsName();
            goodsSn=dajiaRuleCurrent.getGoodsSn();
            specifications=dajiaRuleCurrent.getGoodsProductSpecifications();
            picUrl=dajiaRuleCurrent.getAuctionPicHead();

        } else  if(auctionType.equals("专场拍"))
        {
            zhuanchangOfferCurrent=
                    zhuanchangOfferCurrentService.queryById(offerId);
            userId=zhuanchangOfferCurrent.getUserId();
            paimaiMoney=zhuanchangOfferCurrent.getOfferMoney();
            order.setZhuanchangOfferId(offerId);
            LitemallAuctionZhuanchangGoodsCurrent zhuanchangGoodsCurrent=
                    zhuanchangGoodsCurrentService.queryById(zhuanchangOfferCurrent.getRulesMxId());
            goodsId=zhuanchangGoodsCurrent.getGoodsId();
            goodsProductId=zhuanchangGoodsCurrent.getGoodsProductId();
            goodsName=zhuanchangGoodsCurrent.getGoodsName();
            goodsSn=zhuanchangGoodsCurrent.getGoodsSn();
            specifications=zhuanchangGoodsCurrent.getGoodsProductSpecifications();
            picUrl=zhuanchangGoodsCurrent.getAuctionPicHead();
        }

        LitemallUser user=userService.findById(userId);

        order.setUserId(userId);
        order.setUserName(user.getUsername());
        order.setUserNickname(user.getNickname());
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setOrderStatusName(OrderUtil.STATUS_CREATE_NAME);
        String orderSn = orderService.generateOrderSn(userId);
        order.setOrderSn(orderSn);

        //收货人信息 因为是后台创建订单，此时并不知道用户的收货地址，需要前台由用户进行创建
//        LitemallAddress address=addressService.query(makeOrder.getAddressId());
        order.setConsignee(consignee);
        order.setMobile(mobile);
        order.setAddress(address);
        //订单金额信息
        order.setGoodsPrice(paimaiMoney.add(CharUtil.objectConverToBigdecimalDefault0(freightPrice)));
        order.setFreightPrice(CharUtil.objectConverToBigdecimalDefault0(freightPrice));
        order.setOrderPrice(order.getGoodsPrice());
        order.setActualPrice(order.getGoodsPrice());

        order.setSendWay(sendWay);
        orderService.add(order);

        //生成订单货物明细
        LitemallOrderGoods orderGoods=new LitemallOrderGoods();
        orderGoods.setOrderId(order.getId());
        orderGoods.setGoodsId(goodsId);
        orderGoods.setProductId(goodsProductId);
        orderGoods.setGoodsName(goodsName);
        orderGoods.setGoodsSn(goodsSn);
        String[] goodsSpec= specifications.split(",");
        orderGoods.setSpecifications(goodsSpec);
        orderGoods.setPicUrl(picUrl);
        orderGoods.setNumber(Short.parseShort("1"));
        orderGoods.setPrice(paimaiMoney);
        orderGoodsService.add(orderGoods);

        //更新出价关联订单ID
        if(auctionType.equals("大家拍")){
            dajiaOfferCurrent.setOrderId(order.getId());
            dajiaOfferCurrentService.updateById(dajiaOfferCurrent);
        } else  if(auctionType.equals("专场拍")){
            zhuanchangOfferCurrent.setOrderId(order.getId());
            zhuanchangOfferCurrentService.updateById(zhuanchangOfferCurrent);
        }

        //拍卖结束，拍品下架 大家怕和专场拍过期



        return ResponseUtil.ok(order);

    }



}
