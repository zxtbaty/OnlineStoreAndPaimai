package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.GoodsAllinone;
import org.jinyuanjava.litemall.admin.vo.CatVo;
import org.jinyuanjava.litemall.core.qcode.QCodeService;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.GOODS_NAME_EXIST;

@Service
public class AdminGoodsService {
    private final Log logger = LogFactory.getLog(AdminGoodsService.class);

    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsSpecificationService specificationService;
    @Autowired
    private LitemallGoodsAttributeService attributeService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallCategoryService categoryService;
    @Autowired
    private LitemallBrandService brandService;

    @Autowired
    private LitemallGoodsStoreService goodsStoreService;

    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallCompanyBrandService companyBrandService;

    @Autowired
    private LitemallCartService litemallCartService;


    @Autowired
    private QCodeService qCodeService;

    @Autowired
    private LitemallPrivateMakeRuleService privateMakeRuleService;

    @Autowired
    private LitemallGrouponRulesService litemallGrouponRulesService;

    @Autowired
    private LitemallPromotionSeckillRuleService seckillRuleService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService;


    @Autowired
    private LitemallAuctionZhuanchangRuleCurrentService zhuanchangRuleCurrentService;

    @Autowired
    private LitemallAuctionDajiaRuleCurrentService dajiaRuleCurrentService;



    public Object list(Integer comId,Integer storeId, Integer brandId,Integer categoryId ,Integer isOnSale,
                       String goodsSn, String name, String storeNum,Integer ifXuni,
                       List<String> usedRange, Integer authorId,Integer dajiapaiCategoryId ,Integer privateCategoryId,
                       Integer page, Integer limit, String sort) {
        List<LitemallGoods> goodsList = goodsService.querySelective(comId,storeId,brandId,
                categoryId,isOnSale,goodsSn, name,storeNum,ifXuni,usedRange,authorId,
                dajiapaiCategoryId,privateCategoryId, page, limit, sort);
        return ResponseUtil.okList(goodsList);
    }


    private Object validate(GoodsAllinone goodsAllinone) {
        LitemallGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StringUtils.isEmpty(name)) {
            //return ResponseUtil.badArgument();
            return ResponseUtil.fail(401,"商品名称不能为空");
        }
        String goodsSn = goods.getGoodsSn();
        if (StringUtils.isEmpty(goodsSn)) {
            return ResponseUtil.fail(401,"商品编号不能为空");
            //return ResponseUtil.badArgument();
        }
        // 品牌商可以不设置，如果设置则需要验证品牌商存在
        Integer brandId = goods.getBrandId();
        if (brandId != null && brandId != 0) {
            if (brandService.findById(brandId) == null) {
                return ResponseUtil.fail(401,"商品品牌ID不能为空");
                //return ResponseUtil.badArgumentValue();
            }
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                //return ResponseUtil.badArgumentValue();
                return ResponseUtil.fail(401,"商品分类不能为空");
            }
        }
        //商品属性
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (LitemallGoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品参数名称不能为空");
            }
            String value = attribute.getValue();
            if (StringUtils.isEmpty(value)) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品参数值不能为空");
            }
        }
        //商品规格
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (LitemallGoodsSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StringUtils.isEmpty(spec)) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品规格名称不能为空");
            }
            String value = specification.getValue();
            if (StringUtils.isEmpty(value)) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品规格值不能为空");
            }
        }
       //商品产品
        LitemallGoodsProduct[] products = goodsAllinone.getProducts();
        for (LitemallGoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品明细库存数量不能为空");
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                //return ResponseUtil.badArgument();
                return ResponseUtil.fail(401,"商品明细库存单价不能为空");
            }

            String[] productSpecifications = product.getSpecifications();
            if(productSpecifications!=null) {
                if (productSpecifications.length == 0) {
                    //return ResponseUtil.badArgument();
                    return ResponseUtil.fail(401, "商品明细条数必须大于0");
                }
            }
        }

        return null;
    }

    /**
     * 编辑商品
     * <p>
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新litemall_goods表
     * 2. 逻辑删除litemall_goods_specification、litemall_goods_attribute、litemall_goods_product
     * 3. 添加litemall_goods_specification、litemall_goods_attribute、litemall_goods_product
     * <p>
     * 这里商品三个表的数据采用删除再添加的策略是因为
     * 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。
     * 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
     * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。
     * 所以这里可能需要重新设计。
     */
    @Transactional
    public Object update(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        //保存后的规格型号表
        List<LitemallGoodsSpecification> saveAfterSpecifications = new ArrayList<>();
        LitemallGoodsProduct[] products = goodsAllinone.getProducts();
        //LitemallGoodsStore[] stores=goodsAllinone.getStores();

//        Integer id = goods.getId();
        //如果更新的商品下架，则删除购物车商品
        if(goods.getIsOnSale().equals(false)){
            //商品下架 则删除购物车商品
            litemallCartService.deleteByGoodsId(goods.getId());
        }

        //将生成的分享图片地址写入数据库
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        goods.setShareUrl(url);

        // 商品基本信息表litemall_goods
        if(goods.getId()==null){
            goodsService.add(goods);
        } else {
            if (goodsService.updateById(goods) == 0) {
                throw new RuntimeException("更新数据失败");
            }
        }

//        Integer gid = goods.getId();
//        specificationService.deleteByGid(gid);
//        attributeService.deleteByGid(gid);
//        productService.deleteByGid(gid);

        // 商品规格表litemall_goods_specification
        for (LitemallGoodsSpecification specification : specifications) {

            specification.setGoodsId(goods.getId());
            if(specification.getId()==null) {
                specificationService.add(specification);
            } else
            {
                specificationService.update(specification);
            }
            saveAfterSpecifications.add(specification);
        }

        // 商品参数表litemall_goods_attribute
        for (LitemallGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            if(attribute.getId()==null){
                attributeService.add(attribute);
            } else
            {
                attributeService.update(attribute);
            }
        }
        // 商品货品表litemall_product
        //取最小的产品规格id
        Integer minId=0;
        for (LitemallGoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            //处理规格表
            String[] specStringArray=  product.getSpecifications();
            String[] specId=new String[specStringArray.length];
            Integer ii=0;
            for(String specString:specStringArray){
                Optional<Integer>  findId= saveAfterSpecifications.stream().filter((item)->item.getValue().equals(specString)).
                        map(LitemallGoodsSpecification::getId).findFirst();
                if(findId.isPresent()){
                    specId[ii]=findId.get().toString();
                }
                ii++;
            }
            product.setSpecificationIds(specId);

            //当修改库存数量时，应该同步修改可用数量
            product.setRemainNumber( CharUtil.objectConverToInteger(product.getNumber())-
                    CharUtil.objectConverToInteger(product.getYuyueNumber()));
            if(product.getId()==null||product.getId()==0) {
                productService.add(product);
                if(minId==0){
                    minId=product.getId();
                } else
                if(product.getId()<minId){
                    minId=product.getId();
                }
            } else
            {
                productService.update(product);
                if(minId==0){
                    minId=product.getId();
                } else
                if(product.getId()<minId){
                    minId=product.getId();
                }
            }

        }
        //更新商品单价为默认规格单价 及默认规格Id
        if(minId!=0){
           LitemallGoodsProduct  product=  productService.findById(minId);
           goods.setPriceDesc(product.getPriceDesc());
           if(product.getPrice()!=goods.getRetailPrice()) {
               goods.setRetailPrice(product.getPrice());
               goods.setCounterPrice(product.getPrice());
               goods.setDefaultProductId(product.getId());
               goodsService.updateById(goods);
           }

        }
        //更新私人定制规则中的出品人信息及商品信息
        List<LitemallPrivateMakeRule> privateMakeRules= privateMakeRuleService.findByGoodsId(goods.getId());
        for(LitemallPrivateMakeRule privateMakeRule:privateMakeRules){
            if(!privateMakeRule.getAuthorId().equals(goods.getAuthorId())){
                privateMakeRule.setAuthorId(goods.getAuthorId());
                privateMakeRule.setAuthorName(goods.getAuthorName());
                privateMakeRuleService.updateById(privateMakeRule);
            }
        }

        //更新大家拍中的商品信息

        //更新专场拍中的商品明细信息


//        //商品店铺
//        for (LitemallGoodsStore goodsStore : stores) {
//            goodsStore.setGoodsId(goods.getId());
//            if(goodsStore.getId()==null) {
//                goodsStoreService.add(goodsStore);
//            } else
//            {
//                goodsStoreService.update(goodsStore);
//            }
//        }
        goodsService.updateStoreBiggerZeroOrNot(goods.getId());
        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(LitemallGoods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        specificationService.deleteByGid(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        //保存后的规格型号表
        List<LitemallGoodsSpecification> saveAfterSpecifications = new ArrayList<>();
        LitemallGoodsProduct[] products = goodsAllinone.getProducts();
        //LitemallGoodsStore[] stores=goodsAllinone.getStores();

        String name = goods.getName();
        if (goodsService.checkExistByName(name,goods.getComId())) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "【"+goods.getComName()+"】商品名【"+name+"】已经存在");
        }

        // 商品基本信息表litemall_goods
        goodsService.add(goods);

        //将生成的分享图片地址写入数据库
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        if (!StringUtils.isEmpty(url)) {
            goods.setShareUrl(url);
            if (goodsService.updateById(goods) == 0) {
                throw new RuntimeException("更新数据失败");
            }
        }

        // 商品规格表litemall_goods_specification
        for (LitemallGoodsSpecification specification : specifications) {
            specification.setId(null);
            specification.setGoodsId(goods.getId());
            specificationService.add(specification);
            saveAfterSpecifications.add(specification);
        }

        // 商品参数表litemall_goods_attribute
        for (LitemallGoodsAttribute attribute : attributes) {
            attribute.setId(null);
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // 商品货品表litemall_product
        Boolean firstP=true;
        for (LitemallGoodsProduct product : products) {
            product.setId(null);
            product.setGoodsId(goods.getId());
            //处理规格表
            String[] specStringArray=  product.getSpecifications();
            String[] specId=new String[specStringArray.length];
            Integer ii=0;
            for(String specString:specStringArray){
                Optional<Integer>  findId= saveAfterSpecifications.stream().filter((item)->item.getValue().equals(specString)).
                        map(LitemallGoodsSpecification::getId).findFirst();
                if(findId.isPresent()){
                    specId[ii]=findId.get().toString();
                }
                ii++;
            }
            product.setSpecificationIds(specId);
            product.setRemainNumber(product.getNumber());
            product.setYuyueNumber(0);

            productService.add(product);

            //跟新商品表中的默认单价为默认规格的单价
            //2019.11.05 设置原价和默认单价相等
            if(firstP==true){
                goods.setPriceDesc(product.getPriceDesc());
                goods.setRetailPrice(product.getPrice());
                goods.setCounterPrice(product.getPrice());
                goods.setDefaultProductId(product.getId());
                goodsService.updateById(goods);
            }
            firstP=false;
        }

        goodsService.updateStoreBiggerZeroOrNot(goods.getId());


//        // 商品店铺表litemall_store
//        for (LitemallGoodsStore store : stores) {
//            store.setGoodsId(goods.getId());
//            goodsStoreService.add(store);
//        }
        return ResponseUtil.ok();
    }

    public Object list2(Integer comId) {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader

        // 管理员设置“所属分类”
        List<LitemallCategory> l1CatList = categoryService.queryL1(comId);
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (LitemallCategory l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<LitemallCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (LitemallCategory l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            if(children.size()>0) {
                l1CatVo.setChildren(children);
            }

            categoryList.add(l1CatVo);
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // 管理员设置“所属品牌商”
        List<Integer> brandIdList=null;

        List<LitemallCompanyBrand> companyBrands= companyBrandService.queryByComId(comId);
        brandIdList=companyBrands.stream().map(LitemallCompanyBrand::getBrandId).collect(Collectors.toList());

        List<LitemallBrand> list =null;
        List<Map<String, Object>> brandList = new ArrayList<>();
        if(brandIdList==null||brandIdList.size()==0){
            //未选择商贸类型或者该商贸类型下的品牌列表为空
            //list = brandService.all();
        } else
        {
            list = brandService.queryByIds(brandIdList);
            for (LitemallBrand brand : list) {
                Map<String, Object> b = new HashMap<>(2);
                b.put("value", brand.getId());
                b.put("label", brand.getName());
                brandList.add(b);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        data.put("brandList", brandList);
        return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        LitemallGoods goods = goodsService.findById(id);
        List<LitemallGoodsProduct> products = productService.queryByGid(id);
        List<LitemallGoodsSpecification> specifications = specificationService.queryByGid(id);
        List<LitemallGoodsAttribute> attributes = attributeService.queryByGid(id);
//        List<LitemallGoodsStore> stores=goodsStoreService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        LitemallCategory category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);
        //data.put("stores", stores);

        return ResponseUtil.ok(data);
    }


    public Object getGoodsProduct(@NotNull Integer id) {
        List<LitemallGoodsProduct> goodsProducts=productService.queryByGid(id);

        return ResponseUtil.ok(goodsProducts);
    }

    public Object getGoodsProductById(@NotNull Integer id) {
       LitemallGoodsProduct  goodsProducts=productService.findById(id);

       return ResponseUtil.ok(goodsProducts);
    }

    public Boolean updateGoodsProduct(LitemallGoodsProduct goodsProduct){
        //因为只更新几个字段，所以取到原商品信息后，再重新赋值更新
        LitemallGoodsProduct goodsStore=productService.findById(goodsProduct.getId());
        goodsStore.setNumber(goodsProduct.getNumber());
        goodsStore.setMinStorenum(goodsProduct.getMinStorenum());
        //当修改库存数量时，应该同步修改可用数量
        goodsStore.setRemainNumber( CharUtil.objectConverToInteger(goodsStore.getNumber())-
                CharUtil.objectConverToInteger(goodsStore.getYuyueNumber()));
        productService.update(goodsStore);
        goodsService.updateStoreBiggerZeroOrNot(goodsStore.getGoodsId());
        return true;
    }


    public String getHaveExistsPromotion(Integer goodsId,Integer productId){
        String strError="";

        //判断商品是否在团购中存在,判断商品是否存在某个已经开始但还没有结束的团购活动中,如果在则返回团购规则
        List<LitemallGrouponRules> grouponRulesList= litemallGrouponRulesService.queryGoodsProductIdIsInGroupon(goodsId,productId);
        if(grouponRulesList!=null&&grouponRulesList.size()>0){
            strError+="商品在以下正在进行中的团购活动中出现:";
            for(LitemallGrouponRules grouponRules:grouponRulesList){
                strError+="【"+grouponRules.getName()+"】,";
            }
        }
        //判断商品是否在秒杀活动中出现
        List<LitemallPromotionSeckillRule> seckillRuleList= seckillRuleService.queryGoodsProductIdIsInSkill(goodsId,productId);
        if(seckillRuleList!=null&&seckillRuleList.size()>0){
            strError+="商品在以下正在进行中的秒杀活动中出现:";
            for(LitemallPromotionSeckillRule seckillRule:seckillRuleList){
                strError+="【"+seckillRule.getSeckillName()+"】,";
            }
        }

        //判断商品是否在品项折扣中出现
        List<ViewPromotionGoodsRebateRuleGoods> goodsRebateRuleGoodsList= goodsRebateRuleService.getRuleByGoodsProductId(goodsId,productId);
        if(goodsRebateRuleGoodsList!=null&&goodsRebateRuleGoodsList.size()>0){
            strError+="商品在以下正在进行中的品项折扣活动中出现:";
            for(ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods:goodsRebateRuleGoodsList){
                strError+="【"+goodsRebateRuleGoods.getRuleName()+"】,";
            }
        }

        //判断商品是否在会员活动中出现
        List<ViewPromotionGoodsDetail> viewPromotionGoodsDetailList= promotionGoodsRuleService.getRuleByGoodsProductId(goodsId,productId);
        if(viewPromotionGoodsDetailList!=null&&viewPromotionGoodsDetailList.size()>0){
            strError+="商品在以下正在进行中的会员活动中出现:";
            for(ViewPromotionGoodsDetail viewPromotionGoodsDetail:viewPromotionGoodsDetailList){
                strError+="【"+viewPromotionGoodsDetail.getName()+"】,";
            }
        }

        //判断商品是否在专场活动中出现
        List<ViewAuctionZhuanchangGoodsCurrent> zhuanchangGoodsCurrentList= zhuanchangRuleCurrentService.getRuleByGoodsProductId(goodsId,productId);
        if(zhuanchangGoodsCurrentList!=null&&zhuanchangGoodsCurrentList.size()>0){
            strError+="商品在以下正在进行中的专场拍活动中出现:";
            for(ViewAuctionZhuanchangGoodsCurrent zhuanchangGoodsCurrent:zhuanchangGoodsCurrentList){
                strError+="【"+zhuanchangGoodsCurrent.getZhuanchangName()+"】,";
            }
        }
        //判断商品是否在大家拍活动中出现
        List<LitemallAuctionDajiaRuleCurrent> dajiaRuleCurrentList= dajiaRuleCurrentService.getRuleByGoodsProductId(goodsId,productId);
        if(dajiaRuleCurrentList!=null&&dajiaRuleCurrentList.size()>0){
            strError+="商品在以下正在进行中的大家拍活动中出现:";
            for(LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent:dajiaRuleCurrentList){
                strError+="【"+dajiaRuleCurrent.getGoodsName()+"】,";
            }
        }

        //判断商品是否在私人定制活动中出现
        List<LitemallPrivateMakeRule> privateMakeRuleList= privateMakeRuleService.getRuleByGoodsProductId(goodsId,productId);
        if(privateMakeRuleList!=null&&privateMakeRuleList.size()>0){
            strError+="商品在以下正在进行中的私人定制活动中出现:";
            for(LitemallPrivateMakeRule privateMakeRule:privateMakeRuleList){
                strError+="【"+privateMakeRule.getGoodsName()+"】,";
            }
        }
        if(!StringUtils.isEmpty(strError)){
            strError=strError.substring(0,strError.length()-1);
        }

        return strError;

    }


}
