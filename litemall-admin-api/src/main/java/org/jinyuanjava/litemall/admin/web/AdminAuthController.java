package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.jinyuanjava.litemall.admin.service.LogHelper;
import org.jinyuanjava.litemall.admin.util.Permission;
import org.jinyuanjava.litemall.admin.util.PermissionUtil;
import org.jinyuanjava.litemall.core.util.IpUtil;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin;
import org.jinyuanjava.litemall.db.domain.ViewRoleMenu;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_ACCOUNT;

@RestController
@RequestMapping("/admin/auth")
@Validated
@Api(description = "后台管理/登陆授权:/admin/auth")
public class AdminAuthController {
    private final Log logger = LogFactory.getLog(AdminAuthController.class);

    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LitemallRoleService roleService;
    @Autowired
    private LitemallPermissionService permissionService;
    @Autowired
    private LogHelper logHelper;

    @Autowired
    private LitemallRoleMenuService rolemenuService;

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logHelper.logAuthFail("登录", "用户帐号或密码不正确");
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            logHelper.logAuthFail("登录", "用户帐号已锁定不可用");
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logHelper.logAuthFail("登录", "认证失败");
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(LocalDateTime.now());
        adminService.updateById(admin);

        logHelper.logAuthSucceed("登录");
        return ResponseUtil.ok(currentUser.getSession().getId());
    }

    /*
     *
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logHelper.logAuthSucceed("退出");
        currentUser.logout();
        return ResponseUtil.ok();
    }


    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());
        data.put("ifModifyOnSale",admin.getIfModifyOnSale());

//        LitemallAdmin queryUser=adminService.findById(admin.getId());
//

        //如果是超级管理员，则拥有所有的角色
        if(admin.getUsername().equals("AdminSys"))
        {
            data.put("roles", null);
            data.put("menus", null);
            data.put("secondmenus", null);
        } else {
            Integer[] roleIds = admin.getRoleIds();
            Set<String> roles = roleService.queryByIds(roleIds);
            //Set<String> permissions = permissionService.queryByRoleIds(roleIds);
            data.put("roles", roles);
            //Set<String> menus=rolemenuService.queryCodeByRoleId(Arrays.asList(roleIds));
            Set<String> menus=rolemenuService.getMenuRoleFirstLevel(Arrays.asList(roleIds));
            data.put("menus", menus);
            //获取二级菜单编码
            List<ViewRoleMenu> secondLevelMenus=rolemenuService.getMenuRoleViewSecondLevel(Arrays.asList(roleIds),null,null);
            data.put("secondmenus", secondLevelMenus);
            //获取二级菜单编码
        }

        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
//        data.put("perms", toAPI(permissions));
        Collection<String> apis = new HashSet<>();

        apis.add("*");
        data.put("perms", apis);
        return ResponseUtil.ok(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toAPI(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.jinyuanjava.litemall.admin";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
//                return systemPermissionsMap.values();

            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return ResponseUtil.unlogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return ResponseUtil.ok();
    }

    @GetMapping("/403")
    public Object page403() {
        return ResponseUtil.unauthz();
    }
}
