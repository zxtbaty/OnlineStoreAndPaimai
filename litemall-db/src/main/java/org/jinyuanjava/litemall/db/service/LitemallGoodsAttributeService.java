package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsAttributeMapper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsMapper;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsAttribute;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsAttributeExample;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallGoodsAttributeService {
    @Resource
    private LitemallGoodsAttributeMapper goodsAttributeMapper;

    @Resource
    private LitemallGoodsMapper goodsMapper;

    public List<LitemallGoodsAttribute> queryByGid(Integer goodsId) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return goodsAttributeMapper.selectByExample(example);
    }

    public void add(LitemallGoodsAttribute goodsAttribute) {
        goodsAttribute.setAddTime(LocalDateTime.now());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttributeMapper.insertSelective(goodsAttribute);
    }

    public void update(LitemallGoodsAttribute goodsAttribute){
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttributeMapper.updateByPrimaryKey(goodsAttribute);
    }

    public LitemallGoodsAttribute findById(Integer id) {
        return goodsAttributeMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        example.or().andGoodsIdEqualTo(gid);
        goodsAttributeMapper.logicalDeleteByExample(example);
    }

    public List<LitemallGoodsAttribute> getGoodsAttributeForInterface(
            LocalDateTime lastUpdateTime,Integer comId, Integer page,Integer limit){
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        LitemallGoodsAttributeExample.Criteria criteria=example.createCriteria();
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }
        if(!StringUtils.isEmpty(comId)){
            LitemallGoodsExample goodsExample=new LitemallGoodsExample();
            LitemallGoodsExample.Criteria goodsCriteria= goodsExample.createCriteria();
            goodsCriteria.andComIdEqualTo(comId);
            List<LitemallGoods> goodsList= goodsMapper.selectByExample(goodsExample);
            List<Integer> goodsIds=goodsList.stream().map(LitemallGoods::getId).collect(Collectors.toList());
            if(goodsIds!=null&&goodsIds.size()>0){
                criteria.andGoodsIdIn(goodsIds);
            }
        }
        //criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page,limit);
        return goodsAttributeMapper.selectByExample(example);
    }
}
