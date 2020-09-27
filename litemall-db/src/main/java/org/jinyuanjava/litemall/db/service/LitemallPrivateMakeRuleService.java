package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPrivateMakeRuleMapper;
import org.jinyuanjava.litemall.db.domain.LitemallPrivateMakeRule;
import org.jinyuanjava.litemall.db.domain.LitemallPrivateMakeRuleExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPrivateMakeRuleService {

    @Resource
    private LitemallPrivateMakeRuleMapper mapper;

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
    public List<LitemallPrivateMakeRule> querySelective(Integer expireFlag,
        Integer enabled, Integer offerFlag,String goodsSn, String goodsName, Integer page, Integer size, String sort) {
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
        example.setOrderByClause(sort);
        LitemallPrivateMakeRuleExample.Criteria criteria = example.createCriteria();

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
    public int createRules(LitemallPrivateMakeRule rules) {
        rules.setAddTime(LocalDateTime.now());
        rules.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应专场活动规则
     *
     * @param id
     * @return
     */
    public LitemallPrivateMakeRule queryById(Integer id) {
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据分类名称查询大家拍的商品 已经开始执行
     *
     * @param className
     * @return
     */
    public List<LitemallPrivateMakeRule> queryByPrivateMakeClass(String className,Integer page,
       Integer size) {
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
        LitemallPrivateMakeRuleExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(className)&&!className.equals("全部")){
            criteria.andPrivateCategoryNameEqualTo(className);
        }
        criteria.andDeletedEqualTo(false)
                .andEnabledEqualTo(true)
                .andBeginTimeLessThan(LocalDateTime.now())
                .andExpireFlagEqualTo(false);
        example.setOrderByClause("display_order,add_time desc");
        if(size!=null&&size<999999){
            PageHelper.startPage(page,size);
        }
        return mapper.selectByExample(example);
    }


    /**
     * 判断某个大家拍活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallPrivateMakeRule rules) {
        return (rules == null || rules.getEndTime().isBefore(LocalDateTime.now()));
    }

    /**
     * 查询状态正常，但是时间已经过期的私人定制活动
     *
     * @return
     */
    public List<LitemallPrivateMakeRule> queryHaveExpiredButStateError() {
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
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
    public List<LitemallPrivateMakeRule> queryHaveBeginPrivateMake() {
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
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
    public int updateById(LitemallPrivateMakeRule rule) {
        rule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(rule);
    }

    public LitemallPrivateMakeRule findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    public List<LitemallPrivateMakeRule> findByGoodsId(Integer goodsId){
        LitemallPrivateMakeRuleExample example = new LitemallPrivateMakeRuleExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);

    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的私人定制活动中
     * @param productId
     * @return
     */
    public List<LitemallPrivateMakeRule> getRuleByGoodsProductId(Integer goodsId, Integer productId){
        //判断该商品是否在此会员专属规则中
        LitemallPrivateMakeRuleExample view=new LitemallPrivateMakeRuleExample();
        LitemallPrivateMakeRuleExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        if(productId==null){
            criteria.andGoodsIdEqualTo(goodsId);
        } else {
            criteria.andGoodsProductIdEqualTo(productId);
        }
        List<LitemallPrivateMakeRule> privateMakeRuleList= mapper.selectByExample(view);
        return privateMakeRuleList;
    }



}
