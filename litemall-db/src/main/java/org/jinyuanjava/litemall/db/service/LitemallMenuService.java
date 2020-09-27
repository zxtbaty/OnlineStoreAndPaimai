package org.jinyuanjava.litemall.db.service;

import org.springframework.util.StringUtils;
import org.jinyuanjava.litemall.db.dao.LitemallMenuMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LitemallMenuService {
    @Resource
    private LitemallMenuMapper menuMapper;


    public Set<String> queryByIds(Integer[] menuIds) {
        Set<String> menus = new HashSet<String>();
        if(menuIds.length == 0){
            return menus;
        }
        LitemallMenuExample example = new LitemallMenuExample();
        example.or().andIdIn(Arrays.asList(menuIds)).andIsShowEqualTo(true).andDeletedEqualTo(false);
        List<LitemallMenu> menuList = menuMapper.selectByExample(example);
        for(LitemallMenu menu: menuList){
            menus.add(menu.getName());
        }
        return menus;

    }

    public List<LitemallMenu> querySelective(Integer id, String name) {
        LitemallMenuExample example = new LitemallMenuExample();
        LitemallMenuExample.Criteria criteria = example.createCriteria();

        if (!org.springframework.util.StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!org.springframework.util.StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        example.setOrderByClause("level,sort,id");

        return menuMapper.selectByExample(example);

    }

    public LitemallMenu findById(Integer id) {
        return menuMapper.selectByPrimaryKey(id);
    }

    public void add(LitemallMenu role) {
        role.setAddTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        menuMapper.insertSelective(role);
    }

    public void deleteById(Integer id) {
        menuMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByIdList(List<Integer> idList) {
        LitemallMenuExample example = new LitemallMenuExample();
        example.or().andIdIn(idList);
        menuMapper.logicalDeleteByExample(example);
    }

    public void updateById(LitemallMenu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    public boolean checkExist(String code) {
        LitemallMenuExample example = new LitemallMenuExample();
        example.or().andCodeEqualTo(code).andDeletedEqualTo(false);
        return menuMapper.countByExample(example) != 0;
    }
    /**
     * 查询某菜单编码是否已经存在
     */
    public boolean checkExist(LitemallMenu litemallMenu) {
        LitemallMenuExample example = new LitemallMenuExample();
        LitemallMenuExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(litemallMenu.getCode())){
                criteria.andCodeEqualTo(litemallMenu.getCode());
        }
        Integer id=litemallMenu.getId();
        if(id!=null&&!StringUtils.isEmpty(id.toString())){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);
        return menuMapper.countByExample(example) != 0;
    }


    public List<LitemallMenu> queryAll() {
        LitemallMenuExample example = new LitemallMenuExample();
        example.or().andDeletedEqualTo(false);
        return menuMapper.selectByExample(example);
    }

    public LitemallMenu getHaveExistsMenu(LitemallMenu litemallMenu) {
        LitemallMenuExample example = new LitemallMenuExample();
        LitemallMenuExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(litemallMenu.getName())){
            criteria.andNameEqualTo(litemallMenu.getName());
        }
        if(!StringUtils.isEmpty(litemallMenu.getParentId())){
            criteria.andParentIdEqualTo(litemallMenu.getParentId());
        }
        Integer id=litemallMenu.getId();
        if(id!=null&&!StringUtils.isEmpty(id.toString())){
            criteria.andIdNotEqualTo(id);
        }
        criteria.andDeletedEqualTo(false);
        return menuMapper.selectOneByExample(example);
    }

    public  LitemallMenu  getMenuByPath(String path) {
        LitemallMenuExample example = new LitemallMenuExample();
        LitemallMenuExample.Criteria criteria = example.createCriteria();

        criteria.andPathEqualTo(path);
        criteria.andDeletedEqualTo(false);

        example.setOrderByClause("level,sort,id");

        return menuMapper.selectOneByExample(example);

    }

}
