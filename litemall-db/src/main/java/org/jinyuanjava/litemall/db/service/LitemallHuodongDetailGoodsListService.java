package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallHuodongDetailGoodsListMapper;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailGoodsList;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailGoodsListExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallHuodongDetailGoodsListService {
    @Resource
    private LitemallHuodongDetailGoodsListMapper huodongDetailGoodsListMapper;

    //根据id查询
    public  LitemallHuodongDetailGoodsList queryById(Integer uid) {
        LitemallHuodongDetailGoodsListExample example = new LitemallHuodongDetailGoodsListExample();
        example.or().andIdEqualTo(uid);
        return huodongDetailGoodsListMapper.selectOneByExample(example);
    }

    //根据id查询
    public List<LitemallHuodongDetailGoodsList> queryByMainId(Integer mainId) {
        LitemallHuodongDetailGoodsListExample example = new LitemallHuodongDetailGoodsListExample();
        example.or().andMainIdEqualTo(mainId).andDeletedEqualTo(false);
        return huodongDetailGoodsListMapper.selectByExample(example);
    }


    public int add(LitemallHuodongDetailGoodsList HuodongMain) {
        HuodongMain.setAddTime(LocalDateTime.now());
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return huodongDetailGoodsListMapper.insertSelective(HuodongMain);
    }

    public int update(LitemallHuodongDetailGoodsList HuodongMain) {
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return huodongDetailGoodsListMapper.updateByPrimaryKeySelective(HuodongMain);
    }

    public void delete(Integer id) {
        huodongDetailGoodsListMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByMainId(Integer mainId) {
        LitemallHuodongDetailGoodsListExample example = new LitemallHuodongDetailGoodsListExample();
        LitemallHuodongDetailGoodsListExample.Criteria criteria = example.createCriteria();
        criteria.andMainIdEqualTo(mainId);
        huodongDetailGoodsListMapper.deleteByExample(example);
    }


    public List<LitemallHuodongDetailGoodsList> querySelective(Integer mainId,
                        Integer page, Integer limit, String sort) {
        LitemallHuodongDetailGoodsListExample example = new LitemallHuodongDetailGoodsListExample();
        LitemallHuodongDetailGoodsListExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(mainId)){
        	criteria.andMainIdEqualTo(mainId);
        }
//        if (!StringUtils.isEmpty(goodsList.getGoodsName())) {
//            criteria.andGoodsNameLike("%" + goodsList.getGoodsName() + "%");
//        }

        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return huodongDetailGoodsListMapper.selectByExample(example);
    }

}
