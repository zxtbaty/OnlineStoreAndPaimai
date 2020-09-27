package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.HuodongsetAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminHuodongsetService {
    private final Log logger = LogFactory.getLog(AdminHuodongsetService.class);

    @Autowired
    private LitemallHuodongMainService huodongMainService;
    @Autowired
    private LitemallHuodongDetailPicLinkService huodongDetailPicLinkService;
    @Autowired
    private LitemallHuodongDetailGoodsListService huodongDetailGoodsListService;

    public Object list(String name,Boolean expireFlag,
                       Integer page, Integer limit, String sort) {
        List<LitemallHuodongMain> huodongMains = huodongMainService.querySelective(name,expireFlag,page, limit, sort);
        return ResponseUtil.okList(huodongMains);
    }

    private Object validate(HuodongsetAllinone huodongsetAllinone) {
        LitemallHuodongMain litemallCompany = huodongsetAllinone.getHuodongMain();
        String name = litemallCompany.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "活动名称不不能为空");
        }
        LitemallHuodongDetailPicLink[] huodongDetailPicLinks = huodongsetAllinone.getHuodongDetailPicLinks();
        if(huodongDetailPicLinks!=null) {
            for (LitemallHuodongDetailPicLink huodongDetailPicLink : huodongDetailPicLinks) {
                String  picUrl = huodongDetailPicLink.getPicUrl();
                if (StringUtils.isEmpty(picUrl)) {
                    return ResponseUtil.fail(401, "活动图不能为空");
                }
//                Integer goodsId = huodongDetailPicLink.getGoodsId();
//                if (StringUtils.isEmpty(goodsId)) {
//                    return ResponseUtil.fail(401, "活动图商品链接不能为空");
//                }
            }
        }
        LitemallHuodongDetailGoodsList[] huodongDetailGoodsLists = huodongsetAllinone.getHuodongDetailGoodsLists();
        if(huodongDetailGoodsLists!=null) {
            for (LitemallHuodongDetailGoodsList huodongDetailGoodsList : huodongDetailGoodsLists) {
                Integer goodsId = huodongDetailGoodsList.getGoodsId();
                if (StringUtils.isEmpty(goodsId)) {
                    return ResponseUtil.fail(401, "活动列表商品不能为空");
                }
            }
        }
        return null;
    }

    /**
     * 编辑字典表
     * <p>
     * TODO
     * 编辑字典表，需要判断是否明细主键ID已经存在，如果存在，则更新信息，如果不存在，则插入数据
     */
    @Transactional
    public Object update(HuodongsetAllinone huodongsetAllinone) {
        Object error = validate(huodongsetAllinone);
        if (error != null) {
            return error;
        }
        LitemallHuodongMain huodongMain=huodongsetAllinone.getHuodongMain();
        LitemallHuodongDetailPicLink[] huodongDetailPicLinks = huodongsetAllinone.getHuodongDetailPicLinks();
        LitemallHuodongDetailGoodsList[] huodongDetailGoodsLists=huodongsetAllinone.getHuodongDetailGoodsLists();

        Integer mainId = huodongMain.getId();
        if(mainId==null){
            huodongMainService.add(huodongMain);
        }else
        {
            huodongMainService.update(huodongMain);
        }

        // 活动商品链接
        for (LitemallHuodongDetailPicLink huodongDetailPicLink : huodongDetailPicLinks) {
            huodongDetailPicLink.setMainId(huodongMain.getId());
            if(huodongDetailPicLink.getId()==null){
                huodongDetailPicLinkService.add(huodongDetailPicLink);
            } else
            {
                huodongDetailPicLinkService.update(huodongDetailPicLink);
            }
        }
        // 活动商品明细
        for (LitemallHuodongDetailGoodsList huodongDetailGoodsList : huodongDetailGoodsLists) {
            huodongDetailGoodsList.setMainId(huodongMain.getId());
            if(huodongDetailGoodsList.getId()==null){
                huodongDetailGoodsListService.add(huodongDetailGoodsList);
            } else
            {
                huodongDetailGoodsListService.update(huodongDetailGoodsList);
            }
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param huodongMain
     * @return
     */
    @Transactional
    public Object delete(LitemallHuodongMain huodongMain) {
        Integer mainId= huodongMain.getId();
        if (mainId == null) {

            return ResponseUtil.fail(502, "删除主键不能为空");
        }

        huodongMainService.delete(mainId);
        huodongDetailPicLinkService.deleteByMainId(mainId);
        huodongDetailGoodsListService.deleteByMainId(mainId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(HuodongsetAllinone huodongsetAllinone) {
        Object error = validate(huodongsetAllinone);
        if (error != null) {
            return error;
        }

        LitemallHuodongMain huodongMain = huodongsetAllinone.getHuodongMain();
        LitemallHuodongDetailPicLink[] huodongDetailPicLinks = huodongsetAllinone.getHuodongDetailPicLinks();
        LitemallHuodongDetailGoodsList[] companyHangzhanlous=huodongsetAllinone.getHuodongDetailGoodsLists();

        String name = huodongMain.getName();
        if (huodongMainService.checkExistByNameAndId(name,huodongMain.getId())) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "活动名称已经存在");
        }
        //保存主表
        huodongMainService.add(huodongMain);
        // 活动图片
        for (LitemallHuodongDetailPicLink huodongDetailPicLink : huodongDetailPicLinks) {
            huodongDetailPicLink.setMainId(huodongMain.getId());

            huodongDetailPicLinkService.add(huodongDetailPicLink);
        }
        // 活动商品列表
        for (LitemallHuodongDetailGoodsList huodongDetailGoodsList : companyHangzhanlous) {
            huodongDetailGoodsList.setMainId(huodongMain.getId());
            huodongDetailGoodsListService.add(huodongDetailGoodsList);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallHuodongMain huodongMain = huodongMainService.queryById(id);
        List<LitemallHuodongDetailPicLink> huodongDetailPicLinks = huodongDetailPicLinkService.queryByMainId(id);
        List<LitemallHuodongDetailGoodsList> huodongDetailGoodsLists = huodongDetailGoodsListService.queryByMainId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("huodongMain", huodongMain);
        data.put("huodongDetailPicLink", huodongDetailPicLinks);
        data.put("huodongDetailGoodsList", huodongDetailGoodsLists);

        return ResponseUtil.ok(data);
    }


}
