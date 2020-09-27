package org.jinyuanjava.litemall.db.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallOrderMapper;
import org.jinyuanjava.litemall.db.dao.OrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewOrderGoodsMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LitemallOrderService {
    @Resource
    private LitemallOrderMapper litemallOrderMapper;

    @Resource
    private ViewOrderGoodsMapper viewOrderGoodsMapper;

    @Resource
    private OrderMapper orderMapper;

    @Autowired
    private LitemallOrderGoodsService litemallOrderGoodsService;

    @Autowired
    private LitemallOrderFapiaoService litemallOrderFapiaoService;

    @Autowired
    private LitemallOrderTicketsService litemallOrderTicketsService;

    public int add(LitemallOrder order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.insertSelective(order);
    }

    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    public LitemallOrder findById(Integer orderId) {
        return litemallOrderMapper.selectByPrimaryKey(orderId);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public int countByOrderSn(Integer userId, String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
    public String generateOrderSn(Integer userId) {
        //20190707072912123456
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now = df.format(LocalDateTime.now());
        //用户ID是唯一的,因此只取一位随机数即可
        String orderSn = now + getRandomNum(1)+userId;
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now +getRandomNum(1)+userId;
        }
//        String orderSn = now + getRandomNum(6)+userId;
//        while (countByOrderSn(userId, orderSn) != 0) {
//            orderSn = now +getRandomNum(6)+userId;
//        }
        return orderSn;
    }
    public List<LitemallOrder> queryByCom(Integer userId, List<Short> orderStatus,Integer page, Integer limit) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus != null) {
            criteria.andYuyueStatusCodeIn(orderStatus);
        }
        criteria.andYuyueComIdBetween(2, 3);
        criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer limit) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andYuyueComIdIsNull();
        criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> querySelective(Integer userId, String orderSn,
            String  typeCode, String sourceCode,String orderType,String consignee,
            List<Short> orderStatusArray,List<Short> yuyueStatusArray,
            Boolean ifFapiao,String fapiaoStatus,String sendWay,
            String phone,String beginDate,String endDate,
            List<Integer> pickSiteIdArray,Integer payMethod,
            List<Integer> orderIds,
            Integer page, Integer limit, String sort) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        LitemallOrderExample.Criteria criteriaBackup = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId);
            criteriaBackup.andUserIdEqualTo(userId);
        }

        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%"+orderSn+"%");
            criteriaBackup.andOrderSnLike("%"+orderSn+"%");
        }
        if (!StringUtils.isEmpty(typeCode)) {
            criteria.andTypeCodeEqualTo(typeCode);
            criteriaBackup.andTypeCodeEqualTo(typeCode);
        }
        if (!StringUtils.isEmpty(sourceCode)) {
            criteria.andSourceCodeEqualTo(sourceCode);
            criteriaBackup.andSourceCodeEqualTo(sourceCode);
        }
        if (!StringUtils.isEmpty(consignee)) {
            criteria.andConsigneeLike("%"+consignee+"%");
            criteriaBackup.andConsigneeLike("%"+consignee+"%");
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
            criteriaBackup.andOrderStatusIn(orderStatusArray);
        }
        if (yuyueStatusArray != null && yuyueStatusArray.size() != 0) {
            criteria.andYuyueStatusCodeIn(yuyueStatusArray);
            criteriaBackup.andYuyueStatusCodeIn(yuyueStatusArray);
        }
        if(!StringUtils.isEmpty(orderType)) {
            if (orderType.equals("电商")) {
                criteria.andTypeCodeEqualTo("10");
            } else {
                criteria.andTypeCodeNotEqualTo("10");
            }
            if (orderType.equals("电商")) {
                criteriaBackup.andTypeCodeEqualTo("10");
            } else {
                criteriaBackup.andTypeCodeNotEqualTo("10");
            }
        }
        if(!StringUtils.isEmpty(ifFapiao)){
            criteria.andIfFapiaoEqualTo(ifFapiao);
            criteriaBackup.andIfFapiaoEqualTo(ifFapiao);
        }
        if(!StringUtils.isEmpty(fapiaoStatus)){
            criteria.andFapiaoStatusEqualTo(fapiaoStatus);
            criteriaBackup.andFapiaoStatusEqualTo(fapiaoStatus);
        }
        if(!StringUtils.isEmpty(sendWay)){
            criteria.andSendWayEqualTo(sendWay);
            criteriaBackup.andSendWayEqualTo(sendWay);
        }

        if(!StringUtils.isEmpty(payMethod)){
            criteria.andPayMethodEqualTo(payMethod);
            criteriaBackup.andPayMethodEqualTo(payMethod);
        }

        if (orderIds != null && orderIds.size() != 0) {
            criteria.andIdIn(orderIds);
            criteriaBackup.andIdIn(orderIds);
        }

        if(!StringUtils.isEmpty(beginDate)){
            criteria.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromString(beginDate));
            criteriaBackup.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromString(beginDate));
        }
        if(!StringUtils.isEmpty(endDate)){
            criteria.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromString(endDate));
            criteriaBackup.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromString(endDate));
        }
        if(!StringUtils.isEmpty(pickSiteIdArray)){
            criteria.andSendWayEqualTo("自提取货").andPickSiteIdIn(pickSiteIdArray);
            criteriaBackup.andSendWayEqualTo("自提取货").andPickSiteIdIn(pickSiteIdArray);
        }


        criteria.andDeletedEqualTo(false);
        criteriaBackup.andDeletedEqualTo(false);

        if(!StringUtils.isEmpty(phone)){
            //查询订单电
            criteria.andUserPhoneLike("%"+phone+"%");
            criteriaBackup.andMobileLike("%"+phone+"%");
            example.or(criteriaBackup);

        } else
        {

        }

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果超过此数，系统则认为是不分页
        if(limit<999999){
            PageHelper.startPage(page, limit);
        }

        return litemallOrderMapper.selectByExample(example);
    }

    public List<ViewOrderGoods> querySelectiveMx(Integer userId, String orderSn,
                                              String  typeCode, String sourceCode,String orderType,String consignee,
                                              List<Short> orderStatusArray,List<Short> yuyueStatusArray,
                                              Boolean ifFapiao,String fapiaoStatus,String sendWay,
                                              String phone,String beginDate,String endDate,
                                              List<Integer> pickSiteIdArray,
                                              Integer payMethod,
                                              List<Integer> orderIds,
                                              Integer page, Integer limit, String sort) {
        ViewOrderGoodsExample example = new ViewOrderGoodsExample();
        ViewOrderGoodsExample.Criteria criteria = example.createCriteria();

        ViewOrderGoodsExample.Criteria criteriaBackup = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId);
            criteriaBackup.andUserIdEqualTo(userId);
        }

        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%"+orderSn+"%");
            criteriaBackup.andOrderSnLike("%"+orderSn+"%");
        }
        if (!StringUtils.isEmpty(typeCode)) {
            criteria.andTypeCodeEqualTo(typeCode);
            criteriaBackup.andTypeCodeEqualTo(typeCode);
        }
        if (!StringUtils.isEmpty(sourceCode)) {
            criteria.andSourceCodeEqualTo(sourceCode);
            criteriaBackup.andSourceCodeEqualTo(sourceCode);
        }
        if (!StringUtils.isEmpty(consignee)) {
            criteria.andConsigneeLike("%"+consignee+"%");
            criteriaBackup.andConsigneeLike("%"+consignee+"%");
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
            criteriaBackup.andOrderStatusIn(orderStatusArray);
        }
        if (yuyueStatusArray != null && yuyueStatusArray.size() != 0) {
            criteria.andYuyueStatusCodeIn(yuyueStatusArray);
            criteriaBackup.andYuyueStatusCodeIn(yuyueStatusArray);
        }
        if(!StringUtils.isEmpty(orderType)) {
            if (orderType.equals("电商")) {
                criteria.andTypeCodeEqualTo("10");
            } else {
                criteria.andTypeCodeNotEqualTo("10");
            }
            if (orderType.equals("电商")) {
                criteriaBackup.andTypeCodeEqualTo("10");
            } else {
                criteriaBackup.andTypeCodeNotEqualTo("10");
            }
        }
        if(!StringUtils.isEmpty(ifFapiao)){
            criteria.andIfFapiaoEqualTo(ifFapiao);
            criteriaBackup.andIfFapiaoEqualTo(ifFapiao);
        }
        if(!StringUtils.isEmpty(fapiaoStatus)){
            criteria.andFapiaoStatusEqualTo(fapiaoStatus);
            criteriaBackup.andFapiaoStatusEqualTo(fapiaoStatus);
        }
        if(!StringUtils.isEmpty(sendWay)){
            criteria.andSendWayEqualTo(sendWay);
            criteriaBackup.andSendWayEqualTo(sendWay);
        }

        if(!StringUtils.isEmpty(payMethod)){
            criteria.andPayMethodEqualTo(payMethod);
            criteriaBackup.andPayMethodEqualTo(payMethod);
        }

        if (orderIds != null && orderIds.size() != 0) {
            criteria.andOrderIdIn(orderIds);
            criteriaBackup.andOrderIdIn(orderIds);
        }

        if(!StringUtils.isEmpty(beginDate)){
            criteria.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromString(beginDate));
            criteriaBackup.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromString(beginDate));
        }
        if(!StringUtils.isEmpty(endDate)){
            criteria.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromString(endDate));
            criteriaBackup.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromString(endDate));
        }
        if(!StringUtils.isEmpty(pickSiteIdArray)){
            criteria.andSendWayEqualTo("自提取货").andPickSiteIdIn(pickSiteIdArray);
            criteriaBackup.andSendWayEqualTo("自提取货").andPickSiteIdIn(pickSiteIdArray);
        }

        criteria.andDeletedEqualTo(false);
        criteriaBackup.andDeletedEqualTo(false);

        if(!StringUtils.isEmpty(phone)){
            //查询订单电
            criteria.andUserPhoneLike("%"+phone+"%");
            criteriaBackup.andMobileLike("%"+phone+"%");
            example.or(criteriaBackup);

        } else
        {

        }

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果超过此数，系统则认为是不分页
        if(limit<999999){
            PageHelper.startPage(page, limit);
        }

        return viewOrderGoodsMapper.selectByExample(example);
    }

    public int updateWithOptimisticLocker(LitemallOrder order) {
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.updateByPrimaryKey(order);
//        return orderMapper.updateWithOptimisticLocker(preUpdateTime, order);
    }

    public int update(LitemallOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.updateByPrimaryKeySelective(order);
    }

    public void deleteById(Integer id) {
        litemallOrderMapper.logicalDeleteByPrimaryKey(id);
    }

    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }
    public int countToday() {

        LocalDateTime localDateTimeBegin=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime localDateTimeEnd=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false).andAddTimeGreaterThanOrEqualTo(localDateTimeBegin).andAddTimeLessThanOrEqualTo(localDateTimeEnd);

        return (int) litemallOrderMapper.countByExample(example);
    }


    public List<LitemallOrder> queryUnpaid(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusMinutes(minutes);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andAddTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryHaveExpiredYuyue(int hour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusHours(hour);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andTypeCodeNotEqualTo("10")
                .andYuyueStatusCodeLessThan(OrderUtil.STATUS_CANCEL)
                .andYuyueFetchTimeLessThanOrEqualTo(expired)
                .andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryUnconfirm(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public LitemallOrder findBySn(String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    public LitemallOrder findBySnAndYuYueComId(String orderSn,Integer yuyueComId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andYuyueComIdEqualTo(yuyueComId).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    public Map<Object, Object> orderInfo(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        List<LitemallOrder> orders = litemallOrderMapper.selectByExampleSelective(example, LitemallOrder.Column.orderStatus, LitemallOrder.Column.comments);

        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        int uncomment = 0;
        for (LitemallOrder order : orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderUtil.isPayStatus(order)) {
                unship++;
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++;
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // do nothing
            }
        }

        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        return orderInfo;

    }

    public List<LitemallOrder> queryComment(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andCommentsGreaterThan((short) 0).andConfirmTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> querySelective(Integer Id,LocalDateTime updateDateTime,Integer page, Integer limit) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if (Id != null) {
            criteria.andIdEqualTo(Id);
        }
        if (updateDateTime!=null) {
            criteria.andUpdateTimeGreaterThan(updateDateTime);
        }
        //10、电商订单 20、大兴预约单 30、首都预约单
        criteria.andTypeCodeEqualTo("10");
        criteria.andDeletedEqualTo(false);

        example.setOrderByClause("id");

        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    public Object getOrderDetail(Integer id) {
        LitemallOrder order =  findById(id);
        List<LitemallOrderGoods> orderGoods = litemallOrderGoodsService.queryByOid(id);
        List<LitemallOrderFapiao> orderFapiaos=litemallOrderFapiaoService.queryByOid(id);
        List<LitemallOrderTickets> ordertickets=litemallOrderTicketsService.queryByOid(id);

        for(LitemallOrderGoods goods:orderGoods){
            if(goods.getGoodsPosKey()==null){
                goods.setGoodsPosKey(goods.getGoodsSn());
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order",order);
        data.put("orderGoods", orderGoods);
        data.put("orderFapiaos", orderFapiaos);
        data.put("ordertickets", ordertickets);

        return  data;
    }



    public LitemallOrder judgeOrderNoIfExists(String orderSn,String sourceCode){
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if (orderSn != null) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        if (sourceCode!=null) {
            criteria.andSourceCodeEqualTo(sourceCode);
        }
        return litemallOrderMapper.selectOneByExample(example);
    }

    /**
     * 获取所有已经过期的预约单
     */
    public List<LitemallOrder> getYuyueOrderHaveExpireOrders(){
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        //待备货
        Short statusBeiHuoCode= 50;
        //待取货
        Short statusFetchCode= 101;
        List<Short> inCodeList=new ArrayList<>();
        inCodeList.add(statusBeiHuoCode);
        inCodeList.add(statusFetchCode);

        criteria.andDeletedEqualTo(false);

        criteria.andYuyueStatusCodeIn(inCodeList);

        criteria.andYuyueFetchExpireTimeLessThan(LocalDateTime.now());

        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 获取所有首次提醒的预约单
     */
    public List<LitemallOrder> getYuyueOrderFirstHintOrders(String firstOrSecond, Integer yuyueHintFirstHour){
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        //待备货
        Short statusBeiHuoCode= 50;
        //待取货
        Short statusFetchCode= 101;
        List<Short> inCodeList=new ArrayList<>();
        inCodeList.add(statusBeiHuoCode);
        inCodeList.add(statusFetchCode);
        criteria.andYuyueStatusCodeIn(inCodeList);

        criteria.andDeletedEqualTo(false);

        if(firstOrSecond.equals("first")) {
            criteria.andYuyueHintFirstEqualTo(false);
        } else  if(firstOrSecond.equals("second")) {
            criteria.andYuyueHintSecondEqualTo(false);
        }

        //提前设置的时间范围
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime addHour=LocalDateTime.now().plusHours(yuyueHintFirstHour);
        criteria.andYuyueFetchTimeLessThanOrEqualTo(addHour);

        return litemallOrderMapper.selectByExample(example);
    }
    /**
     * 获取所有首次提醒的预约单
     */
    public List<LitemallOrder> getTihuoOrderFirstHintOrders(Integer tihuoHintFirstHour){
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        //待备货
        Short statusBeiHuoCode= 201;
        //待发货
        Short statusSendCode= 250;
        //待发货
        Short statusFetchCode= 301;
        List<Short> inCodeList=new ArrayList<>();
        inCodeList.add(statusBeiHuoCode);
        inCodeList.add(statusSendCode);
        inCodeList.add(statusFetchCode);
        criteria.andOrderStatusIn(inCodeList);
        criteria.andPickSiteIdGreaterThan(0);
        criteria.andPickSiteNameIsNotNull();
        criteria.andDeletedEqualTo(false);
        criteria.andYuyueComIdGreaterThan(0);

        //提前设置的时间范围
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime addHour=LocalDateTime.now().plusHours(tihuoHintFirstHour);
        criteria.andPickTimeLessThanOrEqualTo(addHour);

        return litemallOrderMapper.selectByExample(example);
    }
    /**
     * 获取预约单待备货和待取货列表
     * @return
     */
    public Object getYuyueList(String addBeginDate,String addEndDate,String yuyueBeginDate,String yuyueEndDate,String storeNo,String orderNo,Integer yuyueComId ) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        if(addEndDate==null){
            addEndDate=addBeginDate;
        }
        if(!StringUtils.isEmpty(addBeginDate))
        {
            LocalDateTime beginDate= DateUtil.getLocalDateTimeFromString(addBeginDate);
            LocalDateTime endDate=DateUtil.getLocalDateTimeFromString(DateUtil.getDateAfter2(addEndDate,1)) ;
            criteria.andAddTimeBetween(beginDate,endDate);
        }
        if(yuyueEndDate==null){
            yuyueEndDate=yuyueBeginDate;
        }
        if(!StringUtils.isEmpty(yuyueBeginDate))
        {
            LocalDateTime beginDate= DateUtil.getLocalDateTimeFromString(yuyueBeginDate);
            LocalDateTime endDate=DateUtil.getLocalDateTimeFromString(DateUtil.getDateAfter2(yuyueEndDate,1)) ;
            criteria.andYuyueFetchTimeBetween(beginDate,endDate);
        }
        if(!StringUtils.isEmpty(storeNo)){
            criteria.andYuyueStorePosKeyEqualTo(storeNo);
        }
        if(!StringUtils.isEmpty(orderNo)){
            criteria.andOrderSnEqualTo(orderNo);
        }
        if(!StringUtils.isEmpty(yuyueComId)){
            criteria.andYuyueComIdEqualTo(yuyueComId);
        }

        Short yuyueBeiHuoStatus=50;
        Short yuyueQuHuoStatus=101;
        List<Short> list=new ArrayList<>();
        list.add(yuyueBeiHuoStatus);
        list.add(yuyueQuHuoStatus);

        criteria.andYuyueStatusCodeIn(list);

        List<LitemallOrder> orders= litemallOrderMapper.selectByExample(example);

        JSONArray jsonAl=new JSONArray();

        for(LitemallOrder order:orders){
            //获取订单明细
            List<LitemallOrderGoods> orderGoods = litemallOrderGoodsService.queryByOid(order.getId());
            List<LitemallOrderFapiao> orderFapiaos=litemallOrderFapiaoService.queryByOid(order.getId());
            List<LitemallOrderTickets> ordertickets=litemallOrderTicketsService.queryByOid(order.getId());
            //Map<String, Object> data = new HashMap<>();
            JSONObject jsonOb=new JSONObject();
            jsonOb.put("order",order);
            jsonOb.put("orderGoods", orderGoods);
            jsonOb.put("orderFapiaos", orderFapiaos);
            jsonOb.put("ordertickets", ordertickets);
            jsonAl.add(jsonOb);
        }
        return jsonAl;
    }

    /**
     * 获取预约单待备货数量
     * @return
     */
    public Long getYuyueCountInfo( ) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);

        Short yuyueStatus=50;
        criteria.andYuyueStatusCodeEqualTo(yuyueStatus);
        return litemallOrderMapper.countByExample(example);
    }
    /**
     * 获取电商订单待备货数量
     * @return
     */
    public Long getBeihuoCountInfo() {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        //订单类型是电商订单
        criteria.andTypeCodeEqualTo("10");
        //订单状态是201-待备货
        Short orderStatus=201;
        criteria.andOrderStatusEqualTo(orderStatus);
        return litemallOrderMapper.countByExample(example);
    }

    /**
     * 获取电商订单待备货数量
     * @return
     */
    public Long getBeihuoCountInfo(String sendWay) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        //订单类型是电商订单
        criteria.andTypeCodeEqualTo("10");
        if(!StringUtils.isEmpty(sendWay) ){
            criteria.andSendWayEqualTo(sendWay);
        }
        //订单状态是201-待备货
        Short orderStatus=201;
        criteria.andOrderStatusEqualTo(orderStatus);
        return litemallOrderMapper.countByExample(example);
    }

    /**
     * 获取电商单退款单的数量
     * @return
     */
    public Long getTuiKuanCountInfo( ) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        //订单类型是电商订单
        criteria.andTypeCodeEqualTo("10");
        //订单状态是202-待退款
        Short orderStatus=202;
        criteria.andOrderStatusEqualTo(orderStatus);
        return litemallOrderMapper.countByExample(example);
    }

    /**
     * 获取电商单待开票的单据数量
     * @return
     */
    public Long getKaipiaoCountInfo( ) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        //订单类型是电商订单
        criteria.andTypeCodeEqualTo("10");
        //是否需要发票
        criteria.andIfFapiaoEqualTo(true);
        //发票状态
        criteria.andFapiaoStatusEqualTo("未开");
        //并且订是如下状态
        //201: '待备货', 250: '待发货', 301: '待收货', 401: '用户确认收货', 402: '超时确认收货'

        List<Short> statusArray=new ArrayList<>();
        statusArray.add(Short.valueOf("201") );
        statusArray.add(Short.valueOf("250"));
        statusArray.add(Short.valueOf("301"));
        statusArray.add(Short.valueOf("401"));
        statusArray.add(Short.valueOf("402"));
        criteria.andOrderStatusIn(statusArray);
        return litemallOrderMapper.countByExample(example);
    }

    /**
     * 获取某用户所有未取消的订单Id列表
     */
    public List<Integer> getUnCancelOrderIdList(Integer userId){
        LitemallOrderExample orderExample=new LitemallOrderExample();
        LitemallOrderExample.Criteria createCriteria=orderExample.createCriteria();
        createCriteria.andDeletedEqualTo(false);
        createCriteria.andOrderStatusNotEqualTo(OrderUtil.STATUS_CANCEL);
        List<LitemallOrder> orders=litemallOrderMapper.selectByExample(orderExample);
        List<Integer> orderIds=orders.stream().map(LitemallOrder::getId).collect(Collectors.toList());
        return orderIds;
    }

    /**
     * 获取某用户所有未取消的订单Id列表
     */
    public List<LitemallOrder> getOrderListByIds(Integer[] orderIds){
        LitemallOrderExample orderExample=new LitemallOrderExample();
        LitemallOrderExample.Criteria createCriteria=orderExample.createCriteria();
        createCriteria.andDeletedEqualTo(false);
        createCriteria.andIdIn(Arrays.asList(orderIds));
        List<LitemallOrder> orders=litemallOrderMapper.selectByExample(orderExample);
        return orders;
    }

    public List<LitemallOrder> queryByOrderStatus( List<Short> orderStatus) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
//        criteria.andYuyueComIdIsNull();
        //10、电商订单 20、大兴预约单 30、首都预约单
        criteria.andTypeCodeEqualTo("10");
        criteria.andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    //查询除已经支付以及退货状态外的所有订单
    public List<LitemallOrder> allListWithoutPayed() {

        List<LitemallOrder> orderList;

        List<Short> orderStatus = OrderUtil.withoutPayed();
        orderList =  queryByOrderStatus(orderStatus);

        return orderList;
    }


    /**
     * 判断拍卖订单是否已经创建，条件是订单状态为非取消
     * @param type 大家拍、专场拍、私人定制
     * @param ruleMxId
     * @return
     */
    public Boolean ifExistsOrder(String type,Integer ruleMxId){
        List<Short> orderCancelList=new ArrayList<>();
        orderCancelList.add(OrderUtil.STATUS_CANCEL);
        orderCancelList.add(OrderUtil.STATUS_AUTO_CANCEL);
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria=example.createCriteria();
        criteria
                .andAuctionTypeEqualTo(type)
                .andDeletedEqualTo(false)
                .andOrderStatusNotIn(orderCancelList);
        if(type.equals("大家拍")){
            criteria.andDajiapaiOfferIdEqualTo(ruleMxId);
        } else if(type.equals("专场拍")){
            criteria.andZhuanchangOfferIdEqualTo(ruleMxId);
        }else if(type.equals("私人定制")){
            criteria.andPrivateMakeRuleIdEqualTo(ruleMxId);
        }
        return  litemallOrderMapper.countByExample(example)!=0;
    }


}
