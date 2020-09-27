package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.util.StringUtil;
import org.jinyuanjava.litemall.db.dao.LitemallMenuMapper;
import org.jinyuanjava.litemall.db.dao.LitemallRoleMenuMapper;
import org.jinyuanjava.litemall.db.dao.ViewRoleMenuMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LitemallRoleMenuService {
    @Resource
    private LitemallRoleMenuMapper rolemenuMapper;

    @Resource
    private LitemallMenuMapper menuMapper;

    @Resource
    private ViewRoleMenuMapper viewRoleMenuMapper;


    public Set<Integer> queryByRoleId(Integer roleId) {
        Set<Integer> menus = new HashSet<Integer>();
        if(roleId == null){
            return menus;
        }
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        List<LitemallRoleMenu> menuList = rolemenuMapper.selectByExample(example);
        menus=menuList.stream().map(LitemallRoleMenu::getMenuId).distinct().collect(Collectors.toSet());

//        for(LitemallRoleMenu roleMenu : menuList){
//            menus.add(roleMenu.getMenuId());
//        }
        return menus;
    }

    public Set<String> queryCodeByRoleId(Integer roleId) {
        Set<String> menus = new HashSet<String>();
        if(roleId == null){
            return menus;
        }
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        List<LitemallRoleMenu> menuList = rolemenuMapper.selectByExample(example);
        menus=menuList.stream().map(LitemallRoleMenu::getMenuCode).distinct().collect(Collectors.toSet());

//        for(LitemallRoleMenu roleMenu : menuList){
//            menus.add(roleMenu.getMenuCode());
//        }
        return menus;
    }

    public Set<String> queryCodeByRoleId(List<Integer> roleIds) {
        Set<String> menus = new HashSet<String>();
        if(roleIds == null){
            return menus;
        }
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdIn(roleIds).andDeletedEqualTo(false);
        List<LitemallRoleMenu> menuList = rolemenuMapper.selectByExample(example);
        menus=menuList.stream().map(LitemallRoleMenu::getMenuCode).distinct().collect(Collectors.toSet());


        return menus;
    }


    public List<LitemallMenu> getLiemallMenuListByRoleId(Integer roleId) {
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        List<LitemallRoleMenu> roleMenus = rolemenuMapper.selectByExample(example);

        List<Integer> menuIds=roleMenus.stream().map(LitemallRoleMenu::getMenuId).collect(Collectors.toList());
        if(menuIds==null||menuIds.size()==0){
            return null;
        } else
        {
            LitemallMenuExample menuExample = new LitemallMenuExample();
            example.or().andIdIn(menuIds).andDeletedEqualTo(false);
            List<LitemallMenu> menuList = menuMapper.selectByExample(menuExample);
            return  menuList;
        }
    }

    public LitemallRoleMenu findById(Integer id) {
        return rolemenuMapper.selectByPrimaryKey(id);
    }

    public void add(LitemallRoleMenu roleMenu) {
        roleMenu.setAddTime(LocalDateTime.now());
        roleMenu.setUpdateTime(LocalDateTime.now());
        rolemenuMapper.insertSelective(roleMenu);
    }

    public void deleteById(Integer id) {
        rolemenuMapper.logicalDeleteByPrimaryKey(id);
    }


    public void deleteByRoleId(Integer roleId) {
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        rolemenuMapper.deleteByExample(example);
        //rolemenuMapper.logicalDeleteByExample(example);
    }

    public void updateById(LitemallRoleMenu roleMenu) {
        roleMenu.setUpdateTime(LocalDateTime.now());
        rolemenuMapper.updateByPrimaryKeySelective(roleMenu);
    }

    public boolean checkRoleMenus(Integer roleId) {
        if(roleId == null){
            return false;
        }
        LitemallRoleMenuExample example = new LitemallRoleMenuExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        return rolemenuMapper.countByExample(example) != 0;
    }


    /**
     * 获取用户一级菜单权限
     *
     */
    public Set<String> getMenuRoleFirstLevel(List<Integer> roleIds) {
        ViewRoleMenuExample example=new ViewRoleMenuExample();
        ViewRoleMenuExample.Criteria criteria=example.createCriteria();
        if(roleIds!=null&&roleIds.size()>0){
            criteria.andRoleIdIn(roleIds);
        }
        criteria.andDeletedEqualTo(false);
        criteria.andLevelEqualTo(1);
        List<ViewRoleMenu> result=viewRoleMenuMapper.selectByExample(example);
        Set<String> setResult=result.stream().map(ViewRoleMenu::getMenuCode).collect(Collectors.toSet());

        return setResult;
    }

    /**
     * 获取用户一级菜单权限
     *
     */

    public  Set<String> getMenuRoleSecondLevel(List<Integer> roleIds,List<String> roleNames,String parentCode) {
        ViewRoleMenuExample example=new ViewRoleMenuExample();
        ViewRoleMenuExample.Criteria criteria=example.createCriteria();
        if(roleIds!=null&&roleIds.size()>0){
            criteria.andRoleIdIn(roleIds);
        }
        if(roleNames!=null&&roleNames.size()>0){
            criteria.andRoleNameIn(roleNames);
        }
        if(!StringUtil.isEmpty(parentCode)){
            criteria.andParentCodeEqualTo(parentCode);
        }
        criteria.andDeletedEqualTo(false);
        criteria.andLevelEqualTo(2);

        List<ViewRoleMenu> result=viewRoleMenuMapper.selectByExample(example);

        Set<String> setResult=result.stream().map(ViewRoleMenu::getMenuCode).collect(Collectors.toSet());

        return setResult;
    }

    public  List<ViewRoleMenu> getMenuRoleViewSecondLevel(List<Integer> roleIds,List<String> roleNames,String parentCode) {
        ViewRoleMenuExample example=new ViewRoleMenuExample();
        ViewRoleMenuExample.Criteria criteria=example.createCriteria();
        if(roleIds!=null&&roleIds.size()>0){
            criteria.andRoleIdIn(roleIds);
        }
        if(roleNames!=null&&roleNames.size()>0){
            criteria.andRoleNameIn(roleNames);
        }
        if(!StringUtil.isEmpty(parentCode)){
            criteria.andParentCodeEqualTo(parentCode);
        }
        criteria.andDeletedEqualTo(false);
        criteria.andLevelEqualTo(2);

        List<ViewRoleMenu> result=viewRoleMenuMapper.selectByExample(example);

        return result;
    }
}
