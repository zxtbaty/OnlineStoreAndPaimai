package org.jinyuanjava.litemall.db.service;

import org.jinyuanjava.litemall.db.dao.LitemallSystemMapper;
import org.jinyuanjava.litemall.db.domain.LitemallSystem;
import org.jinyuanjava.litemall.db.domain.LitemallSystemExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LitemallSystemConfigService {
    @Resource
    private LitemallSystemMapper systemMapper;

    public Map<String, String> queryAll() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andDeletedEqualTo(false);

        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> systemConfigs = new HashMap<>();
        for (LitemallSystem item : systemList) {
            systemConfigs.put(item.getKeyName(), item.getKeyValue());
        }

        return systemConfigs;
    }

    public Map<String, String> listMall() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> data = new HashMap<>();
        for(LitemallSystem system : systemList){
            data.put(system.getKeyName(), system.getKeyValue());
        }
        return data;
    }

    public Map<String, String> listWx() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameLike("litemall_wx_%").andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> data = new HashMap<>();
        for(LitemallSystem system : systemList){
            data.put(system.getKeyName(), system.getKeyValue());
        }
        return data;
    }

    /**
     * 获取默认微商城设置
     * @return
     */
    public Map<String, String> listWeiShang() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameLike("litemall_weishang_%").andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> data = new HashMap<>();
        for(LitemallSystem system : systemList){
            data.put(system.getKeyName(), system.getKeyValue());
        }
        return data;
    }

    public Map<String, String> listOrder() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameLike("litemall_order_%").andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> data = new HashMap<>();
        for(LitemallSystem system : systemList){
            data.put(system.getKeyName(), system.getKeyValue());
        }
        return data;
    }

    public Map<String, String> listExpress() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameLike("litemall_express_%").andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        Map<String, String> data = new HashMap<>();
        for(LitemallSystem system : systemList){
            data.put(system.getKeyName(), system.getKeyValue());
        }
        return data;
    }

    public void updateConfig(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            LitemallSystemExample example = new LitemallSystemExample();
            example.or().andKeyNameEqualTo(entry.getKey()).andDeletedEqualTo(false);
            List<LitemallSystem> litemallSystems= systemMapper.selectByExample(example);
            if(litemallSystems.size()>0) {
                LitemallSystem system = new LitemallSystem();
                system.setKeyName(entry.getKey());
                system.setKeyValue(entry.getValue());
                system.setUpdateTime(LocalDateTime.now());
                systemMapper.updateByExampleSelective(system, example);

            } else
            {
                addConfig(entry.getKey(),entry.getValue());
            }

            //重新加载系统配置
        }

    }

    public void addConfig(String key, String value) {
        LitemallSystem system = new LitemallSystem();
        system.setKeyName(key);
        system.setKeyValue(value);
        system.setAddTime(LocalDateTime.now());
        system.setUpdateTime(LocalDateTime.now());
        systemMapper.insertSelective(system);
    }

    public Boolean ifDisplayGoodsWhenStoreEqualZero(){
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andKeyNameLike("litemall_mall_goods_display_when_zero").andDeletedEqualTo(false);
        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
        if(systemList==null||systemList.size()==0){
            return true;
        } else
        {
            if(systemList.get(0).getKeyValue().equalsIgnoreCase("true")){
                return true;
            } else
            {
                return false;
            }
        }
    }

//    public Boolean ifSartTimeJob(){
//        LitemallSystemExample example = new LitemallSystemExample();
//        example.or().andKeyNameLike("litemall_mall_sart_time_job").andDeletedEqualTo(false);
//        List<LitemallSystem> systemList = systemMapper.selectByExample(example);
//        if(systemList==null||systemList.size()==0){
//            return true;
//        } else
//        {
//            if(systemList.get(0).getKeyValue().equalsIgnoreCase("true")){
//                return true;
//            } else
//            {
//                return false;
//            }
//        }
//    }
}

