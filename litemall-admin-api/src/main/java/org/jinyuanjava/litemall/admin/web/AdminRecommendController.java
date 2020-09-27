package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.service.AdminRecommendService;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsRecommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/recommend")
@Validated
@Api(description = "后台管理/商城设置/推荐排序:/admin/recommend")
public class AdminRecommendController {
    private final Log logger = LogFactory.getLog(AdminRecommendController.class);

    @Autowired
    private AdminRecommendService adminRecommendService;


    /**
     * 查询推荐商品
     *
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    @RequiresPermissions("admin:recommend:list")
    @RequiresPermissionsDesc(menu = {"商城设置", "推荐排序"}, button = "查询")
    @GetMapping("/list")
    public Object list(String posType, Integer comId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        return adminRecommendService.list(posType, comId, page, limit, sort);
    }

    /**
     * 编辑排序
     *
     * @param litemallGoodsRecommends
     * @return
     */
    @RequiresPermissions("admin:recommend:update")
    @RequiresPermissionsDesc(menu = {"商城设置", "推荐排序"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody List<LitemallGoodsRecommend> litemallGoodsRecommends) {
        return adminRecommendService.batchUpdate(litemallGoodsRecommends);
    }

    /**
     * 删除商品
     *
     * @param litemallGoodsRecommend
     * @return
     */
    @RequiresPermissions("admin:recommend:delete")
    @RequiresPermissionsDesc(menu = {"商城设置", "推荐排序"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGoodsRecommend litemallGoodsRecommend) {
        return adminRecommendService.delete(litemallGoodsRecommend);
    }

    /**
     * 添加商品
     *
     * @param litemallGoodsRecommends
     * @return
     */
    @RequiresPermissions("admin:recommend:create")
    @RequiresPermissionsDesc(menu = {"商城设置", "推荐排序"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody List<LitemallGoodsRecommend> litemallGoodsRecommends) {
        return adminRecommendService.batchCreate(litemallGoodsRecommends);
    }



}
