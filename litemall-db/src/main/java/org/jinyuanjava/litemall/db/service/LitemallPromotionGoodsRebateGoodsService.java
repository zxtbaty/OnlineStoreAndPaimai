package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRebateGoodsMapper;
import org.jinyuanjava.litemall.db.dao.ViewPromotionGoodsRebateRuleGoodsMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPromotionGoodsRebateGoodsService {
    @Resource
    private LitemallPromotionGoodsRebateGoodsMapper mapper;
    @Resource
    private ViewPromotionGoodsRebateRuleGoodsMapper viewMapper;
    /**
     * 获取品项折扣商品明细用户列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsRebateGoods> querySelective(Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallPromotionGoodsRebateGoodsExample example = new LitemallPromotionGoodsRebateGoodsExample();
        example.setOrderByClause(sort);

        LitemallPromotionGoodsRebateGoodsExample.Criteria criteria = example.createCriteria();

        if(rule_id!=null)
        {
          criteria.andRuleIdEqualTo(rule_id);
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 添加品项折扣下商品
     * @param goodsRebateGoods
     * @return
     */
    public int createRulesGoods(LitemallPromotionGoodsRebateGoods goodsRebateGoods) {
        goodsRebateGoods.setAddTime(LocalDateTime.now());
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(goodsRebateGoods);
    }

    /**
     * 根据ID查找对应品项折扣活动会员
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsRebateGoods queryById(Integer id) {
        LitemallPromotionGoodsRebateGoodsExample example = new LitemallPromotionGoodsRebateGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应品项折扣活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallPromotionGoodsRebateGoods> queryByRuleId(Integer id) {
        LitemallPromotionGoodsRebateGoodsExample example = new LitemallPromotionGoodsRebateGoodsExample();
        example.or().andRuleIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 根据活动ID查找对应品项折扣活动商品
     *
     * @param id
     * @return
     */
    public List<ViewPromotionGoodsRebateRuleGoods> getViewDetailsByRuleId(Integer id) {
        ViewPromotionGoodsRebateRuleGoodsExample example = new ViewPromotionGoodsRebateRuleGoodsExample();
        example.or().andRuleIdEqualTo(id).andDeletedEqualTo(false);
        return viewMapper.selectByExample(example);
    }


    /**
     * 删除所选的品项折扣记录Id
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 按业务规则，删除所有的品项折扣商品列表
     * @param ruleId
     */
    public void deleteByRuleId(Integer ruleId){
        LitemallPromotionGoodsRebateGoodsExample example = new LitemallPromotionGoodsRebateGoodsExample();
        example.or().andRuleIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新品项折扣活动
     * @param goodsRebateGoods
     * @return
     */
    public int updateById(LitemallPromotionGoodsRebateGoods goodsRebateGoods) {
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRebateGoods);
    }



}
