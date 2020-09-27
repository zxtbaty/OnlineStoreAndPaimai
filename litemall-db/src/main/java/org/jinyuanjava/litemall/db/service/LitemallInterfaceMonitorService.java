package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallInterfaceMonitorMapper;
import org.jinyuanjava.litemall.db.domain.LitemallInterfaceMonitor;
import org.jinyuanjava.litemall.db.domain.LitemallInterfaceMonitorExample;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallInterfaceMonitorService {
    @Resource
    private LitemallInterfaceMonitorMapper litemallInterfaceMonitorMapper;

    public List<LitemallInterfaceMonitor> querySelective(
            String logSourceCode,String logDirectionCode,String logTypeCode,String logContent,
            Integer errorFlag,String startDate,String endDate,
            Integer page,Integer limit,String sort){
        LitemallInterfaceMonitorExample example=new LitemallInterfaceMonitorExample();
        LitemallInterfaceMonitorExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(logSourceCode)){
            criteria.andLogSourceCodeEqualTo(logSourceCode);
        }
        if(!StringUtils.isEmpty(logDirectionCode)){
            criteria.andLogDirectionCodeEqualTo(logDirectionCode);
        }
        if(!StringUtils.isEmpty(logTypeCode)){
            criteria.andLogTypeCodeEqualTo(logTypeCode);
        }
        if(!StringUtils.isEmpty(logContent)){
            criteria.andLogContentLike("%"+logContent+"%");
        }
        if(!StringUtils.isEmpty(errorFlag)){
            if(errorFlag==0) {
                criteria.andErrorFlagEqualTo(false);
            } else  if(errorFlag==1) {
                criteria.andErrorFlagEqualTo(true);
            }
        }
        if(!StringUtils.isEmpty(startDate)){
            criteria.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(startDate));
        }
        if(!StringUtils.isEmpty(endDate)){
            criteria.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(endDate));
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallInterfaceMonitorMapper.selectByExample(example);
    }

    public void add(LitemallInterfaceMonitor userinfoDef){
        userinfoDef.setAddTime(LocalDateTime.now());
        userinfoDef.setUpdateTime(LocalDateTime.now());
        litemallInterfaceMonitorMapper.insertSelective(userinfoDef);
    }

    public int updateById(LitemallInterfaceMonitor userinfoDef){
        userinfoDef.setUpdateTime(LocalDateTime.now());
        return litemallInterfaceMonitorMapper.updateByPrimaryKeySelective(userinfoDef);
    }
    public void deleteById(Integer id){
        litemallInterfaceMonitorMapper.logicalDeleteByPrimaryKey(id);
    }


    public LitemallInterfaceMonitor findById(Integer id){
        return litemallInterfaceMonitorMapper.selectByPrimaryKey(id);
    }

}
