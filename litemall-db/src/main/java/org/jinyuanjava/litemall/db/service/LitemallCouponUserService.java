package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallCouponUserMapper;
import org.jinyuanjava.litemall.db.dao.ViewUserTicketsMapper;
import org.jinyuanjava.litemall.db.domain.LitemallCouponUser;
import org.jinyuanjava.litemall.db.domain.LitemallCouponUserExample;
import org.jinyuanjava.litemall.db.domain.ViewUserTickets;
import org.jinyuanjava.litemall.db.domain.ViewUserTicketsExample;
import org.jinyuanjava.litemall.db.util.CouponUserConstant;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCouponUserService {
    @Resource
    private LitemallCouponUserMapper couponUserMapper;

    @Resource
    private ViewUserTicketsMapper viewUserTicketsMapper;

    public Integer countCoupon(Integer couponId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
        return (int)couponUserMapper.countByExample(example);
    }

    public Integer countUserAndCoupon(Integer userId, Integer couponId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andUserIdEqualTo(userId).andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
        return (int)couponUserMapper.countByExample(example);
    }

    public void add(LitemallCouponUser couponUser) {
        couponUser.setAddTime(LocalDateTime.now());
        couponUser.setUpdateTime(LocalDateTime.now());
        couponUserMapper.insertSelective(couponUser);
    }

    public List<LitemallCouponUser> queryList(Integer userId, Integer couponId, Short status, Integer page, Integer size, String sort) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        LitemallCouponUserExample.Criteria criteria = example.createCriteria();
        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if(couponId != null){
            criteria.andCouponIdEqualTo(couponId);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(size)) {
            PageHelper.startPage(page, size);
        }

        return couponUserMapper.selectByExample(example);
    }

    public List<LitemallCouponUser> queryAll(Integer userId, Integer couponId) {
        return queryList(userId, couponId, CouponUserConstant.STATUS_USABLE, null, null, "add_time");
    }

    public List<LitemallCouponUser> queryAll(Integer userId) {
        return queryList(userId, null, CouponUserConstant.STATUS_USABLE, null, null, "add_time");
    }

    public LitemallCouponUser queryOne(Integer userId, Integer couponId) {
        List<LitemallCouponUser> couponUserList = queryList(userId, couponId, CouponUserConstant.STATUS_USABLE, 1, 1, "add_time");
        if(couponUserList.size() == 0){
            return null;
        }
        return couponUserList.get(0);
    }

    public LitemallCouponUser findById(Integer id) {
        return couponUserMapper.selectByPrimaryKey(id);
    }


    public int update(LitemallCouponUser couponUser) {
        couponUser.setUpdateTime(LocalDateTime.now());
        return couponUserMapper.updateByPrimaryKeySelective(couponUser);
    }

    public List<LitemallCouponUser> queryExpired() {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andEndTimeLessThan(LocalDateTime.now()).andDeletedEqualTo(false);
        return couponUserMapper.selectByExample(example);
    }



    /**
     * 查询可用优惠券，目前只支持满减券
     * @param userId
     * @param orderMoney
     * @return
     */
    public List<ViewUserTickets>  getAvailableTicketsList(Integer userId, BigDecimal orderMoney) {
        ViewUserTicketsExample example = new ViewUserTicketsExample();
        //优惠券状态可用，当前时间在开始时间和结束时间之间的
        example.or()
                .andUserIdEqualTo(userId)
                .andDeletedEqualTo(false)
                .andStatusEqualTo(Short.valueOf("0"))
                .andStartTimeLessThanOrEqualTo(LocalDateTime.now())
                .andEndTimeGreaterThan(LocalDateTime.now())
                .andMinGreaterThanOrEqualTo(orderMoney);
        List<ViewUserTickets> ticketsList=viewUserTicketsMapper.selectByExample(example);
        return ticketsList;

    }
}
