package org.jinyuanjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.dto.UserinfoDefAllinone;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoDef;
import org.jinyuanjava.litemall.db.domain.LitemallUserinfoPub;
import org.jinyuanjava.litemall.db.service.LitemallUserinfoPubService;
import org.jinyuanjava.litemall.db.service.LitemallUserinfoDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserinfoDefService {
    private final Log logger = LogFactory.getLog(AdminUserinfoDefService.class);

    @Autowired
    private LitemallUserinfoDefService userinfoDefService;
    @Autowired
    private LitemallUserinfoPubService userinfoPubService;


    public Object list(
            String typeCode,String typeName,String title,Integer expireFlag,
           Integer page, Integer limit, String sort) {
        List<LitemallUserinfoDef> dicMainsList = userinfoDefService.querySelective(typeCode,typeName,title,expireFlag, page, limit, sort);
        return ResponseUtil.okList(dicMainsList);
    }

    private Object validate(UserinfoDefAllinone userinfoDefAllinone) {
        LitemallUserinfoDef userDef = userinfoDefAllinone.getUserinfoDef();
        if(userDef.getTitle()==null){
            return ResponseUtil.fail(401,"消息标题不能为空");
        }
        if(userDef.getContent()==null){
            return ResponseUtil.fail(401,"消息内容不能为空");
        }
        return null;
    }


    @Transactional
    public Object update(UserinfoDefAllinone userinfoDefAllinone) {
        Object error = validate(userinfoDefAllinone);
        if (error != null) {
            return error;
        }
        LitemallUserinfoDef userinfoDef=userinfoDefAllinone.getUserinfoDef();
        LitemallUserinfoPub[] userinfoPubs = userinfoDefAllinone.getUserinfoPubs();

        Integer mainId = userinfoDef.getId();
        if(mainId==null){
            userinfoDefService.add(userinfoDef);
        }else
        {
            userinfoDefService.updateById(userinfoDef);
        }

        for (LitemallUserinfoPub userinfoPub : userinfoPubs) {
            userinfoPub.setInfoId(userinfoDef.getId());
            Integer codeId=userinfoPub.getId();
            if(codeId==null){
                userinfoPubService.add(userinfoPub);
            } else
            {
                userinfoPubService.updateById(userinfoPub);
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
    public Object delete(LitemallUserinfoDef userinfoDef) {
        Integer userinfoDefId= userinfoDef.getId();
        if (userinfoDefId == null) {
            return ResponseUtil.badArgument();
        }

        userinfoDefService.deleteById(userinfoDefId);
        userinfoPubService.deleteByInfoId(userinfoDefId);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(UserinfoDefAllinone userinfoDefAllinone) {
        Object error = validate(userinfoDefAllinone);
        if (error != null) {
            return error;
        }

        LitemallUserinfoDef userinfoDef = userinfoDefAllinone.getUserinfoDef();
        LitemallUserinfoPub[] userinfoPubs = userinfoDefAllinone.getUserinfoPubs();


        //保存字典主表
        userinfoDefService.add(userinfoDef);
        // 字典明细表
        for (LitemallUserinfoPub userinfoPub : userinfoPubs) {
            userinfoPub.setInfoId(userinfoDef.getId());

            userinfoPubService.add(userinfoPub);
        }
        return ResponseUtil.ok();
    }

    public Object detail(Integer id) {
        LitemallUserinfoDef userinfoDef = userinfoDefService.findById(id);
        List<LitemallUserinfoPub> userinfoPubs = userinfoPubService.queryByInfoId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("userinfoDef", userinfoDef);
        data.put("userinfoPubs", userinfoPubs);

        return ResponseUtil.ok(data);
    }

    public Object getUserinfoPubs(@NotNull Integer id) {
        List<LitemallUserinfoPub> userinfoPubs=userinfoPubService.queryByInfoId(id);

        return ResponseUtil.ok(userinfoPubs);
    }

}
