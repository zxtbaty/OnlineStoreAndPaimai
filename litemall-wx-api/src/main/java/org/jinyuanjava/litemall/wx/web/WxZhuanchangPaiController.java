package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jinyuanjava.litemall.core.util.*;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;

import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@RequestMapping("/wx/zhuanchang")
@Validated
@Api(description = "微信前端/专场拍卖管理:/wx/zhuanchang")
public class WxZhuanchangPaiController {
    private final Log logger = LogFactory.getLog(WxZhuanchangPaiController.class);


    @Autowired
    private LitemallAuctionZhuanchangRuleCurrentService zhuanchangRuleService;


    @Autowired
    private LitemallAuctionZhuanchangGoodsCurrentService zhuanchangGoodsService;

    @Autowired
    private LitemallAuctionZhuanchangOfferCurrentService zhuanchangOfferService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallGoodsAttributeService goodsAttributeService;

    @Autowired
    private LitemallGoodsProductService goodsProductService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallFootprintService footprintService;

    @Autowired
    private LitemallUserChargeMoneyLockService chargeMoneyLockService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);




    @GetMapping("zhuanChangPaimaiIng")
    public Object getZhuanChangPaimaiIng(@LoginUser Integer userId, @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(defaultValue = "add_time") String sort) throws Exception{

       List<LitemallAuctionZhuanchangRuleCurrent> paimaiIngs=
               zhuanchangRuleService.queryHaveBeginZhuanChang(page,size,sort);
       return ResponseUtil.okList(paimaiIngs);
    }

    @GetMapping("zhuanChangPaimaiPreview")
    public Object getZhuanChangPaimaiPreview(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer limit) throws Exception{
        List<LitemallAuctionZhuanchangRuleCurrent> paimaiPreviews=
                zhuanchangRuleService.queryUnBeginAndMayPreview(page,limit);
        return ResponseUtil.okList(paimaiPreviews);
    }

    @GetMapping("zhuanChangPaimaiEnding")
    public Object getZhuanChangPaimaiEnding(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer limit) throws Exception{
        List<LitemallAuctionZhuanchangRuleCurrent> endingList=
                zhuanchangRuleService.queryHaveExpiredList(page,limit);
        return ResponseUtil.okList(endingList);
    }

    @GetMapping("zhuanChangPaimaiDetail")
    public Object getZhuanChangPaimaiDetail(@NotNull Integer id) throws Exception{
        LitemallAuctionZhuanchangRuleCurrent zhuanchangRule=  zhuanchangRuleService.findById(id);
        List<LitemallAuctionZhuanchangGoodsCurrent> zhuanchangGoodsList=  zhuanchangGoodsService.queryByRuleId(id);
//        List<LitemallAuctionDajiaOfferCurrent> zhuanchangOfferList= zhuanchangOfferService.queryByRuleId(id);
        Map<String, Object> paimaiDetails = new HashMap<String, Object>();
        paimaiDetails.put("zhuanchangRule",zhuanchangRule);
        paimaiDetails.put("zhuanchangGoodsList",zhuanchangGoodsList);
//        paimaiDetails.put("zhuanchangOfferList",zhuanchangOfferList);
        return ResponseUtil.ok(paimaiDetails);
    }

    @GetMapping("zhuanChangPaimaiGoodsDetail")
    public Object getZhuanChangPaimaiGoodsDetail(Integer userId, @NotNull Integer id) throws Exception{
        LitemallAuctionZhuanchangGoodsCurrent zhuanchangGoods=  zhuanchangGoodsService.queryById(id);
        Integer ruleId=zhuanchangGoods.getZhuanchangId();
        LitemallAuctionZhuanchangRuleCurrent rules=zhuanchangRuleService.findById(ruleId);
        //只显示最近10条
        List<LitemallAuctionZhuanchangOfferCurrent> zhuanchangOfferList= zhuanchangOfferService.queryByRuleMxId(id,1,10);
        Integer goodsId= zhuanchangGoods.getGoodsId();
        LitemallGoods goods=goodsService.findById(goodsId);
        List<LitemallGoodsAttribute> attributeList= goodsAttributeService.queryByGid(goodsId);
        Integer goodsProductId=zhuanchangGoods.getGoodsProductId();
        LitemallGoodsProduct goodsProduct= goodsProductService.findById(goodsProductId);
        Map<String, Object> paimaiDetails = new HashMap<String, Object>();
        paimaiDetails.put("zhuanchangRule",rules);
        paimaiDetails.put("zhuanchangGoods",zhuanchangGoods);
        paimaiDetails.put("zhuanchangOfferList",zhuanchangOfferList);
        paimaiDetails.put("goods",goods);
        paimaiDetails.put("attributeList",attributeList);
        paimaiDetails.put("goodsProduct",goodsProduct);

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
            footprint.setGoodsSn(zhuanchangGoods.getGoodsSn());
            footprint.setGoodsName(zhuanchangGoods.getGoodsName());
            footprint.setZhuanchangMxId(zhuanchangGoods.getId());
            footprint.setZhuanchangId(zhuanchangGoods.getZhuanchangId());
            footprintService.add(footprint);
            //更新围观数
            zhuanchangGoods.setVisitCount(zhuanchangOfferService.getVisitPersonCountByRuleMxId(zhuanchangGoods.getId()));
            zhuanchangGoodsService.updateById(zhuanchangGoods);

            rules.setVisitCount(zhuanchangOfferService.getVisitPersonCountByMainId(zhuanchangGoods.getZhuanchangId()));
            zhuanchangRuleService.updateById(rules);
        });

        return ResponseUtil.ok(paimaiDetails);
    }

    /**
     * 提交拍品记录
     * @param userId 用户
     * @param ruleMxId 拍品明细ID
     * @param offerMoney 出价金额
     * @return
     * @throws Exception
     */
    @GetMapping("submitZhuanchangOffer")
    public Object submitZhuanchangOffer(@LoginUser Integer userId, Integer ruleMxId,
        BigDecimal offerMoney,Boolean ifUserChargeMoneyLock) throws Exception{
        if(StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
        LitemallUser user=userService.findById(userId);

        //明细和主体规则一致
        LitemallAuctionZhuanchangGoodsCurrent auctionZhuanchangGoodsCurrent= zhuanchangGoodsService.queryById(ruleMxId);

        LitemallAuctionZhuanchangRuleCurrent auctionZhuanchangRuleCurrent=
                zhuanchangRuleService.queryById(auctionZhuanchangGoodsCurrent.getZhuanchangId());

        if(auctionZhuanchangRuleCurrent.getExpireFlag()){
            return ResponseUtil.fail(502,"该专场活动已经过期");
        }
        if(auctionZhuanchangRuleCurrent.getBeginTime().compareTo(LocalDateTime.now())>=0 ){
            return ResponseUtil.fail(502,"该专场活动还未开始");
        }
        //为了防止用户并行或者重复提交，先判断一下用户余额是否大于保证金额
        if(ifUserChargeMoneyLock==false&&offerMoney.compareTo(BigDecimal.ZERO)==1){
            if(user.getChargeRemainMoney().compareTo(offerMoney)==-1){
                return ResponseUtil.fail(502,"用户余额不足,不能锁定，请先充值");
            }
        }
        //查询所有的出价记录,并且更新状态为出局

        List<LitemallAuctionZhuanchangOfferCurrent> offerCurrents= zhuanchangOfferService.queryByRuleId(ruleMxId);

        Integer orderNumber=1;
        if(offerCurrents!=null&&offerCurrents.size()>0){
            if(offerMoney.compareTo(offerCurrents.get(0).getOfferMoney())<0){
                return ResponseUtil.fail(900,"其他参与人已经出具更高报价,请查看出价历史");
            }
            orderNumber=offerCurrents.size()+1;
            for(LitemallAuctionZhuanchangOfferCurrent offerCurrent:offerCurrents){
                //释放锁定保证金
                if(offerCurrent.getOfferState().equals("领先")&&auctionZhuanchangGoodsCurrent.getDeposit()!=null&&
                        auctionZhuanchangGoodsCurrent.getDeposit().compareTo(BigDecimal.ZERO)==1
                ){
                    chargeMoneyLockService.handleLock("解锁",null,offerCurrent.getId(),offerCurrent.getUserId(),
                            "专场拍",auctionZhuanchangGoodsCurrent.getDeposit(),ruleMxId,null);
                }
                offerCurrent.setOfferState("出局");
                zhuanchangOfferService.updateById(offerCurrent);
            }
        }
        //向出价明细表中写入记录，并且更新商品的最新报价
        LitemallAuctionZhuanchangOfferCurrent offerCurrent=new LitemallAuctionZhuanchangOfferCurrent();
        offerCurrent.setUserId(userId);
        offerCurrent.setUserName(user.getNickname());
        offerCurrent.setRulesId(auctionZhuanchangGoodsCurrent.getZhuanchangId());
        offerCurrent.setRulesMxId(ruleMxId);
        offerCurrent.setOfferTime(LocalDateTime.now());
        offerCurrent.setOfferMoney(offerMoney);
        offerCurrent.setOrdernumber(orderNumber);
        offerCurrent.setOfferState("领先");
        offerCurrent.setAddTime(LocalDateTime.now());
        offerCurrent.setUpdateTime(LocalDateTime.now());
        offerCurrent.setUserCreateId(userId);
        offerCurrent.setUserModifyId(userId);

        zhuanchangOfferService.create(offerCurrent);
        //添加锁定保证金 如果没有锁定，则加锁
        if(auctionZhuanchangGoodsCurrent.getDeposit()!=null&&
                ifUserChargeMoneyLock==false&&
                auctionZhuanchangGoodsCurrent.getDeposit().compareTo(BigDecimal.ZERO)==1) {
            chargeMoneyLockService.handleLock("加锁", null, offerCurrent.getId(), userId,
                    "专场拍", auctionZhuanchangGoodsCurrent.getDeposit(), ruleMxId, null);
        }
        //更新专场拍卖商品汇总情况

        auctionZhuanchangGoodsCurrent.setMaxPrice(offerMoney);
        auctionZhuanchangGoodsCurrent.setOfferId(offerCurrent.getId());
        auctionZhuanchangGoodsCurrent.setOfferFlag(true);
        auctionZhuanchangGoodsCurrent.setAuctionCount(orderNumber);
        auctionZhuanchangGoodsCurrent.setAuctionPersonCount(zhuanchangOfferService.getAuctionPersonCount(ruleMxId));
        auctionZhuanchangGoodsCurrent.setVisitCount(zhuanchangOfferService.getVisitPersonCountByRuleMxId(ruleMxId));
        zhuanchangGoodsService.updateById(auctionZhuanchangGoodsCurrent);

        //更新专场拍卖的情况统计
        Map getSumInfo= zhuanchangOfferService.getZhuangChangSumInfo(auctionZhuanchangGoodsCurrent.getZhuanchangId());
        if(getSumInfo!=null){
            Object SumOfferCount=getSumInfo.get("SumOfferCount");
            if(SumOfferCount!=null){
                auctionZhuanchangRuleCurrent.setAuctionCount(Integer.parseInt(SumOfferCount.toString()));
            }
            Object sumPersonCount=getSumInfo.get("sumPersonCount");
            if(sumPersonCount!=null){
                auctionZhuanchangRuleCurrent.setAuctionPersonCount(Integer.parseInt(sumPersonCount.toString()));
            }
        }
        auctionZhuanchangRuleCurrent.setVisitCount(
                zhuanchangOfferService.getVisitPersonCountByMainId(auctionZhuanchangGoodsCurrent.getZhuanchangId()));
        zhuanchangRuleService.updateById(auctionZhuanchangRuleCurrent);
        //返回大家拍的出价记录,出价记录由前台刷新

        return ResponseUtil.ok();

    }



}
