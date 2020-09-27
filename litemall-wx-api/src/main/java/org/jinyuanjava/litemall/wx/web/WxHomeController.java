package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.system.SystemConfig;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.service.HomeCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/home")
@Validated
@Api(description = "微信前端/用户首页:/wx/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    //首页显示的顺序为
    //Banner(Ad),Icon,会员专区,首页秒杀，首页团购，首页特产

    //banner
    @Autowired
    private LitemallAdService adService;

    //Icon
    @Autowired
    private LitemallHomeIconService homeIconService;

    @Autowired
    private LitemallHomeAndListService homeAndListService;
    @Autowired
    private LitemallHomeBackgroundImageService homeBackgroundImageService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private LitemallPromotionGoodsRuleService promotionGoodsRuleService;

    @Autowired
    private LitemallFootprintService footprintService;

    @Autowired
    private LitemallUserService userService;

    @GetMapping("/cache")
    public Object cache(@NotNull String key) {
        if (!key.equals("litemall_cache")) {
            return ResponseUtil.fail();
        }
        // 清除缓存
        HomeCacheManager.clearAll();
        return ResponseUtil.ok("缓存已清除");
    }

    public Log getLogger() {
        return logger;
    }

    /**
     * 首页数据
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/index")
    public Object index(@LoginUser Integer userId) {
        if (userId == null) {
           return ResponseUtil.unlogin();
//            ResponseUtil.ok(null);
            //return ResponseUtil.ok(null);
        }

        // 记录用户的足迹 异步处理 用户登陆一次要写入一次访问
        if (userId != null) {
            executorService.execute(()->{
                LitemallFootprint footprint = new LitemallFootprint();
                LitemallUser userInfo =userService.findById(userId);
                footprint.setUserId(userId);
                footprint.setWxNickname(userInfo.getNickname());
                footprint.setWeixinOpenid(userInfo.getWeixinOpenid());
                footprint.setCrmId(userInfo.getCrmId());
                footprint.setGoodsId(null);
                footprint.setGoodsSn(null);
                footprint.setGoodsName(null);
                footprintService.add(footprint);

            });
        }

        //临时设置一个变量，以便测试专属会员商品
//        userId=1;
//        final Integer userTempId=userId;
        //优先从缓存中读取
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //会员商品
        Callable<List> userHomeGoodsListCallable = null;
        if (userId != null) {
//            userHomeGoodsListCallable = () -> homeAndListService.getHomeUserRecommendGoods(userId, SystemConfig.getHunyuanLimit());
            //返回正在进行的活动，并且按排序规则，已经排好的商品
            userHomeGoodsListCallable = () -> promotionGoodsRuleService.getGoodsListByUserId(userId,SystemConfig.getHunyuanLimit());
        }
        FutureTask<List> userGoodsHomeTask = null;
        if (userHomeGoodsListCallable != null) {
            userGoodsHomeTask = new FutureTask<>(userHomeGoodsListCallable);
        }
        if (userGoodsHomeTask != null) {
            executorService.submit(userGoodsHomeTask);
        }

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

        if (HomeCacheManager.hasData(HomeCacheManager.INDEX)) {
            Map<String, Object> entity = HomeCacheManager.getCacheData(HomeCacheManager.INDEX);
            if (userGoodsHomeTask != null) {
                try {
                    entity.put("userGoodsHome", userGoodsHomeTask.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            return ResponseUtil.ok(entity);
        }


        //根据用户的公众号来源信息，区分是首都机场还是大兴机场，目前是部署两套微商城链接网页
        //首页显示的顺序为
        //Banner(Ad),Icon,会员专区,首页秒杀，首页团购，首页特产
        Callable<List> bannerListCallable = () -> adService.queryIndex();

        Callable<List> iconListCallable = () -> homeIconService.queryIndex();

        //电商商品
        Callable<List> ecHomeGoodsListCallable = () -> homeAndListService.getHomeEcGoods(SystemConfig.getBuyLimit());

        FutureTask<List> bannerTask = new FutureTask<>(bannerListCallable);
        FutureTask<List> iconTask = new FutureTask<>(iconListCallable);
        FutureTask<List> ecHomeTask = new FutureTask<>(ecHomeGoodsListCallable);

        executorService.submit(bannerTask);
        executorService.submit(iconTask);

        executorService.submit(ecHomeTask);

        List<LitemallHomeBackgroundImage> queryIndex = homeBackgroundImageService.queryIndex();
        Map<String, Object> entity = new HashMap<>();
        try {
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
            entity.put("banner", bannerTask.get());
            entity.put("icon", iconTask.get());

            if (queryIndex != null && queryIndex.size() > 0) {
                entity.put("backimg", queryIndex.get(0));
            }
            entity.put("ecHome", ecHomeTask.get());

//            //缓存数据
            HomeCacheManager.loadData(HomeCacheManager.INDEX, entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }

    @GetMapping("/getUserGoods")
    public Object getUserGoods(@LoginUser Integer userId){
//        List<ViewListRecommendGoodsUser> viewListRecommendGoodsUserList=
//                homeAndListService.getHomeUserRecommendGoods(userId, SystemConfig.getHunyuanLimit());
        List goodsUserList= promotionGoodsRuleService.getGoodsListByUserId(userId,SystemConfig.getHunyuanLimit());
        return ResponseUtil.okList(goodsUserList);
    }



    private List<Map> getCategoryList() {
        List<Map> categoryList = new ArrayList<>();
//        List<LitemallCategory> catL1List = categoryService.queryL1WithoutRecommend(0, SystemConfig.getCatlogListLimit());
//        for (LitemallCategory catL1 : catL1List) {
//            List<LitemallCategory> catL2List = categoryService.queryByPid(catL1.getId());
//            List<Integer> l2List = new ArrayList<>();
//            for (LitemallCategory catL2 : catL2List) {
//                l2List.add(catL2.getId());
//            }
//
//            List<LitemallGoods> categoryGoods;
//            if (l2List.size() == 0) {
//                categoryGoods = new ArrayList<>();
//            } else {
//                categoryGoods = goodsService.queryByCategory(l2List, 0, SystemConfig.getCatlogMoreLimit());
//            }
//
//            Map<String, Object> catGoods = new HashMap<>();
//            catGoods.put("id", catL1.getId());
//            catGoods.put("name", catL1.getName());
//            catGoods.put("goodsList", categoryGoods);
//            categoryList.add(catGoods);
//        }
        return categoryList;
    }
}
