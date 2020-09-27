package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.ViewAuctionZhuanchangOfferCurrentMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.LitemallAuctionZhuanchangOfferCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/aucitonZhuanchangOffer")
@Validated
@Api(description = "后台管理/拍卖管理/专场拍卖活动出价记录:/admin/aucitonZhuanchangOffer")
public class AdminAuctionZhuanchangOfferCurrentController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallAuctionZhuanchangOfferCurrentService offerService;

    @Resource
    ViewAuctionZhuanchangOfferCurrentMapper zhuanchangOfferCurrentMapper;


    @RequiresPermissions("admin:aucitonZhuanchangOffer:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "专场拍卖活动出价记录"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(Integer userId,Integer rulesId,Integer rulesMxId,
                             String userName,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewAuctionZhuanchangOfferCurrent> orderList =
                offerService.getViewAuctionZhuanchangOfferCurrent(
                userId,rulesId,rulesMxId, userName,page, limit, sort);

        return ResponseUtil.okList(orderList);
    }



    @GetMapping("/getDetail")
    public Object getDetail(Integer id) {
        ViewAuctionZhuanchangOfferCurrentExample example=new ViewAuctionZhuanchangOfferCurrentExample();
        example.or().andIdEqualTo(id);
        ViewAuctionZhuanchangOfferCurrent zhuanchangOfferCurrent = zhuanchangOfferCurrentMapper.selectOneByExample(example);

        return ResponseUtil.ok(zhuanchangOfferCurrent);
    }



}
