package org.jinyuanjava.litemall.db.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallBrandMapper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsMapper;
import org.jinyuanjava.litemall.db.dao.ViewListRecommendEcMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.util.CharUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallGoodsService {
    ViewListRecommendEc.Column[] viewColumns = new  ViewListRecommendEc.Column[]{ViewListRecommendEc.Column.id,
            ViewListRecommendEc.Column.name, ViewListRecommendEc.Column.brief, ViewListRecommendEc.Column.picUrl,
            ViewListRecommendEc.Column.priceDesc,
            ViewListRecommendEc.Column.isHot, ViewListRecommendEc.Column.isNew, ViewListRecommendEc.Column.counterPrice,
            ViewListRecommendEc.Column.retailPrice};

    LitemallGoods.Column[] columns = new   LitemallGoods.Column[]{ LitemallGoods.Column.id,  LitemallGoods.Column.name,
            LitemallGoods.Column.brief,  LitemallGoods.Column.picUrl,
            LitemallGoods.Column.priceDesc,
            LitemallGoods.Column.isHot,  LitemallGoods.Column.isNew,  LitemallGoods.Column.counterPrice,  LitemallGoods.Column.retailPrice};


    LitemallGoods.Column[] BrandColumns = new   LitemallGoods.Column[]{ LitemallGoods.Column.brandId};

    @Resource
    private LitemallGoodsMapper goodsMapper;

    @Resource
    private ViewListRecommendEcMapper recommendEcMapper;

    @Autowired
    private LitemallGoodsStoreService goodsStoreService;

    @Autowired
    private LitemallGoodsProductService goodsProductService;

    @Autowired
    private LitemallPromotionGoodsRebateRuleService goodsRebateRuleService;

    @Autowired
    private StatService statService;

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    @Resource
    private LitemallBrandMapper brandMapper;


    /**
     * 获取热卖商品
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByHot(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByNew(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的商品
     *
     * @param catList
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<LitemallGoods> queryByCategory(List<Integer> catList, Integer pageIndex, Integer pageSize) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        if(pageSize!=null&&pageSize<999999) {
            PageHelper.startPage(pageIndex, pageSize);
        }

        return goodsMapper.selectByExampleSelective(example, columns);
    }


    /**
     * 获取分类下的商品
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }


    public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keywords, Boolean isHot, Boolean isNew, Integer offset, Integer limit, String sort) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(catId) && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId);
            criteria2.andCategoryIdEqualTo(catId);
        }
        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);


        //有一个单独字段是否显示列表来标识是否在列表中显示
        criteria1.andIfListGoodsEqualTo(true);


        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallGoods> querySelective(Integer comId,Integer storeId,Integer brandId,Integer categoryId ,Integer isOnSale,
                                              String goodsSn, String name, String storeNum,Integer ifXuni,
                                              List<String> usedRange, Integer authorId,
                                              Integer dajiapaiCategoryId ,Integer privateCategoryId,
                                              Integer page, Integer size, String sort) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(comId)) {
            criteria.andComIdEqualTo(comId);
        }
        if(!StringUtils.isEmpty(storeId)){
            //预约店铺
           criteria.andStoreIdEqualTo(storeId);
//           //或者商品规格里含有该店铺的商品
//           List<Integer> goodsIdList= goodsProductService.queryListGoodsIdByStoreId(storeId);
//           if(goodsIdList!=null&&goodsIdList.size()>0) {
//               example.or().andIdIn(goodsIdList);
//           }

        }
        if(!StringUtils.isEmpty(brandId)){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(!StringUtils.isEmpty(categoryId)){
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if(!StringUtils.isEmpty(isOnSale)){
            if(isOnSale==1) {
                criteria.andIsOnSaleEqualTo(true);
            } else
            {
                criteria.andIsOnSaleEqualTo(false);
            }
        }
        if(!StringUtils.isEmpty(ifXuni)){
            if(ifXuni==1) {
                criteria.andIfXuniEqualTo(true);
            } else
            {
                criteria.andIfXuniEqualTo(false);
            }
        }

//        if(storeId!=null){
//            List<Integer> goodsStoreId=goodsStoreService.queryByStoreId(storeId);
//            if(goodsStoreId!=null&&goodsStoreId.size()>0) {
//                criteria.andIdIn(goodsStoreId);
//            }else
//            {
//                //该店铺没有值
//                criteria.andIdEqualTo(-1);
//            }
//        }

        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnLike("%" + goodsSn + "%");
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(usedRange)&&usedRange.size()>0) {
            criteria.andUsedRangeIn(usedRange);
        }
        if (!StringUtils.isEmpty(authorId)) {
            criteria.andAuthorIdEqualTo(authorId);
        }
        if (!StringUtils.isEmpty(dajiapaiCategoryId)) {
            criteria.andDajiapaiCategoryIdEqualTo(dajiapaiCategoryId);
        }
        if (!StringUtils.isEmpty(privateCategoryId)) {
            criteria.andPrivateCategoryIdEqualTo(privateCategoryId);
        }

        if(!StringUtils.isEmpty(storeNum)){
            if(storeNum.equalsIgnoreCase("<=0")||storeNum.equalsIgnoreCase(">0")){
                List<Integer> goodsIdExistsList=goodsProductService.queryGoodsIdListByStoreNumCondition(storeNum);
                if(goodsIdExistsList!=null&&goodsIdExistsList.size()>0) {
                    criteria.andIdIn(goodsIdExistsList);
                }
            } else if(storeNum.equalsIgnoreCase("null")){
                List<Integer> goodsIdExistsList=goodsProductService.queryGoodsIdListByStoreNumCondition("");
                if(goodsIdExistsList!=null&&goodsIdExistsList.size()>0) {
                    criteria.andIdNotIn(goodsIdExistsList);
                }
            }
        }


        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
        if(size!=null&&size<999999) {
            PageHelper.startPage(page, size);
        }
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

    public List<LitemallGoods> searchGoods(String name,Integer page, Integer size,String sort) {
    	LitemallGoodsExample example = new LitemallGoodsExample();

    	LitemallGoodsExample.Criteria criteria = example.createCriteria();
    	if (!StringUtils.isEmpty(name)) {
    		criteria.andNameLike("%" + name + "%");
    	}
    	criteria.andIsOnSaleEqualTo(true);
        //是否在列表显示
        criteria.andIfListGoodsEqualTo(true);
        criteria.andDeletedEqualTo(false);

        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(true);
        }

        //增加一个品牌是否停用的判断，停用的品牌不再显示
        LitemallBrandExample brandExample=new LitemallBrandExample();
        brandExample.or().andEnabledEqualTo(true).andDeletedEqualTo(false);
        List<LitemallBrand> brands=brandMapper.selectByExample(brandExample);
        List<Integer> brandIds=brands.stream().map(LitemallBrand::getId).collect(Collectors.toList());
        if(brandIds!=null&&brandIds.size()>0&&brandIds.get(0)!=0){
            criteria.andBrandIdNotIn(brandIds);
        }


        //没有品牌设置的商品也要能够查询出来
        LitemallGoodsExample.Criteria criteriaBrandIsNull = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            criteriaBrandIsNull.andNameLike("%" + name + "%");
        }
        criteriaBrandIsNull.andIsOnSaleEqualTo(true);
        //是否在列表显示
        criteriaBrandIsNull.andIfListGoodsEqualTo(true);
        criteriaBrandIsNull.andDeletedEqualTo(false);
        criteriaBrandIsNull.andBrandIdIsNull();

        example.or(criteriaBrandIsNull);

    	if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }
    	PageHelper.startPage(page, size);
    	return goodsMapper.selectByExample(example);
}

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallGoods findById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    public LitemallGoods findByIdVO(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleSelective(example, columns);
    }


    /**
     * 获取所有上架物品总数
     *
     * @return
     */
    public Integer queryOnSale() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int) goodsMapper.countByExample(example);
    }

    public int updateById(LitemallGoods goods) {
        goods.setUpdateTime(LocalDateTime.now());
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public void deleteById(Integer id) {
        goodsMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoods goods) {
        goods.setAddTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goodsMapper.insertSelective(goods);
    }

    /**
     * 获取所有物品总数，包括上架的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    public int count() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andDeletedEqualTo(false);
        return (int) goodsMapper.countByExample(example);
    }

    public int countToday() {

        LocalDateTime localDateTimeBegin=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime localDateTimeEnd=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andDeletedEqualTo(false).andAddTimeGreaterThanOrEqualTo(localDateTimeBegin).andAddTimeLessThanOrEqualTo(localDateTimeEnd);

        return (int) goodsMapper.countByExample(example);
    }


    public List<Integer> getCatIds(Integer brandId, String keywords, Boolean isHot, Boolean isNew) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<LitemallGoods> goodsList = goodsMapper.selectByExampleSelective(example, LitemallGoods.Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for (LitemallGoods goods : goodsList) {
            cats.add(goods.getCategoryId());
        }
        return cats;
    }

    public boolean checkExistByName(String name) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }

    public boolean checkExistByName(String name,Integer comId) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andNameEqualTo(name).andComIdEqualTo(comId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }

	public List<LitemallGoods> querySelectiveIds(List<Integer> ids,
			Integer page, Integer size, String sort) {
		 LitemallGoodsExample example = new LitemallGoodsExample();
		 LitemallGoodsExample.Criteria criteria = example.or();
		 if(ids!=null && ids.size()>0){
			 criteria.andCategoryIdIn(ids);
		 }

		 criteria.andDeletedEqualTo(false);

	     if (!StringUtils.isEmpty(sort)) {
	    	 example.setOrderByClause(sort);
	     }
         if(size==null||size<999999) {
             PageHelper.startPage(page, size);
         }

		return goodsMapper.selectByExampleSelective(example, columns);
	}


    public List<LitemallGoods> syncGoodsList(Integer comId,LocalDateTime lastUpdateTime,
                                              Integer page, Integer size) {

        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();
        if (comId!=null) {
            criteria.andComIdEqualTo(comId);
        }
        if (lastUpdateTime!=null) {
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }

         example.setOrderByClause("id asc");


        PageHelper.startPage(page, size);
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

	public List<ViewListRecommendEc> getListBrandGoods(Integer brandId, Integer offset,
                                                       Integer limit) {
        ViewListRecommendEcExample example = new ViewListRecommendEcExample();
        ViewListRecommendEcExample.Criteria criteria =example.createCriteria();
		 if(brandId!=null && brandId!=0){
			 criteria.andBrandIdEqualTo(brandId);
		 }

        if(systemConfigService.ifDisplayGoodsWhenStoreEqualZero()==false){
            criteria.andIfStoreBiggerZeroEqualTo(CharUtil.booleanConverToByte(true));
        }

        //有一个单独字段是否显示列表来标识是否在列表中显示
        criteria.andIfListGoodsEqualTo(CharUtil.booleanConverToByte(true));

		 criteria.andComIdEqualTo(1).andIsOnSaleEqualTo(CharUtil.booleanConverToByte(true)).andDeletedEqualTo(Byte.valueOf("0"));
		 PageHelper.startPage(offset, limit);
         List<ViewListRecommendEc> result=recommendEcMapper.selectByExample(example);
         //如果商品处于品项折扣活动中，则商品标价要乘以折扣比率 商品的默认规格ID已经写进了数据库
        List<Integer> productIds= result.stream().map(ViewListRecommendEc::getDefaultProductId).collect(Collectors.toList());
         if(productIds!=null&&productIds.size()>0) {
             List<ViewPromotionGoodsRebateRuleGoods> goodsRebateRuleGoodsList=  goodsRebateRuleService.getRuleByProductIds(productIds);
             if(goodsRebateRuleGoodsList!=null&&goodsRebateRuleGoodsList.size()>0) {
                 for(ViewPromotionGoodsRebateRuleGoods goodsRebateRuleGoods:goodsRebateRuleGoodsList) {
                     Optional<ViewListRecommendEc> findProduct = result.stream().filter(item -> item.getDefaultProductId().equals(goodsRebateRuleGoods.getGoodsProductId())).findFirst();
                     if(findProduct.isPresent()){
                         findProduct.get().setRetailPrice(goodsRebateRuleGoods.getRebatePrice());
                     }
                 }
             }
         }
        return result;
	}
	//获取上架商品的品牌列表
    public List<Integer> getBrandList(Integer comId) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.or();

        criteria.andDeletedEqualTo(false);
        criteria.andIsOnSaleEqualTo(true);
        if(comId!=null){
            criteria.andComIdEqualTo(comId);
        }
        List<LitemallGoods>  result= goodsMapper.selectByExampleSelective(example, BrandColumns);
        if(result!=null&&result.size()>0){
            return result.stream().map(LitemallGoods::getBrandId).distinct().collect(Collectors.toList());
        } else
        {
            return null;
        }
    }
    //更新该商品库存数据是否大于零
    public Boolean updateStoreBiggerZeroOrNot(Integer goodsId){
       Boolean ifStoreBiggerZero= goodsProductService.queryByGidAndStoreBiggerZero(goodsId);
       LitemallGoods goods=findById(goodsId);
       if(goods!=null){
           goods.setIfStoreBiggerZero(ifStoreBiggerZero);
           updateById(goods);
       }
       return true;
    }


    /**
     * 查询所有可用的商品，不包含已经下架的商品
     * @return
     */
    public List<LitemallGoods> queryAll(){
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andIsOnSaleEqualTo(true);
        return goodsMapper.selectByExample(example);
    }
}
