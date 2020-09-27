package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuctionZhuanchangOfferCurrentMapper;
import org.jinyuanjava.litemall.db.dao.ViewAuctionZhuanchangOfferCurrentMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangOfferCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangOfferCurrentExample;
import org.jinyuanjava.litemall.db.domain.ViewAuctionZhuanchangOfferCurrent;
import org.jinyuanjava.litemall.db.domain.ViewAuctionZhuanchangOfferCurrentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LitemallAuctionZhuanchangOfferCurrentService {
    @Resource
    private LitemallAuctionZhuanchangOfferCurrentMapper mapper;
    @Resource
    private ViewAuctionZhuanchangOfferCurrentMapper viewOfferMapper;

    @Autowired
    private CommonDBService commonDBService;

    /**
     * 获取专场拍卖出价明细列表
     * @param rule_id
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<LitemallAuctionZhuanchangOfferCurrent> querySelective(Integer user_id, Integer rule_id,
                                                                  Integer page, Integer size, String sort) {
        LitemallAuctionZhuanchangOfferCurrentExample example = new LitemallAuctionZhuanchangOfferCurrentExample();
        example.setOrderByClause(sort);

        LitemallAuctionZhuanchangOfferCurrentExample.Criteria criteria = example.createCriteria();

        if(user_id!=null)
        {
            criteria.andUserIdEqualTo(rule_id);
        }

        if(rule_id!=null)
        {
          criteria.andRulesIdEqualTo(rule_id);
        }

        criteria.andDeletedEqualTo(false);
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
        return mapper.selectByExample(example);
    }

    /**
     * 添加专场拍出价记录
     * @param goodsRebateGoods
     * @return
     */
    public int create(LitemallAuctionZhuanchangOfferCurrent goodsRebateGoods) {
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
    public LitemallAuctionZhuanchangOfferCurrent queryById(Integer id) {
        LitemallAuctionZhuanchangOfferCurrentExample example = new LitemallAuctionZhuanchangOfferCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallAuctionZhuanchangOfferCurrent> queryByRuleId(Integer id) {
        LitemallAuctionZhuanchangOfferCurrentExample example = new LitemallAuctionZhuanchangOfferCurrentExample();
        example.or().andRulesIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 根据活动ID查找对应专场拍卖活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallAuctionZhuanchangOfferCurrent> queryByRuleMxId(Integer id,Integer page, Integer size) {
        LitemallAuctionZhuanchangOfferCurrentExample example = new LitemallAuctionZhuanchangOfferCurrentExample();
        example.or().andRulesMxIdEqualTo(id).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber desc");
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
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
        LitemallAuctionZhuanchangOfferCurrentExample example = new LitemallAuctionZhuanchangOfferCurrentExample();
        example.or().andRulesIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新品项折扣活动
     * @param goodsRebateGoods
     * @return
     */
    public int updateById(LitemallAuctionZhuanchangOfferCurrent goodsRebateGoods) {
        goodsRebateGoods.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(goodsRebateGoods);
    }


    /**
     * 获取专场拍卖活动出价视图
     * @param userId
     * @param rulesId
     * @param userName
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<ViewAuctionZhuanchangOfferCurrent> getViewAuctionZhuanchangOfferCurrent(
        Integer userId,Integer rulesId,Integer rulesMxId,
        String userName, Integer page, Integer size, String sort) {
        ViewAuctionZhuanchangOfferCurrentExample example = new ViewAuctionZhuanchangOfferCurrentExample();
        ViewAuctionZhuanchangOfferCurrentExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(rulesMxId))
        {
            criteria.andRulesMxIdEqualTo(rulesMxId);
        }

        if(!StringUtils.isEmpty(rulesId))
        {
            criteria.andRulesIdEqualTo(rulesId);
        }

        if(!StringUtils.isEmpty(userId))
        {
            criteria.andRulesIdEqualTo(userId);
        }

        if(!StringUtils.isEmpty(userName))
        {
            criteria.andUserNameLike("%"+userName+"%");
        }
        if (!org.springframework.util.StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, size);

        example.orderBy("add_time desc");
        return viewOfferMapper.selectByExample(example);
    }

    /**
     * 计算出价人数
     *
     * @param ruleMxId
     * @return
     */
    public Integer getAuctionPersonCount(Integer ruleMxId) {
        String strSql="select count(distinct user_id) as count from litemall_auction_zhuanchang_offer_current where deleted=0 and "+
                "rules_mx_id="+ruleMxId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else {
            Object value = m.get("count");
            return Integer.parseInt(value.toString());
        }
    }

    /**
     * 计算最大出价ID
     *
     * @param ruleMxId
     * @return
     */
    public Integer getMaxOfferId(Integer ruleMxId) {
        String strSql="select max(id) as id from litemall_auction_zhuanchang_offer_current where deleted=0 and "+
                "rules_mx_id="+ruleMxId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else {
            Object value = m.get("id");
            return Integer.parseInt(value.toString());
        }
    }

    /**
     * 计算专场拍明细围观人数
     *
     * @param zhangchangMxId
     * @return
     */
    public Integer getVisitPersonCountByRuleMxId(Integer zhangchangMxId) {
        String strSql="select count(distinct user_id) as count from litemall_footprint where deleted=0 and "+
                "zhuanchang_mx_id="+zhangchangMxId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else {
            Object value = m.get("count");
            return Integer.parseInt(value.toString());
        }
    }

    /**
     * 计算专场拍活动围观人数
     *
     * @param zhangchangId
     * @return
     */
    public Integer getVisitPersonCountByMainId(Integer zhangchangId) {
        String strSql="select count(distinct user_id) as count from litemall_footprint where deleted=0 and "+
                "zhuanchang_id="+zhangchangId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else {
            Object value = m.get("count");
            return Integer.parseInt(value.toString());
        }
    }


    /**
     * 计算专场出价数及出价人数
     *
     * @param ruleId
     * @return
     */
    public Map getZhuangChangSumInfo(Integer ruleId) {
        String strSql="select count(id) as SumOfferCount," +
                "count(distinct user_id) as sumPersonCount from litemall_auction_zhuanchang_offer_current where deleted=0 and "+
                "rules_id="+ruleId;
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return null;
        } else {
            return m;

        }
    }
}
