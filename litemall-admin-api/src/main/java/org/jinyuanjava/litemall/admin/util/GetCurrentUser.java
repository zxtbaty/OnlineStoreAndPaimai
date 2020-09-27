package org.jinyuanjava.litemall.admin.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jinyuanjava.litemall.db.domain.LitemallAdmin;


public class GetCurrentUser {
    /**
     * 获取当前后台操作用户的ID
     * @return
     */
    public static String getCurrentUserId() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        return admin.getId().toString();
    }

    /**
     * 获取当前后台操作用户的ID
     * @return
     */
    public static Integer getCurrentUserIdInt() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        return admin.getId();
    }


    /**
     * 获取当前后台操作用户的结构
     * @return
     */
    public static LitemallAdmin getCurrentUser() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        return admin;
    }

}
