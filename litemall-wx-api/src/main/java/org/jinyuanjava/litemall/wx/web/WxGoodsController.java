package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.core.validator.Sort;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.util.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;



/**
 * 商品服务
 */
@RestController
@RequestMapping("/wx/goods")
@Validated
@Api(description = "微信前端/商品服务:/wx/goods")
public class WxGoodsController {
	private final Log logger = LogFactory.getLog(WxGoodsController.class);

	@Autowired
	private LitemallGoodsService goodsService;

	@Autowired
	private LitemallGoodsProductService productService;

	@Autowired
	private LitemallIssueService goodsIssueService;

	@Autowired
	private LitemallGoodsAttributeService goodsAttributeService;

	@Autowired
	private LitemallBrandService brandService;

	@Autowired
	private LitemallCommentService commentService;

	@Autowired
	private LitemallUserService userService;

	@Autowired
	private LitemallCollectService collectService;

	@Autowired
	private LitemallFootprintService footprintService;

	@Autowired
	private LitemallCategoryService categoryService;

	@Autowired
	private LitemallSearchHistoryService searchHistoryService;

	@Autowired
	private LitemallGoodsSpecificationService goodsSpecificationService;

	@Autowired
	private LitemallGrouponRulesService rulesService;

	@Autowired
	private LitemallPromotionSeckillRuleService litemallPromotionSeckillRuleService;


	@Autowired
	private LitemallPromotionGoodsRuleService litemallPromotionGoodsRuleService;

	@Autowired
	private LitemallGrouponRulesService litemallGrouponRulesService;

	@Autowired
	private LitemallPromotionGoodsRebateRuleService litemallPromotionGoodsRebateRuleService;

	@Autowired
	private LitemallGrouponOrderService litemallGrouponOrderService;

	@Autowired
	private LitemallGoodsProductService goodsProductService;




	private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

	private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

	public Map<String, Object>  getDetail(Integer userId,Integer id){
 			// 商品信息
			LitemallGoods info = goodsService.findById(id);
			String detail = info.getDetail();
			if(!StringUtils.isNullOrEmpty(detail)){
				detail = RegexUtil.addOrReplaceHtmlTag(detail, "img", "style", "width:100%");
			}
			info.setDetail(detail);
//		//是否参与秒杀
//		Callable<List<LitemallPromotionSeckillRule>> litemallPromotionSeckillRuleGoodstCallable = () ->
//				litemallPromotionSeckillRuleService.queryGoodsIsInSkill(id);
			// 商品属性
			Callable<List<LitemallGoodsAttribute>> goodsAttributeListestGoodstCallable = () -> goodsAttributeService.queryByGid(id);

			// 商品规格 返回的是定制的GoodsSpecificationVo
			Callable<Object> objectCallable = () -> goodsSpecificationService.getSpecificationVoList(id);

			// 商品规格对应的数量和价格
			Callable<List<LitemallGoodsProduct>> productListCallable = () -> productService.queryByGid(id);

			// 商品问题，这里是一些通用问题
			Callable<List> issueCallable = () -> goodsIssueService.query();

			// 商品品牌商
			Callable<LitemallBrand> brandCallable = ()->{
				Integer brandId = info.getBrandId();
				LitemallBrand brand;
				if (brandId == null) {
					brand = new LitemallBrand();
				} else {
					brand = brandService.findById(info.getBrandId());
				}
				return brand;
			};

			// 评论
			Callable<Map> commentsCallable = () -> {
				List<LitemallComment> comments = commentService.queryGoodsByGid(id, 0, 2);
				List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
				long commentCount = PageInfo.of(comments).getTotal();
				for (LitemallComment comment : comments) {
					Map<String, Object> c = new HashMap<>();
					c.put("id", comment.getId());
					c.put("addTime", comment.getAddTime());
					c.put("content", comment.getContent());
					LitemallUser user = userService.findById(comment.getUserId());
					c.put("nickname", user == null ? "" : user.getNickname());
					c.put("avatar", user == null ? "" : user.getAvatar());
					c.put("picList", comment.getPicUrls());
					commentsVo.add(c);
				}
				Map<String, Object> commentList = new HashMap<>();
				commentList.put("count", commentCount);
				commentList.put("data", commentsVo);
				return commentList;
			};

			//团购信息
			Callable<List> grouponRulesCallable = () ->rulesService.queryByGoodsId(id);

			// 用户收藏
			int userHasCollect = 0;
			if (userId != null) {
				userHasCollect = collectService.count(userId, id);
			}

			// 记录用户的足迹 异步处理 为了处理页面访问量，只要访问都会被记录
			if (userId != null) {
				executorService.execute(()->{
					LitemallFootprint footprint = new LitemallFootprint();
					LitemallUser userInfo =userService.findById(userId);
					footprint.setUserId(userId);
					footprint.setWxNickname(userInfo.getNickname());
					footprint.setWeixinOpenid(userInfo.getWeixinOpenid());
					footprint.setCrmId(userInfo.getCrmId());
					footprint.setGoodsId(id);
					footprint.setGoodsSn(info.getGoodsSn());
					footprint.setGoodsName(info.getName());
					footprintService.add(footprint);
//					LitemallFootprint f= footprintService.queryByUserIdAndGoodId(userId,id);
//					if(f!=null){
//						footprintService.update(f);
//					}else{
//						footprintService.add(footprint);
//					}

				});
			}
			//FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
//		FutureTask<List<LitemallPromotionSeckillRule>> litemallPromotionSeckillRuleTask = new FutureTask<>(litemallPromotionSeckillRuleGoodstCallable);
			FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);

		    FutureTask<List<LitemallGoodsAttribute>> goodsAttributeListestGoodstCallableTask = new FutureTask<>(goodsAttributeListestGoodstCallable);
			FutureTask<List<LitemallGoodsProduct>> productListCallableTask = new FutureTask<>(productListCallable);
			FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
			FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);
			FutureTask<LitemallBrand> brandCallableTask = new FutureTask<>(brandCallable);
			FutureTask<List> grouponRulesCallableTask = new FutureTask<>(grouponRulesCallable);

			//executorService.submit(goodsAttributeListTask);
//        executorService.submit(litemallPromotionSeckillRuleTask);
			executorService.submit(objectCallableTask);
		    executorService.submit(goodsAttributeListestGoodstCallableTask);
			executorService.submit(productListCallableTask);
			executorService.submit(issueCallableTask);
			executorService.submit(commentsCallableTsk);
			executorService.submit(brandCallableTask);
			executorService.submit(grouponRulesCallableTask);

			Map<String, Object> data = new HashMap<>();

			try {
//			List<LitemallPromotionSeckillRule> SeckillRuleList = litemallPromotionSeckillRuleTask.get();
//			List<LitemallGoodsProduct> list = productListCallableTask.get();
//			for (LitemallPromotionSeckillRule srule:SeckillRuleList) {
//				Boolean seckillOnlyOne = srule.getSeckillOnlyOne();
//				Boolean checkExistByUseridRuleid =false;
//				if(!seckillOnlyOne){
//					if(userId!=null || userId!=0){
//						//查询是是否参加过
//						checkExistByUseridRuleid = litemallPromotionSeckillOrderService.checkExistByUseridRuleid(userId, srule.getId());
//					}
//				}

//				Integer goodsProductId = srule.getGoodsProductId();
//				for (LitemallGoodsProduct goodsProduct : list) {
//					Integer pid = goodsProduct.getId();
//					if(pid.equals(goodsProductId) && srule.getSeckillStoreNum()>0 && !checkExistByUseridRuleid){
//						//是同一个货品且有库存
//						goodsProduct.setPrice(srule.getSeckillPrice());
//						goodsProduct.setNumber(srule.getSeckillStoreNum());
//					}
//				}
//			}

				//重新构造ProductList列表 获取商品订单
				List<LitemallGoodsProduct> productList= productListCallableTask.get();
				for(LitemallGoodsProduct product:productList)
				{
					product.setReturnIfOnlyUserBonus(false);
					product.setReturnAllowBuyGoods(false);
					product.setReturnRuleSourcePrice(info.getCounterPrice());
					//以下活动的优先级别是会员活动第一/秒杀第二/团购第三/品项折扣第四
					if(userId!=null){
						//判断该商品是否是会员商品，如果是则执行会员价
						LitemallPromotionGoodsDetail promotionGoodsDetail= litemallPromotionGoodsRuleService.getRuleByProductId(product.getId(),userId);
						if(promotionGoodsDetail!=null){
							product.setReturnType("会员商品");
							product.setReturnRuleId(promotionGoodsDetail.getRuleId());
							product.setReturnRuleNumber(promotionGoodsDetail.getRemainNum());
							product.setReturnRulePrice(promotionGoodsDetail.getHuiYuanPrice());
//							//是否只支持积分购买
//							product.setReturnIfOnlyUserBonus(promotionGoodsDetail.getIfOnlyUseBonus());
//							product.setReturnIfFreePost(promotionGoodsDetail.getFreePost());
//							product.setReturnIfUseCoupon(promotionGoodsDetail.getIfUseCoupon());
//							product.setReturnIfUseBonus(promotionGoodsDetail.getIfUseBonus());
							//2020-03-04修改，由于优惠群和积分是在订单层面限制的，会员商品加购物车后，限制单品没有意义
							product.setReturnIfOnlyUserBonus(false);
							product.setReturnIfFreePost(false);
							product.setReturnIfUseCoupon(false);
							product.setReturnIfUseBonus(false);

							product.setReturnAllowBuyGoods(promotionGoodsDetail.getOnlyBuyOne());
							product.setRemainNumber(promotionGoodsDetail.getRemainNum());
							//判断是否允许下单
							product.setReturnAllowOrder(litemallPromotionGoodsRuleService.checkIfAllowGoodsUserNext(promotionGoodsDetail.getRuleId(),product.getId(),userId));
							//如果已经在会员活动中，则直接返回下一商品
							continue;
						}
					}
					//判断该商品是否可以下单，如果是秒杀商品规定了用户只能下一单，如果已经下过，则不可以继续下，
					// 如果是团购商品，该用户已经下过单，则不允许再继续下单
					//如果是会员商品，规定单人单次，如果用户已经下过单，则不允许再购买
					//判断该商品是否在正在执行的秒杀列表中,如果是，执行秒杀价
					LitemallPromotionSeckillRule promotionSeckillRule= litemallPromotionSeckillRuleService.queryProductIdIsInSkill(product.getId());
					if(promotionSeckillRule!=null){
						product.setReturnType("秒杀商品");
						product.setReturnRuleId(promotionSeckillRule.getId());
						product.setReturnRuleNumber(promotionSeckillRule.getSeckillRemainNum());
						product.setReturnRulePrice(promotionSeckillRule.getSeckillPrice());
						product.setReturnAllowBuyGoods(promotionSeckillRule.getSeckillOnlyBuyOne());
						product.setReturnIfFreePost(promotionSeckillRule.getFreePost());
						product.setReturnIfUseCoupon(promotionSeckillRule.getIfUseCoupon());
						product.setReturnIfUseBonus(promotionSeckillRule.getIfUseBonus());
						LocalDateTime seckillEndDate = promotionSeckillRule.getSeckillEndDate();
						product.setSeckillEndDate(seckillEndDate);
						product.setRemainNumber(promotionSeckillRule.getSeckillRemainNum());
						//判断是否允许下单
						product.setReturnAllowOrder(litemallPromotionSeckillRuleService.checkIfAllowSeckillNext(promotionSeckillRule.getId(),userId));
						//如果已经在秒杀活动中，则直接返回下一商品
						continue;
					}
					//判断该商品是否在正在执行的团购商品列表中，如果是，执行团购价
					LitemallGrouponRules grouponRules= litemallGrouponRulesService.queryProductIdIsInGroupon(product.getId());
					if(grouponRules!=null){
						product.setReturnType("团购商品");
						product.setReturnRuleId(grouponRules.getId());
						product.setReturnRuleNumber(grouponRules.getGroupRemainStore());
						product.setReturnRulePrice(grouponRules.getGrouponPrice());
						//设置团购商品参与人数/最低团购人数
						Integer canyuCount= litemallGrouponOrderService.countGroupon(grouponRules.getId());
						product.setReturnGrouponInfo(canyuCount.toString()+"/"+grouponRules.getGroupMinperson().toString());
						product.setReturnIfFreePost(grouponRules.getFreePost());
						product.setReturnIfUseCoupon(grouponRules.getIfUseCoupon());
						product.setReturnIfUseBonus(grouponRules.getIfUseBonus());
						//判断是否允许下单
						product.setReturnAllowBuyGoods(true);
						product.setReturnAllowOrder(litemallGrouponRulesService.checkIfAllowGroupOnNext(grouponRules.getId(),userId));
						product.setRemainNumber(grouponRules.getGroupRemainStore());
						//如果已经在团购活动中，则直接返回下一商品
						continue;
					}
					//判断该商品是否是在品项折扣活动中，如果是，则取品项折扣价
					ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods= litemallPromotionGoodsRebateRuleService.getRuleByProductId(product.getId());
					if(goodsRebateRuleGoods!=null){
						product.setReturnType("折扣商品");
						product.setReturnRuleId(goodsRebateRuleGoods.getRuleId());
						product.setReturnRuleNumber(product.getRemainNumber());
						product.setReturnRulePrice(goodsRebateRuleGoods.getRebatePrice());
						product.setReturnIfFreePost(false);
						product.setReturnIfUseCoupon(true);
						product.setReturnIfUseBonus(true);
						product.setReturnAllowOrder(true);
						//如果已经在会员活动中，则直接返回下一商品
						continue;
					}
					//以上都不是，则取商品的默认单价及库存
					product.setReturnType("正常商品");
					product.setReturnRuleId(null);
					product.setReturnRuleNumber(product.getRemainNumber());
					product.setReturnRulePrice(product.getPrice());
					product.setReturnAllowOrder(true);
					product.setReturnIfUseCoupon(true);
					product.setReturnIfUseBonus(true);
					goodsService.updateStoreBiggerZeroOrNot(product.getGoodsId());
				}


				data.put("info", info);
				data.put("userHasCollect", userHasCollect);
				data.put("issue", issueCallableTask.get());
				data.put("comment", commentsCallableTsk.get());
				data.put("specificationList", objectCallableTask.get());
				data.put("attributeList", goodsAttributeListestGoodstCallableTask.get());

				data.put("productList", productList);
				//data.put("attribute", goodsAttributeListTask.get());
				if(brandCallableTask!=null) {
					data.put("brand", brandCallableTask.get());
				}
//			data.put("groupon", grouponRulesCallableTask.get());
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			//商品分享图片地址
			data.put("shareImage", info.getShareUrl());
			return data;


	}

	/**
	 * 商品详情
	 * <p>
	 * 用户可以不登录。
	 * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
	 *
	 * @param userId 用户ID
	 * @param id     商品ID
	 * @return 商品详情
	 */
	@PostMapping("detail")
	public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
		Object goodsIndo= getDetail(userId,id);
		return ResponseUtil.ok(goodsIndo);
	}

	/**
	 * 将json字符串转为Map结构
	 * 如果json复杂，结果可能是map嵌套map
	 * @param jsonStr 入参，json格式字符串
	 * @return 返回一个map
	 */
	public static Map<String, Object> json2Map(String jsonStr) {
		Map<String, Object> map = new HashMap<>();
		if(jsonStr != null && !"".equals(jsonStr)){
			//最外层解析
			JSONObject json = JSONObject.fromObject(jsonStr);
			for (Object k : json.keySet()) {
				Object v = json.get(k);
				//如果内层还是数组的话，继续解析
				if (v instanceof JSONArray) {
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					Iterator<JSONObject> it = ((JSONArray) v).iterator();
					JSONObject json2=null;
					while (it.hasNext()) {
						try {
							json2 = it.next();
							list.add(json2Map(json2.toString()));
						}catch (Exception e)
						{
							continue;
						}

					}
					map.put(k.toString(), list);
				} else {
					map.put(k.toString(), v);
				}
			}
			return map;
		}else{
			return null;
		}
	};

	/**
	 * 商品分类类目
	 *
	 * @param id 分类类目ID
	 * @return 商品分类类目
	 */
	@GetMapping("categoryTest")
	public Object categoryTest(@NotNull Integer id) {
		LitemallCategory cur = categoryService.findById(id);
		LitemallCategory parent = null;
		List<LitemallCategory> children = null;

		if (cur.getPid() == 0) {
			parent = cur;
			children = categoryService.queryByPid(cur.getId());
			cur = children.size() > 0 ? children.get(0) : cur;
		} else {
			parent = categoryService.findById(cur.getPid());
			children = categoryService.queryByPid(cur.getPid());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("currentCategory", cur);
		data.put("parentCategory", parent);
		data.put("brotherCategory", children);
		return ResponseUtil.ok(data);
	}

	/**
	 * 商品分类类目
	 *
	 * @param id 分类类目ID
	 * @return 商品分类类目
	 */
	@GetMapping("category")
	public Object category(@NotNull Integer id) {
		LitemallCategory cur = categoryService.findById(id);
		LitemallCategory parent = null;
		List<LitemallCategory> children = null;

		if (cur.getPid() == 0) {
			parent = cur;
			children = categoryService.queryByPid(cur.getId());
			cur = children.size() > 0 ? children.get(0) : cur;
		} else {
			parent = categoryService.findById(cur.getPid());
			children = categoryService.queryByPid(cur.getPid());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("currentCategory", cur);
		data.put("parentCategory", parent);
		data.put("brotherCategory", children);
		return ResponseUtil.ok(data);
	}

	/**
	 * 根据条件搜素商品
	 * <p>
	 * 1. 这里的前五个参数都是可选的，甚至都是空
	 * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
	 *
	 * @param categoryId 分类类目ID，可选
	 * @param brandId    品牌商ID，可选
	 * @param keyword    关键字，可选
	 * @param isNew      是否新品，可选
	 * @param isHot      是否热买，可选
	 * @param userId     用户ID
	 * @param page       分页页数
	 * @param size       分页大小
	 * @param sort       排序方式，支持"add_time", "retail_price"或"name"
	 * @return 根据条件搜素的商品详情
	 */
	@PostMapping("list")
	public Object list(
		Integer categoryId,
		Integer pCategoryId,
		Integer levl,
		Integer brandId,
		String keyword,
		Boolean isNew,
		Boolean isHot,
		@LoginUser Integer userId,
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@Sort(accepts = {"add_time", "retail_price", "name"}) @RequestParam(defaultValue = "add_time") String sort
		) {

		//添加到搜索历史
		if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
			LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
			searchHistoryVo.setKeyword(keyword);
			searchHistoryVo.setUserId(userId);
			searchHistoryVo.setFrom("wx");
			searchHistoryService.save(searchHistoryVo);
		}
		//查询列表数据
		List<LitemallGoods> goodsList;
		if(pCategoryId!=null){
			if(levl==1){
				List<LitemallCategory> queryL1 = categoryService.queryL2ByPid(pCategoryId);
				if(queryL1!=null && queryL1.size()>0){
					List<Integer> ids = new ArrayList<>();
					for (LitemallCategory l : queryL1) {
						ids.add(l.getId());
					}
					goodsList = goodsService.querySelectiveIds(ids,page, size, sort);
				}else{
					return ResponseUtil.ok();
				}

			}else{
				//查询列表数据
				goodsList = goodsService.querySelective(categoryId, brandId, keyword, isHot, isNew, page, size, sort);

			}
		}else{
			//查询列表数据
			goodsList = goodsService.querySelective(pCategoryId, brandId, keyword, isHot, isNew, page, size, sort);
		}


		// 查询商品所属类目列表。
		List<Integer> goodsCatIds = goodsService.getCatIds(brandId, keyword, isHot, isNew);
		List<LitemallCategory> categoryList = null;
		if (goodsCatIds.size() != 0) {
			categoryList = categoryService.queryL2ByIds(goodsCatIds);
		} else {
			categoryList = new ArrayList<>(0);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("goodsList", goodsList);
		data.put("count", PageInfo.of(goodsList).getTotal());
		data.put("filterCategoryList", categoryList);

		return ResponseUtil.ok(data);
	}

	/**
	 * 根据条件搜素商品
	 * <p>
	 * @param keyword    关键字
	 * @param page       分页页数
	 * @param size       分页大小
	 * @param sort       排序方式，支持"add_time", "retail_price"或"name"
	 * @return 根据条件搜素的商品详情
	 */
	@PostMapping("search")
	public Object search(@LoginUser Integer userId,String keyword,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@Sort(accepts = {"add_time", "retail_price", "name"}) @RequestParam(defaultValue = "add_time") String sort
			) {

		//查询列表数据
		List<LitemallGoods> goodsList;

		//向后台数据库插入搜索历史记录
        LitemallSearchHistory searchHistory=new LitemallSearchHistory();
        searchHistory.setUserId(userId);
		LitemallUser userInfo =userService.findById(userId);
		searchHistory.setWxNickname(userInfo.getNickname());
		searchHistory.setWeixinOpenid(userInfo.getWeixinOpenid());
		searchHistory.setCrmId(userInfo.getCrmId());

        searchHistory.setKeyword(keyword);
        searchHistory.setFrom("1");//商贸类型是1-机场商贸
		searchHistoryService.save(searchHistory);

		//查询列表数据
		goodsList = goodsService.searchGoods(keyword,page,size,sort);

		//对查询到的商品判断当前商品是否在不同的活动中，如果是，则取活动价格
		for(LitemallGoods goods: goodsList){
			goods.setRetailPrice(goodsProductService.getGoodsPrice(userId,goods.getDefaultProductId()));
		}
		Integer total = goodsList.size();
		Map<String, Object> data = new HashMap<>();
		data.put("goodslist", goodsList);
		data.put("totals", total);
		return ResponseUtil.ok(data);
	}

	/**
	 * 商品详情页面“大家都在看”推荐商品
	 *
	 * @param id, 商品ID
	 * @return 商品详情页面推荐商品
	 */
	@GetMapping("related")
	public Object related(@NotNull Integer id) {
		LitemallGoods goods = goodsService.findById(id);
		if (goods == null) {
			return ResponseUtil.badArgumentValue();
		}

		// 目前的商品推荐算法仅仅是推荐同类目的其他商品
		int cid = goods.getCategoryId();

		// 查找六个相关商品
		int related = 6;
		List<LitemallGoods> goodsList = goodsService.queryByCategory(cid, 0, related);
		Map<String, Object> data = new HashMap<>();
		data.put("goodsList", goodsList);
		return ResponseUtil.ok(data);
	}

	/**
	 * 上架的商品总数
	 *
	 * @return 上架的商品总数
	 */
	@GetMapping("count")
	public Object count() {
		Integer goodsCount = goodsService.queryOnSale();
		Map<String, Object> data = new HashMap<>();
		data.put("goodsCount", goodsCount);
		return ResponseUtil.ok(data);
	}

}
