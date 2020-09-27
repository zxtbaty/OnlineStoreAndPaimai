package org.jinyuanjava.litemall.wx.service;

import com.github.pagehelper.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallGrouponRules;
import org.jinyuanjava.litemall.db.service.LitemallGoodsService;
import org.jinyuanjava.litemall.db.service.LitemallGrouponRulesService;
import org.jinyuanjava.litemall.db.service.LitemallGrouponOrderService;
import org.jinyuanjava.litemall.wx.vo.GrouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WxGrouponRuleService {
    private final Log logger = LogFactory.getLog(WxGrouponRuleService.class);

    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallGrouponOrderService grouponService;
    @Autowired
    private LitemallGoodsService goodsService;


    public List<GrouponRuleVo> queryList(Integer page, Integer size) {
        return queryList(page, size, "add_time");
    }


    public List<GrouponRuleVo> queryList(Integer page, Integer size, String sort) {
        Page<LitemallGrouponRules> grouponRulesList = (Page)grouponRulesService.queryList(page, size, sort);

        Page<GrouponRuleVo> grouponList = new Page<GrouponRuleVo>();
        grouponList.setPages(grouponRulesList.getPages());
        grouponList.setPageNum(grouponRulesList.getPageNum());
        grouponList.setPageSize(grouponRulesList.getPageSize());
        grouponList.setTotal(grouponRulesList.getTotal());

        for (LitemallGrouponRules rule : grouponRulesList) {
            Integer goodsId = rule.getGoodsId();
            LitemallGoods goods = goodsService.findById(goodsId);
            if (goods == null)
                continue;

            GrouponRuleVo grouponRuleVo = new GrouponRuleVo();
            grouponRuleVo.setId(goods.getId());
            grouponRuleVo.setName(goods.getName());
            grouponRuleVo.setBrief(goods.getBrief());
            grouponRuleVo.setPicUrl(goods.getPicUrl());
            grouponRuleVo.setCounterPrice(goods.getCounterPrice());
            grouponRuleVo.setRetailPrice(goods.getRetailPrice());
            grouponRuleVo.setGrouponPrice(goods.getRetailPrice().subtract(rule.getGrouponPrice()));
            grouponRuleVo.setGrouponDiscount(rule.getGrouponPrice());
            grouponRuleVo.setGrouponMember(rule.getGroupMinperson());
            grouponList.add(grouponRuleVo);
        }

        return grouponList;
    }
}
