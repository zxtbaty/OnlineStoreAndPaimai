package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallDicCode;
import org.jinyuanjava.litemall.db.service.LitemallDiccodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专题服务
 */
@RestController
@RequestMapping("/wx/code")
@Validated
@Api(description = "微信前端/代码管理:/wx/code")
public class WxDicCodeController {
    private final Log logger = LogFactory.getLog(WxDicCodeController.class);

    @Autowired
    private LitemallDiccodeService diccodeService;

    /**
     * 查询字典明细
     * @param dicmainId
     * @param dicmainName
     * @param sort
     * @return
     */
    @GetMapping("/list")
    public Object list(Integer dicmainId, String dicmainName,
                       @RequestParam(defaultValue = "add_time") String sort
    ) {
        List<LitemallDicCode> dicCodes= diccodeService.findByDicmainIdOrDicmainName(dicmainId,dicmainName,sort);

        return ResponseUtil.okList(dicCodes);
    }


}
