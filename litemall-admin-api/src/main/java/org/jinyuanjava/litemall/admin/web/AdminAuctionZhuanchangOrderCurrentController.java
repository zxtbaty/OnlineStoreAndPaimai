package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.ViewAucitonZhuanchangOrderCurrent;
import org.jinyuanjava.litemall.db.service.LitemallAucitonZhuanchangOrderCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/aucitonZhuanchangOrder")
@Validated
@Api(description = "后台管理/拍卖管理/专场拍卖活动订单:/admin/aucitonZhuanchangOrder")
public class AdminAuctionZhuanchangOrderCurrentController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallAucitonZhuanchangOrderCurrentService orderService;


    @RequiresPermissions("admin:aucitonZhuanchangOrder:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场拍卖活动订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(
                             String orderSn,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             String username,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewAucitonZhuanchangOrderCurrent> orderList = orderService.getViewAucitonZhuanchangOrderCurrent(
                orderSn,orderStatusArray, username,page, limit, sort);

        return ResponseUtil.okList(orderList);
    }







}
