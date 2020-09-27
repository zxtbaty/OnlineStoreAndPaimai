package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuthororcorpItemsMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpItems;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpItemsExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAuthororcorpItemsService {
    @Resource
    private LitemallAuthororcorpItemsMapper authororcorpItemsMapper;


    public List<LitemallAuthororcorpItems> queryIndex(){
        LitemallAuthororcorpItemsExample example=new LitemallAuthororcorpItemsExample();
        example.or().andDeletedEqualTo(false);
        return authororcorpItemsMapper.selectByExample(example);
    }

    public List<LitemallAuthororcorpItems> querySelective(Integer authorId,LocalDateTime lastUpdateTime, Integer page,Integer limit,String sort){
        LitemallAuthororcorpItemsExample example=new LitemallAuthororcorpItemsExample();
        LitemallAuthororcorpItemsExample.Criteria criteria=example.createCriteria();
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
        return authororcorpItemsMapper.selectByExample(example);
    }


    public int updateById(LitemallAuthororcorpItems authororcorpItems){
        authororcorpItems.setUpdateTime(LocalDateTime.now());
        return authororcorpItemsMapper.updateByPrimaryKeySelective(authororcorpItems);
    }

    public void deleteById(Integer id){
        authororcorpItemsMapper.logicalDeleteByPrimaryKey(id);
    }


    public void deleteByAuthorId(Integer authorId){
        LitemallAuthororcorpItemsExample example = new LitemallAuthororcorpItemsExample();
        example.or().andAuthorIdEqualTo(authorId);
        authororcorpItemsMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallAuthororcorpItems authororcorpItems){
        authororcorpItems.setAddTime(LocalDateTime.now());
        authororcorpItems.setUpdateTime(LocalDateTime.now());
        authororcorpItemsMapper.insertSelective(authororcorpItems);
    }


    public LitemallAuthororcorpItems findById(Integer id){
        return authororcorpItemsMapper.selectByPrimaryKey(id);
    }

    public List<LitemallAuthororcorpItems> queryByAuthorId(Integer authorId) {
        LitemallAuthororcorpItemsExample example = new LitemallAuthororcorpItemsExample();
        example.or().andAuthorIdEqualTo(authorId).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber");
        return authororcorpItemsMapper.selectByExample(example);
    }
    }
