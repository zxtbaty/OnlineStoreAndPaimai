package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallOpadminDefMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminDef;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminDefExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallOpadminDefService {
    @Resource
    private LitemallOpadminDefMapper litemallOpadminDefMapper;

    public List<LitemallOpadminDef> querySelective(
            String typeCode,String typeName, String title,Integer expireFlag,
            Integer page,Integer limit,String sort){
        LitemallOpadminDefExample example=new LitemallOpadminDefExample();
        LitemallOpadminDefExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(typeCode)){
            criteria.andTypeCodeEqualTo(typeCode);
        }
        if(!StringUtils.isEmpty(typeName)){
            criteria.andTypeNameEqualTo(typeName);
        }
        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%"+title+"%");
        }
        if(!StringUtils.isEmpty(expireFlag)){
            if(expireFlag==0) {
                criteria.andExpireFlagEqualTo(false);
            } else  if(expireFlag==1) {
                criteria.andExpireFlagEqualTo(true);
            }
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallOpadminDefMapper.selectByExample(example);
    }

    public void add(LitemallOpadminDef opadminDef){
        opadminDef.setAddTime(LocalDateTime.now());
        opadminDef.setUpdateTime(LocalDateTime.now());
        litemallOpadminDefMapper.insertSelective(opadminDef);
    }

    public int updateById(LitemallOpadminDef opadminDef){
        opadminDef.setUpdateTime(LocalDateTime.now());
        return litemallOpadminDefMapper.updateByPrimaryKeySelective(opadminDef);
    }
    public void deleteById(Integer id){
        litemallOpadminDefMapper.logicalDeleteByPrimaryKey(id);
    }


    public LitemallOpadminDef findById(Integer id){
        return litemallOpadminDefMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByName(String typeName, String title) {
        LitemallOpadminDefExample example = new LitemallOpadminDefExample();
        example.or().andTypeNameEqualTo(typeName).andTitleEqualTo(title).andDeletedEqualTo(false);
        return litemallOpadminDefMapper.countByExample(example) != 0;
    }
}
