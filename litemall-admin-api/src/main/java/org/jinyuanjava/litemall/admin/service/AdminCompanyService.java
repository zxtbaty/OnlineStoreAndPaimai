package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.CompanyAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.*;
import org.jinyuanjava.litemall.db.service.*;
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
public class AdminCompanyService {
    private final Log logger = LogFactory.getLog(AdminCompanyService.class);

    @Autowired
    private LitemallCompanyService companyService;
    @Autowired
    private LitemallCompanyBrandService companyBrandService;
    @Autowired
    private LitemallCompanyHangzhanlouService companyHangzhanlouService;

    public Object list(String name,
                       Integer page, Integer limit, String sort) {
        List<LitemallCompany> dicMainsList = companyService.querySelective(name,page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(CompanyAllinone companyAllinone) {
        LitemallCompany litemallCompany = companyAllinone.getCompany();
        String name = litemallCompany.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail(401, "公司名称不不能为空");
        }
        LitemallCompanyBrand[] companyBrands = companyAllinone.getCompanyBrands();
        if(companyBrands!=null) {
            for (LitemallCompanyBrand companyBrand : companyBrands) {
                String codeName = companyBrand.getBrandName();
                if (StringUtils.isEmpty(codeName)) {
                    return ResponseUtil.fail(401, "公司品牌商名称不能为空");
                }
            }
        }
        LitemallCompanyHangzhanlou[] companyHangzhanlous = companyAllinone.getCompanyHangzhanlous();
        if(companyHangzhanlous!=null) {
            for (LitemallCompanyHangzhanlou companyHangzhanlou : companyHangzhanlous) {
                String codeName = companyHangzhanlou.getHangzhanlouName();
                if (StringUtils.isEmpty(codeName)) {
                    return ResponseUtil.fail(401, "航站楼名称不能为空");
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
    public Object update(CompanyAllinone companyAllinone) {
        Object error = validate(companyAllinone);
        if (error != null) {
            return error;
        }
        LitemallCompany company=companyAllinone.getCompany();
        LitemallCompanyBrand[] companyBrands = companyAllinone.getCompanyBrands();
        LitemallCompanyHangzhanlou[] companyHangzhanlous=companyAllinone.getCompanyHangzhanlous();

        Integer mainId = company.getId();
        if(mainId==null){
            companyService.add(company);
        }else
        {
            companyService.updateById(company);
        }

        // 公司经营品牌表
        for (LitemallCompanyBrand companyBrand : companyBrands) {
            companyBrand.setComId(company.getId());
            companyBrand.setComName(company.getName());
            Integer codeId=companyBrand.getId();
            if(codeId==null){
                companyBrandService.add(companyBrand);
            } else
            {
                companyBrandService.updateById(companyBrand);
            }
        }
        // 公司航站楼
        for (LitemallCompanyHangzhanlou companyHangzhanlou : companyHangzhanlous) {
            companyHangzhanlou.setComId(company.getId());
            companyHangzhanlou.setComName(company.getName());
            Integer codeId=companyHangzhanlou.getId();
            if(codeId==null){
                companyHangzhanlouService.add(companyHangzhanlou);
            } else
            {
                companyHangzhanlouService.updateById(companyHangzhanlou);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param company
     * @return
     */
    @Transactional
    public Object delete(LitemallCompany company) {
        Integer comId= company.getId();
        if (comId == null) {
            return ResponseUtil.badArgument();
        }

        companyService.deleteById(comId);
        companyBrandService.deleteByComId(comId);
        companyHangzhanlouService.deleteByComId(comId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(CompanyAllinone companyAllinone) {
        Object error = validate(companyAllinone);
        if (error != null) {
            return error;
        }

        LitemallCompany company = companyAllinone.getCompany();
        LitemallCompanyBrand[] companyBrands = companyAllinone.getCompanyBrands();
        LitemallCompanyHangzhanlou[] companyHangzhanlous=companyAllinone.getCompanyHangzhanlous();

        String name = company.getName();
        if (companyService.checkExistByName(name)) {
            return ResponseUtil.fail(DICMAIN_NAME_EXIST, "公司名称已经存在");
        }
        //保存字典主表
        companyService.add(company);
        // 公司品牌
        for (LitemallCompanyBrand companyBrand : companyBrands) {
            companyBrand.setComId(company.getId());
            companyBrand.setComName(company.getName());
            companyBrandService.add(companyBrand);
        }
        // 公司航站楼
        for (LitemallCompanyHangzhanlou companyHangzhanlou : companyHangzhanlous) {
            companyHangzhanlou.setComId(company.getId());
            companyHangzhanlou.setComName(company.getName());
            companyHangzhanlouService.add(companyHangzhanlou);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallCompany store = companyService.findById(id);
        List<LitemallCompanyBrand> storeBrands = companyBrandService.queryByComId(id);
        List<LitemallCompanyHangzhanlou> companyHangzhanlous = companyHangzhanlouService.queryByComId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("company", store);
        data.put("companyBrands", storeBrands);
        data.put("companyHangzhanlous", companyHangzhanlous);

        return ResponseUtil.ok(data);
    }

    public Object getCompanyBrands(@NotNull Integer id) {
        List<LitemallCompanyBrand> storeBrands=companyBrandService.queryByComId(id);

        return ResponseUtil.ok(storeBrands);
    }

    public Object getHangzhanlous(@NotNull Integer id) {
        List<LitemallCompanyHangzhanlou> hangzhanlous=companyHangzhanlouService.queryByComId(id);

        return ResponseUtil.okList(hangzhanlous);
    }
}
