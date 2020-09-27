package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.AuthororcorpAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorp;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpItems;
import org.jinyuanjava.litemall.db.domain.LitemallAuthororcorpPara;
import org.jinyuanjava.litemall.db.service.LitemallAuthororcorpItemsService;
import org.jinyuanjava.litemall.db.service.LitemallAuthororcorpParaService;
import org.jinyuanjava.litemall.db.service.LitemallAuthororcorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminAuthorcorpService {
    private final Log logger = LogFactory.getLog(AdminAuthorcorpService.class);

    @Autowired
    private LitemallAuthororcorpService authororcorpService;
    @Autowired
    private LitemallAuthororcorpParaService authororcorpParaService;
    @Autowired
    private LitemallAuthororcorpItemsService authororcorpItemsService;

    public Object list(String name,
                       Integer page, Integer limit, String sort) {
        List<LitemallAuthororcorp> dicMainsList = authororcorpService.querySelective(name,page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }


    private Object validate(AuthororcorpAllinone allinone) {
        LitemallAuthororcorp litemallAuthororcorp = allinone.getAuthororcorp();
        String name = litemallAuthororcorp.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "供应商名称不不能为空");
        }
        if (authororcorpService.checkExistByNameAndId(name,litemallAuthororcorp.getId())) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "供应商名称已经存在");
        }
        LitemallAuthororcorpPara[] authororcorpParas = allinone.getAuthororcorpParas();
        if(authororcorpParas!=null) {
            for (LitemallAuthororcorpPara para : authororcorpParas) {
                String attribute = para.getAttribute();
                if (StringUtils.isEmpty(attribute)) {
                    return ResponseUtil.fail(502, "供应商参数名称不能为空");
                }
                String value = para.getValue();
                if (StringUtils.isEmpty(value)) {
                    return ResponseUtil.fail(502, "供应商参数值不能为空");
                }
            }
        }
        LitemallAuthororcorpItems[] authororcorpItems = allinone.getAuthororcorpItems();
        if(authororcorpItems!=null) {
            for (LitemallAuthororcorpItems item : authororcorpItems) {
                String itemName = item.getItemName();
                if (StringUtils.isEmpty(itemName)) {
                    return ResponseUtil.fail(502, "供应商私人定制项目名称不能为空");
                }
                BigDecimal minPrice = item.getItemMinPrice();
                if (StringUtils.isEmpty(minPrice)) {
                    return ResponseUtil.fail(502, "供应商私人定制项目起价不能为空");
                }
                BigDecimal dingMoney = item.getItemDingMoney();
                if (StringUtils.isEmpty(dingMoney)) {
                    return ResponseUtil.fail(502, "供应商私人定制项目定金不能为空");
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
    public Object update(AuthororcorpAllinone allinone) {
        Object error = validate(allinone);
        if (error != null) {
            return error;
        }
        LitemallAuthororcorp authororcorp=allinone.getAuthororcorp();
        LitemallAuthororcorpPara[] authororcorpParas = allinone.getAuthororcorpParas();
        LitemallAuthororcorpItems[] authororcorpItems=allinone.getAuthororcorpItems();

        Integer mainId = authororcorp.getId();
        if(mainId==null){
            authororcorpService.add(authororcorp);
        }else
        {
            authororcorpService.update(authororcorp);
        }

        // 供应商参数表
        for (LitemallAuthororcorpPara authororcorpPara : authororcorpParas) {
            authororcorpPara.setAuthorId(authororcorp.getId());
            Integer codeId=authororcorpPara.getId();
            if(codeId==null){
                authororcorpParaService.add(authororcorpPara);
            } else
            {
                authororcorpParaService.updateById(authororcorpPara);
            }
        }

        // 供应商定制项目表
        for (LitemallAuthororcorpItems item : authororcorpItems) {
            item.setAuthorId(authororcorp.getId());
            Integer codeId=item.getId();
            if(codeId==null){
                authororcorpItemsService.add(item);
            } else
            {
                authororcorpItemsService.updateById(item);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param authororcorp
     * @return
     */
    @Transactional
    public Object delete(LitemallAuthororcorp authororcorp) {
        Integer mainId= authororcorp.getId();
        if (mainId == null) {
            return ResponseUtil.badArgument();
        }

        authororcorpService.delete(mainId);
        authororcorpParaService.deleteByAuthorId(mainId);

        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(AuthororcorpAllinone allinone) {
        Object error = validate(allinone);
        if (error != null) {
            return error;
        }

        LitemallAuthororcorp authororcorp = allinone.getAuthororcorp();
        LitemallAuthororcorpPara[] authororcorpParas = allinone.getAuthororcorpParas();
        LitemallAuthororcorpItems[] authororcorpItems=allinone.getAuthororcorpItems();

//        String name = authororcorp.getName();
//        if (authororcorpService.checkExistByNameAndId(name,authororcorp.getId())) {
//            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "供应商名称已经存在");
//        }
        //保存字典主表
        authororcorpService.add(authororcorp);
        // 供应商参数
        for (LitemallAuthororcorpPara authororcorpPara : authororcorpParas) {
            authororcorpPara.setAuthorId(authororcorp.getId());
            authororcorpParaService.add(authororcorpPara);
        }
        // 供应商定制项目表
        for (LitemallAuthororcorpItems item : authororcorpItems) {
            item.setAuthorId(authororcorp.getId());
            authororcorpItemsService.add(item);

        }

        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallAuthororcorp authororcorp = authororcorpService.query(id);
        List<LitemallAuthororcorpPara> authororcorpParas = authororcorpParaService.queryByAuthorId(id);
        List<LitemallAuthororcorpItems> authororcorpItems = authororcorpItemsService.queryByAuthorId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("authororcorp", authororcorp);
        data.put("paras", authororcorpParas);
        data.put("items", authororcorpItems);

        return ResponseUtil.ok(data);
    }

    public Object getAuthorParas(@NotNull Integer id) {
        List<LitemallAuthororcorpPara> authororcorpParas=authororcorpParaService.queryByAuthorId(id);

        return ResponseUtil.ok(authororcorpParas);
    }



}
