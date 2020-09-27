package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文博馆接口
 */
@RestController
@RequestMapping("/wx/wenBoGuan")
@Validated
@Api(description = "微信前端/文博馆:/wx/wenBoGuan")
public class WxWenBoGuanController {
    private final Log logger = LogFactory.getLog(WxWenBoGuanController.class);

    @Autowired
    private LitemallDiccodeService diccodeService;

    @Autowired
    private LitemallWenboguanArticleService wenboguanArticleService;

    @Autowired
    private LitemallWeboguanRecommendService weboguanRecommendService;

    /**
     * 文博馆分类
     * @return
     * @throws Exception
     */
    @GetMapping("getClass")
    public Object getDajiaPaiClass(){
        List<LitemallDicCode> dicCodes=  diccodeService.findByDicmainIdOrDicmainName(null,"文博馆分类",null );
        LitemallDicCode dicCode=new LitemallDicCode();
        dicCode.setCode("TuiJian");
        dicCode.setName("全部");
        dicCodes.add(0,dicCode);
        return ResponseUtil.okList(dicCodes);
    }

    /**
     * 文博馆文章列表,按推荐顺序进行排序

     * @param page
     * @param size

     * @return
     */
    @GetMapping("getAricleList")
    public Object getAricleList(Integer classId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size ) {
        List<ViewWeboguanRecommendWithBLOBs> articleList=
                wenboguanArticleService.querySelective(classId ,page,size );
        return ResponseUtil.okList(articleList);
    }

    /**
     * 文博馆文章明细
     * @param articleId
     */
    @GetMapping("getArticleDetail")
    public Object getArticleDetail( Integer articleId) {
        LitemallWenboguanArticle wenboguanArticle= wenboguanArticleService.findById(articleId);
        return ResponseUtil.ok(wenboguanArticle);
    }


    /**
     * 文博馆浏览量统计
     * @param userId

     */
    @GetMapping("logBrowse")
    public Object addLogBrowse(Integer userId,Integer articleId) {
        LitemallWenboguanArticleWithBLOBs wenboguanArticle= wenboguanArticleService.findById(articleId);
        Integer browseCount=CharUtil.objectConverToInteger(wenboguanArticle.getBrowseCount())+1;
        wenboguanArticle.setBrowseCount(browseCount);
        wenboguanArticleService.updateById(wenboguanArticle);
        return ResponseUtil.ok(wenboguanArticle);
    }

    /**
     * 文博馆点赞量统计
     */
    @GetMapping("logZan")
    public Object addLogZan(Integer userId,Integer articleId) {
        LitemallWenboguanArticleWithBLOBs wenboguanArticle= wenboguanArticleService.findById(articleId);
        Integer zanCount=CharUtil.objectConverToInteger(wenboguanArticle.getZanCount())+1;
        wenboguanArticle.setZanCount(zanCount);
        wenboguanArticleService.updateById(wenboguanArticle);
        return ResponseUtil.ok(wenboguanArticle);
    }

    /**
     * 文博馆点赞量统计
     */
    @GetMapping("logShare")
    public Object addLogShare(Integer userId, @NotNull Integer articleId) {
        LitemallWenboguanArticleWithBLOBs wenboguanArticle= wenboguanArticleService.findById(articleId);
        Integer shareCount=CharUtil.objectConverToInteger(wenboguanArticle.getShareCount())+1;
        wenboguanArticle.setShareCount(shareCount);
        wenboguanArticleService.updateById(wenboguanArticle);
        return ResponseUtil.ok(wenboguanArticle);
    }

}
