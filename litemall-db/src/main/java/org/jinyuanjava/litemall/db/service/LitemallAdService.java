package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAdMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAd;
import org.jinyuanjava.litemall.db.domain.LitemallAdExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAdService {
    @Resource
    private LitemallAdMapper adMapper;

    public List<LitemallAd> queryIndex() {
        LitemallAdExample example = new LitemallAdExample();
        example.or().andPositionEqualTo((byte) 1).andDeletedEqualTo(false).andEnabledEqualTo(true);
        example.orderBy("ordernumber asc");
        return adMapper.selectByExample(example);
    }

    public List<LitemallAd> querySelective(String name, String content, Integer page, Integer limit, String sort) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(content)) {
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return adMapper.selectByExample(example);
    }

    public int updateById(LitemallAd ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return adMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        adMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallAd ad) {
        ad.setAddTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        adMapper.insertSelective(ad);
    }

    public LitemallAd findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }
}
