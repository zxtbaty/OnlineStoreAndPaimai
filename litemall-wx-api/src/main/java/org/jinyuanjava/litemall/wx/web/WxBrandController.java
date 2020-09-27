package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallBrand;
import org.jinyuanjava.litemall.db.service.LitemallBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 专题服务
 */
@RestController
@RequestMapping("/wx/brand")
@Validated
@Api(description = "微信前端/品牌管理:/wx/brand")
public class WxBrandController {
    private final Log logger = LogFactory.getLog(WxBrandController.class);

    @Autowired
    private LitemallBrandService brandService;

    /**
     * 品牌列表
     * @param comId
     * @param ifDisplayNoGoods 无货品牌是否显示
     * @param page 分页页数
     * @param limit 分页大小
     * @param sort
     * @return 品牌列表
     */
    @GetMapping("list")
    public Object list(
            Integer comId,
            Integer ifDisplayNoGoods,
            @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallBrand> brandList = brandService.query(comId,ifDisplayNoGoods,page, limit, sort);
        return ResponseUtil.okList(brandList);
    }

    /**
     * 品牌详情
     *
     * @param id 品牌ID
     * @return 品牌详情
     */
    @GetMapping("detail")
    public Object detail(@NotNull Integer id) {
        LitemallBrand entity = brandService.findById(id);
        if (entity == null) {
            return ResponseUtil.badArgumentValue();
        }

        return ResponseUtil.ok(entity);
    }
}
