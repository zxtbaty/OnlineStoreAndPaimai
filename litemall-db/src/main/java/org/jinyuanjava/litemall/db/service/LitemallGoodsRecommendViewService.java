package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.ViewLitemallGoodsRecommendMapper;
import org.jinyuanjava.litemall.db.domain.ViewLitemallGoodsRecommend;
import org.jinyuanjava.litemall.db.domain.ViewLitemallGoodsRecommendExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallGoodsRecommendViewService {
    @Resource
    private ViewLitemallGoodsRecommendMapper viewLitemallGoodsRecommendMapper;

    public List<ViewLitemallGoodsRecommend> querySelective(String posType, Integer comId,
                                                           Integer page, Integer limit, String sort){
        ViewLitemallGoodsRecommendExample example=new ViewLitemallGoodsRecommendExample();
        ViewLitemallGoodsRecommendExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(posType)){
            criteria.andPosTypeEqualTo(posType);
        }
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }

        criteria.andDeletedEqualTo(false);
//        if(!StringUtils.isEmpty(sort)){
//            example.setOrderByClause(sort);
//        }
        PageHelper.startPage(page,limit);
        return viewLitemallGoodsRecommendMapper.selectByExample(example);
    }

}
