package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.service.GetRegionService;
import org.jinyuanjava.litemall.wx.service.WxOrderService;
import org.jinyuanjava.litemall.wx.vo.cartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import static org.jinyuanjava.litemall.wx.util.WxResponseCode.GOODS_NO_STOCK;
import static org.jinyuanjava.litemall.wx.util.WxResponseCode.GOODS_UNSHELVE;

/**
 * 用户购物车服务
 */
@RestController
@RequestMapping("/wx/cart")
@Validated
@Api(description = "微信前端/购物车:/wx/cart")
public class WxCartController {
    private final Log logger = LogFactory.getLog(WxCartController.class);

    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private CouponVerifyService couponVerifyService;
    @Autowired
    private LitemallStoreService storeService;
    @Autowired
    private GetRegionService getRegionService;

    @Autowired
    private LitemallPromotionGoodsRuleService goodsRuleService;
    @Autowired
    private LitemallPromotionSeckillRuleService seckillRuleService;
    @Autowired
    private LitemallGrouponRulesService getGrouponRulesService;

    @Autowired
    private WxOrderService wxOrderService;

    @Autowired
    private LitemallPickSiteService litemallPickSiteService;

    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("index")
    public Object index(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallCart> cartList = cartService.queryByUid(userId);
        if(cartList==null||cartList.size()==0){
            return ResponseUtil.ok();
        }
        List<LitemallCart> shopList = new ArrayList<>();
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);

        List<LitemallCart> shouduList = new ArrayList<>();
        Integer shoudugoodsCount = 0;
        BigDecimal shoudugoodsAmount = new BigDecimal(0.00);
        Integer shouducheckedGoodsCount = 0;
        BigDecimal shouducheckedGoodsAmount = new BigDecimal(0.00);

        List<LitemallCart> daxingList = new ArrayList<>();
        Integer daxinggoodsCount = 0;
        BigDecimal daxinggoodsAmount = new BigDecimal(0.00);
        Integer daxingcheckedGoodsCount = 0;
        BigDecimal daxingcheckedGoodsAmount = new BigDecimal(0.00);
        List<Integer> productIds = new ArrayList<>();
        for (LitemallCart cart : cartList) {
        	cartVo cartVo=null;
        	Integer comId = cart.getComId();
        	Integer goodsId = cart.getGoodsId();
        	Integer productId = cart.getProductId();
        	LitemallGoods goods = goodsService.findById(goodsId);
        	LitemallGoodsProduct product = productService.findById(productId);

            //2020.08.17 张相涛增加，获取用户的活动价格，更新成活动价
            BigDecimal activePrice= productService.getGoodsPrice(userId,productId);
            cart.setPrice(activePrice);
            //更新购物车单价
            cartService.updateById(cart);

        	if(product.getNumber()<=0&&cart.getChecked()){
        		productIds.add(productId);
        		cart.setChecked(false);
        	}
        	if(goods.getIsOnSale()){
	        	switch (comId) {
				case 1:
		            goodsCount += cart.getNumber();
		            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            if (cart.getChecked()) {
		                checkedGoodsCount += cart.getNumber();
		                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            }
		            cartVo = new cartVo(cart,product.getNumber());
		            shopList.add(cartVo);

					break;
				case 2://首都
					shoudugoodsCount += cart.getNumber();
		            shoudugoodsAmount = shoudugoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            if (cart.getChecked()) {
		            	shouducheckedGoodsCount += cart.getNumber();
		            	shouducheckedGoodsAmount = shouducheckedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            }
		             cartVo = new cartVo(cart,product.getNumber());
		            shouduList.add(cartVo);
					break;
				case 3://大兴
					daxinggoodsCount += cart.getNumber();
					daxinggoodsAmount = daxinggoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            if (cart.getChecked()) {
		            	daxingcheckedGoodsCount += cart.getNumber();
		            	daxingcheckedGoodsAmount = daxingcheckedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
		            }
		            cartVo = new cartVo(cart,product.getNumber());
		            daxingList.add(cartVo);
					break;
				default:
					break;
				}
        	}

        }

        if(userId!=null && userId>0&&productIds!=null && productIds.size()>0){
        	cartService.updateCheck(userId, productIds, false);
        }

        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> shouduTotal = new HashMap<>();
        shouduTotal.put("goodsCount", shoudugoodsCount);
        shouduTotal.put("goodsAmount", shoudugoodsAmount);
        shouduTotal.put("checkedGoodsCount", shouducheckedGoodsCount);
        shouduTotal.put("checkedGoodsAmount", shouducheckedGoodsAmount);

        Map<String, Object> daxingTotal = new HashMap<>();
        daxingTotal.put("goodsCount", daxinggoodsCount);
        daxingTotal.put("goodsAmount", daxinggoodsAmount);
        daxingTotal.put("checkedGoodsCount", daxingcheckedGoodsCount);
        daxingTotal.put("checkedGoodsAmount", daxingcheckedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", shopList);
        result.put("cartTotal", cartTotal);
        result.put("shouduList", shouduList);
        result.put("shouduTotal", shouduTotal);
        result.put("daxingList", daxingList);
        result.put("daxingTotal", daxingTotal);
        return ResponseUtil.ok(result);
    }


    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("cartNumber")
    public Object cartNumber(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.ok(0);
        }
        Map<String, Object> result =(Map<String, Object>) ((Map<String, Object>) this.index(userId)).get("data");
        int shoudugoodsCount;
		int daxinggoodsCount;
		int cartgoodsCount;
		try {
			 List<LitemallCart> shouduList= ( List<LitemallCart>) result.get("shouduList");
			 List<LitemallCart> daxingList= ( List<LitemallCart>) result.get("daxingList");
			 List<LitemallCart> cartList= ( List<LitemallCart>) result.get("cartList");
			shoudugoodsCount = shouduList==null?0:shouduList.size();
			daxinggoodsCount = daxingList==null?0:daxingList.size();
			cartgoodsCount = cartList==null?0:cartList.size();
			 return ResponseUtil.ok(cartgoodsCount+daxinggoodsCount+shoudugoodsCount);
		} catch (Exception e) {
			  return ResponseUtil.ok(0);
		}


    }

    /**
     * 加入商品到购物车
     * <p>
     * 如果已经存在购物车货品，则增加数量	；
     * 否则添加新的购物车货品项。
     *
     * @param userId 用户ID
     * @return 加入购物车操作结果
     */
    @PostMapping("add")
    public Object add(@LoginUser Integer userId, Integer gid,
                      Integer pid,Short count,BigDecimal price,
                      Integer ruleId,String ruleType) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (gid == null || pid==null || count ==null||price==null) {
            return ResponseUtil.fail(502,"商品ID、产品ID、购买数量、购买单价都不能为空");
        }

        LitemallCart cart=new LitemallCart();
        cart.setGoodsId(gid);
        cart.setProductId(pid);
        cart.setNumber(count);
        cart.setPrice(price);
        //秒杀商品 团购商品 会员商品 折扣商品 正常商品
        if(ruleType!=null) {
            if (ruleType.equals("秒杀商品")) {
                cart.setSecRuleId(ruleId);
            } else if (ruleType.equals("团购商品")) {
                cart.setGrouponRuleId(ruleId);
            } else if (ruleType.equals("会员商品")) {
                cart.setUserGoodsRuleId(ruleId);
            } else if (ruleType.equals("折扣商品")) {
                cart.setRebateRuleId(ruleId);
            }
        }

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
//            return ResponseUtil.badArgument();
            return ResponseUtil.fail(502,"商品ID、产品ID、购买数量、购买单价都不能为空");
        }
        if(number <= 0){
            return ResponseUtil.fail(502,"购买数量必须大于0");
        }

        //判断商品是否可以购买
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
        }

        LitemallGoodsProduct product = productService.findById(productId);
        //判断购物车中是否存在此规格商品
        LitemallCart existCart = cartService.queryExist(goodsId, productId, userId);
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
            }

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(price);
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cart.setComId(goods.getComId());
            cart.setStoreId(goods.getStoreId());
            cart.setStoreName(goods.getStoreName());
            cartService.add(cart);
        } else {
            //取得规格的信息,判断规格库存
            int num = existCart.getNumber() + number;
            if (num > product.getNumber()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
            }
            existCart.setNumber((short) num);
            existCart.setPrice(price);
            if (cartService.updateById(existCart) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }

        return goodscount(userId);
    }

    /**
     * 立即购买
     * <p>
     * 和add方法的区别在于：
     * 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖
     * 2. 添加成功以后，前者的逻辑是返回当前购物车商品数量，这里的逻辑是返回对应购物车项的ID
     *
     * @param userId 用户ID
     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 立即购买操作结果
     */
    @PostMapping("fastadd")
    public Object fastadd(@LoginUser Integer userId, @RequestBody LitemallCart cart) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return ResponseUtil.badArgument();
        }
        if(number <= 0){
            return ResponseUtil.badArgument();
        }

        //判断商品是否可以购买
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
        }

        LitemallGoodsProduct product = productService.findById(productId);
        //判断购物车中是否存在此规格商品
        LitemallCart existCart = cartService.queryExist(goodsId, productId, userId);
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
            }

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            //取得规格的信息,判断规格库存
            int num = number;
            if (num > product.getNumber()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
            }
            existCart.setNumber((short) num);
            if (cartService.updateById(existCart) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }

        return ResponseUtil.ok(existCart != null ? existCart.getId() : cart.getId());
    }

    /**
     * 修改购物车商品货品数量
     *
     * @param userId 用户ID
     * @return 修改结果
     */
    @PostMapping("update")
    public Object update(@LoginUser Integer userId, Integer id,Short number) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        if (cart == null) {
//            return ResponseUtil.badArgument();
//        }
//        Integer productId = cart.getProductId();
//        Integer number = cart.getNumber().intValue();
//        Integer goodsId = cart.getGoodsId();
//        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id,  number)) {
            return ResponseUtil.badArgument();
        }
        if(number <= 0){
            return ResponseUtil.badArgument();
        }

        //判断是否存在该订单
        // 如果不存在，直接返回错误
        LitemallCart existCart = cartService.findById(id);
        if (existCart == null) {
            return ResponseUtil.badArgumentValue();
        }

        if(!existCart.getUserId().equals(userId)){
        	ResponseUtil.badArgumentValue();
        }

        Integer productId = existCart.getProductId();
        Integer goodsId = existCart.getGoodsId();

        // 判断goodsId和productId是否与当前cart里的值一致
        if (!existCart.getGoodsId().equals(goodsId)) {
            return ResponseUtil.badArgumentValue();
        }
        if (!existCart.getProductId().equals(productId)) {
            return ResponseUtil.badArgumentValue();
        }

        //判断商品是否可以购买
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
        }

        //取得规格的信息,判断规格库存
        LitemallGoodsProduct product = productService.findById(productId);
        if (product == null || product.getNumber() < number) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "库存不足");
        }

        existCart.setNumber(number.shortValue());
        existCart.setId(id);
        int updateById = cartService.updateById(existCart);
        if (updateById==0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 购物车商品货品勾选状态
     * <p>
     * 如果原来没有勾选，则设置勾选状态；如果商品已经勾选，则设置非勾选状态。
     *
     * @param userId 用户ID
     * @return 购物车信息
     */
    @PostMapping("checked")
    public Object checked(@LoginUser Integer userId, String ids, Integer checkValue) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (ids == null || ids.trim()=="") {
            return ResponseUtil.badArgument();
        }
        String[] split = ids.split(",");
        List<Integer> productIds = new ArrayList<>();
        for (String idStr : split) {
        	productIds.add(Integer.parseInt(idStr));
		}

//        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
        if (productIds.size()==0) {
            return ResponseUtil.badArgument();
        }

//        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return ResponseUtil.badArgument();
        }
        Boolean isChecked = (checkValue == 1);

        cartService.updateCheck(userId, productIds, isChecked);
        return index(userId);
    }

    /**
     * 购物车商品删除
     *
     * @param userId 用户ID
     * @return 购物车信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId,  HttpServletRequest request) {
    	if (userId == null) {
            return ResponseUtil.unlogin();
        }
    	String parameter = request.getParameter("productIds");
        if (StringUtil.isEmpty(parameter)) {
            return ResponseUtil.badArgument();
        }
        String[] idsStr = parameter.split(",");
//        List<Integer> productIds = JacksonUtil.parseIntegerList(parameter, "productIds");
        List<Integer> productIds = new ArrayList<>();
        for (String str : idsStr) {
        	try {
				productIds.add(Integer.parseInt(str));
			} catch (NumberFormatException e) {
			}
		}

        if (productIds == null || productIds.size() == 0) {
            return ResponseUtil.badArgument();
        }

        cartService.delete(productIds, userId);
        return index(userId);
    }

    /**
     * 清空购物车商品
     *
     * @param userId 用户ID
     * @return 购物车信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("clear")
    public Object clearAll(@LoginUser Integer userId) {
    	if (userId == null) {
            return ResponseUtil.unlogin();
        }
        cartService.clearGoods(userId);
        return  ResponseUtil.ok(0);
    }

    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("goodscount")
    public Object goodscount(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.ok(0);
        }

        int goodsCount = 0;
        List<LitemallCart> cartList = cartService.queryByUid(userId);
        for (LitemallCart cart : cartList) {
            goodsCount += cart.getNumber();
        }

        return ResponseUtil.ok(goodsCount);
    }


    /**
     * 购物车下单NEW
     *
     * @param userId    用户ID
     *                  如果购物车商品ID是空，则下单当前用户所有购物车商品；
     *                  如果购物车商品ID非空，则只下单当前购物车商品。
     * @param addressId 收货地址ID：
     *                  如果收货地址ID是空，则查询当前用户的默认地址。
     * @return 购物车操作结果
     */
    @PostMapping("checkoutinfo")
    public Object newCheckout(@LoginUser Integer userId,Integer addressId,Integer goodId,Short number,BigDecimal price,String cartIds
                              ) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallRegion> regionList = getRegionService.getLitemallRegions();
        // 收货地址
        LitemallAddress checkedAddress = null;
        if (addressId == null || addressId.equals(0)) {
            checkedAddress = addressService.findDefault(userId);
            // 如果仍然没有地址，则是没有收获地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new LitemallAddress();
                checkedAddress.setId(0);
                addressId = 0;
            } else {
                addressId = checkedAddress.getId();
            }

        } else {
            checkedAddress = addressService.query(userId, addressId);
            // 如果null, 则报错
            if (checkedAddress == null) {
                return ResponseUtil.badArgumentValue();
            }
        }
        //判断是否有商品在免运费范围内
        Boolean ifFreePost=false;
        // 商品价格
        List<LitemallCart> checkedGoodsList = null;
        if(goodId !=null && goodId >0){
        	LitemallGoodsProduct goodsProduct = productService.findById(goodId);
        	Integer goodsId = goodsProduct.getGoodsId();
        	LitemallGoods goods = goodsService.findById(goodsId);

        	//虚拟商品免运费
        	if(goods.getIfXuni()){
                ifFreePost=true;
            }

        	LitemallCart cart = new LitemallCart();
//        	cart.setPrice(goodsProduct.getPrice().multiply(new BigDecimal(number)));
//            cart.setPrice(price.multiply(new BigDecimal(number)));

            cart.setPrice(price );
        	cart.setGoodsId(goodsId);
        	cart.setGoodsName(goods.getName());
        	cart.setGoodsSn(goods.getGoodsSn());
        	cart.setNumber(number);
        	cart.setPicUrl(goods.getPicUrl());
        	cart.setProductId(goodId);
        	cart.setUserId(userId);
        	cart.setSpecifications(goodsProduct.getSpecifications());
//        	cartService.add(cart);
        	checkedGoodsList=new ArrayList<>();
        	checkedGoodsList.add(cart);
        }else{
        	 if (cartIds == null || cartIds.equals(0)) {
                 checkedGoodsList = cartService.queryByUidAndChecked(userId);
             } else {

             	String[] split = cartIds.split(",");
             	for (String cartIdStr : split) {
             		try {
     					int cartId = Integer.parseInt(cartIdStr);
     					LitemallCart cart = cartService.findById(cartId);
     					if (cart == null) {
     					    return ResponseUtil.badArgumentValue();
     					}
     					if(checkedGoodsList==null){
     						checkedGoodsList = new ArrayList<>();
     					}
     					checkedGoodsList.add(cart);
     				} catch (NumberFormatException e) {
     					return ResponseUtil.badArgumentValue();
     				}
     			}

             }
        }

        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);

        Integer freeGoodsCount=0;

        for (LitemallCart cart : checkedGoodsList) {
            //购物车单价乘以数量，得到购物车中的商品总价
            checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            //判断该规型号商品是否在免邮活动中，如果存在，则返回true
            // 如果是一个商品判断单商品是事免邮，如果是多商品，只要有一个不免邮，则整单不免邮
            if(ifFreePost==false) {
                ifFreePost = checkGoodsIdIfFreePost(cart.getProductId());
                if (ifFreePost) {
                    freeGoodsCount++;
                }
            }
        }
        if(ifFreePost==false) {
            if (freeGoodsCount == checkedGoodsList.size()) {
                ifFreePost = true;
            } else {
                ifFreePost = false;
            }
        }


        // 计算优惠券可用情况
        BigDecimal tmpCouponPrice = new BigDecimal(0.00);
        Integer tmpCouponId = 0;
        int tmpCouponLength = 0;
        List<LitemallCouponUser> couponUserList = couponUserService.queryAll(userId);
        for(LitemallCouponUser couponUser : couponUserList){
            LitemallCoupon coupon = couponVerifyService.checkCoupon(userId, couponUser.getCouponId(), checkedGoodsPrice);
            if(coupon == null){
                continue;
            }

            tmpCouponLength++;
            if(tmpCouponPrice.compareTo(coupon.getDiscount()) == -1){
                tmpCouponPrice = coupon.getDiscount();
                tmpCouponId = coupon.getId();
            }
        }
//
//        // 获取优惠券减免金额，优惠券可用数量
//        int availableCouponLength = tmpCouponLength;
//        BigDecimal couponPrice = new BigDecimal(0);
//        Integer couponId = -1;
//        // 这里存在三种情况
//        // 1. 用户不想使用优惠券，则不处理
//        // 2. 用户想自动使用优惠券，则选择合适优惠券
//        // 3. 用户已选择优惠券，则测试优惠券是否合适
//        if (couponId == null || couponId.equals(-1)){
//            couponId = -1;
//        }
//        else if (couponId.equals(0)) {
//            couponPrice = tmpCouponPrice;
//            couponId = tmpCouponId;
//        }
//        else {
//            LitemallCoupon coupon = couponVerifyService.checkCoupon(userId, couponId, checkedGoodsPrice);
//            // 用户选择的优惠券有问题，则选择合适优惠券，否则使用用户选择的优惠券
//            if(coupon == null){
//                couponPrice = tmpCouponPrice;
//                couponId = tmpCouponId;
//            }
//            else {
//                couponPrice = coupon.getDiscount();
//            }
//        }
        // 根据订单商品总价计算运费，满199则免运费，否则市内10元，市外18元；
        BigDecimal freightPrice = new BigDecimal(0.00);
        BigDecimal freightLimit = SystemConfig.getFreightLimit();
        if (checkedGoodsPrice.compareTo(freightLimit) < 0 && checkedAddress.getId()>0) {
            //判断该商品是否在促销活动免运费规则当中 判断规则是，只要有商品在免运费服务范围内，则整单免运费

            if(ifFreePost){
                freightPrice=BigDecimal.ZERO;
            }
            else {

                if (checkedAddress.getProvince().equals("1")) {
                    //北京市内运费
                    freightPrice = SystemConfig.getBeiJingFreight();
                } else {
                    //北京市外运费
                    freightPrice = SystemConfig.getOtherCityFreight();
                }
            }
        }
        // 用户积分兑换
        BigDecimal integralPrice = new BigDecimal(0.00);

        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice);
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        Map<String, Object> data = new HashMap<>();
        data.put("addressId", addressId);
//        data.put("couponId", couponId);
        data.put("checkedAddress", checkedAddress);
//        data.put("availableCouponLength", availableCouponLength);
        data.put("goodsTotalPrice", checkedGoodsPrice);
        data.put("freightPrice", freightPrice);
//        data.put("couponPrice", couponPrice);
        data.put("orderTotalPrice", orderTotalPrice);
        data.put("actualPrice", actualPrice);
        data.put("checkedGoodsList", checkedGoodsList);
        data.put("regionList", regionList);

        //取提货点列表的第一个时间点
        LitemallPickSite firstPickSite=litemallPickSiteService.findFist();
        ArrayList al=null;
        //自提货的起始日期
        if(firstPickSite!=null){
        	al=   wxOrderService.getIihuoTime(null,firstPickSite.getSitePickTime()[0],firstPickSite.getSitePickTime()[1]);
        }
        if(al!=null){
            data.put("startTime",al.get(0));
            data.put("endTime",al.get(1));
            data.put("startHour",firstPickSite.getSitePickTime()[0]);
            data.put("endHour",firstPickSite.getSitePickTime()[1]);
        } else
        {
            data.put("startTime","");
            data.put("endTime","");
            data.put("startHour","");
            data.put("endHour","");
        }

        return ResponseUtil.ok(data);
    }

    //判断某个商品是否在免除运费的商品范围之内，如果在，则返回True，如果不存在，则返回False
    private Boolean checkGoodsIdIfFreePost(Integer productId){
        //会员活动是否免邮
       Boolean ifExistsGoodsRule=  goodsRuleService.checkExistsByGoodsIdAndFreePost(productId);
       if(ifExistsGoodsRule==true){
              return true;
       }
      //秒杀活动是否免邮
        Boolean ifExistsSeckillRule=  seckillRuleService.checkGoodsIsInSkillAndFreePost(productId);
        if(ifExistsSeckillRule==true){
            return true;
        }
        //团购活动是否免邮
        Boolean ifExistsGroupOnRule=  getGrouponRulesService.checkGoodsIsInGroupOnAndFreePost(productId);
        if(ifExistsGroupOnRule==true){
            return true;
        }
        return false;
    }


    /**
     * 预约下单NEW
     *
     * @param userId    用户ID
     * @return 购物车操作结果
     */
    @PostMapping("recomandcheckoutinfo")
    public Object recomandCheckout(@LoginUser Integer userId,Integer goodId,Short number,String cartIds,Integer comId) {
        if (userId == null|| comId==null) {
            return ResponseUtil.unlogin();
        }
        List result=new ArrayList<>();

        // 商品价格
        List<LitemallCart> checkedGoodsList = null;

        //获取预约门店
        LitemallStore yuyueStore=null;

        if(goodId !=null && goodId >0){
        	LitemallGoodsProduct goodsProduct = productService.findById(goodId);
        	Integer goodsId = goodsProduct.getGoodsId();
        	LitemallGoods goods = goodsService.findById(goodsId);

        	//商品上挂的是主店铺，为了支持一品多铺的情况，在产品上可以设置多个店铺信息
        	Integer[] productStoreIds= goodsProduct.getStoreIds();
            List<Integer> listStoreIds=new ArrayList<>();
        	if(productStoreIds!=null&&productStoreIds.length>0) {
                listStoreIds = Arrays.asList(productStoreIds);
            }

        	LitemallCart cart = new LitemallCart();
        	cart.setPrice(goodsProduct.getPrice());
        	cart.setGoodsId(goodsId);
        	cart.setGoodsName(goods.getName());
        	cart.setGoodsSn(goods.getGoodsSn());
        	cart.setNumber(number);
        	cart.setPicUrl(goods.getPicUrl());
        	cart.setProductId(goodId);
        	cart.setUserId(userId);
        	//处理一个商品有多个店铺的情况 如果一品多铺
            Integer storeId=null;
            if(goods.getStoreId()!=null) {
                storeId=goods.getStoreId();
                //cart.setStoreId(goods.getStoreId());
            } else if(listStoreIds.size()>0){
                storeId=listStoreIds.get(0);
                //cart.setStoreId(listStoreIds.get(0));
            }
            cart.setStoreId(storeId);
            if(storeId!=null) {
                yuyueStore=storeService.findById(storeId);
                cart.setStoreName(yuyueStore.getName());
            }
        	cart.setSpecifications(goodsProduct.getSpecifications());
//        	cartService.add(cart);
        	checkedGoodsList=new ArrayList<>();
        	checkedGoodsList.add(cart);

        }else{
        	 if (cartIds == null || cartIds.equals(0)) {
                 checkedGoodsList = cartService.queryByUidAndChecked(userId);
             } else {
             	String[] split = cartIds.split(",");
             	for (String cartIdStr : split) {
             		try {
     					int cartId = Integer.parseInt(cartIdStr);
     					LitemallCart cart = cartService.findById(cartId);
     					if (cart == null) {
                            return  ResponseUtil.fail(502, "购物车不能为空");
     					}
     					if(checkedGoodsList==null){
     						checkedGoodsList = new ArrayList<>();
     					}
     					checkedGoodsList.add(cart);
     				} catch (NumberFormatException e) {
     					return ResponseUtil.badArgumentValue();
     				}
     			}

             }
        }

        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        //生成产品ID集合
        List<Integer> productIds= checkedGoodsList.stream().map(LitemallCart::getProductId).distinct().collect(Collectors.toList());
        //根据产品ID集合查询出产品表中的所有记录
        List<LitemallGoodsProduct> goodsProducts=productService.findByIdList(productIds);

        List<LitemallGoods> allGoodsList=goodsService.queryAll();

        List<LitemallStore> allStoreList= storeService.queryAll();

        for (LitemallCart cart : checkedGoodsList) {
          checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
          //从集合中中查询出商品信息，要比单条循环速度快
          Optional<LitemallGoodsProduct> findProduct= goodsProducts.stream().filter(item->item.getId().equals(cart.getProductId())).findFirst();
          //全部加入到列表中
          List<Integer> listStoreIds=new ArrayList<>();
          ArrayList temp=new ArrayList();
          LitemallGoods litemallGoods=allGoodsList.stream().filter(item->item.getId().equals(findProduct.get().getGoodsId())).findFirst().get();

//          2020.01.15修改，根据实际情况，一个商品目前只支持一家预约门店，直接从商品基本信息表里关联出来
         LitemallStore defaultStore= allStoreList.stream().filter(item->item.getId().equals(litemallGoods.getStoreId())).findFirst().get();
         cart.setStoreId(litemallGoods.getStoreId());
         List<LitemallStore> storeList= new ArrayList<>();
         storeList.add(defaultStore);
//          Integer[] productStoreIds= findProduct.get().getStoreIds();
//          if(productStoreIds!=null&&productStoreIds.length>0) {
//              listStoreIds.addAll(Arrays.asList(productStoreIds));
//          }
//          Integer storeId=cart.getStoreId();
//          if(cart.getStoreId()!=null) {
//               listStoreIds.add(storeId);
//          }
//          //列表去重
//         List<Integer> distinctStoreIds=listStoreIds.stream().distinct().collect(Collectors.toList());
//          //查询符合要求的所有店铺 预约商品必须至少设置一个店铺
//         List<LitemallStore> storeList= storeService.findByIds(distinctStoreIds);
//
//         if(cart.getStoreId()==null){
//             cart.setStoreId(storeList.get(0).getId());
//         }
//         LitemallStore defaultStore= storeList.stream().filter(item->item.getId().equals(cart.getStoreId())).findFirst().get();


         yuyueStore=defaultStore;
         Map<String,Object> map = new HashMap<>();
         map.put("cart", cart);
         map.put("storeList", storeList);
         map.put("defaultStore", defaultStore);
         map.put("orderTotalPrice", cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
         map.put("orderTotalPriceReal", cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
         map.put("integralPrice", 0);
         map.put("integralBonus", 0);

         result.add(map);
        }
        //集成线上线下的所有优惠券和积分信息 此信息与CRM打通，直接取CRM里的信息
        //线上预定不涉及到运费信息
        //各种计算的金额在前台完成

        Map<String, Object> data = new HashMap<>();
        data.put("goodsTotalPrice", checkedGoodsPrice);
        data.put("checkedGoodsList", result);

        //预约单的起始日期

        data.put("startTime","");
        data.put("endTime","");
        data.put("startHour","");
        data.put("endHour","");


        return ResponseUtil.ok(data);
    }




    /**
     * 购物车下单
     *
     * @param userId    用户ID
     * @param cartId    购物车商品ID：
     *                  如果购物车商品ID是空，则下单当前用户所有购物车商品；
     *                  如果购物车商品ID非空，则只下单当前购物车商品。
     * @param addressId 收货地址ID：
     *                  如果收货地址ID是空，则查询当前用户的默认地址。
     * @param couponId  优惠券ID：
     *                  如果优惠券ID是空，则自动选择合适的优惠券。
     * @return 购物车操作结果
     */
    @GetMapping("checkout")
    public Object checkout(@LoginUser Integer userId, Integer cartId, Integer addressId, Integer couponId, Integer grouponRulesId,String productJsonArr) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        // 收货地址
        LitemallAddress checkedAddress = null;
        if (addressId == null || addressId.equals(0)) {
            checkedAddress = addressService.findDefault(userId);
            // 如果仍然没有地址，则是没有收获地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new LitemallAddress();
                checkedAddress.setId(0);
                addressId = 0;
            } else {
                addressId = checkedAddress.getId();
            }

        } else {
            checkedAddress = addressService.query(userId, addressId);
            // 如果null, 则报错
            if (checkedAddress == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        // 团购优惠
        BigDecimal grouponPrice = new BigDecimal(0.00);
        LitemallGrouponRules grouponRules = grouponRulesService.queryById(grouponRulesId);
        if (grouponRules != null) {
            grouponPrice = grouponRules.getGrouponPrice();
        }

        // 商品价格
        List<LitemallCart> checkedGoodsList = null;
        if (cartId == null || cartId.equals(0)) {
            checkedGoodsList = cartService.queryByUidAndChecked(userId);
        } else {
            LitemallCart cart = cartService.findById(cartId);
            if (cart == null) {
                return ResponseUtil.badArgumentValue();
            }
            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);
        }
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (LitemallCart cart : checkedGoodsList) {
            //  只有当团购规格商品ID符合才进行团购优惠
            if (grouponRules != null && grouponRules.getGoodsId().equals(cart.getGoodsId())) {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().subtract(grouponPrice).multiply(new BigDecimal(cart.getNumber())));
            } else {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }

        // 计算优惠券可用情况
        BigDecimal tmpCouponPrice = new BigDecimal(0.00);
        Integer tmpCouponId = 0;
        int tmpCouponLength = 0;
        List<LitemallCouponUser> couponUserList = couponUserService.queryAll(userId);
        for(LitemallCouponUser couponUser : couponUserList){
            LitemallCoupon coupon = couponVerifyService.checkCoupon(userId, couponUser.getCouponId(), checkedGoodsPrice);
            if(coupon == null){
                continue;
            }

            tmpCouponLength++;
            if(tmpCouponPrice.compareTo(coupon.getDiscount()) == -1){
                tmpCouponPrice = coupon.getDiscount();
                tmpCouponId = coupon.getId();
            }
        }
        // 获取优惠券减免金额，优惠券可用数量
        int availableCouponLength = tmpCouponLength;
        BigDecimal couponPrice = new BigDecimal(0);
        // 这里存在三种情况
        // 1. 用户不想使用优惠券，则不处理
        // 2. 用户想自动使用优惠券，则选择合适优惠券
        // 3. 用户已选择优惠券，则测试优惠券是否合适
        if (couponId == null || couponId.equals(-1)){
            couponId = -1;
        }
        else if (couponId.equals(0)) {
            couponPrice = tmpCouponPrice;
            couponId = tmpCouponId;
        }
        else {
            LitemallCoupon coupon = couponVerifyService.checkCoupon(userId, couponId, checkedGoodsPrice);
            // 用户选择的优惠券有问题，则选择合适优惠券，否则使用用户选择的优惠券
            if(coupon == null){
                couponPrice = tmpCouponPrice;
                couponId = tmpCouponId;
            }
            else {
                couponPrice = coupon.getDiscount();
            }
        }

        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        BigDecimal freightPrice = new BigDecimal(0.00);
        if (checkedGoodsPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }

        // 可以使用的其他钱，例如用户积分
        BigDecimal integralPrice = new BigDecimal(0.00);

        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        Map<String, Object> data = new HashMap<>();
        data.put("addressId", addressId);
        data.put("couponId", couponId);
        data.put("cartId", cartId);
        data.put("grouponRulesId", grouponRulesId);
        data.put("grouponPrice", grouponPrice);
        data.put("checkedAddress", checkedAddress);
        data.put("availableCouponLength", availableCouponLength);
        data.put("goodsTotalPrice", checkedGoodsPrice);
        data.put("freightPrice", freightPrice);
        data.put("couponPrice", couponPrice);
        data.put("orderTotalPrice", orderTotalPrice);
        data.put("actualPrice", actualPrice);
        data.put("checkedGoodsList", checkedGoodsList);
        return ResponseUtil.ok(data);
    }
}
