package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.service.LitemallGoodsProductService;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallOrderService;
import org.jinyuanjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Validated
@Api(description = "后台管理/DashBoard:/admin/dashboard")
public class AdminDashbordController {
    private final Log logger = LogFactory.getLog(AdminDashbordController.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallOrderService orderService;

    @GetMapping("")
    public Object info() {
        int userTotal = userService.count();
        int todayUserToday = userService.countToday();
        int goodsTotal = goodsService.count();
        int todayGoodsToday = goodsService.countToday();
        int productTotal = productService.count();
        int todayProductTotal = orderService.countToday();
        int orderTotal = orderService.count();
        int todayOrderTotal = orderService.countToday();

        Map<String, Integer> data = new HashMap<>();
        data.put("userTotal", userTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("productTotal", productTotal);
        data.put("orderTotal", orderTotal);
        data.put("todayUserTotal", todayUserToday);
        data.put("todayGoodsToday", todayGoodsToday);
        data.put("todayProductTotal", todayProductTotal);
        data.put("todayOrderTotal", todayOrderTotal);

        return ResponseUtil.ok(data);
    }

}
