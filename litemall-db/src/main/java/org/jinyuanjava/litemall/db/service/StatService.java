package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.StatMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class StatService {
    @Resource
    private StatMapper statMapper;

    public List<Map> statBrowse(String beginDate,String endDate) {
        return statMapper.statBrowse(beginDate,endDate);
    }

    public List<Map> statUser(String beginDate,String endDate) {
        return statMapper.statUser(beginDate,endDate);
    }

    public List<Map> statOrder(String beginDate,String endDate) {
        return statMapper.statOrder(beginDate,endDate);
    }

    public List<Map> statGoods(String beginDate,String endDate) {
        return statMapper.statGoods(beginDate,endDate);
    }

    public List<Map> statUserGoodsOrder(String beginDate,String endDate) {
        return statMapper.statUserGoodsOrder(beginDate,endDate);
    }

    public List<Map>  statGoodsOrderSumAmount(List<Integer> list){
        return statMapper.statGoodsOrderSumAmount(list);
    }

    public List<Map>  statGoodsOrderPDF( Integer id ){
        return statMapper.statGoodsOrderPDF(id);
    }




}
