package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAucitonZhuanchangOrderCurrentMapper;
import org.jinyuanjava.litemall.db.dao.ViewAucitonZhuanchangOrderCurrentMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAucitonZhuanchangOrderCurrentService {
    @Resource
    private LitemallAucitonZhuanchangOrderCurrentMapper mapper;
    @Resource
    private ViewAucitonZhuanchangOrderCurrentMapper viewOrderMapper;

    /**
     * 获取专场拍卖商品明细用户列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<LitemallAucitonZhuanchangOrderCurrent> querySelective(Integer user_id, Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallAucitonZhuanchangOrderCurrentExample example = new LitemallAucitonZhuanchangOrderCurrentExample();
        example.setOrderByClause(sort);

        LitemallAucitonZhuanchangOrderCurrentExample.Criteria criteria = example.createCriteria();

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
     * @param goodsRebateGoods
     * @return
     */
    public int createRulesGoods(LitemallAucitonZhuanchangOrderCurrent goodsRebateGoods) {
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
    public LitemallAucitonZhuanchangOrderCurrent queryById(Integer id) {
        LitemallAucitonZhuanchangOrderCurrentExample example = new LitemallAucitonZhuanchangOrderCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动订单
     *
     * @param id
     * @return
     */
    public List<LitemallAucitonZhuanchangOrderCurrent> queryByRuleId(Integer id) {
        LitemallAucitonZhuanchangOrderCurrentExample example = new LitemallAucitonZhuanchangOrderCurrentExample();
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
        LitemallAucitonZhuanchangOrderCurrentExample example = new LitemallAucitonZhuanchangOrderCurrentExample();
        example.or().andRulesIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新专场拍卖活动订单
     * @param goodsRebateGoods
     * @return
     */
    public int updateById(LitemallAucitonZhuanchangOrderCurrent goodsRebateGoods) {
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRebateGoods);
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
    public List<ViewAucitonZhuanchangOrderCurrent> getViewAucitonZhuanchangOrderCurrent(String orderSn,
         List<Short> orderStatusArray, String username,Integer page, Integer size, String sort) {
        ViewAucitonZhuanchangOrderCurrentExample example = new ViewAucitonZhuanchangOrderCurrentExample();
        ViewAucitonZhuanchangOrderCurrentExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%" + orderSn + "%");
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUserNameLike("%" + orderSn + "%");
        }

        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }

        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }

        example.orderBy("add_time desc");
        return viewOrderMapper.selectByExample(example);
    }



}
