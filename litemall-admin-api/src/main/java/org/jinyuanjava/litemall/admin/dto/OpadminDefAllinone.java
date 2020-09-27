package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.LitemallOpadminDef;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminPub;

public class OpadminDefAllinone {
    LitemallOpadminDef opadminDef;
    LitemallOpadminPub[] opadminPubs;

    public LitemallOpadminDef getOpadminDef() {
        return opadminDef;
    }

    public void setOpadminDef(LitemallOpadminDef opadminDef) {
        this.opadminDef = opadminDef;
    }

    public LitemallOpadminPub[] getOpadminPubs() {
        return opadminPubs;
    }

    public void setOpadminPubs(LitemallOpadminPub[] opadminPubs) {
        this.opadminPubs = opadminPubs;
    }
}
