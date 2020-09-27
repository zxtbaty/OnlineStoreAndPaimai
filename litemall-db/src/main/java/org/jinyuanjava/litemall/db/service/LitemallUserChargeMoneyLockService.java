package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUserChargeMoneyLockMapper;
import org.jinyuanjava.litemall.db.dao.ViewUserChargeMoneyLockMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallUserChargeMoneyLockService {

    @Resource
    private LitemallUserChargeMoneyLockMapper mapper;

    @Resource
    private ViewUserChargeMoneyLockMapper chargeMoneyLockMapper;

    @Autowired
    private LitemallAuctionZhuanchangGoodsCurrentService zhuanchangGoodsCurrentService;

    @Autowired
    private LitemallAuctionDajiaRuleCurrentService dajiaRuleCurrentService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallAuctionDajiaOfferCurrentService dajiaOfferCurrentService;

    @Autowired
    private LitemallAuctionZhuanchangOfferCurrentService auctionZhuanchangOfferCurrentService;



    /**
     * 获取锁定列表
     * @param userId
     * @param beginChargeTime
     * @param endChargeTime
     * @param payMethod
     * @param chargeSheetNo
     * @param payNo
     * @param payReturn
     * @param page
     * @param size
     * @param sort
     * @return
     */
    /**
     *
     * @param userId
     * @param beginLockTime
     * @param endLockTime
     * @param lockType
     * @param ruleId
     * @param goodsName
     * @param orderSn
     * @param userPhone
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public List<ViewUserChargeMoneyLock> querySelective(Integer userId,
        String beginLockTime, String endLockTime,
        String lockType, Integer ruleId, String goodsName, String orderSn, String userPhone,String username,
        Boolean ifUnlockIsNull,
        Integer page, Integer size, String sort) {
        ViewUserChargeMoneyLockExample example = new ViewUserChargeMoneyLockExample();
        example.setOrderByClause("add_time desc");
        ViewUserChargeMoneyLockExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(userId))
        {
            criteria.andUserIdEqualTo(userId);
        }

        if(!StringUtils.isEmpty(beginLockTime)) {
            criteria.andLockTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(beginLockTime));
        }
        if(!StringUtils.isEmpty(endLockTime)) {
            criteria.andLockTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(endLockTime));
        }
        if(!StringUtils.isEmpty(lockType)) {
            criteria.andLockTypeEqualTo(lockType);
        }
        if(!StringUtils.isEmpty(ruleId)) {
            criteria.andRuleMxIdEqualTo(ruleId);
        }
        if(!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameEqualTo(goodsName);
        }
        if(!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(!StringUtils.isEmpty(userPhone)) {
            criteria.andUserPhoneEqualTo(userPhone);
        }
        if(!StringUtils.isEmpty(username)) {
            criteria.andUserNameEqualTo(username);
        }
        if(!StringUtils.isEmpty(ifUnlockIsNull)) {
            if (ifUnlockIsNull == true) {
                criteria.andUnlockTimeIsNull();
            } else {
                criteria.andUnlockTimeIsNotNull();
            }
        }
        criteria.andDeletedEqualTo(false);
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }

        return chargeMoneyLockMapper.selectByExample(example);
    }


    /**
     * 创建用户锁定金额
     * @param userChargeMoneyLock
     * @return
     */
    public int create(LitemallUserChargeMoneyLock userChargeMoneyLock) {
        userChargeMoneyLock.setAddTime(LocalDateTime.now());
        userChargeMoneyLock.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(userChargeMoneyLock);
    }

    /**
     * 根据ID查找用户锁定金额
     *
     * @param id
     * @return
     */
    public LitemallUserChargeMoneyLock queryById(Integer id) {
        LitemallUserChargeMoneyLockExample example = new LitemallUserChargeMoneyLockExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 删除用户锁定记录ID,理论上讲用户一旦参与报价，则应该锁定，不允许撤销，直到拍卖成功，有竞得者，其它未拍则自动释放锁定
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);

    }

    /**
     * 更新用户锁定信息
     * @param userChargeMoneyLock
     * @return
     */
    public int updateById(LitemallUserChargeMoneyLock userChargeMoneyLock) {
        userChargeMoneyLock.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(userChargeMoneyLock);
    }

    public LitemallUserChargeMoneyLock findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    public LitemallUserChargeMoneyLock findByLockTypeAndOfferId(String lockType,Integer offerId){
        LitemallUserChargeMoneyLockExample example = new LitemallUserChargeMoneyLockExample();
        example.or().andLockTypeEqualTo(lockType).andOfferIdEqualTo(offerId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 处理加锁、解锁、锁定金额转订单(单独结算，不再混用)
     * @param opType 加锁 解锁 锁定金额转订单(单独结算，不再混用)
     * @param userId
     * @param lockType 专场拍、大家拍
     * @param ruleMxId
     * @param orderId
     * @return
     */
    public boolean handleLock(String opType,Integer id, Integer offerId,Integer userId,
                              String lockType,BigDecimal lockMoney,Integer ruleMxId,Integer orderId){
        String goodsName="";

        LitemallUser user=userService.findById(userId);
        if(lockType.equals("专场拍")){
            LitemallAuctionZhuanchangGoodsCurrent zhuanchangGoodsCurrent=zhuanchangGoodsCurrentService.queryById(ruleMxId);
            goodsName=zhuanchangGoodsCurrent.getGoodsName();
//            lockMoney=zhuanchangGoodsCurrent.getDeposit();
        } else if(lockType.equals("大家拍")){
            LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent=dajiaRuleCurrentService.queryById(ruleMxId);
            goodsName=dajiaRuleCurrent.getGoodsName();
//            lockMoney=dajiaRuleCurrent.getDeposit();
        }
        if(opType.equals("加锁")){
            //增加新的锁定记录
            LitemallUserChargeMoneyLock userChargeMoneyLock=new LitemallUserChargeMoneyLock();
            userChargeMoneyLock.setUserId(userId);
            userChargeMoneyLock.setLockType(lockType);
            userChargeMoneyLock.setRuleMxId(ruleMxId);
            userChargeMoneyLock.setGoodsName(goodsName);
            userChargeMoneyLock.setLockMoney(lockMoney);
            userChargeMoneyLock.setLockTime(LocalDateTime.now());
            userChargeMoneyLock.setUserCreateId(userId);
            userChargeMoneyLock.setUserModifyId(userId);
            userChargeMoneyLock.setOfferId(offerId);

            create(userChargeMoneyLock);
            //更新用户表资金占用和可用余额
            user.setChargeLockMoney(user.getChargeLockMoney().add(lockMoney));
            user.setChargeRemainMoney(user.getChargeRemainMoney().subtract(lockMoney));
            userService.updateById(user);

        } else if(opType.equals("解锁")){
            LitemallUserChargeMoneyLock userChargeMoneyLock=null;
            if(id==null){
                return false;
            }
            if(!StringUtils.isEmpty(id)) {
                userChargeMoneyLock = findById(id);
            } else {
                //通过出价记录ID查询
                userChargeMoneyLock=findByLockTypeAndOfferId(lockType,offerId);
            }

            //判断是否已经解锁，如果没有，则进行操作
            if(userChargeMoneyLock.getUnlockTime()==null) {
                userChargeMoneyLock.setUnlockTime(LocalDateTime.now());
                updateById(userChargeMoneyLock);
                //更新用户表资金占用和可用余额
                user.setChargeLockMoney(user.getChargeLockMoney().subtract(lockMoney));
                user.setChargeRemainMoney(user.getChargeRemainMoney().add(lockMoney));
                userService.updateById(user);
            }
        } else if(opType.equals("锁定金额转订单")){

            LitemallUserChargeMoneyLock userChargeMoneyLock=null;
            if(!StringUtils.isEmpty(id)) {
                userChargeMoneyLock = findById(id);
            } else {
                //通过出价记录ID查询
                findByLockTypeAndOfferId(lockType,offerId);
            }
            //判断是否已经解锁，如果没有，则进行操作
            if(userChargeMoneyLock.getLockToPayTime()==null) {
                userChargeMoneyLock.setLockToPayTime(LocalDateTime.now());
                userChargeMoneyLock.setOrderId(orderId);
                updateById(userChargeMoneyLock);
                //锁定金额减少，转订单金额增加
                user.setChargeLockMoney(user.getChargeLockMoney().subtract(userChargeMoneyLock.getLockMoney()));
                userService.updateById(user);
            }
        }
        return true;
    }

    public Boolean handleUnLockOrder(LitemallOrder order){

        if(order.getAuctionType().equals("大家拍")) {
            LitemallAuctionDajiaOfferCurrent dajiaOfferCurrent = dajiaOfferCurrentService.queryById(order.getDajiapaiOfferId());
            LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent = dajiaRuleCurrentService.findById(dajiaOfferCurrent.getRulesId());
            if (dajiaRuleCurrent.getDeposit() != null &&
                    dajiaRuleCurrent.getDeposit().compareTo(BigDecimal.ZERO) == 1 ) {
                //解锁和加锁保证金额
                handleLock("解锁", null, dajiaOfferCurrent.getId(), dajiaOfferCurrent.getUserId(),
                        "大家拍", dajiaRuleCurrent.getDeposit(), dajiaOfferCurrent.getRulesId(), null);
            }
        } else if(order.getAuctionType().equals("专场拍")) {
            LitemallAuctionZhuanchangOfferCurrent zhuanchangOfferCurrent = auctionZhuanchangOfferCurrentService.queryById(order.getZhuanchangOfferId());
            LitemallAuctionZhuanchangGoodsCurrent zhuanchangGoodsCurrent = zhuanchangGoodsCurrentService.queryById(zhuanchangOfferCurrent.getRulesMxId());
            if (zhuanchangGoodsCurrent.getDeposit() != null &&
                    zhuanchangGoodsCurrent.getDeposit().compareTo(BigDecimal.ZERO) == 1) {
                //解锁和加锁保证金额
                handleLock("解锁", null, zhuanchangOfferCurrent.getId(), zhuanchangOfferCurrent.getUserId(),
                        "专场拍", zhuanchangGoodsCurrent.getDeposit(), zhuanchangOfferCurrent.getRulesMxId(), null);
            }
        }

       return true;
    }

    public LitemallUserChargeMoneyLock queryByUserIdRuleMxIdType(Integer userId,String type,Integer ruleMxId) {
        LitemallUserChargeMoneyLockExample example = new LitemallUserChargeMoneyLockExample();
        LitemallUserChargeMoneyLockExample.Criteria criteria=example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andRuleMxIdEqualTo(ruleMxId);
        criteria.andLockTypeEqualTo(type);
        //解锁时间为null,还没有解锁的保证金，如果活动已经结束，用户的保证金会自动解锁退还
        criteria.andUnlockTimeIsNull();
        criteria.andDeletedEqualTo(false);

        return mapper.selectOneByExample(example);
    }

    public List<LitemallUserChargeMoneyLock> queryByRuleMxIdType(String type,Integer ruleMxId) {
        LitemallUserChargeMoneyLockExample example = new LitemallUserChargeMoneyLockExample();
        LitemallUserChargeMoneyLockExample.Criteria criteria=example.createCriteria();

        criteria.andRuleMxIdEqualTo(ruleMxId);
        criteria.andLockTypeEqualTo(type);
        //解锁时间为null,还没有解锁的保证金，如果活动已经结束，用户的保证金会自动解锁退还
        criteria.andUnlockTimeIsNull();
        criteria.andDeletedEqualTo(false);

        return mapper.selectByExample(example);
    }

    /**
     * 除了出价最高者外，批量解锁用户保证金,也可以全部解锁
     * @param type
     * @param ruleMxId
     * @return
     */
    public boolean unLockMoneyBatch(String type,Integer ruleMxId,Boolean ifAll){
        if(type.equals("大家拍")) {
            LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent = dajiaRuleCurrentService.findById(ruleMxId);
            //获取出价最高的那个大家拍ID
            Integer maxOfferId = dajiaOfferCurrentService.getMaxOfferId(ruleMxId);
            LitemallAuctionDajiaOfferCurrent dajiaOfferCurrent=null;
            if(!StringUtils.isEmpty(maxOfferId)){
                dajiaOfferCurrent=dajiaOfferCurrentService.queryById(maxOfferId);
            }

            List<LitemallUserChargeMoneyLock> lockList= queryByRuleMxIdType(type,ruleMxId);
            for(LitemallUserChargeMoneyLock moneyLock:lockList){
                //出价最高者的保证金是否可以退还
                if(ifAll==true) {
                    handleLock("解锁", moneyLock.getId(), null, moneyLock.getUserId(),
                            "大家拍", dajiaRuleCurrent.getDeposit(), moneyLock.getRuleMxId(), null);
                }else
                {
                    if(dajiaOfferCurrent==null||(dajiaOfferCurrent!=null&&!moneyLock.getUserId().equals(dajiaOfferCurrent.getUserId()))){
                        handleLock("解锁", moneyLock.getId(), null, moneyLock.getUserId(),
                                "大家拍", dajiaRuleCurrent.getDeposit(), moneyLock.getRuleMxId(), null);
                    }
                }
            }
        } else if(type.equals("专场拍")) {
            LitemallAuctionZhuanchangGoodsCurrent zhuanchangGoodsCurrent = zhuanchangGoodsCurrentService.queryById(ruleMxId);
            //获取出价最高的那个大家拍ID
            Integer maxOfferId = auctionZhuanchangOfferCurrentService.getMaxOfferId(ruleMxId);
            LitemallAuctionZhuanchangOfferCurrent zhuanchangOfferCurrent=null;
            if(!StringUtils.isEmpty(maxOfferId)){
                zhuanchangOfferCurrent=auctionZhuanchangOfferCurrentService.queryById(maxOfferId);
            }
            List<LitemallUserChargeMoneyLock> lockList= queryByRuleMxIdType(type,ruleMxId);
            for(LitemallUserChargeMoneyLock moneyLock:lockList){
                //出价最高者的保证金是否可以退还
                if(ifAll==true) {
                    handleLock("解锁", moneyLock.getId(), null, moneyLock.getUserId(),
                            "专场拍", zhuanchangGoodsCurrent.getDeposit(), moneyLock.getRuleMxId(), null);
                }else
                {
                    if(zhuanchangOfferCurrent==null||(zhuanchangOfferCurrent!=null&&!moneyLock.getUserId().equals(zhuanchangOfferCurrent.getUserId()))){
                        handleLock("解锁", moneyLock.getId(), null, moneyLock.getUserId(),
                                "专场拍", zhuanchangGoodsCurrent.getDeposit(), moneyLock.getRuleMxId(), null);
                    }
                }
            }
        }
        return true;
    }


}
