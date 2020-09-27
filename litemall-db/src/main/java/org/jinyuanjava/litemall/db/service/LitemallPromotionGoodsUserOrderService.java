package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.LitemallOrderGoodsMapper;
import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsUserOrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewUserGoodsOrdersMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallPromotionGoodsUserOrderService {
    @Resource
    private LitemallPromotionGoodsUserOrderMapper mapper;

    @Resource
    private LitemallOrderGoodsMapper orderGoodsMapper;

    @Resource
    private ViewUserGoodsOrdersMapper viewUserGoodsOrdersMapper;
    /**
     * 获取用户参与的专享活动记录
     *
     * @param userId
     * @return
     */
    public List<LitemallPromotionGoodsUserOrder> querySeckillByUser(Integer userId) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }


    /**
     * 获取专享活动记录列表，以视图的方式呈现
     * @param goodsSn
     * @param goodsName
     * @param orderSn
     * @param orderStatusArray
     * @param username
     * @return
     */
    public List<ViewUserGoodsOrders> getViewQueryUserGoodsOrders(Integer rulesId,String name,String goodsSn,String goodsName,
     String orderSn,List<Short> orderStatusArray,String username,String userNickname,String userPhone,
       Integer page, Integer size, String sort) {
        ViewUserGoodsOrdersExample example = new ViewUserGoodsOrdersExample();
        ViewUserGoodsOrdersExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(rulesId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo(name);
        }
//        if (!StringUtils.isEmpty(goodsSn)) {
//            criteria.andGoodsSnEqualTo(goodsSn);
//        }
//        if (!StringUtils.isEmpty(goodsName)) {
//            criteria.andGoodsNameLike("%" + goodsName + "%");
//        }
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
            criteria.andUserPhoneEqualTo( userPhone );
        }
        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        example.orderBy("add_time desc");
        return viewUserGoodsOrdersMapper.selectByExample(example);
    }

    /**
     * 根据OrderId查询专享活动记录
     *
     * @param orderId
     * @return
     */
    public LitemallPromotionGoodsUserOrder queryByOrderId(Integer orderId) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }



    /**
     * 获取某个专享活动参与的记录
     *
     * @param rulesId
     * @return
     */
    public List<LitemallPromotionGoodsUserOrder> queryJoinRecord(Integer rulesId) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andRulesIdEqualTo(rulesId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsUserOrder queryById(Integer id) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 返回某个发起的专享活动参与人数
     *
     * @param rulesId
     * @return
     */
    public int countSeckill(Integer rulesId) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andRulesIdEqualTo(rulesId).andDeletedEqualTo(false);
        return (int) mapper.countByExample(example);
    }

    /**
     * 更新一个参与秒杀
     * @param userGoodsOrder
     * @return
     */
    public int updateById(LitemallPromotionGoodsUserOrder userGoodsOrder) {
        userGoodsOrder.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(userGoodsOrder);
    }

    /**
     * 创建或参与一个会员专享活动
     *
     * @param userGoodsOrder
     * @return
     */
    public int createUserGoodsRuleOrder(LitemallPromotionGoodsUserOrder userGoodsOrder) {
        //判断该用户是否已经参与过该会员专享活动，如果已经参与过，则不可以重复参与
//        if(checkExistByUseridRuleid(userGoodsOrder.getUserId(),userGoodsOrder.getRulesId())==false) {
            userGoodsOrder.setAddTime(LocalDateTime.now());
            userGoodsOrder.setUpdateTime(LocalDateTime.now());
            return mapper.insertSelective(userGoodsOrder);
//        } else
//        {
//            return -1;
//        }
    }


    /**
     * 查询某个会员专享活动里所有发起的记录，分页
     *
     * @param rulesId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsUserOrder> querySelective(String rulesId, Integer page, Integer size, String sort) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        LitemallPromotionGoodsUserOrderExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }




    /**
     * 查询某个用户是否已经参与过某个会员专享活动
     */
    public boolean checkExistByUseridRuleid(Integer userId,Integer ruleId) {
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andUserIdEqualTo(userId).andRulesIdEqualTo(ruleId).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    /**
     * 查询某个用户是否已经参与过某个会员专享活动,并且购买过该商品
     */
    public boolean checkExistByUseridRuleid(Integer userId,Integer ruleId,Integer productId) {

        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andUserIdEqualTo(userId).andRulesIdEqualTo(ruleId).andDeletedEqualTo(false);
        List<LitemallPromotionGoodsUserOrder> goodsUserOrders=  mapper.selectByExample(example);
        List<Integer> orderIdList=goodsUserOrders.stream().map(LitemallPromotionGoodsUserOrder::getOrderId).distinct().collect(Collectors.toList());

        if(orderIdList==null||orderIdList.size()==0){
            return false;
        }

        //判断订单明细是否包含这个商品
        LitemallOrderGoodsExample orderGoodsExample=new LitemallOrderGoodsExample();
        orderGoodsExample.or().andOrderIdIn(orderIdList).andDeletedEqualTo(false).andProductIdEqualTo(productId);

        return orderGoodsMapper.countByExample(orderGoodsExample) != 0;
    }


    public void deleteById(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }


    /**
     * 取消订单时，从会员商品单里删除
     */
    public boolean cancelOrderDelete(Integer orderId){
        LitemallPromotionGoodsUserOrderExample example = new LitemallPromotionGoodsUserOrderExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.deleteByExample(example) != 0;
    }
}
