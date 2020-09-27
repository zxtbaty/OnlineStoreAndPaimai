package org.jinyuanjava.litemall.admin.vo;

import java.util.List;

public class CompanyVo {
    private Integer id;
    private String name;
    private String label;
    private String poskey;
    private String desc;
    private Boolean owned;
    private Boolean enabled;
    private Integer ordernumber;
    private Integer level;
    private Integer pid;
    private String appId;
    private String appSecret;
    private List<CompanyVo> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<CompanyVo> getChildren() {
        return children;
    }

    public void setChildren(List<CompanyVo> children) {
        this.children = children;
    }

    public String getPoskey() {
        return poskey;
    }

    public void setPoskey(String poskey) {
        this.poskey = poskey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getOwned() {
        return owned;
    }

    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(Integer ordernumber) {
        this.ordernumber = ordernumber;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
