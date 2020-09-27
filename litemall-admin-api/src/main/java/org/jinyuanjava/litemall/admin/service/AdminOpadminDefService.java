package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.OpadminDefAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminDef;
import org.jinyuanjava.litemall.db.domain.LitemallOpadminPub;
import org.jinyuanjava.litemall.db.service.LitemallOpadminDefService;
import org.jinyuanjava.litemall.db.service.LitemallOpadminPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminOpadminDefService {
    private final Log logger = LogFactory.getLog(AdminOpadminDefService.class);

    @Autowired
    private LitemallOpadminDefService opadminDefService;
    @Autowired
    private LitemallOpadminPubService opadminPubService;


    public Object list(
            String typeCode,String typeName,String title,Integer expireFlag,
           Integer page, Integer limit, String sort) {
        List<LitemallOpadminDef> dicMainsList = opadminDefService.querySelective(typeCode,typeName,title,expireFlag, page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(OpadminDefAllinone opadminDefAllinone) {
        LitemallOpadminDef opadminDef = opadminDefAllinone.getOpadminDef();
        if(opadminDef.getTitle()==null){
            return ResponseUtil.fail(401,"消息标题不能为空");
        }
        if(opadminDef.getContent()==null){
            return ResponseUtil.fail(401,"消息内容不能为空");
        }
        if(opadminDefAllinone.getOpadminPubs()==null){
            return ResponseUtil.fail(401,"必须设置消息要送达的客户");
        }
        return null;
    }


    @Transactional
    public Object update(OpadminDefAllinone opadminDefAllinone) {
        Object error = validate(opadminDefAllinone);
        if (error != null) {
            return error;
        }
        LitemallOpadminDef userinfoDef=opadminDefAllinone.getOpadminDef();
        LitemallOpadminPub[] userinfoPubs = opadminDefAllinone.getOpadminPubs();

        Integer mainId = userinfoDef.getId();
        if(mainId==null){
            opadminDefService.add(userinfoDef);
        }else
        {
            opadminDefService.updateById(userinfoDef);
        }

        for (LitemallOpadminPub userinfoPub : userinfoPubs) {
            userinfoPub.setInfoId(userinfoDef.getId());
            Integer codeId=userinfoPub.getId();
            if(codeId==null){
                opadminPubService.add(userinfoPub);
            } else
            {
                opadminPubService.updateById(userinfoPub);
            }
        }

        return ResponseUtil.ok();
    }

    /**
     * 删除字典表
     * @param userinfoDef
     * @return
     */
    @Transactional
    public Object delete(LitemallOpadminDef userinfoDef) {
        Integer userinfoDefId= userinfoDef.getId();
        if (userinfoDefId == null) {
            return ResponseUtil.badArgument();
        }

        opadminDefService.deleteById(userinfoDefId);
        opadminPubService.deleteByInfoId(userinfoDefId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(OpadminDefAllinone opadminDefAllinone) {
        Object error = validate(opadminDefAllinone);
        if (error != null) {
            return error;
        }

        LitemallOpadminDef userinfoDef = opadminDefAllinone.getOpadminDef();
        LitemallOpadminPub[] userinfoPubs = opadminDefAllinone.getOpadminPubs();


        //保存字典主表
        opadminDefService.add(userinfoDef);
        // 字典明细表
        for (LitemallOpadminPub userinfoPub : userinfoPubs) {
            userinfoPub.setInfoId(userinfoDef.getId());

            opadminPubService.add(userinfoPub);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallOpadminDef userinfoDef = opadminDefService.findById(id);
        List<LitemallOpadminPub> userinfoPubs = opadminPubService.queryByInfoId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("userinfoDef", userinfoDef);
        data.put("userinfoPubs", userinfoPubs);

        return ResponseUtil.ok(data);
    }

    public Object getOpadminPubs(@NotNull Integer id) {
        List<LitemallOpadminPub> userinfoPubs=opadminPubService.queryByInfoId(id);

        return ResponseUtil.ok(userinfoPubs);
    }

}
