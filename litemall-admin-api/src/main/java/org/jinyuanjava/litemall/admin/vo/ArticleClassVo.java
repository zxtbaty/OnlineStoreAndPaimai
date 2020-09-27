package org.jinyuanjava.litemall.admin.vo;

import java.util.List;

public class ArticleClassVo {
    private Integer id;
    private String name;
    private Integer level;
    private Integer pid;
    private List<ArticleClassVo> children;

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

    public List<ArticleClassVo> getChildren() {
        return children;
    }

    public void setChildren(List<ArticleClassVo> children) {
        this.children = children;
    }
}
