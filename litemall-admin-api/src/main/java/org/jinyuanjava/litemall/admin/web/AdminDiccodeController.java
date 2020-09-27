package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallDicCode;
import org.jinyuanjava.litemall.db.service.LitemallDiccodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/diccode")
@Api(description = "后台管理/系统管理/代码管理:/admin/diccode")
public class AdminDiccodeController {
    private final Log logger = LogFactory.getLog(AdminDiccodeController.class);

    @Autowired
    private LitemallDiccodeService diccodeService;

    /**
     * 查询字典明细
     * @param dicmainId
     * @param dicmainName
     * @param sort
     * @return
     */
    @RequiresPermissions("admin:diccode:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "代码管理"}, button = "查询明细")
    @GetMapping("/list")
    public Object list(Integer dicmainId, String dicmainName,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallDicCode> dicCodes= diccodeService.findByDicmainIdOrDicmainName(dicmainId,dicmainName,sort);

        return ResponseUtil.okList(dicCodes);
    }

}
