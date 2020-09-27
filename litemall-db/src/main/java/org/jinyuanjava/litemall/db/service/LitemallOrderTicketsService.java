package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.LitemallOrderTicketsMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOrderTickets;
import org.jinyuanjava.litemall.db.domain.LitemallOrderTicketsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallOrderTicketsService {
    @Resource
    private LitemallOrderTicketsMapper orderTicketsMapper;
    @Autowired
    private LitemallOrderService orderService;

    public int add(LitemallOrderTickets orderTickets) {
        orderTickets.setAddTime(LocalDateTime.now());
        orderTickets.setUpdateTime(LocalDateTime.now());
        return orderTicketsMapper.insertSelective(orderTickets);
    }

    public List<LitemallOrderTickets> queryByOid(Integer orderId) {
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderTicketsMapper.selectByExample(example);
    }

    public List<LitemallOrderTickets> findByOidAndTid(Integer orderId, String ticketId) {
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        example.or().andOrderIdEqualTo(orderId).andTicketIdEqualTo(ticketId).andDeletedEqualTo(false);
        return orderTicketsMapper.selectByExample(example);
    }

    public LitemallOrderTickets findById(Integer id) {

        return orderTicketsMapper.selectByPrimaryKey(id);
    }

    public List<LitemallOrderTickets> findByIds(List<Integer> ids) {
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        example.or().andOrderIdIn(ids).andDeletedEqualTo(false);
        return orderTicketsMapper.selectByExample(example);

    }


    public void updateById(LitemallOrderTickets orderTickets) {
        orderTickets.setUpdateTime(LocalDateTime.now());
        orderTicketsMapper.updateByPrimaryKeySelective(orderTickets);
    }

    public Short getComments(Integer orderId) {
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        long count = orderTicketsMapper.countByExample(example);
        return (short) count;
    }

    public boolean checkExist(String ticketId) {
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        example.or().andTicketIdEqualTo(ticketId).andDeletedEqualTo(false);
        return orderTicketsMapper.countByExample(example) != 0;
    }

    public LitemallOrderTickets judgeOrderTicketIfExists(Integer orderId,String ticketId){
        LitemallOrderTicketsExample example = new LitemallOrderTicketsExample();
        LitemallOrderTicketsExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(orderId)){
            criteria.andOrderIdEqualTo(orderId);
        }
        if(!StringUtils.isEmpty(ticketId)){
            criteria.andTicketIdEqualTo(ticketId);
        }
        criteria.andDeletedEqualTo(false);
        return orderTicketsMapper.selectOneByExample(example);
    }

    public List<LitemallOrderTickets> getOrderTicketsByUserId(Integer userId){
        //先查询有效的订单 订单状态不为取消的

        List<Integer> orderIds=orderService.getUnCancelOrderIdList(userId);

        LitemallOrderTicketsExample orderTicketsExample=new LitemallOrderTicketsExample();
        LitemallOrderTicketsExample.Criteria orderTickets= orderTicketsExample.createCriteria();
        orderTickets.andOrderIdIn(orderIds);
        return orderTicketsMapper.selectByExample(orderTicketsExample);
    }



}
