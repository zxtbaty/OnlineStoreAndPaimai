package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuctionDajiaOrderCurrentMapper;
import org.jinyuanjava.litemall.db.dao.ViewAuctionDajiaOrderCurrentMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAuctionDajiaOrderCurrentService {
    @Resource
    private LitemallAuctionDajiaOrderCurrentMapper mapper;

    @Resource
    private ViewAuctionDajiaOrderCurrentMapper viewAuctionDajiaOrderCurrentMapper;
    /**
     * 获取专场拍卖商品明细用户列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<LitemallAuctionDajiaOrderCurrent> querySelective(Integer user_id, Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallAuctionDajiaOrderCurrentExample example = new LitemallAuctionDajiaOrderCurrentExample();
        example.setOrderByClause(sort);

        LitemallAuctionDajiaOrderCurrentExample.Criteria criteria = example.createCriteria();

        if(user_id!=null)
        {
            criteria.andUserIdEqualTo(rule_id);
        }

        if(rule_id!=null)
        {
          criteria.andRulesIdEqualTo(rule_id);
        }

        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 添加专场拍卖下商品
     * @param dajiaOrderCurrent
     * @return
     */
    public int createRulesGoods(LitemallAuctionDajiaOrderCurrent dajiaOrderCurrent) {
        dajiaOrderCurrent.setAddTime(LocalDateTime.now());
        dajiaOrderCurrent.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(dajiaOrderCurrent);
    }

    /**
     * 根据ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public LitemallAuctionDajiaOrderCurrent queryById(Integer id) {
        LitemallAuctionDajiaOrderCurrentExample example = new LitemallAuctionDajiaOrderCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动订单
     *
     * @param id
     * @return
     */
    public List<LitemallAuctionDajiaOrderCurrent> queryByRuleId(Integer id) {
        LitemallAuctionDajiaOrderCurrentExample example = new LitemallAuctionDajiaOrderCurrentExample();
        example.or().andRulesIdEqualTo(id).andDeletedEqualTo(false);
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
        LitemallAuctionDajiaOrderCurrentExample example = new LitemallAuctionDajiaOrderCurrentExample();
        example.or().andRulesIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新专场拍卖活动订单
     * @param dajiaOrderCurrent
     * @return
     */
    public int updateById(LitemallAuctionDajiaOrderCurrent dajiaOrderCurrent) {
        dajiaOrderCurrent.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(dajiaOrderCurrent);
    }


    /**
     * 获取专场拍卖活动订单视图
     * @param orderSn
     * @param orderStatusArray
     * @param username
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<ViewAuctionDajiaOrderCurrent> getViewAuctionDajiaOrderCurrent(String orderSn,Integer rulesId,
        List<Short> orderStatusArray, String username,Integer page, Integer size, String sort) {
        ViewAuctionDajiaOrderCurrentExample example = new ViewAuctionDajiaOrderCurrentExample();
        ViewAuctionDajiaOrderCurrentExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%" + orderSn + "%");
        }
        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(rulesId);
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUserNameLike("%" + orderSn + "%");
        }

        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }

        example.orderBy("add_time desc");
        return viewAuctionDajiaOrderCurrentMapper.selectByExample(example);
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的专场活动中
     * @param productId
     * @return
     */
    public ViewAuctionDajiaOrderCurrent getRuleByProductId(Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewAuctionDajiaOrderCurrentExample view=new ViewAuctionDajiaOrderCurrentExample();
        ViewAuctionDajiaOrderCurrentExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdEqualTo(productId);
        ViewAuctionDajiaOrderCurrent dajiaOrderCurrent= viewAuctionDajiaOrderCurrentMapper.selectOneByExample(view);
        if(dajiaOrderCurrent!=null){
            return dajiaOrderCurrent;
        } else
        {
            return null;
        }
    }

    /**
     * 判断商品列表是否存在某个已经开始但还没有结束的专场活动中
     * @param productIds
     * @return
     */
    public List<ViewAuctionDajiaOrderCurrent> getRuleByProductIds(List<Integer> productIds){
        //判断该商品是否在此会员专属规则中
        ViewAuctionDajiaOrderCurrentExample dajiaOrderCurrentExample=new ViewAuctionDajiaOrderCurrentExample();
        ViewAuctionDajiaOrderCurrentExample.Criteria criteria=dajiaOrderCurrentExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdIn(productIds);
        List<ViewAuctionDajiaOrderCurrent> goodsRebateRuleGoods= viewAuctionDajiaOrderCurrentMapper.selectByExample(dajiaOrderCurrentExample);
        if(goodsRebateRuleGoods!=null){
            return goodsRebateRuleGoods;
        } else
        {
            return null;
        }
    }

}
