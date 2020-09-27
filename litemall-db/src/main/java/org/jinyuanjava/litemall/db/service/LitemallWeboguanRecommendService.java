package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallWeboguanRecommendMapper;
import org.jinyuanjava.litemall.db.dao.LitemallWenboguanArticleMapper;
import org.jinyuanjava.litemall.db.dao.ViewWeboguanRecommendMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallWeboguanRecommendService {
    @Resource
    private LitemallWeboguanRecommendMapper articleRecommendMapper;

    @Resource
    private ViewWeboguanRecommendMapper recommendMapper;

    @Resource
    private LitemallWenboguanArticleMapper articleMapper;


    public List<LitemallWeboguanRecommend> queryIndex() {
        LitemallWeboguanRecommendExample example = new LitemallWeboguanRecommendExample();
        example.or().andDeletedEqualTo(false);
        example.orderBy("sort_order asc");
        return articleRecommendMapper.selectByExample(example);
    }


    public List<ViewWeboguanRecommendWithBLOBs> querySelective( Integer page, Integer limit ) {

        LitemallWeboguanRecommendExample example = new LitemallWeboguanRecommendExample();
        example.or().andDeletedEqualTo(false);
        example.orderBy("ordernumber asc");
        PageHelper.startPage(page, limit);
        List<LitemallWeboguanRecommend> recommendList=  articleRecommendMapper.selectByExample(example);

        if(recommendList!=null&&recommendList.size()>0){
            List<Integer> articleIdList=recommendList.stream().map(LitemallWeboguanRecommend::getArticleId).collect(Collectors.toList());
            ViewWeboguanRecommendExample articleExample = new ViewWeboguanRecommendExample();
            articleExample.or().andArticleIdIn(articleIdList);
            List<ViewWeboguanRecommendWithBLOBs> articleList=  recommendMapper.selectByExampleWithBLOBs(articleExample);
            return articleList;
        } else
        {
            return null;
        }
    }

    public int updateById(LitemallWeboguanRecommend articleList) {
        articleList.setUpdateTime(LocalDateTime.now());
        return articleRecommendMapper.updateByPrimaryKeySelective(articleList);
    }


    public void deleteById(Integer id) {
        articleRecommendMapper.logicalDeleteByPrimaryKey(id);
    }
    public void deleteByArticleId(Integer id) {
        LitemallWeboguanRecommendExample example = new LitemallWeboguanRecommendExample();
        example.or().andDeletedEqualTo(false).andArticleIdEqualTo(id);
        articleRecommendMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallWeboguanRecommend articleList) {
        articleList.setAddTime(LocalDateTime.now());
        articleList.setUpdateTime(LocalDateTime.now());
        articleRecommendMapper.insertSelective(articleList);
    }

    public LitemallWeboguanRecommend findById(Integer id) {
        return articleRecommendMapper.selectByPrimaryKey(id);
    }
}
