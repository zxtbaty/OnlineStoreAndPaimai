package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallStoreBrandMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallStoreBrandService {
    @Resource
    private LitemallStoreBrandMapper storeBrandMapper;


    public List<LitemallStoreBrand> queryIndex(){
        LitemallStoreBrandExample example=new LitemallStoreBrandExample();
        example.or().andDeletedEqualTo(false);
        return storeBrandMapper.selectByExample(example);
    }

    public List<LitemallStoreBrand> querySelective(Integer storeId, Integer page,Integer limit,String sort){
        LitemallStoreBrandExample example=new LitemallStoreBrandExample();
        LitemallStoreBrandExample.Criteria criteria=example.createCriteria();
        if(storeId!=null){
            criteria.andStoreIdEqualTo(storeId);
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return storeBrandMapper.selectByExample(example);
    }


    public int updateById(LitemallStoreBrand storeBrand){
        storeBrand.setUpdateTime(LocalDateTime.now());
        return storeBrandMapper.updateByPrimaryKeySelective(storeBrand);
    }

    public void deleteById(Integer id){
        storeBrandMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByStoreId(Integer storeId){
        LitemallStoreBrandExample example = new LitemallStoreBrandExample();
        example.or().andStoreIdEqualTo(storeId);
        storeBrandMapper.logicalDeleteByExample(example);
    }

    public void deleteByBrandId(Integer brandId){
        LitemallStoreBrandExample example = new LitemallStoreBrandExample();
        example.or().andBrandIdEqualTo(brandId);
        storeBrandMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallStoreBrand storeBrand){
        storeBrand.setAddTime(LocalDateTime.now());
        storeBrand.setUpdateTime(LocalDateTime.now());
        storeBrandMapper.insertSelective(storeBrand);
    }


    public LitemallStoreBrand findById(Integer id){
        return storeBrandMapper.selectByPrimaryKey(id);
    }

    public List<LitemallStoreBrand> queryByStoreId(Integer storeId) {
        LitemallStoreBrandExample example = new LitemallStoreBrandExample();
        example.or().andStoreIdEqualTo(storeId).andDeletedEqualTo(false);
        return storeBrandMapper.selectByExample(example);
    }
    public List<Integer> queryByBrandId(Integer brandId) {
        LitemallStoreBrandExample example = new LitemallStoreBrandExample();
        example.or().andBrandIdEqualTo(brandId).andDeletedEqualTo(false);
        List<LitemallStoreBrand> result= storeBrandMapper.selectByExample(example);

        return result.stream().map(LitemallStoreBrand::getStoreId).collect(Collectors.toList());
    }

    }
