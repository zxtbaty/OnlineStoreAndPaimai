package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.GOODS_NAME_EXIST;

@Service
public class AdminRecommendService {
    private final Log logger = LogFactory.getLog(AdminRecommendService.class);


    @Autowired
    private LitemallGoodsRecommendService goodsRecommendService;
    @Autowired
    private LitemallGoodsRecommendViewService goodsRecommendViewService;

    public Object list(String posType, Integer comId,
                       Integer page, Integer limit, String sort) {


        if(posType.equals("首页秒杀")){
            comId=1;
            //return getHomepageRecommendSeckill(posType,comId,page,limit,sort,order);
        } else  if(posType.equals("列表秒杀")){
            comId=1;
            //return getListRecommendSeckill(posType,comId,page,limit,sort,order);
        } else  if(posType.equals("首页团购")){
            comId=1;
            //return getHomepageRecommendGroupon(posType,comId,page,limit,sort,order);
        } else  if(posType.equals("列表团购")){
            comId=1;
            //return getListRecommendGroupon(posType,comId,page,limit,sort,order);
        } else  if(posType.equals("首页特产")){
            comId=1;
            //return getHomepageRecommendEc(posType,comId,page,limit,sort,order);
        } else  if(posType.equals("列表特产")){
            comId=1;
            //return getListRecommendEc(posType,comId,page,limit,sort,order);
        }
        List<ViewLitemallGoodsRecommend>  viewRecommendList = goodsRecommendViewService.querySelective(posType, comId, page, limit, sort);

        return ResponseUtil.okList(viewRecommendList);

    }



    private Object validate(LitemallGoodsRecommend litemallGoodsRecommend) {

        String postype = litemallGoodsRecommend.getPosType();
        if (StringUtils.isEmpty(postype)) {
            return ResponseUtil.fail(401, "展示位置名称不能为空");
        }
        Integer comId = litemallGoodsRecommend.getComId();
        if (comId==null) {
            return ResponseUtil.fail(401, "公司ID不能为空");
        }
        return null;
    }

    @Transactional
    public Object update(LitemallGoodsRecommend litemallGoodsRecommend) {
        Object error = validate(litemallGoodsRecommend);
        if (error != null) {
            return error;
        }
       goodsRecommendService.updateById(litemallGoodsRecommend);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object batchUpdate(List<LitemallGoodsRecommend> litemallGoodsRecommends) {
        for(LitemallGoodsRecommend litemallGoodsRecommend:litemallGoodsRecommends) {
            Object error = validate(litemallGoodsRecommend);
            if (error != null) {
                return error;
            }
            goodsRecommendService.updateById(litemallGoodsRecommend);

        }
        return ResponseUtil.ok();
    }


    @Transactional
    public Object delete(LitemallGoodsRecommend litemallGoodsRecommend) {
        Integer id = litemallGoodsRecommend.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        goodsRecommendService.deleteById(id);

        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(LitemallGoodsRecommend litemallGoodsRecommend) {
        Object error = validate(litemallGoodsRecommend);
        if (error != null) {
            return error;
        }
        if (goodsRecommendService.checkExistByPostypeAndComidAndGoodsid(litemallGoodsRecommend.getPosType(),
                litemallGoodsRecommend.getComId(),litemallGoodsRecommend.getGoodsId())) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "商品名已经存在");
        }

        goodsRecommendService.add(litemallGoodsRecommend);

        return ResponseUtil.ok();
    }

    @Transactional
    public Object batchCreate(List<LitemallGoodsRecommend> litemallGoodsRecommends) {
        for(LitemallGoodsRecommend litemallGoodsRecommend:litemallGoodsRecommends) {
            Object error = validate(litemallGoodsRecommend);
            if (error != null) {
                return error;
            }
            if (!goodsRecommendService.checkExistByPostypeAndComidAndGoodsid(litemallGoodsRecommend.getPosType(),
                    litemallGoodsRecommend.getComId(), litemallGoodsRecommend.getGoodsId())) {
                goodsRecommendService.add(litemallGoodsRecommend);
            }
        }
        return ResponseUtil.ok();
    }
}
