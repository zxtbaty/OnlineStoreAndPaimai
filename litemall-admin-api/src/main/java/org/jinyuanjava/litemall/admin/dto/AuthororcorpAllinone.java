package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class AuthororcorpAllinone {
    LitemallAuthororcorp authororcorp;
    LitemallAuthororcorpPara[] authororcorpParas;
    LitemallAuthororcorpItems[] authororcorpItems;

    public LitemallAuthororcorp getAuthororcorp() {
        return authororcorp;
    }

    public void setAuthororcorp(LitemallAuthororcorp authororcorp) {
        this.authororcorp = authororcorp;
    }

    public LitemallAuthororcorpPara[] getAuthororcorpParas() {
        return authororcorpParas;
    }

    public void setAuthororcorpParas(LitemallAuthororcorpPara[] authororcorpParas) {
        this.authororcorpParas = authororcorpParas;
    }

    public LitemallAuthororcorpItems[] getAuthororcorpItems() {
        return authororcorpItems;
    }

    public void setAuthororcorpItems(LitemallAuthororcorpItems[] authororcorpItems) {
        this.authororcorpItems = authororcorpItems;
    }
}
