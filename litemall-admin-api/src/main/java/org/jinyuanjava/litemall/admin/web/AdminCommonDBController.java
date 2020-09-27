package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.util.MapConvertCamel;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/commonDB")
@Validated
@Api(description = "后台管理/通用数据执行:/admin/commonDB")
public class AdminCommonDBController {
    private final Log logger = LogFactory.getLog(AdminCommonDBController.class);

    @Autowired
    private CommonDBService commonDBService;

    @RequiresPermissions("admin:commonDB:exec")
    @RequiresPermissionsDesc(menu={"维护工具" , "通用数据执行"}, button="执行")
    @PostMapping("/exec")
    public Object exec(@RequestBody String data) {
        try {
           String strSql = JacksonUtil.parseString(data, "strSql");
           commonDBService.procedureExec(strSql);
            return ResponseUtil.ok();
        } catch (Exception e){
            return ResponseUtil.ok("错误:"+e.getMessage());
        }

    }

    @RequiresPermissions("admin:commonDB:insert")
    @RequiresPermissionsDesc(menu={"维护工具" , "通用数据执行"}, button="插入")
    @PostMapping("/insert")
    public Object insert(@RequestBody String data) {
        try {
            String strSql = JacksonUtil.parseString(data, "strSql");
            Map<String, Object> param = new HashMap<>();
            param.put("sqlS", strSql);
            commonDBService.procedureInsert(param);
            Integer returnId=(Integer) param.get("id");
            return ResponseUtil.ok(returnId);
        } catch (Exception e){
            return ResponseUtil.ok("错误:"+e.getMessage());
        }
    }

    @RequiresPermissions("admin:commonDB:select")
    @RequiresPermissionsDesc(menu={"维护工具" , "通用数据执行"}, button="选择不分页")
    @PostMapping("/select")
    public Object select(@RequestBody String data) {
        try {
            String strSql = JacksonUtil.parseString(data, "strSql");
            Boolean ifCamel = JacksonUtil.parseBoolean(data, "ifCamel");
            List<Integer> ids= JacksonUtil.parseIntegerList(data, "ids");
            if(ids!=null&&ids.size()>0){
                int whereIndex= strSql.toLowerCase().indexOf("where");
                if(whereIndex>0){
                    strSql=strSql.substring(0,whereIndex)+" where id in ("+
                            commonDBService.listToInteger(ids)+") and "+
                            strSql.substring(whereIndex+5,strSql.length());
                }
            }
            Map<String, Object> param = new HashMap<>();
            param.put("sqlS", strSql);
            List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
            Integer count=(Integer) param.get("total");
            List<Map<String, Object>> convertResult=result;
            if(ifCamel){
                convertResult=new ArrayList<>();
                //转驼峰命名法
                for(Map<String, Object> row:result) {
                    Map<String, Object> mapRow=  MapConvertCamel.toReplaceKeyLow(row);
                    convertResult.add(mapRow);
                }
            }
            return ResponseUtil.okList(convertResult,count,1,999999);
        } catch (Exception e){
            return ResponseUtil.ok("错误:"+e.getMessage());
        }
    }


    /**
     *
     * @param data
     * @return
     */
    @RequiresPermissions("admin:commonDB:selectPage")
    @RequiresPermissionsDesc(menu={"维护工具" , "通用数据执行"}, button="选择分页")
    @PostMapping("/selectPage")
    public Object selectPage(@RequestBody String data) {
        try {
            String strSql = JacksonUtil.parseString(data, "strSql");
            Boolean ifCamel = JacksonUtil.parseBoolean(data, "ifCamel");
            Integer page = JacksonUtil.parseInteger(data, "page");
            Integer limit = JacksonUtil.parseInteger(data, "limit");
            Map<String, Object> param = new HashMap<>();
            if(strSql.toLowerCase().startsWith("select")){
                strSql=strSql.toLowerCase().substring(0,6)+" sql_calc_found_rows "+
                        strSql.toLowerCase().substring(7,strSql.length());
            }
            //构造分页
            //如果传入的参数是999999,则表示不分页
            if(limit!=null&&limit<999999) {
                int fromIndex = (page-1) * limit;
//                int toIndex = fromIndex + limit;
                strSql+=  " limit " + fromIndex + "," + limit;
            }
            param.put("sqlS", strSql);
            List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
            Integer count=(Integer) param.get("total");
            List<Map<String, Object>> convertResult=result;
            if(ifCamel){
               convertResult=new ArrayList<>();
                //转驼峰命名法
                for(Map<String, Object> row:result) {
                    Map<String, Object> mapRow=  MapConvertCamel.toReplaceKeyLow(row);
                    convertResult.add(mapRow);
                }
            }
            return ResponseUtil.okList(convertResult,count,page,limit);
        } catch (Exception e){
            return ResponseUtil.ok("错误:"+e.getMessage());
        }
    }



}
