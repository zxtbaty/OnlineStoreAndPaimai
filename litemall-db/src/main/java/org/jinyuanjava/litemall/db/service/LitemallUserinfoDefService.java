package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUserinfoDefMapper;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDefExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallUserinfoDefService {
    @Resource
    private LitemallUserinfoDefMapper litemallUserinfoDefMapper;

    @Autowired
    private LitemallUserinfoPubService userinfoPubService;

    public List<LitemallUserinfoDef> querySelective(
            String typeCode,String typeName,String title,Integer expireFlag,
            Integer page,Integer limit,String sort){
        LitemallUserinfoDefExample example=new LitemallUserinfoDefExample();
        LitemallUserinfoDefExample.Criteria criteria=example.createCriteria();
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
        return litemallUserinfoDefMapper.selectByExample(example);
    }

    public void add(LitemallUserinfoDef userinfoDef){
        userinfoDef.setAddTime(LocalDateTime.now());
        userinfoDef.setUpdateTime(LocalDateTime.now());
        litemallUserinfoDefMapper.insertSelective(userinfoDef);
    }

    public int updateById(LitemallUserinfoDef userinfoDef){
        userinfoDef.setUpdateTime(LocalDateTime.now());
        return litemallUserinfoDefMapper.updateByPrimaryKeySelective(userinfoDef);
    }
    public void deleteById(Integer id){
        litemallUserinfoDefMapper.logicalDeleteByPrimaryKey(id);
    }


    public LitemallUserinfoDef findById(Integer id){
        return litemallUserinfoDefMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByName(String title) {
        LitemallUserinfoDefExample example = new LitemallUserinfoDefExample();
        example.or().andTitleEqualTo(title).andDeletedEqualTo(false);
        return litemallUserinfoDefMapper.countByExample(example) != 0;
    }
    //获取用户消息列表
    public List<LitemallUserinfoDef> getUserInfoByUserId(Integer userId){
        //获取该用户可得到的消息通知列表
      List<Integer> infoIds=  userinfoPubService.getInfoIdsByUserId(userId);
      LitemallUserinfoDefExample example = new LitemallUserinfoDefExample();
      if(infoIds!=null&&infoIds.size()>0) {
          example.or().andIdIn(infoIds).andDeletedEqualTo(false).andExpireFlagEqualTo(false).andTypeNameNotEqualTo("系统消息");
      } else
      {
          return null;
      }
      //倒序
      example.setOrderByClause("add_time desc");
      return litemallUserinfoDefMapper.selectByExample(example);
    }

    /**
     * 查询状态正常，但是时间已经过期的消息公告,更新成已经过期
     *
     * @return
     */
    public List<LitemallUserinfoDef> queryHaveExpiredButStateError() {
        LitemallUserinfoDefExample example = new LitemallUserinfoDefExample();
        example.or().andExpireFlagEqualTo(false).andEndDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return litemallUserinfoDefMapper.selectByExample(example);
    }
}
