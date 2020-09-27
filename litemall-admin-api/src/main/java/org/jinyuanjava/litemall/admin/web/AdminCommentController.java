package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallComment;
import org.jinyuanjava.litemall.db.service.LitemallCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/comment")
@Validated
@Api(description = "后台管理/商品管理/评论管理:/admin/comment")
public class AdminCommentController {
    private final Log logger = LogFactory.getLog(AdminCommentController.class);

    @Autowired
    private LitemallCommentService commentService;

    @RequiresPermissions("admin:comment:list")
    @RequiresPermissionsDesc(menu={"商品管理" , "评论管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String userId, String valueId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort
                       ) {
        List<LitemallComment> commentList = commentService.querySelective(userId, valueId, page, limit, sort);
        return ResponseUtil.okList(commentList);
    }

    @RequiresPermissions("admin:comment:delete")
    @RequiresPermissionsDesc(menu={"商品管理" , "评论管理"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallComment comment) {
        Integer id = comment.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        commentService.deleteById(id);
        return ResponseUtil.ok();
    }

}
