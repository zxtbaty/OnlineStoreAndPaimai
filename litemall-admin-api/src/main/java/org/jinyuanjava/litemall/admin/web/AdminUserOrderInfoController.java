package org.jinyuanjava.litemall.admin.web;

import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.ViewUserOrderInfoMapper;
import org.jinyuanjava.litemall.db.domain.ViewUserOrderInfo;
import org.jinyuanjava.litemall.db.domain.ViewUserOrderInfoExample;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/userorderinfo")
@Validated
@Api(description = "后台管理/消息管理/用户订单消息:/admin/userorderinfo")
public class AdminUserOrderInfoController {
    private final Log logger = LogFactory.getLog(AdminUserOrderInfoController.class);

    @Resource
    private ViewUserOrderInfoMapper viewUserOrderInfoMapper;



    @RequiresPermissions("admin:userorderinfo:list")
    @RequiresPermissionsDesc(menu={"消息管理","用户订单消息"},button = "查询")
    @GetMapping("list")
    public Object List(
            String orderSn,Short orderStatus,
            String userName,Integer expireFlag,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "add_time") String sort
                       ){
        ViewUserOrderInfoExample example=new ViewUserOrderInfoExample();
        ViewUserOrderInfoExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            criteria.andOrderStatusEqualTo(orderStatus);
        }
        if(!StringUtils.isEmpty(userName)){
            criteria.andUsernameEqualTo(userName);
        }
        PageHelper.startPage(page,limit);
        if(!StringUtils.isEmpty(sort)){
            example.orderBy(sort);
        }
        List<ViewUserOrderInfo> viewUserOrderInfoList=viewUserOrderInfoMapper.selectByExample(example);
        return ResponseUtil.okList(viewUserOrderInfoList);
    }

    @RequiresPermissions("admin:userorderinfo:read")
    @RequiresPermissionsDesc(menu={"消息管理","用户订单消息"},button = "详情")
    @GetMapping("read")
    public Object read(@NotNull Integer id){
        ViewUserOrderInfoExample example=new ViewUserOrderInfoExample();
        example.or().andIdEqualTo(id);
        ViewUserOrderInfo userOrderInfo=viewUserOrderInfoMapper.selectOneByExample(example);
        return ResponseUtil.ok(userOrderInfo);
    }

}
