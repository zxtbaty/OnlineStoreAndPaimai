package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAdminMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin.Column;
import org.jinyuanjava.litemall.db.domain.LitemallAdminExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAdminService {
    private final Column[] result = new Column[]{Column.id, Column.username, Column.avatar, Column.roleIds,Column.ifModifyOnSale};
    @Resource
    private LitemallAdminMapper adminMapper;

    public List<LitemallAdmin> findAdmin(String username) {
        LitemallAdminExample example = new LitemallAdminExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    public LitemallAdmin findAdmin(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    public List<LitemallAdmin> querySelective(String username, Integer page, Integer limit, String sort) {
        LitemallAdminExample example = new LitemallAdminExample();
        LitemallAdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example, result);
    }

    public int updateById(LitemallAdmin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallAdmin admin) {
        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public LitemallAdmin findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }

    public List<LitemallAdmin> all() {
        LitemallAdminExample example = new LitemallAdminExample();
        example.or().andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }
}
