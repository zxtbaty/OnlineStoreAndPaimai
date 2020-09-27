package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGrouponRulesMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallGrouponRulesService {
    @Resource
    private LitemallGrouponRulesMapper mapper;

    @Autowired
    private LitemallGrouponOrderService grouponOrderService;

    /**
     * 获取团购活动列表
     *
     * @param goodsId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallGrouponRules> querySelective(String name, String goodsId, String goodsName,Integer expireFlag,

                                                     Integer page, Integer size, String sort) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.setOrderByClause(sort);
        LitemallGrouponRulesExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andNameLike("%"+name+"%");
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.parseInt(goodsId));
        }
        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%"+goodsName+"%");
        }
        if (expireFlag!=null) {
            if(expireFlag==0){
                criteria.andExpireFlagEqualTo(false);
            } else if(expireFlag==1){
                criteria.andExpireFlagEqualTo(true);
            }
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }


    /**
     * 创建团购活动规则
     * @param rules
     * @return
     */
    public int createRules(LitemallGrouponRules rules) {
        rules.setGroupRemainStore(rules.getGroupMaxStore());
        rules.setAddTime(LocalDateTime.now());
        rules.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(rules);
    }

    /**
     * 更新团购活动规则
     * @param grouponRules
     * @return
     */
    public int updateById(LitemallGrouponRules grouponRules) {
        if(grouponRules.getGroupRemainStore()!=null&&
                grouponRules.getGroupRemainStore()>grouponRules.getGroupMaxStore())
        {
            grouponRules.setGroupRemainStore(grouponRules.getGroupMaxStore());
        }
        grouponRules.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(grouponRules);
    }

    /**
     * 删除团购活动规则
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 根据主键ID查找对应团购项
     *
     * @param id
     * @return
     */
    public LitemallGrouponRules queryById(Integer id) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 查询某个商品关联的团购活动规则
     *
     * @param goodsId
     * @return
     */
    public List<LitemallGrouponRules> queryByGoodsId(Integer goodsId) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 获取团购活动列表
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    public List<LitemallGrouponRules> queryList(Integer page, Integer limit, String sort) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andDeletedEqualTo(false);
        example.setOrderByClause(sort);
        PageHelper.startPage(page, limit);
        return mapper.selectByExample(example);
    }

    /**
     * 判断某个团购活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallGrouponRules rules) {
        return (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()));
    }


    /**
     * 查询状态正常，但是时间已经过期的团购活动,这种类型要更新成已经过期
     *
     * @return
     */
    public List<LitemallGrouponRules> queryHaveExpiredButStateError() {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andExpireFlagEqualTo(false).andExpireTimeLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 查询状态正常，但是剩余库存已经小于等于0，要更新成已经过期
     *
     * @return
     */
    public List<LitemallGrouponRules> queryHaveExpiredButStoreNumEqualZero() {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andExpireFlagEqualTo(false).andGroupMaxStoreLessThanOrEqualTo(0).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 查询状态正常，已经开始秒杀进行的任务列表
     *
     * @return
     */
    public List<LitemallGrouponRules> queryHaveBeginGroupon() {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andExpireFlagEqualTo(false).andBeginDateGreaterThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 判断用户是否已经参与过某个秒杀活动，是否还可以再继续创建订单
     * @param ruleId
     * @param userId
     * @return
     */
    public Boolean checkIfAllowGroupOnNext(Integer ruleId,Integer userId){
        //判断一下团购规则，团购不用说明也是限制单用户的
    	if(userId==null){
    		return false;
    	}
        Boolean ifHave=  grouponOrderService.checkExistByUseridRuleid(userId,ruleId);
        return !ifHave;
    }


    /**
     * 判断商品是否存在某个已经开始但还没有结束的团购活动中,如果在则返回团购规则
     * @param productId
     * @return
     */
    public LitemallGrouponRules queryProductIdIsInGroupon(Integer productId) {
        if(productId==null){
            return null;
        }
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andGoodsProductIdEqualTo(productId).andExpireFlagEqualTo(false).
                andBeginDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        LitemallGrouponRules grouponRules=mapper.selectOneByExample(example);
        return grouponRules;
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的团购活动中,如果在则返回团购规则
     * @param productId
     * @return
     */
    public List<LitemallGrouponRules> queryGoodsProductIdIsInGroupon(Integer goodsId, Integer productId) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        if(productId==null){
            example.or().andGoodsIdEqualTo(goodsId).andExpireFlagEqualTo(false).
                    andBeginDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        } else {

            example.or().andGoodsProductIdEqualTo(productId).andExpireFlagEqualTo(false).
                    andBeginDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        }
        List<LitemallGrouponRules> grouponRules=mapper.selectByExample(example);
        return grouponRules;
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的团购活动中,并且免运费
     * @param productId
     * @return
     */
    public Boolean checkGoodsIsInGroupOnAndFreePost( Integer productId) {
        if(productId==null){
            return false;
        }
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andGoodsProductIdEqualTo(productId).andExpireFlagEqualTo(false).
                andFreePostEqualTo(true).andBeginDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }


    //列出所有正在参与团购活动的商品，并且活动正在进行中
    public List<Integer> queryAllProductIdList() {

        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andBeginDateLessThanOrEqualTo(LocalDateTime.now())
                .andExpireFlagEqualTo(false).andDeletedEqualTo(false);
        List<LitemallGrouponRules> promotionSeckillRules=mapper.selectByExample(example);
        return promotionSeckillRules.stream().map(LitemallGrouponRules::getGoodsProductId).distinct().collect(Collectors.toList());
    }

    //团购加库存
    public Boolean addGoodsStoreNum(Integer id, Integer addGoodsStoreNum) {
        try {
            LitemallGrouponRules grouponRules =  queryById(id);

            grouponRules.setGroupMaxStore(grouponRules.getGroupMaxStore() + addGoodsStoreNum);
            grouponRules.setGroupRemainStore(grouponRules.getGroupRemainStore() + addGoodsStoreNum);

            updateById(grouponRules);
            return true;
        }catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }

    }

}
