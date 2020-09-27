package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallCompanyBrandMapper;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyBrand;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyBrandExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCompanyBrandService {
    @Resource
    private LitemallCompanyBrandMapper companyBrandMapper;


    public List<LitemallCompanyBrand> queryIndex(){
        LitemallCompanyBrandExample example=new LitemallCompanyBrandExample();
        example.or().andDeletedEqualTo(false);
        return companyBrandMapper.selectByExample(example);
    }

    public List<LitemallCompanyBrand> querySelective(Integer comId,LocalDateTime lastUpdateTime, Integer page,Integer limit,String sort){
        LitemallCompanyBrandExample example=new LitemallCompanyBrandExample();
        LitemallCompanyBrandExample.Criteria criteria=example.createCriteria();
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
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
        return companyBrandMapper.selectByExample(example);
    }


    public int updateById(LitemallCompanyBrand storeBrand){
        storeBrand.setUpdateTime(LocalDateTime.now());
        return companyBrandMapper.updateByPrimaryKeySelective(storeBrand);
    }

    public void deleteById(Integer id){
        companyBrandMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByComId(Integer comId){
        LitemallCompanyBrandExample example = new LitemallCompanyBrandExample();
        example.or().andComIdEqualTo(comId);
        companyBrandMapper.logicalDeleteByExample(example);
    }

    public void deleteByBrandId(Integer brandId){
        LitemallCompanyBrandExample example = new LitemallCompanyBrandExample();
        example.or().andBrandIdEqualTo(brandId);
        companyBrandMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallCompanyBrand storeBrand){
        storeBrand.setAddTime(LocalDateTime.now());
        storeBrand.setUpdateTime(LocalDateTime.now());
        companyBrandMapper.insertSelective(storeBrand);
    }


    public LitemallCompanyBrand findById(Integer id){
        return companyBrandMapper.selectByPrimaryKey(id);
    }

    public List<LitemallCompanyBrand> queryByComId(Integer comId) {
        LitemallCompanyBrandExample example = new LitemallCompanyBrandExample();
        LitemallCompanyBrandExample.Criteria criteria=example.createCriteria();
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        criteria.andDeletedEqualTo(false);
//        example.or().andComIdEqualTo(comId).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber");
        return companyBrandMapper.selectByExample(example);
    }
    }
