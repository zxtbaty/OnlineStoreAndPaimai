package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoPub;

public class UserinfoDefAllinone {
    LitemallUserinfoDef userinfoDef;
    LitemallUserinfoPub[] userinfoPubs;

    public LitemallUserinfoDef getUserinfoDef() {
        return userinfoDef;
    }

    public void setUserinfoDef(LitemallUserinfoDef userinfoDef) {
        this.userinfoDef = userinfoDef;
    }

    public LitemallUserinfoPub[] getUserinfoPubs() {
        return userinfoPubs;
    }

    public void setUserinfoPubs(LitemallUserinfoPub[] userinfoPubs) {
        this.userinfoPubs = userinfoPubs;
    }
}
