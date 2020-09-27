package org.jinyuanjava.litemall.admin.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class ProcessLinuxShellUtil
{
    /**
     * 执行Shell脚本
     */
    /**
     * 解决了 参数中包含 空格和脚本没有执行权限的问题
     * @param scriptPath 脚本路径
     * @param para 参数数组
     */
    public static String execShell(String scriptPath, String ... para) {
        try {
            String[] cmd = new String[]{scriptPath};
            //为了解决参数中包含空格
            cmd= ArrayUtils.addAll(cmd,para);
            //解决脚本没有执行权限
            ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755",scriptPath);
            Process process = builder.start();
            process.waitFor();
            Process ps = Runtime.getRuntime().exec(cmd);
            ps.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            //执行结果
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "错误:"+e.getMessage();
        }
    }



    /**
     * 执行Shell命令
     * @param cmd
     */
    public static String execCommand(String cmd){
        try{
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd,null,null);
            InputStream stderr =  proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr,"GBK");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line).append("\n");
            }
            //执行结果
            String result = sb.toString();
            return result;
        }catch (Exception e){
//            e.printStackTrace();
            return "错误:"+e.getMessage();
        }
    }


    /**
     * 执行Shell命令
     * @param cmd
     */
    public static String execCommand(String[] cmd){
        try{
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd,null,null);
            InputStream stderr =  proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr,"GBK");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                line = new String(line.toString().getBytes("ISO-8859-1"),"UTF-8");
                System.out.println(line);
                sb.append(line).append("\n");
            }
            //执行结果
            String result = sb.toString();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return "错误:"+e.getMessage();
        }
    }



}
