package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUserOrderInfoMapper;
import org.jinyuanjava.litemall.db.domain.LitemallUserOrderInfo;
import org.jinyuanjava.litemall.db.domain.LitemallUserOrderInfoExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallUserOrderInfoService {
    @Resource
    private LitemallUserOrderInfoMapper litemallUserOrderInfoMapper;

    public List<LitemallUserOrderInfo> querySelective(
            Integer orderId,Integer userId,Integer statusId,String content,

            Integer page,Integer limit,String sort){
        LitemallUserOrderInfoExample example=new LitemallUserOrderInfoExample();
        LitemallUserOrderInfoExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(orderId)){
            criteria.andOrderIdEqualTo(orderId);
        }
        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(statusId)){
            criteria.andStatusCodeEqualTo(statusId);
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%"+content+"%");
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallUserOrderInfoMapper.selectByExample(example);
    }

    public void add(LitemallUserOrderInfo userinfoDef){
        userinfoDef.setAddTime(LocalDateTime.now());
        userinfoDef.setUpdateTime(LocalDateTime.now());
        litemallUserOrderInfoMapper.insertSelective(userinfoDef);
    }

    public int updateById(LitemallUserOrderInfo userinfoDef){
        userinfoDef.setUpdateTime(LocalDateTime.now());
        return litemallUserOrderInfoMapper.updateByPrimaryKeySelective(userinfoDef);
    }
    public void deleteById(Integer id){
        litemallUserOrderInfoMapper.logicalDeleteByPrimaryKey(id);
    }


    public LitemallUserOrderInfo findById(Integer id){
        return litemallUserOrderInfoMapper.selectByPrimaryKey(id);
    }

}
