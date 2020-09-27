package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.jinyuanjava.litemall.db.service.LitemallPromotionGoodsRebateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goodsRebateOrder")
@Validated
@Api(description = "后台管理/营销管理/品项折扣活动订单:/admin/goodsRebateOrder")
public class AdminPromotionGoodsRebateOrderController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallPromotionGoodsRebateOrderService orderService;

    @Autowired
    private CommonDBService commonDBService;


    @RequiresPermissions("admin:goodsRebateOrder:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(Integer ruleId,
                             String goodsSn,String goodsName,String orderSn,
                             String username, String userPhone,String userNickname,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewPromotionGoodsRebateRuleOrderGoods> rebateOrderList = orderService.getViewQueryUserGoodsOrders(
                ruleId,goodsSn,goodsName,orderSn, username,userPhone,userNickname,orderStatusArray,page, limit, sort);

        return ResponseUtil.okList(rebateOrderList);
    }

    /**
     * 只显示订单主单
     * @param ruleId
     * @param name
     * @param goodsSn
     * @param goodsName
     * @param orderSn
     * @param orderStatusArray
     * @param username
     * @param page
     * @param limit
     * @param sort
     * @return
     */
    @GetMapping("/listMainOrder")
    public Object listMainOrder(Integer ruleId, String name,
                                String goodsSn,String goodsName,String orderSn,
                                @RequestParam(required = false) List<Short> orderStatusArray,
                                String username,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer limit,
                                @RequestParam(defaultValue = "add_time") String sort
    ) {
        String strSql="select sql_calc_found_rows distinct order_id as orderId, order_Sn as orderSn,order_status_name as orderStatusName,actual_price as actualPrice,"+
                "pay_time as payTime,ship_sn as shipSn,ship_channel as shipChannel from view_promotion_goods_rebate_rule_order_goods where 1=1 ";
        String strCondition="";
        if(!StringUtils.isEmpty(ruleId)){
            strCondition+=" and rule_id="+ruleId;
        }
        if(!StringUtils.isEmpty(name)){
            strCondition+=" and name like '%"+name+"%'";
        }
        if(!StringUtils.isEmpty(goodsSn)){
            strCondition+=" and goods_sn='"+goodsSn+"'";
        }
        if(!StringUtils.isEmpty(goodsName)){
            strCondition+=" and goods_name like '%"+goodsName+"%'";
        }
        if(!StringUtils.isEmpty(orderSn)){
            strCondition+=" and goods_name='"+orderSn+"'";
        }
        if(!StringUtils.isEmpty(orderStatusArray)&&orderStatusArray.size()>0){
            strCondition+=" and order_status in ("+commonDBService.listToShort(orderStatusArray) +")" ;
        }
        if(!StringUtils.isEmpty(username)){
            strCondition+=" and username like '%"+username +"%'" ;
        }
        strSql=strSql+strCondition;
        if(limit<999999){
            int fromIndex = (page-1) * limit;
            strSql=strSql+ " limit " + fromIndex + "," + limit;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        Integer count=(Integer) param.get("total");

        return ResponseUtil.okList(result,count,page,limit);
    }

    private Object validate(LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        Integer orderId = goodsRebateOrderGoods.getOrderId();
        if (orderId == null) {
            return ResponseUtil.fail(401,"保存品项折扣订单时订单ID不能为空");
        }
        Integer rulesId = goodsRebateOrderGoods.getRuleId();
        if (rulesId == null) {
            return ResponseUtil.fail(401,"保存品项折扣订单时规则ID不能为空");
        }
        Integer userId = goodsRebateOrderGoods.getProductId();
        if (userId == null) {
            return ResponseUtil.fail(401,"保存品项折扣订单时商品ID不能为空");
        }
        return null;
    }

    @RequiresPermissions("admin:goodsRebateOrder:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动订单"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        Object error = validate(goodsRebateOrderGoods);
        if (error != null) {
            return error;
        }
        if (orderService.updateById(goodsRebateOrderGoods) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:goodsRebateOrder:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动订单"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        Object error = validate(goodsRebateOrderGoods);
        if (error != null) {
            return error;
        }

        orderService.createGoodsRebateOrderGoods(goodsRebateOrderGoods);

        return ResponseUtil.ok(goodsRebateOrderGoods);
    }

    @RequiresPermissions("admin:goodsRebateOrder:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "品项折扣活动订单"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionGoodsRebateOrderGoods goodsRebateOrderGoods) {
        Integer id = goodsRebateOrderGoods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        orderService.deleteById(id);
        return ResponseUtil.ok();
    }

}
