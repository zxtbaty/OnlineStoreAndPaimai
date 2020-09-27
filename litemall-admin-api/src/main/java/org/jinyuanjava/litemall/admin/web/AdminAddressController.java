package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAddress;
import org.jinyuanjava.litemall.db.service.LitemallAddressService;
import org.jinyuanjava.litemall.db.service.LitemallRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/address")
@Validated
@Api(description = "后台管理/用户管理/收货地址:/admin/address")
public class AdminAddressController {
    private final Log logger = LogFactory.getLog(AdminAddressController.class);

    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallRegionService regionService;

    @RequiresPermissions("admin:address:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "收货地址"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer userId, String name,
                       String wxNickname,String tel,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {

        List<LitemallAddress> addressList = addressService.querySelective(userId, name,wxNickname,tel, page, limit, sort);
        return ResponseUtil.okList(addressList);
    }
}
