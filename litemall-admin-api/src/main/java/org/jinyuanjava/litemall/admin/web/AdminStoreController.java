package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.StoreAllinone;
import org.jinyuanjava.litemall.admin.service.AdminStoreService;
import org.jinyuanjava.litemall.db.domain.LitemallStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/store")
@Validated
@Api(description = "后台管理/商城设置/店铺信息:/admin/store")
public class AdminStoreController {
    private final Log logger = LogFactory.getLog(AdminStoreController.class);

    @Autowired
    private AdminStoreService storeService;

    @RequiresPermissions("admin:store:list")
    @RequiresPermissionsDesc(menu={"商城设置","店铺信息"},button = "查询")
    @GetMapping("list")
    public Object List(String name,
                       Integer comId,
                       Integer ownType,
                       Integer brandId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        return storeService.list(name,comId,ownType,brandId,page,limit,sort);
    }



    @RequiresPermissions("admin:store:create")
    @RequiresPermissionsDesc(menu ={"商城设置","店铺信息"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody StoreAllinone store){

        return storeService.create(store);
    }


    @RequiresPermissions("admin:store:read")
    @RequiresPermissionsDesc(menu ={"商城设置","店铺信息"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return storeService.detail(id);
    }

    @RequiresPermissions("admin:store:update")
    @RequiresPermissionsDesc(menu = {"商城设置","店铺信息"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody StoreAllinone store){

        return storeService.update(store);
    }


    @RequiresPermissions("admin:store:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","店铺信息"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallStore store){

        return storeService.delete(store);
    }

    /**
     * 店铺品牌
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:store:storebrands")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "店铺品牌")
    @GetMapping("/storebrands")
    public Object getGoodsProduct(@NotNull Integer id) {
        return storeService.getStoreBrands(id);
    }

}
