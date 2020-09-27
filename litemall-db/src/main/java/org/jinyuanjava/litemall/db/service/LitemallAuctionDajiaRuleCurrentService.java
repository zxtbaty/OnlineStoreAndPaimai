package org.jinyuanjava.litemall.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallAuctionDajiaRuleCurrentService {

    @Resource
    private LitemallAuctionDajiaRuleCurrentMapper mapper;

    @Autowired
    private LitemallAuctionZhuanchangRuleCurrentService zhuanchangRuleCurrentService;

    @Resource
    private LitemallAuctionZhuanchangGoodsCurrentMapper zhuanchangGoodsCurrentMapper;

    /**
     * 获取大家拍列表
     * @param expireFlag
     * @param goodsSn
     * @param goodsName
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<LitemallAuctionDajiaRuleCurrent> querySelective(Integer expireFlag,
        Integer enabled,Integer offerFlag,
        String goodsSn, String goodsName,Integer page, Integer size, String sort) {
        LitemallAuctionDajiaRuleCurrentExample example = new LitemallAuctionDajiaRuleCurrentExample();
        example.setOrderByClause(sort);
        LitemallAuctionDajiaRuleCurrentExample.Criteria criteria = example.createCriteria();

        if(expireFlag!=null)
        {
            if(expireFlag==0){
                criteria.andExpireFlagEqualTo(false);
            }else if(expireFlag==1){
                criteria.andExpireFlagEqualTo(true);
            }
        }
        if(enabled!=null)
        {
            if(enabled==0){
                criteria.andEnabledEqualTo(false);
            }else if(enabled==1){
                criteria.andEnabledEqualTo(true);
            }
        }
        if(offerFlag!=null)
        {
            if(offerFlag==0){
                criteria.andOfferFlagEqualTo(false);
            }else if(offerFlag==1){
                criteria.andOfferFlagEqualTo(true);
            }
        }
        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(goodsName)){
            criteria.andGoodsNameLike("%"+goodsName+"%");
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 创建专场活动规则
     * @param rules
     * @return
     */
    public int createRules(LitemallAuctionDajiaRuleCurrent rules) {

        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找大家拍规则
     *
     * @param id
     * @return
     */
    public LitemallAuctionDajiaRuleCurrent queryById(Integer id) {
        LitemallAuctionDajiaRuleCurrentExample example = new LitemallAuctionDajiaRuleCurrentExample();

        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 根据分类名称查询大家拍的商品 已经开始执行
     *
     * @param className
     * @return
     */
    public List<LitemallAuctionDajiaRuleCurrent> queryByDajiaPaiClass(String className,
      Integer page,Integer size, String sort) {
        LitemallAuctionDajiaRuleCurrentExample example = new LitemallAuctionDajiaRuleCurrentExample();
        LitemallAuctionDajiaRuleCurrentExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(className)&&!className.equals("全部")){
            criteria.andDajiapaiCategoryNameEqualTo(className);
        }

        criteria.andDeletedEqualTo(false)
        .andEnabledEqualTo(true)
        .andBeginTimeLessThan(LocalDateTime.now())
        .andExpireFlagEqualTo(false);

        example.setOrderByClause("display_order,add_time desc");


        //如果在专场拍里存在，则不要显示在大家拍里
        List<LitemallAuctionZhuanchangRuleCurrent> paimaiIngs=
                zhuanchangRuleCurrentService.queryHaveBeginZhuanChang(page,999999,sort);
        if(paimaiIngs!=null&&paimaiIngs.size()>0){
            List<Integer> ruleIdList=paimaiIngs.stream().map(LitemallAuctionZhuanchangRuleCurrent::getId).distinct().collect(Collectors.toList());
            LitemallAuctionZhuanchangGoodsCurrentExample goodsCurrentExample=new LitemallAuctionZhuanchangGoodsCurrentExample();
            goodsCurrentExample.or().andDeletedEqualTo(false).andZhuanchangIdIn(ruleIdList);
            List<LitemallAuctionZhuanchangGoodsCurrent> goodsCurrentList= zhuanchangGoodsCurrentMapper.selectByExample(goodsCurrentExample);
            if(goodsCurrentList!=null&&goodsCurrentList.size()>0){
                List<Integer> goodsProductIdList=goodsCurrentList.stream().map(LitemallAuctionZhuanchangGoodsCurrent::getGoodsProductId).distinct().collect(Collectors.toList());
                criteria.andGoodsProductIdNotIn(goodsProductIdList);
            }
        }

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }


    /**
     * 判断某个大家拍活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallAuctionDajiaRuleCurrent rules) {
        return (rules == null || rules.getEndTime().isBefore(LocalDateTime.now()));
    }

    /**
     * 查询状态正常，但是时间已经过期的大家拍活动
     *
     * @return
     */
    public List<LitemallAuctionDajiaRuleCurrent> queryHaveExpiredButStateError() {
        LitemallAuctionDajiaRuleCurrentExample example = new LitemallAuctionDajiaRuleCurrentExample();
        example.or()
                .andExpireFlagEqualTo(false)
                .andEnabledEqualTo(true)
                .andEndTimeLessThanOrEqualTo(LocalDateTime.now())
                .andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 查询状态正常，已经开始进行的任务列表
     *
     * @return
     */
    public List<LitemallAuctionDajiaRuleCurrent> queryHaveBeginZhuanChang() {
        LitemallAuctionDajiaRuleCurrentExample example = new LitemallAuctionDajiaRuleCurrentExample();
        example.or().andExpireFlagEqualTo(false).andBeginTimeGreaterThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 删除大家拍活动规则及对应的商品Id
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);

    }

    /**
     * 更新大家拍活动规则
     * @param rule
     * @return
     */
    public int updateById(LitemallAuctionDajiaRuleCurrent rule) {
        rule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(rule);
    }

    public LitemallAuctionDajiaRuleCurrent findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的大家拍活动中
     * @param productId
     * @return
     */
    public List<LitemallAuctionDajiaRuleCurrent> getRuleByGoodsProductId(Integer goodsId, Integer productId){
        //判断该商品是否在此会员专属规则中
        LitemallAuctionDajiaRuleCurrentExample view=new LitemallAuctionDajiaRuleCurrentExample();
        LitemallAuctionDajiaRuleCurrentExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        if(productId==null){
            criteria.andGoodsIdEqualTo(goodsId);
        } else {
            criteria.andGoodsProductIdEqualTo(productId);
        }
        List<LitemallAuctionDajiaRuleCurrent> dajiaRuleCurrentList= mapper.selectByExample(view);
        return dajiaRuleCurrentList;
    }


}
