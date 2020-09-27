package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallHuodongMainMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallHuodongMainService {
    @Resource
    private LitemallHuodongMainMapper huodongMainMapper;

    //根据id查询
    public  LitemallHuodongMain queryById(Integer id) {
        LitemallHuodongMainExample example = new LitemallHuodongMainExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return huodongMainMapper.selectOneByExample(example);
    }


    public int add(LitemallHuodongMain HuodongMain) {
        HuodongMain.setAddTime(LocalDateTime.now());
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return huodongMainMapper.insertSelective(HuodongMain);
    }

    public int update(LitemallHuodongMain HuodongMain) {
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return huodongMainMapper.updateByPrimaryKeySelective(HuodongMain);
    }

    public void delete(Integer id) {
        huodongMainMapper.logicalDeleteByPrimaryKey(id);
    }


    public List<LitemallHuodongMain> querySelective(String name,Boolean expireFlag,
                        Integer page, Integer limit, String sort) {
        LitemallHuodongMainExample example = new LitemallHuodongMainExample();
        LitemallHuodongMainExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if(expireFlag!=null){
        	criteria.andExpireFlagEqualTo(expireFlag);
        }

        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return huodongMainMapper.selectByExample(example);
    }

    /**
     * 查询某活动名称是否已经存在
     */
    public boolean checkExistByNameAndId(String name,Integer id) {
        LitemallHuodongMainExample example = new LitemallHuodongMainExample();
        LitemallHuodongMainExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameEqualTo(name);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);

        return huodongMainMapper.countByExample(example) != 0;
    }

    /**
     * 查询状态正常，但是时间已经过期的活动设置,更新成已经过期
     *
     * @return
     */
    public List<LitemallHuodongMain> queryHaveExpiredButStateError() {
        LitemallHuodongMainExample example = new LitemallHuodongMainExample();
        example.or().andExpireFlagEqualTo(false).andEndDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return huodongMainMapper.selectByExample(example);
    }

}
