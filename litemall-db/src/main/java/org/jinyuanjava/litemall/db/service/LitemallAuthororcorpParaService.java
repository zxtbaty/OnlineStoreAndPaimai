package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuthororcorpParaMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpPara;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpParaExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAuthororcorpParaService {
    @Resource
    private LitemallAuthororcorpParaMapper authororcorpParaMapper;


    public List<LitemallAuthororcorpPara> queryIndex(){
        LitemallAuthororcorpParaExample example=new LitemallAuthororcorpParaExample();
        example.or().andDeletedEqualTo(false);
        return authororcorpParaMapper.selectByExample(example);
    }

    public List<LitemallAuthororcorpPara> querySelective(Integer authorId,LocalDateTime lastUpdateTime, Integer page,Integer limit,String sort){
        LitemallAuthororcorpParaExample example=new LitemallAuthororcorpParaExample();
        LitemallAuthororcorpParaExample.Criteria criteria=example.createCriteria();
        if(authorId!=null){
            criteria.andAuthorIdEqualTo(authorId);
        }
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }

        criteria.andDeletedEqualTo(false);
//        if(!StringUtils.isEmpty(sort)){
//            example.setOrderByClause(sort);
//        }
        example.setOrderByClause("ordernumber");
        PageHelper.startPage(page,limit);
        return authororcorpParaMapper.selectByExample(example);
    }


    public int updateById(LitemallAuthororcorpPara storeBrand){
        storeBrand.setUpdateTime(LocalDateTime.now());
        return authororcorpParaMapper.updateByPrimaryKeySelective(storeBrand);
    }

    public void deleteById(Integer id){
        authororcorpParaMapper.logicalDeleteByPrimaryKey(id);
    }


    public void deleteByAuthorId(Integer authorId){
        LitemallAuthororcorpParaExample example = new LitemallAuthororcorpParaExample();
        example.or().andAuthorIdEqualTo(authorId);
        authororcorpParaMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallAuthororcorpPara storeBrand){
        storeBrand.setAddTime(LocalDateTime.now());
        storeBrand.setUpdateTime(LocalDateTime.now());
        authororcorpParaMapper.insertSelective(storeBrand);
    }


    public LitemallAuthororcorpPara findById(Integer id){
        return authororcorpParaMapper.selectByPrimaryKey(id);
    }

    public List<LitemallAuthororcorpPara> queryByAuthorId(Integer authorId) {
        LitemallAuthororcorpParaExample example = new LitemallAuthororcorpParaExample();
        example.or().andAuthorIdEqualTo(authorId).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber");
        return authororcorpParaMapper.selectByExample(example);
    }
    }
