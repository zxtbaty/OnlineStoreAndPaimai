package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallUpdateVersionMapper;
import org.jinyuanjava.litemall.db.domain.LitemallUpdateVersion;
import org.jinyuanjava.litemall.db.domain.LitemallUpdateVersionExample;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LitemallUpdateVersionService {
    @Resource
    private LitemallUpdateVersionMapper updateVersion;

    @Autowired
    private CommonDBService commonDBService;

    public LitemallUpdateVersion findById(Integer id) {
        return updateVersion.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        updateVersion.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallUpdateVersion footprint) {
        footprint.setAddTime(LocalDateTime.now());
        footprint.setUpdateTime(LocalDateTime.now());
        updateVersion.insertSelective(footprint);
    }
    public void update(LitemallUpdateVersion footprint) {
        footprint.setUpdateTime(LocalDateTime.now());
        updateVersion.updateByPrimaryKeySelective(footprint);
    }
    public Integer queryMaxVersionByAddDate(String versionType,LocalDate currentDate){
        String strSql="select max(file_version) as fileVersion from litemall_update_version where deleted=0 and update_date='"+
                currentDate+"' and version_type='"+versionType+"'";
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);

        List<Map> result= commonDBService.procedureDaoList(param);
        Map m=result.get(0);
        if(m==null){
            return 0;
        } else
        {
            Object value= m.get("fileVersion");
            if(value==null){
                return 0;
            } else {
                Integer maxInt = Integer.parseInt(value.toString());
                return maxInt;
            }
        }
    }

    public List<LitemallUpdateVersion> querySelective(String versionType,String sourceFileName,
        String updateBeginDate, String updateEndDate,Integer ifBushu, Integer ifCurrent,
        Integer page, Integer size) {
        LitemallUpdateVersionExample example = new LitemallUpdateVersionExample();
        LitemallUpdateVersionExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(versionType)) {
            criteria.andVersionTypeEqualTo(versionType);
        }
        if (!StringUtils.isEmpty(sourceFileName)) {
            criteria.andSourceFileNameLike("%"+sourceFileName+"%");
        }
        if (!StringUtils.isEmpty(updateBeginDate)) {
            criteria.andUpdateDateGreaterThanOrEqualTo(DateUtil.getLocalShortDateFromString(updateBeginDate) );
        }
        if (!StringUtils.isEmpty(updateEndDate)) {
            criteria.andUpdateDateLessThanOrEqualTo(DateUtil.getLocalShortDateFromString(updateEndDate));
        }
        if (!StringUtils.isEmpty(ifBushu)) {
            if(ifBushu==0) {
                criteria.andIfBushuEqualTo(false);
            } else if(ifBushu==1) {
                criteria.andIfBushuEqualTo(true);
            }
        }
        if (!StringUtils.isEmpty(ifCurrent)) {
            if(ifCurrent==0) {
                criteria.andIfCurrentEqualTo(false);
            } else if(ifCurrent==1) {
                criteria.andIfCurrentEqualTo(true);
            }
        }
        criteria.andDeletedEqualTo(false);
        example.setOrderByClause(" update_date desc,file_version desc");
        PageHelper.startPage(page, size);
        return updateVersion.selectByExample(example);
    }

}
