package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.LitemallOrderGoodsMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.CharUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LitemallOrderGoodsService {
    @Resource
    private LitemallOrderGoodsMapper orderGoodsMapper;

    @Autowired
    private CommonDBService commonDBService;

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService;

    @Autowired
    private LitemallPromotionGoodsDetailService promotionGoodsDetailService;

    @Autowired
    private LitemallPromotionSeckillRuleService promotionSeckillRuleService;

    @Autowired
    private LitemallGrouponRulesService grouponRulesService;

    @Autowired
    private LitemallGoodsProductService productService;

    @Autowired
    private LitemallGoodsService goodsService;

    public int add(LitemallOrderGoods orderGoods) {
        orderGoods.setAddTime(LocalDateTime.now());
        orderGoods.setUpdateTime(LocalDateTime.now());
        return orderGoodsMapper.insertSelective(orderGoods);
    }

    public List<LitemallOrderGoods> queryByOid(Integer orderId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

    public List<LitemallOrderGoods> findByOidAndGid(Integer orderId, Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

    public List<LitemallOrderGoods> findByOidAndIdOrGid(Integer orderId,Integer id, Integer productId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        LitemallOrderGoodsExample.Criteria criteria=example.createCriteria();
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(orderId)){
            criteria.andOrderIdEqualTo(orderId);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdEqualTo(id);
        }
        if(!StringUtils.isEmpty(productId)){
            criteria.andProductIdEqualTo(productId);
        }

        return orderGoodsMapper.selectByExample(example);
    }

    public LitemallOrderGoods findById(Integer id) {

        return orderGoodsMapper.selectByPrimaryKey(id);
    }

    public List<LitemallOrderGoods> findByIds(List<Integer> ids) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdIn(ids).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);

    }


    public void updateById(LitemallOrderGoods orderGoods) {
        orderGoods.setUpdateTime(LocalDateTime.now());
        orderGoodsMapper.updateByPrimaryKeySelective(orderGoods);
    }

    public Short getComments(Integer orderId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        long count = orderGoodsMapper.countByExample(example);
        return (short) count;
    }



    public boolean checkExist(Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsMapper.countByExample(example) != 0;
    }

    public LitemallOrderGoods judgeOrderDetailIfExists(Integer orderId,String productId){
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        LitemallOrderGoodsExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(orderId)){
            criteria.andOrderIdEqualTo(orderId);
        }
        if(!StringUtils.isEmpty(productId)){
            criteria.andProductIdEqualTo(Integer.parseInt(productId) );
        }
        criteria.andDeletedEqualTo(false);
        return orderGoodsMapper.selectOneByExample(example);
    }

    /**
     * 计算某项活动中的用户下了订单在某些状态下的产品数量合计
     * @param ruleType
     * @param orderCodition
     * @param ruleId
     * @param productId
     * @return
     */
    public Integer getOrderSumNumByRuleId(String ruleType,String orderCodition, Integer ruleId,Integer productId){
        String strRuleCondition="";
        if(ruleType.equals("会员")) {
            strRuleCondition = " and user_goods_rule_id=" + ruleId;
        } else  if(ruleType.equals("品项折扣")) {
            strRuleCondition = " and rebate_rule_id=" + ruleId;
        } else  if(ruleType.equals("团购")) {
            strRuleCondition = " and groupon_rule_id=" + ruleId;
        } else if(ruleType.equals("秒杀")) {
            strRuleCondition = " and sec_rule_id=" + ruleId;
        };
        String strSql="Select sum(number) as unpayNum from view_order_goods where 1=1  "+
                orderCodition+strRuleCondition+" and product_id="+productId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else
        {
            Object value= m.get("unpayNum");
            if(value==null){
                return 0;
            } else {
                Integer maxInt = Integer.parseInt(value.toString());
                return maxInt;
            }
        }
    }

    /**
     *
     */
    /**
     * 计算某项活动中的用户下了订单未支付的产品数量合计 按品项分组
     * @param ruleType
     * @param orderCodition
     * @param ruleId
     * @return
     */
    public List<Map<String,Integer>>  getOrderSumNumByRuleId(String ruleType,String orderCodition, Integer ruleId){
        String strRuleCondition="";
        if(ruleType.equals("会员")) {
            strRuleCondition = " and user_goods_rule_id=" + ruleId;
        } else  if(ruleType.equals("品项折扣")) {
            strRuleCondition = " and rebate_rule_id=" + ruleId;
        } else  if(ruleType.equals("团购")) {
            strRuleCondition = " and groupon_rule_id=" + ruleId;
        } else if(ruleType.equals("秒杀")) {
            strRuleCondition = " and sec_rule_id=" + ruleId;
        };
        String strSql="Select product_id as productId,sum(number) as unpayNum from view_order_goods " +
                "where 1=1 "+orderCodition+strRuleCondition;

        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String,Integer>> result= commonDBService.procedureDaoList(param);
        return result;
    }


    /**
     * 货品加库存 适合取消 退款,电商和预约均适合
     * @param orderId
     * @param orderDetailId
     * @param productId
     * @return
     */
    public Boolean AddStorNum(Integer orderId,String typeCode,Integer orderDetailId,Integer productId){
        try {
            List<LitemallOrderGoods> orderGoodsList = findByOidAndIdOrGid(orderId, orderDetailId, productId);
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                //判断该商品是否是活动商品,如果是活动商品，则在活动中进行相应的增减操作,如果是品项折扣，走普通订单一样的操作
                if (orderGoods.getUserGoodsRuleId() != null) {
                    //会员活动订单
                    LitemallPromotionGoodsDetail goodsDetail= promotionGoodsDetailService.querySelective(orderGoods.getUserGoodsRuleId(),orderGoods.getProductId());
                    goodsDetail.setRemainNum(CharUtil.objectConverToInteger(goodsDetail.getRemainNum() + orderGoods.getNumber()));
                    promotionGoodsDetailService.updateById(goodsDetail);
                } else if (orderGoods.getSecRuleId() != null) {
                    //会员活动订单
                    LitemallPromotionSeckillRule seckillRule = promotionSeckillRuleService.queryById(orderGoods.getSecRuleId());
                    seckillRule.setSeckillRemainNum(CharUtil.objectConverToInteger(seckillRule.getSeckillRemainNum() + orderGoods.getNumber()));
                    promotionSeckillRuleService.updateById(seckillRule);
                } else if (orderGoods.getGrouponRuleId() != null) {
                    //会员活动订单
                    LitemallGrouponRules grouponRules = grouponRulesService.queryById(orderGoods.getGrouponRuleId());
                    grouponRules.setGroupRemainStore(CharUtil.objectConverToInteger(grouponRules.getGroupRemainStore() + orderGoods.getNumber()));
                    grouponRulesService.updateById(grouponRules);
                } else {
                    Integer tempProductId = orderGoods.getProductId();
                    LitemallGoodsProduct litemallGoodsProduct = productService.findById(tempProductId);
                    //如果商品类型是预约，则变更预约商品，如果商品类型是非预约，则变更非预约库存数量
//                     10、电商订单
//                    20、首都预约单
//                    30、大兴预约单
                    //总库存增加
                    if(typeCode.equals("10")){
                        litemallGoodsProduct.setNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber() + orderGoods.getNumber()));
                    } else
                    {
                        //如果是预约单，则可用预约数量同步减少 可用库存增加
                        litemallGoodsProduct.setYuyueNumber(litemallGoodsProduct.getYuyueNumber()-orderGoods.getNumber());
                    }
                    //库存减去预约
                    litemallGoodsProduct.setRemainNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber()) -
                            CharUtil.objectConverToInteger(litemallGoodsProduct.getYuyueNumber()));
                    productService.update(litemallGoodsProduct);
                    goodsService.updateStoreBiggerZeroOrNot(litemallGoodsProduct.getGoodsId());
                }
            }
            return true;
        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }
    }

    /**
     * 货品减库存 适合下单销售,电商订单和预约单均适合
     * @param orderId
     * @param orderDetailId
     * @param productId
     * @return
     */
    public Boolean SubstractStorNum(Integer orderId,String typeCode,Integer orderDetailId,Integer productId){
        try {
            List<LitemallOrderGoods> orderGoodsList = findByOidAndIdOrGid(orderId, orderDetailId, productId);
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                //判断该商品是否是活动商品,如果是活动商品，则在活动中进行相应的增减操作,如果是品项折扣，走普通订单一样的操作
                if (orderGoods.getUserGoodsRuleId() != null) {
                    //会员活动订单
                    LitemallPromotionGoodsDetail goodsDetail= promotionGoodsDetailService.querySelective(orderGoods.getUserGoodsRuleId(),orderGoods.getProductId());
                    goodsDetail.setRemainNum(CharUtil.objectConverToInteger(goodsDetail.getRemainNum() - orderGoods.getNumber()));
                    promotionGoodsDetailService.updateById(goodsDetail);
                } else if (orderGoods.getSecRuleId() != null) {
                    //会员活动订单
                    LitemallPromotionSeckillRule seckillRule = promotionSeckillRuleService.queryById(orderGoods.getSecRuleId());
                    seckillRule.setSeckillRemainNum(CharUtil.objectConverToInteger(seckillRule.getSeckillRemainNum() - orderGoods.getNumber()));
                    promotionSeckillRuleService.updateById(seckillRule);
                } else if (orderGoods.getGrouponRuleId() != null) {
                    //秒杀活动订单
                    LitemallGrouponRules grouponRules = grouponRulesService.queryById(orderGoods.getGrouponRuleId());
                    grouponRules.setGroupRemainStore(CharUtil.objectConverToInteger(grouponRules.getGroupRemainStore() - orderGoods.getNumber()));
                    grouponRulesService.updateById(grouponRules);
                } else {
                    Integer tempProductId = orderGoods.getProductId();
                    LitemallGoodsProduct litemallGoodsProduct = productService.findById(tempProductId);
                    //如果商品类型是预约，则变更预约商品，如果商品类型是非预约，则变更非预约库存数量
//                     10、电商订单
//                    20、首都预约单
//                    30、大兴预约单
                    //总库存减少
                    if(typeCode.equals("10")) {
                        litemallGoodsProduct.setNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber() - orderGoods.getNumber()));
                    } else {
                        //如果是预约单，则用预约数量同步增加 可用库存减少
                        litemallGoodsProduct.setYuyueNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getYuyueNumber())+orderGoods.getNumber());

                    }
                    //库存减去预约
                    litemallGoodsProduct.setRemainNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber()) -
                            CharUtil.objectConverToInteger(litemallGoodsProduct.getYuyueNumber()));
                    productService.update(litemallGoodsProduct);
                    goodsService.updateStoreBiggerZeroOrNot(litemallGoodsProduct.getGoodsId());
                }
            }
            return true;
        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }
    }

    /**
     * 预约单机场消费成功
     * @param orderId
     * @param orderDetailId
     * @param productId
     * @return
     */
    public Boolean UpdateYuYueStoreNum(Integer orderId,Integer orderDetailId,Integer productId){
        try {
            List<LitemallOrderGoods> orderGoodsList = findByOidAndIdOrGid(orderId, orderDetailId, productId);
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                //判断该商品是否是活动商品,如果是活动商品，则在活动中进行相应的增减操作,如果是品项折扣，走普通订单一样的操作
                if (orderGoods.getUserGoodsRuleId() != null) {
                    //会员活动订单
//                    LitemallPromotionGoodsDetail goodsDetail= promotionGoodsDetailService.querySelective(orderGoods.getUserGoodsRuleId(),orderGoods.getProductId());
//                    goodsDetail.setRemainNum(CharUtil.objectConverToInteger(goodsDetail.getRemainNum() - orderGoods.getNumber()));
//                    promotionGoodsDetailService.updateById(goodsDetail);

                } else if (orderGoods.getSecRuleId() != null) {
                    //会员活动订单
//                    LitemallPromotionSeckillRule seckillRule = promotionSeckillRuleService.queryById(orderGoods.getSecRuleId());
//                    seckillRule.setSeckillRemainNum(CharUtil.objectConverToInteger(seckillRule.getSeckillRemainNum() - orderGoods.getNumber()));
//                    promotionSeckillRuleService.updateById(seckillRule);
                } else if (orderGoods.getGrouponRuleId() != null) {
                    //秒杀活动订单
//                    LitemallGrouponRules grouponRules = grouponRulesService.queryById(orderGoods.getGrouponRuleId());
//                    grouponRules.setGroupRemainStore(CharUtil.objectConverToInteger(grouponRules.getGroupRemainStore() - orderGoods.getNumber()));
//                    grouponRulesService.updateById(grouponRules);
                } else {
                    Integer tempProductId = orderGoods.getProductId();
                    LitemallGoodsProduct litemallGoodsProduct = productService.findById(tempProductId);
                    //如果商品类型是预约，则变更预约商品，如果商品类型是非预约，则变更非预约库存数量
//                     10、电商订单
//                    20、首都预约单
//                    30、大兴预约单
                    //总库存减少
                    litemallGoodsProduct.setNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber() - orderGoods.getNumber()));
                    //如果是预约单，则用预约数量同步增加 可用库存减少
                    litemallGoodsProduct.setYuyueNumber(litemallGoodsProduct.getYuyueNumber()-orderGoods.getNumber());
                    //库存减去预约
                    litemallGoodsProduct.setRemainNumber(CharUtil.objectConverToInteger(litemallGoodsProduct.getNumber()) -
                            CharUtil.objectConverToInteger(litemallGoodsProduct.getYuyueNumber()));
                    productService.update(litemallGoodsProduct);
                    goodsService.updateStoreBiggerZeroOrNot(litemallGoodsProduct.getGoodsId());
                }
            }
            return true;
        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }
    }

}
