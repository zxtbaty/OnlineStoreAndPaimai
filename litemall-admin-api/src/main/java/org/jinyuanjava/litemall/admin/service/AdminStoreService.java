package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.StoreAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.LitemallStoreBrandService;
import org.jinyuanjava.litemall.db.service.LitemallStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jinyuanjava.litemall.admin.util.AdminResponseCode.DICMAIN_NAME_EXIST;

@Service
public class AdminStoreService {
    private final Log logger = LogFactory.getLog(AdminStoreService.class);

    @Autowired
    private LitemallStoreService storeService;
    @Autowired
    private LitemallStoreBrandService storeBrandService;


    public Object list(String name,
                       Integer comId,
                       Integer ownType,
                       Integer brandId,
                       Integer page, Integer limit, String sort) {
        List<LitemallStore> dicMainsList = storeService.querySelective(name,comId,ownType,brandId,null, page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(StoreAllinone storeAllinone) {
        LitemallStore litemallStore = storeAllinone.getStore();
        String name = litemallStore.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "店铺名称不不能为空");
        }
        LitemallStoreBrand[] storeBrands = storeAllinone.getStoreBrands();
        for (LitemallStoreBrand storeBrand : storeBrands) {
            String codeName = storeBrand.getBrandName();
            if (StringUtils.isEmpty(codeName)) {
                return ResponseUtil.fail(401, "店铺品牌商名称不能为空");
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
    public Object update(StoreAllinone storeAllinone) {
        Object error = validate(storeAllinone);
        if (error != null) {
            return error;
        }
        LitemallStore store=storeAllinone.getStore();
        LitemallStoreBrand[] storeBrands = storeAllinone.getStoreBrands();

        Integer mainId = store.getId();
        if(mainId==null){
            storeService.add(store);
        }else
        {
            storeService.updateById(store);
        }

        // 店铺经营品牌表
        for (LitemallStoreBrand storeBrand : storeBrands) {
            storeBrand.setStoreId(store.getId());
            storeBrand.setStoreName(store.getName());
            Integer codeId=storeBrand.getId();
            if(codeId==null){
                storeBrandService.add(storeBrand);
            } else
            {
                storeBrandService.updateById(storeBrand);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param store
     * @return
     */
    @Transactional
    public Object delete(LitemallStore store) {
        Integer storeId= store.getId();
        if (storeId == null) {
            return ResponseUtil.badArgument();
        }

        storeService.deleteById(storeId);
        storeBrandService.deleteByStoreId(storeId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(StoreAllinone storeAllinone) {
        Object error = validate(storeAllinone);
        if (error != null) {
            return error;
        }

        LitemallStore store = storeAllinone.getStore();
        LitemallStoreBrand[] storeBrands = storeAllinone.getStoreBrands();

        String name = store.getName();
        if (storeService.checkExistByName(name)) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "店铺名称已经存在");
        }
        //保存字典主表
        storeService.add(store);
        // 字典明细表
        for (LitemallStoreBrand storeBrand : storeBrands) {
            storeBrand.setStoreId(store.getId());
            storeBrand.setStoreName(store.getName());
            storeBrandService.add(storeBrand);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallStore store = storeService.findById(id);
        List<LitemallStoreBrand> storeBrands = storeBrandService.queryByStoreId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("store", store);
        data.put("storebrands", storeBrands);

        return ResponseUtil.ok(data);
    }

    public Object getStoreBrands(@NotNull Integer id) {
        List<LitemallStoreBrand> storeBrands=storeBrandService.queryByStoreId(id);

        return ResponseUtil.ok(storeBrands);
    }

}
