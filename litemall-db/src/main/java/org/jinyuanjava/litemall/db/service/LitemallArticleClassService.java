package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallArticleClassMapper;
import org.jinyuanjava.litemall.db.domain.LitemallArticleClass;
import org.jinyuanjava.litemall.db.domain.LitemallArticleClassExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallArticleClassService {
    @Resource
    private LitemallArticleClassMapper rticleClassMapper;
    private LitemallArticleClass.Column[] CHANNEL = {LitemallArticleClass.Column.id, LitemallArticleClass.Column.name};


    public List<LitemallArticleClass> queryAll() {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> queryLevel(int level, int page, int limit) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(level).andDeletedEqualTo(false);
        PageHelper.startPage(page, limit);
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> queryLevel(int level) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(level).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> queryByPid(Integer pid) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> queryByPid(Integer pid,Integer id, String name) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        LitemallArticleClassExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(pid).andDeletedEqualTo(false);
        if(id!=null){
            criteria.andIdEqualTo(id);
        };
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> queryByIds(int level, List<Integer> ids) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andIdIn(ids).andLevelEqualTo(level).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

    public LitemallArticleClass findById(Integer id) {
        return rticleClassMapper.selectByPrimaryKey(id);
    }

    public List<LitemallArticleClass> querySelective(String id, String name, Integer page, Integer size, String sort) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        LitemallArticleClassExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, size);
        return rticleClassMapper.selectByExample(example);
    }

    public List<LitemallArticleClass> querySelective(Integer id, String name) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        LitemallArticleClassExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        example.setOrderByClause("level,sort_order,id");

        return rticleClassMapper.selectByExample(example);
    }

    public int updateById(LitemallArticleClass category) {
        category.setUpdateTime(LocalDateTime.now());
        return rticleClassMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteById(Integer id) {
        rticleClassMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallArticleClass category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        rticleClassMapper.insertSelective(category);
    }

    public List<LitemallArticleClass> queryChannel(int level) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(level).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExampleSelective(example, CHANNEL);
    }

    public List<LitemallArticleClass> queryL1() {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(null).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

	public List<LitemallArticleClass> queryL2ByPid(int level, Integer pCategoryId) {
		LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(level).andPidEqualTo(pCategoryId).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
	}

    public List<LitemallArticleClass> getTreeArticleClassList(int level, Integer pCategoryId) {
        LitemallArticleClassExample example = new LitemallArticleClassExample();
        example.or().andLevelEqualTo(level).andPidEqualTo(pCategoryId).andDeletedEqualTo(false);
        return rticleClassMapper.selectByExample(example);
    }

}
