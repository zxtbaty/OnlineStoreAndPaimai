package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallHuodongDetailPicLinkMapper;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailPicLink;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailPicLinkExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallHuodongDetailPicLinkService {
    @Resource
    private LitemallHuodongDetailPicLinkMapper detailPicLinkMapper;

    //根据id查询
    public  LitemallHuodongDetailPicLink  queryById(Integer uid) {
        LitemallHuodongDetailPicLinkExample example = new LitemallHuodongDetailPicLinkExample();
        example.or().andIdEqualTo(uid);
        return detailPicLinkMapper.selectOneByExample(example);
    }


    //根据id查询
    public List<LitemallHuodongDetailPicLink> queryByMainId(Integer mainId) {
        LitemallHuodongDetailPicLinkExample example = new LitemallHuodongDetailPicLinkExample();
        example.or().andMainIdEqualTo(mainId).andDeletedEqualTo(false);
        return detailPicLinkMapper.selectByExample(example);
    }


    public int add(LitemallHuodongDetailPicLink HuodongMain) {
        HuodongMain.setAddTime(LocalDateTime.now());
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return detailPicLinkMapper.insertSelective(HuodongMain);
    }

    public int update(LitemallHuodongDetailPicLink HuodongMain) {
        HuodongMain.setUpdateTime(LocalDateTime.now());
        return detailPicLinkMapper.updateByPrimaryKeySelective(HuodongMain);
    }

    public void delete(Integer id) {
        detailPicLinkMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByMainId(Integer mainId) {
        LitemallHuodongDetailPicLinkExample example = new LitemallHuodongDetailPicLinkExample();
        LitemallHuodongDetailPicLinkExample.Criteria criteria = example.createCriteria();
        criteria.andMainIdEqualTo(mainId);
        detailPicLinkMapper.deleteByExample(example);
    }


    public List<LitemallHuodongDetailPicLink> querySelective(Integer mainId,
                        Integer page, Integer limit, String sort) {
        LitemallHuodongDetailPicLinkExample example = new LitemallHuodongDetailPicLinkExample();
        LitemallHuodongDetailPicLinkExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(mainId)){
        	criteria.andMainIdEqualTo(mainId);
        }
//        if (!StringUtils.isEmpty(DetailPicLink.getGoodsName())) {
//            criteria.andGoodsNameLike("%" + DetailPicLink.getGoodsName() + "%");
//        }

        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return detailPicLinkMapper.selectByExample(example);
    }

}
