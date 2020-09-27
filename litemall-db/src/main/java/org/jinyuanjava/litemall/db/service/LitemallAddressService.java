package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallAddressMapper;
import org.jinyuanjava.litemall.db.dao.LitemallRegionMapper;
import org.jinyuanjava.litemall.db.domain.LitemallAddress;
import org.jinyuanjava.litemall.db.domain.LitemallAddressExample;
import org.jinyuanjava.litemall.db.domain.LitemallRegion;
import org.jinyuanjava.litemall.db.domain.LitemallRegionExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAddressService {
    @Resource
    private LitemallAddressMapper addressMapper;

    @Resource
    private LitemallRegionMapper regionMapper;

    public List<LitemallAddress> queryByUid(Integer uid) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        return addressMapper.selectByExample(example);
    }

    public LitemallAddress query(Integer userId, Integer id) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andIdEqualTo(id).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return addressMapper.selectOneByExample(example);
    }

    public LitemallAddress query(Integer id) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andIdEqualTo(id);
        return addressMapper.selectOneByExample(example);
    }

    public int add(LitemallAddress address) {
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.insertSelective(address);
    }

    public int update(LitemallAddress address) {
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.updateByPrimaryKeySelective(address);
    }

    public void delete(Integer id) {
        addressMapper.logicalDeleteByPrimaryKey(id);
    }

    public LitemallAddress findDefault(Integer userId) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return addressMapper.selectOneByExample(example);
    }

    public void resetDefault(Integer userId) {
        LitemallAddress address = new LitemallAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        addressMapper.updateByExampleSelective(address, example);
    }

    public List<LitemallAddress> querySelective(Integer userId, String name,
                                                String wxNickname,String tel,
                                                Integer page, Integer limit, String sort) {
        LitemallAddressExample example = new LitemallAddressExample();
        LitemallAddressExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(wxNickname)) {
            criteria.andWxNicknameLike("%" + wxNickname + "%");
        }
        if (!StringUtils.isEmpty(tel)) {
            criteria.andTelEqualTo( tel);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(limit<999999) {
            PageHelper.startPage(page, limit);
        }
        return addressMapper.selectByExample(example);
    }

	public List<LitemallRegion> queryRegionByPidAndType(Integer pid, Integer type) {

		 LitemallRegionExample example = new LitemallRegionExample();
	        example.or().andTypeEqualTo(type.byteValue()).andPidEqualTo(pid);

	        return regionMapper.selectByExample(example);

	}

	public LitemallRegion getById(Integer province) {

		return regionMapper.selectByPrimaryKey(province);
	}
}
