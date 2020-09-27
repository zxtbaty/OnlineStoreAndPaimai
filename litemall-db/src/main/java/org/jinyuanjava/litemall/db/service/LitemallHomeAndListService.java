package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.CharUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LitemallHomeAndListService {

    //首页显示的顺序为
    //Banner(Ad),Icon,会员专区,首页秒杀，首页团购，首页特产

    //首页会员专区
    @Resource
    private ViewListRecommendGoodsUserMapper goodsUserMapper;

    // 首页秒杀
    @Resource
    private ViewHomepageRecommendSeckillMapper seckillHompageMapper;

    // 首页团购
    @Resource
    private ViewHomepageRecommendGrouponMapper grouponHomePageMapper;

    // 首页特产
    @Resource
    private ViewHomepageRecommendEcMapper ecHomePageMapper;



    //列表秒杀
    @Resource
    private ViewListRecommendSeckillMapper seckillListMapper;

    //列表团购
    @Resource
    private ViewListRecommendGrouponMapper grouponListMapper;

    //列表特产
    @Resource
    private ViewListRecommendEcMapper ecListMapper;


    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallSearchHistoryService searchHistoryService;



    @Autowired
    private LitemallSystemConfigService systemConfigService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;

//

    /**
     * 获取首页显示的会员商品
     * @return
     */
    public List<ViewListRecommendGoodsUser> getHomeUserRecommendGoods(Integer userId,Integer limit){
        if(userId==null){
            return null;
        }

        ViewListRecommendGoodsUserExample example=new ViewListRecommendGoodsUserExample();
        ViewListRecommendGoodsUserExample.Criteria criteria=example.createCriteria();
        byte b = 0;
        criteria.andExpireFlagEqualTo(CharUtil.booleanConverToByte(false));
        //会员活动已经开始
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(userId);
        }
        if(limit==null||limit==0){
            limit=10;
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        PageHelper.startPage(1,limit);

        List<ViewListRecommendGoodsUser> goodsUsersTest= goodsUserMapper.selectByExample(example);



        return goodsUsersTest;

    }


    /**
     * 获取首页秒杀的商品
     * @return
     */
    public List<ViewHomepageRecommendSeckill> getHomeSeckillGoods(Integer limit){
        ViewHomepageRecommendSeckillExample example=new ViewHomepageRecommendSeckillExample();
        ViewHomepageRecommendSeckillExample.Criteria criteria=example.createCriteria();
        if(limit==null||limit==0){
            limit=10;
        }
        byte b = 0;
        criteria.andSeckillExpireFlagEqualTo(CharUtil.booleanConverToByte(false));
        //会员活动已经开始
        criteria.andSeckillBeginDateLessThanOrEqualTo(LocalDateTime.now());


        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        PageHelper.startPage(1,limit);
        List<ViewHomepageRecommendSeckill> seckills= seckillHompageMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewHomepageRecommendSeckill seckill:seckills){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(seckill,temp);
//            litemallGoods.add(temp);
//        }
        return seckills;
    }

    /**
     * 获取首页团购的商品
     * @return
     */
    public List<ViewHomepageRecommendGroupon> getHomeGrouponGoods(Integer limit){
        ViewHomepageRecommendGrouponExample example=new ViewHomepageRecommendGrouponExample();
        ViewHomepageRecommendGrouponExample.Criteria criteria=example.createCriteria();
        if(limit==null||limit==0){
            limit=10;
        }
        byte b = 0;
        criteria.andExpireFlagEqualTo(CharUtil.booleanConverToByte(false));
        //会员活动已经开始
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());

        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        PageHelper.startPage(1,limit);
        List<ViewHomepageRecommendGroupon> groupons= grouponHomePageMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewHomepageRecommendGroupon groupon:groupons){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(groupon,temp);
//            litemallGoods.add(temp);
//        }
        return groupons;
    }

    /**
     * 获取首页特产的商品
     * @return
     */
    public List<ViewHomepageRecommendEc> getHomeEcGoods(Integer limit){
        ViewHomepageRecommendEcExample example=new ViewHomepageRecommendEcExample();
        ViewHomepageRecommendEcExample.Criteria criteria=example.createCriteria();
        if(limit==null||limit==0){
            limit=10;
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
           criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        //虚拟商品只显示在活动中，不显示在正常列表中
        criteria.andIfXuniEqualTo(CharUtil.booleanConverToByte(false));

        PageHelper.startPage(1,limit);
        List<ViewHomepageRecommendEc> ecs= ecHomePageMapper.selectByExample(example);
        //如果商品处于品项折扣活动中，则商品标价要乘以折扣比率 商品的默认规格ID已经写进了数据库
        List<Integer> productIds= ecs.stream().map(ViewHomepageRecommendEc::getDefaultProductId).collect(Collectors.toList());
        if(productIds!=null&&productIds.size()>0) {
            List<ViewPromotionGoodsRebateRuleGoods> goodsRebateRuleGoodsList=  goodsRebateRuleService.getRuleByProductIds(productIds);
            if(goodsRebateRuleGoodsList!=null&&goodsRebateRuleGoodsList.size()>0) {
                for(ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods:goodsRebateRuleGoodsList) {
                    Optional<ViewHomepageRecommendEc> findProduct = ecs.stream().filter(item -> item.getDefaultProductId().equals(goodsRebateRuleGoods.getGoodsProductId())).findFirst();
                    if(findProduct.isPresent()){
                        findProduct.get().setRetailPrice(goodsRebateRuleGoods.getRebatePrice());
                    }
                }
            }
        }
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewHomepageRecommendEc ec:ecs){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(ec,temp);
//            litemallGoods.add(temp);
//        }
        return ecs;
    }


    /**
     * 获取首页显示的会员商品列表
     * @return
     */
    public List<ViewListRecommendGoodsUser> getListUserGoods(String name,Integer userId, Integer page, Integer limit){

        ViewListRecommendGoodsUserExample example=new ViewListRecommendGoodsUserExample();
        ViewListRecommendGoodsUserExample.Criteria criteria=example.createCriteria();
        byte b = 0;
        criteria.andExpireFlagEqualTo(CharUtil.booleanConverToByte(false));
        if(!StringUtils.isEmpty(name)){
            criteria.andGoodsNameLike("%"+name+"%");
        }
        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(userId);
        }
        if(limit==null||limit==0){
            limit=10;
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        PageHelper.startPage(1,limit);
        List<ViewListRecommendGoodsUser> goodsUsers= goodsUserMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewListRecommendGoodsUser goodsUser:goodsUsers){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(goodsUser,temp);
//            litemallGoods.add(temp);
//        }
        return goodsUsers;

    }

    /**
     * 获取列表秒杀的商品
     * @return
     */
    public List<ViewListRecommendSeckill> getListSeckillGoods(String name, Integer page, Integer limit){
        ViewListRecommendSeckillExample example=new ViewListRecommendSeckillExample();
        ViewListRecommendSeckillExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        if(limit==null||limit==0){
            limit=10;
        }
        PageHelper.startPage(page,limit);
        List<ViewListRecommendSeckill> seckills= seckillListMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewListRecommendSeckill seckill:seckills){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(seckill,temp);
//            litemallGoods.add(temp);
//        }
        return seckills;
    }

    /**
     * 获取列表团购的商品
     * @return
     */
    public List<ViewListRecommendGroupon> getListGrouponGoods(String name, Integer page, Integer limit){

        ViewListRecommendGrouponExample example=new ViewListRecommendGrouponExample();
        ViewListRecommendGrouponExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        if(limit==null||limit==0){
            limit=10;
        }
        PageHelper.startPage(page,limit);
        List<ViewListRecommendGroupon> groupons= grouponListMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewListRecommendGroupon groupon:groupons){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(groupon,temp);
//            litemallGoods.add(temp);
//        }
        return groupons;
    }

    /**
     * 获取列表特产的商品
     * @return
     */
    public List<ViewListRecommendEc> getListEcGoods(
            Integer categoryId,
            Integer pCategoryId,
            Integer levl,
            String keyword,
            Integer userId,
            Integer page, Integer limit){

        //保存搜索记录
        saveSearchInfo(userId,keyword);

        //判断一下选择的类别
        List<Integer> categoryIds=new ArrayList<>();
        if(levl!=null) {
            if (levl == 1 && pCategoryId!=null) {
                categoryIds.add(pCategoryId);
                //获取子类
                List<LitemallCategory> litemallCategories = categoryService.queryL2ByPid(pCategoryId);
                if (litemallCategories != null && litemallCategories.size() > 0) {
                    for (LitemallCategory l : litemallCategories) {
                        categoryIds.add(l.getId());
                    }
                }
            } else if (levl == 2) {
                categoryIds.add(categoryId);
            }
        }

        ViewListRecommendEcExample example=new ViewListRecommendEcExample();
        ViewListRecommendEcExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(keyword)){
            criteria.andNameLike("%"+keyword+"%");
        }
        if(categoryIds.size()>0){
            criteria.andCategoryIdIn(categoryIds);
        }
        if(limit==null||limit==0){
            limit=10;
        }
        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }
        criteria.andIfListGoodsEqualTo(CharUtil.booleanConverToByte(true));
        PageHelper.startPage(page,limit);
        List<ViewListRecommendEc> ecs= ecListMapper.selectByExample(example);
//        List<LitemallGoods> litemallGoods=new ArrayList<>();
//        for(ViewListRecommendEc ec:ecs){
//            LitemallGoods temp=new LitemallGoods();
//            BeanUtils.copyProperties(ec,temp);
//            litemallGoods.add(temp);
//        }
        return ecs;
    }



    private void saveSearchInfo(Integer userId, String keyword){
        //添加到搜索历史
        if (userId != null && !com.mysql.jdbc.StringUtils.isNullOrEmpty(keyword)) {
            LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(userId);
            searchHistoryVo.setFrom("wx");
            searchHistoryService.save(searchHistoryVo);
        }
    }

}
