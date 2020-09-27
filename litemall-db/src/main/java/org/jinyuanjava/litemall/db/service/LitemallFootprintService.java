package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallFootprintMapper;
import org.jinyuanjava.litemall.db.domain.LitemallFootprint;
import org.jinyuanjava.litemall.db.domain.LitemallFootprintExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallFootprintService {
    @Resource
    private LitemallFootprintMapper footprintMapper;

    public List<LitemallFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false).andGoodsIdNotEqualTo(0);
//      example.setOrderByClause(LitemallFootprint.Column.addTime.desc());
        example.setOrderByClause(LitemallFootprint.Column.updateTime.desc());
        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public LitemallFootprint findById(Integer id) {
        return footprintMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        footprintMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallFootprint footprint) {
        footprint.setAddTime(LocalDateTime.now());
        footprint.setUpdateTime(LocalDateTime.now());
        footprintMapper.insertSelective(footprint);
    }
    public void update(LitemallFootprint footprint) {
        footprint.setUpdateTime(LocalDateTime.now());
        footprintMapper.updateByPrimaryKeySelective(footprint);
    }


    public List<LitemallFootprint> querySelective(String userId, String goodsId,
                                                  String wxNickname, String weixinOpenid,
                                                  String crmId, String goodsSn, String goodsName, Integer page, Integer size, String sort) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        LitemallFootprintExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(wxNickname)) {
            criteria.andWxNicknameLike("%"+wxNickname+"%");
        }
        if (!StringUtils.isEmpty(weixinOpenid)) {
            criteria.andWeixinOpenidEqualTo(weixinOpenid);
        }
        if (!StringUtils.isEmpty(crmId)) {
            criteria.andCrmIdEqualTo(crmId);
        }
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if (!StringUtils.isEmpty(goodsName)) {
            criteria.andGoodsNameLike("%"+goodsName+"%");
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false).andGoodsIdNotEqualTo(0);;

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        return footprintMapper.selectByExample(example);
    }

	public LitemallFootprint queryByUserIdAndGoodId(Integer userId,
			Integer id) {
		 LitemallFootprintExample example = new LitemallFootprintExample();
	        example.or().andUserIdEqualTo(userId).andGoodsIdEqualTo(id).andDeletedEqualTo(false).andGoodsIdNotEqualTo(0);;
	        example.setOrderByClause(LitemallFootprint.Column.updateTime.desc());
		return footprintMapper.selectOneByExample(example);
	}
}
