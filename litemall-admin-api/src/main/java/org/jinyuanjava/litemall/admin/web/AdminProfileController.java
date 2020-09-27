package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin;
import org.jinyuanjava.litemall.db.service.LitemallAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_ACCOUNT;

@RestController
@RequestMapping("/admin/profile")
@Validated
@Api(description = "后台管理/更改密码:/admin/profile")
public class AdminProfileController {
    private final Log logger = LogFactory.getLog(AdminProfileController.class);

    @Autowired
    private LitemallAdminService adminService;

    @RequiresAuthentication
    @PostMapping("/password")
    public Object create(@RequestBody String body) {
        String oldPassword = JacksonUtil.parseString(body, "oldPassword");
        String newPassword = JacksonUtil.parseString(body, "newPassword");
        if (StringUtils.isEmpty(oldPassword)) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(newPassword)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, admin.getPassword())) {
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "账号密码不对");
        }

        String encodedNewPassword = encoder.encode(newPassword);
        admin.setPassword(encodedNewPassword);

        adminService.updateById(admin);
        return ResponseUtil.ok();
    }

}
