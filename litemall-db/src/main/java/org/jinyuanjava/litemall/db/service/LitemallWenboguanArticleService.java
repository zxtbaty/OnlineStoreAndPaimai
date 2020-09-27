package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallWeboguanRecommendMapper;
import org.jinyuanjava.litemall.db.dao.LitemallWenboguanArticleMapper;
import org.jinyuanjava.litemall.db.dao.ViewWeboguanRecommendMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallWenboguanArticleService {
    @Resource
    private LitemallWenboguanArticleMapper articleMapper;

    @Resource
    private ViewWeboguanRecommendMapper recommendMapper;

    @Resource
    private LitemallWeboguanRecommendMapper weboguanRecommendMapper;

    public List<LitemallWenboguanArticleWithBLOBs> queryIndex(Integer classId,
        String title,String author,
        Integer page, Integer limit ) {
        LitemallWenboguanArticleExample example = new LitemallWenboguanArticleExample();
        LitemallWenboguanArticleExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(classId)){
            criteria.andClassIdEqualTo(classId);
        }
        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%"+title+"%");
        }
        if(!StringUtils.isEmpty(author)){
            criteria.andAuthorLike("%"+author+"%");
        }

        example.orderBy("sort_order asc,update_time desc");
        PageHelper.startPage(page, limit);
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public List<LitemallWenboguanArticleWithBLOBs> queryUnRecommendList(Integer classId,
        String title,String author, Integer page, Integer limit ) {
        LitemallWenboguanArticleExample example = new LitemallWenboguanArticleExample();
        LitemallWenboguanArticleExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(classId)){
            criteria.andClassIdEqualTo(classId);
        }
        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%"+title+"%");
        }
        if(!StringUtils.isEmpty(author)){
            criteria.andAuthorLike("%"+author+"%");
        }

        LitemallWeboguanRecommendExample recommendExample=new LitemallWeboguanRecommendExample();
        recommendExample.or().andDeletedEqualTo(false);
        List<LitemallWeboguanRecommend> recommendList= weboguanRecommendMapper.selectByExample(recommendExample);

        if(recommendList!=null&&recommendList.size()>0){
            List<Integer> articleRecommendList=recommendList.stream().map(LitemallWeboguanRecommend::getArticleId).collect(Collectors.toList());
            criteria.andIdNotIn(articleRecommendList);
        }

        example.orderBy("sort_order asc,update_time desc");
        PageHelper.startPage(page, limit);
        return articleMapper.selectByExampleWithBLOBs(example);
    }

    public List<ViewWeboguanRecommendWithBLOBs> querySelective(Integer classId,
         Integer page, Integer limit ) {
        ViewWeboguanRecommendExample example = new ViewWeboguanRecommendExample();
        ViewWeboguanRecommendExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(classId)) {
            criteria.andClassIdEqualTo(classId);
        }
        PageHelper.startPage(page, limit);
        return recommendMapper.selectByExampleWithBLOBs(example);
    }

    public int updateById(LitemallWenboguanArticleWithBLOBs articleList) {
        articleList.setUpdateTime(LocalDateTime.now());
        return articleMapper.updateByPrimaryKeySelective(articleList);
    }

    public void deleteById(Integer id) {
        articleMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallWenboguanArticleWithBLOBs articleList) {
        articleList.setAddTime(LocalDateTime.now());
        articleList.setUpdateTime(LocalDateTime.now());
        articleMapper.insertSelective(articleList);
    }

    public LitemallWenboguanArticleWithBLOBs findById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }
}
