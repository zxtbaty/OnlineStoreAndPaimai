package org.jinyuanjava.litemall.wx.service;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.yichi.alipay.pay.util.AlipayConfig;
import com.yichi.weixin.pay.inter.WeixinPayResultQuery;
import com.yichi.weixin.pay.util.PayCommonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.config.AliPayConfig;
import org.jinyuanjava.litemall.core.config.AlipaySonResultQuery;
import org.jinyuanjava.litemall.core.express.ExpressService;
import org.jinyuanjava.litemall.core.express.dao.ExpressInfo;
import org.jinyuanjava.litemall.core.express.dao.UserTicket;
import org.jinyuanjava.litemall.core.notify.NotifyService;
import org.jinyuanjava.litemall.core.notify.NotifyType;
import org.jinyuanjava.litemall.core.qcode.QCodeService;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.core.util.*;
import org.jinyuanjava.litemall.core.util.wxMessage.WxMessageUtil;
import org.jinyuanjava.litemall.db.dao.LitemallOrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewLitemallGoodsProductMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderHandleOption;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.jinyuanjava.litemall.wx.dto.OrderAllInOne;
import org.jinyuanjava.litemall.wx.dto.PrivateMakOrderAllInOne;
import org.jinyuanjava.litemall.wx.util.CommonTools;
import org.jinyuanjava.litemall.wx.util.RequestHandler;
import org.jinyuanjava.litemall.wx.util.WXPreutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.jinyuanjava.litemall.wx.util.WxResponseCode.*;

/**
 * 订单服务
 *
 * <p>
 * 订单状态：
 * 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货； 402 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作：
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * <p>
 * 注意：目前不支持订单退货和售后服务
 */
@Service
public class WxOrderService_Backup_20200315 {
    private final Log logger = LogFactory.getLog(WxOrderService_Backup_20200315.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallRegionService regionService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private LitemallUserFormIdService formIdService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallGrouponOrderService grouponOrderService;

    @Autowired
    private QCodeService qCodeService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private CouponVerifyService couponVerifyService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallStoreService storeService;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AliPayConfig alipayConfig;
    @Autowired
    private WxPayConfig wxPayConfig;
    @Autowired
    private WxMaConfig wxMaConfig;

    @Autowired
    private LitemallOpadminInfoService opadminInfoService;
    @Autowired
    private LitemallPromotionSeckillRuleService promotionSeckillRuleService;
    @Autowired
    private LitemallPromotionSeckillOrderService promotionSeckillOrderService;

    @Autowired
    private LitemallOrderTicketsService orderTicketsService;
    @Autowired
    private LitemallOrderFapiaoService orderFapiaoService;

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService; //会员专区商品规则

    @Autowired
    private LitemallPromotionGoodsDetailService promotionGoodsDetailService;//会员专区商品列表明细

    @Autowired
    private LitemallPromotionGoodsUserOrderService promotionGoodsUserOrderService; //会员专区商品订单

    @Autowired
    private LitemallPickSiteService litemallPickSiteService;

    @Resource
    private LitemallOrderMapper litemallOrderMapper;
	@Autowired
    private WxMessageUtil wxMessageUtil;

	@Resource
    private ViewLitemallGoodsProductMapper viewLitemallGoodsProductMapper;

	@Autowired
    private LitemallPromotionGoodsRebateOrderService goodsRebateOrderService;

	@Autowired
    private LitemallPrivateMakeOrderService privateMakeOrderService;

	@Autowired
    private LitemallUserChargeMoneyService userChargeMoneyService;




    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 订单信息：
     *                 0，全部订单；
     *                 1，待付款；
     *                 2，待发货；
     *                 3，待收货；
     *                 4，已完成。
     *                 5,已取消
     *
     *                 9,全部预约单
     *                 10,预约待取货单
     *                 11,预约已完成单
     *                 12,预约已取消单
     * @param page     分页页数
     * @param limit     分页大小
     * @return 订单列表
     */
    public Object list(Integer userId, Integer showType, Integer page, Integer limit) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        List<LitemallOrder> orderList;
        if(showType==9||showType==10||showType==11||showType==12){
        	List<Short> orderStatus = OrderUtil.orderStatus(showType);
            orderList = orderService.queryByCom(userId,orderStatus, page, limit);
        }else{
        	List<Short> orderStatus = OrderUtil.orderStatus(showType);
            orderList = orderService.queryByOrderStatus(userId, orderStatus, page, limit);
        }
        long count = PageInfo.of(orderList).getTotal();
        int totalPages = (int) Math.ceil((double) count / limit);

        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (LitemallOrder order : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            if(showType==9||showType==10||showType==11||showType==12){
            	orderVo.put("state", order.getYuyueStatusCode());
            	orderVo.put("address", order.getYuyueComName()==null?"":order.getYuyueComName()
            			+order.getYuyueComHangzhanlou()==null?"":order.getYuyueComHangzhanlou()
            			+order.getYuyueStoreName()==null?"":order.getYuyueStoreName());
            	orderVo.put("fetchTime", order.getYuyueFetchTime());
            	orderVo.put("fetchExpireTime", order.getYuyueFetchExpireTime());
            }else{
            	orderVo.put("state", order.getOrderStatus());
            }
            orderVo.put("id", order.getId());
            orderVo.put("time", order.getAddTime());
            orderVo.put("adminAllowRefund", order.getAdminAllowRefund());
            orderVo.put("orderSn", order.getOrderSn());
            orderVo.put("actualPrice", order.getActualPrice());
            orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
            orderVo.put("handleOption", OrderUtil.build(order));

//            LitemallGrouponOrder groupon = grouponService.queryByOrderId(order.getId());
//            if (groupon != null) {
//                orderVo.put("isGroupin", true);
//            } else {
//                orderVo.put("isGroupin", false);
//            }

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            int nums=0;
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();

                nums=nums+orderGoods.getNumber();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("title", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVo.put("image", orderGoods.getPicUrl());
                orderGoodsVo.put("price", orderGoods.getPrice());
                orderGoodsVo.put("attr", orderGoods.getSpecifications());
                orderGoodsVo.put("specifications", orderGoods.getSpecifications());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("nums", nums);
            orderVo.put("goodsList", orderGoodsVoList);

            orderVoList.add(orderVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("data", orderVoList);
        result.put("totalPages", totalPages);

        return ResponseUtil.ok(result);
    }


    /**
     * 获在线订单各状态数量
     * @param userId
     * @return
     */
    public JSONObject getOnLinePayListCount(Integer userId){
        //获取我的订单数量
        List<Short> orderStatus = OrderUtil.orderStatus(0);
        LitemallOrderExample orderExample=new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myAllOrderCount= litemallOrderMapper.countByExample(orderExample);
        //待付款
        orderStatus = OrderUtil.orderStatus(1);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myDaiFuKuanCount= litemallOrderMapper.countByExample(orderExample);
        //待发货
        orderStatus = OrderUtil.orderStatus(2);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myDaiFaHuoCount= litemallOrderMapper.countByExample(orderExample);
        //待收货
        orderStatus = OrderUtil.orderStatus(3);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myDaiShouHuoCount= litemallOrderMapper.countByExample(orderExample);
        //已完成
        orderStatus = OrderUtil.orderStatus(4);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myYiWanChengCount= litemallOrderMapper.countByExample(orderExample);
        //已取消
        orderStatus = OrderUtil.orderStatus(5);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        Long myYiQuXiaoCount= litemallOrderMapper.countByExample(orderExample);

        //待退款/已退款

        Long myTuikuanShouhouCount= getBackOrderCount(userId);


        JSONObject jsonObject=new JSONObject();
        jsonObject.put("myAllOrderCount",myAllOrderCount);
        jsonObject.put("myDaiFuKuanCount",myDaiFuKuanCount);
        jsonObject.put("myDaiFaHuoCount",myDaiFaHuoCount);
        jsonObject.put("myDaiShouHuoCount",myDaiShouHuoCount);
        jsonObject.put("myYiWanChengCount",myYiWanChengCount);
        jsonObject.put("myYiQuXiaoCount",myYiQuXiaoCount);
        jsonObject.put("myTuikuanShouhouCount",myTuikuanShouhouCount);
        return jsonObject;
    }

    public Long getBackOrderCount(Integer userId) {
    	 LitemallOrderExample orderExample=new LitemallOrderExample();
         LitemallOrderExample.Criteria criteria= orderExample.createCriteria();
    	//待退款/已退款
         List<Short> orderStatus = OrderUtil.orderStatus(6);
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        if(orderStatus!=null) {
            criteria.andOrderStatusIn(orderStatus);
        }
    	return litemallOrderMapper.countByExample(orderExample);
    }


    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    public Object detail(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        // 订单信息
        LitemallOrder order = orderService.findById(orderId);
        if (null == order) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail(ORDER_INVALID, "不是当前用户的订单");
        }
        Integer yuyueComId = order.getYuyueComId();
        Map<String, Object> orderVo = new HashMap<String, Object>();
        Map<String, Object> orderVo2 = new HashMap<String, Object>();


        	//支付订单
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("couponPrice", order.getCouponPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        orderVo.put("status", order.getOrderStatus());
        orderVo.put("mai", OrderUtil.orderStatusText(order));
        orderVo.put("phone", order.getMobile());
        orderVo.put("username", order.getConsignee());

        //根据例子的数据来，价格啥的都放在这里
        orderVo2.put("id", order.getId());
        orderVo2.put("orderSn", order.getOrderSn());//订单编号
        orderVo2.put("countgoods", order.getGoodsPrice());//商品金额
        orderVo2.put("kuaidi", order.getFreightPrice());//运费
        orderVo2.put("cut", order.getCouponPrice());//优惠券金额
        //订单总价 商品总价+运费
        orderVo2.put("countorder",order.getGoodsPrice().add(order.getFreightPrice()));
        orderVo2.put("redbag", order.getGrouponPrice()); //红包
        orderVo2.put("pay", order.getActualPrice());//实际付款金额
        orderVo2.put("jifen", order.getIntegralBonus());//使用积分
        orderVo2.put("integralPrice", order.getIntegralPrice());//积分兑换金额
        orderVo2.put("ordernumber", order.getOrderSn());//订单编号
        orderVo2.put("countnumber", order.getPayId());
        orderVo2.put("createtime", order.getAddTime());//创建时间
        orderVo2.put("paytime", order.getPayTime());//支付时间
        orderVo2.put("carttime", order.getShipTime());//发运时间
        orderVo2.put("kuaidiCom", order.getShipChannel());//快递公司
        orderVo2.put("shipSn", order.getShipSn());//物流单号
        orderVo2.put("receiveTime", order.getConfirmTime());//收货时间


        //提货点
        orderVo2.put("PickSiteName",order.getPickSiteName());
        orderVo2.put("PickTime",order.getPickTime());
        orderVo2.put("PickPerson",order.getPickPerson());
        orderVo2.put("PickMobile",order.getPickMobile());


        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        List<Map<String, Object>> orderGoodsListNew = new ArrayList<Map<String,Object>>();

        for (LitemallOrderGoods litemallOrderGoods : orderGoodsList) {
        	 Map<String, Object> orderGood= new HashMap<String, Object>();
        	 orderGood.put("title",litemallOrderGoods.getGoodsName() );
        	 orderGood.put("img",litemallOrderGoods.getPicUrl() );
        	 orderGood.put("id",litemallOrderGoods.getGoodsId());
        	 orderGood.put("price",litemallOrderGoods.getPrice());
        	 orderGood.put("num",litemallOrderGoods.getNumber() );
        	 orderGood.put("size",litemallOrderGoods.getSpecifications() );
        	// orderGood.put("others",litemallOrderGoods.get );

        	 orderGoodsListNew.add(orderGood);
		}

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderInfo2", orderVo2);
        result.put("orderGoods", orderGoodsListNew);

        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            result.put("expressInfo", ei);
        }

        return ResponseUtil.ok(result);

    }
    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderSn 订单号
     * @return 订单详情
     */
    public Object detail(Integer userId, String orderSn) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        // 订单信息
        LitemallOrder order = orderService.findBySn(orderSn);
        if (null == order) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail(ORDER_INVALID, "不是当前用户的订单");
        }
        Integer yuyueComId = order.getYuyueComId();
        Map<String, Object> orderVo = new HashMap<String, Object>();
        Map<String, Object> orderVo2 = new HashMap<String, Object>();


        	//支付订单
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("couponPrice", order.getCouponPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        orderVo.put("status", order.getOrderStatus());
        orderVo.put("mai", OrderUtil.orderStatusText(order));
        orderVo.put("phone", order.getMobile());
        orderVo.put("username", order.getConsignee());

        //根据例子的数据来，价格啥的都放在这里
        orderVo2.put("id", order.getId());
        orderVo2.put("orderSn", order.getOrderSn());//订单编号
        orderVo2.put("countgoods", order.getGoodsPrice());//商品金额
        orderVo2.put("kuaidi", order.getFreightPrice());//运费
        orderVo2.put("cut", order.getCouponPrice());//优惠券金额
        //订单总价 商品总价+运费
        orderVo2.put("countorder",order.getGoodsPrice().add(order.getFreightPrice()));
        orderVo2.put("redbag", order.getGrouponPrice()); //红包
        orderVo2.put("pay", order.getActualPrice());//实际付款金额
        orderVo2.put("jifen", order.getIntegralBonus());//使用积分
        orderVo2.put("integralPrice", order.getIntegralPrice());//积分兑换金额
        orderVo2.put("ordernumber", order.getOrderSn());//订单编号
        orderVo2.put("countnumber", order.getPayId());
        orderVo2.put("createtime", order.getAddTime());//创建时间
        orderVo2.put("paytime", order.getPayTime());//支付时间
        orderVo2.put("carttime", order.getShipTime());//发运时间
        orderVo2.put("kuaidiCom", order.getShipChannel());//快递公司
        orderVo2.put("shipSn", order.getShipSn());//物流单号
        orderVo2.put("receiveTime", order.getConfirmTime());//收货时间


        //提货点
        orderVo2.put("PickSiteName",order.getPickSiteName());
        orderVo2.put("PickTime",order.getPickTime());
        orderVo2.put("PickPerson",order.getPickPerson());
        orderVo2.put("PickMobile",order.getPickMobile());


        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        List<Map<String, Object>> orderGoodsListNew = new ArrayList<Map<String,Object>>();

        for (LitemallOrderGoods litemallOrderGoods : orderGoodsList) {
        	 Map<String, Object> orderGood= new HashMap<String, Object>();
        	 orderGood.put("title",litemallOrderGoods.getGoodsName() );
        	 orderGood.put("img",litemallOrderGoods.getPicUrl() );
        	 orderGood.put("id",litemallOrderGoods.getGoodsId());
        	 orderGood.put("price",litemallOrderGoods.getPrice());
        	 orderGood.put("num",litemallOrderGoods.getNumber() );
        	 orderGood.put("size",litemallOrderGoods.getSpecifications() );
        	// orderGood.put("others",litemallOrderGoods.get );

        	 orderGoodsListNew.add(orderGood);
		}

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderInfo2", orderVo2);
        result.put("orderGoods", orderGoodsListNew);

        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            result.put("expressInfo", ei);
        }

        return ResponseUtil.ok(result);

    }

    /**
     *提交订单
     * @param userId 用户ID
     * @param orderStructData 订单数据结构
     * @return
     */
    @Transactional
    public Object submit(Integer userId,OrderAllInOne orderStructData) {

        UserTicket coupon=null;
        LitemallOrderFapiao orderFapiao=null;
        if(orderStructData!=null){
            coupon=orderStructData.getCoupon();
            orderFapiao=orderStructData.getFapiao();
        }
        LitemallUser user = userService.findById(userId);
    	if (user ==null) {
            return ResponseUtil.unlogin();
        }

        String cartIds = orderStructData.getCartIds();
        Integer productId= orderStructData.getProductId();
        //该订单来源于某个活动规则
        Integer ruleId= orderStructData.getRuleId();
        Boolean ifXuni=null;//单一商品是否是虚拟商品

        if (cartIds == null && productId==null) {
            return ResponseUtil.fail(502,"未选择要结帐的商品");
        }
        Integer addressId = orderStructData.getAddressId();
//        if(!orderStructData.getSendWay().equals("自提取货")) {
//            if (addressId == null||addressId==0) {
//                return ResponseUtil.fail(502, "请先选择收款地址");
//            }
//        }

        String message= orderStructData.getMessage();
        Short number= orderStructData.getNumber();
        Integer comId=orderStructData.getComId();
        //使用的积分抵现
        BigDecimal integralPrice=orderStructData.getIntegralPrice();
        //使用积分
        BigDecimal integralBonus=orderStructData.getIntegralBonus();
        //运费
        BigDecimal freightPrice=orderStructData.getFreightPrice();
        String mallOrderTypeCode=orderStructData.getMallOrderTypeCode();

        if(orderStructData.getSendWay().equals("自提取货")&&orderStructData.getPickSiteId()!=null&&orderStructData.getPickSiteId()!=0) {
           String verifyTihuo= verifyTihuoTime(null,orderStructData.getPickSiteId(),orderStructData.getPickTime());
           if(verifyTihuo!=null){
               return ResponseUtil.fail(502,verifyTihuo);
           }
           //扣除运费
           freightPrice=new BigDecimal(0);
        }
        LitemallAddress checkedAddress = addressService.query(addressId);
        // 收货地址 如果不是自提取货，则验证收货地址
        if(!orderStructData.getSendWay().equals("自提取货")) {
            if (checkedAddress == null) {
//            return ResponseUtil.badArgument();
                if (productId != null) {
                    ViewLitemallGoodsProductExample example = new ViewLitemallGoodsProductExample();
                    example.or().andIdEqualTo(productId);
                    ViewLitemallGoodsProduct viewLitemallGoodsProduct = viewLitemallGoodsProductMapper.selectOneByExample(example);
                    if (viewLitemallGoodsProduct.getIfXuni()) {
                        //如果是虚拟商品订单，则可以不用验证收货地址
                    } else {
                        return ResponseUtil.fail(502, "未设置收货地址");
                    }
                } else {
                    return ResponseUtil.fail(502, "未设置收货地址");
                }
            }
        }

        // 订单
        LitemallOrder order = new LitemallOrder();
        String orderSn = orderService.generateOrderSn(userId);

        Integer userGoodsRuleId=null;
        Integer secRuleId=null;
        Integer grouponRuleId=null;
        Integer rebateRuleId=null;

        // 构建购物车
        List<LitemallCart> checkedGoodsList = null;
        //单一商品结账 秒杀单、团购单、虚拟商品都必须要走单一商品创建订单和支付
        if(productId!=null && productId!=0){
            LitemallGoodsProduct goodsProduct = productService.findById(productId);
            Integer goodsId = goodsProduct.getGoodsId();
            LitemallGoods goods = goodsService.findById(goodsId);
            if(goods.getIsOnSale()==false){
            	return ResponseUtil.fail(502,"商品已下架");
            }
            ifXuni=goods.getIfXuni();
            LitemallCart cart = new LitemallCart();
            BigDecimal goodsPrice=orderStructData.getPrice();
            //如果订单类型是秒杀单，则从秒杀规则中取单价
            if(mallOrderTypeCode.equals("20")){
                //判断单人单次规则
                Boolean  ifAllowSeckillNext=promotionSeckillRuleService.checkIfAllowSeckillNext(ruleId,userId);
                if(ifAllowSeckillNext==false){
                    return ResponseUtil.fail(502,"您已经参与过此次秒杀活动,请将机会留给其他人吧!");
                }
                secRuleId=ruleId;
//                LitemallPromotionSeckillRule litemallPromotionSeckillRule= promotionSeckillRuleService.queryById(ruleId);
//                goodsPrice=litemallPromotionSeckillRule.getSeckillPrice();
            } else  if(mallOrderTypeCode.equals("30")){ //如果订单是团购，则从团购里取单价
                //判断单人单次规则
                Boolean  ifAllowGroupOnNext=grouponRulesService.checkIfAllowGroupOnNext(ruleId,userId);
                if(ifAllowGroupOnNext==false){
                    return ResponseUtil.fail(502,"您已经参与过此次团购活动,不能重复参与!");
                }
                grouponRuleId=ruleId;
//                LitemallGrouponRules litemallGrouponRules= grouponRulesService.queryById(ruleId);
//                goodsPrice=litemallGrouponRules.getGrouponPrice();
            } else{//如果是普通商品，包括虚拟商品，则从商品表里取数
                //判断单人单次规则
                //判断一下该商品是否是会员专属商品，如果是会员商品则需要从规则里进行判断
                LitemallPromotionGoodsDetail promotionGoodsDetail=promotionGoodsRuleService.getRuleByProductId(productId,userId);
                if(promotionGoodsDetail!=null){
//                    goodsPrice=promotionGoodsRule.getUserPrice();
                    //设置是否是会员商品
                    order.setMallOrderIfHuiyuan(true);
                    if(promotionGoodsDetail.getOnlyBuyOne()){
                        if (number > 1) {
                            return ResponseUtil.fail(502, "商品【" + promotionGoodsDetail.getGoodsName() + "】,只能购买一个,请修改数量!");
                        }
                    }
                    Boolean  ifAllowGoodsUserNext=promotionGoodsRuleService.checkIfAllowGoodsUserNext(ruleId,productId,userId);
                    if(ifAllowGoodsUserNext==false){
                        return ResponseUtil.fail(502,"商品【"+promotionGoodsDetail.getGoodsName()+"】,会员只能下单一次,不能重复参与!");
                    }
                    userGoodsRuleId=ruleId;
                } else
                {
                    //如果以上都不是，但后台收到了前台传过来的活动ID，则只能是品项折扣规则ID
                    if(!StringUtils.isEmpty(ruleId)){
                        rebateRuleId=ruleId;
                    }
                }
            }
            cart.setPrice(goodsPrice);
            //判断是否是虚拟商品
            if(goods.getIfXuni()){
                order.setMallOrderIfXuni(true);
            }
            //是否CRM购券
            if(goods.getIfTicket()){
                order.setMallOrderIfTicket(true);
            }
            cart.setGoodsId(goodsId);
            cart.setGoodsPosKey(goodsProduct.getGoodsPosKey());
            cart.setGoodsName(goods.getName());
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setNumber(number);
            cart.setPicUrl(goods.getPicUrl());
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setUserGoodsRuleId(userGoodsRuleId);
            cart.setSecRuleId(secRuleId);
            cart.setGrouponRuleId(grouponRuleId);
            cart.setRebateRuleId(rebateRuleId);
            cart.setSpecifications(goodsProduct.getSpecifications());
            checkedGoodsList=new ArrayList<>();
            checkedGoodsList.add(cart);
        }else{
            if (cartIds.equals(0)) {
                //结算购物车里的所有商品
                checkedGoodsList = cartService.queryByUidAndChecked(userId);
            } else {
                String[] split = cartIds.split(",");
                for (String cartIdStr : split) {
                    int cartId = Integer.parseInt(cartIdStr);
                    LitemallCart cart = cartService.findById(cartId);
                    Integer goodsId = cart.getGoodsId();
                    LitemallGoods goods = goodsService.findById(goodsId);
                    if(goods.getIsOnSale()==false){
                    	return ResponseUtil.fail(502,"商品"+goods.getName()+"已下架");
                    }
                    if(checkedGoodsList==null){
                        checkedGoodsList = new ArrayList<>();
                    }
                    checkedGoodsList.add(cart);
                }
            }
        }
        if (checkedGoodsList.size() == 0) {
            return ResponseUtil.fail(502,"没有可结帐商品");
        }

        List<LitemallOrderGoods> orderGoodsList = new ArrayList<>();
        Map<Integer,LitemallGoodsProduct> goodsProductMap = new HashMap<>();//productId为key
        for (LitemallCart cartGoods : checkedGoodsList) {
            //判断各种活动库存是否足够
            Integer cartUserGoodsRuleId=cartGoods.getUserGoodsRuleId();
            Integer cartRebateRuleId=cartGoods.getRebateRuleId();//品项折扣和正常商品使用相同库存
            Integer cartSecRuleId=cartGoods.getSecRuleId();
            Integer cartGrouponRuleId=cartGoods.getGrouponRuleId();
            if(cartUserGoodsRuleId!=null){
                LitemallPromotionGoodsDetail promotionGoodsDetail= promotionGoodsDetailService.querySelective(cartUserGoodsRuleId,cartGoods.getProductId());
                if(CharUtil.objectConverToInteger(promotionGoodsDetail.getRemainNum())<CharUtil.objectConverToInteger(cartGoods.getNumber())){
                    return ResponseUtil.fail(502, "创建订单失败,货品"+cartGoods.getGoodsName()+cartGoods.getSpecifications()+"数量大于库存量");
                }
            } else
            if(cartSecRuleId!=null){
                //秒杀商品和规则是一体的
                LitemallPromotionSeckillRule promotionSeckillRule= promotionSeckillRuleService.queryById(cartSecRuleId);
                if(CharUtil.objectConverToInteger(promotionSeckillRule.getSeckillRemainNum())<CharUtil.objectConverToInteger(cartGoods.getNumber())){
                    return ResponseUtil.fail(502, "创建订单失败,货品"+cartGoods.getGoodsName()+cartGoods.getSpecifications()+"数量大于库存量");
                }
            } else
            if(cartGrouponRuleId!=null){
                //秒杀商品和规则是一体的
                LitemallGrouponRules grouponRules= grouponRulesService.queryById(cartGrouponRuleId);
                if(CharUtil.objectConverToInteger(grouponRules.getGroupRemainStore())<CharUtil.objectConverToInteger(cartGoods.getNumber())){
                    return ResponseUtil.fail(502, "创建订单失败,货品"+cartGoods.getGoodsName()+cartGoods.getSpecifications()+"数量大于库存量");
                }
            } else {
                LitemallGoodsProduct product = productService.findById(cartGoods.getProductId());
                if (product != null) {
                    goodsProductMap.put(cartGoods.getProductId(), product);
                    Integer remainNumber = CharUtil.objectConverToInteger(product.getNumber()) - cartGoods.getNumber();
                    if (remainNumber < 0) {
                        LitemallGoods goods = goodsService.findById(product.getGoodsId());
                        String Specifications = "[";
                        for (String string : product.getSpecifications()) {
                            Specifications += string;
                        }
                        Specifications += "]";
                        return ResponseUtil.fail(502, "创建订单失败,货品" + goods.getName() + Specifications + "数量大于库存量");
                    }
                } else {
                    return ResponseUtil.fail(502, "货品不存在");
                }
            }
            //如果是会员商品，则判断会员商品规则
            if(cartGoods.getUserGoodsRuleId()!=null){
                LitemallPromotionGoodsDetail promotionGoodsDetail= promotionGoodsDetailService.querySelective(cartGoods.getUserGoodsRuleId(),cartGoods.getProductId());
                if (promotionGoodsDetail.getOnlyBuyOne()) {
                    if (cartGoods.getNumber() > 1) {
                        return ResponseUtil.fail(502, "商品【" + promotionGoodsDetail.getGoodsName() + "】,只能购买一个,请修改数量!");
                    }
                }
                Boolean ifAllowGoodsUserNext = promotionGoodsRuleService.checkIfAllowGoodsUserNext(cartGoods.getUserGoodsRuleId(),
                        cartGoods.getProductId(), userId);
                if (ifAllowGoodsUserNext == false) {
                    return ResponseUtil.fail(502, "商品【" + promotionGoodsDetail.getGoodsName() + "】,会员只能下单一次,不能重复参与!");
                }

            }

        }


        //计算商品总金额
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (LitemallCart checkGoods : checkedGoodsList) {
            checkedGoodsPrice = checkedGoodsPrice.add(checkGoods.getPrice().multiply(new BigDecimal(checkGoods.getNumber())));
        }
        // 获取可用的优惠券信息
        // 使用优惠券减免的金额
        BigDecimal couponPrice = new BigDecimal(0.00);
        // 如果couponId=0则没有优惠券，couponId=-1则不使用优惠券
        if (coupon != null) {
            couponPrice =new BigDecimal(coupon.getMaxAmt());
        }

        // 订单费用 +运费-优惠券-积分
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        // 最终支付费用
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        //开始创建订单
        Integer orderId = null;

        LitemallUser orderUser= userService.findById(userId);

        order.setIntegralPrice(integralPrice);
        order.setIntegralBonus(integralBonus);
        order.setUserId(userId);
        order.setUserName(orderUser.getUsername());
        order.setUserNickname(orderUser.getNickname());
        order.setUserPhone(orderUser.getMobile());
        order.setCrmId(orderUser.getCrmId());
        order.setCrmName(orderUser.getCrmName());

        order.setOrderSn(orderSn);
        //如果是虚拟商品则跳到已经收货确认状态，应该是支付完毕后，再修改成已经收货确认状态 ,
        // 如果支付金额为0，则全部使用积分购买，则判断是虚拟商品时会自动确认收货状态
        if(null!=ifXuni&&true==ifXuni&&actualPrice.equals(BigDecimal.valueOf(0))){
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setOrderStatusName(OrderUtil.STATUS_AUTO_CONFIRM_NAME);
            order.setPayMethod(3);
            order.setPayMethodName("0支付");
        } else {
            order.setOrderStatus(OrderUtil.STATUS_CREATE);
            order.setOrderStatusName(OrderUtil.STATUS_CREATE_NAME);
            //如果支付金额为0，则直接修改成已经支付，待备货状态
            if(actualPrice.doubleValue()==0){
                order.setOrderStatus(OrderUtil.STATUS_PAY);
                order.setOrderStatusName(OrderUtil.STATUS_PAY_NAME);
                order.setPayMethod(3);
                order.setPayMethodName("0支付");
            }
        }

        if(!orderStructData.getSendWay().equals("自提取货")) {
            if (checkedAddress != null){
                order.setConsignee(checkedAddress.getName());
                order.setMobile(checkedAddress.getTel());
                String detailedAddress = checkedAddress.getProvinceName() + checkedAddress.getCityName() +
                        checkedAddress.getCountryName() + " " + checkedAddress.getAddressDetail()+
                        checkedAddress.getArea();
                order.setAddress(detailedAddress);
            }
        }
        order.setMessage(message);

        order.setGoodsPrice(checkedGoodsPrice);
        //此处运费由前端计算好传入到后台，这样能够让客户感觉到一致
        // 根据订单商品总价计算运费，满足条件（例如199元）则免运费，否则需要支付运费（例如8元）；
        // 除此之外，市内10元，外省18元
        // 如果是秒杀、团购、会员专属活动则依据活动规则计算是否免运费
        order.setFreightPrice(freightPrice);
        order.setCouponPrice(couponPrice);
        order.setIntegralPrice(integralPrice);
        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);

        order.setPickSiteId(orderStructData.getPickSiteId());
        order.setPickMobile(orderStructData.getPickMobile());
        order.setPickPerson(orderStructData.getPickPerson());
        order.setPickSiteName(orderStructData.getPickSiteName());
        order.setSendWay(orderStructData.getSendWay());
        if(orderStructData.getSendWay().equals("快递")){
            order.setPickSiteId(null);
            order.setPickSiteName(null);
            order.setPickTime(null);
        }
        if(orderStructData.getPickTime()!=null) {
            order.setPickTime(DateUtil.getLocalDateTimeFromStringHMS(orderStructData.getPickTime() + ":00.000"));
        }
        order.setMallOrderTypeCode(mallOrderTypeCode);



        //设置订单的其它属性 需要根据传入条件来判断，这里先默认是电商订单
        //订单类型 10-电商订单 20-首都预约单 30-大兴预约单
        order.setTypeCode("10");
        order.setTypeName("电商订单");
        //订单来源 10-商贸公司 20-大兴PC 来自于大兴PC端的订单通过后台接口对接，因此此参数一定是10
        order.setSourceCode("10");
        order.setSourceName("商贸公司");

        // 有团购活动 此处未使用，设置为0
        order.setGrouponPrice(new BigDecimal(0.00));    //  团购价格

        Map<String, Object> data;
		try {
			// 添加订单表项 开始创建订单
			orderService.add(order);
			orderId = order.getId();

			// 添加订单商品表项
			for (LitemallCart cartGoods : checkedGoodsList) {
			    // 订单商品
				LitemallOrderGoods orderGoods = new LitemallOrderGoods();
			    orderGoods.setOrderId(order.getId());
			    orderGoods.setGoodsId(cartGoods.getGoodsId());
			    orderGoods.setGoodsPosKey(cartGoods.getGoodsPosKey());
			    orderGoods.setGoodsSn(cartGoods.getGoodsSn());
			    orderGoods.setProductId(cartGoods.getProductId());
			    orderGoods.setGoodsName(cartGoods.getGoodsName());
			    orderGoods.setPicUrl(cartGoods.getPicUrl());
			    orderGoods.setPrice(cartGoods.getPrice());
			    orderGoods.setNumber(cartGoods.getNumber());
                orderGoods.setUserGoodsRuleId(cartGoods.getUserGoodsRuleId());
                orderGoods.setSecRuleId(cartGoods.getSecRuleId());
                orderGoods.setGrouponRuleId(cartGoods.getGrouponRuleId());
                orderGoods.setRebateRuleId(cartGoods.getRebateRuleId());
			    orderGoods.setSpecifications(cartGoods.getSpecifications());
			    orderGoods.setAddTime(LocalDateTime.now());
			    orderGoodsList.add(orderGoods);
			}

			if(StringUtil.isNotEmpty(cartIds)){
			    // 删除购物车里面的商品信息
				cartService.clearCheckGoods(userId,comId);
			}
			//添加订单明细
            //订单明细中是否包含会员活动商品
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                orderGoodsService.add(orderGoods);
                if(orderGoods.getUserGoodsRuleId()!=null){
                    //如果是会员商品单，向会员商品规则订单里增加记录
                    LitemallPromotionGoodsUserOrder promotionGoodsUserOrder=new LitemallPromotionGoodsUserOrder();
                    promotionGoodsUserOrder.setOrderId(order.getId());
                    promotionGoodsUserOrder.setRulesId(orderGoods.getUserGoodsRuleId());
                    promotionGoodsUserOrder.setProductId(orderGoods.getProductId());
                    promotionGoodsUserOrder.setUserId(userId);
                    promotionGoodsUserOrderService.createUserGoodsRuleOrder(promotionGoodsUserOrder);
                }
                if(orderGoods.getRebateRuleId()!=null){
                    //如果有品项折扣活动商品，则填加品项折扣活动商品订单
                    LitemallPromotionGoodsRebateOrderGoods rebateOrderGoods=new LitemallPromotionGoodsRebateOrderGoods();
                    rebateOrderGoods.setOrderId(order.getId());
                    rebateOrderGoods.setRuleId(orderGoods.getRebateRuleId());
                    rebateOrderGoods.setProductId(orderGoods.getProductId());
                    rebateOrderGoods.setUserId(userId);
                    goodsRebateOrderService.createGoodsRebateOrderGoods(rebateOrderGoods);
                }
                //如果是秒杀单 向秒杀活动订单里增加记录
                if(orderGoods.getSecRuleId()!=null){
//                if(mallOrderTypeCode.equals("20")){
                    LitemallPromotionSeckillOrder promotionSeckillOrder=new LitemallPromotionSeckillOrder();
                    promotionSeckillOrder.setOrderId(order.getId());
                    promotionSeckillOrder.setRulesId(orderGoods.getSecRuleId());
                    promotionSeckillOrder.setUserId(userId);
                    promotionSeckillOrderService.createSeckill(promotionSeckillOrder);
                }
                //如果是团购单，向团购活动订单里增加团购记录
                if(orderGoods.getGrouponRuleId()!=null){
//                if(mallOrderTypeCode.equals("30")){
                    LitemallGrouponOrder litemallGrouponOrder=new LitemallGrouponOrder();
                    litemallGrouponOrder.setOrderId(order.getId());
                    litemallGrouponOrder.setRulesId(orderGoods.getGrouponRuleId());
                    litemallGrouponOrder.setUserId(userId);
                    grouponOrderService.createGroupon(litemallGrouponOrder);
                }
            }
			// 商品货品数量减少

            //此处更新库存
            Boolean result=  orderGoodsService.SubstractStorNum(order.getId(),order.getTypeCode(),null,null);
            if(!result){
                return ResponseUtil.fail(502,"后台出现错误");
            }

			// 如果使用了优惠券，设置优惠券使用状态
			if (coupon != null && coupon.getTicketId()!=null) {
				LitemallOrderTickets orderTickets = new LitemallOrderTickets();
				orderTickets.setOrderId(order.getId());
				orderTickets.setTicketId(coupon.getTicketId());
				orderTickets.setTicketDesci(coupon.getTicketDesci());
				orderTickets.setSerialId(coupon.getSerialId());
				orderTickets.setTicketType(coupon.getTicketType());
				orderTickets.setMinAmt(new BigDecimal(coupon.getMinAmt()));
				orderTickets.setMaxAmt(new BigDecimal(coupon.getMaxAmt()));
				orderTickets.setIncreaseAmt(new BigDecimal(coupon.getIncreaseAmt()));
				orderTickets.setDiscount(new BigDecimal(coupon.getDiscount()));;
				orderTickets.setEffectDate(coupon.getEffectDate());
				orderTickets.setExpiryDate(coupon.getExpiryDate());
				orderTickets.setStatus(1);
				orderTicketsService.add(orderTickets);
			}


			//如果设置了发票信息
			if(orderFapiao!=null){
                orderFapiao.setOrderId(order.getId());
                orderFapiao.setMoney(order.getActualPrice());
                orderFapiaoService.add(orderFapiao);
                //更新订单状态为有发票
                order.setIfFapiao(true);
                order.setFapiaoStatus("未开");
                orderService.update(order);
            }

			data = new HashMap<>();
			data.put("orderId", orderId);
			data.put("pay", order.getActualPrice());
			data.put("orderSn", order.getOrderSn());
		} catch (Exception e) {
		      System.out.println(e.getMessage());
			 //shouduRemoteCrm.unHexiaoJiFen(user.getCrmId(),orderSn,order.getOrderPrice().doubleValue());
             System.out.println(e.getMessage());
             //删除订单
             if(order!=null && order.getId()!=null){
            	 orderService.deleteById(order.getId());
             }

			 return ResponseUtil.fail(502, "创建订单失败");
		}
        return ResponseUtil.ok(data);
    }



    /**
     * 校验提货时间
     * @param orderId
     * @param pickupSiteId
     * @param tiHuoTime
     * @return
     */
    public String verifyTihuoTime(Integer orderId,Integer pickupSiteId, String tiHuoTime){
        LocalDateTime localTihuoTime=  DateUtil.getLocalDateTimeFromStringHHMM(tiHuoTime);
        if(localTihuoTime==null){
            return "提货时间格式错误,请设置成yyyy-MM-dd HH:mm格式";
        }
        if(pickupSiteId==null||pickupSiteId==0){
            return "必须选择提货点";
        }
        //当前时间
        LocalDateTime currentDateTime=null;
        if(orderId==null){
            currentDateTime=LocalDateTime.now();
        } else
        {
            LitemallOrder litemallOrder=orderService.findById(orderId);
            currentDateTime=litemallOrder.getAddTime();
        }
        //判断自提货时间是否大于12小时
        Long diffHour=Duration.between(currentDateTime,localTihuoTime).toHours()+1;
        if(Math.abs(diffHour)<SystemConfig.getOrderTihuoBeihuoHour()){
            return "为了店面备货时间，订单提货时间必须要大于"+SystemConfig.getOrderTihuoBeihuoHour()+"小时";
        }
        Long diffDays=Duration.between(currentDateTime,localTihuoTime).toDays();
        if(Math.abs(diffDays)>SystemConfig.getTihuoMaxDays()){
            return "只能提货"+SystemConfig.getTihuoMaxDays()+"天以内,超出无效";
        }
        //判断店铺预约规则，是否在预约期间范围内，是否在工作时间段内
        if(pickupSiteId!=null){
            LitemallPickSite pickSite= litemallPickSiteService.query(pickupSiteId);
            int dayOfWeekIndex= localTihuoTime.getDayOfWeek().getValue();
            String dayOfWeekName="";
            if(dayOfWeekIndex==1){
                dayOfWeekName="周一";
            } else  if(dayOfWeekIndex==2){
                dayOfWeekName="周二";
            }else  if(dayOfWeekIndex==3){
                dayOfWeekName="周三";
            }else  if(dayOfWeekIndex==4){
                dayOfWeekName="周四";
            }else  if(dayOfWeekIndex==5){
                dayOfWeekName="周五";
            }else  if(dayOfWeekIndex==6){
                dayOfWeekName="周六";
            }else  if(dayOfWeekIndex==7){
                dayOfWeekName="周日";
            }
            if(pickSite!=null){
                //判断当前日期是周几
                String[] weeksIn=pickSite.getSitePickWeek();

                if(weeksIn!=null&&weeksIn.length>0) {
                    boolean haveFind=false;
                    for (String week : weeksIn) {
                        if(week.equals(dayOfWeekName)){
                            haveFind=true;
                            break;
                        }
                    }
                    if(haveFind==false){
                        return "店铺在约定日期【"+dayOfWeekName+"】不能提供服务";
                    }
                }
                //判断一下时间范围
                String strRightTime=tiHuoTime.substring(10);
                String[] timesIn=pickSite.getSitePickTime();
                if(timesIn!=null&&timesIn.length>0) {
                    if(strRightTime.trim().compareToIgnoreCase(timesIn[0])<0||strRightTime.trim().compareToIgnoreCase(timesIn[1].toString())>0)
                    {
                        return "店铺在【"+timesIn[0]+"】至【"+timesIn[1]+"】时间范围内提供预约服务，当前提货时间【"+strRightTime+"】不在此范围";
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取提货时间范围
     * @param orderId
     * @return
     */
    public ArrayList getIihuoTime(Integer orderId,String beginTime,String endTime){

        //当前时间
        LocalDateTime currentDateTime=null;
        if(orderId==null){
            currentDateTime=LocalDateTime.now();
        } else
        {
            LitemallOrder litemallOrder=orderService.findById(orderId);
            currentDateTime=litemallOrder.getAddTime();
        }


        //如果当前日期不在工作时间，则调整起始时间为工作时间起始时间
        String currentDateTimeStr=DateUtil.getDateString(currentDateTime);
        if(currentDateTimeStr.substring(11,16).compareToIgnoreCase(beginTime)<0){
            currentDateTimeStr=DateUtil.getDateString(currentDateTime).substring(0,10)+" "+beginTime;
        }
        if(currentDateTimeStr.substring(11,16).compareToIgnoreCase(endTime)>=0){
            currentDateTimeStr=DateUtil.getDateString(currentDateTime.plusDays(1)).substring(0,10)+" "+beginTime;
        }

        currentDateTime=DateUtil.getLocalDateTimeFromStringHHMM(currentDateTimeStr);
        //加上设置的时间间隔
        LocalDateTime startTime= currentDateTime.plusHours(SystemConfig.getOrderTihuoBeihuoHour());

        String startTimeStr= DateUtil.getDateString(startTime);

        //如果开始时间小于早上9点，则取早上9点再加上备货时间
        if(startTimeStr.substring(11,16).compareToIgnoreCase(beginTime)<0){
            startTimeStr=DateUtil.getDateString(startTime).substring(0,10)+" "+beginTime;
        }
        //如果开始时间大于17点，则取第二天9：00
        if(startTimeStr.substring(11,16).compareToIgnoreCase(endTime)>=0){
            startTimeStr=DateUtil.getDateString(startTime.plusDays(1)).substring(0,10)+" "+beginTime;
        }

        LocalDateTime tihuoMaxDays= currentDateTime.plusDays(SystemConfig.getTihuoMaxDays());
        //如果截至时间
        String endTimeStr=DateUtil.getDateString(tihuoMaxDays);
        //如果截止时间小于系统默认营业的截至时间，则取前一天的截至时间做为最终时间，如果截至时间大于默认的当天截至时间，则取增加一天后的起始时间
        if(endTimeStr.substring(11,16).compareToIgnoreCase(beginTime)<0){
            endTimeStr=DateUtil.getDateString(tihuoMaxDays).substring(0,10)+" "+beginTime;
        }
        if(endTimeStr.substring(11,16).compareToIgnoreCase(endTime)>=0){
            endTimeStr=DateUtil.getDateString(tihuoMaxDays.plusDays(1)).substring(0,10)+" "+beginTime;
        }
        ArrayList al=new ArrayList();

        al.add(startTimeStr);
        al.add(endTimeStr);

        return al;
    }


    /**
     * 取消订单
     * <p>
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * 4. TODO 优惠券；
     * 5. TODO 团购活动订单。
     *
     * @param userId 用户ID

     * @return 取消订单操作结果
     */
    @Transactional
    public Object cancel(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        LocalDateTime preUpdateTime = order.getUpdateTime();
        Integer yuyueComId = order.getYuyueComId();

        if(yuyueComId==null || yuyueComId==1){
        	//支付订单取消
        	// 检测是否能够取消
            OrderHandleOption handleOption = OrderUtil.build(order);
            if (!handleOption.isCancel()) {
                return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
            }
            // 设置订单已取消状态
            order.setOrderStatus(OrderUtil.STATUS_CANCEL);
            order.setOrderStatusName(OrderUtil.STATUS_CANCEL_NAME);
            //order.setCancelTime(LocalDateTime.now());
        }

        order.setEndTime(LocalDateTime.now());
        order.setCancelTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        //从秒杀里取消订单
        promotionSeckillOrderService.cancelOrderDelete(orderId);

        //从团购里取消订单
        grouponOrderService.cancelOrderDelete(orderId);

        //从会员里取消订单
        promotionGoodsUserOrderService.cancelOrderDelete(orderId);


        // 商品货品数量增加
       Boolean result= orderGoodsService.AddStorNum(orderId,order.getTypeCode(), null,null);
        if(!result){
            return ResponseUtil.fail(502,"后台出现错误");
        }

        return ResponseUtil.ok();
    }

    public Object topay(Integer userId,
                        Integer orderId,BigDecimal payMoney, HttpServletRequest request,HttpServletResponse response) {
        System.out.println("=====================参数bg=====================");
        System.out.println("orderPaySn:" + orderId);
        System.out.println("=====================参数ed=====================");
        try {
            if (userId == null) {
                return ResponseUtil.unlogin();
            }
//            Integer orderId = JacksonUtil.parseInteger(body, "orderId");
            if (orderId == null) {

                    return ResponseUtil.badArgument();

            }

            LitemallOrder order = orderService.findById(orderId);
            if (order == null) {
                return ResponseUtil.badArgumentValue();
            }
            if (!order.getUserId().equals(userId)) {
                return ResponseUtil.badArgumentValue();
            }

            // 检测是否能够取消
            OrderHandleOption handleOption = OrderUtil.build(order);
            if (!handleOption.isPay()) {
                return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
            }

            LitemallUser user = userService.findById(userId);
            String openid = user.getWeixinOpenid();
            if (openid == null) {
                return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "订单不能支付");
            }

            //总金额以分为单位，不带小数点
         // 元转成分
            int total_fee = 0;
            BigDecimal actualPrice = order.getActualPrice();
            total_fee = actualPrice.multiply(new BigDecimal(100)).intValue();

            //商户相关资料
            String appid = wxMaConfig.getAppid();
            String appsecret = wxMaConfig.getSecret();
            String partner = wxPayConfig.getMchId();
            String partnerkey = wxPayConfig.getMchKey();


            //获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
            String currTime = CommonTools.getCurrTime();
            //8位日期
            String strTime = currTime.substring(8, currTime.length());
            //四位随机数
            String strRandom = CommonTools.buildRandom(4) + "";
            //10位序列号,可以自行调整。
            String strReq = strTime + strRandom;

            //商户号
            String mch_id = partner;
            //子商户号  非必输
            //String sub_mch_id="";
            //随机数
            String nonce_str = strReq;
            //商品描述
            //String body = describe;



            //商品描述根据情况修改
            String body = order.getOrderSn();
            //附加数据
            String attach = orderId+"";
            //商户订单号
            String out_trade_no = order.getOrderSn();

            //订单生成的机器 IP
            String spbill_create_ip = request.getRemoteAddr();

            //这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
            String notify_url = wxPayConfig.getNotifyUrl();

//          orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            String trade_type = "JSAPI";
            //非必输
            //              String product_id = "";
            SortedMap<String, String> packageParams = new TreeMap<String, String>();
            packageParams.put("appid", appid);
            packageParams.put("mch_id", mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("attach", attach);
            packageParams.put("out_trade_no", out_trade_no);

            //这里写的金额为1 分到时修改
            packageParams.put("total_fee", total_fee + "");
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", notify_url);

            packageParams.put("trade_type", trade_type);
            packageParams.put("openid", openid);

            RequestHandler reqHandler = new RequestHandler(request, response);
            reqHandler.init(appid, appsecret, partnerkey);

            String sign = reqHandler.createSign(packageParams);
            String xml = "<xml>" +
            			 "<appid>" + appid + "</appid>" +
            			 "<mch_id>" + mch_id + "</mch_id>" +
            			 "<nonce_str>" + nonce_str + "</nonce_str>" +
            			 "<sign>" + sign + "</sign>" +
            			 "<body><![CDATA[" + body + "]]></body>" +
            			 "<attach>" + attach + "</attach>" +
            			 "<out_trade_no>" + out_trade_no + "</out_trade_no>" +
            			 "<total_fee>" + total_fee + "</total_fee>" +
            			 "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" +
            			 "<notify_url>" + notify_url + "</notify_url>" +
            			 "<trade_type>" + trade_type + "</trade_type>" +
            			 "<openid>" + openid + "</openid>" +
            			 "</xml>";

            System.out.println("sign:"+sign);
            System.out.println(xml);
            String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            String prepay_id = "";
            prepay_id = WXPreutil.getPayNo(createOrderURL, xml);

            SortedMap<String, String> finalpackage = new TreeMap<String, String>();
            String appid2 = appid;
            String timestamp = CommonTools.getTimeStamp();
            String nonceStr2 = nonce_str;
            String prepay_id2 = "prepay_id=" + prepay_id;
            String packages = prepay_id2;
            finalpackage.put("appId", appid2);
            finalpackage.put("timeStamp", timestamp);
            finalpackage.put("nonceStr", nonceStr2);
            finalpackage.put("package", packages);
            finalpackage.put("signType", "MD5");
            String finalsign = reqHandler.createSign(finalpackage);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("appid", appid2);
            dataMap.put("timeStamp", timestamp);
            dataMap.put("nonceStr", nonceStr2);
            dataMap.put("package", packages);
            dataMap.put("sign", finalsign);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.badArgumentValue();
        }
    }

    /**
     *
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId 用户ID

     * @return 支付订单ID
     */
    @Transactional
    public Object prepay(Integer userId, Integer orderId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
        }

        LitemallUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
//            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "订单不能支付");
        	openid="oquTFjujqFoPfbjYVt0EgB1im4pM";
        }
        WxPayMpOrderResult result = null;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(order.getOrderSn());
            orderRequest.setOpenid(openid);
            orderRequest.setBody("订单：" + order.getOrderSn());
            // 元转成分
            int fee = 0;
            BigDecimal actualPrice = order.getActualPrice();
            fee = actualPrice.multiply(new BigDecimal(100)).intValue();
            orderRequest.setTotalFee(fee);
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));

            result = wxPayService.createOrder(orderRequest);

            //缓存prepayID用于后续模版通知
            String prepayId = result.getPackageValue();
            prepayId = prepayId.replace("prepay_id=", "");
            LitemallUserFormid userFormid = new LitemallUserFormid();
            userFormid.setOpenid(user.getWeixinOpenid());
            userFormid.setFormid(prepayId);
            userFormid.setIsprepay(true);
            userFormid.setUseamount(3);
            userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
            formIdService.addUserFormid(userFormid);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok(result);
    }

    /**
     *
     * <p>
     * 1. 客户储值，传入的订单号为空，则后台重新生

     *
     * @param userId 用户ID

     * @return 支付订单ID
     */
    @Transactional
    public Object wxChargeMoney(Integer userId, BigDecimal payMoney, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (payMoney == null) {
            return ResponseUtil.badArgument();
        }
        //系统生成一个支付单号，为了和普通订单支付单号区别，前面加一个首字母C,代表充值的意思
        String orderSn="C"+orderService.generateOrderSn(userId);

        LitemallUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
//            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "订单不能支付");
            openid="oquTFjujqFoPfbjYVt0EgB1im4pM";
        }
        WxPayMpOrderResult result = null;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(orderSn);
            orderRequest.setOpenid(openid);
            orderRequest.setBody("订单：" + orderSn);
            // 元转成分
            int fee = 0;
            BigDecimal actualPrice = payMoney;
            fee = actualPrice.multiply(new BigDecimal(100)).intValue();
            orderRequest.setTotalFee(fee);
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));

            result = wxPayService.createOrder(orderRequest);



            //缓存prepayID用于后续模版通知
            String prepayId = result.getPackageValue();
            prepayId = prepayId.replace("prepay_id=", "");
            LitemallUserFormid userFormid = new LitemallUserFormid();
            userFormid.setOpenid(user.getWeixinOpenid());
            userFormid.setFormid(prepayId);
            userFormid.setIsprepay(true);
            userFormid.setUseamount(3);
            userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
            formIdService.addUserFormid(userFormid);

            //向用户充值表中增加一条记录
            LitemallUserChargeMoney userChargeMoney=new LitemallUserChargeMoney();
            userChargeMoney.setUserId(userId);
            userChargeMoney.setChargeTime(LocalDateTime.now());
            userChargeMoney.setChargeMoney(payMoney);
            userChargeMoney.setPayMethod("2");
            userChargeMoney.setPayMethodName("微信");
            userChargeMoney.setChargeSheetNo(orderSn);
            userChargeMoneyService.create(userChargeMoney);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }

        return ResponseUtil.ok(result);
    }


    /**
     * 支付宝付款订单的预支付会话标识
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId 用户ID

     * @return 支付订单ID
     */
    @Transactional
    public Object aliPrepay(Integer userId, Integer orderId,BigDecimal payMoney, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            if(payMoney==null) {
                return ResponseUtil.badArgument();
            } else
            {
                return aliChargeMoney(userId,payMoney,request);
            }
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
        }

        LitemallUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "订单不能支付");
        }
        StringBuffer bizContent=new StringBuffer();
        try {
        	bizContent.append("{");
        	bizContent.append(" \"out_trade_no\":"+"\""+order.getOrderSn()+"\",");

             BigDecimal actualPrice = order.getActualPrice();

        	bizContent.append(" \"total_amount\":"+"\""+actualPrice.toString()+"\",");
        	bizContent.append("\"subject\":\"订单支付\",");
        	bizContent.append("\"product_code\":\"QUICK_WAP_WAY\"");
        	bizContent.append("}");


		} catch (Exception e) {
			bizContent=null;
		}

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok(bizContent);
    }

    /**
     * 支付宝付款订单的预支付会话标识
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId 用户ID

     * @return 支付订单ID
     */
    @Transactional
    public Object aliChargeMoney(Integer userId, BigDecimal payMoney, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (payMoney == null) {
            return ResponseUtil.badArgument();
        }
        //系统生成一个支付单号，为了和普通订单支付单号区别，前面加一个首字母C,代表充值的意思
        String orderSn="C"+orderService.generateOrderSn(userId);


        LitemallUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "没有发现用户的OpenId,订单不能支付");
        }
        StringBuffer bizContent=new StringBuffer();
        try {
            bizContent.append("{");
            bizContent.append(" \"out_trade_no\":"+"\""+orderSn+"\",");

            BigDecimal actualPrice = payMoney;

            bizContent.append(" \"total_amount\":"+"\""+actualPrice.toString()+"\",");
            bizContent.append("\"subject\":\"订单支付\",");
            bizContent.append("\"product_code\":\"QUICK_WAP_WAY\"");
            bizContent.append("}");


        } catch (Exception e) {
            bizContent=null;
        }

        //向用户充值表中增加一条记录
        LitemallUserChargeMoney userChargeMoney=new LitemallUserChargeMoney();
        userChargeMoney.setUserId(userId);
        userChargeMoney.setChargeTime(LocalDateTime.now());
        userChargeMoney.setChargeMoney(payMoney);
        userChargeMoney.setPayMethod("1");
        userChargeMoney.setPayMethodName("支付宝");
        userChargeMoney.setChargeSheetNo(orderSn);
        userChargeMoneyService.create(userChargeMoney);

        return ResponseUtil.ok(bizContent);
    }


    /**
     * 微信付款成功或失败回调接口
     * <p>
     * 1. 检测当前订单是否是付款状态;
     * 2. 设置订单付款成功状态相关信息;
     * 3. 响应微信商户平台.
     *
     * @param request  请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @Transactional
    public Object payNotify(HttpServletRequest request, HttpServletResponse response) {
        String xmlResult = null;
        try {
            xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        } catch (IOException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        WxPayOrderNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyResult(xmlResult);

            if(!WxPayConstants.ResultCode.SUCCESS.equals(result.getResultCode())){
                logger.error(xmlResult);
                throw new WxPayException("微信通知支付失败！");
            }
            if(!WxPayConstants.ResultCode.SUCCESS.equals(result.getReturnCode())){
                logger.error(xmlResult);
                throw new WxPayException("微信通知支付失败！");
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        logger.info("处理腾讯支付平台的订单支付");
        logger.info(result);

        String orderSn = result.getOutTradeNo();
        String payId = result.getTransactionId();

        // 分转化成元
        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
        //如果是充值订单，则走充值处理过程，如果是普通订单支付，则走普通订单处理后续流程
        if(orderSn.startsWith("C")){
            //处理充值卡
            return operatCharge(orderSn, payId, totalFee, result.getOpenid());
        } else {
            //处理支付订单
            return operatOrder(orderSn, payId, totalFee, result.getOpenid());
        }
    }

    /**
     * 普通订单处理后续流程
     * @param orderSn
     * @param payId
     * @param totalFee
     * @param openid
     * @return
     */
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
         if(order.getMallOrderIfXuni()==true||order.getMallOrderIfTicket()==true){
             order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
             order.setOrderStatusName(OrderUtil.STATUS_AUTO_CONFIRM_NAME);
         } else {
             order.setOrderStatus(OrderUtil.STATUS_PAY);
             order.setOrderStatusName(OrderUtil.STATUS_PAY_NAME);
         }
         order.setPayMethod(2);
         order.setPayMethodName("微信");
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

         //TODO 发送邮件和短信通知，这里采用异步发送
         // 订单支付成功以后，会发送短信给用户，以及发送邮件给管理员
         notifyService.notifyMail("新订单通知", order.toString());
         // 这里微信的短信平台对参数长度有限制，所以将订单号只截取后6位
         //notifyService.notifySmsTemplateSync(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});
//       notifyService.notifySmsTemplate(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});

         Map<String, Object> valueMap=new HashMap<>();
         valueMap.put("orderSn",order.getOrderSn());
         notifyService.notifySmsTemplate(order.getMobile(), NotifyType.PAY_SUCCEED, valueMap);


         // 请依据自己的模版消息配置更改参数
         String[] parms = new String[]{
                 order.getOrderSn(),
                 order.getOrderPrice().toString(),
                 DateTimeUtil.getDateTimeDisplayString(order.getAddTime()),
                 order.getConsignee(),
                 order.getMobile(),
                 order.getAddress()
         };

         notifyService.notifyWxTemplate(openid, NotifyType.PAY_SUCCEED, parms, "pages/index/index?orderId=" + order.getId());

         //此处向消息表中写入订单创建信息
         //TODO 这里发邮件或发站内消息给相应的操作人员
         LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
         opadminInfo.setTypeName("订单创建");
         opadminInfo.setTitle("电商订单创建");
         opadminInfo.setSourceCode(order.getSourceCode());
         opadminInfo.setSourceName(order.getSourceName());
         Map<String,Object> parameters=new HashMap<>();
         parameters.put("orderSn",order.getOrderSn());
         parameters.put("orderAddTime",order.getAddTime());
         parameters.put("orderConsignee",order.getConsignee());
         parameters.put("orderAddress",order.getAddress());
         parameters.put("orderMobile",order.getMobile());
         parameters.put("orderPrice",order.getOrderPrice());
         opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

         return WxPayNotifyResponse.success("处理成功!");
    }

    /**
     * 充值订单处理后续流程
     * @param orderSn
     * @param payId
     * @param totalFee
     * @param openid
     * @return
     */
    private Object operatCharge(String orderSn,String payId,String totalFee,String openid){

        LitemallUserChargeMoney userChargeMoney = userChargeMoneyService.queryByChargeSheetNo(orderSn);
        if (userChargeMoney == null) {
            return WxPayNotifyResponse.fail("储值记录未写入 sn=" + orderSn);
        }
        userChargeMoney.setPayNo(payId);
        userChargeMoney.setPaySuccess(true);
        userChargeMoney.setPayTime(LocalDateTime.now());
        userChargeMoneyService.updateById(userChargeMoney);


        //TODO 发送邮件和短信通知，这里采用异步发送
        // 订单支付成功以后，会发送短信给用户，以及发送邮件给管理员
        // 这里微信的短信平台对参数长度有限制，所以将订单号只截取后6位
        //notifyService.notifySmsTemplateSync(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});
//       notifyService.notifySmsTemplate(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});


        // 请依据自己的模版消息配置更改参数
//        String[] parms = new String[]{
//                userChargeMoney.getChargeSheetNo(),
//                userChargeMoney.getChargeMoney().toString(),
//                DateTimeUtil.getDateTimeDisplayString(userChargeMoney.getAddTime()),
//                user.getNickname(),
//                user.getMobile(),
//                ""
//        };
//
//        notifyService.notifyWxTemplate(openid, NotifyType.PAY_SUCCEED, parms, "pages/index/index?orderId=" + userChargeMoney.getId());


        //此处向消息表中写入订单创建信息
        //TODO 这里发邮件或发站内消息给相应的操作人员
//        LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
//        opadminInfo.setTypeName("充值单创建");
//        opadminInfo.setTitle("充值单创建");
//        opadminInfo.setSourceCode(order.getSourceCode());
//        opadminInfo.setSourceName(order.getSourceName());
//        Map<String,Object> parameters=new HashMap<>();
//        parameters.put("orderSn",order.getOrderSn());
//        parameters.put("orderAddTime",order.getAddTime());
//        parameters.put("orderConsignee",order.getConsignee());
//        parameters.put("orderAddress",order.getAddress());
//        parameters.put("orderMobile",order.getMobile());
//        parameters.put("orderPrice",order.getOrderPrice());
//        opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

        return WxPayNotifyResponse.success("处理成功!");
    }


    /**
     * 微信支付结果查询方法-支付成功返回false,未支付返回true（判断订单是否可以调用统一下单接口）
     * @param orderSn
     * @return true:微信已经支付 false:微信未支付
     */
    public Boolean wxQueryPayResult(String orderSn){

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


		//TODO 发送邮件和短信通知，这里采用异步发送
		// 订单支付成功以后，会发送短信给用户，以及发送邮件给管理员
		notifyService.notifyMail("新订单通知", order.toString());
		// 这里微信的短信平台对参数长度有限制，所以将订单号只截取后6位
//		notifyService.notifySmsTemplateSync(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{ordersn.substring(8, 14)});
//      notifyService.notifySmsTemplate(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});

        Map<String, Object> valueMap=new HashMap<>();
        valueMap.put("orderSn",order.getOrderSn());
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.PAY_SUCCEED, valueMap);

		// 请依据自己的模版消息配置更改参数
		String[] parms = new String[]{
		        order.getOrderSn(),
		        order.getOrderPrice().toString(),
		        DateTimeUtil.getDateTimeDisplayString(order.getAddTime()),
		        order.getConsignee(),
		        order.getMobile(),
		        order.getAddress()
		};

//    	        notifyService.notifyWxTemplate(.getOpenid(), NotifyType.PAY_SUCCEED, parms, "pages/index/index?orderId=" + order.getId());

        //此处向消息表中写入订单创建信息
        //TODO 这里发邮件或发站内消息给相应的操作人员
        LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
        opadminInfo.setTypeName("订单创建");
        opadminInfo.setTitle("电商订单创建");
        opadminInfo.setSourceCode(order.getSourceCode());
        opadminInfo.setSourceName(order.getSourceName());
        Map<String,Object> parameters=new HashMap<>();
        parameters.put("orderSn",order.getOrderSn());
        parameters.put("orderAddTime",order.getAddTime());
        parameters.put("orderConsignee",order.getConsignee());
        parameters.put("orderAddress",order.getAddress());
        parameters.put("orderMobile",order.getMobile());
        parameters.put("orderPrice",order.getOrderPrice());
        opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

	}

    /**
     * 订单申请退款
     * <p>
     * 1. 检测当前订单是否能够退款；
     * 2. 设置订单申请退款状态。
     *
     * @param userId 用户ID

     * @return 订单退款操作结果
     */
    public Object refund(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

//      Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.fail(502,"订单Id不能为空");
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.fail(502,"订单不能为空");

        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail(502,"用户Id不能为空");
        }
        //前台既然已经能够发起退款，则此地不再进行判断
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isRefund()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能退款");
        }

        if(order.getMallOrderIfXuni()==true&&!order.getAdminAllowRefund()){
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "虚拟商品一经购买,不允许取消");
        }
        if(order.getMallOrderIfTicket()==true&&!order.getAdminAllowRefund()){
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "优惠券一经购买,不允许取消");
        }

        // 设置订单申请退款状态
        //先备份当前的订单状态
        order.setOrderStatusRefund(order.getOrderStatus());

        order.setOrderStatus(OrderUtil.STATUS_REFUND);
        order.setOrderStatusName(OrderUtil.STATUS_REFUND_NAME);
        order.setReturnTime(LocalDateTime.now());

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();

        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 有用户申请退款，邮件通知运营人员
        notifyService.notifyMail("退款申请", order.toString());

        //此处向消息表中写入订单创建信息
        //TODO 这里发邮件或发站内消息给相应的操作人员
        LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
        opadminInfo.setTypeName("申请退款");
        opadminInfo.setTitle("申请退款");
        opadminInfo.setSourceCode(order.getSourceCode());
        opadminInfo.setSourceName(order.getSourceName());
        Map<String,Object> parameters=new HashMap<>();
        parameters.put("orderSn",order.getOrderSn());
        parameters.put("orderAddTime",order.getAddTime());
        parameters.put("orderReturnReason",order.getReturnReason());
        opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

        return ResponseUtil.ok();
    }


    /**
     * 确认收货
     * <p>
     * 1. 检测当前订单是否能够确认收货；
     * 2. 设置订单确认收货状态。
     *
     * @param userId 用户ID

     * @return 订单操作结果
     */
    public Object confirm(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能确认收货");
        }

        Short comments = orderGoodsService.getComments(orderId);
        order.setComments(comments);

        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setOrderStatusName(OrderUtil.STATUS_CONFIRM_NAME);
        order.setConfirmTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除订单
     * <p>
     * 1. 检测当前订单是否可以删除；
     * 2. 删除订单。
     *
     * @param userId 用户ID

     * @return 订单操作结果
     */
    public Object delete(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isDelete()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能删除");
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的delete字段表示是否删除
        orderService.deleteById(orderId);

        return ResponseUtil.ok();
    }

    /**
     * 待评价订单商品信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param goodsId 商品ID
     * @return 待评价订单商品信息
     */
    public Object goods(Integer userId, Integer orderId, Integer goodsId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
        int size = orderGoodsList.size();

        Assert.state(size < 2, "存在多个符合条件的订单商品");

        if (size == 0) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrderGoods orderGoods = orderGoodsList.get(0);
        return ResponseUtil.ok(orderGoods);
    }

    /**
     * 评价订单商品
     * <p>
     * 确认商品收货或者系统自动确认商品收货后7天内可以评价，过期不能评价。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object comment(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Integer orderGoodsId = JacksonUtil.parseInteger(body, "orderGoodsId");
        if (orderGoodsId == null) {
            return ResponseUtil.badArgument();
        }
        LitemallOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
        if (orderGoods == null) {
            return ResponseUtil.badArgumentValue();
        }
        Integer orderId = orderGoods.getOrderId();
        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        Short orderStatus = order.getOrderStatus();
        if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "当前商品不能评价");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail(ORDER_INVALID, "当前商品不属于用户");
        }
        Integer commentId = orderGoods.getComment();
        if (commentId == -1) {
            return ResponseUtil.fail(ORDER_COMMENT_EXPIRED, "当前商品评价时间已经过期");
        }
        if (commentId != 0) {
            return ResponseUtil.fail(ORDER_COMMENTED, "订单商品已评价");
        }

        String content = JacksonUtil.parseString(body, "content");
        Integer star = JacksonUtil.parseInteger(body, "star");
        if (star == null || star < 0 || star > 5) {
            return ResponseUtil.badArgumentValue();
        }
        Boolean hasPicture = JacksonUtil.parseBoolean(body, "hasPicture");
        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
        if (hasPicture == null || !hasPicture) {
            picUrls = new ArrayList<>(0);
        }

        // 1. 创建评价
        LitemallComment comment = new LitemallComment();
        comment.setUserId(userId);
        comment.setType((byte) 0);
        comment.setValueId(orderGoods.getGoodsId());
        comment.setStar(star.shortValue());
        comment.setContent(content);
        comment.setHasPicture(hasPicture);
        comment.setPicUrls(picUrls.toArray(new String[]{}));
        commentService.save(comment);

        // 2. 更新订单商品的评价列表
        orderGoods.setComment(comment.getId());
        orderGoodsService.updateById(orderGoods);

        // 3. 更新订单中未评价的订单商品可评价数量
        Short commentCount = order.getComments();
        if (commentCount > 0) {
            commentCount--;
        }
        order.setComments(commentCount);
        orderService.updateWithOptimisticLocker(order);

        return ResponseUtil.ok();
    }

    //查询除已经支付以及退货状态外的所有订单
    public List<LitemallOrder> allListWithoutPayed() {

        List<LitemallOrder> orderList;

        List<Short> orderStatus = OrderUtil.withoutPayed();
        orderList = orderService.queryByOrderStatus(orderStatus);

        return orderList;
    }
    /**
     * 当用户在创建订单之前，可以查询后台该用户可用的优惠券信息
     * orderMoney，不包含运费
     */
    public List<UserTicket> getOrderTicketsList(Integer userId,BigDecimal orderMoney){
        List<ViewUserTickets> ticketsList= couponUserService.getAvailableTicketsList(userId,orderMoney);
        //转换成系统定义好的结构，并且返回
        List<UserTicket> userTickets=convertTicketsToList(userId, ticketsList);
        return userTickets;
    }
    /**
     * ；转换会员的优惠券信息
     * @param ticketList
     * @return
     */
    private List<UserTicket> convertTicketsToList(Integer userId , List<ViewUserTickets> ticketList){
        LitemallUser litemallUser=userService.findById(userId);
        List<UserTicket> result=new ArrayList<>();
        if(ticketList!=null){
            for(ViewUserTickets ticket: ticketList){
                //此处还需要增加一个判断券使用门槛判断
                UserTicket userTicket=new UserTicket();
                userTicket.setUserId(litemallUser.getId());
                userTicket.setVipCode(litemallUser.getCrmId());
                userTicket.setOpenId(litemallUser.getWeixinOpenid());
                userTicket.setMobile(litemallUser.getMobile());
                //userTicket.setTicketId(CharUtil.objectConverToString(jsonObject.get("TicketId")));
                userTicket.setTicketId(ticket.getCouponId().toString());
                //userTicket.setTicketDesci(CharUtil.objectConverToString(jsonObject.get("TicketDesci")));
                userTicket.setTicketDesci(ticket.getDesc());
                //userTicket.setSerialId(CharUtil.objectConverToString(jsonObject.get("SerialId")));
                userTicket.setSerialId(ticket.getId().toString());
                //userTicket.setTicketType(CharUtil.objectConverToString(jsonObject.get("TicketType")));
                userTicket.setTicketType(ticket.getType().toString());
                //userTicket.setMinAmt(CharUtil.objectConverToDouble(jsonObject.get("MinAmt")));
                userTicket.setMinAmt(ticket.getMin().doubleValue());
                //userTicket.setIncreaseAmt(CharUtil.objectConverToDouble(jsonObject.get("IncreaseAmt")));
                //优惠券只用一次
                userTicket.setIncreaseAmt(null);

                //userTicket.setMaxAmt(CharUtil.objectConverToDouble(jsonObject.get("MaxAmt")));
                userTicket.setMaxAmt(null);
                //userTicket.setDiscount(CharUtil.objectConverToDouble(jsonObject.get("Discount")));
                userTicket.setDiscount(ticket.getDiscount().doubleValue());
                //userTicket.setEffectDate(CharUtil.objectConverToString(jsonObject.get("EffectDate")));
                userTicket.setEffectDate(DateUtil.getDateyyyMMddHHmmss_Format(ticket.getStartTime()) );
                //userTicket.setExpiryDate(CharUtil.objectConverToString(jsonObject.get("ExpiryDate")));
                userTicket.setExpiryDate(DateUtil.getDateyyyMMddHHmmss_Format(ticket.getEndTime()));
                //userTicket.setRemark(CharUtil.objectConverToString(jsonObject.get("Remark")));
                userTicket.setRemark(null);

                result.add(userTicket);
            }
        }
        return result;
    }

    /**
     * 提交私人定制订单信息,私人定制后续的其它价格走线下渠道
     * @param userId
     * @param makeOrder
     * @return
     */
    public Object submitPrivateMakeOrder(Integer userId,
                                         PrivateMakOrderAllInOne makeOrder){
        LitemallUser user=userService.findById(userId);
        //创建订单
        LitemallOrder order=new LitemallOrder();
        order.setUserId(userId);
        order.setUserName(user.getUsername());
        order.setUserNickname(user.getNickname());
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setOrderStatusName(OrderUtil.STATUS_CREATE_NAME);
        String orderSn = orderService.generateOrderSn(userId);
        order.setOrderSn(orderSn);

        //收货人信息
        LitemallAddress address=addressService.query(makeOrder.getAddressId());
        order.setConsignee(address.getName());
        order.setMobile(address.getTel());
        order.setAddress(address.getProvinceName()+address.getCityName()+address.getCountryName()+
                address.getAddressDetail()+address.getArea());
        //订单金额信息
        order.setGoodsPrice(makeOrder.getPrePayMoney());
        order.setFreightPrice(makeOrder.getShipCostMoney());
        order.setOrderPrice(makeOrder.getPrePayMoney().add(makeOrder.getShipCostMoney()));
        order.setActualPrice(makeOrder.getPrePayMoney().add(makeOrder.getShipCostMoney()));
        //私人定制品项信息
        order.setPrivateItemName(makeOrder.getPrivateItemName());
        order.setPrivateDeliverDate(DateUtil.getLocalDateTimeFromStringHM(makeOrder.getPrivateDeliverDate()+" 00:00:00") );
        order.setPrivateContentDesc(makeOrder.getPrivateContentDesc());
        order.setPrivateRemark(makeOrder.getPrivateRemark());
        order.setPrivateUploadPic(makeOrder.getPrivateUploadPic());
        order.setSendWay("快递");
        order.setPrivateMakeRuleId(makeOrder.getRuleMxId());
        orderService.add(order);

        //向私人定制订单明细表中写入记录
        LitemallPrivateMakeOrder privateMakeOrder=new LitemallPrivateMakeOrder();
        privateMakeOrder.setRulesId(makeOrder.getRuleMxId());
        privateMakeOrder.setUserId(userId);
        privateMakeOrder.setOrderId(order.getId());
        privateMakeOrderService.create(privateMakeOrder);

        return ResponseUtil.ok(order);

    }





}
