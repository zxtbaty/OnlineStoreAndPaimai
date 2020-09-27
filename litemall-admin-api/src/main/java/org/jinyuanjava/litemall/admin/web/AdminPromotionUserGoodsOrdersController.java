package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsUserOrder;
import org.jinyuanjava.litemall.db.domain.ViewUserGoodsOrders;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.jinyuanjava.litemall.db.service.LitemallPromotionGoodsUserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/userGoodsOrders")
@Validated
@Api(description = "后台管理/营销管理/会员专享活动订单:/admin/userGoodsOrders")
public class AdminPromotionUserGoodsOrdersController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallPromotionGoodsUserOrderService orderService;

    @Autowired
    private CommonDBService commonDBService;


    @RequiresPermissions("admin:userGoodsOrders:list")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动订单"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(Integer rulesId, String name,
                             String goodsSn,String goodsName,String orderSn,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             String username,String userNickname,String userPhone,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewUserGoodsOrders> goodsOrdersList = orderService.getViewQueryUserGoodsOrders(
                rulesId,name,goodsSn,goodsName,orderSn,orderStatusArray,username,userNickname,userPhone, page, limit, sort);
        return ResponseUtil.okList(goodsOrdersList);
    }

    /**
     * 只显示订单主单
     * @param rulesId
     * @param name
     * @param goodsSn
     * @param goodsName
     * @param orderSn
     * @param orderStatusArray
     * @param userNickname
     * @param userPhone
     * @param page
     * @param limit
     * @param sort
     * @return
     */
    @GetMapping("/listMainOrder")
    public Object listMainOrder(Integer rulesId, String name,
                             String goodsSn,String goodsName,String orderSn,
                             @RequestParam(required = false) List<Short> orderStatusArray,
                             String userNickname,String userPhone,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
    ) {
        String strSql="select sql_calc_found_rows distinct order_id as orderId,  order_Sn as orderSn,order_status_name as orderStatusName,actual_price as actualPrice,"+
                  "pay_time as payTime,ship_sn as shipSn,ship_channel as shipChannel from view_user_goods_orders where 1=1 ";
        String strCondition="";
        if(!StringUtils.isEmpty(rulesId)){
              strCondition+=" and rules_id="+rulesId;
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
        if(!StringUtils.isEmpty(userNickname)){
            strCondition+=" and user_nickname like '%"+userNickname +"%'" ;
        }
        if(!StringUtils.isEmpty(userPhone)){
            strCondition+=" and user_phone='"+userPhone +"'" ;
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

    private Object validate(LitemallPromotionGoodsUserOrder userGoodsOrder) {
        Integer orderId = userGoodsOrder.getOrderId();
        if (orderId == null) {
            return ResponseUtil.fail(401,"保存会员专享订单信息时订单ID不能为空");
        }
        Integer rulesId = userGoodsOrder.getRulesId();
        if (rulesId == null) {
            return ResponseUtil.fail(401,"保存会员专享订单信息时规则ID不能为空");
        }
        Integer userId = userGoodsOrder.getUserId();
        if (userId == null) {
            return ResponseUtil.fail(401,"保存会员专享订单信息时会员ID不能为空");
        }

        return null;
    }

    @RequiresPermissions("admin:userGoodsOrders:update")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动订单"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPromotionGoodsUserOrder userGoodsOrder) {
        Object error = validate(userGoodsOrder);
        if (error != null) {
            return error;
        }
        if (orderService.updateById(userGoodsOrder) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:userGoodsOrders:create")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动订单"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPromotionGoodsUserOrder userGoodsOrder) {
        Object error = validate(userGoodsOrder);
        if (error != null) {
            return error;
        }

        orderService.createUserGoodsRuleOrder(userGoodsOrder);

        return ResponseUtil.ok(userGoodsOrder);
    }

    @RequiresPermissions("admin:userGoodsOrders:delete")
    @RequiresPermissionsDesc(menu={"营销管理" , "会员专享活动订单"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPromotionGoodsUserOrder userGoodsOrder) {
        Integer id = userGoodsOrder.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        orderService.deleteById(id);
        return ResponseUtil.ok();
    }

}
