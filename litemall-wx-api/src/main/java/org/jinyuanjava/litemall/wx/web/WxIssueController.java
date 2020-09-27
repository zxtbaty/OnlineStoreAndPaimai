package org.jinyuanjava.litemall.wx.web;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallIssue;
import org.jinyuanjava.litemall.db.service.LitemallIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/issue")
@Validated
@Api(description = "微信前端/帮助中心:/wx/issue")
public class WxIssueController {
    private final Log logger = LogFactory.getLog(WxIssueController.class);

    @Autowired
    private LitemallIssueService issueService;

    /**
     * 帮助中心
     */
    @RequestMapping("/list")
    public Object list(String question,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallIssue> issueList = issueService.querySelective(question, page, size, sort);
        long total = PageInfo.of(issueList).getTotal();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("data", issueList);
        data.put("count", total);
        return ResponseUtil.ok(data);
    }

}
