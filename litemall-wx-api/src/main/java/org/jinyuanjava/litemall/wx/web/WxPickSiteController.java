package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallPickSite;
import org.jinyuanjava.litemall.db.service.LitemallPickSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/picksite")
@Validated
@Api(description = "微信前端/自提货点:/wx/picksite")
public class WxPickSiteController {
    private final Log logger = LogFactory.getLog(WxPickSiteController.class);

    @Autowired
    private LitemallPickSiteService pickSiteService;

    @GetMapping("list")
    public Object List(String name,String remark,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
    ){
        List<LitemallPickSite> pickSiteList=pickSiteService.querySelectiveEnable(name,page,limit,sort);
        return ResponseUtil.okList(pickSiteList);
    }

}
