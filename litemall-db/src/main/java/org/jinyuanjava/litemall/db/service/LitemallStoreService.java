package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;

import org.jinyuanjava.litemall.db.dao.LitemallStoreMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallStoreService {
    @Resource
    private LitemallStoreMapper litemallStoreMapper;

    @Autowired
    private LitemallGoodsStoreService litemallGoodsStoreService;

    @Autowired
    private LitemallStoreBrandService litemallStoreBrandService;

    public List<LitemallStore> querySelective(String name,
                                              Integer comId,
                                              Integer ownType,
                                              Integer brandId,
                                              LocalDateTime updateTime,
                                              Integer page,Integer limit,String sort){
        LitemallStoreExample example=new LitemallStoreExample();
        LitemallStoreExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        if(ownType!=null){
            if(ownType==0)
            {
                criteria.andOwnTypeEqualTo(false);
            } else
            {
                criteria.andOwnTypeEqualTo(true);
            }
        }
        if(brandId!=null){
            List<Integer> storeIds= litemallStoreBrandService.queryByBrandId(brandId);
            if(storeIds.size()>0) {
                criteria.andIdIn(storeIds);
            }
        }

        if(updateTime!=null){
            criteria.andUpdateTimeGreaterThan(updateTime);
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(limit<999999) {
            PageHelper.startPage(page, limit);
        }
        return litemallStoreMapper.selectByExample(example);
    }

    public void add(LitemallStore store){
        store.setAddTime(LocalDateTime.now());
        store.setUpdateTime(LocalDateTime.now());
        litemallStoreMapper.insertSelective(store);
    }

    public int updateById(LitemallStore store){
        store.setUpdateTime(LocalDateTime.now());
        return litemallStoreMapper.updateByPrimaryKeySelective(store);
    }
    public void deleteById(Integer id){
        litemallStoreMapper.logicalDeleteByPrimaryKey(id);
    }



    public LitemallStore findById(Integer id){
        return litemallStoreMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByName(String name) {
        LitemallStoreExample example = new LitemallStoreExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return litemallStoreMapper.countByExample(example) != 0;
    }

	public List<LitemallStore> queryByGoodsIdAndComId(Integer goodsId, Integer comId) {

        LitemallStoreExample example = new LitemallStoreExample();
        LitemallStoreExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(comId) ){
            criteria.andComIdEqualTo(comId);
        }
        criteria.andDeletedEqualTo(false);

        if(goodsId!=null) {
            List<LitemallGoodsStore> litemallGoodsStore = litemallGoodsStoreService.queryByGid(goodsId);
            List<Integer> storeIds=litemallGoodsStore.stream().map(LitemallGoodsStore::getStoreId).collect(Collectors.toList());
            if(storeIds.size()>0){
                criteria.andIdIn(storeIds);
            }
        }
        //example.or().andNameEqualTo(name).andDeletedEqualTo(false);
		List<LitemallStore> list = litemallStoreMapper.selectByExample(example);
		return list;
	}

	public List<LitemallStore> findByIds(List<Integer> storeIds) {
		 LitemallStoreExample example = new LitemallStoreExample();
	     LitemallStoreExample.Criteria criteria=example.createCriteria();
	     if(storeIds!=null && storeIds.size()>0){
	    	 criteria.andIdIn(storeIds);
	    	 List<LitemallStore> list = litemallStoreMapper.selectByExample(example);
	    	 return list;
	     }
		return null;
	}

    public List<LitemallStore> queryAll() {
        LitemallStoreExample example = new LitemallStoreExample();
        LitemallStoreExample.Criteria criteria=example.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andEnabledEqualTo(true);
        return litemallStoreMapper.selectByExample(example);

    }
}
