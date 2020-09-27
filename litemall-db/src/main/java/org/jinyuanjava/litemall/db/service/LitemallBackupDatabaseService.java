package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallBackupDatabaseMapper;
import org.jinyuanjava.litemall.db.domain.LitemallBackupDatabase;
import org.jinyuanjava.litemall.db.domain.LitemallBackupDatabaseExample;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallBackupDatabaseService {
    @Resource
    private LitemallBackupDatabaseMapper updateVersion;

    public LitemallBackupDatabase findById(Integer id) {
        return updateVersion.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        updateVersion.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallBackupDatabase footprint) {
        footprint.setAddTime(LocalDateTime.now());
        footprint.setUpdateTime(LocalDateTime.now());
        updateVersion.insertSelective(footprint);
    }
    public void update(LitemallBackupDatabase footprint) {
        footprint.setUpdateTime(LocalDateTime.now());
        updateVersion.updateByPrimaryKeySelective(footprint);
    }


    public List<LitemallBackupDatabase> querySelective(String fileName,
        String backupBeginDate, String backupEndDate,
        Integer page, Integer size) {
        LitemallBackupDatabaseExample example = new LitemallBackupDatabaseExample();
        LitemallBackupDatabaseExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(fileName)) {
            criteria.andFileNameLike("%"+fileName+"%");
        }
        if (!StringUtils.isEmpty(backupBeginDate)) {
            criteria.andBackupDateGreaterThanOrEqualTo(DateUtil.getLocalShortDateFromString(backupBeginDate));
        }
        if (!StringUtils.isEmpty(backupEndDate)) {
            criteria.andBackupDateLessThanOrEqualTo(DateUtil.getLocalShortDateFromString(backupEndDate));
        }

        criteria.andDeletedEqualTo(false);
        example.setOrderByClause(" backup_date desc,backup_version desc");
        PageHelper.startPage(page, size);
        return updateVersion.selectByExample(example);
    }

}
