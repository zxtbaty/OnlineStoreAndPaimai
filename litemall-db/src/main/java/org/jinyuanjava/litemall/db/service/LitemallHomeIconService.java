package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallHomeIconMapper;
import org.jinyuanjava.litemall.db.domain.LitemallHomeIcon;
import org.jinyuanjava.litemall.db.domain.LitemallHomeIconExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallHomeIconService {
    @Resource
    private LitemallHomeIconMapper homeIconMapper;

    public List<LitemallHomeIcon> queryIndex(){
        LitemallHomeIconExample example=new LitemallHomeIconExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        example.orderBy("ordernumber");
        return homeIconMapper.selectByExample(example);
    }

    public List<LitemallHomeIcon> querySelective(String name,String remark,Integer page,Integer limit,String sort){
        LitemallHomeIconExample example=new LitemallHomeIconExample();
        LitemallHomeIconExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!StringUtils.isEmpty(remark)){
            criteria.andRemarkLike("%"+remark+"%");
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return homeIconMapper.selectByExample(example);
    }


    public int updateById(LitemallHomeIcon homeIcon){
        homeIcon.setUpdateTime(LocalDateTime.now());
        return homeIconMapper.updateByPrimaryKeySelective(homeIcon);
    }

    public void deleteById(Integer id){
        homeIconMapper.logicalDeleteByPrimaryKey(id);
    }


    public void add(LitemallHomeIcon homeIcon){
        homeIcon.setAddTime(LocalDateTime.now());
        homeIcon.setUpdateTime(LocalDateTime.now());
        homeIconMapper.insertSelective(homeIcon);
    }


    public LitemallHomeIcon findById(Integer id){
        return homeIconMapper.selectByPrimaryKey(id);
    }
}
