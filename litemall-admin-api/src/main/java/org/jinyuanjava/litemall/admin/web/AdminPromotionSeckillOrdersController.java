package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/seckillorders")
@Validated
@Api(description = "后台管理/营销管理/秒杀活动订单:/admin/profile")
public class AdminPromotionSeckillOrdersController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallPromotionSeckillRuleService rulesService;

    @Autowired
    private LitemallPromotionSeckillOrderService orderService;


    @RequiresPermissions("admin:seckillorders:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(Integer rulesId, String seckillName,
                             String goodsSn,String goodsName,String orderSn,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             String username,String userNickname,String userPhone,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewSeckillOrders> seckillList = orderService.getViewQuerySeckillOrders
                (rulesId,seckillName,goodsSn,goodsName,orderSn,orderStatusArray,
                        username,userNickname,userPhone, page, limit, sort);


        return ResponseUtil.okList(seckillList);
    }


    private Object validate(LitemallPromotionSeckillOrder seckillOrder) {
        Integer orderId = seckillOrder.getOrderId();
        if (orderId == null) {
            return ResponseUtil.fail(401,"保存秒杀订单信息时订单ID不能为空");
        }
        Integer rulesId = seckillOrder.getRulesId();
        if (rulesId == null) {
            return ResponseUtil.fail(401,"保存秒杀订单信息时规则ID不能为空");
        }
        Integer userId = seckillOrder.getUserId();
        if (userId == null) {
            return ResponseUtil.fail(401,"保存秒杀订单信息时会员ID不能为空");
        }

        return null;
    }

    @RequiresPermissions("admin:seckillorders:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动订单"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPromotionSeckillOrder seckillOrder) {
        Object error = validate(seckillOrder);
        if (error != null) {
            return error;
        }
        if (orderService.updateById(seckillOrder) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:seckillorders:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动订单"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPromotionSeckillOrder seckillOrder) {
        Object error = validate(seckillOrder);
        if (error != null) {
            return error;
        }

        orderService.createSeckill(seckillOrder);

        return ResponseUtil.ok(seckillOrder);
    }

    @RequiresPermissions("admin:seckillorders:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "秒杀活动订单"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionSeckillOrder seckillOrder) {
        Integer id = seckillOrder.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        rulesService.delete(id);
        return ResponseUtil.ok();
    }

}
