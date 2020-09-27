package org.jinyuanjava.litemall.admin.vo;

import java.util.List;

public class MenuClassVo {
    private Integer id;
    private String name;
    private Integer level;
    private Integer parentId;
    private List<MenuClassVo> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<MenuClassVo> getChildren() {
        return children;
    }

    public void setChildren(List<MenuClassVo> children) {
        this.children = children;
    }
}
