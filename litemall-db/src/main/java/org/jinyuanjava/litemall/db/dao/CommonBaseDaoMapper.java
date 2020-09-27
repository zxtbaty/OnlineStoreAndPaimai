package org.jinyuanjava.litemall.db.dao;

import java.util.LinkedHashMap;
import java.util.List;

public interface CommonBaseDaoMapper {
    List<LinkedHashMap<String, Object>> select(String sql);
    int insert(String sql);
    int update(String sql);
    int delete(String sql);
}
