package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.vo.CategoryVo;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallCategory;
import org.jinyuanjava.litemall.db.service.LitemallCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
@Validated
@Api(description = "后台管理/商场管理/类目管理:/admin/category")
public class AdminCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Autowired
    private LitemallCategoryService categoryService;

    @RequiresPermissions("admin:category:list")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer id, String name,String comName) {
        List<CategoryVo> categoryVoList = new ArrayList<>();

        List<LitemallCategory> categoryList = categoryService.queryByPid(0,id,name,comName);
        for(LitemallCategory category : categoryList){
            CategoryVo categoryVO = new CategoryVo();
            categoryVO.setId(category.getId());
            categoryVO.setDesc(category.getDesc());
            categoryVO.setIconUrl(category.getIconUrl());
            categoryVO.setPicUrl(category.getPicUrl());
            categoryVO.setKeywords(category.getKeywords());
            categoryVO.setName(category.getName());
            categoryVO.setLevel(category.getLevel());
            categoryVO.setComId(category.getComId());
            categoryVO.setComName(category.getComName());
            categoryVO.setSortOrder(category.getSortOrder());

            List<CategoryVo> children = new ArrayList<>();
            List<LitemallCategory> subCategoryList = categoryService.queryByPid(category.getId());
            for(LitemallCategory subCategory : subCategoryList){
                CategoryVo subCategoryVo = new CategoryVo();
                subCategoryVo.setId(subCategory.getId());
                subCategoryVo.setDesc(subCategory.getDesc());
                subCategoryVo.setIconUrl(subCategory.getIconUrl());
                subCategoryVo.setPicUrl(subCategory.getPicUrl());
                subCategoryVo.setKeywords(subCategory.getKeywords());
                subCategoryVo.setName(subCategory.getName());
                subCategoryVo.setLevel(subCategory.getLevel());
                categoryVO.setComId(category.getComId());
                categoryVO.setComName(category.getComName());
                subCategoryVo.setSortOrder(subCategory.getSortOrder());
                children.add(subCategoryVo);
            }
            if(children.size()>0) {
                categoryVO.setChildren(children);
            }
            categoryVoList.add(categoryVO);
        }

        return ResponseUtil.okList(categoryVoList);
    }

    private Object validate(LitemallCategory category) {
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(502, "类名称不能为空");
        }

        String level = category.getLevel();
        if (StringUtils.isEmpty(level)) {
            return ResponseUtil.fail(502, "类级别不能为空");
        }
        if (!level.equals("L1") && !level.equals("L2")) {
            return ResponseUtil.fail(502, "类级别必须等于L1或者L2");
        }

        Integer pid = category.getPid();
        if (level.equals("L2") && (pid == null)) {
            return ResponseUtil.fail(502, "未设置一级分类");

        }
        //判断类目名称是否已经存在
        Boolean ifExists=categoryService.checkExistByNameAndId(category.getName(),category.getId());
        if(ifExists==true){
            return ResponseUtil.fail(502, "类名称已经存在");
        }


        return null;
    }

    @RequiresPermissions("admin:category:create")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallCategory category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        categoryService.add(category);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:read")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallCategory category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:update")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallCategory category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }

        if (categoryService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:delete")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallCategory category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        categoryService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:l1")
    @GetMapping("/l1")
    public Object catL1(Integer comId) {
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1(comId);
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (LitemallCategory category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return ResponseUtil.okList(data);
    }
}
