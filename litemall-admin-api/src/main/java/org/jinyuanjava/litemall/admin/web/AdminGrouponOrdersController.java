package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallGrouponOrder;
import org.jinyuanjava.litemall.db.domain.ViewGrouponOrders;
import org.jinyuanjava.litemall.db.service.LitemallGrouponRulesService;
import org.jinyuanjava.litemall.db.service.LitemallGrouponOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/grouponorders")
@Validated
@Api(description = "后台管理/营销管理/团购活动订单:/admin/grouponorders")
public class AdminGrouponOrdersController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallGrouponRulesService rulesService;

    @Autowired
    private LitemallGrouponOrderService orderService;


    @RequiresPermissions("admin:grouponorders:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(
              Integer rulesId,
              String name,
              String goodsSn,String goodsName,String orderSn,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             String username,String userNickname,String userPhone,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewGrouponOrders> grouponList = orderService.getViewQueryGrouponOrders(
                rulesId,name,goodsSn,goodsName,orderSn,orderStatusArray,username,userNickname,userPhone,page, limit, sort);
        return ResponseUtil.okList(grouponList);
//        List<Map<String, Object>> groupons = new ArrayList<>();
//        for (LitemallGrouponOrder groupon : grouponList) {
//            try {
//                Map<String, Object> RecordData = new HashMap<>();
//                List<LitemallGrouponOrder> subGrouponList = grouponService.queryJoinRecord(groupon.getId());
//                LitemallGrouponRules rules = rulesService.queryById(groupon.getRulesId());
//                LitemallGoods goods = goodsService.findById(rules.getGoodsId());
//
//                RecordData.put("groupon", groupon);
//                RecordData.put("subGroupons", subGrouponList);
//                RecordData.put("rules", rules);
//                RecordData.put("goods", goods);
//
//                groupons.add(RecordData);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return ResponseUtil.okList(groupons, grouponList);
    }
    private Object validate(LitemallGrouponOrder grouponOrder) {
        Integer orderId = grouponOrder.getOrderId();
        if (orderId == null) {
            return ResponseUtil.fail(401,"保存团购订单信息时订单ID不能为空");
        }
        Integer rulesId = grouponOrder.getRulesId();
        if (rulesId == null) {
            return ResponseUtil.fail(401,"保存团购订单信息时规则ID不能为空");
        }
        Integer userId = grouponOrder.getUserId();
        if (userId == null) {
            return ResponseUtil.fail(401,"保存团购订单信息时会员ID不能为空");
        }

        return null;
    }

    @RequiresPermissions("admin:grouponorders:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动订单"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallGrouponOrder grouponOrder) {
        Object error = validate(grouponOrder);
        if (error != null) {
            return error;
        }
        if (orderService.updateById(grouponOrder) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:grouponorders:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动订单"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallGrouponOrder grouponOrder) {
        Object error = validate(grouponOrder);
        if (error != null) {
            return error;
        }

        orderService.createGroupon(grouponOrder);

        return ResponseUtil.ok(grouponOrder);
    }

    @RequiresPermissions("admin:grouponorders:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "团购活动订单"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGrouponOrder grouponOrder) {
        Integer id = grouponOrder.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        rulesService.delete(id);
        return ResponseUtil.ok();
    }

}
