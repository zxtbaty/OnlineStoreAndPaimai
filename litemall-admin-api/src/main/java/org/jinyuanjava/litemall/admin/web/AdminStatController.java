package org.jinyuanjava.litemall.admin.web;

import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.vo.StatVo;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.ViewLitemallGoodsProductMapper;
import org.jinyuanjava.litemall.db.domain.ViewLitemallGoodsProduct;
import org.jinyuanjava.litemall.db.domain.ViewLitemallGoodsProductExample;
import org.jinyuanjava.litemall.db.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/stat")
@Validated
@Api(description = "后台管理/统计报表/用户统计:/admin/stat")
public class AdminStatController {
    private final Log logger = LogFactory.getLog(AdminStatController.class);

    @Autowired
    private StatService statService;

    @Resource
    private ViewLitemallGoodsProductMapper viewLitemallGoodsProductMapper;


    @RequiresPermissions("admin:stat:user")
    @RequiresPermissionsDesc(menu={"统计报表" , "用户统计"}, button="查询")
    @GetMapping("/user")
    public Object statUser(String beginDate,String endDate) {
        List<Map> rows = statService.statUser(beginDate,endDate);
        String[] columns = new String[]{"day", "users"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

    @RequiresPermissions("admin:stat:order")
    @RequiresPermissionsDesc(menu={"统计报表" , "订单统计"}, button="查询")
    @GetMapping("/order")
    public Object statOrder(String beginDate,String endDate) {
        List<Map> rows = statService.statOrder(beginDate,endDate);
        String[] columns = new String[]{"day", "orders", "customers", "actualPrice", "pcr"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);

        return ResponseUtil.ok(statVo);
    }

    @RequiresPermissions("admin:stat:goods")
    @RequiresPermissionsDesc(menu={"统计报表" , "商品统计"}, button="查询")
    @GetMapping("/goods")
    public Object statGoods(String beginDate,String endDate) {
        List<Map> rows = statService.statGoods(beginDate,endDate);
        String[] columns = new String[]{"day", "orders", "productSum", "productCount","sumMoney"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

    @RequiresPermissions("admin:stat:browse")
    @RequiresPermissionsDesc(menu={"统计报表" , "用户浏览情况"}, button="查询")
    @GetMapping("/browse")
    public Object statBrowse(String beginDate,String endDate) {
        List<Map> rows = statService.statBrowse(beginDate,endDate);
        String[] columns = new String[]{"day", "cusVisitCount", "goodsVisitCount","huiYuanVisitCount","pageVisitCount"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

//    @RequiresPermissions("admin:stat:homelinechart")
    @RequiresPermissionsDesc(menu={"统计报表" , "首页折线图"}, button="首页折线图")
    @GetMapping("/homelinechart")
    public Object statHomeLineChart(String beginDate,String endDate) {
        List<Map> rows = statService.statUserGoodsOrder(beginDate,endDate);
        String[] columns = new String[]{"日期", "新增用户量", "新增商品品种", "订单量","收款金额","订单商品数量合计","用户访问量","会员用户访问量","页面访问量","商品访问量"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

    @RequiresPermissionsDesc(menu={"统计报表" , "综合报表"}, button="数据查询表")
    @GetMapping("/zongHe")
    public Object statZongHe(String beginDate,String endDate) {
        List<Map> rows = statService.statUserGoodsOrder(beginDate,endDate);
        String[] columns = new String[]{"日期", "新增用户量", "新增商品品种", "订单量","收款金额","订单商品数量合计","用户访问量","会员用户访问量","页面访问量","商品访问量"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return ResponseUtil.ok(statVo);
    }

    @RequiresPermissions("admin:stat:warningquery")
    @RequiresPermissionsDesc(menu={"统计报表" , "库存报警"}, button="报警查询")
    @GetMapping("/warningquery")
    public Object statWarningQuery(
            Integer comId,Integer brandId,Integer categoryId,
            String goodsSn,String name,String goodsPosKey,
            Integer minAmount,Integer maxAmount,Integer isOnSale,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "add_time asc") String sort
    ) {
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        example.setOrderByClause(sort);
        ViewLitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        if(brandId!=null){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(categoryId!=null){
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
//        if(!StringUtils.isEmpty(specifications)){
//            criteria.andSpecificationsLike("%"+specifications+"%");
//        }
        if(!StringUtils.isEmpty(goodsPosKey)){
            criteria.andGoodsPosKeyEqualTo(goodsPosKey);
        }
        if(minAmount!=null){
            criteria.andNumberLessThanOrEqualTo(minAmount);
        }
        if(maxAmount!=null){
            criteria.andNumberGreaterThanOrEqualTo(maxAmount);
        }
        if(isOnSale!=null){
            if(isOnSale==0) {
                criteria.andIsOnSaleEqualTo(false);
            } else if(isOnSale==1) {
                criteria.andIsOnSaleEqualTo(true);
            }
        }
        criteria.andNumberLessThanOrEqualToColumn(ViewLitemallGoodsProduct.Column.minStorenum);
        PageHelper.startPage(page,limit);
        List<ViewLitemallGoodsProduct> viewLitemallGoodsProducts= viewLitemallGoodsProductMapper.selectByExample(example);
        return ResponseUtil.okList(viewLitemallGoodsProducts);
    }

    @RequiresPermissions("admin:stat:storequery")
    @RequiresPermissionsDesc(menu={"统计报表" , "库存报警"}, button="库存查询")
    @GetMapping("/storequery")
    public Object statStoreQuery(
            Integer comId,Integer brandId,Integer categoryId,
            String goodsSn,String name,String goodsPosKey,
            Integer minAmount,Integer maxAmount,Integer isOnSale,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "add_time asc") String sort
    ) {
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        example.setOrderByClause(sort);
        ViewLitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        if(brandId!=null){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(categoryId!=null){
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
//        if(!StringUtils.isEmpty(specifications)){
//            criteria.andSpecificationsLike("%"+specifications+"%");
//        }
        if(!StringUtils.isEmpty(goodsPosKey)){
            criteria.andGoodsPosKeyEqualTo(goodsPosKey);
        }
        if(minAmount!=null){
            criteria.andNumberLessThanOrEqualTo(minAmount);
        }
        if(maxAmount!=null){
            criteria.andNumberGreaterThanOrEqualTo(maxAmount);
        }
        if(isOnSale!=null){
            if(isOnSale==0) {
                criteria.andIsOnSaleEqualTo(false);
            } else if(isOnSale==1) {
                criteria.andIsOnSaleEqualTo(true);
            }
        }
        //criteria.andNumberLessThanOrEqualToColumn(ViewLitemallGoodsProduct.Column.minStorenum);
        PageHelper.startPage(page,limit);
        List<ViewLitemallGoodsProduct> viewLitemallGoodsProducts= viewLitemallGoodsProductMapper.selectByExample(example);
        return ResponseUtil.okList(viewLitemallGoodsProducts);
    }
}
