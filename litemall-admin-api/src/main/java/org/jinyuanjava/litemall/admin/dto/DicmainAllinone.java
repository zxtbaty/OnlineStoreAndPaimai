package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class DicmainAllinone {
    LitemallDicMain dicmain;
    LitemallDicCode[] diccodes;

    public LitemallDicMain getDicmain() {
        return dicmain;
    }

    public void setDicmain(LitemallDicMain dicmain) {
        this.dicmain = dicmain;
    }

    public LitemallDicCode[] getDiccodes() {
        return diccodes;
    }

    public void setDiccodes(LitemallDicCode[] diccodes) {
        this.diccodes = diccodes;
    }
}
