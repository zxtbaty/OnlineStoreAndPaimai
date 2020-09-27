package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPrivateMakeOrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewPrivateMakeOrderMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPrivateMakeOrderService {
    @Resource
    private LitemallPrivateMakeOrderMapper mapper;

    @Resource
    private ViewPrivateMakeOrderMapper viewPrivateMakeOrderMapper;
    /**
     * 获取专场拍卖商品明细用户列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<LitemallPrivateMakeOrder> querySelective(Integer user_id, Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallPrivateMakeOrderExample example = new LitemallPrivateMakeOrderExample();
        example.setOrderByClause(sort);

        LitemallPrivateMakeOrderExample.Criteria criteria = example.createCriteria();

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
     * @param privateMakeOrder
     * @return
     */
    public int create(LitemallPrivateMakeOrder privateMakeOrder) {
        privateMakeOrder.setAddTime(LocalDateTime.now());
        privateMakeOrder.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(privateMakeOrder);
    }

    /**
     * 根据ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public LitemallPrivateMakeOrder queryById(Integer id) {
        LitemallPrivateMakeOrderExample example = new LitemallPrivateMakeOrderExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动订单
     *
     * @param id
     * @return
     */
    public List<LitemallPrivateMakeOrder> queryByRuleId(Integer id) {
        LitemallPrivateMakeOrderExample example = new LitemallPrivateMakeOrderExample();
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
        LitemallPrivateMakeOrderExample example = new LitemallPrivateMakeOrderExample();
        example.or().andRulesIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新专场拍卖活动订单
     * @param privateMakeOrder
     * @return
     */
    public int updateById(LitemallPrivateMakeOrder privateMakeOrder) {
        privateMakeOrder.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(privateMakeOrder);
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
    public List<ViewPrivateMakeOrder> getViewPrivateMakeOrder(
        String userPhone,String orderSn,Integer rulesId,
        List<Short> orderStatusArray, String username,String goodsSn,String goodsName, Integer page, Integer size, String sort) {
        ViewPrivateMakeOrderExample example = new ViewPrivateMakeOrderExample();
        ViewPrivateMakeOrderExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userPhone)) {
            criteria.andUserPhoneEqualTo(userPhone);
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%" + orderSn + "%");
        }
        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(rulesId);
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUserNameLike("%" + orderSn + "%");
        }
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnLike(goodsSn);
        }

        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%" + goodsName + "%");
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
        return viewPrivateMakeOrderMapper.selectByExample(example);
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的专场活动中
     * @param productId
     * @return
     */
    public ViewPrivateMakeOrder getRuleByProductId(Integer productId){
        //判断该商品是否在此会员专属规则中
        ViewPrivateMakeOrderExample view=new ViewPrivateMakeOrderExample();
        ViewPrivateMakeOrderExample.Criteria criteria=view.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdEqualTo(productId);
        ViewPrivateMakeOrder privateMakeOrder= viewPrivateMakeOrderMapper.selectOneByExample(view);
        if(privateMakeOrder!=null){
            return privateMakeOrder;
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
    public List<ViewPrivateMakeOrder> getRuleByProductIds(List<Integer> productIds){
        //判断该商品是否在此会员专属规则中
        ViewPrivateMakeOrderExample privateMakeOrderExample=new ViewPrivateMakeOrderExample();
        ViewPrivateMakeOrderExample.Criteria criteria=privateMakeOrderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andGoodsProductIdIn(productIds);
        List<ViewPrivateMakeOrder> privateMakeOrders= viewPrivateMakeOrderMapper.selectByExample(privateMakeOrderExample);
        if(privateMakeOrders!=null){
            return privateMakeOrders;
        } else
        {
            return null;
        }
    }

}
