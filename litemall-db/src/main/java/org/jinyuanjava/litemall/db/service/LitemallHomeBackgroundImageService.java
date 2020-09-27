package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallHomeBackgroundImageMapper;
import org.jinyuanjava.litemall.db.domain.LitemallHomeBackgroundImage;
import org.jinyuanjava.litemall.db.domain.LitemallHomeBackgroundImageExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallHomeBackgroundImageService {
    @Resource
    private LitemallHomeBackgroundImageMapper homeBackgroudImage;

    public List<LitemallHomeBackgroundImage> queryIndex(){
        LitemallHomeBackgroundImageExample example=new LitemallHomeBackgroundImageExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        example.orderBy("ordernumber");
        return homeBackgroudImage.selectByExample(example);
    }

    public List<LitemallHomeBackgroundImage> querySelective(String name,String remark,Integer page,Integer limit,String sort){
        LitemallHomeBackgroundImageExample example=new LitemallHomeBackgroundImageExample();
        LitemallHomeBackgroundImageExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!StringUtils.isEmpty(remark)){
            criteria.andRemarkLike("%"+remark+"%");
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return homeBackgroudImage.selectByExample(example);
    }


    public int updateById(LitemallHomeBackgroundImage homeIcon){
        homeIcon.setUpdateTime(LocalDateTime.now());
        return homeBackgroudImage.updateByPrimaryKeySelective(homeIcon);
    }

    public void deleteById(Integer id){
        homeBackgroudImage.logicalDeleteByPrimaryKey(id);
    }


    public void add(LitemallHomeBackgroundImage homeIcon){
        homeIcon.setAddTime(LocalDateTime.now());
        homeIcon.setUpdateTime(LocalDateTime.now());
        homeBackgroudImage.insertSelective(homeIcon);
    }


    public LitemallHomeBackgroundImage findById(Integer id){
        return homeBackgroudImage.selectByPrimaryKey(id);
    }
}
