package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallCategoryMapper;
import org.jinyuanjava.litemall.db.domain.LitemallCategory;
import org.jinyuanjava.litemall.db.domain.LitemallCategoryExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCategoryService {
    @Resource
    private LitemallCategoryMapper categoryMapper;
    private LitemallCategory.Column[] CHANNEL = {LitemallCategory.Column.id, LitemallCategory.Column.name, LitemallCategory.Column.iconUrl};

    public List<LitemallCategory> queryL1WithoutRecommend(int offset, int limit) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
        example.setOrderByClause("sort_order");
        PageHelper.startPage(offset, limit);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL1(int offset, int limit) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        example.setOrderByClause("sort_order");
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL1(Integer comId) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria=example.createCriteria();
        criteria.andLevelEqualTo("L1").andDeletedEqualTo(false);
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        example.setOrderByClause("sort_order");
        //example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryByPid(Integer pid) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        example.setOrderByClause("sort_order");
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryByPClassName(String pClassName) {
        Integer pid=0;
        if(!StringUtils.isEmpty(pClassName)) {
            LitemallCategoryExample example = new LitemallCategoryExample();
            example.or().andNameEqualTo(pClassName).andDeletedEqualTo(false);
            LitemallCategory pClass = categoryMapper.selectOneByExample(example);
            if(pClass!=null){
                pid=pClass.getId();
            }
        }
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }


    public List<LitemallCategory> queryByPid(Integer pid,Integer id, String name,String comName) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(pid).andDeletedEqualTo(false);
        if(id!=null){
            criteria.andIdEqualTo(id);
        };
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!StringUtils.isEmpty(comName)){
            criteria.andComNameLike("%"+comName+"%");
        }
        example.setOrderByClause("sort_order");
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL2ByIds(List<Integer> ids) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.setOrderByClause("sort_order");
        example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryByIds(List<Integer> ids) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.setOrderByClause("sort_order");
        example.or().andIdIn(ids).andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }

    public LitemallCategory findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    public List<LitemallCategory> querySelective(String id, String name, Integer page, Integer size, String sort) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.createCriteria();

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
        return categoryMapper.selectByExample(example);
    }

    public int updateById(LitemallCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteById(Integer id) {
        categoryMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallCategory category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insertSelective(category);
    }

    public List<LitemallCategory> queryChannel() {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        example.setOrderByClause("sort_order");
        return categoryMapper.selectByExampleSelective(example, CHANNEL);
    }

	public List<LitemallCategory> queryL2() {
		LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L2").andDeletedEqualTo(false);
        example.setOrderByClause("sort_order");
        return categoryMapper.selectByExample(example);
	}

	public List<LitemallCategory> queryL2ByPid(Integer pCategoryId) {
		LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria=example.createCriteria();
        criteria.andLevelEqualTo("L2").andDeletedEqualTo(false);
        if(pCategoryId!=null){
            criteria.andPidEqualTo(pCategoryId);
        }
        example.setOrderByClause("sort_order");
        //example.or().andLevelEqualTo("L2").andPidEqualTo(pCategoryId).andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
	}


    /**
     * 查询某个品类名称是否已经存在
     */
    public boolean checkExistByNameAndId(String name,Integer id) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameEqualTo(name);
        }
        if(!StringUtils.isEmpty(id)){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);

        return categoryMapper.countByExample(example) != 0;
    }
}
