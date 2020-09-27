package org.jinyuanjava.litemall.db.service;

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
public class LitemallAuctionZhuanchangRuleCurrentService {
    @Resource
    private LitemallAuctionZhuanchangRuleCurrentMapper mapper;

    @Resource
    private LitemallAuctionZhuanchangGoodsCurrentMapper goodsCurrentMapper;

    @Resource
    private ViewAuctionZhuanchangGoodsCurrentMapper viewAuctionZhuanchangGoodsCurrentMapper;



    /**
     * 获取专场活动列表
     * @param zhuanchangName
     * @param expireFlag
     * @param goodsSn
     * @param goodsName
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallAuctionZhuanchangRuleCurrent> querySelective(String zhuanchangName, Integer expireFlag,
                                                                 String goodsSn, String goodsName,
                                                                 Integer page, Integer size, String sort) {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        example.setOrderByClause(sort);

        LitemallAuctionZhuanchangRuleCurrentExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(zhuanchangName)) {
            criteria.andZhuanchangNameLike("%"+zhuanchangName+"%");
        }
        if(expireFlag!=null)
        {
            if(expireFlag==0){
                criteria.andExpireFlagEqualTo(false);
            }else if(expireFlag==1){
                criteria.andExpireFlagEqualTo(true);
            }
        }
        //判断专场活动明细中是否有该商品的信息

        if (!StringUtils.isEmpty(goodsSn)) {
            LitemallAuctionZhuanchangGoodsCurrentExample goodsExample = new LitemallAuctionZhuanchangGoodsCurrentExample();
            goodsExample.or().andGoodsSnEqualTo(goodsSn).andDeletedEqualTo(false);
            List<LitemallAuctionZhuanchangGoodsCurrent> rebateGoodsList=  goodsCurrentMapper.selectByExample(goodsExample);
            List<Integer> mainIdList=rebateGoodsList.stream().map(LitemallAuctionZhuanchangGoodsCurrent::getZhuanchangId).distinct().collect(Collectors.toList());
            if(mainIdList!=null&&mainIdList.size()>0) {
                criteria.andIdIn(mainIdList);
            }
        }

        if (!StringUtils.isEmpty(goodsName)) {
            LitemallAuctionZhuanchangGoodsCurrentExample goodsExample = new LitemallAuctionZhuanchangGoodsCurrentExample();
            goodsExample.or().andGoodsNameLike("%"+goodsName+"%").andDeletedEqualTo(false);
            List<LitemallAuctionZhuanchangGoodsCurrent> rebateGoodsList=  goodsCurrentMapper.selectByExample(goodsExample);
            List<Integer> mainIdList=rebateGoodsList.stream().map(LitemallAuctionZhuanchangGoodsCurrent::getZhuanchangId).distinct().collect(Collectors.toList());
            if(mainIdList!=null&&mainIdList.size()>0) {
                criteria.andIdIn(mainIdList);
            }
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
    public int createRules(LitemallAuctionZhuanchangRuleCurrent rules) {
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应专场活动规则
     *
     * @param id
     * @return
     */
    public LitemallAuctionZhuanchangRuleCurrent queryById(Integer id) {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 判断某个专场活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallAuctionZhuanchangRuleCurrent rules) {
        return (rules == null || rules.getEndTime().isBefore(LocalDateTime.now()));
    }

    /**
     * 查询状态正常，但是时间已经过期的专场活动
     *
     * @return
     */
    public List<LitemallAuctionZhuanchangRuleCurrent> queryHaveExpiredButStateError() {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        example.or()
                .andExpireFlagEqualTo(false)
                .andEnabledEqualTo(true)
                .andEndTimeLessThanOrEqualTo(LocalDateTime.now())
                .andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 查询已经过期的专场活动
     *
     * @return
     */
    public List<LitemallAuctionZhuanchangRuleCurrent> queryHaveExpiredList(Integer page,
                                                                           Integer limit) {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        example.or().andExpireFlagEqualTo(true).andEnabledEqualTo(true).
                andEndTimeLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        PageHelper.startPage(page,limit);
        example.setOrderByClause("end_time desc,add_time desc");
        return mapper.selectByExample(example);
    }


    /**
     * 查询状态正常，已经开始进行的任务列表
     *
     * @return
     */
    public List<LitemallAuctionZhuanchangRuleCurrent> queryHaveBeginZhuanChang(Integer page,
                                                                               Integer limit,String sort) {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        LitemallAuctionZhuanchangRuleCurrentExample.Criteria criteria=example.createCriteria();
        criteria
                .andExpireFlagEqualTo(false)
                .andEnabledEqualTo(true)
                .andBeginTimeLessThan(LocalDateTime.now())
                .andDeletedEqualTo(false);
        if(limit!=null&&limit<999999) {
            PageHelper.startPage(page, limit);
        }
        example.setOrderByClause("display_order,add_time desc");
        List<LitemallAuctionZhuanchangRuleCurrent> result= mapper.selectByExample(example);
        return result;
    }

    /**
     * 查询状态正常，未开始并且预展标记需要预展的专场拍列表
     *
     * @return
     */
    public List<LitemallAuctionZhuanchangRuleCurrent> queryUnBeginAndMayPreview(Integer page,
                                                                                Integer limit) {
        PageHelper.startPage(page,limit);
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        example.or()
                .andExpireFlagEqualTo(false)
                .andEnabledEqualTo(true)
                .andPreviewFlagEqualTo(true)
                .andBeginTimeGreaterThanOrEqualTo(LocalDateTime.now())
                .andDeletedEqualTo(false);
        example.setOrderByClause("display_order,add_time desc");
        return mapper.selectByExample(example);
    }



    /**
     * 删除品项折扣活动规则及对应的商品Id
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
        //删除品项规则下的商品明细
        LitemallAuctionZhuanchangGoodsCurrentExample example = new LitemallAuctionZhuanchangGoodsCurrentExample();
        example.or().andZhuanchangIdEqualTo(id).andDeletedEqualTo(false);
        goodsCurrentMapper.logicalDeleteByExample(example);
    }

    /**
     * 更新专场活动规则
     * @param rule
     * @return
     */
    public int updateById(LitemallAuctionZhuanchangRuleCurrent rule) {
        rule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(rule);
    }

    public boolean checkExistByName(String name,Integer id) {
        LitemallAuctionZhuanchangRuleCurrentExample example = new LitemallAuctionZhuanchangRuleCurrentExample();
        LitemallAuctionZhuanchangRuleCurrentExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andZhuanchangNameEqualTo(name);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);

        return mapper.countByExample(example) != 0;
    }

    public LitemallAuctionZhuanchangRuleCurrent findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }


    /**
     * 判断商品是否存在某个已经开始但还没有结束的专场活动中
     * @param productId
     * @return
     */
    public ViewAuctionZhuanchangGoodsCurrent getRuleByProductId(Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewAuctionZhuanchangGoodsCurrentExample view=new ViewAuctionZhuanchangGoodsCurrentExample();
        ViewAuctionZhuanchangGoodsCurrentExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdEqualTo(productId);
        ViewAuctionZhuanchangGoodsCurrent goodsRebateRuleGoods= viewAuctionZhuanchangGoodsCurrentMapper.selectOneByExample(view);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的专场活动中
     * @param productId
     * @return
     */
    public List<ViewAuctionZhuanchangGoodsCurrent> getRuleByGoodsProductId(Integer goodsId, Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewAuctionZhuanchangGoodsCurrentExample view=new ViewAuctionZhuanchangGoodsCurrentExample();
        ViewAuctionZhuanchangGoodsCurrentExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        if(productId==null){
            criteria.andGoodsIdEqualTo(goodsId);
        } else {
            criteria.andGoodsProductIdEqualTo(productId);
        }
        List<ViewAuctionZhuanchangGoodsCurrent> goodsRebateRuleGoods= viewAuctionZhuanchangGoodsCurrentMapper.selectByExample(view);
        return goodsRebateRuleGoods;
    }

    /**
     * 判断商品列表是否存在某个已经开始但还没有结束的专场活动中
     * @param productIds
     * @return
     */
    public List<ViewAuctionZhuanchangGoodsCurrent> getRuleByProductIds(List<Integer> productIds){
        //判断该商品是否在此会员专属规则中
        ViewAuctionZhuanchangGoodsCurrentExample goodsRebateGoodsExample=new ViewAuctionZhuanchangGoodsCurrentExample();
        ViewAuctionZhuanchangGoodsCurrentExample.Criteria criteria=goodsRebateGoodsExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdIn(productIds);
        List<ViewAuctionZhuanchangGoodsCurrent> goodsRebateRuleGoods= viewAuctionZhuanchangGoodsCurrentMapper.selectByExample(goodsRebateGoodsExample);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

}
