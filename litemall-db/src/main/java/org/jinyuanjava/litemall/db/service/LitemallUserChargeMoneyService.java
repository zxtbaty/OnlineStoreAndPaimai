package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUserChargeMoneyMapper;
import org.jinyuanjava.litemall.db.dao.ViewUserChargeMoneyMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.CharUtil;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallUserChargeMoneyService {

    @Resource
    private LitemallUserChargeMoneyMapper mapper;

    @Resource
    private ViewUserChargeMoneyMapper moneyMapper;

    @Autowired
    private LitemallUserService litemallUserService;



    /**
     * 获取押金支付金额列表
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
    public List<ViewUserChargeMoney> querySelective(Integer userId,
      String beginChargeTime, String endChargeTime,
      String payMethod, String chargeSheetNo, String payNo, Integer payReturn,
      String username, String userPhone,
      Integer page, Integer size, String sort) {
        ViewUserChargeMoneyExample example = new ViewUserChargeMoneyExample();
        example.setOrderByClause("add_time desc");
        ViewUserChargeMoneyExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(userId))
        {
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(beginChargeTime)) {
            criteria.andChargeTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(beginChargeTime));
        }
        if(!StringUtils.isEmpty(endChargeTime)) {
            criteria.andChargeTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(endChargeTime));
        }
        if(!StringUtils.isEmpty(payMethod)) {
            criteria.andPayMethodEqualTo(payMethod);
        }
        if(!StringUtils.isEmpty(chargeSheetNo)) {
            criteria.andChargeSheetNoEqualTo(chargeSheetNo);
        }
        if(!StringUtils.isEmpty(payNo)) {
            criteria.andPayNoEqualTo(payNo);
        }
        if(!StringUtils.isEmpty(payReturn)) {
            criteria.andPayReturnEqualTo(CharUtil.integerConverToBoolean(payReturn));
        }
        if(!StringUtils.isEmpty(username)) {
            criteria.andUsernameEqualTo(username);
        }
        if(!StringUtils.isEmpty(userPhone)) {
            criteria.andMobileEqualTo(userPhone);
        }
        criteria.andDeletedEqualTo(false);
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
        return moneyMapper.selectByExample(example);
    }

    public List<LitemallUserChargeMoney> queryMyCharge(Integer userId) {
        LitemallUserChargeMoneyExample example = new LitemallUserChargeMoneyExample();
        example.setOrderByClause("add_time desc");
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);

        return mapper.selectByExample(example);
    }

    public List<LitemallUserChargeMoney> queryByPayMethodAndPayNo(String payMethod,String payNo) {
        LitemallUserChargeMoneyExample example = new LitemallUserChargeMoneyExample();
        example.setOrderByClause("add_time desc");
        example.or().andPayMethodEqualTo(payMethod).andPayNoEqualTo(payNo).andDeletedEqualTo(false);

        return mapper.selectByExample(example);
    }

    public LitemallUserChargeMoney queryByChargeSheetNo(String chargeSheetNo) {
        LitemallUserChargeMoneyExample example = new LitemallUserChargeMoneyExample();
        example.setOrderByClause("add_time desc");
        example.or().andChargeSheetNoEqualTo(chargeSheetNo);

        return mapper.selectOneByExample(example);
    }



    /**
     * 创建用户支付金额
     * @param userChargeMoney
     * @return
     */
    public int create(LitemallUserChargeMoney userChargeMoney) {
        userChargeMoney.setAddTime(LocalDateTime.now());
        userChargeMoney.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(userChargeMoney);
    }

    /**
     * 根据ID查找用户支付金额
     *
     * @param id
     * @return
     */
    public LitemallUserChargeMoney queryById(Integer id) {
        LitemallUserChargeMoneyExample example = new LitemallUserChargeMoneyExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }


    /**
     * 删除支付记录ID,理论上一旦支付，不允许撤销
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);

    }

    /**
     * 更新用户支付金额
     * @param userChargeMoney
     * @return
     */
    public int updateById(LitemallUserChargeMoney userChargeMoney) {
        userChargeMoney.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(userChargeMoney);
    }

    public LitemallUserChargeMoney findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }


    public Boolean retfundCharge(Integer id){
        LitemallUserChargeMoney chargeMoney=  mapper.selectByPrimaryKey(id);
        //已经申请过款状态
        if(chargeMoney.getRefundTime()!=null){
            return false;
        }
        chargeMoney.setRefundStatus("待处理");
        chargeMoney.setRefundTime(LocalDateTime.now());
        mapper.updateByPrimaryKey(chargeMoney);


        return true;
    }

}
