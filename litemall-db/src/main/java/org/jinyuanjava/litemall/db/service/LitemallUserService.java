package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;

import org.jinyuanjava.litemall.db.dao.LitemallUserMapper;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.domain.LitemallUserExample;
import org.jinyuanjava.litemall.db.domain.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class LitemallUserService {
    @Resource
    private LitemallUserMapper userMapper;

    public LitemallUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserVo findUserVoById(Integer userId) {
        LitemallUser user = findById(userId);
        if(user==null){
            return null;
        }
        UserVo userVo = new UserVo();
        if(user.getNickname()!=null) {
            userVo.setNickname(user.getNickname());
        }
        if(user.getAvatar()!=null) {
            userVo.setAvatar(user.getAvatar());
        }

        return userVo;
    }

    public LitemallUser queryByOid(String openId) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public  LitemallUser queryOneByOpenid(String sourceId,String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andSourceIdEqualTo(sourceId).andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public  List<LitemallUser> queryByOpenid(String sourceId,String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andSourceIdEqualTo(sourceId).andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public  LitemallUser  queryByOneOpenid(String sourceId,String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andSourceIdEqualTo(sourceId).andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public  LitemallUser queryByOpenid(String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(LitemallUser user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(LitemallUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<LitemallUser> querySelective(String username,String nickname, String mobile,String userClassAttr1Code,String userClassAttr2Code, Integer page, Integer size, String sort) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(nickname)) {
            criteria.andNicknameLike("%" + nickname + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileLike("%" + mobile + "%");
        }
        if (!StringUtils.isEmpty(userClassAttr1Code)) {
            criteria.andUserClassAttr1CodeEqualTo(userClassAttr1Code);
        }
        if (!StringUtils.isEmpty(userClassAttr2Code)) {
            criteria.andUserClassAttr2CodeEqualTo(userClassAttr2Code);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        //如果Size设置成999999，则是导出全部Excel用，不再分页
        if(size<999999) {
            PageHelper.startPage(page, size);
        }
        return userMapper.selectByExample(example);
    }

    public int count() {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public int countToday() {

        LocalDateTime localDateTimeBegin=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime localDateTimeEnd=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false).andAddTimeGreaterThanOrEqualTo(localDateTimeBegin).andAddTimeLessThanOrEqualTo(localDateTimeEnd);

        return (int) userMapper.countByExample(example);
    }


    public List<LitemallUser> queryByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<LitemallUser> queryByMobile(String mobile) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }



    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }
    public LitemallUser queryByUserId(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}
	public boolean checkUserIfForbiddenByTelphone(String mobile){
        LitemallUserExample example=new LitemallUserExample();
        LitemallUserExample.Criteria criteria=example.createCriteria();
        criteria.andMobileEqualTo(mobile).andIfBlacklistEqualTo(true).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public LitemallUser selectUserByInfo(String sourceId,String crmId,String comId,String weixinOpenid,String userName){
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(sourceId)) {
            criteria.andSourceIdEqualTo(sourceId);
        }
        if (!StringUtils.isEmpty(crmId)) {
            criteria.andCrmIdEqualTo(crmId);
        }
        if (!StringUtils.isEmpty(comId)) {
            criteria.andComIdEqualTo(Integer.parseInt(comId));
        }
        if (!StringUtils.isEmpty(weixinOpenid)) {
            criteria.andWeixinOpenidEqualTo(weixinOpenid);
        }
        if (!StringUtils.isEmpty(userName)) {
            criteria.andUsernameEqualTo(userName);
        }
        criteria.andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public List<LitemallUser> userList(){
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }



}
