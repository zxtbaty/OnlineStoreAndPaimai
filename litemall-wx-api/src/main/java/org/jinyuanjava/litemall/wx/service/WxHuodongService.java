package org.jinyuanjava.litemall.wx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRebateGoods;
import org.jinyuanjava.litemall.db.domain.LitemallPromotionGoodsRebateRule;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallPromotionGoodsRebateGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallPromotionGoodsRebateRuleService;
import org.jinyuanjava.litemall.wx.util.MapUtil;
import org.springframework.stereotype.Service;
@Service
public class WxHuodongService {
	 @Resource
	 private LitemallPromotionGoodsRebateRuleService promotionGoodsRebateRuleService;
	 @Resource
	 private LitemallPromotionGoodsRebateGoodsService promotionGoodsRebateGoodsService;
	 @Resource
	 private LitemallGoodsService goodsService;
	    /**
	     * 获取参加品项折扣活动的商品列表
	     * @param name
	     * @param expireFlag
	     * @param goodsSn
	     * @param goodsName
	     * @param page
	     * @param size
	     * @param sort

	     * @return
	     */
	    public List<Map> queryHuodongGoods(Integer page, Integer size, String sort) {
	    	List <Map> returnList =new ArrayList<Map>();
	    	List<LitemallPromotionGoodsRebateRule> querySelective = promotionGoodsRebateRuleService.querySelective(null, 0, null, null, page, size, sort);
	    	if(querySelective!=null &&querySelective.size()>0){
	    		for (LitemallPromotionGoodsRebateRule litemallPromotionGoodsRebateRule : querySelective) {
		    		List<LitemallPromotionGoodsRebateGoods> queryByRuleId = promotionGoodsRebateGoodsService.queryByRuleId(litemallPromotionGoodsRebateRule.getId());
		    		for (LitemallPromotionGoodsRebateGoods litemallPromotionGoodsRebateGoods : queryByRuleId) {
		    			Integer goodsId = litemallPromotionGoodsRebateGoods.getGoodsId();
		    			LitemallGoods goods = goodsService.findById(goodsId);
		    			try {
							Map<String, Object> objectToMap = MapUtil.objectToMap(litemallPromotionGoodsRebateGoods);
							objectToMap.put("picUrl", goods.getPicUrl());
							returnList.add(objectToMap);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
	    	}


	        return returnList;
	    }

}
