package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.CompanyAllinone;
import org.jinyuanjava.litemall.admin.service.AdminCompanyService;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallCompany;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyBrand;
import org.jinyuanjava.litemall.db.service.LitemallCompanyBrandService;
import org.jinyuanjava.litemall.db.service.LitemallCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/company")
@Validated
@Api(description = "后台管理/商城设置/商贸类型:/admin/company")
public class AdminCompanyController {
    private final Log logger = LogFactory.getLog(AdminCompanyController.class);

    @Autowired
    private LitemallCompanyService litemallCompanyService;

    @Autowired
    private AdminCompanyService adminCompanyService;

    @Autowired
    private LitemallCompanyBrandService companyBrandService;

    @RequiresPermissions("admin:company:list")
    @RequiresPermissionsDesc(menu={"商城设置","商贸类型"},button = "查询")
    @GetMapping("list")
    public Object List(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ){
        return adminCompanyService.list(name,page,limit,sort);
//        List<LitemallCompany> companyList=adminCompanyService.querySelective(name,page,limit,sort);
//        return ResponseUtil.okList(companyList);
    }

    @RequiresPermissions("admin:company:all")
    @RequiresPermissionsDesc(menu={"商城设置","商贸类型"},button = "所有列表")
    @GetMapping("all")
    public Object allList(){
        List<LitemallCompany> companyList=litemallCompanyService.queryIndex();
        return ResponseUtil.okList(companyList);
    }


//    private Object validate(LitemallCompany company){
//        String name=company.getName();
//        if(StringUtils.isEmpty(name)){
//            return ResponseUtil.badArgument();
//        }
//        return null;
//    }


    @RequiresPermissions("admin:company:create")
    @RequiresPermissionsDesc(menu ={"商城设置","商贸类型"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody CompanyAllinone companyAllinone){


        return adminCompanyService.create(companyAllinone);

//        Object error=validate(company);
//        if(error!=null){
//            return error;
//        }
//        companyService.add(company);
//        return ResponseUtil.ok(company);
    }


    @RequiresPermissions("admin:company:read")
    @RequiresPermissionsDesc(menu ={"商城设置","商贸类型"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        return adminCompanyService.detail(id);
//        LitemallCompany litemallHomeIcon=companyService.findById(id);
//        return ResponseUtil.ok(litemallHomeIcon);
    }

    @RequiresPermissions("admin:company:update")
    @RequiresPermissionsDesc(menu = {"商城设置","商贸类型"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody CompanyAllinone companyAllinone){
        return adminCompanyService.update(companyAllinone);
//        Object error=validate(company);
//        if(error!=null){
//            return error;
//        }
//        if(companyService.updateById(company)==0){
//            return ResponseUtil.updatedDataFailed();
//        }
//        return ResponseUtil.ok(company);
    }


    @RequiresPermissions("admin:company:delete")
    @RequiresPermissionsDesc(menu = {"商城设置","商贸类型"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallCompany company){
        return adminCompanyService.delete(company);
//        Integer id=company.getId();
//        if(id==null){
//            return ResponseUtil.badArgument();
//        }
//        companyService.deleteById(id);
//        return ResponseUtil.ok();
    }
    /**
     * 公司品牌
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:company:companybrands")
    @RequiresPermissionsDesc(menu = {"商城设置", "商贸类型"}, button = "公司品牌")
    @GetMapping("/companybrands")
    public Object getCompanyBrands(@NotNull Integer id) {
        return adminCompanyService.getCompanyBrands(id);
    }

    /**
     * 航站楼
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:company:hangzhanlou")
    @RequiresPermissionsDesc(menu = {"商城设置", "商贸类型"}, button = "航站楼")
    @GetMapping("/hangzhanlou")
    public Object getHangzhanlous(@NotNull Integer id) {
        return adminCompanyService.getHangzhanlous(id);
    }

    /**
     * 更新公司品牌
     *
     * @param companyBrand
     * @return
     */
    @RequiresPermissions("admin:company:updatecompanybrand")
    @RequiresPermissionsDesc(menu = {"商城设置", "商贸类型"}, button = "更新品牌排序")
    @PostMapping("/updatecompanybrand")
    public Object updateCompanyBrand(@RequestBody LitemallCompanyBrand companyBrand) {
        if(companyBrand.getId()==null){
            return null;
        }
        if(companyBrand.getId()==null&&companyBrand.getComId()!=null){
            companyBrandService.add(companyBrand);
        }
        if(companyBrand.getId()!=null){
            companyBrandService.updateById(companyBrand);
        }

        return ResponseUtil.ok();

    }

}
