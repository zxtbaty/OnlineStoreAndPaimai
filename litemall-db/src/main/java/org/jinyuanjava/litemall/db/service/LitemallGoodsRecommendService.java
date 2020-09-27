package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsRecommendMapper;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsRecommend;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsRecommendExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGoodsRecommendService {
    @Resource
    private LitemallGoodsRecommendMapper litemallGoodsRecommendMapper;


    public List<LitemallGoodsRecommend> queryIndex(){
        LitemallGoodsRecommendExample example=new LitemallGoodsRecommendExample();
        example.or().andDeletedEqualTo(false);
        return litemallGoodsRecommendMapper.selectByExample(example);
    }

    public List<LitemallGoodsRecommend> querySelective(String postype,Integer comid, Integer page,Integer limit,String sort){
        LitemallGoodsRecommendExample example=new LitemallGoodsRecommendExample();
        LitemallGoodsRecommendExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(postype)){
            criteria.andPosTypeEqualTo(postype);
        }
        if(comid!=null){
            criteria.andComIdEqualTo(comid);
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallGoodsRecommendMapper.selectByExample(example);
    }


    public int updateById(LitemallGoodsRecommend homeIcon){
        homeIcon.setUpdateTime(LocalDateTime.now());
        return litemallGoodsRecommendMapper.updateByPrimaryKeySelective(homeIcon);
    }

    public void deleteById(Integer id){
        litemallGoodsRecommendMapper.logicalDeleteByPrimaryKey(id);
    }


    public void add(LitemallGoodsRecommend homeIcon){
        homeIcon.setAddTime(LocalDateTime.now());
        homeIcon.setUpdateTime(LocalDateTime.now());
        litemallGoodsRecommendMapper.insertSelective(homeIcon);
    }


    public LitemallGoodsRecommend findById(Integer id){
        return litemallGoodsRecommendMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByPostypeAndComidAndGoodsid(String posname,Integer comid,Integer goodsid) {
        LitemallGoodsRecommendExample example = new LitemallGoodsRecommendExample();
        example.or().andPosTypeEqualTo(posname).andComIdEqualTo(comid).andGoodsIdEqualTo(goodsid).andDeletedEqualTo(false);
        return litemallGoodsRecommendMapper.countByExample(example) != 0;
    }
}
