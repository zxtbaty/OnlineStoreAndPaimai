package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRebateGoodsMapper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRebateRuleMapper;

import org.jinyuanjava.litemall.db.dao.ViewPromotionGoodsRebateRuleGoodsMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallPromotionGoodsRebateRuleService {
    @Resource
    private LitemallPromotionGoodsRebateRuleMapper mapper;

    @Resource
    private LitemallPromotionGoodsRebateGoodsMapper goodsMapper;

    @Resource
    private ViewPromotionGoodsRebateRuleGoodsMapper promotionGoodsRebateRuleGoodsMapper;

    //筛选活动的商品要判断一下是否在秒杀、团购、会员商品规则中目前正在的进行的活动中出现，如果出现，则不能允许客户设置折扣

    /**
     * 获取品项折扣活动列表
     * @param name
     * @param expireFlag
     * @param goodsSn
     * @param goodsName
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsRebateRule> querySelective(String name, Integer expireFlag,
                                                                 String goodsSn, String goodsName,
                                                                 Integer page, Integer size, String sort) {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.setOrderByClause(sort);

        LitemallPromotionGoodsRebateRuleExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%"+name+"%");
        }
        if(expireFlag!=null)
        {
            if(expireFlag==0){
                criteria.andExpireFlagEqualTo(false);
            }else if(expireFlag==1){
                criteria.andExpireFlagEqualTo(true);
            }
        }
        //判断品项折扣明细中是否有该商品的信息

        if (!StringUtils.isEmpty(goodsSn)) {
            LitemallPromotionGoodsRebateGoodsExample goodsExample = new LitemallPromotionGoodsRebateGoodsExample();
            goodsExample.or().andGoodsSnEqualTo(goodsSn).andDeletedEqualTo(false);
            List<LitemallPromotionGoodsRebateGoods> rebateGoodsList=  goodsMapper.selectByExample(goodsExample);
            List<Integer> mainIdList=rebateGoodsList.stream().map(LitemallPromotionGoodsRebateGoods::getRuleId).distinct().collect(Collectors.toList());
            if(mainIdList!=null&&mainIdList.size()>0) {
                criteria.andIdIn(mainIdList);
            }
        }

        if (!StringUtils.isEmpty(goodsName)) {
            LitemallPromotionGoodsRebateGoodsExample goodsExample = new LitemallPromotionGoodsRebateGoodsExample();
            goodsExample.or().andGoodsNameLike("%"+goodsName+"%").andDeletedEqualTo(false);
            List<LitemallPromotionGoodsRebateGoods> rebateGoodsList=  goodsMapper.selectByExample(goodsExample);
            List<Integer> mainIdList=rebateGoodsList.stream().map(LitemallPromotionGoodsRebateGoods::getRuleId).distinct().collect(Collectors.toList());
            if(mainIdList!=null&&mainIdList.size()>0) {
                criteria.andIdIn(mainIdList);
            }
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 创建会员专属活动规则
     * @param rules
     * @return
     */
    public int createRules(LitemallPromotionGoodsRebateRule rules) {
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应品项折扣活动规则
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsRebateRule queryById(Integer id) {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 判断某个品项折扣活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallPromotionGoodsRebateRule rules) {
        return (rules == null || rules.getEndDate().isBefore(LocalDateTime.now()));
    }

    /**
     * 查询状态正常，但是时间已经过期的品项折扣活动
     *
     * @return
     */
    public List<LitemallPromotionGoodsRebateRule> queryHaveExpiredButStateError() {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.or().andExpireFlagEqualTo(false).andEndDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 查询状态正常，已经开始品项折扣进行的任务列表
     *
     * @return
     */
    public List<LitemallPromotionGoodsRebateRule> queryHaveBeginRebate() {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.or().andExpireFlagEqualTo(false).andBeginDateGreaterThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    //列出所有正在参与团购活动的商品，并且活动正在进行中
    public List<Integer> queryAllProductIdList() {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.or().andBeginDateLessThanOrEqualTo(LocalDateTime.now())
                .andExpireFlagEqualTo(false).andDeletedEqualTo(false);
        List<LitemallPromotionGoodsRebateRule> rebateRules=mapper.selectByExample(example);
        //该品项规格下的所有商品id明细
        List<Integer> ruleIdList=rebateRules.stream().map(LitemallPromotionGoodsRebateRule::getId).distinct().collect(Collectors.toList());
        if(ruleIdList==null||ruleIdList.size()==0){
            return null;
        }
        LitemallPromotionGoodsRebateGoodsExample rebateGoodsExample=new LitemallPromotionGoodsRebateGoodsExample();
        rebateGoodsExample.or().andDeletedEqualTo(false).andRuleIdIn(ruleIdList);
        List<LitemallPromotionGoodsRebateGoods> goodsRebateGoods= goodsMapper.selectByExample(rebateGoodsExample);
        List<Integer> productIdList=goodsRebateGoods.stream().map(LitemallPromotionGoodsRebateGoods::getGoodsProductId).distinct().collect(Collectors.toList());
        return productIdList;
    }


    /**
     * 删除品项折扣活动规则及对应的商品Id
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
        //删除品项规则下的商品明细
        LitemallPromotionGoodsRebateGoodsExample example = new LitemallPromotionGoodsRebateGoodsExample();
        example.or().andRuleIdEqualTo(id).andDeletedEqualTo(false);
        goodsMapper.logicalDeleteByExample(example);
    }

    /**
     * 更新品项折扣活动规则
     * @param rule
     * @return
     */
    public int updateById(LitemallPromotionGoodsRebateRule rule) {
        rule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(rule);
    }

    //检查是否存在
    public boolean checkExistByName(String name) {
        LitemallPromotionGoodsRebateRuleExample example = new LitemallPromotionGoodsRebateRuleExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    public LitemallPromotionGoodsRebateRule findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }


    /**
     * 判断商品是否存在某个已经开始但还没有结束的品项折扣活动中
     * @param productId
     * @return
     */
    public ViewPromotionGoodsRebateRuleGoods getRuleByProductId(Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewPromotionGoodsRebateRuleGoodsExample goodsRebateGoodsExample=new ViewPromotionGoodsRebateRuleGoodsExample();
        ViewPromotionGoodsRebateRuleGoodsExample.Criteria criteria=goodsRebateGoodsExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdEqualTo(productId);
        ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods= promotionGoodsRebateRuleGoodsMapper.selectOneByExample(goodsRebateGoodsExample);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的品项折扣活动中
     * @param productId
     * @return
     */
    public List<ViewPromotionGoodsRebateRuleGoods> getRuleByGoodsProductId(Integer goodsId,Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewPromotionGoodsRebateRuleGoodsExample goodsRebateGoodsExample=new ViewPromotionGoodsRebateRuleGoodsExample();
        ViewPromotionGoodsRebateRuleGoodsExample.Criteria criteria=goodsRebateGoodsExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());
        if(productId==null){
            criteria.andGoodsIdEqualTo(goodsId);
        } else {
            criteria.andGoodsProductIdEqualTo(productId);
        }
        List<ViewPromotionGoodsRebateRuleGoods> goodsRebateRuleGoods= promotionGoodsRebateRuleGoodsMapper.selectByExample(goodsRebateGoodsExample);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

    /**
     * 判断商品列表是否存在某个已经开始但还没有结束的品项折扣活动中
     * @param productIds
     * @return
     */
    public List<ViewPromotionGoodsRebateRuleGoods> getRuleByProductIds(List<Integer> productIds){
        //判断该商品是否在此会员专属规则中
        ViewPromotionGoodsRebateRuleGoodsExample goodsRebateGoodsExample=new ViewPromotionGoodsRebateRuleGoodsExample();
        ViewPromotionGoodsRebateRuleGoodsExample.Criteria criteria=goodsRebateGoodsExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdIn(productIds);
        List<ViewPromotionGoodsRebateRuleGoods> goodsRebateRuleGoods= promotionGoodsRebateRuleGoodsMapper.selectByExample(goodsRebateGoodsExample);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

}
