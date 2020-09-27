package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/tablelist")
@Validated
@Api(description = "微信前端/用户列表页:/wx/tablelist")
public class WxTableListController {
    private final Log logger = LogFactory.getLog(WxTableListController.class);

    @Autowired
    private LitemallHomeAndListService homeAndListService;
	@Autowired
	private LitemallGoodsService goodsService;

    /**
     * 获取会员商品列表
     * @param name
     * @param userId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/userGoods")
    public Object getListUserGoods(
            String name,Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewListRecommendGoodsUser> goodsUsers= homeAndListService.getListUserGoods(name,userId,page,limit);
        return ResponseUtil.okList(goodsUsers);
    }

    /**
     * 获取秒杀商品列表
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/seckill")
    public Object getListSeckillGoods(
            String name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewListRecommendSeckill> seckillGoods= homeAndListService.getListSeckillGoods(name,page,limit);
        return ResponseUtil.okList(seckillGoods);
    }

    /**
     * 获取团购商品列表
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/groupon")
    public Object getListGrouponGoods(
            String name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewListRecommendGroupon> seckillGoods= homeAndListService.getListGrouponGoods(name,page,limit);
        return ResponseUtil.okList(seckillGoods);
    }

    /**
     * 获取电商商品列表
     * @param categoryId
     * @param pCategoryId
     * @param levl
     * @param goodsName
     * @param userId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/ec")
    public Object getListEcGoods(
            Integer categoryId,
            Integer pCategoryId,
            Integer levl,
            String goodsName,
             Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewListRecommendEc> seckillGoods= homeAndListService.getListEcGoods(categoryId,pCategoryId,levl,goodsName,userId,page,limit);
        return ResponseUtil.okList(seckillGoods);
    }


    /**
     * 20190703修改后获取电商商品列表
     * @param brandId 品牌
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/bandgoods")
    public Object getListrBandGoods(
            Integer brandId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        List<ViewListRecommendEc> brandGoods= goodsService.getListBrandGoods(brandId,page,limit);
        return ResponseUtil.okList(brandGoods);
    }

}
