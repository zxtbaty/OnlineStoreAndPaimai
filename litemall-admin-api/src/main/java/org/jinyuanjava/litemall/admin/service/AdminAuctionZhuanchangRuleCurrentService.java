package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.AuctionZhuanchangRuleCurrentAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangGoodsCurrent;
import org.jinyuanjava.litemall.db.domain.LitemallAuctionZhuanchangRuleCurrent;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminAuctionZhuanchangRuleCurrentService {
    private final Log logger = LogFactory.getLog(AdminAuctionZhuanchangRuleCurrentService.class);

    @Autowired
    private LitemallAuctionZhuanchangRuleCurrentService zhuanchangRuleCurrentService;

    @Autowired
    private LitemallAuctionZhuanchangGoodsCurrentService zhuanchangGoodsCurrentService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallAuthororcorpService authororcorpService;



    public Object list(String zhuanchangName, Integer expireFlag,
                       String goodsSn, String goodsName,
                       Integer page, Integer limit, String sort) {
        List<LitemallAuctionZhuanchangRuleCurrent> zhuanchangRuleCurrents = zhuanchangRuleCurrentService.querySelective(
                zhuanchangName,expireFlag,goodsSn,goodsName, page, limit, sort);
        return ResponseUtil.okList(zhuanchangRuleCurrents);
    }

    private Object validate(AuctionZhuanchangRuleCurrentAllinone ruleCurrentAllinone) {
        LitemallAuctionZhuanchangRuleCurrent ruleCurrent = ruleCurrentAllinone.getZhuanchangRuleCurrent();
        String name = ruleCurrent.getZhuanchangName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "专场活动名称不不能为空");
        }
        if (zhuanchangRuleCurrentService.checkExistByName(ruleCurrent.getZhuanchangName(),ruleCurrent.getId())) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "专场活动名称已经存在");
        }
        if (StringUtils.isEmpty(ruleCurrent.getAuthorName())) {
            return ResponseUtil.fail(401, "专场活动出品人不能为空");
        }

        if (StringUtils.isEmpty(ruleCurrent.getBeginTime())) {
            return ResponseUtil.fail(401, "专场活动起始时间不能为空");
        }
        if (StringUtils.isEmpty(ruleCurrent.getEndTime())) {
            return ResponseUtil.fail(401, "专场活动截止时间不能为空");
        }
        LitemallAuctionZhuanchangGoodsCurrent[] goodsArray = ruleCurrentAllinone.getZhuanchangGoodsCurrents();
        for (LitemallAuctionZhuanchangGoodsCurrent goods : goodsArray) {
            String goodsName=goods.getGoodsName();
            if (StringUtils.isEmpty(goodsName)) {
                return ResponseUtil.fail(401, "商品名称不能为空");
            }
        }

        return null;
    }

    /**
     * 编辑品项折扣活动
     * <p>
     * TODO
     * 需要判断是否明细主键ID已经存在，如果存在，则更新信息，如果不存在，则插入数据
     */
    @Transactional
    public Object update(AuctionZhuanchangRuleCurrentAllinone ruleCurrentAllinone) {
        Object error = validate(ruleCurrentAllinone);
        if (error != null) {
            return error;
        }
        LitemallAuctionZhuanchangRuleCurrent ruleCurrent=ruleCurrentAllinone.getZhuanchangRuleCurrent();
        LitemallAuctionZhuanchangGoodsCurrent[] goodsRebateRuleGoodsArray = ruleCurrentAllinone.getZhuanchangGoodsCurrents();

        if(goodsRebateRuleGoodsArray!=null) {
            ruleCurrent.setGoodsCount(goodsRebateRuleGoodsArray.length);
        } else
        {
            ruleCurrent.setGoodsCount(0);
        }
        Integer mainId = ruleCurrent.getId();

        LitemallAuthororcorp authororcorp=authororcorpService.query(ruleCurrent.getAuthorId());
        ruleCurrent.setAuthorZuopin(authororcorp.getGallery());
        ruleCurrent.setAuthorDesc(authororcorp.getDesc());

        if(mainId==null){
            zhuanchangRuleCurrentService.createRules(ruleCurrent);
        }else
        {
            zhuanchangRuleCurrentService.updateById(ruleCurrent);
        }

        // 品项折扣列表
        for (LitemallAuctionZhuanchangGoodsCurrent zhuanchangRuleGoods : goodsRebateRuleGoodsArray) {
            zhuanchangRuleGoods.setZhuanchangId(ruleCurrent.getId());
            //更新商品图
            LitemallGoods goods=goodsService.findById(zhuanchangRuleGoods.getGoodsId());
            if(goods!=null){
                zhuanchangRuleGoods.setGalleryBig(goods.getGalleryBig());
                zhuanchangRuleGoods.setGalllerySmall(goods.getGallery());
                zhuanchangRuleGoods.setAuctionPicHead(goods.getPicUrl());
            }
            Integer codeId=zhuanchangRuleGoods.getId();
            if(codeId==null){
                zhuanchangGoodsCurrentService.createRulesGoods(zhuanchangRuleGoods);
            } else
            {
                zhuanchangGoodsCurrentService.updateById(zhuanchangRuleGoods);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除专场拍卖活动
     * @param ruleCurrent
     * @return
     */
    @Transactional
    public Object delete(LitemallAuctionZhuanchangRuleCurrent ruleCurrent) {
        Integer goodsRebateRuleId= ruleCurrent.getId();
        if (goodsRebateRuleId == null) {
            return ResponseUtil.badArgument();
        }
        zhuanchangRuleCurrentService.delete(goodsRebateRuleId);
        zhuanchangGoodsCurrentService.deleteByRuleId(goodsRebateRuleId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(AuctionZhuanchangRuleCurrentAllinone ruleCurrentAllinone) {
        Object error = validate(ruleCurrentAllinone);
        if (error != null) {
            return error;
        }

        LitemallAuctionZhuanchangRuleCurrent ruleCurrent = ruleCurrentAllinone.getZhuanchangRuleCurrent();
        LitemallAuctionZhuanchangGoodsCurrent[] goodsRebateRuleGoodsArray = ruleCurrentAllinone.getZhuanchangGoodsCurrents();



        //保存品项折扣活动主表
        if(goodsRebateRuleGoodsArray!=null) {
            ruleCurrent.setGoodsCount(goodsRebateRuleGoodsArray.length);
        } else
        {
            ruleCurrent.setGoodsCount(0);
        }

        LitemallAuthororcorp authororcorp=authororcorpService.query(ruleCurrent.getAuthorId());
        ruleCurrent.setAuthorZuopin(authororcorp.getGallery());
        ruleCurrent.setAuthorDesc(authororcorp.getDesc());

        zhuanchangRuleCurrentService.createRules(ruleCurrent);
        // 保存品项折扣活动商品列表明细
        for (LitemallAuctionZhuanchangGoodsCurrent zhuanchangRuleGoods : goodsRebateRuleGoodsArray) {

            zhuanchangRuleGoods.setZhuanchangId(ruleCurrent.getId());
            //更新商品图
            LitemallGoods goods=goodsService.findById(zhuanchangRuleGoods.getGoodsId());
            if(goods!=null){
                zhuanchangRuleGoods.setGalleryBig(goods.getGalleryBig());
                zhuanchangRuleGoods.setGalllerySmall(goods.getGallery());
                zhuanchangRuleGoods.setAuctionPicHead(goods.getPicUrl());
            }
            zhuanchangGoodsCurrentService.createRulesGoods(zhuanchangRuleGoods);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallAuctionZhuanchangRuleCurrent ruleCurrent = zhuanchangRuleCurrentService.findById(id);
        List<LitemallAuctionZhuanchangGoodsCurrent> goodsRebateGoodsList = zhuanchangGoodsCurrentService.queryByRuleId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("ruleCurrent", ruleCurrent);
        data.put("goodsList", goodsRebateGoodsList);

        return ResponseUtil.ok(data);
    }


}
