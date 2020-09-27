package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.service.CommonService;
import org.jinyuanjava.litemall.admin.util.ProcessLinuxShellUtil;
import org.jinyuanjava.litemall.core.util.JacksonUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/admin/commonLog")
@Validated
@Api(description = "后台管理/处理日志:/admin/commonLog")
public class AdminCommonLogController {
    private final Log logger = LogFactory.getLog(AdminCommonLogController.class);

    @Autowired
    private CommonService commonService;

    @RequiresPermissions("admin:commonLog:getLogSetting")
    @RequiresPermissionsDesc(menu={"维护工具" , "获取日志文件路径"}, button="日志路径")
    @GetMapping("/getLogSetting")
    public Object getLogSetting(String strKey) {
        //"logFilePath" 日志文件路径
        //logFileRows 日志文件显示行数
        String logFilePath= commonService.getProjectYml(strKey);
        return ResponseUtil.ok(logFilePath);
    }

    @RequiresPermissions("admin:commonLog:saveLogSetting")
    @RequiresPermissionsDesc(menu={"维护工具" , "保存日志文件路径"}, button="保存设置")
    @PostMapping("/saveLogSetting")
    public Object saveLogSetting(@RequestBody String data ) {
        String strKey = JacksonUtil.parseString(data, "strKey");
        String strValue = JacksonUtil.parseString(data, "strValue");
        Boolean saveResult= commonService.saveProjectYml(strKey,strValue);
        return ResponseUtil.ok(saveResult);
    }

    @RequiresPermissions("admin:commonLog:clearLog")
    @RequiresPermissionsDesc(menu={"维护工具" , "获取日志文件"}, button="清理日志")
    @GetMapping("/clearLog")
    public Object clearLog() {
//        第一种：cp /dev/null nohup.out
//        echo '' > nohup.out
//        第二种：cat /dev/null > nohup.out
        try {
            //切换目录
//            String strChangeDir="cd /opt";
//            ProcessLinuxShellUtil.execCommand(strChangeDir);
            String strClearLog="cat /dev/null > /opt/nohup.out";
//            ProcessLinuxShellUtil.execShell(strClearLog);
            String strResult= ProcessLinuxShellUtil.execCommand(strClearLog);
            return ResponseUtil.ok(strResult);
        }catch (Exception ex){
            return ResponseUtil.fail(502,"错误:"+ex.getMessage());
        }
    }

    /**
     * 检索输出日志文件
     * @param filePath
     * @param startDate
     * @param endDate
     * @param rows
     * @return
     */
    @RequiresPermissions("admin:commonLog:getLog")
    @RequiresPermissionsDesc(menu={"维护工具" , "获取日志文件"}, button="获取")
    @GetMapping("/getLog")
    public Object getLog(String filePath,String startDate,String endDate,Integer rows) {
        String strResult ="";
        try {
             strResult = tail(filePath, startDate, endDate, rows);
//            strResult = tail_New(filePath, startDate, endDate, rows);
             return ResponseUtil.ok(strResult);
        }catch (Exception ex){
            return ResponseUtil.fail(502,"读取文件错误");
        }
    }

    private String tail(String filePath,String startDate,String endDate,Integer rows) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(filePath, "r");
        long total = accessFile.length();
        int bath = 20000;
        int curN = 0;
        StringBuilder result = new StringBuilder();
        long pos = total - 1;
        if(bath>pos){
            bath=  (int)pos;
        }
        while (curN < rows && pos > 0) {
            byte[] bytes = new byte[bath];
            pos = pos - bath;
            pos = pos < 0 ? 0 : pos;
            accessFile.seek(pos);
            accessFile.read(bytes, 0, bath);

            String lines = new String(bytes, "GBK");
            String[] split = lines.split("\n");
            if (!lines.endsWith("\n") && curN > 0) {
                curN--;
            }
            int accept = rows - curN;
            int length = split.length;
            if (accept < length) {
                for (int i = length - 1; i >= length - accept; i--) {
                    if (i < length - 1) {
                        result.insert(0, "\n");
                    }
                    result.insert(0, split[i]);
                }
                curN = rows;
            } else {
                curN += split.length;
                result.insert(0, lines);
            }
        }

        try {
            accessFile.close();
        } catch (Exception ignore) {
             return "";
        }

        return result.toString();
    }

    /**
     * 下载日志文件
     * @param path
     * @param response
     */
    @RequiresPermissions("admin:commonLog:downLoadLog")
    @RequiresPermissionsDesc(menu={"维护工具" , "下载日志文件"}, button="获取")
    @GetMapping("/downLoadLog")
    public void download(String path, HttpServletResponse response) {
        try {
            if(path==null){
                path="/opt/nohup.out";
            }
            // path： 欲下载的文件的路径
            File file = new File(path);
            // 获取文件名 - 设置字符集
            String downloadFileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso-8859-1");
            // 以流的形式下载文件
            InputStream fis;
            fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
//            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
