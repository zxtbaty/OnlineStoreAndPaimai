package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.GoodsRuleAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminGoodsRuleService {
    private final Log logger = LogFactory.getLog(AdminGoodsRuleService.class);

    @Autowired
    private LitemallPromotionGoodsRuleService goodsRuleService;
    @Autowired
    private LitemallPromotionGoodsRuleUserService goodsRuleUserService;

    @Autowired
    private LitemallPromotionGoodsDetailService goodsDetailService;

    @Autowired
    private LitemallCartService litemallCartService;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;


    public Object list(String name, Integer expireFlag,
                       String goodsSn, String goodsName,
                       String username,
                       Integer page, Integer limit, String sort) {
        List<LitemallPromotionGoodsRule> dicMainsList = goodsRuleService.querySelective(
                name,expireFlag,goodsSn,goodsName,username, page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(GoodsRuleAllinone goodsRuleAllinone) {
        LitemallPromotionGoodsRule goodsRule = goodsRuleAllinone.getGoodsRule();
        String name = goodsRule.getName();

//        if (goodsRule.getGoodsId() == null) {
//            return ResponseUtil.fail(401,"会员专享活动规则里没有设置商品");
//        }

//        BigDecimal seckillPrice = goodsRule.getUserPrice();
//        if (seckillPrice == null) {
//            return ResponseUtil.fail(401,"会员专享活动规则里会员价不能为空");
//        }
        LocalDateTime beginDate = goodsRule.getBeginDate();
        if (beginDate == null) {
            return ResponseUtil.fail(401,"会员专享活动规则里开始时间不能为空");
        }
        LocalDateTime endDate = goodsRule.getEndDate();
        if (endDate == null) {
            return ResponseUtil.fail(401,"会员专享活动规则里结束时间不能为空");
        }

        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "会员专享活动名称不不能为空");
        }
        LitemallPromotionGoodsRuleUser[] goodsRuleUsers = goodsRuleAllinone.getGoodsRuleUsers();
        for (LitemallPromotionGoodsRuleUser goodsRuleUser : goodsRuleUsers) {
            String userClass1Name=goodsRuleUser.getUserClassAttr1Name();
            String userClass2Name=goodsRuleUser.getUserClassAttr2Name();
            String username = goodsRuleUser.getUsername();
            if (StringUtils.isEmpty(userClass1Name)&&StringUtils.isEmpty(userClass2Name)&&StringUtils.isEmpty(username)) {
                return ResponseUtil.fail(401, "会员专享活动会员类别1、会员类别2、用户名称三者不能同时为空");
            }
        }

        LitemallPromotionGoodsDetail[] goodsDetails = goodsRuleAllinone.getGoodsDetails();
        for (LitemallPromotionGoodsDetail goodsDetail : goodsDetails) {
            Integer goodsProductId=goodsDetail.getGoodsProductId();
            if (StringUtils.isEmpty(goodsProductId)) {
                return ResponseUtil.fail(401, "会员商品ID不能为空");
            }
            Integer storeNum=goodsDetail.getStoreNum();
            if (StringUtils.isEmpty(storeNum)) {
                return ResponseUtil.fail(401, "会员活动商品库存不能为空");
            }
            BigDecimal huiYuanPrice = goodsDetail.getHuiYuanPrice();
            if (StringUtils.isEmpty(huiYuanPrice)) {
                return ResponseUtil.fail(401, "会员活动商品会员价不能为空");
            }

        }

        return null;
    }

    /**
     * 编辑会员专享活动
     * <p>
     * TODO
     * 需要判断是否明细主键ID已经存在，如果存在，则更新信息，如果不存在，则插入数据
     */
    @Transactional
    public Object update(GoodsRuleAllinone goodsRuleAllinone) {
        Object error = validate(goodsRuleAllinone);
        if (error != null) {
            return error;
        }
        LitemallPromotionGoodsRule goodsRule=goodsRuleAllinone.getGoodsRule();
        LitemallPromotionGoodsRuleUser[] goodsRuleUsers = goodsRuleAllinone.getGoodsRuleUsers();
        LitemallPromotionGoodsDetail[] goodsDetails = goodsRuleAllinone.getGoodsDetails();
        if(goodsDetails!=null) {
            goodsRule.setGoodsCount(goodsDetails.length);
        }
        Integer mainId = goodsRule.getId();
        if(mainId==null){
            goodsRuleService.createRules(goodsRule);
        }else
        {

            goodsRuleService.updateById(goodsRule);
        }

        // 活动会员列表
        for (LitemallPromotionGoodsRuleUser goodsRuleUser : goodsRuleUsers) {
            goodsRuleUser.setRuleId(goodsRule.getId());
            Integer codeId=goodsRuleUser.getId();
            if(codeId==null){
                goodsRuleUserService.createRulesUser(goodsRuleUser);
            } else
            {
                goodsRuleUserService.updateById(goodsRuleUser);
            }
        }
        // 活动商品列表
        for (LitemallPromotionGoodsDetail goodsDetail : goodsDetails) {
            goodsDetail.setRuleId(goodsRule.getId());
            Integer codeId=goodsDetail.getId();
            if(goodsDetail.getRemainNum()==null&&goodsDetail.getStoreNum()!=null){
                goodsDetail.setRemainNum(goodsDetail.getStoreNum());
            }
            if(codeId==null){
                goodsDetailService.createGoodsDetail(goodsDetail);
            } else
            {
                goodsDetailService.updateById(goodsDetail);
            }
        }

        //如果会员活动规则为过期，则清空购物车
        if(goodsRule.getExpireFlag()){
            //获取该会员活动中的所有商品列表
            List<LitemallPromotionGoodsDetail> productList= goodsDetailService.queryByRuleId(goodsRule.getId());
            List<Integer> productIdList=productList.stream().map(LitemallPromotionGoodsDetail::getGoodsProductId).collect(Collectors.toList());
            if(productIdList!=null&&productIdList.size()>0){
                litemallCartService.deleteByProductIds("会员",goodsRule.getId(), productIdList);
            }
        }
        return ResponseUtil.ok(goodsRule);
    }

    /**
     * 删除会员活动列表
     * @param goodsRule
     * @return
     */
    @Transactional
    public Object delete(LitemallPromotionGoodsRule goodsRule) {
        Integer goodsRuleId= goodsRule.getId();
        if (goodsRuleId == null) {
            return ResponseUtil.badArgument();
        }

        goodsRuleService.delete(goodsRuleId);
        goodsRuleUserService.deleteByRuleId(goodsRuleId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsRuleAllinone goodsRuleAllinone) {
        Object error = validate(goodsRuleAllinone);
        if (error != null) {
            return error;
        }

        LitemallPromotionGoodsRule goodsRule = goodsRuleAllinone.getGoodsRule();
        LitemallPromotionGoodsRuleUser[] goodsRuleUsers = goodsRuleAllinone.getGoodsRuleUsers();
        LitemallPromotionGoodsDetail[] goodsDetails = goodsRuleAllinone.getGoodsDetails();

        String name = goodsRule.getName();
        if (goodsRuleService.checkExistByName(name)) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "会员活动名称已经存在");
        }
        if(goodsDetails!=null) {
            goodsRule.setGoodsCount(goodsDetails.length);
        }
        //保存主表
        goodsRuleService.createRules(goodsRule);
        // 保存会员明细
        for (LitemallPromotionGoodsRuleUser goodsRuleUser : goodsRuleUsers) {
            goodsRuleUser.setRuleId(goodsRule.getId());
            goodsRuleUserService.createRulesUser(goodsRuleUser);
        }
        //保存会员商品
        for (LitemallPromotionGoodsDetail goodsDetail : goodsDetails) {
            goodsDetail.setRuleId(goodsRule.getId());
            goodsDetail.setRemainNum(goodsDetail.getStoreNum());
            goodsDetailService.createGoodsDetail(goodsDetail);
        }
        return ResponseUtil.ok(goodsRule);
    }

    public Object detail(Integer id) {
        LitemallPromotionGoodsRule userGoodsRule = goodsRuleService.findById(id);

        List<LitemallPromotionGoodsRuleUser> goodsRuleUsers = goodsRuleUserService.queryByRuleId(id);
        List<LitemallPromotionGoodsDetail> goodsDetails = goodsDetailService.queryByRuleId(id);
        for(LitemallPromotionGoodsDetail goodsDetail:goodsDetails){
            Integer unPayNum=orderGoodsService.getOrderSumNumByRuleId("会员",
                    " and order_status="+ OrderUtil.STATUS_CREATE,id,goodsDetail.getGoodsProductId());
            goodsDetail.setUnPayNum(unPayNum);
            //去掉取消，已退款
//                101: '待支付',
//                102: '用户取消',
//                103: '超时取消',
//                201: '待备货',
//                250: '待发货',
//                202: '待退款',
//                203: '已退款',
//                301: '待收货',
//                401: '用户确认收货',
//                402: '超时确认收货'
            Integer havePayNum=orderGoodsService.getOrderSumNumByRuleId("会员",
                    " and order_status in (201,250,202,301,401,402) ",id,goodsDetail.getGoodsProductId());
            goodsDetail.setHavePayNum(havePayNum);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goodsRule", userGoodsRule);
        data.put("goodsRuleUser", goodsRuleUsers);
        data.put("goodsDetails", goodsDetails);

        return ResponseUtil.ok(data);
    }



//    public Boolean addGoodsStoreNum(Integer id, Integer addGoodsStoreNum) {
//        try {
//            LitemallPromotionGoodsRule userRule = goodsRuleService.findById(id);
//            userRule.setStoreNum(userRule.getStoreNum() + addGoodsStoreNum);
//            userRule.setRemainNum(userRule.getRemainNum() + addGoodsStoreNum);
//            goodsRuleService.updateById(userRule);
//            return true;
//        }catch (Exception ex){
//            System.out.print(ex.getMessage());
//            return false;
//        }
//    }

    public Boolean addGoodsStoreNum(Integer id, Integer addGoodsStoreNum) {
        try {
            LitemallPromotionGoodsDetail goodsDetail = goodsDetailService.queryById(id);
            goodsDetail.setStoreNum(goodsDetail.getStoreNum() + addGoodsStoreNum);
            goodsDetail.setRemainNum(goodsDetail.getRemainNum() + addGoodsStoreNum);
            goodsDetailService.updateById(goodsDetail);
            return true;
        }catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }
    }
}
