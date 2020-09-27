package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.GoodsProductMapper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsMapper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsProductMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallGoodsProductService {
    @Resource
    private LitemallGoodsProductMapper litemallGoodsProductMapper;
    @Resource
    private GoodsProductMapper goodsProductMapper;

    @Resource
    private LitemallGoodsMapper goodsMapper;


    @Autowired
    private LitemallGoodsProductService goodsProductService;

    @Autowired
    private LitemallPromotionSeckillRuleService litemallPromotionSeckillRuleService;
    @Autowired
    private LitemallPromotionGoodsRuleService litemallPromotionGoodsRuleService;

    @Autowired
    private LitemallGrouponRulesService litemallGrouponRulesService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService litemallPromotionGoodsRebateRuleService;

    public List<LitemallGoodsProduct> queryByGid(Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
        //按id默认排序
        example.orderBy("id asc");
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public List<Integer> queryListGoodsIdByStoreId(Integer storeId) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        Integer[] al=new Integer[1];
        al[0]=storeId;
        List<Integer[]> storeIds=new ArrayList<>();
        storeIds.add(al);
        example.or().andStoreIdsIn( storeIds  ).andDeletedEqualTo(false);

        List<LitemallGoodsProduct> goodsProduct= litemallGoodsProductMapper.selectByExample(example);
        return goodsProduct.stream().map(LitemallGoodsProduct::getGoodsId).distinct().collect(Collectors.toList());
    }

    public Boolean queryByGidAndStoreBiggerZero(Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        LitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        criteria.andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
        criteria.andRemainNumberGreaterThan(0);
        return litemallGoodsProductMapper.countByExample(example) != 0;
    }

    public List<Integer> queryGoodsIdListByStoreNumCondition(String storeNumCondition) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        LitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        criteria.andDeletedEqualTo(false);
        if(storeNumCondition.equalsIgnoreCase(">0")) {
            criteria.andRemainNumberGreaterThan(0);
        } else if(storeNumCondition.equalsIgnoreCase("<=0")){
            criteria.andRemainNumberLessThanOrEqualTo(0);
        }
        List<LitemallGoodsProduct> goodsProducts= litemallGoodsProductMapper.selectByExample(example);
        List<Integer> goodsIdList=goodsProducts.stream().map(LitemallGoodsProduct::getGoodsId).distinct().collect(Collectors.toList());
        return goodsIdList;
    }

    public List<LitemallGoodsProduct> queryByIdOrGoodsId(Integer id, Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        LitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        if(id!=null){
            criteria.andIdEqualTo(id);
        }
        if(gid!=null){
            criteria.andGoodsIdEqualTo(gid);
        }
        criteria.andDeletedEqualTo(false);
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public LitemallGoodsProduct findById(Integer id) {
        return litemallGoodsProductMapper.selectByPrimaryKey(id);
    }

    public List<LitemallGoodsProduct> findByGoodsIdList(List<Integer> goodsIds) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andGoodsIdIn(goodsIds);
        return litemallGoodsProductMapper.selectByExample(example);
    }
    public List<LitemallGoodsProduct> findByIdList(List<Integer> ids) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andIdIn(ids);
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        litemallGoodsProductMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoodsProduct goodsProduct) {
        goodsProduct.setAddTime(LocalDateTime.now());
        goodsProduct.setUpdateTime(LocalDateTime.now());
        litemallGoodsProductMapper.insertSelective(goodsProduct);
    }

    public void update(LitemallGoodsProduct litemallGoodsProduct){
        litemallGoodsProduct.setUpdateTime(LocalDateTime.now());
        litemallGoodsProductMapper.updateByPrimaryKey(litemallGoodsProduct);
    }

    public int count() {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallGoodsProductMapper.countByExample(example);
    }

    public int countToday() {
        LocalDateTime localDateTimeBegin=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime localDateTimeEnd=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andDeletedEqualTo(false).andAddTimeGreaterThanOrEqualTo(localDateTimeBegin).andAddTimeLessThanOrEqualTo(localDateTimeEnd);

        return (int) litemallGoodsProductMapper.countByExample(example);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andGoodsIdEqualTo(gid);
        litemallGoodsProductMapper.logicalDeleteByExample(example);
    }



    public int addStock(Integer id, Short num){
        return goodsProductMapper.addStock(id, num);
    }

    public int reduceStock(Integer id, Short num){
        return goodsProductMapper.reduceStock(id, num);
    }


    public List<LitemallGoodsProduct> getGoodsProductForInterface(
            LocalDateTime lastUpdateTime, Integer comId, Integer page,Integer limit){
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        LitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }
        if(!StringUtils.isEmpty(comId)){
            LitemallGoodsExample goodsExample=new LitemallGoodsExample();
            LitemallGoodsExample.Criteria goodsCriteria= goodsExample.createCriteria();
            goodsCriteria.andComIdEqualTo(comId);
            List<LitemallGoods> goodsList= goodsMapper.selectByExample(goodsExample);
            List<Integer> goodsIds=goodsList.stream().map(LitemallGoods::getId).collect(Collectors.toList());
            if(goodsIds!=null&&goodsIds.size()>0){
                criteria.andGoodsIdIn(goodsIds);
            }
        }
        //criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page,limit);
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public BigDecimal getGoodsPrice(Integer userId, Integer productId) {
        LitemallGoodsProduct product = goodsProductService.findById(productId);
        //判断该商品是否可以下单，如果是秒杀商品规定了用户只能下一单，如果已经下过，则不可以继续下，
        // 如果是团购商品，该用户已经下过单，则不允许再继续下单
        //如果是会员商品，规定单人单次，如果用户已经下过单，则不允许再购买
        //判断该商品是否在正在执行的秒杀列表中,如果是，执行秒杀价
        LitemallPromotionSeckillRule promotionSeckillRule = litemallPromotionSeckillRuleService.queryProductIdIsInSkill(product.getId());
        if (promotionSeckillRule != null) {
            return promotionSeckillRule.getSeckillPrice();
        }
        //判断该商品是否在正在执行的团购商品列表中，如果是，执行团购价
        LitemallGrouponRules grouponRules = litemallGrouponRulesService.queryProductIdIsInGroupon(product.getId());
        if (grouponRules != null) {
            return grouponRules.getGrouponPrice();
        }
        //判断该商品是否是会员商品，如果是则执行会员价
        LitemallPromotionGoodsDetail promotionGoodsDetail = litemallPromotionGoodsRuleService.getRuleByProductId(product.getId(), userId);
        if (promotionGoodsDetail != null) {
            return promotionGoodsDetail.getHuiYuanPrice();
        }
        //判断该商品是否是在品项折扣活动中，如果是，则取品项折扣价
        ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods = litemallPromotionGoodsRebateRuleService.getRuleByProductId(product.getId());
        if (goodsRebateRuleGoods != null) {
            return goodsRebateRuleGoods.getRebatePrice();
            //如果已经在会员活动中，则直接返回下一商品
        }
        //如果不在活动中，则直接返回商品定价
        return product.getPrice();
    }


}
