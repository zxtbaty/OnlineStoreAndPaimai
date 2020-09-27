package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallDicCodeMapper;
import org.jinyuanjava.litemall.db.domain.LitemallDicCode;
import org.jinyuanjava.litemall.db.domain.LitemallDicCodeExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallDiccodeService {
    @Resource
    private LitemallDicCodeMapper litemallDicCodeMapper;

    public List<LitemallDicCode> queryIndex(){
        LitemallDicCodeExample example=new LitemallDicCodeExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        return litemallDicCodeMapper.selectByExample(example);
    }

    /**
     * 模糊查找
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @return
     */
    public List<LitemallDicCode> querySelective(String name, Integer page,Integer limit,String sort){
        LitemallDicCodeExample example=new LitemallDicCodeExample();
        LitemallDicCodeExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        } else{
            example.setOrderByClause("ordernumber");
        }
        PageHelper.startPage(page,limit);
        return litemallDicCodeMapper.selectByExample(example);
    }

    /**
     * 精确查找
     * @param name
     * @param sort
     * @return
     */
    public List<LitemallDicCode> findByDicmainIdOrDicmainName(Integer dicmainId, String name,String sort){
        LitemallDicCodeExample example=new LitemallDicCodeExample();
        LitemallDicCodeExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(dicmainId)){
            criteria.andMainidEqualTo(dicmainId);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andMainnameEqualTo(name);
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }else
        {
            example.setOrderByClause("ordernumber");
        }

        return litemallDicCodeMapper.selectByExample(example);
    }

    public int updateById(LitemallDicCode litemallDicCode){
        litemallDicCode.setUpdateTime(LocalDateTime.now());
        return litemallDicCodeMapper.updateByPrimaryKeySelective(litemallDicCode);
    }

    public void deleteById(Integer id){
        litemallDicCodeMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByMainId(Integer mainId){
        LitemallDicCodeExample example = new LitemallDicCodeExample();
        example.or().andMainidEqualTo(mainId);
        litemallDicCodeMapper.logicalDeleteByExample(example);
    }


    public void add(LitemallDicCode litemallDicCode){
        litemallDicCode.setAddTime(LocalDateTime.now());
        litemallDicCode.setUpdateTime(LocalDateTime.now());
        litemallDicCodeMapper.insertSelective(litemallDicCode);
    }


    public LitemallDicCode findById(Integer id){
        return litemallDicCodeMapper.selectByPrimaryKey(id);
    }

    public List<LitemallDicCode> queryByMainId(Integer gid) {
        LitemallDicCodeExample example = new LitemallDicCodeExample();
        example.or().andMainidEqualTo(gid).andDeletedEqualTo(false);
        example.setOrderByClause("ordernumber");
        return litemallDicCodeMapper.selectByExample(example);
    }
}
