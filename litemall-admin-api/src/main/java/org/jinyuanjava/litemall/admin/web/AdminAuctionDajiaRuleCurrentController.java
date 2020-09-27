package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionDajiaRuleCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.service.LitemallAuctionDajiaRuleCurrentService;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallUserChargeMoneyLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/dajiaPaimaiRule")
@Validated
@Api(description = "后台管理/拍卖管理/大家拍设置:/admin/dajiaPaimaiRule")
public class AdminAuctionDajiaRuleCurrentController {
    private final Log logger = LogFactory.getLog(AdminAuctionDajiaRuleCurrentController.class);

    @Autowired
    private LitemallAuctionDajiaRuleCurrentService rulesService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallUserChargeMoneyLockService userChargeMoneyLockService;


    @RequiresPermissions("admin:dajiaPaimaiRule:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "大家拍设置"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer expireFlag,
                       Integer enabled,Integer offerFlag,
                       String goodsSn, String goodsName,

                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallAuctionDajiaRuleCurrent> result= rulesService.querySelective(expireFlag,
                enabled,offerFlag,
                goodsSn, goodsName,page, limit, sort);
        return ResponseUtil.okList(result);

    }
    @RequiresPermissions("admin:dajiaPaimaiRule:create")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "大家拍设置"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent) {
        Object error = validate(dajiaRuleCurrent);
        if (error != null) {
            return error;
        }
        //更新商品图
        LitemallGoods goods=goodsService.findById(dajiaRuleCurrent.getGoodsId());
        if(goods!=null){
            dajiaRuleCurrent.setGalleryBig(goods.getGalleryBig());
            dajiaRuleCurrent.setGalllerySmall(goods.getGallery());
            dajiaRuleCurrent.setAuctionPicHead(goods.getPicUrl());
        }
        rulesService.createRules(dajiaRuleCurrent);
        return ResponseUtil.ok();

    }

    @RequiresPermissions("admin:dajiaPaimaiRule:update")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "大家拍设置"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent) {
        Object error = validate(dajiaRuleCurrent);
        if (error != null) {
            return error;
        }
        //更新商品图
        LitemallGoods goods=goodsService.findById(dajiaRuleCurrent.getGoodsId());
        if(goods!=null){
            dajiaRuleCurrent.setGalleryBig(goods.getGalleryBig());
            dajiaRuleCurrent.setGalllerySmall(goods.getGallery());
            dajiaRuleCurrent.setAuctionPicHead(goods.getPicUrl());
        }
        rulesService.updateById(dajiaRuleCurrent);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:dajiaPaimaiRule:delete")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "大家拍设置"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAuctionDajiaRuleCurrent litemallAuctionDajiaRuleCurrent) {
        rulesService.delete(litemallAuctionDajiaRuleCurrent.getId());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:dajiaPaimaiRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","大家拍设置"},button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        LitemallAuctionDajiaRuleCurrent result= rulesService.queryById(id);
        return ResponseUtil.ok(result);
    }

    private Object validate(LitemallAuctionDajiaRuleCurrent ruleCurrent) {

        if (StringUtils.isEmpty(ruleCurrent.getGoodsName())) {
            return ResponseUtil.fail(401, "大家拍商品名称不不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getBeginTime())) {
            return ResponseUtil.fail(401, "专场活动起始时间不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getEndTime())) {
            return ResponseUtil.fail(401, "专场活动截止时间不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getMinPrice())) {
            return ResponseUtil.fail(401, "最低起拍价不不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getAddPrice())) {
            return ResponseUtil.fail(401, "加价单位不不能为空");
        }

        return null;
    }

    @RequiresPermissions("admin:dajiaPaimaiRule:read")
    @RequiresPermissionsDesc(menu ={"商城设置","大家拍设置"},button = "批量解锁保证金")
    @GetMapping("/unLockMoney")
    public Object unLockMoney(@NotNull Integer ruleMxId,Boolean ifAll){
        Boolean result= userChargeMoneyLockService.unLockMoneyBatch("大家拍",ruleMxId,ifAll);
        return ResponseUtil.ok(result);
    }



}
