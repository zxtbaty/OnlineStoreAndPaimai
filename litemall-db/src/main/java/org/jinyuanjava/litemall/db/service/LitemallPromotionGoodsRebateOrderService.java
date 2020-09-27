package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRebateOrderGoodsMapper;
import org.jinyuanjava.litemall.db.dao.ViewPromotionGoodsRebateRuleOrderGoodsMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPromotionGoodsRebateOrderService {
    @Resource
    private LitemallPromotionGoodsRebateOrderGoodsMapper mapper;

    @Resource
    private ViewPromotionGoodsRebateRuleOrderGoodsMapper viewPromotionGoodsRebateRuleOrderGoodsMapper;


    /**
     * 获取品项折扣活动记录列表，以视图的方式呈现
     * @param goodsSn
     * @param goodsName
     * @param orderSn
     * @param orderStatusArray
     * @return
     */
    public List<ViewPromotionGoodsRebateRuleOrderGoods> getViewQueryUserGoodsOrders(Integer ruleId, String goodsSn,
          String goodsName,String orderSn, String username,
          String userPhone,String userNickname,List<Short> orderStatusArray,
          Integer page, Integer size, String sort) {
        ViewPromotionGoodsRebateRuleOrderGoodsExample example = new ViewPromotionGoodsRebateRuleOrderGoodsExample();
        ViewPromotionGoodsRebateRuleOrderGoodsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(ruleId)) {
            criteria.andRuleIdEqualTo(ruleId);
        }
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo( orderSn );
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(userPhone)) {
            criteria.andUserPhoneEqualTo(userPhone);
        }
        if (!StringUtils.isEmpty(userNickname)) {
            criteria.andUserNicknameLike("%" +userNickname+ "%");
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }

        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        example.orderBy("add_time desc");
        return viewPromotionGoodsRebateRuleOrderGoodsMapper.selectByExample(example);
    }

    /**
     * 根据OrderId查询品项折扣活动记录
     *
     * @param orderId
     * @return
     */
    public List<LitemallPromotionGoodsRebateOrderGoods> queryByOrderId(Integer orderId) {
        LitemallPromotionGoodsRebateOrderGoodsExample example = new LitemallPromotionGoodsRebateOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }



    /**
     * 获取某个品项折扣活动参与的记录
     *
     * @param rulesId
     * @return
     */
    public List<LitemallPromotionGoodsRebateOrderGoods> queryJoinRecord(Integer rulesId) {
        LitemallPromotionGoodsRebateOrderGoodsExample example = new LitemallPromotionGoodsRebateOrderGoodsExample();
        example.or().andRuleIdEqualTo(rulesId).andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsRebateOrderGoods queryById(Integer id) {
        LitemallPromotionGoodsRebateOrderGoodsExample example = new LitemallPromotionGoodsRebateOrderGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 更新一个品项折扣活动
     * @param goodsRebateOrderGoods
     * @return
     */
    public int updateById(LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        goodsRebateOrderGoods.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRebateOrderGoods);
    }

    /**
     * 创建或参与一个品项折扣活动
     *
     * @param goodsRebateOrderGoods
     * @return
     */
    public int createGoodsRebateOrderGoods(LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        //用户可以不受限制多次下单
        goodsRebateOrderGoods.setAddTime(LocalDateTime.now());
        goodsRebateOrderGoods.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(goodsRebateOrderGoods);

    }


    /**
     * 查询某个项目折扣活动里所有发起的记录，分页
     *
     * @param rulesId
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsRebateOrderGoods> querySelective(String rulesId, Integer page, Integer size, String sort) {
        LitemallPromotionGoodsRebateOrderGoodsExample example = new LitemallPromotionGoodsRebateOrderGoodsExample();
        LitemallPromotionGoodsRebateOrderGoodsExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRuleIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }


    /**
     * 取消订单时，从会员商品单里删除
     */
    public boolean cancelOrderDelete(Integer orderId){
        LitemallPromotionGoodsRebateOrderGoodsExample example = new LitemallPromotionGoodsRebateOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.deleteByExample(example) != 0;
    }
}
