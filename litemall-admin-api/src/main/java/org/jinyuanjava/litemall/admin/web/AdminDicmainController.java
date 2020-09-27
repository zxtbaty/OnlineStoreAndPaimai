package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.DicmainAllinone;
import org.jinyuanjava.litemall.admin.service.AdminDicmainService;
import org.jinyuanjava.litemall.db.domain.LitemallDicMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/dicmain")
@Validated
@Api(description = "后台管理/系统管理/代码管理:/admin/dicmain")
public class AdminDicmainController {
    private final Log logger = LogFactory.getLog(AdminDicmainController.class);

    @Autowired
    private AdminDicmainService adminDicmainService;

    /**
     * 查询字典主表
     *
     * @param name
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    @RequiresPermissions("admin:dicmain:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "查询主表")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return adminDicmainService.list(name, page, limit, sort);
    }

    /**
     * 编辑字典主表
     *
     * @param dicmainAllinone
     * @return
     */
    @RequiresPermissions("admin:dicmain:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody DicmainAllinone dicmainAllinone) {
        return adminDicmainService.update(dicmainAllinone);
    }

    /**
     * 删除字典主表
     *
     * @param litemallDicMain
     * @return
     */
    @RequiresPermissions("admin:dicmain:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallDicMain litemallDicMain) {
        return adminDicmainService.delete(litemallDicMain);
    }

    /**
     * 添加字典表
     *
     * @param dicmainAllinone
     * @return
     */
    @RequiresPermissions("admin:dicmain:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody DicmainAllinone dicmainAllinone) {
        return adminDicmainService.create(dicmainAllinone);
    }

    /**
     * 字典详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:dicmain:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminDicmainService.detail(id);

    }

}
