package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.util.ProcessLinuxShellUtil;
import org.jinyuanjava.litemall.core.util.DateUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallBackupDatabase;
import org.jinyuanjava.litemall.db.service.LitemallBackupDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/backupDb")
@Validated
@Api(description = "后台管理/维护管理/备份DB:/admin/backupDb")
public class AdminBackupDatabaseController {
    private final Log logger = LogFactory.getLog(AdminBackupDatabaseController.class);

    @Autowired
    private LitemallBackupDatabaseService backupDatabaseService;

    @RequiresPermissions("admin:backupDb:list")
    @RequiresPermissionsDesc(menu={"维护管理" , "备份DB"}, button="查询")
    @GetMapping("/list")
    public Object list(String fileName,String backupBeginDate,
            String backupEndDate,
           @RequestParam(defaultValue = "1") Integer page,
           @RequestParam(defaultValue = "10") Integer limit) {

        List<LitemallBackupDatabase> backupDbList = backupDatabaseService.querySelective(fileName,
                backupBeginDate,backupEndDate,page, limit);
        return ResponseUtil.okList(backupDbList);
    }

    @RequiresPermissions("admin:backupDb:create")
    @RequiresPermissionsDesc(menu={"维护管理" , "备份DB"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallBackupDatabase backupDb) {
        backupDatabaseService.add(backupDb);
        return ResponseUtil.ok(backupDb);
    }

    @RequiresPermissions("admin:backupDb:delete")
    @RequiresPermissionsDesc(menu={"维护管理" , "备份DB"}, button="删除")
    @GetMapping("/delete")
    public Object create(Integer id) {
        backupDatabaseService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:backupDb:backup")
    @RequiresPermissionsDesc(menu={"维护管理" , "备份DB"}, button="执行")
    @PostMapping("/backup")
    public Object backup() {
        //todo 调用Shell脚本执行备份，如果成功返回true

        //设置数据库备份的文件名称
        String strDBName="Litemall_DB_"+ DateUtil.getDateyyyMMddHHmmss(LocalDateTime.now());

        //获取文件的绝对路径
        String strDbBackPath="/opt/dbbackup/";

        try {
            //调用Linux下的打包部署替换文件
//            ClassPathResource resource = new ClassPathResource("dbBackup.sh");
//            File deployFile = resource.getFile(); //获取resources根目录
            String dbBackUpSh="/opt/litemall/dbBackup.sh";
            String strResult=  ProcessLinuxShellUtil.execShell(dbBackUpSh, strDBName);
            if(strResult.startsWith("错误:")){
                return ResponseUtil.fail(502,strResult);
            } else
            {
                LitemallBackupDatabase backupDatabase=new LitemallBackupDatabase();
                backupDatabase.setBackupDate(LocalDate.now());
                backupDatabase.setFileName(strDbBackPath+strDBName);
                backupDatabase.setBackupPath(strDbBackPath);
                backupDatabaseService.add(backupDatabase);
                return ResponseUtil.ok(strResult);
            }
        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return ResponseUtil.fail(502,"执行错误");
        }

    }

    @RequiresPermissions("admin:backupDb:downLoad")
    @RequiresPermissionsDesc(menu={"维护管理" , "备份DB"}, button="下载")
    @GetMapping("/downLoad")
    public void downLoad(Integer id,HttpServletResponse response) {
        LitemallBackupDatabase backupDatabase=   backupDatabaseService.findById(id);
        //这里文件是全路径
        String path=backupDatabase.getFileName();
        try {
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
