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
@RequestMapping("/wx/dajiaPai")
@Validated
@Api(description = "微信前端/大家拍卖:/wx/dajiaPai")
public class WxDajiaPaiController {
    private final Log logger = LogFactory.getLog(WxDajiaPaiController.class);


    @Autowired
    private LitemallDiccodeService diccodeService;

    @Autowired
    private LitemallAuctionDajiaRuleCurrentService dajiaRuleCurrentService;

    @Autowired
    private LitemallAuctionDajiaOfferCurrentService dajiaOfferCurrentService;

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



    @GetMapping("dajiaPaiClass")
    public Object getDajiaPaiClass() throws Exception{
        List<LitemallDicCode> dicCodes=  diccodeService.findByDicmainIdOrDicmainName(null,"商品分类_大家拍",null );
        LitemallDicCode dicCode=new LitemallDicCode();
        dicCode.setCode("TuiJian");
        dicCode.setName("全部");
        dicCodes.add(0,dicCode);
        return ResponseUtil.okList(dicCodes);
    }

    @GetMapping("dajiaPaiList")
    public Object getDajiaPaiList(String className,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "add_time") String sort) throws Exception{
        List<LitemallAuctionDajiaRuleCurrent> dajiaRuleCurrentList=
                dajiaRuleCurrentService.queryByDajiaPaiClass(className,page,size,sort);
        return ResponseUtil.okList(dajiaRuleCurrentList);
    }

    @GetMapping("dajiaPaiDetail")
    public Object getDajiaPaiDetail(Integer userId, @NotNull Integer id) throws Exception{

        LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent=  dajiaRuleCurrentService.queryById(id);
        List<LitemallAuctionDajiaOfferCurrent> dajiaOfferCurrentList= dajiaOfferCurrentService.queryByRuleId(id,1,10);
        Map<String, Object> dajiaPaiDetail = new HashMap<String, Object>();
        dajiaPaiDetail.put("dajiaRuleCurrent",dajiaRuleCurrent);
        dajiaPaiDetail.put("dajiaOfferCurrentList",dajiaOfferCurrentList);

        Integer goodsId= dajiaRuleCurrent.getGoodsId();
        LitemallGoods goods=goodsService.findById(goodsId);
        List<LitemallGoodsAttribute> attributeList= goodsAttributeService.queryByGid(goodsId);
        Integer goodsProductId=dajiaRuleCurrent.getGoodsProductId();
        LitemallGoodsProduct goodsProduct= goodsProductService.findById(goodsProductId);
        dajiaPaiDetail.put("goods",goods);
        dajiaPaiDetail.put("attributeList",attributeList);
        dajiaPaiDetail.put("goodsProduct",goodsProduct);


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
            footprint.setGoodsSn(dajiaRuleCurrent.getGoodsSn());
            footprint.setGoodsName(dajiaRuleCurrent.getGoodsName());
            footprint.setDajiapaiId(dajiaRuleCurrent.getId());
            footprintService.add(footprint);
            //更新围观数
            dajiaRuleCurrent.setVisitCount(dajiaOfferCurrentService.getVisitPersonCount(dajiaRuleCurrent.getId()));
            dajiaRuleCurrentService.updateById(dajiaRuleCurrent);
        });



        return ResponseUtil.ok(dajiaPaiDetail);
    }


    /**
     * 查询大家拍的出价记录
     * @param ruleMxId
     * @return
     * @throws Exception
     */
    @GetMapping("getDajiaPaiOfferList")
    public Object getDajiaPaiOfferList(Integer ruleMxId) throws Exception{
        List<LitemallAuctionDajiaOfferCurrent> dajiaOfferCurrentList= dajiaOfferCurrentService.queryByRuleId(ruleMxId,1,10);
        return ResponseUtil.okList(dajiaOfferCurrentList);
    }

    /**
     * 提交拍品记录
     * @param userId 用户
     * @param ruleMxId 拍品明细ID
     * @param offerMoney 出价金额
     * @return
     * @throws Exception
     */
    @GetMapping("submitDajiaPaiOffer")
    public Object submitDajiaPaiOffer(@LoginUser Integer userId, Integer ruleMxId,
        BigDecimal offerMoney,Boolean ifUserChargeMoneyLock) throws Exception{
        if(StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
        LitemallUser user=userService.findById(userId);

        //明细和主体规则一致
        LitemallAuctionDajiaRuleCurrent auctionDajiaRuleCurrent= dajiaRuleCurrentService.findById(ruleMxId);
        if(auctionDajiaRuleCurrent.getExpireFlag()){
            return ResponseUtil.fail(502,"该拍卖活动已经过期");
        }
        if(auctionDajiaRuleCurrent.getBeginTime().compareTo(LocalDateTime.now())>=0 ){
            return ResponseUtil.fail(502,"该拍卖活动还未开始");
        }
        //为了防止用户并行或者重复提交，先判断一下用户余额是否大于保证金额
        if(ifUserChargeMoneyLock==false&&offerMoney.compareTo(BigDecimal.ZERO)==1){
            if(user.getChargeRemainMoney().compareTo(offerMoney)==-1){
                return ResponseUtil.fail(502,"用户余额不足,不能锁定，请先充值");
            }
        }

       //查询所有的出价记录,并且更新状态为出局 只显示最近的10条
        List<LitemallAuctionDajiaOfferCurrent> offerCurrents= dajiaOfferCurrentService.queryByRuleId(ruleMxId,1,10);

        Integer orderNumber=1;
        if(offerCurrents!=null&&offerCurrents.size()>0){
            if(offerMoney.compareTo(offerCurrents.get(0).getOfferMoney())<0){
                return ResponseUtil.fail(900,"其他参与人已经出具更高报价,请查看出价历史");
            }
            orderNumber=offerCurrents.size()+1;
            for(LitemallAuctionDajiaOfferCurrent offerCurrent:offerCurrents){
                //释放锁定保证金
                if(offerCurrent.getOfferState().equals("领先")&&
                        auctionDajiaRuleCurrent.getDeposit()!=null&&
                        auctionDajiaRuleCurrent.getDeposit().compareTo(BigDecimal.ZERO)==1){
                    //解锁和加锁保证金额
                    chargeMoneyLockService.handleLock("解锁",null,offerCurrent.getId(),offerCurrent.getUserId(),
                            "大家拍",auctionDajiaRuleCurrent.getDeposit(),ruleMxId,null);
                }
                offerCurrent.setOfferState("出局");
                dajiaOfferCurrentService.updateById(offerCurrent);
            }
        }
        //向出价明细表中写入记录，并且更新商品的最新报价
        LitemallAuctionDajiaOfferCurrent offerCurrent=new LitemallAuctionDajiaOfferCurrent();
        offerCurrent.setUserId(userId);
        offerCurrent.setUserName(user.getNickname());
        offerCurrent.setRulesId(ruleMxId);
        offerCurrent.setOfferTime(LocalDateTime.now());
        offerCurrent.setOfferMoney(offerMoney);
        offerCurrent.setOrdernumber(orderNumber);
        offerCurrent.setOfferState("领先");
        offerCurrent.setAddTime(LocalDateTime.now());
        offerCurrent.setUpdateTime(LocalDateTime.now());
        offerCurrent.setUserCreateId(userId);
        offerCurrent.setUserModifyId(userId);

        dajiaOfferCurrentService.create(offerCurrent);

        //添加锁定保证金 如果没有锁定，才加锁
        if(auctionDajiaRuleCurrent.getDeposit()!=null&&
                ifUserChargeMoneyLock==false&&
                auctionDajiaRuleCurrent.getDeposit().compareTo(BigDecimal.ZERO)==1) {
            chargeMoneyLockService.handleLock("加锁", null, offerCurrent.getId(), userId,
                    "大家拍", auctionDajiaRuleCurrent.getDeposit(), ruleMxId, null);
        }
        //更新主单
        LitemallAuctionDajiaRuleCurrent dajiaRuleCurrent= dajiaRuleCurrentService.findById(ruleMxId);
        dajiaRuleCurrent.setMaxPrice(offerMoney);
        dajiaRuleCurrent.setOfferId(offerCurrent.getId());
        dajiaRuleCurrent.setOfferFlag(true);
        dajiaRuleCurrent.setAuctionCount(orderNumber);
        dajiaRuleCurrent.setAuctionPersonCount(dajiaOfferCurrentService.getAuctionPersonCount(ruleMxId));
        dajiaRuleCurrent.setVisitCount(dajiaOfferCurrentService.getVisitPersonCount(ruleMxId));
        dajiaRuleCurrentService.updateById(dajiaRuleCurrent);

//        //返回大家拍的出价记录
//        List<LitemallAuctionDajiaOfferCurrent> dajiaOfferCurrentList= dajiaOfferCurrentService.queryByRuleId(ruleMxId);
//
        return ResponseUtil.ok();

    }




}
