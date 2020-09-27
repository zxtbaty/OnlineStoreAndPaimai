package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallFapiaoInfoMapper;
import org.jinyuanjava.litemall.db.domain.LitemallFapiaoInfo;
import org.jinyuanjava.litemall.db.domain.LitemallFapiaoInfoExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallFapiaoInfoService {
    @Resource
    private LitemallFapiaoInfoMapper fapiaoInfoMapper;

    public List<LitemallFapiaoInfo> queryByUid(Integer uid) {
        LitemallFapiaoInfoExample example = new LitemallFapiaoInfoExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        return fapiaoInfoMapper.selectByExample(example);
    }

    public int add(LitemallFapiaoInfo fapiaoInfo) {
        fapiaoInfo.setAddTime(LocalDateTime.now());
        fapiaoInfo.setUpdateTime(LocalDateTime.now());
        return fapiaoInfoMapper.insertSelective(fapiaoInfo);
    }

    public int update(LitemallFapiaoInfo fapiaoInfo) {
        fapiaoInfo.setUpdateTime(LocalDateTime.now());
        return fapiaoInfoMapper.updateByPrimaryKeySelective(fapiaoInfo);
    }

    public void delete(Integer id) {
        fapiaoInfoMapper.logicalDeleteByPrimaryKey(id);
    }

    public LitemallFapiaoInfo findDefault(Integer userId) {
        LitemallFapiaoInfoExample example = new LitemallFapiaoInfoExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return fapiaoInfoMapper.selectOneByExample(example);
    }

    public void resetDefault(Integer userId) {
        LitemallFapiaoInfo fapiaoInfo = new LitemallFapiaoInfo();
        fapiaoInfo.setIsDefault(false);
        fapiaoInfo.setUpdateTime(LocalDateTime.now());
        LitemallFapiaoInfoExample example = new LitemallFapiaoInfoExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        fapiaoInfoMapper.updateByExampleSelective(fapiaoInfo, example);
    }

    public List<LitemallFapiaoInfo> querySelective(Integer userId, String name, Integer page, Integer limit, String sort) {
        LitemallFapiaoInfoExample example = new LitemallFapiaoInfoExample();
        LitemallFapiaoInfoExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return fapiaoInfoMapper.selectByExample(example);
    }

}
