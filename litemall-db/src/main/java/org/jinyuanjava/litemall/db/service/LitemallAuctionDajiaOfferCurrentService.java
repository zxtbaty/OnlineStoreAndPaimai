package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuctionDajiaOfferCurrentMapper;
import org.jinyuanjava.litemall.db.dao.ViewAuctionDajiaOfferCurrentMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionDajiaOfferCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionDajiaOfferCurrentExample;
import org.jinyuanjava.litemall.db.domain.ViewAuctionDajiaOfferCurrent;
import org.jinyuanjava.litemall.db.domain.ViewAuctionDajiaOfferCurrentExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LitemallAuctionDajiaOfferCurrentService {
    @Resource
    private LitemallAuctionDajiaOfferCurrentMapper mapper;

    @Resource
    private ViewAuctionDajiaOfferCurrentMapper viewMapper;

    @Resource
    private CommonDBService commonDBService;


    /**
     * 获取专场拍卖出价列表
     * @param userId
     * @param rulesId
     * @param userName
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<ViewAuctionDajiaOfferCurrent> querySelective(Integer userId,Integer rulesId,
                                                             String userName,
                                                             Integer page, Integer size, String sort) {
        ViewAuctionDajiaOfferCurrentExample example = new ViewAuctionDajiaOfferCurrentExample();
        example.setOrderByClause(sort);

        ViewAuctionDajiaOfferCurrentExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(rulesId))
        {
            criteria.andUserIdEqualTo(rulesId);
        }

        if(!StringUtils.isEmpty(userId))
        {
          criteria.andRulesIdEqualTo(userId);
        }

        if(!StringUtils.isEmpty(userName))
        {
            criteria.andUserNameLike("%"+userName+"%");
        }

        example.setOrderByClause("ordernumber desc");

        criteria.andDeletedEqualTo(false);
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
        return viewMapper.selectByExample(example);
    }

    /**
     * 添加大家拍的出价记录
     * @param dajiaOfferCurrentExample
     * @return
     */
    public int create(LitemallAuctionDajiaOfferCurrent dajiaOfferCurrentExample) {
        dajiaOfferCurrentExample.setAddTime(LocalDateTime.now());
        dajiaOfferCurrentExample.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(dajiaOfferCurrentExample);
    }

    /**
     * 根据ID查找对应专场拍出价明细
     *
     * @param id
     * @return
     */
    public LitemallAuctionDajiaOfferCurrent queryById(Integer id) {
        LitemallAuctionDajiaOfferCurrentExample example = new LitemallAuctionDajiaOfferCurrentExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据活动ID查找对应大家拍卖活动商品
     *
     * @param id
     * @return
     */
    public List<LitemallAuctionDajiaOfferCurrent> queryByRuleId(Integer id,Integer page, Integer size) {
        LitemallAuctionDajiaOfferCurrentExample example = new LitemallAuctionDajiaOfferCurrentExample();
        example.or().andRulesIdEqualTo(id).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber desc");
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
        return mapper.selectByExample(example);
    }

    /**
     * 计算出价人数
     *
     * @param ruleId
     * @return
     */
    public Integer getAuctionPersonCount(Integer ruleId) {
        String strSql="select count(distinct user_id) as count from litemall_auction_dajia_offer_current where deleted=0 and "+
                "rules_id="+ruleId;
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
     * 计算围观人数
     *
     * @param ruleId
     * @return
     */
    public Integer getVisitPersonCount(Integer ruleId) {
        String strSql="select count(distinct user_id) as count from litemall_footprint where deleted=0 and "+
                "dajiapai_id="+ruleId;
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
     * 获得出价最高的记录ID
     *
     * @param ruleId
     * @return
     */
    public Integer getMaxOfferId(Integer ruleId) {
        String strSql="select max(id) as id from litemall_auction_dajia_offer_current where deleted=0 and "+
                "rules_id="+ruleId;
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
        LitemallAuctionDajiaOfferCurrentExample example = new LitemallAuctionDajiaOfferCurrentExample();
        example.or().andRulesIdEqualTo(ruleId);
        mapper.logicalDeleteByExample(example);
    }

    /**
     * 更新大家拍出价记录
     * @param dajiaOfferCurrentExample
     * @return
     */
    public int updateById(LitemallAuctionDajiaOfferCurrent dajiaOfferCurrentExample) {
        dajiaOfferCurrentExample.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(dajiaOfferCurrentExample);
    }



}
