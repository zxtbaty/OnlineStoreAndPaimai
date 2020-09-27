package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallBrand;
import org.jinyuanjava.litemall.db.service.LitemallBrandService;
import org.jinyuanjava.litemall.db.service.LitemallCompanyBrandService;
import org.jinyuanjava.litemall.db.service.LitemallStoreBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/brand")
@Validated
@Api(description = "后台管理/商场管理/品牌管理:/admin/brand")
public class AdminBrandController {
    private final Log logger = LogFactory.getLog(AdminBrandController.class);

    @Autowired
    private LitemallBrandService brandService;

    @Autowired
    private LitemallCompanyBrandService companyBrandService;

    @Autowired
    private LitemallStoreBrandService storeBrandService;

    @RequiresPermissions("admin:brand:list")
    @RequiresPermissionsDesc(menu={"商场管理" , "品牌管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String id, String name,String sn,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort)
    {
        List<LitemallBrand> brandList = brandService.querySelective(id, name,sn,page, limit, sort);
        return ResponseUtil.okList(brandList);
    }

    private Object validate(LitemallBrand brand) {
        String name = brand.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(502,"品牌名称不能为空");
            //return ResponseUtil.badArgument();
        }
        if(brandService.checkBrandIfExists(brand)){
            return ResponseUtil.fail(502,"品牌名称已经存在,不能重复");
        }

//       String desc = brand.getDesc();
//       if (StringUtils.isEmpty(desc)) {
//            return ResponseUtil.fail(502,"品牌描述不能为空");
//            //return ResponseUtil.badArgument();
//        }

//        BigDecimal price = brand.getFloorPrice();
//        if (price == null) {
//            return ResponseUtil.fail(502,"底价不能为空");
//            //return ResponseUtil.badArgument();
//        }
        return null;
    }

    @RequiresPermissions("admin:brand:create")
    @RequiresPermissionsDesc(menu={"商场管理" , "品牌管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        brandService.add(brand);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:read")
    @RequiresPermissionsDesc(menu={"商场管理" , "品牌管理"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallBrand brand = brandService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:update")
    @RequiresPermissionsDesc(menu={"商场管理" , "品牌管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        if (brandService.updateById(brand) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:delete")
    @RequiresPermissionsDesc(menu={"商场管理" , "品牌管理"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallBrand brand) {
        Integer id = brand.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        brandService.deleteById(id);
        //删除商贸类型经营品牌
        companyBrandService.deleteByBrandId(id);
        //删除店铺所经营品牌
        storeBrandService.deleteByBrandId(id);

        return ResponseUtil.ok();
    }

}
