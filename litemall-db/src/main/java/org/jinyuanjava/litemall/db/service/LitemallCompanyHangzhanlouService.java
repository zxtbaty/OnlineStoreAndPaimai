package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallCompanyHangzhanlouMapper;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyHangzhanlou;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyHangzhanlouExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCompanyHangzhanlouService {
    @Resource
    private LitemallCompanyHangzhanlouMapper companyHangzhanlouMapper;


    public List<LitemallCompanyHangzhanlou> queryIndex(){
        LitemallCompanyHangzhanlouExample example=new LitemallCompanyHangzhanlouExample();
        example.or().andDeletedEqualTo(false);
        return companyHangzhanlouMapper.selectByExample(example);
    }

    public List<LitemallCompanyHangzhanlou> querySelective(Integer comId,LocalDateTime lastUpdateTime, Integer page,Integer limit,String sort){
        LitemallCompanyHangzhanlouExample example=new LitemallCompanyHangzhanlouExample();
        LitemallCompanyHangzhanlouExample.Criteria criteria=example.createCriteria();
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return companyHangzhanlouMapper.selectByExample(example);
    }


    public int updateById(LitemallCompanyHangzhanlou storeBrand){
        storeBrand.setUpdateTime(LocalDateTime.now());
        return companyHangzhanlouMapper.updateByPrimaryKeySelective(storeBrand);
    }

    public void deleteById(Integer id){
        companyHangzhanlouMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByComId(Integer comId){
        LitemallCompanyHangzhanlouExample example = new LitemallCompanyHangzhanlouExample();
        example.or().andComIdEqualTo(comId);
        companyHangzhanlouMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallCompanyHangzhanlou storeBrand){
        storeBrand.setAddTime(LocalDateTime.now());
        storeBrand.setUpdateTime(LocalDateTime.now());
        companyHangzhanlouMapper.insertSelective(storeBrand);
    }


    public LitemallCompanyHangzhanlou findById(Integer id){
        return companyHangzhanlouMapper.selectByPrimaryKey(id);
    }

    public List<LitemallCompanyHangzhanlou> queryByComId(Integer comId) {
        LitemallCompanyHangzhanlouExample example = new LitemallCompanyHangzhanlouExample();
        example.or().andComIdEqualTo(comId).andDeletedEqualTo(false);
        return companyHangzhanlouMapper.selectByExample(example);
    }
    }
