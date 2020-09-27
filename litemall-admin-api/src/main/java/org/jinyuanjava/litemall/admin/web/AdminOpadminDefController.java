package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.OpadminDefAllinone;
import org.jinyuanjava.litemall.admin.service.AdminOpadminDefService;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/opadmindef")
@Validated
@Api(description = "后台管理/消息管理/后端消息定义:/admin/opadmindef")
public class AdminOpadminDefController {
    private final Log logger = LogFactory.getLog(AdminOpadminDefController.class);

    @Autowired
    private AdminOpadminDefService opadminDefService;

    @RequiresPermissions("admin:opadmindef:list")
    @RequiresPermissionsDesc(menu={"消息管理","后端消息定义"},button = "查询")
    @GetMapping("list")
    public Object List(
            String typeCode,String typeName,String title,Integer expireFlag,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        return opadminDefService.list(typeCode,typeName,title,expireFlag,page,limit,sort);
    }

    @RequiresPermissions("admin:opadmindef:create")
    @RequiresPermissionsDesc(menu ={"消息管理","后端消息定义"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody OpadminDefAllinone opadminDefAllinone){
        return opadminDefService.create(opadminDefAllinone);
    }


    @RequiresPermissions("admin:opadmindef:read")
    @RequiresPermissionsDesc(menu ={"消息管理","后端消息定义"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return opadminDefService.detail(id);
    }

    @RequiresPermissions("admin:opadmindef:update")
    @RequiresPermissionsDesc(menu = {"消息管理","后端消息定义"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody OpadminDefAllinone opadminDefAllinone){
        return opadminDefService.update(opadminDefAllinone);
    }

    @RequiresPermissions("admin:opadmindef:delete")
    @RequiresPermissionsDesc(menu = {"消息管理","后端消息定义"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallOpadminDef store){
        return opadminDefService.delete(store);
    }

    /**
     * 获取某个通知用户明细
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:opadmindef:opadminpubs")
    @RequiresPermissionsDesc(menu = {"消息管理", "后端消息定义"}, button = "通知用户")
    @GetMapping("/opadminpubs")
    public Object getGoodsProduct(@NotNull Integer id) {
        return opadminDefService.getOpadminPubs(id);
    }

}
