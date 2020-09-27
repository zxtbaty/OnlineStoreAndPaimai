package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.DicmainAllinone;
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
public class AdminDicmainService {
    private final Log logger = LogFactory.getLog(AdminDicmainService.class);

    @Autowired
    private LitemallDicmainService litemallDicmainService;
    @Autowired
    private LitemallDiccodeService litemallDiccodeService;


    public Object list(String name,
                       Integer page, Integer limit, String sort) {
        List<LitemallDicMain> dicMainsList = litemallDicmainService.querySelective(name, page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(DicmainAllinone dicmainAllinone) {
        LitemallDicMain litemallDicMain = dicmainAllinone.getDicmain();
        String name = litemallDicMain.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "字典表名称不不能为空");
        }
        LitemallDicCode[] litemallDicCodes = dicmainAllinone.getDiccodes();
        for (LitemallDicCode litemallDicCode : litemallDicCodes) {
            String codeName = litemallDicCode.getName();
            if (StringUtils.isEmpty(codeName)) {
                return ResponseUtil.fail(401, "字典表代码名称不能为空");
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
    public Object update(DicmainAllinone dicmainAllinone) {
        Object error = validate(dicmainAllinone);
        if (error != null) {
            return error;
        }
        LitemallDicMain dicMain=dicmainAllinone.getDicmain();
        LitemallDicCode[] dicCodes = dicmainAllinone.getDiccodes();

        Integer mainId = dicMain.getId();
        if(mainId==null){
            litemallDicmainService.add(dicMain);
        }else
        {
            litemallDicmainService.updateById(dicMain);
        }

        // 商品规格表litemall_goods_specification
        for (LitemallDicCode litemallDicCode : dicCodes) {
            litemallDicCode.setMainid(dicMain.getId());
            litemallDicCode.setMainname(dicMain.getName());
            Integer codeId=litemallDicCode.getId();
            if(codeId==null){
                litemallDiccodeService.add(litemallDicCode);
            } else
            {
                litemallDiccodeService.updateById(litemallDicCode);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param litemallDicMain
     * @return
     */
    @Transactional
    public Object delete(LitemallDicMain litemallDicMain) {
        Integer id = litemallDicMain.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        Integer mainId = litemallDicMain.getId();
        litemallDicmainService.deleteById(mainId);
        litemallDiccodeService.deleteByMainId(mainId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(DicmainAllinone dicmainAllinone) {
        Object error = validate(dicmainAllinone);
        if (error != null) {
            return error;
        }

        LitemallDicMain dicMain = dicmainAllinone.getDicmain();
        LitemallDicCode[] dicCodes = dicmainAllinone.getDiccodes();

        String name = dicMain.getName();
        if (litemallDicmainService.checkExistByName(name)) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "字典名称已经存在");
        }
        //保存字典主表
        litemallDicmainService.add(dicMain);
        // 字典明细表
        for (LitemallDicCode litemallDicCode : dicCodes) {
            litemallDicCode.setMainid(dicMain.getId());
            litemallDicCode.setMainname(dicMain.getName());
            litemallDiccodeService.add(litemallDicCode);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallDicMain litemallDicMain = litemallDicmainService.findById(id);
        List<LitemallDicCode> dicCodes = litemallDiccodeService.queryByMainId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("dicmain", litemallDicMain);
        data.put("diccodes", dicCodes);

        return ResponseUtil.ok(data);
    }

}
