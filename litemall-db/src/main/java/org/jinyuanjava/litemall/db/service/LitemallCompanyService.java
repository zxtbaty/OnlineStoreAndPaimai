package org.jinyuanjava.litemall.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.jinyuanjava.litemall.db.dao.LitemallCompanyMapper;
import org.jinyuanjava.litemall.db.domain.LitemallCompany;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyBrand;
import org.jinyuanjava.litemall.db.domain.LitemallCompanyExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

@Service
public class LitemallCompanyService {
    @Resource
    private LitemallCompanyMapper litemallCompanyMapper;

    @Autowired
    private LitemallCompanyBrandService companyBrandService;


    public List<LitemallCompany> queryIndex(){
        LitemallCompanyExample example=new LitemallCompanyExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true);
        return litemallCompanyMapper.selectByExample(example);
    }

    /**
     * 查询预约商品
     * @return
     */
    public List<LitemallCompany> queryYuYue(){
        LitemallCompanyExample example=new LitemallCompanyExample();
        example.or().andDeletedEqualTo(false).andEnabledEqualTo(true).andIdNotEqualTo(1);
        return litemallCompanyMapper.selectByExample(example);
    }

    public List<LitemallCompany> querySelective(String name, Integer page,Integer limit,String sort){
        LitemallCompanyExample example=new LitemallCompanyExample();
        LitemallCompanyExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }

        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallCompanyMapper.selectByExample(example);
    }


    public int updateById(LitemallCompany homeIcon){
        homeIcon.setUpdateTime(LocalDateTime.now());
        return litemallCompanyMapper.updateByPrimaryKeySelective(homeIcon);
    }

    public void deleteById(Integer id){
        litemallCompanyMapper.logicalDeleteByPrimaryKey(id);
    }




    public void add(LitemallCompany homeIcon){
        homeIcon.setAddTime(LocalDateTime.now());
        homeIcon.setUpdateTime(LocalDateTime.now());
        litemallCompanyMapper.insertSelective(homeIcon);
    }


    public LitemallCompany findById(Integer id){
        return litemallCompanyMapper.selectByPrimaryKey(id);
    }


    public Object getStoreBrands( Integer id) {
    	if(id==null){
    		return null;
    	}
        List<LitemallCompanyBrand> storeBrands=companyBrandService.queryByComId(id);
        return storeBrands;
    }

    public boolean checkExistByName(String name) {
        LitemallCompanyExample example = new LitemallCompanyExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return litemallCompanyMapper.countByExample(example) != 0;
    }
}
