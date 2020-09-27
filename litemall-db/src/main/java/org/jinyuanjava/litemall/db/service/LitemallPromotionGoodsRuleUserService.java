package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRuleUserMapper;
import org.jinyuanjava.litemall.db.dao.ViewPromotionGoodsRuleUserMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPromotionGoodsRuleUserService {
    @Resource
    private LitemallPromotionGoodsRuleUserMapper mapper;
    @Resource
    private ViewPromotionGoodsRuleUserMapper viewMapper;
    /**
     * 获取会员专享用户列表
     * @param rule_id
     * @param username
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsRuleUser> querySelective(Integer rule_id, String username,
                                                           Integer page, Integer size, String sort) {
        LitemallPromotionGoodsRuleUserExample example = new LitemallPromotionGoodsRuleUserExample();
        example.setOrderByClause(sort);

        LitemallPromotionGoodsRuleUserExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%"+username+"%");
        }
        if(rule_id!=null)
        {
          criteria.andRuleIdEqualTo(rule_id);
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 添加商品规则下会员
     * @param rules
     * @return
     */
    public int createRulesUser(LitemallPromotionGoodsRuleUser rules) {
        rules.setAddTime(LocalDateTime.now());
        rules.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应专属活动会员
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsRuleUser queryById(Integer id) {
        LitemallPromotionGoodsRuleUserExample example = new LitemallPromotionGoodsRuleUserExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据ID查找对应专属活动会员
     *
     * @param id
     * @return
     */
    public List<LitemallPromotionGoodsRuleUser> queryByRuleId(Integer id) {
        LitemallPromotionGoodsRuleUserExample example = new LitemallPromotionGoodsRuleUserExample();
        example.or().andRuleIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 删除所选的会员列表
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 删除所选的会员列表
     * @param ruleId
     */
    public void deleteByRuleId(Integer ruleId){
        LitemallPromotionGoodsRuleUserExample example = new LitemallPromotionGoodsRuleUserExample();
        example.or().andRuleIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新专享活动会员
     * @param goodsRuleUser
     * @return
     */
    public int updateById(LitemallPromotionGoodsRuleUser goodsRuleUser) {
        goodsRuleUser.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRuleUser);
    }



}
