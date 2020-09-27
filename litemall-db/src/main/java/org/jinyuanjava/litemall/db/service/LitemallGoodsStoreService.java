package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsStoreMapper;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsStore;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsStoreExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallGoodsStoreService {
    @Resource
    private LitemallGoodsStoreMapper litemallGoodsStoreMapper;

    public List<LitemallGoodsStore> queryByGid(Integer id) {
        LitemallGoodsStoreExample example = new LitemallGoodsStoreExample();
        example.or().andGoodsIdEqualTo(id).andDeletedEqualTo(false);
        return litemallGoodsStoreMapper.selectByExample(example);
    }

    public List<Integer> queryByStoreId(Integer storeId) {
        LitemallGoodsStoreExample example = new LitemallGoodsStoreExample();
        example.or().andStoreIdEqualTo(storeId).andDeletedEqualTo(false);
        List<Integer> goodsIdList=litemallGoodsStoreMapper.selectByExample(example).stream().map(LitemallGoodsStore::getGoodsId).collect(Collectors.toList());
        return goodsIdList;
    }

    public LitemallGoodsStore findById(Integer id) {
        return litemallGoodsStoreMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsStoreExample example = new LitemallGoodsStoreExample();
        example.or().andGoodsIdEqualTo(gid);
        litemallGoodsStoreMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallGoodsStore litemallGoodsStore) {
        litemallGoodsStore.setAddTime(LocalDateTime.now());
        litemallGoodsStore.setUpdateTime(LocalDateTime.now());
        litemallGoodsStoreMapper.insertSelective(litemallGoodsStore);
    }

    public void update(LitemallGoodsStore litemallGoodsStore){
        litemallGoodsStore.setUpdateTime(LocalDateTime.now());
        litemallGoodsStoreMapper.updateByPrimaryKey(litemallGoodsStore);
    }

    public List<LitemallGoodsStore> getGoodsStoresForInterface(
            LocalDateTime lastUpdateTime, Integer page,Integer limit){
        LitemallGoodsStoreExample example = new LitemallGoodsStoreExample();
        LitemallGoodsStoreExample.Criteria criteria=example.createCriteria();
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }
        //criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page,limit);
        return litemallGoodsStoreMapper.selectByExample(example);
    }

}
