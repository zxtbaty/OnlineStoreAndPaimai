package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.ViewPromotionGoodsDetailMapper;
import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallPromotionGoodsRuleMapper;
import org.jinyuanjava.litemall.db.dao.ViewListRecommendGoodsUserMapper;
import org.jinyuanjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LitemallPromotionGoodsRuleService {
    @Resource
    private LitemallPromotionGoodsRuleMapper mapper;

    @Autowired
    private LitemallPromotionGoodsUserOrderService promotionGoodsUserOrderService;

    @Autowired
    private LitemallPromotionGoodsDetailService goodsDetailService;

    @Resource
    private ViewListRecommendGoodsUserMapper viewListRecommendGoodsUserMapper;

    @Resource
    private ViewPromotionGoodsDetailMapper viewPromotionGoodsDetailMapper;

    @Autowired
    private CommonDBService commonDBService;

    @Autowired
    private LitemallUserService userService;


    /**
     * 获取会员专享活动列表
     * @param name
     * @param expireFlag
     * @param goodsSn
     * @param goodsName
     * @param username
     * @param page
     * @param size
     * @param sort

     * @return
     */
    public List<LitemallPromotionGoodsRule> querySelective(String name, Integer expireFlag,
                                                           String goodsSn, String goodsName,
                                                           String username,
                                                           Integer page, Integer size, String sort) {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.setOrderByClause(sort);

        LitemallPromotionGoodsRuleExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%"+name+"%");
        }
        if(expireFlag!=null)
        {
            if(expireFlag==0){
                criteria.andExpireFlagEqualTo(false);
            }else if(expireFlag==1){
                criteria.andExpireFlagEqualTo(true);
            }
        }

        if (!StringUtils.isEmpty(goodsSn)) {
            //通过子表查询
            List<LitemallPromotionGoodsDetail> goodsDetailList=  goodsDetailService.querySelective(null,goodsSn,null,1,999999,null);
            List<Integer> ruleIdList=goodsDetailList.stream().map(LitemallPromotionGoodsDetail::getRuleId).distinct().collect(Collectors.toList());
            if(ruleIdList!=null&&ruleIdList.size()>0) {
                criteria.andIdIn(ruleIdList);
            }
        }

        if (!StringUtils.isEmpty(goodsName)) {
            //通过子表查询
            List<LitemallPromotionGoodsDetail> goodsDetailList=  goodsDetailService.querySelective(null,null,goodsName,1,999999,null);
            List<Integer> ruleIdList=goodsDetailList.stream().map(LitemallPromotionGoodsDetail::getRuleId).distinct().collect(Collectors.toList());
            if(ruleIdList!=null&&ruleIdList.size()>0) {
                criteria.andIdIn(ruleIdList);
            }
        }
//        if (!StringUtils.isEmpty(username)) {
//            //通过子表查询
//            //用Sql语句查询
//            criteria.andUsernameLike("%"+username+"%");
//        }


        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    /**
     * 创建会员专属活动规则
     * @param rules
     * @return
     */
    public int createRules(LitemallPromotionGoodsRule rules) {
//        rules.setRemainNum(rules.getStoreNum());
        rules.setAddTime(LocalDateTime.now());
        rules.setUpdateTime(LocalDateTime.now());
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应会员专属活动规则
     *
     * @param id
     * @return
     */
    public LitemallPromotionGoodsRule queryById(Integer id) {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    public LitemallPromotionGoodsRule queryByName(String ruleName) {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.or().andNameEqualTo(ruleName).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 查询状态正常，但是时间已经过期的会员专享活动,这种类型要更新成已经过期
     *
     * @return
     */
    public List<LitemallPromotionGoodsRule> queryHaveExpiredButStateError() {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.or().andExpireFlagEqualTo(false).andEndDateLessThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 因为已经修改成批量的状态，如果该活动下所有的商品可用库存为0，则该活动才过期 查询状态正常，但是剩余库存已经小于等于0，要更新成已经过期
     *
     * @return
     */
    public List<LitemallPromotionGoodsRule> queryHaveExpiredButStoreNumEqualZero() {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        //思路，查询现在未过期的正在进行中的所有的会员活动
        //查询这些会员活动中的所有商品可用库存是小于或等于0
        //如果有一个库存大于0，则该活动不过期，如果全部等于0，则活动过期
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime= LocalDateTime.now().format(df);

        String strSql="select detail.rule_id as id from litemall_promotion_goods_detail detail inner join " +
                "(select distinct id from litemall_promotion_goods_rule where deleted=0 and expire_flag=0 " +
                "and begin_date<='"+currentTime+"') " +
                "b on detail.rule_id=b.id  where detail.deleted=0 " +
                "group by detail.rule_id HAVING sum(remain_num)=0";
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);

        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        if(result==null||result.size()==0){
            return null;
        } else {
            List<Object> ruleIdList = result.stream().map(i -> i.get("id")).collect(Collectors.toList());

            if (ruleIdList == null || ruleIdList.size() <= 0) {
                return null;
            } else {
                List<Integer> idList=new ArrayList<>();
                ruleIdList.forEach(item->{
                    idList.add(Integer.parseInt(item.toString()));
                });
                example.or().andIdIn(idList);
                return mapper.selectByExample(example);
            }
        }
    }

    /**
     * 查询状态正常，已经开始秒杀进行的任务列表
     *
     * @return
     */
    public List<LitemallPromotionGoodsRule> queryHaveBeginSeckill() {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.or().andExpireFlagEqualTo(false).andBeginDateGreaterThanOrEqualTo(LocalDateTime.now()).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }


    /**
     * 删除所选的商品
     * @param id
     */
    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 更新会员专享活动规则
     * @param rule
     * @return
     */
    public int updateById(LitemallPromotionGoodsRule rule) {
//        if(rule.getRemainNum()!=null&&
//                rule.getRemainNum()>rule.getStoreNum())
//        {
//            rule.setRemainNum(rule.getStoreNum());
//        }
        rule.setUpdateTime(LocalDateTime.now());
        return mapper.updateByPrimaryKeySelective(rule);
    }

    public boolean checkExistByName(String name) {
        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return mapper.countByExample(example) != 0;
    }

    public LitemallPromotionGoodsRule findById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 判断用户是否已经参与过某个会员专享活动，是否还可以再继续创建订单,判断具体商品
     * @param ruleId
     * @param userId
     * @return
     */
    public Boolean checkIfAllowGoodsUserNext(Integer ruleId,Integer productId,Integer userId){
        //判断一下会员专享规则，如果限制单用户，再判断是否已经创建订单
        LitemallPromotionGoodsRule promotionGoodsRule=mapper.selectByPrimaryKey(ruleId);
        if(promotionGoodsRule==null){
            return true;
        }
        LitemallPromotionGoodsDetail promotionGoodsDetail= goodsDetailService.querySelective(ruleId,productId);
        if(promotionGoodsDetail.getOnlyOne()==false)
        {
            return true;
        }
        //判断用户是否已经下单
        Boolean ifHave=  promotionGoodsUserOrderService.checkExistByUseridRuleid(userId,ruleId,productId);

        return !ifHave;
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的会员专享活动中,某个用户是否享有专属商品
     * @param productId
     * @return
     */
    public LitemallPromotionGoodsDetail getRuleByProductId(Integer productId,Integer userId){
        LitemallUser user=userService.findById(userId);
        String attrCode1= user.getUserClassAttr1Code();
        String attrCode2=user.getUserClassAttr2Code();

        //直接写SQl语句
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime= LocalDateTime.now().format(df);
        //思路，前找出所有已经开始未失效的活动
        //再计算出该会员享有的所有可参加的活动
        //然后再在这些可用的活动中查询该商品是否在其中，如果在，则返回活动ID,如果是多个，需要在前台在做活动中判断
       String strSql="select detail.id as ruleId from litemall_promotion_goods_detail detail inner join " +
               "( select b.id from litemall_promotion_goods_rule_user a inner join " +
                    "(select distinct id from litemall_promotion_goods_rule where deleted=0 and " +
                         "expire_flag=0 and  begin_date<='"+currentTime+"' " +
                     ") b on a.rule_id=b.id and (a.user_id='"+userId+"' or a.user_class_attr1_code='"+attrCode1+
                     "' or a.user_class_attr2_code='"+attrCode2+"') " +
               ") c  on detail.rule_id=c.id and detail.deleted=0 and detail.goods_product_id='"+productId+"'";
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);

        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        if(result==null){
            return null;
        } else {
            List<Object> ruleIdList = result.stream().map(i -> i.get("ruleId")).collect(Collectors.toList());
            if (ruleIdList == null ||ruleIdList.size()<=0){
                return null;
            } else
            {
                Integer firstId=Integer.parseInt(ruleIdList.get(0).toString()) ;
                LitemallPromotionGoodsDetail goodsRule= goodsDetailService.queryById(firstId);
                return goodsRule;
            }
        }
//        //判断该用户是否在此会员专属规则中
//        ViewListRecommendGoodsUserExample goodsUserExample=new ViewListRecommendGoodsUserExample();
//        ViewListRecommendGoodsUserExample.Criteria criteria=goodsUserExample.createCriteria();
//        criteria.andDeletedEqualTo(Byte.valueOf("0"));
//        criteria.andExpireFlagEqualTo(Byte.valueOf("0"));
//        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());
//        criteria.andGoodsProductIdEqualTo(productId);
//        if(userId!=null){
//        	criteria.andUserIdEqualTo(userId);
//        }
//        ViewListRecommendGoodsUser recommendGoodsUser= viewListRecommendGoodsUserMapper.selectOneByExample(goodsUserExample);
//        if(recommendGoodsUser!=null){
//            LitemallPromotionGoodsRule promotionGoodsRule= mapper.selectByPrimaryKey(recommendGoodsUser.getId());
//            return promotionGoodsRule;
//        } else
//        {
//            return null;
//        }
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的会员专享活动中,某个用户是否享有专属商品
     * @param productId
     * @return
     */
    public List<ViewPromotionGoodsDetail> getRuleByGoodsProductId(Integer goodsId,Integer productId){
        //判断该用户是否在此会员专属规则中
        ViewPromotionGoodsDetailExample goodsDetailExample=new ViewPromotionGoodsDetailExample();
        ViewPromotionGoodsDetailExample.Criteria criteria=goodsDetailExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andExpireFlagEqualTo(false);
        criteria.andBeginDateLessThanOrEqualTo(LocalDateTime.now());
        if(productId==null) {
            criteria.andGoodsIdEqualTo(goodsId);
        } else
        {
            criteria.andGoodsProductIdEqualTo(productId);
        }

        List<ViewPromotionGoodsDetail> viewPromotionGoodsDetailList= viewPromotionGoodsDetailMapper.selectByExample(goodsDetailExample);
        return viewPromotionGoodsDetailList;

    }


    /**
     * 返回所有可用的前端会员活动列表，要考虑到后台设置的显示参数,显示的商品要去重，取其中的一个规格ID
     * @param userId
     * @param ifHome
     * @return
     */
    /**
     * 显示数量
     * @param userId 用户ID
     * @param displayCount 显示数量 如果传入的是999999,则取全部   SystemConfig.getHunyuanLimit()
     * @return
     */
    public List<Map<String, Object>> getGoodsListByUserId(Integer userId,Integer displayCount){

        LitemallUser user=userService.findById(userId);
        String attrCode1= user.getUserClassAttr1Code();
        String attrCode2=user.getUserClassAttr2Code();

        String strLimit="";
        if(displayCount!=999999){
            strLimit="limit 0,"+displayCount;
        }
        //直接写SQl语句
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime= LocalDateTime.now().format(df);
        //思路，前找出所有已经开始未失效的活动
        //再计算出该会员享有的所有可参加的活动
        //然后再在这些可用的活动中查询按规则id,商品id分组，计算出最大的产品id,然后再从活动商品表里按规则id/商品id/产品id取值
        String strSql="select x.goods_id as goodsId,x.goods_product_id as goodsProductId,pic_url as picUrl," +
                " goods_name as goodsName,hui_yuan_price as huiYuanPrice,source_price as sourcePrice from " +
                "litemall_promotion_goods_detail x ,(select detail.rule_id,detail.goods_id," +
                "max(goods_product_id) as goods_product_id from litemall_promotion_goods_detail detail inner join " +
                "(select  b.id from litemall_promotion_goods_rule_user a inner join " +
                "   (select distinct id  from litemall_promotion_goods_rule where deleted=0 and " +
                "   expire_flag=0 and  begin_date <='"+currentTime+"' " +
                "   ) b on a.rule_id=b.id and (a.user_id='"+userId+"' or a.user_class_attr1_code='"+attrCode1+"' or a.user_class_attr2_code='"+attrCode2+"') " +
                ") main on detail.rule_id=main.id  where detail.deleted=0 " +
                " group by detail.rule_id,detail.goods_id ) y where x.rule_id=y.rule_id and x.goods_id=y.goods_id" +
                " and x.goods_product_id=y.goods_product_id  and x.deleted=0 "+strLimit;
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        if(result==null){
            return null;
        } else {
            return result;
        }
    }

    /**
     * 判断商品是否存在某个已经开始但还没有结束的会员专享活动中,并且免运费
     * @param productId
     * @return
     */
    public Boolean checkExistsByGoodsIdAndFreePost(Integer productId){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime= LocalDateTime.now().format(df);
        String strSql="select detail.id from litemall_promotion_goods_detail detail inner join "+
         "(select distinct id from litemall_promotion_goods_rule where deleted=0 and free_post=1 " +
         "and expire_flag=0 and DATE_FORMAT(begin_date,'%Y-%m-%d %H:%m:%s')<='"+currentTime+"')"+
         "b on detail.rule_id=b.id where detail.deleted=0 and detail.goods_product_id='"+productId+"' ";
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        if(result==null){
            return false;
        } else {
            List<Object> ruleIdList = result.stream().map(i -> i.get("id")).collect(Collectors.toList());
            if (ruleIdList == null ||ruleIdList.size()<=0){
                return false;
            } else
            {
                return true;
            }
        }
    }

    //列出所有正在参与会员活动的商品，并且活动正在进行中
    public List<Integer> queryAllProductIdList() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime= LocalDateTime.now().format(df);
        String strSql="select distinct detail.goods_product_id as goodsProductId from litemall_promotion_goods_detail detail inner join "+
                "(select distinct id from litemall_promotion_goods_rule where deleted=0  " +
                "and expire_flag=0 and DATE_FORMAT(begin_date,'%Y-%m-%d %H:%m:%s')<='"+currentTime+"')"+
                "b on detail.rule_id=b.id where detail.deleted=0 ";
        Map<String, Object> param=new HashMap<>();
        param.put("sqlS",strSql);
        List<Map<String, Object>> result= commonDBService.procedureDaoList(param);
        if(result==null){
            return null;
        } else {
            List<Object> ruleIdList = result.stream().map(i -> i.get("goodsProductId")).collect(Collectors.toList());
            if (ruleIdList == null ||ruleIdList.size()<=0){
                return null;
            } else
            {
                List<Integer> idList=new ArrayList<>();
                ruleIdList.forEach(item->{
                    idList.add(Integer.parseInt(item.toString()));
                });
                return idList;
            }
        }
//        LitemallPromotionGoodsRuleExample example = new LitemallPromotionGoodsRuleExample();
//        example.or().andExpireFlagEqualTo(false).andBeginDateLessThanOrEqualTo(LocalDateTime.now())
//                .andDeletedEqualTo(false);
//        List<LitemallPromotionGoodsRule> promotionGoodsRules=mapper.selectByExample(example);
//
//
//        return promotionGoodsRules.stream().distinct().map(LitemallPromotionGoodsRule::getGoodsProductId).collect(Collectors.toList());

    }

}
