package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsDetailMapper;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsDetail;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsDetailExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPromotionGoodsDetailService {
    @Resource
    private LitemallPromotionGoodsDetailMapper mapper;

    /**
     * 获取会员专享商品列表
     * @param ruleId
     * @param goodsName
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsDetail> querySelective(Integer ruleId
            , String goodsSn, String goodsName,
        Integer page, Integer size, String sort) {
        LitemallPromotionGoodsDetailExample example = new LitemallPromotionGoodsDetailExample();
        example.setOrderByClause(sort);

        LitemallPromotionGoodsDetailExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%"+goodsName+"%");
        }
        if(ruleId!=null)
        {
          criteria.andRuleIdEqualTo(ruleId);
        }

        criteria.andDeletedEqualTo(false);

        //如果Size设置成999999，则查询全部不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        return mapper.selectByExample(example);
    }

    /**
     * 添加会员商品
     * @param goodsDetail
     * @return
     */
    public int createGoodsDetail(LitemallPromotionGoodsDetail goodsDetail) {
        goodsDetail.setAddTime(LocalDateTime.now());
        goodsDetail.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(goodsDetail);
    }

    /**
     * 根据ID查找对应会员活动商品
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsDetail queryById(Integer id) {
        LitemallPromotionGoodsDetailExample example = new LitemallPromotionGoodsDetailExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据规则ID查找对应会员活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallPromotionGoodsDetail> queryByRuleId(Integer id) {
        LitemallPromotionGoodsDetailExample example = new LitemallPromotionGoodsDetailExample();
        example.or().andRuleIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 删除所选的会员商品列表
     * @param id
     */
    public void delete(Integer id) {
        mapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除所选的给定活动的商品
     * @param ruleId
     */
    public void deleteByRuleId(Integer ruleId){
        LitemallPromotionGoodsDetailExample example = new LitemallPromotionGoodsDetailExample();
        example.or().andRuleIdEqualTo(ruleId);
        mapper.deleteByExample(example);
    }

    /**
     * 更新专享活动会员
     * @param goodsRuleUser
     * @return
     */
    public int updateById(LitemallPromotionGoodsDetail goodsRuleUser) {
        goodsRuleUser.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRuleUser);
    }

    public LitemallPromotionGoodsDetail querySelective(Integer ruleId
            ,  Integer productId) {
        LitemallPromotionGoodsDetailExample example = new LitemallPromotionGoodsDetailExample();
        example.or().andRuleIdEqualTo(ruleId).andGoodsProductIdEqualTo(productId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


}
