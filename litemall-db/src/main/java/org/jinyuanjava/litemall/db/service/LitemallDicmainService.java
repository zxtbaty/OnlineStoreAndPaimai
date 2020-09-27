package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallDicMainMapper;
import org.jinyuanjava.litemall.db.domain.LitemallDicMain;
import org.jinyuanjava.litemall.db.domain.LitemallDicMainExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallDicmainService {
    @Resource
    private LitemallDicMainMapper litemallDicMainMapper;

    public List<LitemallDicMain> queryIndex(){
        LitemallDicMainExample example=new LitemallDicMainExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        return litemallDicMainMapper.selectByExample(example);
    }

    public List<LitemallDicMain> querySelective(String name, Integer page,Integer limit,String sort){
        LitemallDicMainExample example=new LitemallDicMainExample();
        LitemallDicMainExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallDicMainMapper.selectByExample(example);
    }


    public int updateById(LitemallDicMain homeIcon){
        homeIcon.setUpdateTime(LocalDateTime.now());
        return litemallDicMainMapper.updateByPrimaryKeySelective(homeIcon);
    }

    public void deleteById(Integer id){
        litemallDicMainMapper.logicalDeleteByPrimaryKey(id);
    }


    public void add(LitemallDicMain homeIcon){
        homeIcon.setAddTime(LocalDateTime.now());
        homeIcon.setUpdateTime(LocalDateTime.now());
        litemallDicMainMapper.insertSelective(homeIcon);
    }


    public LitemallDicMain findById(Integer id){
        return litemallDicMainMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByName(String name) {
        LitemallDicMainExample example = new LitemallDicMainExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return litemallDicMainMapper.countByExample(example) != 0;
    }
}
