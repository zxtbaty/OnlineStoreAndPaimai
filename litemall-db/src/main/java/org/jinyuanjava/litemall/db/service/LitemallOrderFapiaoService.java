package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.LitemallOrderFapiaoMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOrderFapiao;
import org.jinyuanjava.litemall.db.domain.LitemallOrderFapiaoExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallOrderFapiaoService {
    @Resource
    private LitemallOrderFapiaoMapper orderFapiaoMapper;

    public int add(LitemallOrderFapiao orderFapiao) {
        orderFapiao.setAddTime(LocalDateTime.now());
        orderFapiao.setUpdateTime(LocalDateTime.now());
        return orderFapiaoMapper.insertSelective(orderFapiao);
    }

    public List<LitemallOrderFapiao> queryByOid(Integer orderId) {
        LitemallOrderFapiaoExample example = new LitemallOrderFapiaoExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderFapiaoMapper.selectByExample(example);
    }

    public LitemallOrderFapiao findById(Integer id) {

        return orderFapiaoMapper.selectByPrimaryKey(id);
    }


    public void updateById(LitemallOrderFapiao orderFapiao) {
        orderFapiao.setUpdateTime(LocalDateTime.now());
        orderFapiaoMapper.updateByPrimaryKeySelective(orderFapiao);
    }


    public boolean checkExist(Integer orderId) {
        LitemallOrderFapiaoExample example = new LitemallOrderFapiaoExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderFapiaoMapper.countByExample(example) != 0;
    }


}
