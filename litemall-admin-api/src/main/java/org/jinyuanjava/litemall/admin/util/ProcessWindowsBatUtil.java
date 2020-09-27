package org.jinyuanjava.litemall.admin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ProcessWindowsBatUtil
{

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4);

    public static ProcessRunner buildProcessRunner()
    {
        return new ProcessRunner();
    }

    public static class ProcessRunner
    {
        private int maxErrorLineNumb = 100;

        private int maxInputLineNumb = 500;

        /**
         * 调用核心的命令
         *
         * [@param](https://my.oschina.net/u/2303379) cmd
         *            命令
         * [@param](https://my.oschina.net/u/2303379) cmdInputParas
         *            执行命令需要输入的参数，比如命令行登录数据库需要输入密码<br>
         *            echo "password" | cmd
         * [@return](https://my.oschina.net/u/556800) 返回值，包括错误回显，正确回显，整个脚本执行的返回码
         *
         */
        public Result runCMD(List<String> cmd, List<String> cmdInputParas)
        {
            Process process = null;
            BufferedWriter bw = null;
            try
            {
                ProcessBuilder processBuilder = new ProcessBuilder(cmd);
                process = processBuilder.start();
                bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                for (String p : cmdInputParas)
                {
                    bw.write(p);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                Callable<List<String>> inputRunner = new Runner(maxErrorLineNumb, process.getInputStream());
                FutureTask<List<String>> inputTask = new FutureTask<List<String>>(inputRunner);
                EXECUTORS.execute(inputTask);
                Callable<List<String>> errorRunner = new Runner(maxInputLineNumb, process.getErrorStream());
                FutureTask<List<String>> errorTask = new FutureTask<List<String>>(errorRunner);
                EXECUTORS.execute(errorTask);
                List<String> inputResult = inputTask.get();
                List<String> errorResult = errorTask.get();
                int returnCode = process.waitFor();
                return new Result(inputResult, errorResult, returnCode);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (bw != null)
                    {
                        bw.close();
                    }
                }
                catch (IOException e)
                {

                }
                if (process != null)
                {
                    process.destroy();
                }

            }
            return new Result();
        }

        /**
         * 调用核心的命令
         *
         * [@param](https://my.oschina.net/u/2303379) cmd
         *            命令
         *
         * [@return](https://my.oschina.net/u/556800) 返回值，包括错误回显，正确回显，整个脚本执行的返回码
         */
        public Result runCMD(List<String> cmd)
        {
            return runCMD(cmd, Arrays.asList());
        }

    }

    /**
     * 执行命令类
     *
     */
    private static class Runner implements Callable<List<String>>
    {

        private int maxLineNumb;

        private InputStream inputStream;

        public Runner(int maxLineNumb, InputStream inputStream)
        {
            super();
            this.maxLineNumb = maxLineNumb;
            this.inputStream = inputStream;
        }

        @Override
        public List<String> call()
        {
            List<String> result = new ArrayList<String>();
            BufferedReader br = null;
            try
            {
                br = new BufferedReader(new InputStreamReader(inputStream));
                int i = 0;
                String line = null;
                while (null != (line = br.readLine()) && (i++ < maxLineNumb))
                {
                    result.add(line);
                }
                return result;
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (br != null)
                    {
                        br.close();
                    }
                }
                catch (IOException e)
                {

                }
            }
            return result;
        }

    }

    /**
     * 结果
     *
     */
    public static class Result
    {
        // 回显
        private List<String> inputInfos;
        // 错误回显
        private List<String> errorInfos;
        // 返回码
        private int returnCode = -1;

        public Result(List<String> inputInfos, List<String> errorInfos, int returnCode)
        {
            this.inputInfos = inputInfos;
            this.errorInfos = errorInfos;
            this.returnCode = returnCode;
        }

        public Result()
        {

        }

        @Override
        public String toString()
        {
            return "Result [inputInfos=" + inputInfos + ", errorInfos=" + errorInfos + ", returnCode=" + returnCode + "]";
        }




    }



    /**
     * 执行特定的Shell文件
     * @param filePath
     * @return
     */
    public static String execBat(String filePath){
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(filePath);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in,"GB2312"));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            System.out.println("sb:" + sb.toString());
            System.out.println("callCmd execute finished");
            //执行结果
            String result = sb.toString();
            return result;
        } catch (IOException e) {
            System.out.println(e);
            return "错误:"+e.getMessage();
        }
    }


    public static String runCMD(String cmd) throws IOException, InterruptedException {
        final String METHOD_NAME = "runCMD";

        // Process p = Runtime.getRuntime().exec("cmd.exe /C " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader br = null;
        try {
            // br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
            String readLine = br.readLine();
            StringBuilder builder = new StringBuilder();
            while (readLine != null) {
//                readLine = new String(readLine.toString().getBytes("ISO-8859-1"),"UTF-8");
                builder.append(readLine+ "\n");
                readLine = br.readLine();
            }
//            logger.debug(METHOD_NAME + "#readLine: " + builder.toString());

            p.waitFor();
            int i = p.exitValue();
//            logger.info(METHOD_NAME + "#exitValue = " + i);
            if (i == 0) {
//                return true;
                return builder.toString();
            } else {
//                return false;
                return "错误:"+builder.toString();
            }


        } catch (IOException e) {
//            logger.error(METHOD_NAME + "#ErrMsg=" + e.getMessage());
            e.printStackTrace();
            return e.getMessage();
//            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }



}
