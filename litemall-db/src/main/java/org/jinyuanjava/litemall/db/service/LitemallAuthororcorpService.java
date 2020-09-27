package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAuthororcorpMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorp;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAuthororcorpService {
    @Resource
    private LitemallAuthororcorpMapper authororcorpMapper;


    public LitemallAuthororcorp query(Integer id) {
        LitemallAuthororcorpExample example = new LitemallAuthororcorpExample();
        example.or().andIdEqualTo(id);
        return authororcorpMapper.selectOneByExample(example);
    }

    public LitemallAuthororcorp findFist() {
        LitemallAuthororcorpExample example = new LitemallAuthororcorpExample();
        example.or().andDeletedEqualTo(false);
        return authororcorpMapper.selectOneByExample(example);
    }

    public int add(LitemallAuthororcorp address) {
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return authororcorpMapper.insertSelective(address);
    }

    public int update(LitemallAuthororcorp address) {
        address.setUpdateTime(LocalDateTime.now());
        return authororcorpMapper.updateByPrimaryKeySelective(address);
    }

    public void delete(Integer id) {
        authororcorpMapper.logicalDeleteByPrimaryKey(id);
    }



    public List<LitemallAuthororcorp> querySelective(String name, Integer page, Integer limit, String sort) {
        LitemallAuthororcorpExample example = new LitemallAuthororcorpExample();
        LitemallAuthororcorpExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return authororcorpMapper.selectByExample(example);
    }

    public List<LitemallAuthororcorp> queryAll() {
        LitemallAuthororcorpExample example = new LitemallAuthororcorpExample();
        example.or().andDeletedEqualTo(false);
        return authororcorpMapper.selectByExample(example);
    }

    /**
     * 查询某个自提站点名称是否已经存在
     */
    public boolean checkExistByNameAndId(String name,Integer id) {
        LitemallAuthororcorpExample example = new LitemallAuthororcorpExample();
        LitemallAuthororcorpExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameEqualTo(name);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);

        return authororcorpMapper.countByExample(example) != 0;
    }

}
