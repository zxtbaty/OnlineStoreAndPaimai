package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class HuodongsetAllinone {
    LitemallHuodongMain huodongMain;
    LitemallHuodongDetailPicLink[] huodongDetailPicLinks;
    LitemallHuodongDetailGoodsList[] huodongDetailGoodsLists;

    public LitemallHuodongMain getHuodongMain() {
        return huodongMain;
    }

    public void setHuodongMain(LitemallHuodongMain huodongMain) {
        this.huodongMain = huodongMain;
    }

    public LitemallHuodongDetailPicLink[] getHuodongDetailPicLinks() {
        return huodongDetailPicLinks;
    }

    public void setHuodongDetailPicLinks(LitemallHuodongDetailPicLink[] huodongDetailPicLinks) {
        this.huodongDetailPicLinks = huodongDetailPicLinks;
    }

    public LitemallHuodongDetailGoodsList[] getHuodongDetailGoodsLists() {
        return huodongDetailGoodsLists;
    }

    public void setHuodongDetailGoodsLists(LitemallHuodongDetailGoodsList[] huodongDetailGoodsLists) {
        this.huodongDetailGoodsLists = huodongDetailGoodsLists;
    }
}
