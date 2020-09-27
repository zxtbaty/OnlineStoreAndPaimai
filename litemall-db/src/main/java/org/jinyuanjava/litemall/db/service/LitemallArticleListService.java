package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallArticleListMapper;
import org.jinyuanjava.litemall.db.domain.LitemallArticleList;
import org.jinyuanjava.litemall.db.domain.LitemallArticleListExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallArticleListService {
    @Resource
    private LitemallArticleListMapper articleListMapper;

    public List<LitemallArticleList> queryIndex() {
        LitemallArticleListExample example = new LitemallArticleListExample();
        example.or().andDeletedEqualTo(false);
        example.orderBy("sort_order asc");
        return articleListMapper.selectByExample(example);
    }

    public List<LitemallArticleList> querySelective(Integer classId,String className,
                                                    String title,String author,
                                                    Integer page, Integer limit, String sort) {
        LitemallArticleListExample example = new LitemallArticleListExample();
        LitemallArticleListExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(classId)) {
            criteria.andClassIdEqualTo(classId);
        }
        if (!StringUtils.isEmpty(className)) {
            criteria.andClassNameLike("%" + className + "%");
        }
        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (!StringUtils.isEmpty(author)) {
            criteria.andAuthorLike("%" + author + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return articleListMapper.selectByExample(example);
    }

    public int updateById(LitemallArticleList articleList) {
        articleList.setUpdateTime(LocalDateTime.now());
        return articleListMapper.updateByPrimaryKeySelective(articleList);
    }

    public void deleteById(Integer id) {
        articleListMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallArticleList articleList) {
        articleList.setAddTime(LocalDateTime.now());
        articleList.setUpdateTime(LocalDateTime.now());
        articleListMapper.insertSelective(articleList);
    }

    public LitemallArticleList findById(Integer id) {
        return articleListMapper.selectByPrimaryKey(id);
    }
}
