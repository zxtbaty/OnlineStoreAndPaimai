package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallArticleList;
import org.jinyuanjava.litemall.db.service.LitemallArticleListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/articlelist")
@Validated
@Api(description = "后台管理/文章管理/文章列表:/admin/articlelist")
public class AdminArticleListController {
    private final Log logger = LogFactory.getLog(AdminArticleListController.class);

    @Autowired
    private LitemallArticleListService articleListService;

    @RequiresPermissions("admin:articlelist:list")
    @RequiresPermissionsDesc(menu={"文章管理","文章列表"},button = "查询")
    @GetMapping("list")
    public Object List(
          Integer classId, String className, String title, String author,
           @RequestParam(defaultValue = "1") Integer page,
           @RequestParam(defaultValue = "10") Integer limit,
           @RequestParam(defaultValue = "add_time") String sort
    ){
        List<LitemallArticleList> articleList=articleListService.querySelective(classId,className,title,author,page,limit,sort);
        return ResponseUtil.okList(articleList);
    }


    private Object validate(LitemallArticleList articleList){
        String title=articleList.getTitle();
        if(StringUtils.isEmpty(title)){
            return ResponseUtil.fail(502,"文章标题不能为空");
            //return ResponseUtil.badArgument();
        }
        return null;
    }


    @RequiresPermissions("admin:articlelist:create")
    @RequiresPermissionsDesc(menu ={"文章管理","文章列表"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallArticleList articleList){
        Object error=validate(articleList);
        if(error!=null){
            return error;
        }
        articleListService.add(articleList);
        return ResponseUtil.ok(articleList);
    }


    @RequiresPermissions("admin:articlelist:read")
    @RequiresPermissionsDesc(menu ={"文章管理","文章列表"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallArticleList litemallArticleList=articleListService.findById(id);
        return ResponseUtil.ok(litemallArticleList);
    }

    @RequiresPermissions("admin:articlelist:update")
    @RequiresPermissionsDesc(menu = {"文章管理","文章列表"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallArticleList articleList){
        Object error=validate(articleList);
        if(error!=null){
            return error;
        }
        if(articleListService.updateById(articleList)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(articleList);
    }


    @RequiresPermissions("admin:articlelist:delete")
    @RequiresPermissionsDesc(menu = {"文章管理","文章列表"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallArticleList articleList){
        Integer id=articleList.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        articleListService.deleteById(id);
        return ResponseUtil.ok();
    }

}
