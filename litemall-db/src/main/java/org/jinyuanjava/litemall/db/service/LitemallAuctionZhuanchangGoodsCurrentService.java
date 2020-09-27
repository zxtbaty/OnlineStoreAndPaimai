package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuctionZhuanchangGoodsCurrentMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangGoodsCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangGoodsCurrentExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAuctionZhuanchangGoodsCurrentService {
    @Resource
    private LitemallAuctionZhuanchangGoodsCurrentMapper mapper;

    /**
     * 获取专场拍卖商品明细用户列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallAuctionZhuanchangGoodsCurrent> querySelective(Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallAuctionZhuanchangGoodsCurrentExample example = new LitemallAuctionZhuanchangGoodsCurrentExample();
        example.setOrderByClause(sort);

        LitemallAuctionZhuanchangGoodsCurrentExample.Criteria criteria = example.createCriteria();

        if(rule_id!=null)
        {
          criteria.andZhuanchangIdEqualTo(rule_id);
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 添加专场拍卖下商品
     * @param goodsRebateGoods
     * @return
     */
    public int createRulesGoods(LitemallAuctionZhuanchangGoodsCurrent goodsRebateGoods) {
        goodsRebateGoods.setAddTime(LocalDateTime.now());
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(goodsRebateGoods);
    }

    /**
     * 根据ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public LitemallAuctionZhuanchangGoodsCurrent queryById(Integer id) {
        LitemallAuctionZhuanchangGoodsCurrentExample example = new LitemallAuctionZhuanchangGoodsCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallAuctionZhuanchangGoodsCurrent> queryByRuleId(Integer id) {
        LitemallAuctionZhuanchangGoodsCurrentExample example = new LitemallAuctionZhuanchangGoodsCurrentExample();
        example.or().andZhuanchangIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
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
        LitemallAuctionZhuanchangGoodsCurrentExample example = new LitemallAuctionZhuanchangGoodsCurrentExample();
        example.or().andZhuanchangIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新品项折扣活动
     * @param goodsRebateGoods
     * @return
     */
    public int updateById(LitemallAuctionZhuanchangGoodsCurrent goodsRebateGoods) {
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRebateGoods);
    }



}
