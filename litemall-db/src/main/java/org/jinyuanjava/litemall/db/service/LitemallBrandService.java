package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallBrandMapper;
import org.jinyuanjava.litemall.db.domain.LitemallBrand;
import org.jinyuanjava.litemall.db.domain.LitemallBrand.Column;
import org.jinyuanjava.litemall.db.domain.LitemallBrandExample;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyBrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallBrandService {
    @Autowired
    LitemallCompanyBrandService companyBrandService;

    @Autowired
    LitemallGoodsService goodsService;

    @Resource
    private LitemallBrandMapper brandMapper;
    private Column[] columns = new Column[]{Column.id, Column.name, Column.desc, Column.picUrl, Column.floorPrice};



    public List<LitemallBrand> query(Integer comId,Integer ifDisplayNoGoods, Integer page, Integer limit, String sort) {
        List<Integer> brandIds=new ArrayList<>();
        if(comId!=null) {
            List<LitemallCompanyBrand> companyBrands = companyBrandService.querySelective(comId,null, page, limit, sort);
            brandIds = companyBrands.stream().map(LitemallCompanyBrand::getBrandId).collect(Collectors.toList());
        }
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria=example.createCriteria();
        criteria.andDeletedEqualTo(false).andEnabledEqualTo(false);

        //判断所有货物上架的品牌
        if(ifDisplayNoGoods!=null&&ifDisplayNoGoods==1) {
            List<Integer> brandIdsHaveGoods=goodsService.getBrandList(comId);
            criteria.andIdIn(brandIdsHaveGoods);
        }

        if(brandIds!=null&&brandIds.size()>0){
            criteria.andIdIn(brandIds);
            example.setOrderByClause("field(id,"+brandIds.toString().replace("[","").replace("]","")+")");
        }


//        if (!StringUtils.isEmpty(sort)) {
//            example.setOrderByClause(sort);
//        }

        PageHelper.startPage(page, limit);
        return brandMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallBrand> query(Integer page, Integer limit) {
        return query(null,null,page, limit, null);
    }

    public List<LitemallBrand> queryByLastUpdate(LocalDateTime updateTime, Integer page, Integer limit) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        //criteria.andEnabledEqualTo(false);
        if(updateTime!=null){
            criteria.andUpdateTimeGreaterThan(updateTime);
        }
        criteria.andDeletedEqualTo(false);


        PageHelper.startPage(page, limit);
        return brandMapper.selectByExample(example);
    }

    public LitemallBrand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }



    public List<LitemallBrand> querySelective(String id, String name,String sn, Integer page, Integer size, String sort) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(sn)) {
            criteria.andSnLike("%" + sn + "%");
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        //criteria.andEnabledEqualTo(false);
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, size);
        return brandMapper.selectByExample(example);
    }



    public List<LitemallBrand> queryByIds(List<Integer> ids) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(ids)) {
            criteria.andIdIn(ids);
        }

        return brandMapper.selectByExample(example);
    }

    public List<LitemallBrand> querySelectiveEnable(String id, String name,String sn, Integer page, Integer size, String sort) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(sn)) {
            criteria.andSnLike("%" + sn + "%");
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andEnabledEqualTo(false);
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, size);
        return brandMapper.selectByExample(example);
    }

    public int updateById(LitemallBrand brand) {
        brand.setUpdateTime(LocalDateTime.now());

        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    public void deleteById(Integer id) {
        brandMapper.logicalDeleteByPrimaryKey(id);

    }

    public void add(LitemallBrand brand) {
        brand.setAddTime(LocalDateTime.now());
        brand.setUpdateTime(LocalDateTime.now());
        brandMapper.insertSelective(brand);
    }

    public List<LitemallBrand> all() {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andEnabledEqualTo(false).andDeletedEqualTo(false);
        return brandMapper.selectByExample(example);
    }

    public boolean checkBrandIfExists(LitemallBrand brand){
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andNameEqualTo(brand.getName());
        if(brand.getId()!=null){
            criteria.andIdNotEqualTo(brand.getId());
        }
        return brandMapper.countByExample(example) != 0;
    }

}
