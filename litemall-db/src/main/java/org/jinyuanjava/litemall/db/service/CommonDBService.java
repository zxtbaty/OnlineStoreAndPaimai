package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.CommonDBMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommonDBService {
    @Resource
    private CommonDBMapper commonDBMapper;

    public List procedureDaoList(Map<String, Object> param ){

        List result= commonDBMapper.procedureDaoList(param);
        return result;
    }

    public Integer procedureInsert(Map<String, Object> param ){
        commonDBMapper.procedureInsert(param);
        return Integer.parseInt(param.get("id").toString());
    }

    public void procedureExec(String strSql ){
          commonDBMapper.procedureExec(strSql);
    }


    public String listToInteger(List<Integer> integerList) {
        if (integerList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (Integer integer : integerList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(integer);
        }
        return result.toString();
    }

    public String listToShort(List<Short> integerList) {
        if (integerList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (Short value : integerList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(value);
        }
        return result.toString();
    }
}
