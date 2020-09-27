package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.storage.StorageService;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallStorage;
import org.jinyuanjava.litemall.db.service.LitemallStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/storage")
@Validated
@Api(description = "后台管理/统计管理/对象存储:/admin/storage")
public class AdminStorageController {
    private final Log logger = LogFactory.getLog(AdminStorageController.class);

    @Autowired
    private StorageService storageService;
    @Autowired
    private LitemallStorageService litemallStorageService;

    @RequiresPermissions("admin:storage:list")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="查询")
    @GetMapping("/list")
    public Object list(String key, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallStorage> storageList = litemallStorageService.querySelective(key, name, page, limit, sort);
        return ResponseUtil.okList(storageList);
    }

    @RequiresPermissions("admin:storage:create")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="上传")
    @PostMapping("/create")
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        LitemallStorage litemallStorage = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
        return ResponseUtil.ok(litemallStorage);
    }

    @RequiresPermissions("admin:storage:read")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="详情")
    @PostMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallStorage storageInfo = litemallStorageService.findById(id);
        if (storageInfo == null) {
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(storageInfo);
    }

    @RequiresPermissions("admin:storage:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallStorage litemallStorage) {
        if (litemallStorageService.update(litemallStorage) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(litemallStorage);
    }

    @RequiresPermissions("admin:storage:delete")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallStorage litemallStorage) {
        String key = litemallStorage.getKey();
        if (StringUtils.isEmpty(key)) {
            return ResponseUtil.badArgument();
        }
        litemallStorageService.deleteByKey(key);
        storageService.delete(key);
        return ResponseUtil.ok();
    }
}
