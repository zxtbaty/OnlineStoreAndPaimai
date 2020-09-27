package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.dao.LitemallSystemMapper;
import org.jinyuanjava.litemall.db.domain.LitemallSystem;
import org.jinyuanjava.litemall.db.domain.LitemallSystemExample;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 专题服务
 */
@RestController
@RequestMapping("/wx/config")
@Validated
@Api(description = "微信前端/获取数据库配置:/wx/config")
public class WxConfigController {
    private final Log logger = LogFactory.getLog(WxConfigController.class);

    @Resource
    private LitemallSystemMapper systemMapper;

    /**
     * 获取超时多长时间取消
     * @return
     */
    @GetMapping("unPayCancelMinutes")
    public Object getUnPayCancelMinutes() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameEqualTo("litemall_order_unpaid").andDeletedEqualTo(false);
        LitemallSystem systemList = systemMapper.selectOneByExample(example);
        return ResponseUtil.ok(systemList.getKeyValue());
    }

}
