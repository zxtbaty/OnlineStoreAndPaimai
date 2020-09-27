package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallPromotionSeckillRuleMapper;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionSeckillRule;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionSeckillRuleExample;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;

@Service
public class LitemallPromotionSeckillRuleService {
    @Resource
    private LitemallPromotionSeckillRuleMapper mapper;

    @Resource
    private LitemallPromotionSeckillOrderService promotionSeckillOrderService;

    /**
     * 获取秒杀活动列表
     *
     * @param goodsId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionSeckillRule> querySelective(String seckillName,Integer seckillExpireFlag,
                                                             String goodsId,String goodsName,
                                                             Integer page, Integer size, String sort) {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.setOrderByClause(sort);

        LitemallPromotionSeckillRuleExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(seckillName)) {
            criteria.andSeckillNameLike("%"+seckillName+"%");
        }
        if(seckillExpireFlag!=null)
        {
            if(seckillExpireFlag==0){
                criteria.andSeckillExpireFlagEqualTo(false);
            }else if(seckillExpireFlag==1){
                criteria.andSeckillExpireFlagEqualTo(true);
            }
        }

        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.parseInt(goodsId));
        }

        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%"+goodsName+"%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 创建秒杀活动规则
     * @param rules
     * @return
     */
    public int createRules(LitemallPromotionSeckillRule rules) {
        rules.setSeckillRemainNum(rules.getSeckillStoreNum());
        rules.setAddTime(LocalDateTime.now());
        rules.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应秒杀活动规则
     *
     * @param id
     * @return
     */
    public LitemallPromotionSeckillRule queryById(Integer id) {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 查询某个商品关联的秒杀活动规则
     *
     * @param goodsId
     * @return
     */
    public List<LitemallPromotionSeckillRule> queryByGoodsId(Integer goodsId) {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 判断某个秒杀活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallPromotionSeckillRule rules) {
        return (rules == null || rules.getSeckillEndDate().isBefore(LocalDateTime.now()));
    }

    /**
     * 查询状态正常，但是时间已经过期的优惠券,这种类型的秒杀状态，要更新成已经过期
     *
     * @return
     */
    public List<LitemallPromotionSeckillRule> queryHaveExpiredButStateError() {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andSeckillExpireFlagEqualTo(false).andSeckillEndDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 查询状态正常，但是剩余库存已经小于等于0，要更新成已经过期
     *
     * @return
     */
    public List<LitemallPromotionSeckillRule> queryHaveExpiredButStoreNumEqualZero() {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andSeckillExpireFlagEqualTo(false).andSeckillRemainNumLessThanOrEqualTo(0).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 查询状态正常，已经开始秒杀进行的任务列表
     *
     * @return
     */
    public List<LitemallPromotionSeckillRule> queryHaveBeginSeckill() {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andSeckillExpireFlagEqualTo(false).andSeckillBeginDateGreaterThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 删除所选的商品
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 更新秒杀活动规则
     * @param litemallPromotionSecillRule
     * @return
     */
    public int updateById(LitemallPromotionSeckillRule litemallPromotionSecillRule) {
        if(litemallPromotionSecillRule.getSeckillRemainNum()!=null&&
                litemallPromotionSecillRule.getSeckillRemainNum()>litemallPromotionSecillRule.getSeckillStoreNum())
        {
            litemallPromotionSecillRule.setSeckillRemainNum(litemallPromotionSecillRule.getSeckillStoreNum());
        }
        litemallPromotionSecillRule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(litemallPromotionSecillRule);
    }

    //判断某商品Id是否参与秒杀活动，某商品的某一款式，并且活动正在进行中
	public List<LitemallPromotionSeckillRule> queryGoodsIsInSkill( Integer goodsId) {
		if(goodsId==null){
			return null;
		}
		LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
		example.or().andGoodsIdEqualTo(goodsId).andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
		return mapper.selectByExample(example);
	}

    //判断某商品Id是否参与秒杀活动，某商品的某一款式，并且活动正在进行中
    public List<LitemallPromotionSeckillRule> queryGoodsProductIdIsInSkill(Integer goodsId,Integer productId) {
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        if(productId==null){
            example.or().andGoodsIdEqualTo(goodsId).andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now())
                    .andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
        } else {
            example.or().andGoodsProductIdEqualTo(productId).andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now())
                    .andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
        }
        List<LitemallPromotionSeckillRule> promotionSeckillRule=mapper.selectByExample(example);
        return promotionSeckillRule;
    }

    //判断某商品Id是否参与秒杀活动，某商品的某一款式，并且活动正在进行中
    public LitemallPromotionSeckillRule queryProductIdIsInSkill(Integer productId) {
        if(productId==null){
            return null;
        }
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andGoodsProductIdEqualTo(productId).andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now())
                .andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
        LitemallPromotionSeckillRule promotionSeckillRule=mapper.selectOneByExample(example);
        return promotionSeckillRule;
    }

    //列出所有正在参与秒杀活动的商品，并且活动正在进行中
    public List<Integer> queryAllProductIdList() {

        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now())
                .andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
        List<LitemallPromotionSeckillRule> promotionSeckillRules=mapper.selectByExample(example);
        return promotionSeckillRules.stream().map(LitemallPromotionSeckillRule::getGoodsProductId).distinct().collect(Collectors.toList());
    }

    /**
     * 判断用户是否已经参与过某个秒杀活动，是否还可以再继续创建订单
     * @param ruleId
     * @param userId
     * @return
     */
    public Boolean checkIfAllowSeckillNext(Integer ruleId,Integer userId){
         //判断一下秒杀规则，如果限制单用户，再判断是否已经创建订单
        LitemallPromotionSeckillRule promotionSeckillRule=mapper.selectByPrimaryKey(ruleId);
        if(promotionSeckillRule.getSeckillOnlyOne()==false){
            return true;
        }
        Boolean ifHave=  promotionSeckillOrderService.checkExistByUseridRuleid(userId,ruleId);
        return !ifHave;
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的秒杀活动中,并且免运费
     * @param productId
     * @return
     */
    public Boolean checkGoodsIsInSkillAndFreePost( Integer productId) {
        if(productId==null){
            return false;
        }
        LitemallPromotionSeckillRuleExample example = new LitemallPromotionSeckillRuleExample();
        example.or().andGoodsProductIdEqualTo(productId).andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now()).
                andFreePostEqualTo(true).andSeckillExpireFlagEqualTo(false).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    public Boolean addGoodsStoreNum(Integer id, Integer addGoodsStoreNum) {
        try {
            LitemallPromotionSeckillRule seckillRule =  queryById(id);

            seckillRule.setSeckillStoreNum(seckillRule.getSeckillRemainNum() + addGoodsStoreNum);
            seckillRule.setSeckillRemainNum(seckillRule.getSeckillRemainNum() + addGoodsStoreNum);

            updateById(seckillRule);
            return true;
        }catch (Exception ex){
            System.out.print(ex.getMessage());
            return false;
        }

    }


}
