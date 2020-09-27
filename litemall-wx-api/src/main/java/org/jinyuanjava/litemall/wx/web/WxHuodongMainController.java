package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailGoodsList;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongDetailPicLink;
import org.jinyuanjava.litemall.db.domain.LitemallHuodongMain;
import org.jinyuanjava.litemall.db.service.LitemallGoodsProductService;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallHomeAndListService;
import org.jinyuanjava.litemall.db.service.LitemallHuodongDetailGoodsListService;
import org.jinyuanjava.litemall.db.service.LitemallHuodongDetailPicLinkService;
import org.jinyuanjava.litemall.db.service.LitemallHuodongMainService;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.service.HomeCacheManager;
import org.jinyuanjava.litemall.wx.service.WxHuodongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/huodong")
@Validated
@Api(description = "微信前端/活动页页:/wx/huodong")
public class WxHuodongMainController {
    private final Log logger = LogFactory.getLog(WxHuodongMainController.class);

    @Autowired
    private LitemallHuodongMainService huodongMainService;

    @Autowired
    private LitemallHuodongDetailGoodsListService huodongDetailGoodsListService;
    @Autowired
    private LitemallHuodongDetailPicLinkService huodongDetailPicLinkService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallGoodsProductService goodsProductService;
    @Autowired
    private LitemallHomeAndListService homeAndListService;
    @Autowired
    private WxHuodongService huodongService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);




    /**
     * 活动数据
     *
     * @param mainId 活动id，非空
     * @return 活动数据
     */
    @GetMapping("/index")
    public Object index(@LoginUser Integer userId, Integer mainId) {
        if (mainId == null) {
            ResponseUtil.ok(null);
        }
        //查询活动是否存在且是否过期
        LitemallHuodongMain queryById = huodongMainService.queryById(mainId);
        if (queryById == null) {
            ResponseUtil.ok(null);
        } else if (queryById.getExpireFlag() || queryById.getDeleted()) {
            //已经过期
            ResponseUtil.fail(-200, "活动过期了");
        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);


        Callable<List<LitemallHuodongDetailGoodsList>> huodongDetailGoodsListCallable = () -> huodongDetailGoodsListService.querySelective(mainId, 0, 100, null);

//        LitemallHuodongDetailPicLink huodongDetailPicLink = new LitemallHuodongDetailPicLink();
//        huodongDetailPicLink.setMainId(mainId);
        Callable<List<LitemallHuodongDetailPicLink>> huodongDetailPicLinkCallable = () ->
                huodongDetailPicLinkService.querySelective(mainId, 0, 100, null);

        FutureTask<List<LitemallHuodongDetailGoodsList>> huodongDetailGoodsListTask = new FutureTask<>(huodongDetailGoodsListCallable);
        FutureTask<List<LitemallHuodongDetailPicLink>> huodongDetailPicLinkTask = new FutureTask<>(huodongDetailPicLinkCallable);

        executorService.submit(huodongDetailGoodsListTask);
        executorService.submit(huodongDetailPicLinkTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            List<LitemallHuodongDetailGoodsList> goodsResultList = huodongDetailGoodsListTask.get();
            for (LitemallHuodongDetailGoodsList goods : goodsResultList) {
                LitemallGoods goodsQuery = goodsService.findById( goods.getGoodsId());
                //goods.setGoodsPrice(getGoodsPrice(userId,goodsQuery.getDefaultProductId()));
                goods.setGoodsPrice(goodsProductService.getGoodsPrice(userId,goodsQuery.getDefaultProductId()));
            }
            if(queryById.getExpireFlag()){//过期
            	 entity.put("exp", true);
            }else{//没过期
            	if(queryById.getDeleted()){
            		entity.put("exp", true);
            	}else{
            		entity.put("exp", false);
            	}
            }


            entity.put("goodsList", goodsResultList);
            entity.put("picLink", huodongDetailPicLinkTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }
    /**
     * 活动商品列表
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/list")
    public Object index(@LoginUser Integer userId) {
        //优先从缓存中读取
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //折扣商品
//        Callable<List> userHomeGoodsListCallable = null;
//        if (userId != null) {
//            userHomeGoodsListCallable = () -> huodongService.queryHuodongGoods(null, null, null);
//        }
//        FutureTask<List> userGoodsHomeTask = null;
//        if (userHomeGoodsListCallable != null) {
//            userGoodsHomeTask = new FutureTask<>(userHomeGoodsListCallable);
//        }
//        if (userGoodsHomeTask != null) {
//            executorService.submit(userGoodsHomeTask);
//        }
        List<Map> queryHuodongGoods = huodongService.queryHuodongGoods(0, 100, null);

        //秒杀商品
        Callable<List> seckillHomeGoodsListCallable = () -> homeAndListService.getHomeSeckillGoods(SystemConfig.getMiaoshaLimit());
        FutureTask<List> seckillHomeTask = null;

        if (seckillHomeGoodsListCallable != null) {
            seckillHomeTask = new FutureTask<>(seckillHomeGoodsListCallable);
        }
        if (seckillHomeTask != null) {
            executorService.submit(seckillHomeTask);
        }
        //团购活动

        Callable<List> grouponHomeGoodsListCallable = () -> homeAndListService.getHomeGrouponGoods(SystemConfig.getTuanLimit());
        FutureTask<List> grouponHomeTask = null;
        if (grouponHomeGoodsListCallable != null) {
            grouponHomeTask = new FutureTask<>(grouponHomeGoodsListCallable);
        }
        if (grouponHomeTask != null) {
            executorService.submit(grouponHomeTask);
        }

        Map<String, Object> entity =null;
        if (HomeCacheManager.hasData(HomeCacheManager.INDEX)) {
             entity = HomeCacheManager.getCacheData(HomeCacheManager.INDEX);
//            if (userGoodsHomeTask != null) {
//                try {
//                    entity.put("userGoodsHome", userGoodsHomeTask.get());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
             entity.put("zhekouGoodsHome", queryHuodongGoods);

            if (seckillHomeTask != null) {
                try {
                    entity.put("seckillHome", seckillHomeTask.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (grouponHomeTask != null) {
                try {
                    entity.put("grouponHome", grouponHomeTask.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }else{
        	entity =new HashMap<>();
        	 try {
        		 entity.put("zhekouGoodsHome", queryHuodongGoods);
                 entity.put("seckillHome", homeAndListService.getHomeSeckillGoods(SystemConfig.getMiaoshaLimit()));
                 entity.put("grouponHome", homeAndListService.getHomeGrouponGoods(SystemConfig.getTuanLimit()));
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }

        return ResponseUtil.ok(entity);
    }

}
