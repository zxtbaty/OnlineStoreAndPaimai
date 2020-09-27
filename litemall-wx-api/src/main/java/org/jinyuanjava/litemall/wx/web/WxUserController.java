package org.jinyuanjava.litemall.wx.web;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.LitemallOrderMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOrderExample;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.domain.LitemallUserChargeMoney;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.jinyuanjava.litemall.db.service.*;
import org.jinyuanjava.litemall.db.util.OrderUtil;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@RestController
@RequestMapping("/wx/user")
@Validated
@Api(description = "微信前端/用户服务:/wx/user")
public class WxUserController {
    private final Log logger = LogFactory.getLog(WxUserController.class);

    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallUserinfoDefService userinfoDefService;

    @Resource
    private LitemallOrderMapper litemallOrderMapper;

    @Autowired
    private CommonDBService commonDBService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallUserChargeMoneyService chargeMoneyService;


    /**
     * 用户个人页面数据
     * <p>
     * 目前是用户订单统计信息
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("index")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("order", orderService.orderInfo(userId));
        return ResponseUtil.ok(data);
    }


    /**
     * 根据用户Id，获取所有的通知列表
     *
     * @param userId
     * @return
     */

    @GetMapping("/userinfopubs")
    public Object getUserinfoPubs(@LoginUser Integer userId) {
        List<LitemallUserinfoDef>  result= userinfoDefService.getUserInfoByUserId(userId);
        return ResponseUtil.okList(result);
    }
    /**
     * 根据用户Id，获取所有的通知列表的数量
     *
     * @param userId
     * @return
     */

    @GetMapping("/userinfopubscount")
    public Object getUserinfoPubsCount(@LoginUser Integer userId) {
        if(userId==null){
            return ResponseUtil.ok(0);
        }
        List<LitemallUserinfoDef> result=userinfoDefService.getUserInfoByUserId(userId);
        return ResponseUtil.ok(result.size());
    }


    /**
     * 返回我的页面上的角标信息
     * @return
     */
    @GetMapping("getUserMyInfoCount")
    public Object getUserMyInfoCount(@LoginUser Integer userId) {
        //获取我的预约单数量
        LitemallOrderExample orderExample=new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdBetween(2, 3);
        Long myYuYueCount= litemallOrderMapper.countByExample(orderExample);
        //获取我的在线订单数量
        orderExample=new LitemallOrderExample();
        criteria= orderExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andUserIdEqualTo(userId);
        criteria.andYuyueComIdIsNull();
        Long myOrderCount= litemallOrderMapper.countByExample(orderExample);
        //待退款/已退款
        orderExample=new LitemallOrderExample();
        List<Short> orderStatus = OrderUtil.orderStatus(6);
	    orderExample=new LitemallOrderExample();
	    criteria= orderExample.createCriteria();
	    criteria.andDeletedEqualTo(false);
	    criteria.andUserIdEqualTo(userId);
	    criteria.andYuyueComIdIsNull();
       if(orderStatus!=null) {
           criteria.andOrderStatusIn(orderStatus);
       }
        Long myBackOrderCount= litemallOrderMapper.countByExample(orderExample);

        //获取我的消息数量
        List<LitemallUserinfoDef> result=userinfoDefService.getUserInfoByUserId(userId);
        Integer userInfoCount=null;
        if(result==null||result.size()==0){
            userInfoCount=0;
        }else {
            userInfoCount = result.size();
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("yuYueCount",myYuYueCount);
        jsonObject.put("orderCount",myOrderCount);
        jsonObject.put("userInfoCount",userInfoCount);
        jsonObject.put("backOrderCount",myBackOrderCount);
        return ResponseUtil.ok(jsonObject);
    }

    /**
     * 获取出价列表
     * @param userId
     * @return
     */
    @GetMapping("getUserOfferList")
    public Object getUserOfferList(Integer userId) {
        String strSql="select goods_name,DATE_FORMAT(offer_time,'%Y-%m-%d %H:%m:%s') as offer_time,offer_money,offer_state from view_auction_dajia_offer_current " +
                "where user_id="+userId+" union "+
                "select goods_name,DATE_FORMAT(offer_time,'%Y-%m-%d %H:%m:%s') as offer_time,offer_money,offer_state from view_auction_zhuanchang_offer_current "+
                "where user_id="+userId+"  order by offer_time desc";
        Map<String, Object> param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        Integer count=(Integer) param.get("total");

        strSql="select min(offer_money) as minPrice, max(offer_money) as maxPrice  from " +
                "(select offer_money from litemall_auction_dajia_offer_current " +
                "where user_id="+userId+" union "+
                "select  offer_money  from litemall_auction_zhuanchang_offer_current "+
                "where user_id="+userId+") a";

        param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String, Object>> sumInfo= commonDBService.procedureDaoList(param);

        strSql="select count(offer_money) as sumCount, max(offer_money) as maxPrice  from " +
                "(select offer_money from litemall_auction_dajia_offer_current " +
                "where user_id="+userId+" and deleted=0 and offer_state='领先' union "+
                "select  offer_money  from litemall_auction_zhuanchang_offer_current "+
                "where user_id="+userId+" and deleted=0 and offer_state='领先') a";

        param = new HashMap<>();
        param.put("sqlS", strSql);
        List<Map<String, Object>> lingXianInfo= commonDBService.procedureDaoList(param);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("offerList",result);
        jsonObject.put("sumInfo",sumInfo);
        jsonObject.put("lingXianInfo",lingXianInfo);

        return ResponseUtil.ok(jsonObject);
    }

    /**
     * 获取充值记录
     * @param userId
     * @return
     */
    @GetMapping("getUserChargeList")
    public Object getUserChargeList(Integer userId) {
        LitemallUser userInfo=userService.findById(userId);
        List<LitemallUserChargeMoney> chargeList= chargeMoneyService.queryMyCharge(userId);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userInfo",userInfo);
        jsonObject.put("chargeList",chargeList);

        return ResponseUtil.ok(jsonObject);
    }
    /**
     * 充值单申请退款
     * @param chargeId
     * @return
     */
    @GetMapping("refundCharge")
    public Object refundCharge(Integer chargeId) {
        Boolean result=chargeMoneyService.retfundCharge(chargeId);
        if(!result){
            return ResponseUtil.fail(502,"退款申请已经处理,不能重复申请");
        } else
        {
            return ResponseUtil.ok();
        }
    }
}
