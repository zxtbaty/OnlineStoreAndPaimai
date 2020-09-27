package org.jinyuanjava.litemall.admin.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jinyuanjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.jinyuanjava.litemall.admin.util.ProcessLinuxShellUtil;
import org.jinyuanjava.litemall.admin.util.ProcessWindowsBatUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallUpdateVersion;
import org.jinyuanjava.litemall.db.service.CommonDBService;
import org.jinyuanjava.litemall.db.service.LitemallUpdateVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/updateVersion")
@Validated
@Api(description = "后台管理/维护管理/版本更新:/admin/updateVersion")
public class AdminUpdateVersionController {
    private final Log logger = LogFactory.getLog(AdminUpdateVersionController.class);

    @Autowired
    private LitemallUpdateVersionService updateVersionService;


    @Autowired
    private Environment environment;

    @Autowired
    private CommonDBService commonDBService;

    @RequiresPermissions("admin:updateVersion:list")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本更新"}, button="查询")
    @GetMapping("/list")
    public Object list(String versionType,String sourceFileName,String updateBeginDate,
                       String updateEndDate,Integer ifBushu, Integer ifCurrent,
           @RequestParam(defaultValue = "1") Integer page,
           @RequestParam(defaultValue = "10") Integer limit) {
        List<LitemallUpdateVersion> updateVersionList = updateVersionService.querySelective(versionType,sourceFileName,
                updateBeginDate,updateEndDate,ifBushu,ifCurrent,page, limit);
        return ResponseUtil.okList(updateVersionList);
    }

    @RequiresPermissions("admin:updateVersion:create")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本更新"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallUpdateVersion updateVersion) {
        try {
            String strWebRoot = ClassUtils.getDefaultClassLoader().getResource("").getPath();
            strWebRoot = URLDecoder.decode(strWebRoot, "utf-8");
            updateVersion.setFilePath(strWebRoot + environment.getProperty("litemall.storage.local.storagePath"));
            //上传日期
            updateVersion.setUpdateDate(LocalDate.now());
            //选择当前日期的最大版本数
            updateVersion.setFileVersion(updateVersionService.queryMaxVersionByAddDate(updateVersion.getVersionType(),updateVersion.getUpdateDate())+1);
            updateVersionService.add(updateVersion);
            return ResponseUtil.ok(updateVersion);
        }catch (Exception ex){
            return ResponseUtil.fail(502,"创建失败");
        }
    }

    @RequiresPermissions("admin:updateVersion:delete")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本更新"}, button="删除")
    @GetMapping("/delete")
    public Object create(Integer id) {
        updateVersionService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:updateVersion:downLoad")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本管理"}, button="下载")
    @GetMapping("/downLoad")
    public void downLoad(Integer id, HttpServletResponse response) {
        LitemallUpdateVersion updateVersion=updateVersionService.findById(id);
        //这里人文件是全路径
        String path=updateVersion.getFileName();
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


    @RequiresPermissions("admin:updateVersion:exec")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本更新"}, button="执行部署")
    @GetMapping("/exec")
    public Object exec(Integer id) {
        try {
            LitemallUpdateVersion updateVersion = updateVersionService.findById(id);
            //todo 调用Shell脚本执行部署更新
            //用Windows环境下的bat文件测试
//            ClassPathResource resource = new ClassPathResource("test.bat");
//            File testBatFile = resource.getFile(); //获取resources根目录
//            String filePath = java.net.URLDecoder.decode(testBatFile.getAbsolutePath(), "utf-8");
//            String strResult= ProcessWindowsBatUtil.execBat(filePath);

            //调用Linux下的打包部署替换文件
            //ClassPathResource resource =null;
            String strAbsolutePath=null;
            if(updateVersion.getVersionType().equals("后台")) {
                //resource = new ClassPathResource("deploy.sh");
                strAbsolutePath="/opt/litemall/deploy_back.sh";
            } else
            {
                //resource=new ClassPathResource("deploy_front.sh");
                strAbsolutePath="/opt/litemall/deploy_front.sh";
            }
            //InputStream inputStream = resource.getInputStream();
            //File deployFile = resource.getFile(); //获取resources根目录

            //获取Http后面的文件名称
            String strFileNames= updateVersion.getFileName();
            String strFileName = strFileNames.substring(strFileNames.lastIndexOf("/")+1, strFileNames.length());

            String strResult=  ProcessLinuxShellUtil.execShell(strAbsolutePath, strFileName);
            if(strResult.startsWith("错误:")){
                return ResponseUtil.fail(502,strResult);
            } else
            {
                //如果部署成功，更新已经部署标志，并且更新当前版本，
                String strUpdateSql="update litemall_update_version set if_bushu=1,if_current=1 where id="+id;
                commonDBService.procedureExec(strUpdateSql);
                strUpdateSql="update litemall_update_version set  if_current=0 where version_type='"+updateVersion.getVersionType()+" and id<>"+id;
                commonDBService.procedureExec(strUpdateSql);
                return ResponseUtil.ok(strResult);
            }

        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return ResponseUtil.fail(502,"执行错误");
        }
    }


    @RequiresPermissions("admin:updateVersion:shellCommand")
    @RequiresPermissionsDesc(menu={"维护管理" , "版本更新"}, button="执行命令")
    @GetMapping("/shellCommand")
    public Object shellCommand(@RequestParam(required = false) List<String> commands) {
        try {
            String[] arrCommands=commands.toArray(new String[commands.size()]);
            String strResult="";
            if(File.separator.equals("/")){
                strResult=ProcessLinuxShellUtil.execCommand(arrCommands[0].replace("$>",""));
            } else
            {
                 strResult= ProcessWindowsBatUtil.runCMD(commands.get(0).replace("$>",""));
//                ProcessWindowsBatUtil.Result result=   ProcessWindowsBatUtil.buildProcessRunner().runCMD(commands);
//                strResult=result.toString();
            }


//            if(strResult.startsWith("错误")){
//                return ResponseUtil.fail(502,strResult);
//            } else {
                return ResponseUtil.ok(strResult);
//            }
        } catch (Exception ex){
            System.out.print(ex.getMessage());
            return ResponseUtil.fail(502,"执行错误:"+ex.getMessage());
        }
    }

}
