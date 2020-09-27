package org.jinyuanjava.litemall.admin.dto;

import org.jinyuanjava.litemall.db.domain.*;

public class CompanyAllinone {
    LitemallCompany company;
    LitemallCompanyBrand[] companyBrands;
    LitemallCompanyHangzhanlou[] companyHangzhanlous;

    public LitemallCompany getCompany() {
        return company;
    }

    public void setCompany(LitemallCompany company) {
        this.company = company;
    }

    public LitemallCompanyBrand[] getCompanyBrands() {
        return companyBrands;
    }

    public void setCompanyBrands(LitemallCompanyBrand[] companyBrands) {
        this.companyBrands = companyBrands;
    }

    public LitemallCompanyHangzhanlou[] getCompanyHangzhanlous() {
        return companyHangzhanlous;
    }

    public void setCompanyHangzhanlous(LitemallCompanyHangzhanlou[] companyHangzhanlous) {
        this.companyHangzhanlous = companyHangzhanlous;
    }
}
