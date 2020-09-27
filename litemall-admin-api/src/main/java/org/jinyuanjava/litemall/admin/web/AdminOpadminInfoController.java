package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminInfo;
import org.jinyuanjava.litemall.db.service.LitemallOpadminInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/opadmininfo")
@Validated
@Api(description = "后台管理/消息管理/后端消息列表:/admin/opadmininfo")
public class AdminOpadminInfoController {
    private final Log logger = LogFactory.getLog(AdminOpadminInfoController.class);

    @Autowired
    private LitemallOpadminInfoService opadminInfoService;

    @RequiresPermissions("admin:opadmininfo:list")
    @RequiresPermissionsDesc(menu={"消息管理","后端消息列表"},button = "查询")
    @GetMapping("list")
    public Object List(
            String opadminName,
            String typeCode,String typeName,String soruceCode,
            String sourceName,String title,String content,
            String strBeginDate,String strEndDate,
            Boolean ifViewed,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "add_time") String sort
                       ){
        List<LitemallOpadminInfo> opadminInfoList= opadminInfoService.querySelective(opadminName,typeCode,typeName,
                soruceCode,sourceName,title,content,strBeginDate,strEndDate,ifViewed,page,limit,sort);
        return ResponseUtil.okList(opadminInfoList);
    }

    @RequiresPermissions("admin:opadmininfo:read")
    @RequiresPermissionsDesc(menu={"消息管理","后端消息列表"},button = "详情")
    @GetMapping("read")
    public Object read(@NotNull Integer id){
        LitemallOpadminInfo opadminInfo=opadminInfoService.findById(id);
        return ResponseUtil.ok(opadminInfo);
    }

    @RequiresPermissions("admin:opadmininfo:count")
    @RequiresPermissionsDesc(menu={"消息管理","后端消息列表"},button = "查看数量")
    @GetMapping("count")
    public Object count(
            String loginUserName
    ){
        Long countRecords= opadminInfoService.countSelective(loginUserName);
        return ResponseUtil.ok(countRecords);
    }

    @RequiresPermissions("admin:opadmininfo:haveview")
    @RequiresPermissionsDesc(menu={"消息管理","后端消息列表"},button = "已查看")
    @GetMapping("haveview")
    public Object haveview(Integer[]  infoIds){
        Boolean execResult= opadminInfoService.updateIfViewSelective(infoIds);
        return ResponseUtil.ok(execResult);
    }

}
