package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.ViewPrivateMakeOrder;
import org.jinyuanjava.litemall.db.service.LitemallPrivateMakeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/privateMakeOrder")
@Validated
@Api(description = "后台管理/拍卖管理/私人定制订单:/admin/privateMakeOrder")
public class AdminPrivateMakeOrderController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallPrivateMakeOrderService orderService;

    @RequiresPermissions("admin:privateMakeOrder:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "私人定制订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(
         String userPhone, String orderSn,Integer rulesId,
         @RequestParam(required = false) List<Short> orderStatusArray,
         String username,String goodsSn,String goodsName,
         @RequestParam(defaultValue = "1") Integer page,
         @RequestParam(defaultValue = "10") Integer limit,
         @RequestParam(defaultValue = "add_time") String sort
        ) {
        List<ViewPrivateMakeOrder> orderList = orderService.getViewPrivateMakeOrder(
                userPhone,orderSn,rulesId,orderStatusArray, username,goodsSn,goodsName,page, limit, sort);

        return ResponseUtil.okList(orderList);
    }







}
