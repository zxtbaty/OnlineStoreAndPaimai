package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.domain.ViewUserChargeMoney;
import org.jinyuanjava.litemall.db.domain.ViewUserChargeMoneyLock;
import org.jinyuanjava.litemall.db.service.LitemallUserChargeMoneyLockService;
import org.jinyuanjava.litemall.db.service.LitemallUserChargeMoneyService;
import org.jinyuanjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Validated
@Api(description = "后台管理/用户管理/会员管理:/admin/user")
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallUserChargeMoneyService chargeMoneyService;

    @Autowired
    private LitemallUserChargeMoneyLockService chargeMoneyLockService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String username,String nickname, String mobile,
                       String userClassAttr1Code,String userClassAttr2Code,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallUser> userList = userService.querySelective(username,nickname,mobile,userClassAttr1Code,userClassAttr2Code, page, limit, sort);
        return ResponseUtil.okList(userList);
    }


    @RequiresPermissions("admin:user:update")
    @RequiresPermissionsDesc(menu = {"用户管理","会员管理"},button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallUser user){
        int result= userService.updateById(user);
        return ResponseUtil.ok(result);
    }

    @RequiresPermissions("admin:user:listChargeMoney")
    @RequiresPermissionsDesc(menu = {"用户管理","保证金充值"},button = "查询")
    @GetMapping("/listChargeMoney")
    public Object listChargeMoney(Integer userId,
                                  String beginChargeTime, String endChargeTime,
                                  String payMethod, String chargeSheetNo, String payNo, Integer payReturn,
                                  String username, String userPhone,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(defaultValue = "add_time") String sort){
        List<ViewUserChargeMoney>  viewUserChargeMonies=
                chargeMoneyService.querySelective(userId,beginChargeTime,endChargeTime,payMethod,
                        chargeSheetNo,payNo,payReturn,username,userPhone,page,limit,sort);
        return ResponseUtil.okList(viewUserChargeMonies);
    }

    @RequiresPermissions("admin:user:listChargeMoneyLock")
    @RequiresPermissionsDesc(menu = {"用户管理","保证金占用"},button = "查询")
    @GetMapping("/listChargeMoneyLock")
    public Object listChargeMoneyLock(Integer userId,
        String beginLockTime, String endLockTime,
        String lockType, Integer ruleId, String goodsName, String orderSn,
        String userPhone,String username,Boolean ifUnlockIsNull,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer limit,
        @RequestParam(defaultValue = "add_time") String sort){
        List<ViewUserChargeMoneyLock>  viewUserChargeMoneyLockList=
                chargeMoneyLockService.querySelective(userId,beginLockTime,endLockTime,lockType,
                        ruleId,goodsName,orderSn,userPhone,username,ifUnlockIsNull,page,limit,sort);
        return ResponseUtil.okList(viewUserChargeMoneyLockList);
    }
}
