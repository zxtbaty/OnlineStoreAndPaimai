package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallOpadminInfoMapper;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminDef;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminInfo;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminInfoExample;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminPub;
import org.jinyuanjava.litemall.db.util.DateUtil;
import org.jinyuanjava.litemall.db.util.NotifyPlaceholderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class LitemallOpadminInfoService {
    @Resource
    private LitemallOpadminInfoMapper litemallOpadminInfoMapper;

    @Resource
    private NotifyPlaceholderResolver notifyPlaceholderResolver;

    @Autowired
    private LitemallOpadminDefService opadminDefService;

    @Autowired
    private LitemallOpadminPubService opadminPubService;

    public List<LitemallOpadminInfo> querySelective(
            String opadminName,
            String typeCode,String typeName,String sourceCode,String sourceName,String title,String content,
            String strBeginDate,String strEndDate,Boolean ifViewed,
            Integer page,Integer limit,String sort){
        LitemallOpadminInfoExample example=new LitemallOpadminInfoExample();
        LitemallOpadminInfoExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(opadminName)){
            criteria.andOpadminNameEqualTo(opadminName);
        }
        if(!StringUtils.isEmpty(typeCode)){
            criteria.andTypeCodeEqualTo(typeCode);
        }
        if(!StringUtils.isEmpty(typeName)){
            criteria.andTypeNameEqualTo(typeName);
        }
        if(!StringUtils.isEmpty(sourceCode)){
            criteria.andSourceCodeEqualTo(sourceCode);
        }
        if(!StringUtils.isEmpty(sourceName)){
            criteria.andSourceNameEqualTo(sourceName);
        }
        if(!StringUtils.isEmpty(title)){
            criteria.andTitleLike("%"+title+"%");
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%"+content+"%");
        }
        if(!StringUtils.isEmpty(strBeginDate)){
            criteria.andAddTimeGreaterThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(strBeginDate));
        }
        if(!StringUtils.isEmpty(strEndDate)){
            criteria.andAddTimeLessThanOrEqualTo(DateUtil.getLocalDateTimeFromStringHM(strEndDate));
        }
        if(!StringUtils.isEmpty(ifViewed)){
            criteria.andIfViewedEqualTo(ifViewed);
        }
        criteria.andDeletedEqualTo(false);
        if(!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        PageHelper.startPage(page,limit);
        return litemallOpadminInfoMapper.selectByExample(example);
    }

    public void add(LitemallOpadminInfo userinfoDef){
        userinfoDef.setAddTime(LocalDateTime.now());
        userinfoDef.setUpdateTime(LocalDateTime.now());
        litemallOpadminInfoMapper.insertSelective(userinfoDef);
    }

    public int updateById(LitemallOpadminInfo userinfoDef){
        userinfoDef.setUpdateTime(LocalDateTime.now());
        return litemallOpadminInfoMapper.updateByPrimaryKeySelective(userinfoDef);
    }
    public void deleteById(Integer id){
        litemallOpadminInfoMapper.logicalDeleteByPrimaryKey(id);
    }


    public LitemallOpadminInfo findById(Integer id){
        return litemallOpadminInfoMapper.selectByPrimaryKey(id);
    }

    public boolean checkExistByName(String title) {
        LitemallOpadminInfoExample example = new LitemallOpadminInfoExample();
        example.or().andTitleEqualTo(title).andDeletedEqualTo(false);
        return litemallOpadminInfoMapper.countByExample(example) != 0;
    }

    public Boolean addLitemallOpadminInfo(LitemallOpadminInfo litemallOpadminInfo,
        Map<String,Object> mapParas){
        //查询后端消息定义表
        String typeName=litemallOpadminInfo.getTypeName();
        String infoTitle=litemallOpadminInfo.getTitle();
        List<LitemallOpadminDef> opadminDefs= opadminDefService.querySelective(null,typeName,infoTitle,0,1,10,null);
        if(opadminDefs.size()<=0){
            return false;
        }
        LitemallOpadminDef firstDef=opadminDefs.get(0);
        litemallOpadminInfo.setInfoId(firstDef.getId());
        litemallOpadminInfo.setTypeCode(firstDef.getTypeCode());
        litemallOpadminInfo.setWebHint(firstDef.getWebHint());
        litemallOpadminInfo.setSmsHint(firstDef.getSmsHint());
        litemallOpadminInfo.setMailHint(firstDef.getMailHint());
        litemallOpadminInfo.setPopHint(firstDef.getPopHint());

        String content=firstDef.getContent();
        String replaceContent= notifyPlaceholderResolver.resolveByMap(content,mapParas);
        litemallOpadminInfo.setContent(replaceContent);
        List<LitemallOpadminPub> opadminPubs= opadminPubService.queryByInfoId(firstDef.getId());
        //按通知对象进行写入
        for(LitemallOpadminPub opadminPub:opadminPubs){
            LitemallOpadminInfo newOpadminInfo=litemallOpadminInfo;
            newOpadminInfo.setOpadminId(opadminPub.getUserId());
            newOpadminInfo.setOpadminName(opadminPub.getUserName());
            add(newOpadminInfo);
        }
        return true;
    }

    public Long countSelective(
            String opadminName){
        LitemallOpadminInfoExample example=new LitemallOpadminInfoExample();
        LitemallOpadminInfoExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(opadminName)){
            criteria.andOpadminNameEqualTo(opadminName);
        }

        criteria.andDeletedEqualTo(false);
        criteria.andIfViewedEqualTo(false);
        example.orderBy("add_time desc");

        return litemallOpadminInfoMapper.countByExample(example);
    }

    public Boolean updateIfViewSelective(
            Integer[]  infoIds){
        LitemallOpadminInfoExample example=new LitemallOpadminInfoExample();
        LitemallOpadminInfoExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(infoIds)){
            criteria.andIdIn(Arrays.asList(infoIds));
        }

        List<LitemallOpadminInfo> opadminInfos=litemallOpadminInfoMapper.selectByExample(example);

        for(LitemallOpadminInfo opadminInfo:opadminInfos){
            opadminInfo.setIfViewed(true);
            litemallOpadminInfoMapper.updateByPrimaryKey(opadminInfo);
        }

        return true;
    }

}
