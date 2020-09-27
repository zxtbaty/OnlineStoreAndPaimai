package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.LitemallStore;
import org.jinyuanjava.litemall.db.domain.LitemallStoreBrand;

public class StoreAllinone {
    LitemallStore store;
    LitemallStoreBrand[] storeBrands;

    public LitemallStore getStore() {
        return store;
    }

    public void setStore(LitemallStore store) {
        this.store = store;
    }

    public LitemallStoreBrand[] getStoreBrands() {
        return storeBrands;
    }

    public void setStoreBrands(LitemallStoreBrand[] storeBrands) {
        this.storeBrands = storeBrands;
    }
}
