package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallOpadminPubMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminPub;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminPubExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallOpadminPubService {
    @Resource
    private LitemallOpadminPubMapper litemallOpadminPubMapper;


    public List<LitemallOpadminPub> queryIndex(){
        LitemallOpadminPubExample example=new LitemallOpadminPubExample();
        example.or().andDeletedEqualTo(false);
        return litemallOpadminPubMapper.selectByExample(example);
    }

    public List<LitemallOpadminPub> querySelective(Integer litemallUserinfoPubId, Integer page,Integer limit,String sort){
        LitemallOpadminPubExample example=new LitemallOpadminPubExample();
        LitemallOpadminPubExample.Criteria criteria=example.createCriteria();
        if(litemallUserinfoPubId!=null){
            criteria.andInfoIdEqualTo(litemallUserinfoPubId);
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallOpadminPubMapper.selectByExample(example);
    }


    public int updateById(LitemallOpadminPub litemallOpadminPub){
        litemallOpadminPub.setUpdateTime(LocalDateTime.now());
        return litemallOpadminPubMapper.updateByPrimaryKeySelective(litemallOpadminPub);
    }

    public void deleteById(Integer id){
        litemallOpadminPubMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByInfoId(Integer litemallUserinfoPubId){
        LitemallOpadminPubExample example = new LitemallOpadminPubExample();
        example.or().andInfoIdEqualTo(litemallUserinfoPubId);
        litemallOpadminPubMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallOpadminPub litemallOpadminPub){
        litemallOpadminPub.setAddTime(LocalDateTime.now());
        litemallOpadminPub.setUpdateTime(LocalDateTime.now());
        litemallOpadminPubMapper.insertSelective(litemallOpadminPub);
    }


    public LitemallOpadminPub findById(Integer id){
        return litemallOpadminPubMapper.selectByPrimaryKey(id);
    }

    public List<LitemallOpadminPub> queryByInfoId(Integer infoId) {
        LitemallOpadminPubExample example = new LitemallOpadminPubExample();
        example.or().andInfoIdEqualTo(infoId).andDeletedEqualTo(false);
        return litemallOpadminPubMapper.selectByExample(example);
    }
    }
