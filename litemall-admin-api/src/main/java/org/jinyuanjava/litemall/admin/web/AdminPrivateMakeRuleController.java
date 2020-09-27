package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallPrivateMakeRule;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallPrivateMakeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/privateMakeRule")
@Validated
@Api(description = "后台管理/拍卖管理/私人定制规则:/admin/privateMakeRule")
public class AdminPrivateMakeRuleController {
    private final Log logger = LogFactory.getLog(AdminPrivateMakeRuleController.class);

    @Autowired
    private LitemallPrivateMakeRuleService rulesService;

    @Autowired
    private LitemallGoodsService goodsService;

    @RequiresPermissions("admin:privateMakeRule:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "私人定制规则"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer expireFlag,
                       Integer enabled,Integer offerFlag,
                       String goodsSn, String goodsName,

                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallPrivateMakeRule> result= rulesService.querySelective(expireFlag,
                enabled,offerFlag,
                goodsSn, goodsName,page, limit, sort);
        return ResponseUtil.okList(result);

    }
    @RequiresPermissions("admin:privateMakeRule:create")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "私人定制规则"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPrivateMakeRule litemallPrivateMakeRule) {
        Object error = validate(litemallPrivateMakeRule);
        if (error != null) {
            return error;
        }
        //更新商品图
        LitemallGoods goods=goodsService.findById(litemallPrivateMakeRule.getGoodsId());
        if(goods!=null){
            litemallPrivateMakeRule.setGalleryBig(goods.getGalleryBig());
            litemallPrivateMakeRule.setGalllerySmall(goods.getGallery());
        }
        rulesService.createRules(litemallPrivateMakeRule);
        return ResponseUtil.ok();

    }

    @RequiresPermissions("admin:privateMakeRule:update")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "私人定制规则"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPrivateMakeRule litemallPrivateMakeRule) {
        Object error = validate(litemallPrivateMakeRule);
        if (error != null) {
            return error;
        }
        //更新商品图
        LitemallGoods goods=goodsService.findById(litemallPrivateMakeRule.getGoodsId());
        if(goods!=null){
            litemallPrivateMakeRule.setGalleryBig(goods.getGalleryBig());
            litemallPrivateMakeRule.setGalllerySmall(goods.getGallery());
        }
        rulesService.updateById(litemallPrivateMakeRule);
        return ResponseUtil.ok();
    }

    private Object validate(LitemallPrivateMakeRule ruleCurrent) {

        if (StringUtils.isEmpty(ruleCurrent.getGoodsName())) {
            return ResponseUtil.fail(401, "私人定制商品名称不不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getBeginTime())) {
            return ResponseUtil.fail(401, "私人定制起始时间不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getEndTime())) {
            return ResponseUtil.fail(401, "私人定制截止时间不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getLowPrice())) {
            return ResponseUtil.fail(401, "私人定制起始价不不能为空");
        }


        return null;
    }
    @RequiresPermissions("admin:privateMakeRule:delete")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "私人定制规则"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPrivateMakeRule litemallPrivateMakeRule) {
        rulesService.delete(litemallPrivateMakeRule.getId());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:privateMakeRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","私人定制规则"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallPrivateMakeRule result= rulesService.queryById(id);
        return ResponseUtil.ok(result);
    }


}
