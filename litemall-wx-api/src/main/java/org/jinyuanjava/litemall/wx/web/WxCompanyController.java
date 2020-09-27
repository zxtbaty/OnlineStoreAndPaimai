package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallCompany;
import org.jinyuanjava.litemall.db.service.LitemallCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wx/company")
@Validated
@Api(description = "微信前端/商贸类型管理:/wx/company")
public class WxCompanyController {
    private final Log logger = LogFactory.getLog(WxCompanyController.class);

    @Autowired
    private LitemallCompanyService companyService;


    /**
     * 获取全部公司列表
     * @return
     */
    @GetMapping("listall")
    public Object getCompanyAll() {
        List<LitemallCompany> companyList = companyService.queryIndex();
        return ResponseUtil.okList(companyList);
    }


    /**
     * 返回可预约公司
     * @return
     */
    @GetMapping("listyuyue")
    public Object getCompanyYuYueList() {
        List<LitemallCompany> companyList = companyService.queryYuYue();
        return ResponseUtil.okList(companyList);

    }


}
