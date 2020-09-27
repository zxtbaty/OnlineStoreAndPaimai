package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallCategory;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.jinyuanjava.litemall.db.service.LitemallCategoryService;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.wx.service.HomeCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类目服务
 */
@RestController
@RequestMapping("/wx/catalog")
@Validated
@Api(description = "微信前端/商品分类:/wx/catalog")
public class WxCatalogController {
    private final Log logger = LogFactory.getLog(WxCatalogController.class);

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallGoodsService litemallGoodsService;

    @Autowired
    private CommonDBService commonDBService;

    /**
     * 分类详情
     *
     * @param id   分类类目ID。
     *             如果分类类目ID是空，则选择第一个分类类目。
     *             需要注意，这里分类类目是一级类目
     * @return 分类详情
     */
    @GetMapping("index")
    public Object index(Integer id) {

        //这里取分类所属商贸类型为1-电商的分类数据 如果是预约，则取品牌信息
    	 List<LitemallCategory> returnList = new ArrayList<LitemallCategory>();
         LitemallCategory category = new LitemallCategory();
         category.setName("全部");
         returnList.add(category);
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1(1);
        returnList.addAll(l1CatList);
//        // 当前一级分类目录
//        LitemallCategory currentCategory = null;
//        if (id != null) {
//            currentCategory = categoryService.findById(id);
//        } else {
//            currentCategory = l1CatList.get(0);
//        }
//
//        // 当前一级分类目录对应的二级分类目录
//        List<LitemallCategory> currentSubCategory = null;
//        if (null != currentCategory) {
//            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
//        }
        // 所有二级分类目录
        List<LitemallCategory> l2CatList = categoryService.queryL2();
        returnList.addAll(l2CatList);
//        for (LitemallCategory litemallCategory : l2CatList) {
//        	l1CatList.add(litemallCategory);
//		}

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", returnList);
        return ResponseUtil.ok(data);
    }

    /**
     * 所有分类数据
     *
     * @return 所有分类数据
     */
    @GetMapping("all")
    public Object queryAll() {
        //优先从缓存中读取
        if (HomeCacheManager.hasData(HomeCacheManager.CATALOG)) {
            return ResponseUtil.ok(HomeCacheManager.getCacheData(HomeCacheManager.CATALOG));
        }

        //这里取分类所属商贸类型为1-电商的分类数据 如果是预约，则取品牌信息
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1(1);

        //所有子分类列表
        Map<Integer, List<LitemallCategory>> allList = new HashMap<>();
        List<LitemallCategory> sub;
        for (LitemallCategory category : l1CatList) {
            sub = categoryService.queryByPid(category.getId());
            allList.put(category.getId(), sub);
        }

        // 当前一级分类目录
        LitemallCategory currentCategory = l1CatList.get(0);

        // 当前一级分类目录对应的二级分类目录
        List<LitemallCategory> currentSubCategory = null;
        if (null != currentCategory) {
            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", l1CatList);
        data.put("allList", allList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);

        //缓存数据
        HomeCacheManager.loadData(HomeCacheManager.CATALOG, data);
        return ResponseUtil.ok(data);
    }

    /**
     * 当前分类栏目
     *
     * @param id 分类类目ID
     * @return 当前分类栏目
     */
    @GetMapping("current")
    public Object current(@NotNull Integer id) {
        // 当前分类
        LitemallCategory currentCategory = categoryService.findById(id);
        List<LitemallCategory> currentSubCategory = categoryService.queryByPid(currentCategory.getId());

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return ResponseUtil.ok(data);
    }

    @GetMapping("getClassListAndFirstClassGoods")
    public Object getClassListAndFirstClassGoods(String parentClassName) {
        String strSql="select distinct id from litemall_category where deleted=0 and id in ("+
                "select distinct category_id from litemall_goods where deleted=0 order by sort_order) and pid=0 ";
        if(!StringUtils.isEmpty(parentClassName)){
            strSql="select distinct id from litemall_category where deleted=0 and id in ("+
                    "select distinct category_id from litemall_goods where deleted=0 order by sort_order) and pid in ("+
                    "select id from litemall_category where deleted=0 and name='"+parentClassName+"') ";
        }
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        List<LitemallCategory> categoryList =null;

        List<Map<String, Object>> mapCategoryList=result;
        if(mapCategoryList!=null&&mapCategoryList.size()>0){
            List<Object> idS= mapCategoryList.stream().map(item->item.get("id")).collect(Collectors.toList());
            List<Integer> idList=new ArrayList<>();
            idS.forEach(item->{
                idList.add(Integer.parseInt(item.toString()));
            });
            categoryList =categoryService.queryByIds(idList);
        }
        // 查询分类
        //获取第一个分类的商品
        List<LitemallGoods> firstCategoryGoodsList=null;
        if(categoryList!=null&&categoryList.size()>0){
            LitemallCategory firstCategory=categoryList.get(0);
            firstCategoryGoodsList=litemallGoodsService.queryByCategory(firstCategory.getId(),1,10);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", categoryList);
        data.put("firstCategoryGoodsList", firstCategoryGoodsList);
        return ResponseUtil.ok(data);
    }

    @GetMapping("getGoodsListByCategoryId")
    public Object getGoodsListByCategoryId(Integer categoryId,Integer page,Integer size) {
        List<LitemallGoods>  goodsList= litemallGoodsService.queryByCategory(categoryId,page,size);
        return ResponseUtil.okList(goodsList);
    }


}
