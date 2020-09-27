package org.jinyuanjava.litemall.admin.web;

import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.dto.GoodsAllinone;
import org.jinyuanjava.litemall.admin.service.AdminGoodsService;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/goods")
@Validated
@Api(description = "后台管理/商品管理/商品管理:/admin/goods")
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private AdminGoodsService adminGoodsService;

    @Autowired
    private LitemallGoodsService litemallGoodsService;

    @Autowired
    private LitemallPromotionSeckillRuleService promotionSeckillRuleService;

    @Autowired
    private LitemallGrouponRulesService grouponRulesService;

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;


    @Resource
    private ViewHomepageRecommendEcMapper homepageRecommendEcMapper;

    @Resource
    private ViewListRecommendEcMapper listRecommendEcMapper;

    @Resource
    private ViewHomepageRecommendSeckillMapper homepageRecommendSeckillMapper;
    @Resource
    private ViewHomepageRecommendGrouponMapper homepageRecommendGrouponMapper;

    @Resource
    private ViewListRecommendSeckillMapper listRecommendSeckillMapper;
    @Resource
    private ViewListRecommendGrouponMapper listRecommendGrouponMapper;

    @Resource
    private ViewLitemallGoodsProductMapper viewLitemallGoodsProductMapper;

    /**
     * 查询商品
     *
     * @param goodsSn
     * @param name
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    @RequiresPermissions("admin:goods:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String posType,Integer comId, Integer storeId,Integer brandId,
                       Integer categoryId,Integer isOnSale, String goodsSn, String name,
                       String storeNum,Integer ifXuni,
                       @RequestParam(required = false) List<String> usedRange,
                       Integer authorId,Integer dajiapaiCategoryId ,Integer privateCategoryId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        String strUsedRangeType="";
        if(posType==null) {
            return adminGoodsService.list(comId,storeId,brandId,categoryId,isOnSale, goodsSn, name,
                    storeNum,ifXuni,usedRange,authorId,dajiapaiCategoryId,privateCategoryId,page, limit, sort);
        } else
        {
            if(posType.equals("首页秒杀")){
                comId=1;
                return getHomepageRecommendSeckill(posType,usedRange,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            } else  if(posType.equals("列表秒杀")){
                comId=1;
                 return getListRecommendSeckill(posType,usedRange,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            } else  if(posType.equals("首页团购")){
                comId=1;
                 return getHomepageRecommendGroupon(posType,usedRange,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            } else  if(posType.equals("列表团购")){
                comId=1;
                 return getListRecommendGroupon(posType,usedRange,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            } else  if(posType.equals("首页特产")){
                comId=1;
                 return getHomepageRecommendEc(posType, usedRange,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            } else  if(posType.equals("列表特产")){
                comId=1;
                return getListRecommendEc(posType,comId,storeId,isOnSale,goodsSn,name,page,limit,sort);
            }
        }
        return null;
    }


    @GetMapping("/listGoodsByIdList")
    public Object listGoodsByIdList(@RequestParam(required = false)  List<Integer> ids) {
        List<LitemallGoods> goodsList= litemallGoodsService.querySelectiveIds(ids,1,999999,null);
        return ResponseUtil.okList(goodsList);

    }

    @GetMapping("/listProductByIdList")
    public Object listProductByIdList(@RequestParam(required = false)  List<Integer> ids) {
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        example.or().andIdIn(ids);
        return ResponseUtil.okList(viewLitemallGoodsProductMapper.selectByExample(example));


    }


    /**
     * 查询商品规格明细 为了商品折扣查询，去除在团购、秒杀、会员商品中正在执行的
     *
     * @param goodsSn
     * @param name
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    @RequiresPermissions("admin:goods:listGoodsProduct")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品货品管理"}, button = "查询")
    @GetMapping("/listGoodsProduct")
    public Object listGoodsProduct(Integer comId, Integer brandId,

                                   Integer categoryId,Integer isOnSale, String goodsSn, String name,
                                   Boolean ifNotIncludeHuodong,
                                   @RequestParam(required = false) List<String> usedRange,
                                   Integer authorId,Integer dajiapaiCategoryId ,Integer privateCategoryId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam(defaultValue = "add_time")  String sort ){
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        ViewLitemallGoodsProductExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(comId)){
            criteria.andComIdEqualTo(comId);
        }
        if(!StringUtils.isEmpty(brandId)){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(!StringUtils.isEmpty(categoryId)){
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if(!StringUtils.isEmpty(isOnSale)){
            if(isOnSale==1){
                criteria.andIsOnSaleEqualTo(true);
            } else
            {
                criteria.andIsOnSaleEqualTo(false);
            }
        }
        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!StringUtils.isEmpty(usedRange)){
            criteria.andUsedRangeIn(usedRange);
        }
        if(!StringUtils.isEmpty(authorId)){
            criteria.andAuthorIdEqualTo(authorId);
        }
        if(!StringUtils.isEmpty(dajiapaiCategoryId)){
            criteria.andDajiapaiCategoryIdEqualTo(dajiapaiCategoryId);
        }
        if(!StringUtils.isEmpty(privateCategoryId)){
            criteria.andPrivateCategoryIdEqualTo(privateCategoryId);
        }
        if(ifNotIncludeHuodong!=null&&ifNotIncludeHuodong==true) {
            //计算所有秒杀活动中的商品列表
            List<Integer> procedingSeckillListIds = promotionSeckillRuleService.queryAllProductIdList();
            if (procedingSeckillListIds != null && procedingSeckillListIds.size() > 0) {
                criteria.andIdNotIn(procedingSeckillListIds);
            }
            //计算所有团购活动中的商品列表
            List<Integer> procedingGroupListIds = grouponRulesService.queryAllProductIdList();
            if (procedingGroupListIds != null && procedingGroupListIds.size() > 0) {
                criteria.andIdNotIn(procedingGroupListIds);
            }
            //计算所有会员活动中的商品列表
            List<Integer> procedingGoodsRuleListIds = promotionGoodsRuleService.queryAllProductIdList();
            if (procedingGoodsRuleListIds != null && procedingGoodsRuleListIds.size() > 0) {
                criteria.andIdNotIn(procedingGoodsRuleListIds);
            }
            //计算所有品项折扣活动中的商品列表
            List<Integer> procedingRebateRuleListIds = goodsRebateRuleService.queryAllProductIdList();
            if (procedingRebateRuleListIds != null && procedingRebateRuleListIds.size() > 0) {
                criteria.andIdNotIn(procedingRebateRuleListIds);
            }
        }
        if(limit!=null&&limit<999999) {
            PageHelper.startPage(page, limit);
        }
        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        List<ViewLitemallGoodsProduct> result=viewLitemallGoodsProductMapper.selectByExample(example);

        return ResponseUtil.okList(result);
    }

    @GetMapping("/catAndBrand")
    public Object list2(Integer comId) {
        return adminGoodsService.list2(comId);
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:update")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {
        LitemallGoods goods=goodsAllinone.getGoods();
        if(goods.getId()==null){
            return adminGoodsService.create(goodsAllinone);
        } else {
            return adminGoodsService.update(goodsAllinone);
        }
    }

    /**
     * 更新商品库存
     * @param goodsProduct
     * @return
     */
    @RequiresPermissions("admin:goods:updateStore")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品库存更新"}, button = "更新")
    @PostMapping("/updateStore")
    public Object updateStore(@RequestBody LitemallGoodsProduct goodsProduct) {
       Boolean result= adminGoodsService.updateGoodsProduct(goodsProduct);
       return ResponseUtil.ok(result);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @RequiresPermissions("admin:goods:delete")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGoods goods) {

        //判断商品是否在所有活动中已经存在，如果已经存在，则提示用户先取消活动
       String strError= adminGoodsService.getHaveExistsPromotion(goods.getId(),null);
       if(!StringUtils.isEmpty(strError)){
           return ResponseUtil.fail(502,strError);
       }
      //返回删除信息
      return adminGoodsService.delete(goods);
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:create")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.create(goodsAllinone);
    }

    /**
     * 商品主表信息，主要是为了查询商品主表信息用
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:goodsmain")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "商品主表")
    @GetMapping("/goodsmain")
    public Object getGoodsmain(@NotNull Integer id) {

        LitemallGoods litemallGoods= litemallGoodsService.findById(id);
        return ResponseUtil.ok(litemallGoods);

    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:read")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminGoodsService.detail(id);

    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:goodsProduct")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "规格详情列表")
    @GetMapping("/getGoodsProduct")
    public Object getGoodsProduct(@NotNull Integer id) {
        //查询视图
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        example.or().andGoodsIdEqualTo(id);
        List<ViewLitemallGoodsProduct> goodsProducts= viewLitemallGoodsProductMapper.selectByExample(example);
        return ResponseUtil.okList(goodsProducts);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:goodsProductById")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "规格详情")
    @GetMapping("/goodsProductById")
    public Object getGoodsProductById(@NotNull Integer id) {

        return adminGoodsService.getGoodsProductById(id);

    }

    @GetMapping("/goodsProductViewById")
    public Object getGoodsProductViewById(@NotNull Integer id) {
        ViewLitemallGoodsProductExample example=new ViewLitemallGoodsProductExample();
        example.or().andIdEqualTo(id);
        return ResponseUtil.ok(viewLitemallGoodsProductMapper.selectOneByExample(example));

    }



    //*******************************************************************************
    //以下部分是处理推荐商品专用


    /**
     * 首页秒杀
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getHomepageRecommendSeckill(String posType,List<String> usedRange, Integer comId,
                                               Integer storeId,Integer isOnSale,String goodsSn, String name,
                                               Integer page, Integer limit, String sort)
    {
        ViewHomepageRecommendSeckillExample viewHomepageRecommendSeckillExample=new ViewHomepageRecommendSeckillExample();
        ViewHomepageRecommendSeckillExample.Criteria criteria=viewHomepageRecommendSeckillExample.createCriteria();
//        if(comId!=null){
//            criteria.andComIdEqualTo(comId);
//        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }

        //viewHomepageRecommendEcExample.or().andComidEqualTo(comId);
//        if(!StringUtils.isEmpty(sort)){
//            viewHomepageRecommendSeckillExample.setOrderByClause(sort+" "+order);
//        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(homepageRecommendSeckillMapper.selectByExample(viewHomepageRecommendSeckillExample));

    }
    /**
     * 列表秒杀
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getListRecommendSeckill(String posType, List<String> usedRange,Integer comId,
           Integer storeId,Integer isOnSale,String goodsSn, String name,
           Integer page, Integer limit, String sort)
    {
        ViewListRecommendSeckillExample viewListRecommendSeckillExample=new ViewListRecommendSeckillExample();
        ViewListRecommendSeckillExample.Criteria criteria=viewListRecommendSeckillExample.createCriteria();

//        if(comId!=null){
//            criteria.andComIdEqualTo(comId);
//        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }

        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(listRecommendSeckillMapper.selectByExample(viewListRecommendSeckillExample));

    }

    /**
     * 首页团购
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getHomepageRecommendGroupon(String posType, List<String> usedRange, Integer comId,
                                               Integer storeId,Integer isOnSale,String goodsSn, String name,
                                               Integer page, Integer limit, String sort)
    {
        ViewHomepageRecommendGrouponExample viewHomepageRecommendGrouponExample=new ViewHomepageRecommendGrouponExample();
        ViewHomepageRecommendGrouponExample.Criteria criteria=viewHomepageRecommendGrouponExample.createCriteria();
        //viewHomepageRecommendEcExample.or().andComidEqualTo(comId);
//        if(!StringUtils.isEmpty(sort)){
//            viewHomepageRecommendGrouponExample.setOrderByClause(sort+" "+order);
//        }
//        if(comId!=null){
//            criteria.andComIdEqualTo(comId);
//        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(homepageRecommendGrouponMapper.selectByExample(viewHomepageRecommendGrouponExample));

    }

    /**
     * 列表团购
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getListRecommendGroupon(String posType, List<String> usedRange, Integer comId,
                                           Integer storeId,Integer isOnSale,String goodsSn, String name,
                                           Integer page, Integer limit, String sort)
    {
        ViewListRecommendGrouponExample viewListRecommendGrouponExample=new ViewListRecommendGrouponExample();
        ViewListRecommendGrouponExample.Criteria criteria=viewListRecommendGrouponExample.createCriteria();
        //viewHomepageRecommendEcExample.or().andComidEqualTo(comId);
//        if(!StringUtils.isEmpty(sort)){
//            viewListRecommendGrouponExample.setOrderByClause(sort+" "+order);
//        }
//        if(comId!=null){
//            criteria.andComIdEqualTo(comId);
//        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(listRecommendGrouponMapper.selectByExample(viewListRecommendGrouponExample));

    }


    /**
     * 首页特产
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getHomepageRecommendEc(String posType, List<String> usedRange, Integer comId,
                                          Integer storeId,Integer isOnSale,String goodsSn, String name,
                                          Integer page, Integer limit, String sort)
    {
        ViewHomepageRecommendEcExample viewHomepageRecommendEcExample=new ViewHomepageRecommendEcExample();
        ViewHomepageRecommendEcExample.Criteria criteria=viewHomepageRecommendEcExample.createCriteria();
        //viewHomepageRecommendEcExample.or().andComidEqualTo(comId);
//        if(!StringUtils.isEmpty(sort)){
//            viewHomepageRecommendEcExample.setOrderByClause(sort+" "+order);
//        }
//        if(comId!=null){
//            criteria.andComIdEqualTo(comId);
//        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(homepageRecommendEcMapper.selectByExample(viewHomepageRecommendEcExample));

    }
    /**
     * 列表特产
     * @param posType
     * @param comId
     * @param page
     * @param limit
     * @param sort

     * @return
     */
    private Object getListRecommendEc(String posType, Integer comId,
                                      Integer storeId,Integer isOnSale,String goodsSn, String name,
                                      Integer page, Integer limit, String sort)
    {
        ViewListRecommendEcExample viewListRecommendEcExample=new ViewListRecommendEcExample();
        ViewListRecommendEcExample.Criteria criteria=viewListRecommendEcExample.createCriteria();
        //viewHomepageRecommendEcExample.or().andComidEqualTo(comId);
//        if(!StringUtils.isEmpty(sort)){
//            viewListRecommendEcExample.setOrderByClause(sort+" "+order);
//        }
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        if(isOnSale!=null){
            criteria.andIsOnSaleEqualTo(CharUtil.objectConverToByte(isOnSale));
        }
        if(goodsSn!=null){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(name!=null){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        PageHelper.startPage(page,limit);
        return ResponseUtil.okList(listRecommendEcMapper.selectByExample(viewListRecommendEcExample));

    }



}
