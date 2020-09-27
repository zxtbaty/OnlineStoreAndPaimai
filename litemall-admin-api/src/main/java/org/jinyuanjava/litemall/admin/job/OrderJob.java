package org.jinyuanjava.litemall.admin.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.service.CommonService;
import org.jinyuanjava.litemall.core.notify.NotifyService;
import org.jinyuanjava.litemall.core.notify.NotifyType;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.db.domain.LitemallOrder;
import org.jinyuanjava.litemall.db.domain.LitemallOrderGoods;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检测订单状态
 */
@Component
public class OrderJob {
    private final Log logger = LogFactory.getLog(OrderJob.class);

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallGoodsProductService productService;

    @Autowired
    private LitemallGoodsService goodsService;


    @Autowired
    private NotifyService notifyService;


    @Autowired
    private CommonService commonService;

    /**
     * 自动取消订单
     * <p>
     * 定时检查订单未付款情况，如果超时 LITEMALL_ORDER_UNPAID 分钟则自动取消订单
     * 定时时间是每次相隔半个小时。
     * <p>
     * TODO
     * 注意，因为是相隔半小时检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_UNPAID, 30 + LITEMALL_ORDER_UNPAID]
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    @Transactional
    public void checkOrderUnpaid() {
        //是否启用定时间任务参数，对于该后台部署多接口应用时，可能只需要其中一个后台启动任务
        //这个参数在application.yml里配
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }
        logger.info("系统开启任务检查订单是否已经超期自动取消订单");
        //处理未付款订单
        List<LitemallOrder> orderList = orderService.queryUnpaid(SystemConfig.getOrderUnpaid());
        for (LitemallOrder order : orderList) {
            // 设置订单已取消状态
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
            order.setOrderStatusName(OrderUtil.STATUS_AUTO_CANCEL_NAME);
            order.setEndTime(LocalDateTime.now());
            order.setCancelTime(LocalDateTime.now());
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                throw new RuntimeException("更新数据已失效");
            }
            // 商品货品数量增加
            //执行库存更新
            Integer orderId = order.getId();
            orderGoodsService.AddStorNum(orderId,order.getTypeCode(),null,null);
        }

     }

    /**
     * 自动确认订单
     * <p>
     * 定时检查订单未确认情况，如果超时 LITEMALL_ORDER_UNCONFIRM 天则自动确认订单
     * 定时时间是每天凌晨3点。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_UNCONFIRM, 1 + LITEMALL_ORDER_UNCONFIRM]
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkOrderUnconfirm() {
        //如果不启动定时任务，则直接返回
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }
        logger.info("系统开启任务检查订单是否已经超期自动确认收货");

        List<LitemallOrder> orderList = orderService.queryUnconfirm(SystemConfig.getOrderUnconfirm());
        for (LitemallOrder order : orderList) {

            // 设置订单已自动确认
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setOrderStatusName(OrderUtil.STATUS_AUTO_CONFIRM_NAME);
            order.setConfirmTime(LocalDateTime.now());

            if (orderService.updateWithOptimisticLocker(order) == 0) {
                logger.info("订单 ID=" + order.getId() + " 数据已经更新，放弃自动确认收货");
            }

        }
    }

    /**
     * 可评价订单商品超期
     * <p>
     * 定时检查订单商品评价情况，如果确认商品超时 LITEMALL_ORDER_COMMENT 天则取消可评价状态
     * 定时时间是每天凌晨4点。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_COMMENT, 1 + LITEMALL_ORDER_COMMENT]
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void checkOrderComment() {
        //如果不启动定时任务，则直接返回
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }
        logger.info("系统开启任务检查订单是否已经超期未评价");

        LocalDateTime now = LocalDateTime.now();
        List<LitemallOrder> orderList = orderService.queryComment(SystemConfig.getOrderComment());
        for (LitemallOrder order : orderList) {
            order.setComments((short) 0);
            orderService.updateWithOptimisticLocker(order);

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                orderGoods.setComment(-1);
                orderGoodsService.updateById(orderGoods);
            }
        }
    }

    /**
     * 每隔一分钟检查
     * 检查预约单
     * 注意，因为是相隔一分钟检查，因此导致预约单真正开始可能会迟一分钟，超时时间可能比设定时间延迟1分钟
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void checkSecKillExpired() {
        //如果不启动定时任务，则直接返回
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }
        logger.info("系统开启任务检查预约单是否已经过期或者提前4小时和1小时发送短信及微信通知");

        //自提货提前N小时给客户发送通知
        Integer tihuoFirstHintHours= SystemConfig.getOrderTihuoHintFirstHour();
        if(tihuoFirstHintHours>0) {
            List<LitemallOrder> firstHintOrders = orderService.getTihuoOrderFirstHintOrders(tihuoFirstHintHours);
            for (LitemallOrder order : firstHintOrders) {
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("orderSn", order.getOrderSn());
                valueMap.put("pickSiteName", order.getPickSiteName());
                valueMap.put("hintHours", tihuoFirstHintHours);
                notifyService.notifySmsTemplate(order.getMobile(), NotifyType.TIHUO_FIRST_WARN, valueMap);
                //orderService.update(order);
            }
        }
        //提货单提前N小时复发送通知给客户
        Integer tihuoSecondHintHours= SystemConfig.getOrderTihuoSecondHour();
        if(tihuoSecondHintHours>0) {
            List<LitemallOrder> firstHintOrders = orderService.getTihuoOrderFirstHintOrders(tihuoSecondHintHours);
            for (LitemallOrder order : firstHintOrders) {
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("orderSn", order.getOrderSn());
                valueMap.put("pickSiteName", order.getPickSiteName());
                valueMap.put("hintHours", tihuoSecondHintHours);
                notifyService.notifySmsTemplate(order.getMobile(), NotifyType.TIHUO_SECOND_WARN, valueMap);
                //orderService.update(order);
            }
        }
    }
}
