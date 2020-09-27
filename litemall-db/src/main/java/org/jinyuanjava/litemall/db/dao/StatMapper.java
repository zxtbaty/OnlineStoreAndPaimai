package org.jinyuanjava.litemall.db.dao;

import java.util.List;
import java.util.Map;

public interface StatMapper {
    List<Map> statBrowse(String beginDate,String endDate);

    List<Map> statUser(String beginDate,String endDate);

    List<Map> statOrder(String beginDate,String endDate);

    List<Map> statGoods(String beginDate,String endDate);

    List<Map> statUserGoodsOrder(String beginDate,String endDate);

    List<Map> statGoodsOrderSumAmount(List<Integer> ids);

    List<Map> statGoodsOrderPDF(Integer id);

    List<Map> getDefaultProductSpeci(List<Integer> goodsIds);


}
