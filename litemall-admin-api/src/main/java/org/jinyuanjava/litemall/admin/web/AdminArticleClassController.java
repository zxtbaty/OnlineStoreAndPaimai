package org.jinyuanjava.litemall.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.vo.ArticleClassVo;
import org.jinyuanjava.litemall.admin.vo.CatVo;
import org.jinyuanjava.litemall.core.util.DateUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.LitemallSystemMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/articleclass")
@Validated
@Api(description = "后台管理/文章管理/文章分类:/admin/articleclass")
public class AdminArticleClassController {
    private final Log logger = LogFactory.getLog(AdminArticleClassController.class);

    @Autowired
    private LitemallArticleClassService articleClassService;


    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallOrderGoodsService litemallOrderGoodsService;


    @Autowired
    private LitemallUserService litemallUserService;

    @Autowired
    private LitemallOpadminInfoService opadminInfoService;

    @Resource
    private LitemallSystemMapper systemMapper;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private AliYunSendSms aliYunSendSms;




    @RequiresPermissions("admin:articleclass:list")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer id, String name) {
        List<ArticleClassVo> categoryVoList = new ArrayList<>();
        List<LitemallArticleClass> articleClassList=articleClassService.querySelective(id,name);
        List<ArticleClassVo> result = new ArrayList<>();
        for(LitemallArticleClass litemallArticleClass:articleClassList){
            ArticleClassVo temp=new ArticleClassVo();
            BeanUtils.copyProperties(litemallArticleClass,temp);
            categoryVoList.add(temp);
        }
        //List<LitemallArticleClass> articleClassList = articleClassService.queryByPid(0,id,name);
        // 1、获取第一级节点
        for (ArticleClassVo articleClassVo : categoryVoList) {
            if(0 == articleClassVo.getPid()) {
                result.add(articleClassVo);
            }
        }
        // 2、递归获取子节点
        for (ArticleClassVo parent : result) {
            parent = recursiveTree(parent, categoryVoList);
        }
        return ResponseUtil.okList(result);
    }

    @RequiresPermissions("admin:articleclass:listcascade")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="查询层叠")
    @GetMapping("/listcascade")
    public Object listCascader() {
        List<LitemallArticleClass> articleClasses = articleClassService.queryAll();
        List<CatVo> catVos = new ArrayList<>();
        List<CatVo> result = new ArrayList<>();
        for(LitemallArticleClass litemallArticleClass:articleClasses){
            CatVo temp=new CatVo();
            temp.setValue(litemallArticleClass.getId());
            temp.setPid(litemallArticleClass.getPid());
            temp.setLevel(litemallArticleClass.getLevel());
            temp.setLabel(litemallArticleClass.getName());
            catVos.add(temp);
        }

        //List<LitemallArticleClass> articleClassList = articleClassService.queryByPid(0,id,name);
        // 1、获取第一级节点 加载成一颗树
        for (CatVo catVo : catVos) {
            if(0 == catVo.getPid()) {
                result.add(catVo);
            }
        }
        // 2、递归获取子节点
        for (CatVo parent : result) {
            parent = recursiveCascade(parent, catVos);
        }
        return ResponseUtil.okList(result);
    }
    private ArticleClassVo recursiveTree(ArticleClassVo parent, List<ArticleClassVo> list) {
        for (ArticleClassVo articleClassVo : list) {
            if(parent.getId().equals(articleClassVo.getPid()) ) {
                articleClassVo = recursiveTree(articleClassVo, list);
//                if(articleClassVo!=null) {
                    if(parent.getChildren()==null){
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(articleClassVo);
//                }
            }
        }
        return parent;
    }

    private CatVo recursiveCascade(CatVo parent, List<CatVo> list) {
        for (CatVo catVo : list) {
            if(parent.getValue().equals(catVo.getPid()) ) {
                catVo = recursiveCascade(catVo, list);
                if(catVo!=null) {
                    if(parent.getChildren()==null){
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(catVo);
                }
            }
        }
        return parent;
    }

    private Object validate(LitemallArticleClass category) {
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(502,"分类名称不能为空");
        }
        return null;
    }

    @RequiresPermissions("admin:articleclass:create")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallArticleClass category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        articleClassService.add(category);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:articleclass:read")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallArticleClass category = articleClassService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:articleclass:update")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallArticleClass category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }

        if (articleClassService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:articleclass:delete")
    @RequiresPermissionsDesc(menu={"文章管理" , "文章分类"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallArticleClass category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        articleClassService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:articleclass:list")
    @GetMapping("/l1")
    public Object catL1() {
        // 所有一级分类目录
        List<LitemallArticleClass> l1CatList = articleClassService.queryL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (LitemallArticleClass category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return ResponseUtil.okList(data);
    }


    /**
     * 校验订单有效性
     * @param paramsMap
     * @return
     */
    private String validate(Map<String, Object> paramsMap){
        //校验主单信息
        String sourceId=null;
        if(paramsMap.containsKey("sourceId")){
            sourceId=paramsMap.get("sourceId").toString();
        }
        String crmId=null;
        if(paramsMap.containsKey("crmId")){
            crmId=paramsMap.get("crmId").toString();
        }
        String comId=null;
        if(paramsMap.containsKey("comId")){
            comId=paramsMap.get("comId").toString();
        }
        String comName=null;
        if(paramsMap.containsKey("comName")){
            comName=paramsMap.get("comName").toString();
        }
        String username=null;
        if(paramsMap.containsKey("username")){
            username=paramsMap.get("username").toString();
        }
        String weixinOpenid=null;
        if(paramsMap.containsKey("weixinOpenid")){
            weixinOpenid=paramsMap.get("weixinOpenid").toString();
        }
        String mobile=null;
        if(paramsMap.containsKey("mobile")){
            mobile=paramsMap.get("mobile").toString();
        }
        //如果以上这些信息都为空，则无法向用户表中插入数据进行保存，则无法创建订单
        if(sourceId==null&&crmId==null&&comId==null&&comName==null&&username==null&&weixinOpenid==null&&mobile==null){
            return "创建订单时必须要带有会员信息：【会员来源系统ID】【会员CRM ID】【会员手机号】【公司ID】【公司名称】【会员姓名】【微信Open Id】";
        }

        if(!paramsMap.containsKey("orderSn")||paramsMap.get("orderSn").equals("")){
            return "订单编号不能为空";
        }
        if(!paramsMap.containsKey("consignee")||paramsMap.get("consignee").equals("")){
            return "联系人不能为空";
        }
        if(!paramsMap.containsKey("mobile")||paramsMap.get("mobile").equals("")){
            return "联系电话不能为空";
        }
        if(!paramsMap.containsKey("typeCode")||paramsMap.get("typeCode").equals("")){
            return "订单类型不能为空";
        }
        if(!paramsMap.containsKey("sourceCode")||paramsMap.get("sourceCode").equals("")){
            return "订单来源不能为空";
        }
        if(!paramsMap.containsKey("yuyueComId")||paramsMap.get("yuyueComId").equals("")){
            return "预约机场不能为空";
        }
        if(!paramsMap.containsKey("yuyueStoreId")||paramsMap.get("yuyueStoreId").equals("")){
            return "预约店铺不能为空";
        }
        if(!paramsMap.containsKey("yuyueFetchTime")||paramsMap.get("yuyueFetchTime").equals("")){
            return "预约取货时间不能为空";
        }
        if(!paramsMap.containsKey("yuyueStatusCode")||paramsMap.get("yuyueStatusCode").equals("")){
            return "预约状态不能为空";
        }
        LocalDateTime localDateTime= DateUtil.getLocalDateTimeFromStringHMS(paramsMap.get("yuyueFetchTime").toString());
        if(localDateTime==null){
            return "预约时间格式不正确，请参照【2019-06-31 13:00】";
        }

        String yuyueStatusCode=paramsMap.get("yuyueStatusCode").toString();
        if(yuyueStatusCode.equals("50")||yuyueStatusCode.equals("101")||yuyueStatusCode.equals("102")||yuyueStatusCode.equals("201")||yuyueStatusCode.equals("202")){}
        else
        {
            return "预约状态必须是【50】【101】【102】【201】【202】其中之一";
        }
        return null;
    }

    /**
     * 创建订单接口测试，发起调用请求
     * @return
     */
    @RequiresPermissions("admin:articleclass:createorder")
    @GetMapping("/createorder")
    public Object createOrderTest() {
//        //模拟本地赋值创建订单接口
////        String strData=  invokeInterface.call_Post_Interface();
////        return ResponseUtil.ok(strData);
//
////        //模拟大兴机场调用创建订单接口
////        createOrderFromDaXing();
//
//        //模拟获取订单配置信息
//        LitemallSystemExample example = new LitemallSystemExample();
//        LitemallSystemExample.Criteria criteria1=example.createCriteria();
//        criteria1.andKeyNameLike("litemall_order_yuyue_beihuo_hour").andDeletedEqualTo(false);
//
//        LitemallSystemExample.Criteria criteria2=example.createCriteria();
//        criteria2.andKeyNameLike("litemall_order_yuyue_max_days").andDeletedEqualTo(false);
//
//        example.or(criteria2);
//
////        example.or().andKeyNameLike("litemall_yuyue_beihuo_hour").andDeletedEqualTo(false);
////        example.or().andKeyNameLike("litemall_order_yuyue_max_days").andDeletedEqualTo(false);
//        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
//        Map<String, String> data = new HashMap<>();
//        for(LitemallSystem system : systemList){
//            data.put(system.getKeyName(), system.getKeyValue());
//        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code","1234");
        aliYunSendSms.sendmessage("13521612468",jsonObject.toJSONString());
        return ResponseUtil.ok();


    }

    private boolean createOrderFromDaXing(){
        String strJson="{\"phone\":\"13521612468\",\"actualPrice\":\"8386.00\",\"comId\":3,\"comName\":\"大兴机场\",\"comments\":\"0\",\"consignee\":\"洪湖\",\"crmId\":\"DaxingCRMTest\",\"details\":[{\"comment\":\"0\",\"goodsId\":\"1181239\",\"goodsName\":\"韶音Trekz Air骨传导耳机AS650(页岩灰)\",\"number\":\"7\",\"picUrl\":\"/fs/product/20191030/small8ea1910aba2a34c51d5aa13fc6f9a6b01547994475.jpg\",\"price\":\"1198.00\",\"productId\":\"618\",\"specifications\":\"[\\\\\\\"AS650\\\\\\\"]\"}],\"goodsPrice\":\"8386.00\",\"mobile\":\"13500000000\",\"orderId\":\"05548851201\",\"orderPrice\":\"8386.00\",\"orderSn\":\"DD50668609605548851201\",\"sourceCode\":\"20\",\"sourceId\":\"165994a84afc4cc7a1c3d6ae1d0870ab\",\"sourceName\":\"大兴PC\",\"typeCode\":\"30\",\"typeName\":\"大兴预约单\",\"username\":\"test43\",\"weixinOpenid\":\"\",\"yuyueComId\":\"3\",\"yuyueComName\":\"大兴机场\",\"yuyueFetchExpireTime\":\"2019-10-31 00:00:00\",\"yuyueFetchTime\":\"2019-10-31 00:30:00\",\"yuyueStatusCode\":\"101\",\"yuyueStatusName\":\"创建预约单\",\"yuyueStoreId\":\"38\",\"yuyueStoreName\":\"黑口袋旗舰店\"}";

        Map<String, Object> paramsMap= (Map) JSONObject.parse(strJson);
        //解析主单
        LitemallOrder oderMain = JSON.parseObject(JSON.toJSONString(paramsMap), LitemallOrder.class);

        //LitemallOrder oderMain = JSON.parseObject(JSON.toJSONString(paramsMap), LitemallOrder.class);
        String strError=validate(paramsMap);
        if(strError!=null&&!strError.equals("")){
            return false;
        }
        //订单明细
        JSONArray orderDetails=(JSONArray)paramsMap.get("details");
        if(orderDetails.size()<=0){
            return false;
        }
        //先保存用户信息
        String sourceId=paramsMap.get("sourceId").toString();
        String crmId=paramsMap.get("crmId").toString();
        String comId=paramsMap.get("comId").toString();
        String comName=paramsMap.get("comName").toString();
        String username=paramsMap.get("username").toString();
        String weixinOpenid=paramsMap.get("weixinOpenid").toString();
        String sourceCode=paramsMap.get("sourceCode").toString();
        String orderSn=paramsMap.get("orderSn").toString();
        String phone=paramsMap.get("phone").toString();
        //先检索一下用户信息是否已经存在，如果存在，则更新相应的信息,如果不存在，则新增
        LitemallUser litemallUser= litemallUserService.selectUserByInfo(sourceId,crmId,comId,weixinOpenid,username);
        Integer userId=null;
        if(litemallUser==null){
            LitemallUser addUser=new LitemallUser();
//            addUser.setSourceId(sourceId);
            addUser.setSourceId("大兴机场");
            addUser.setCrmId(crmId);
            addUser.setComId(Integer.parseInt(comId));
            addUser.setComName(comName);
            addUser.setUsername(username);
            addUser.setNickname(username);
            addUser.setWeixinOpenid(weixinOpenid);
            addUser.setMobile(phone);
            litemallUserService.add(addUser);
            userId=addUser.getId();
        } else
        {
            litemallUser.setSourceId(sourceId);
            litemallUser.setCrmId(crmId);
            litemallUser.setComId(Integer.parseInt(comId));
            litemallUser.setComName(comName);
            litemallUser.setUsername(username);
            litemallUser.setNickname(username);
            litemallUser.setWeixinOpenid(weixinOpenid);
            litemallUser.setMobile(phone);
            litemallUserService.updateById(litemallUser);
            userId=litemallUser.getId();

        }

        oderMain.setUserId(userId);
        //保存订单主表
        if(oderMain.getId()==null){
            LitemallOrder litemallOrder=orderService.judgeOrderNoIfExists(orderSn,sourceCode);
            //判断订单编号是否已经存在
            if(litemallOrder==null) {
                orderService.add(oderMain);
            } else
            {
                oderMain.setId(litemallOrder.getId());
                orderService.updateWithOptimisticLocker(oderMain);
            }
        } else
        {
            orderService.updateWithOptimisticLocker(oderMain);
        }
        orderDetails.forEach((item)->{
            //规格型号转成字符数组
            String[] strSpe=(String[])JSON.parseArray(((Map<String, Object>) item).get("specifications").toString().replaceAll("\\\\","")).toArray(new String[0]);
            ((Map<String, Object>) item).remove("specifications");

            String productId= ((Map<String, Object>) item).get("productId").toString();

            LitemallOrderGoods orderGoods = JSON.parseObject(JSON.toJSONString(item), LitemallOrderGoods.class);
            orderGoods.setSpecifications(strSpe);
            orderGoods.setOrderId(oderMain.getId());
            if(orderGoods.getId()==null){
                //判断订单明细是否已经存在
                LitemallOrderGoods litemallOrderGoods=litemallOrderGoodsService.judgeOrderDetailIfExists(oderMain.getId(),productId);
                if(litemallOrderGoods==null) {
                    litemallOrderGoodsService.add(orderGoods);
                } else
                {
                    orderGoods.setId(litemallOrderGoods.getId());
                    litemallOrderGoodsService.updateById(orderGoods);
                }
            }else
            {
                litemallOrderGoodsService.updateById(orderGoods);
            }
        });

        //此处向消息表中写入订单创建信息
        //TODO 这里发邮件或发站内消息给相应的操作人员
        LitemallOpadminInfo opadminInfo=new LitemallOpadminInfo();
        opadminInfo.setTypeName("订单创建");
        opadminInfo.setTitle("预约单创建");
        opadminInfo.setSourceCode(oderMain.getSourceCode());
        opadminInfo.setSourceName(oderMain.getSourceName());
        Map<String,Object> parameters=new HashMap<>();
        parameters.put("orderSn",oderMain.getOrderSn());
        parameters.put("orderSourceName",oderMain.getSourceName());
        parameters.put("orderYuyueComName",oderMain.getYuyueComName());
        parameters.put("orderYuyueComHangzhanlou",oderMain.getYuyueComHangzhanlou());
        parameters.put("orderYuyueStoreName",oderMain.getYuyueStoreName());
        parameters.put("orderAddTime",oderMain.getAddTime());
        parameters.put("orderConsignee",oderMain.getConsignee());
        parameters.put("orderMobile",oderMain.getMobile());
        opadminInfoService.addLitemallOpadminInfo(opadminInfo,parameters);

        return true;
    }





}
