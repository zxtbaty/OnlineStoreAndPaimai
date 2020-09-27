package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsMapper;
import org.jinyuanjava.litemall.db.dao.LitemallGoodsSpecificationMapper;
import org.jinyuanjava.litemall.db.domain.LitemallGoods;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsExample;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsSpecification;
import org.jinyuanjava.litemall.db.domain.LitemallGoodsSpecificationExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LitemallGoodsSpecificationService {
    @Resource
    private LitemallGoodsSpecificationMapper goodsSpecificationMapper;

    @Resource
    private LitemallGoodsMapper goodsMapper;

    public List<LitemallGoodsSpecification> queryByGid(Integer id) {
        LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
        example.or().andGoodsIdEqualTo(id).andDeletedEqualTo(false);
        return goodsSpecificationMapper.selectByExample(example);
    }

    public LitemallGoodsSpecification findById(Integer id) {
        return goodsSpecificationMapper.selectByPrimaryKey(id);
    }

    public List<LitemallGoodsSpecification> getGoodsSpecificationForInterface(
            LocalDateTime lastUpdateTime,Integer comId, Integer page,Integer limit){
        LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
        LitemallGoodsSpecificationExample.Criteria criteria=example.createCriteria();
        if(lastUpdateTime!=null){
            criteria.andUpdateTimeGreaterThan(lastUpdateTime);
        }
        if(!StringUtils.isEmpty(comId)){
            LitemallGoodsExample goodsExample=new LitemallGoodsExample();
            LitemallGoodsExample.Criteria goodsCriteria= goodsExample.createCriteria();
            goodsCriteria.andComIdEqualTo(comId);
            List<LitemallGoods> goodsList= goodsMapper.selectByExample(goodsExample);
            List<Integer> goodsIds=goodsList.stream().map(LitemallGoods::getId).collect(Collectors.toList());
            if(goodsIds!=null&&goodsIds.size()>0){
                criteria.andGoodsIdIn(goodsIds);
            }
        }
        //criteria.andDeletedEqualTo(false);
        PageHelper.startPage(page,limit);
        return goodsSpecificationMapper.selectByExample(example);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
        example.or().andGoodsIdEqualTo(gid);
        goodsSpecificationMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallGoodsSpecification goodsSpecification) {
        goodsSpecification.setAddTime(LocalDateTime.now());
        goodsSpecification.setUpdateTime(LocalDateTime.now());
        goodsSpecificationMapper.insertSelective(goodsSpecification);
    }

    public void update(LitemallGoodsSpecification goodsSpecification){
        goodsSpecification.setUpdateTime(LocalDateTime.now());
        goodsSpecificationMapper.updateByPrimaryKey(goodsSpecification);
    }

    /**
     * [
     * {
     * name: '',
     * valueList: [ {}, {}]
     * },
     * {
     * name: '',
     * valueList: [ {}, {}]
     * }
     * ]
     *
     * @param id
     * @return
     */
    public Object getSpecificationVoList(Integer id) {
        List<LitemallGoodsSpecification> goodsSpecificationList = queryByGid(id);

        Map<String, VO> map = new HashMap<>();
        List<VO> specificationVoList = new ArrayList<>();

        for (LitemallGoodsSpecification goodsSpecification : goodsSpecificationList) {
            String specification = goodsSpecification.getSpecification();
            VO goodsSpecificationVo = map.get(specification);
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new VO();
                goodsSpecificationVo.setName(specification);
                List<LitemallGoodsSpecification> valueList = new ArrayList<>();
                valueList.add(goodsSpecification);
                goodsSpecificationVo.setValueList(valueList);
                map.put(specification, goodsSpecificationVo);
                specificationVoList.add(goodsSpecificationVo);
            } else {
                List<LitemallGoodsSpecification> valueList = goodsSpecificationVo.getValueList();
                valueList.add(goodsSpecification);
            }
        }

        return specificationVoList;
    }

    private class VO {
        private String name;
        private List<LitemallGoodsSpecification> valueList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<LitemallGoodsSpecification> getValueList() {
            return valueList;
        }

        public void setValueList(List<LitemallGoodsSpecification> valueList) {
            this.valueList = valueList;
        }
    }

}
