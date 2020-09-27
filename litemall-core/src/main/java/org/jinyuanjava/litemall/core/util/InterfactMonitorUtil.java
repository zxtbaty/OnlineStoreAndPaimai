package org.jinyuanjava.litemall.core.util;

import org.jinyuanjava.litemall.db.domain.LitemallInterfaceMonitor;
import org.jinyuanjava.litemall.db.service.LitemallInterfaceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InterfactMonitorUtil {

    @Autowired
    private  LitemallInterfaceMonitorService interfaceMonitorService;

    public void writeCRMInterfaceMonitorError(String strLogType,String strLogContent,String errMsg){
        LitemallInterfaceMonitor interfaceMonitor =new LitemallInterfaceMonitor();
        interfaceMonitor.setLogSource("机场CRM");
        interfaceMonitor.setLogDirection("调用他方接口");
        interfaceMonitor.setLogType(strLogType);
        interfaceMonitor.setLogContent(strLogContent);
        interfaceMonitor.setLogSyncType("手工同步");
        interfaceMonitor.setLogStateDesc("失败");
        interfaceMonitor.setLogStartTime(LocalDateTime.now());
        interfaceMonitor.setLogEndTime(LocalDateTime.now());
        interfaceMonitor.setErrorDate(LocalDateTime.now());
        interfaceMonitor.setErrorMsg(errMsg);
        interfaceMonitor.setAddTime(LocalDateTime.now());
        interfaceMonitor.setUpdateTime(LocalDateTime.now());
        interfaceMonitorService.add(interfaceMonitor);
    }
    public void writeDaxingPcInterfaceMonitorError(String strLogType,String strLogContent,String errMsg){
        LitemallInterfaceMonitor interfaceMonitor =new LitemallInterfaceMonitor();
        interfaceMonitor.setLogSource("大兴机场PC");
        interfaceMonitor.setLogDirection("调用他方接口");
        interfaceMonitor.setLogType(strLogType);
        interfaceMonitor.setLogContent(strLogContent);
        interfaceMonitor.setLogSyncType("手工同步");
        interfaceMonitor.setLogStateDesc("失败");
        interfaceMonitor.setLogStartTime(LocalDateTime.now());
        interfaceMonitor.setLogEndTime(LocalDateTime.now());
        interfaceMonitor.setErrorDate(LocalDateTime.now());
        interfaceMonitor.setErrorMsg(errMsg);
        interfaceMonitor.setAddTime(LocalDateTime.now());
        interfaceMonitor.setUpdateTime(LocalDateTime.now());
        interfaceMonitorService.add(interfaceMonitor);
    }
}
