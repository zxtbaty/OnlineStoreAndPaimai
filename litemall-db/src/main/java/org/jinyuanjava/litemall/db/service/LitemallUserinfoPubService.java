package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUserinfoPubMapper;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoPub;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoPubExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallUserinfoPubService {
    @Resource
    private LitemallUserinfoPubMapper litemallUserinfoPubMapper;

    @Autowired
    private LitemallUserService userService;


    public List<LitemallUserinfoPub> queryIndex(){
        LitemallUserinfoPubExample example=new LitemallUserinfoPubExample();
        example.or().andDeletedEqualTo(false);
        return litemallUserinfoPubMapper.selectByExample(example);
    }

    public List<LitemallUserinfoPub> querySelective(Integer litemallUserinfoPubId, Integer page,Integer limit,String sort){
        LitemallUserinfoPubExample example=new LitemallUserinfoPubExample();
        LitemallUserinfoPubExample.Criteria criteria=example.createCriteria();
        if(litemallUserinfoPubId!=null){
            criteria.andInfoIdEqualTo(litemallUserinfoPubId);
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallUserinfoPubMapper.selectByExample(example);
    }


    public int updateById(LitemallUserinfoPub litemallUserinfoPub){
        litemallUserinfoPub.setUpdateTime(LocalDateTime.now());
        return litemallUserinfoPubMapper.updateByPrimaryKeySelective(litemallUserinfoPub);
    }

    public void deleteById(Integer id){
        litemallUserinfoPubMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByInfoId(Integer litemallUserinfoPubId){
        LitemallUserinfoPubExample example = new LitemallUserinfoPubExample();
        example.or().andInfoIdEqualTo(litemallUserinfoPubId);
        litemallUserinfoPubMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallUserinfoPub litemallUserinfoPub){
        litemallUserinfoPub.setAddTime(LocalDateTime.now());
        litemallUserinfoPub.setUpdateTime(LocalDateTime.now());
        litemallUserinfoPubMapper.insertSelective(litemallUserinfoPub);
    }


    public LitemallUserinfoPub findById(Integer id){
        return litemallUserinfoPubMapper.selectByPrimaryKey(id);
    }

    public List<LitemallUserinfoPub> queryByInfoId(Integer infoId) {
        LitemallUserinfoPubExample example = new LitemallUserinfoPubExample();
        example.or().andInfoIdEqualTo(infoId).andDeletedEqualTo(false);
        return litemallUserinfoPubMapper.selectByExample(example);
    }

    public List<Integer> getInfoIdsByUserId(Integer userId) {
        LitemallUserinfoPubExample example = new LitemallUserinfoPubExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        //全部用户
        example.or().andIfAllVisitEqualTo(true).andDeletedEqualTo(false);
        //会员用户
        example.or().andIfAllUserEqualTo(true).andDeletedEqualTo(false);
        LitemallUser litemallUser= userService.findById(userId);
        //分组用户1
        if(litemallUser.getUserClassAttr1Code()!=null) {
            example.or().andUserGroupCode1EqualTo(litemallUser.getUserClassAttr1Code()).andDeletedEqualTo(false);
        }
        //分组用户2
        if(litemallUser.getUserClassAttr2Code()!=null) {
            example.or().andUserGroupCode2EqualTo(litemallUser.getUserClassAttr2Code()).andDeletedEqualTo(false);
        }

        List<LitemallUserinfoPub> result=litemallUserinfoPubMapper.selectByExample(example);
        return result.stream().map(LitemallUserinfoPub::getInfoId).collect(Collectors.toList());
    }
}
