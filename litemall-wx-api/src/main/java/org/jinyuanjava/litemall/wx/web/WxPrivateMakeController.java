package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.dto.PrivateMakOrderAllInOne;
import org.jinyuanjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 鉴权服务
 */
@RestController
@RequestMapping("/wx/privateMake")
@Validated
@Api(description = "微信前端/私人定制:/wx/privateMake")
public class WxPrivateMakeController {
    private final Log logger = LogFactory.getLog(WxPrivateMakeController.class);


    @Autowired
    private LitemallDiccodeService diccodeService;

    @Autowired
    private LitemallPrivateMakeRuleService privateMakeRuleService;

    @Autowired
    private LitemallPrivateMakeOrderService privateMakeOrderService;



    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallGoodsAttributeService goodsAttributeService;

    @Autowired
    private LitemallGoodsProductService goodsProductService;

    @Autowired
    private LitemallAuthororcorpService authororcorpService;

    @Autowired
    private LitemallAuthororcorpParaService authororcorpParaService;

    @Autowired
    private LitemallAuthororcorpItemsService authororcorpItemsService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private WxOrderService wxOrderService;

    @Autowired
    private LitemallFootprintService footprintService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @GetMapping("privateMakeClass")
    public Object getDajiaPaiClass() throws Exception{
        List<LitemallDicCode> dicCodes=  diccodeService.findByDicmainIdOrDicmainName(null,"商品分类_私人定制",null );
        LitemallDicCode dicCode=new LitemallDicCode();
        dicCode.setCode("TuiJian");
        dicCode.setName("全部");
        dicCodes.add(0,dicCode);
        return ResponseUtil.okList(dicCodes);
    }

    @GetMapping("privateMakeList")
    public Object getPrivateMakeList(String className,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) throws Exception{
        List<LitemallPrivateMakeRule> privateMakeRuleList=  privateMakeRuleService.queryByPrivateMakeClass(className,page,size);
        return ResponseUtil.okList(privateMakeRuleList);
    }

    @GetMapping("privateMakeDetail")
    public Object getPrivateMakeDetail(@NotNull Integer id,Integer userId) throws Exception{
        LitemallPrivateMakeRule privateMakeRule=  privateMakeRuleService.queryById(id);
//        List<LitemallPrivateMakeOrder> privateMakeOrderList= privateMakeOrderService.queryByRuleId(id);
        List<ViewPrivateMakeOrder> privateMakeOrderList= privateMakeOrderService.getViewPrivateMakeOrder
                (null,null,id,null,null,null,null,1,10000," add_time desc");
        Map<String, Object> privateMakeDetail = new HashMap<String, Object>();
        privateMakeDetail.put("privateMakeRule",privateMakeRule);
        privateMakeDetail.put("privateMakeOrderList",privateMakeOrderList);

        Integer goodsId= privateMakeRule.getGoodsId();
        LitemallGoods goods=goodsService.findById(goodsId);
        List<LitemallGoodsAttribute> attributeList= goodsAttributeService.queryByGid(goodsId);
        Integer goodsProductId=privateMakeRule.getGoodsProductId();
        LitemallGoodsProduct goodsProduct= goodsProductService.findById(goodsProductId);
        privateMakeDetail.put("goods",goods);
        privateMakeDetail.put("attributeList",attributeList);
        privateMakeDetail.put("goodsProduct",goodsProduct);

        Integer authorId=privateMakeRule.getAuthorId();
        if(!StringUtils.isEmpty(authorId)){
            LitemallAuthororcorp authororcorp=authororcorpService.query(authorId);
            privateMakeDetail.put("authororcorp",authororcorp);
            List<LitemallAuthororcorpPara> authororcorpParas=authororcorpParaService.queryByAuthorId(authorId);
            privateMakeDetail.put("authororcorpParas",authororcorpParas);
            List<LitemallAuthororcorpItems> authororcorpItems=authororcorpItemsService.queryByAuthorId(authorId);
            privateMakeDetail.put("authororcorpItems",authororcorpItems);
        }

        //写入用户浏览足迹
        // 记录用户的足迹 异步处理 为了处理页面访问量，只要访问都会被记录
        executorService.execute(()->{
            LitemallFootprint footprint = new LitemallFootprint();
            LitemallUser userInfo =userService.findById(userId);
            footprint.setUserId(userId);
            footprint.setWxNickname(userInfo.getNickname());
            footprint.setWeixinOpenid(userInfo.getWeixinOpenid());
            footprint.setCrmId(userInfo.getCrmId());
            footprint.setGoodsId(id);
            footprint.setGoodsSn(privateMakeRule.getGoodsSn());
            footprint.setGoodsName(privateMakeRule.getGoodsName());
            footprint.setPrivateMakeId(privateMakeRule.getId());
            footprintService.add(footprint);


        });


        return ResponseUtil.ok(privateMakeDetail);
    }

    /**
     * 提交预约订单
     * @param userId
     * @param makeOrder
     * @return
     */
    @PostMapping("submitPrivateMake")
    public Object submitCommand(@LoginUser Integer userId,
        @RequestBody(required=false) PrivateMakOrderAllInOne makeOrder) {
        if(StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
       return wxOrderService.submitPrivateMakeOrder(userId,makeOrder);

    }

}
