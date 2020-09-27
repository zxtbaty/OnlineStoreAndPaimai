#！/bin/sh
export LANG=en_US.UTF-8
echo "======开始下载文件======="
#$1参数是从上传的文件附件中传过来的上传文件网址
cp /opt/storage/$1 /opt/litemallbak.jar
echo "====下载文件成功===="
##升级前对进程的关闭，和对缓存的清理
echo "======开始关闭进程======="
ps -ef | grep litemall | grep -v grep | awk '{print $2}' | xargs kill -9
echo "====删除进程成功========开始清理日志===="
cat /dev/null > /opt/nohup.out
echo "====清理日志成功========改名开始===="
mv /opt/litemallbak.jar /opt/litemall.jar
echo "====改名结束==========启动进程======="
nohup java -jar /opt/litemall.jar &

echo "======启动进程结束======="
