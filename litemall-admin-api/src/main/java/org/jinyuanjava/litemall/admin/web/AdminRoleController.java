package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.util.AdminResponseCode;
import org.jinyuanjava.litemall.admin.vo.PermVo;
import org.jinyuanjava.litemall.admin.util.Permission;
import org.jinyuanjava.litemall.admin.util.PermissionUtil;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin;
import org.jinyuanjava.litemall.db.domain.LitemallPermission;
import org.jinyuanjava.litemall.db.domain.LitemallRole;
import org.jinyuanjava.litemall.db.service.LitemallAdminService;
import org.jinyuanjava.litemall.db.service.LitemallPermissionService;
import org.jinyuanjava.litemall.db.service.LitemallRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.ROLE_NAME_EXIST;
import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.ROLE_USER_EXIST;

@RestController
@RequestMapping("/admin/role")
@Validated
@Api(description = "后台管理/系统管理/角色管理:/admin/region")
public class AdminRoleController {
    private final Log logger = LogFactory.getLog(AdminRoleController.class);

    @Autowired
    private LitemallRoleService roleService;
    @Autowired
    private LitemallPermissionService permissionService;
    @Autowired
    private LitemallAdminService adminService;

    @RequiresPermissions("admin:role:list")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="角色查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallRole> roleList = roleService.querySelective(name, page, limit, sort);
        return ResponseUtil.okList(roleList);
    }

    @GetMapping("/options")
    public Object options(){
        List<LitemallRole> roleList = roleService.queryAll();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        for (LitemallRole role : roleList) {
            Map<String, Object> option = new HashMap<>(2);
            option.put("value", role.getId());
            option.put("label", role.getName());
            options.add(option);
        }

        return ResponseUtil.okList(options);
    }

    @RequiresPermissions("admin:role:read")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="角色详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallRole role = roleService.findById(id);
        return ResponseUtil.ok(role);
    }


    private Object validate(LitemallRole role) {
        String name = role.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:role:create")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="角色添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        if (roleService.checkExist(role.getName())){
            return ResponseUtil.fail(ROLE_NAME_EXIST, "角色已经存在");
        }

        roleService.add(role);

        return ResponseUtil.ok(role);
    }

    @RequiresPermissions("admin:role:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="角色编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        roleService.updateById(role);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:role:delete")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="角色删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallRole role) {
        Integer id = role.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        List<LitemallAdmin> adminList = adminService.all();
        for(LitemallAdmin admin : adminList){
            Integer[] roleIds = admin.getRoleIds();
            for(Integer roleId : roleIds){
                if(id.equals(roleId)){
                    return ResponseUtil.fail(ROLE_USER_EXIST, "当前角色存在管理员，不能删除");
                }
            }
        }

        roleService.deleteById(id);
        return ResponseUtil.ok();
    }


    @Autowired
    private ApplicationContext context;
    private List<PermVo> systemPermissions = null;
    private Set<String> systemPermissionsString = null;

    private List<PermVo> getSystemPermissions(){
        final String basicPackage = "org.jinyuanjava.litemall.admin";
        if(systemPermissions == null){
            List<Permission> permissions = PermissionUtil.listPermission(context, basicPackage);
            systemPermissions = PermissionUtil.listPermVo(permissions);
            systemPermissionsString = PermissionUtil.listPermissionString(permissions);
        }
        return systemPermissions;
    }

    private Set<String> getAssignedPermissions(Integer roleId){
        // 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        Set<String> assignedPermissions = null;
        if(permissionService.checkSuperPermission(roleId)){
            getSystemPermissions();
            assignedPermissions = systemPermissionsString;
        }
        else{
            assignedPermissions = permissionService.queryByRoleId(roleId);
        }

        return assignedPermissions;
    }

    /**
     * 管理员的权限情况
     *
     * @return 系统所有权限列表和管理员已分配权限
     */
    @RequiresPermissions("admin:role:permission:get")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="权限详情")
    @GetMapping("/permissions")
    public Object getPermissions(Integer roleId) {
        List<PermVo> systemPermissions = getSystemPermissions();
        Set<String> assignedPermissions = getAssignedPermissions(roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);
        data.put("assignedPermissions", assignedPermissions);
        return ResponseUtil.ok(data);
    }


    /**
     * 更新管理员的权限
     *
     * @param body
     * @return
     */
    @RequiresPermissions("admin:role:permission:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="权限变更")
    @PostMapping("/permissions")
    public Object updatePermissions(@RequestBody String body) {
        Integer roleId = JacksonUtil.parseInteger(body, "roleId");
        List<String> permissions = JacksonUtil.parseStringList(body, "permissions");
        if(roleId == null ){
            return ResponseUtil.fail(502,"请选择要设置的角色");
        }

        if(permissions == null){
            return ResponseUtil.fail(502,"请选择要角色对应的资源权限");
        }

        // 如果修改的角色是超级权限，则拒绝修改。
        if(permissionService.checkSuperPermission(roleId)){
            return ResponseUtil.fail(AdminResponseCode.ROLE_SUPER_SUPERMISSION, "当前角色的超级权限不能变更");
        }

        // 先删除旧的权限，再更新新的权限
        permissionService.deleteByRoleId(roleId);
        for(String permission : permissions){
            LitemallPermission litemallPermission = new LitemallPermission();
            litemallPermission.setRoleId(roleId);
            litemallPermission.setPermission(permission);
            permissionService.add(litemallPermission);
        }
        return ResponseUtil.ok();
    }

}
