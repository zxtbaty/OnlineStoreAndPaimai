package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.ViewAuctionDajiaOfferCurrentMapper;
import org.jinyuanjava.litemall.db.domain.ViewAuctionDajiaOfferCurrent;
import org.jinyuanjava.litemall.db.domain.ViewAuctionDajiaOfferCurrentExample;
import org.jinyuanjava.litemall.db.service.LitemallAuctionDajiaOfferCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/dajiaPaimaiOffer")
@Validated
@Api(description = "后台管理/拍卖管理/大家拍出价记录:/admin/dajiaPaimaiOffer")
public class AdminAuctionDajiaOfferCurrentController {
    private final Log logger = LogFactory.getLog(AdminGrouponOrdersController.class);

    @Autowired
    private LitemallAuctionDajiaOfferCurrentService offerService;

    @Resource
    private ViewAuctionDajiaOfferCurrentMapper dajiaOfferCurrentMapper;

    @RequiresPermissions("admin:dajiaPaimaiOffer:list")
    @RequiresPermissionsDesc(menu={"拍卖管理" , "大家拍出价记录"}, button="查询")
    @GetMapping("/list")
    public Object listRecord(Integer userId,Integer rulesId,
                             String userName,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort
                             ) {
        List<ViewAuctionDajiaOfferCurrent> orderList = offerService.querySelective(
                userId,rulesId,userName,page, limit, sort);

        return ResponseUtil.okList(orderList);
    }

    @GetMapping("/getDetail")
    public Object getDetail(Integer id) {
        ViewAuctionDajiaOfferCurrentExample example=new ViewAuctionDajiaOfferCurrentExample();
        example.or().andIdEqualTo(id);
        ViewAuctionDajiaOfferCurrent dajiaOfferCurrent = dajiaOfferCurrentMapper.selectOneByExample(example);

        return ResponseUtil.ok(dajiaOfferCurrent);
    }

}
