package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPickSiteMapper;
import org.jinyuanjava.litemall.db.domain.LitemallPickSite;
import org.jinyuanjava.litemall.db.domain.LitemallPickSiteExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallPickSiteService {
    @Resource
    private LitemallPickSiteMapper pickSiteMapper;


    public LitemallPickSite query(Integer id) {
        LitemallPickSiteExample example = new LitemallPickSiteExample();
        example.or().andIdEqualTo(id);
        return pickSiteMapper.selectOneByExample(example);
    }

    public LitemallPickSite findFist() {
        LitemallPickSiteExample example = new LitemallPickSiteExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        return pickSiteMapper.selectOneByExample(example);
    }

    public int add(LitemallPickSite address) {
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return pickSiteMapper.insertSelective(address);
    }

    public int update(LitemallPickSite address) {
        address.setUpdateTime(LocalDateTime.now());
        return pickSiteMapper.updateByPrimaryKeySelective(address);
    }

    public void delete(Integer id) {
        pickSiteMapper.logicalDeleteByPrimaryKey(id);
    }



    public List<LitemallPickSite> querySelective(String name, Integer page, Integer limit, String sort) {
        LitemallPickSiteExample example = new LitemallPickSiteExample();
        LitemallPickSiteExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andSiteNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return pickSiteMapper.selectByExample(example);
    }

    public List<LitemallPickSite> querySelectiveEnable(String name, Integer page, Integer limit, String sort) {
        LitemallPickSiteExample example = new LitemallPickSiteExample();
        LitemallPickSiteExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andSiteNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false).andEnabledEqualTo(true);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return pickSiteMapper.selectByExample(example);
    }


    /**
     * 查询某个自提站点名称是否已经存在
     */
    public boolean checkExistByNameAndId(String name,Integer id) {
        LitemallPickSiteExample example = new LitemallPickSiteExample();
        LitemallPickSiteExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andSiteNameEqualTo(name);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);

        return pickSiteMapper.countByExample(example) != 0;
    }

}
