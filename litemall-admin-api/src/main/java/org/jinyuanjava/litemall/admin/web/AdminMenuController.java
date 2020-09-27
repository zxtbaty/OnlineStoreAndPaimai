package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.util.AdminResponseCode;
import org.jinyuanjava.litemall.admin.util.GetCurrentUser;
import org.jinyuanjava.litemall.admin.vo.MenuClassVo;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallMenu;
import org.jinyuanjava.litemall.db.domain.LitemallRole;
import org.jinyuanjava.litemall.db.domain.LitemallRoleMenu;
import org.jinyuanjava.litemall.db.service.LitemallMenuService;
import org.jinyuanjava.litemall.db.service.LitemallRoleMenuService;
import org.jinyuanjava.litemall.db.service.LitemallRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/menu")
@Validated
@Api(description = "后台管理/系统管理/角色管理:/admin/menu")
public class AdminMenuController {
    private final Log logger = LogFactory.getLog(AdminMenuController.class);

    @Autowired
    private LitemallMenuService menuService;

    @Autowired
    private LitemallRoleMenuService rolemenuService;

    @Autowired
    private LitemallRoleService roleService;



    @RequiresPermissions("admin:menu:list")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单查询")
    @GetMapping("/list")
    public Object list(Integer id, String name) {
        return ResponseUtil.okList(getMenuList(id,name));
    }



    private MenuClassVo recursiveTree(MenuClassVo parent, List<MenuClassVo> list) {
        for (MenuClassVo menuClassVo : list) {
            if(parent.getId().equals(menuClassVo.getParentId()) ) {
                menuClassVo = recursiveTree(menuClassVo, list);
                if(parent.getChildren()==null){
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(menuClassVo);
            }
        }
        return parent;
    }

    /**
     * 获取所有的菜单功能
     * @return
     */
    @GetMapping("/options")
    public Object options(){
        List<LitemallMenu> menuList = menuService.queryAll();
        List<Map<String, Object>> options = new ArrayList<>(menuList.size());
        for (LitemallMenu menu : menuList) {
            Map<String, Object> option = new HashMap<>(2);
            option.put("value", menu.getId());
            option.put("label", menu.getName());
            options.add(option);
        }
        return ResponseUtil.okList(options);
    }

    @RequiresPermissions("admin:menu:read")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallMenu menu = menuService.findById(id);
        return ResponseUtil.ok(menu);
    }

    @GetMapping("/getMenuByPath")
    public Object getMenuByPath(String path) {
        LitemallMenu menu = menuService.getMenuByPath(path);
        return ResponseUtil.ok(menu);
    }


    private Object validate(LitemallMenu menu) {
        String code = menu.getCode();
        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.fail(502,"菜单编码不能为空");
        }
        String name = menu.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(502,"菜单名称不能为空");
        }

        Boolean ifExists= menuService.checkExist(menu);
        if(ifExists){
            return ResponseUtil.fail(502,"菜单编码已经存在");
        }

        return null;
    }

    @RequiresPermissions("admin:menu:create")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallMenu menu) {
        Object error = validate(menu);
        if (error != null) {
            return error;
        }
        if (menuService.checkExist(menu.getName())){
            return ResponseUtil.fail(AdminResponseCode.ROLE_NAME_EXIST, "菜单编码已经存在");
        }
        if(menu.getParentId()==0){
            menu.setLevel(1);
        } else
        {
            menu.setLevel(2);
        }
        if(menu.getPath()==null){
            //当前路径为上一级路径+本级路径
            Integer parentMenuId=  menu.getParentId();
            if(parentMenuId==null){
                menu.setPath(menu.getHref());
            } else
            {
                LitemallMenu parentMenu=  menuService.findById(parentMenuId);
                menu.setPath("/"+parentMenu.getHref()+"/"+menu.getHref());
            }
        }
        menuService.add(menu);

        return ResponseUtil.ok(menu);
    }

    @PostMapping("/batchAddMenu")
    public Object batchAddMenu(@RequestBody LitemallMenu menu) {
        String code = menu.getCode();
        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.fail(502,"菜单编码不能为空");
        }
        String name = menu.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(502,"菜单名称不能为空");
        }

        LitemallMenu haveExistsMenu= menuService.getHaveExistsMenu(menu);
        if (haveExistsMenu != null) {

            haveExistsMenu.setUserModifyId(GetCurrentUser.getCurrentUserIdInt());
            haveExistsMenu.setUpdateTime(LocalDateTime.now());
            haveExistsMenu.setCode(menu.getCode());
            haveExistsMenu.setHref(menu.getHref());
            haveExistsMenu.setPath(menu.getPath());
            haveExistsMenu.setIcon(menu.getIcon());
            haveExistsMenu.setIsShow(menu.getIsShow());
            haveExistsMenu.setLevel(menu.getLevel());
            haveExistsMenu.setParentId(menu.getParentId());
            haveExistsMenu.setRemarks(menu.getRemarks());
            haveExistsMenu.setSort(menu.getSort());
            menuService.updateById(haveExistsMenu);
            return ResponseUtil.ok(haveExistsMenu);
        } else
        {
            menuService.add(menu);
            return ResponseUtil.ok(menu);
        }

    }

    @RequiresPermissions("admin:menu:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallMenu menu) {
        Object error = validate(menu);
        if (error != null) {
            return error;
        }
        if(menu.getParentId()==0){
            menu.setLevel(1);
        } else
        {
            menu.setLevel(2);
        }
        menuService.updateById(menu);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:role:delete")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallMenu menu) {
        Integer id = menu.getId();
        if (id == null) {
            return ResponseUtil.fail(502,"是删除的菜单项不能为空");
        }

        menuService.deleteById(id);
        return ResponseUtil.ok();
    }
    @RequiresPermissions("admin:role:deletelist")
    @RequiresPermissionsDesc(menu={"系统管理" , "菜单管理"}, button="菜单删除列表")
    @PostMapping("/deletelist")
    public Object deletelist(@RequestBody List<Integer> menuIds) {

        if (menuIds== null||menuIds.size()==0) {
            return ResponseUtil.fail(502,"是删除的菜单项不能为空");
        }

        menuService.deleteByIdList(menuIds);
        return ResponseUtil.ok();
    }


    /**
     * 管理员的菜单权限情况
     *
     * @return 系统所有权限列表和管理员已分配权限
     */
    @RequiresPermissions("admin:role:menu:get")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色菜单管理"}, button="菜单权限详情")
    @GetMapping("/getmenurole")
    public Object getmenurole(Integer roleId) {
        List<MenuClassVo> menuClassVoList = getMenuList(null,null);
        Set<Integer> assignedMenus = getAssignedMenus(roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("systemMenus", menuClassVoList);
        data.put("assignedMenus", assignedMenus);
        return ResponseUtil.ok(data);
    }


    /**
     * 更新管理员的权限
     *
     * @param body
     * @return
     */
    @RequiresPermissions("admin:role:menu:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色菜单管理"}, button="权限变更")
    @PostMapping("/updatemenurole")
    public Object updatemenurole(@RequestBody String body) {
        Integer roleId = JacksonUtil.parseInteger(body, "roleId");
        List<Integer> menusList = JacksonUtil.parseIntegerList(body, "menus");
        if(roleId == null ){
            return ResponseUtil.fail(502,"请选择要设置的角色");
        }

        if(menusList == null){
            return ResponseUtil.fail(502,"请选择要角色对应的资源权限");
        }
        // 如果修改的角色是超级权限，则拒绝修改。
        LitemallRole findRole= roleService.findById(roleId);
        if(findRole!=null&&findRole.getName().equals("超级管理员")){
            return ResponseUtil.fail(AdminResponseCode.ROLE_SUPER_SUPERMISSION, "当前角色的超级权限不能变更");
        }
        // 先删除旧的权限，再更新新的权限
        rolemenuService.deleteByRoleId(roleId);
        for(Integer menuId : menusList){
            LitemallRoleMenu litemallRoleMenu = new LitemallRoleMenu();
            litemallRoleMenu.setRoleId(roleId);
            LitemallMenu litemallMenu=menuService.findById(menuId);
            litemallRoleMenu.setMenuId(menuId);
            if(litemallMenu!=null){
                litemallRoleMenu.setMenuCode(litemallMenu.getCode());
            }
            rolemenuService.add(litemallRoleMenu);
        }
        return ResponseUtil.ok();
    }

    /**
     * 获取用户一级菜单权限
     *
     */
    @RequiresPermissions("admin:role:menu:getMenuFirstLevel")
    @RequiresPermissionsDesc(menu={"系统管理" , "一级菜单权限查询"}, button="一级菜单权限")
    @GetMapping("/getMenuFirstLevel")
    public Object getMenuFirstLevel(List<Integer> roleIds) {

        Set<String> setResult=rolemenuService.getMenuRoleFirstLevel(roleIds);

        return ResponseUtil.ok(setResult);
    }

    /**
     * 获取用户一级菜单权限
     *
     */
    @RequiresPermissions("admin:role:menu:getMenuSecondLevel")
    @RequiresPermissionsDesc(menu={"系统管理" , "二级菜单权限查询"}, button="二级菜单权限")
    @GetMapping("/getMenuSecondLevel")
    public Object getMenuSecondLevel(List<String> roleNames,String parentCode) {
        Set<String> setResult=rolemenuService.getMenuRoleSecondLevel(null,roleNames,parentCode);
        return ResponseUtil.ok(setResult);
    }

    private  List<MenuClassVo> getMenuList(Integer id, String name)
    {
        List<MenuClassVo> menuClassVoList = new ArrayList<>();
        List<LitemallMenu> menuList=menuService.querySelective(id,name);
        List<MenuClassVo> result = new ArrayList<>();
        for(LitemallMenu litemallMenu:menuList){
            MenuClassVo temp=new MenuClassVo();
            BeanUtils.copyProperties(litemallMenu,temp);
            menuClassVoList.add(temp);
        }
        // 1、获取第一级节点
        for (MenuClassVo menuClass : menuClassVoList) {
            if(0 == menuClass.getParentId()) {
                result.add(menuClass);
            }
        }
        // 2、递归获取子节点
        for (MenuClassVo parent : result) {
            parent = recursiveTree(parent, menuClassVoList);
        }
        return result;

    }

    private Set<Integer> getAssignedMenus(Integer roleId){
        Set<Integer> assignedMenus = null;
        if(rolemenuService.checkRoleMenus(roleId)){
            assignedMenus = rolemenuService.queryByRoleId(roleId);
        }
        else{
//            List<LitemallMenu> menuList=menuService.querySelective(null,null);
//            assignedMenus=menuList.stream().map(LitemallMenu::getId).collect(Collectors.toSet());
            return null;
        }

        return assignedMenus;
    }
}
