package org.jinyuanjava.litemall.db.dao;

import java.util.List;
import java.util.Map;

public interface CommonDBMapper {


    /**
     * 传入Sql语句通过存储过程返回分页的结果，包括结果集及总条数
     * @param param 传入的含有分页的语句
     * @return
     */
    List procedureDaoList(Map<String, Object> param);

    Integer procedureInsert(Map<String, Object> param);

    void procedureExec(String strSql);

}
