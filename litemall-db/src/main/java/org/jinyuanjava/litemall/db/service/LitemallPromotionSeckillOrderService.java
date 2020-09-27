package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionSeckillOrderMapper;
import org.jinyuanjava.litemall.db.dao.ViewSeckillOrdersMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPromotionSeckillOrderService {
    @Resource
    private LitemallPromotionSeckillOrderMapper mapper;

    @Resource
    private ViewSeckillOrdersMapper viewSeckillOrdersMapper;
    /**
     * 获取用户参与的秒杀记录
     *
     * @param userId
     * @return
     */
    public List<LitemallPromotionSeckillOrder> querySeckillByUser(Integer userId) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
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
    public List<ViewSeckillOrders> getViewQuerySeckillOrders(Integer rulesId, String seckillName, String goodsSn,String goodsName,
       String orderSn,List<Short> orderStatusArray, String username,String userNickname,String userPhone,
        Integer page, Integer size, String sort) {
        ViewSeckillOrdersExample example = new ViewSeckillOrdersExample();
        ViewSeckillOrdersExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(rulesId);
        }
        if (!StringUtils.isEmpty(seckillName)) {
            criteria.andSeckillNameEqualTo(seckillName);
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
            criteria.andUserPhoneEqualTo( userPhone);
        }
        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        example.orderBy("add_time desc");
        return viewSeckillOrdersMapper.selectByExample(example);
    }

    /**
     * 根据OrderId查询秒杀记录
     *
     * @param orderId
     * @return
     */
    public LitemallPromotionSeckillOrder queryByOrderId(Integer orderId) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }



    /**
     * 获取某个秒杀活动参与的记录
     *
     * @param seckillRulesId
     * @return
     */
    public List<LitemallPromotionSeckillOrder> queryJoinRecord(Integer seckillRulesId) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andRulesIdEqualTo(seckillRulesId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallPromotionSeckillOrder queryById(Integer id) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 返回某个发起的秒杀参与人数
     *
     * @param seckillRulesId
     * @return
     */
    public int countSeckill(Integer seckillRulesId) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andRulesIdEqualTo(seckillRulesId).andDeletedEqualTo(false);
        return (int) mapper.countByExample(example);
    }

    /**
     * 更新一个参与秒杀
     * @param litemallPromotionSecillOrder
     * @return
     */
    public int updateById(LitemallPromotionSeckillOrder litemallPromotionSecillOrder) {
        litemallPromotionSecillOrder.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(litemallPromotionSecillOrder);
    }

    /**
     * 创建或参与一个秒杀
     *
     * @param litemallPromotionSecillOrder
     * @return
     */
    public int createSeckill(LitemallPromotionSeckillOrder litemallPromotionSecillOrder) {
        //判断该用户是否已经参与过该秒杀活动，如果已经参与过，则不可以重复参与
//        if(checkExistByUseridRuleid(litemallPromotionSecillOrder.getUserId(),litemallPromotionSecillOrder.getRulesId())==false) {
            litemallPromotionSecillOrder.setAddTime(LocalDateTime.now());
            litemallPromotionSecillOrder.setUpdateTime(LocalDateTime.now());
            return mapper.insertSelective(litemallPromotionSecillOrder);
//        } else
//        {
//            return -1;
//        }
    }


    /**
     * 查询某个秒杀活动里所有发起的秒杀记录，分页
     *
     * @param rulesId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionSeckillOrder> querySelective(String rulesId, Integer page, Integer size, String sort) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        LitemallPromotionSeckillOrderExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }


    /**
     * 查询某个用户是否已经参与过某个秒杀活动
     */
    public boolean checkExistByUseridRuleid(Integer userId,Integer ruleId) {
        LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
        example.or().andUserIdEqualTo(userId).andRulesIdEqualTo(ruleId).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    /**
     * 取消订单时，从秒杀里删除
     */
   public boolean cancelOrderDelete(Integer orderId){
       LitemallPromotionSeckillOrderExample example = new LitemallPromotionSeckillOrderExample();
       example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
       return mapper.deleteByExample(example) != 0;
   }

}
