package org.jinyuanjava.litemall.db.service;


import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGrouponOrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewGrouponOrdersMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGrouponOrderService {
    @Resource
    private LitemallGrouponOrderMapper mapper;
    @Resource
    private ViewGrouponOrdersMapper viewGrouponOrdersMapper;

    /**
     * 获取用户参与的团购记录
     *
     * @param userId
     * @return
     */
    public List<LitemallGrouponOrder> queryMyJoinGroupon(Integer userId) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据OrderId查询团购记录
     *
     * @param orderId
     * @return
     */
    public LitemallGrouponOrder queryByOrderId(Integer orderId) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 获取某个团购活动参与的记录
     *
     * @param id
     * @return
     */
    public List<LitemallGrouponOrder> queryJoinRecord(Integer id) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andRulesIdEqualTo(id).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallGrouponOrder queryById(Integer id) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 返回某个发起的团购参与人数
     *
     * @param ruleId
     * @return
     */
    public int countGroupon(Integer ruleId) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andRulesIdEqualTo(ruleId).andDeletedEqualTo(false);
        return (int) mapper.countByExample(example);
    }

    public int updateById(LitemallGrouponOrder groupon) {
        groupon.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(groupon);
    }

    /**
     * 创建或参与一个团购
     *
     * @param groupon
     * @return
     */
    public int createGroupon(LitemallGrouponOrder groupon) {
//        if(checkExistByUseridRuleid(groupon.getUserId(),groupon.getRulesId())==false) {
            groupon.setAddTime(LocalDateTime.now());
            groupon.setUpdateTime(LocalDateTime.now());
            return mapper.insertSelective(groupon);
//        } else
//        {
//            return -1;
//        }
    }


    /**
     * 查询所有发起的团购记录
     *
     * @param rulesId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallGrouponOrder> querySelective(String rulesId, Integer page, Integer size, String sort) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        LitemallGrouponOrderExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);
//        criteria.andPayedEqualTo(true);
//        criteria.andGrouponIdEqualTo(0);
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        return mapper.selectByExample(example);
    }


    /**
     * 获取秒杀记录列表，以视图的方式呈现
     * @param goodsSn
     * @param goodsName
     * @param orderSn
     * @param orderStatusArray
     * @param username
     * @return
     */
    public List<ViewGrouponOrders> getViewQueryGrouponOrders(Integer rulesId,String name,String goodsSn, String goodsName,
       String orderSn, List<Short> orderStatusArray, String username,String userNickname,String userPhone,
       Integer page, Integer size, String sort) {
        ViewGrouponOrdersExample example = new ViewGrouponOrdersExample();
        ViewGrouponOrdersExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(rulesId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo(name);
        }
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%" + orderSn + "%");
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnLike("%" + orderSn + "%");
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(userNickname)) {
            criteria.andUserNicknameLike("%" + userNickname + "%");
        }
        if (!StringUtils.isEmpty(userPhone)) {
            criteria.andUserPhoneEqualTo(userPhone);
        }
        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        example.orderBy("add_time desc");
        return viewGrouponOrdersMapper.selectByExample(example);
    }

    /**
     * 查询某个用户是否已经参与过某个团购活动
     */
    public boolean checkExistByUseridRuleid(Integer userId,Integer ruleId) {
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andUserIdEqualTo(userId).andRulesIdEqualTo(ruleId).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    /**
     * 取消订单时，从秒杀里删除
     */
    public boolean cancelOrderDelete(Integer orderId){
        LitemallGrouponOrderExample example = new LitemallGrouponOrderExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.deleteByExample(example) != 0;
    }
}
