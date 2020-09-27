package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.LitemallWeboguanRecommendService;
import org.jinyuanjava.litemall.db.service.LitemallWenboguanArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/wenBoGuanArticlelist")
@Validated
@Api(description = "后台管理/文章管理/文博馆:/admin/wenBoGuanArticlelist")
public class AdminWenBoGuanArticleListController {
    private final Log logger = LogFactory.getLog(AdminWenBoGuanArticleListController.class);

    @Autowired
    private LitemallWenboguanArticleService wenboguanArticleService;

    @Autowired
    private LitemallWeboguanRecommendService weboguanRecommendService;


    /**
     * 获取所有的文章列表
     * @param classId
     * @param title
     * @param author
     * @param page
     * @param limit
     * @return
     */

    @RequiresPermissions("admin:wenBoGuanArticlelist:list")
    @RequiresPermissionsDesc(menu={"文章管理","文博馆文"},button = "查询")
    @GetMapping("list")
    public Object List(
          Integer classId, String title, String author,
           @RequestParam(defaultValue = "1") Integer page,
           @RequestParam(defaultValue = "10") Integer limit
    ){
        List<LitemallWenboguanArticleWithBLOBs> wenBoGuanArticleList=wenboguanArticleService.queryIndex(classId,title,author,page,limit);
        return ResponseUtil.okList(wenBoGuanArticleList);
    }

    /**
     * 获取推荐的文章列表
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:wenBoGuanArticlelist:listRecommend")
    @RequiresPermissionsDesc(menu={"文章管理","文博馆文"},button = "查询")
    @GetMapping("listRecommend")
    public Object listRecommend(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewWeboguanRecommendWithBLOBs> wenBoGuanArticleList=weboguanRecommendService.querySelective(page,limit);
        if(wenBoGuanArticleList==null){
            return ResponseUtil.ok();
        }
        return ResponseUtil.okList(wenBoGuanArticleList);
    }

    /**
     * 查询还没有补推荐的文章列表
     * @param classId
     * @param title
     * @param author
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:wenBoGuanArticlelist:listUnRecommendArticleList")
    @RequiresPermissionsDesc(menu={"文章管理","文博馆文"},button = "查询")
    @GetMapping("listUnRecommendArticleList")
    public Object listUnRecommendArticleList(
            Integer classId, String title, String author,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<LitemallWenboguanArticleWithBLOBs> wenBoGuanArticleList=wenboguanArticleService.queryUnRecommendList(classId,title,author,page,limit);
        return ResponseUtil.okList(wenBoGuanArticleList);

    }


    private Object validate(LitemallWenboguanArticleWithBLOBs articleList){
        String title=articleList.getTitle();
        if(StringUtils.isEmpty(title)){
            return ResponseUtil.fail(502,"文章标题不能为空");
        }
        if(StringUtils.isEmpty(articleList.getClassId())){
            return ResponseUtil.fail(502,"文章分类不能为空");
        }
        if(StringUtils.isEmpty(articleList.getFengmian())){
            return ResponseUtil.fail(502,"封面内容不能为空");
        }
        if(StringUtils.isEmpty(articleList.getContent())){
            return ResponseUtil.fail(502,"文章内容不能为空");
        }
        if(StringUtils.isEmpty(articleList.getAuthor())){
            return ResponseUtil.fail(502,"文章作者不能为空");
        }
        return null;
    }


    @RequiresPermissions("admin:wenBoGuanArticlelist:create")
    @RequiresPermissionsDesc(menu ={"文章管理","文博馆文"},button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallWenboguanArticleWithBLOBs articleList){
        Object error=validate(articleList);
        if(error!=null){
            return error;
        }
        wenboguanArticleService.add(articleList);
        return ResponseUtil.ok(articleList);
    }

    @RequiresPermissions("admin:wenBoGuanArticlelist:addRecommendArticle")
    @RequiresPermissionsDesc(menu ={"文章管理","填加推荐"},button = "添加")
    @PostMapping("/addRecommendArticle")
    public Object addRecommendArticle(@RequestBody List<LitemallWenboguanArticle> articleList){
        for(LitemallWenboguanArticle article:articleList){

            LitemallWeboguanRecommend recommend=new LitemallWeboguanRecommend();
            recommend.setArticleId(article.getId());
            weboguanRecommendService.add(recommend);
        }

        return ResponseUtil.ok(articleList);
    }



    @RequiresPermissions("admin:wenBoGuanArticlelist:read")
    @RequiresPermissionsDesc(menu ={"文章管理","文博馆文"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallWenboguanArticle litemallArticleList=wenboguanArticleService.findById(id);
        return ResponseUtil.ok(litemallArticleList);
    }

    @RequiresPermissions("admin:wenBoGuanArticlelist:update")
    @RequiresPermissionsDesc(menu = {"文章管理","文博馆文"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallWenboguanArticleWithBLOBs articleList){
        Object error=validate(articleList);
        if(error!=null){
            return error;
        }
        if(wenboguanArticleService.updateById(articleList)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(articleList);
    }


    @RequiresPermissions("admin:wenBoGuanArticlelist:delete")
    @RequiresPermissionsDesc(menu = {"文章管理","文博馆文"},button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallWenboguanArticle articleList){
        Integer id=articleList.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        wenboguanArticleService.deleteById(id);
        weboguanRecommendService.deleteByArticleId(id);

        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:wenBoGuanArticlelist:updateRecommend")
    @RequiresPermissionsDesc(menu = {"文章管理","更新排序"},button = "编辑")
    @PostMapping("/updateRecommend")
    public Object updateRecommend(@RequestBody LitemallWeboguanRecommend recommend){

        if(weboguanRecommendService.updateById(recommend)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(recommend);
    }

    @RequiresPermissions("admin:wenBoGuanArticlelist:updateRecommendBatch")
    @RequiresPermissionsDesc(menu = {"文章管理","更新排序"},button = "编辑")
    @PostMapping("/updateRecommendBatch")
    public Object updateRecommendBatch(@RequestBody List<LitemallWeboguanRecommend> recommendList){
        if(recommendList!=null&&recommendList.size()>0){
            for(LitemallWeboguanRecommend recommend:recommendList){
                weboguanRecommendService.updateById(recommend);
            }
        }
        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:articlelist:deleteRecommend")
    @RequiresPermissionsDesc(menu = {"文章管理","删除推荐"},button = "删除")
    @PostMapping("/deleteRecommend")
    public Object deleteRecommend(@RequestBody LitemallWeboguanRecommend recommend){
        Integer id=recommend.getId();
        if(id==null){
            return ResponseUtil.badArgument();
        }
        weboguanRecommendService.deleteById(recommend.getId());
        return ResponseUtil.ok();
    }
}
