#！/bin/sh
export LANG=en_US.UTF-8
time=`date "+%Y%m%d%H"`
nowtime=`date "+%Y%m%d%H%M"`

##升级前对进程的关闭，和对缓存的清理
echo "======开始关闭进程======="
ps -ef | grep java | grep jboss-4.0.5.GA | awk '{print $2}'|xargs kill -9
echo "====删除进程成功===="
echo "====开始清理日志===="
cd /opt
rm -rf nohup.out
rm -rf litemall.jar
echo "====清理日志成功===="

echo "======开始下载文件======="
#$1参数是从上传的文件附件中传过来的上传文件网址
wget -c  -O /opt/litemall.jar  $1

#echo "====开始清理缓存===="
#rm -rf /opt/jboss-4.0.5.GA/server/default/tmp/*
#rm -rf /opt/jboss-4.0.5.GA/server/default/work/*
#echo "====清理缓存成功===="


##进行war包的备份
#echo "======开始备份版本======="
#cp /opt/jboss-4.0.5.GA/server/default/deploy/csms.war  /opt/back/bak_${time}_test.war
#if [ -f /opt/back/bak_${time}_test.war ]
#then
#echo "===版本包备份成功==="
#rm /opt/jboss-4.0.5.GA/server/default/deploy/test.war
#else
#echo "===版本包备份失败， 退出升级==="
#exit 1
#fi

#获取war包，上传war包
echo "====开始上传版本 ====="
cd /opt/jboss-4.0.5.GA/server/default/deploy/
lftp -u  sftp_test,ftp_password sftp://172.18.102.11 <<EOF
cd /migudata/deploy/test
get test.war
bye
EOF
if [ -f /opt/jboss-4.0.5.GA/server/default/deploy/test.war ]
then
echo "====上传版本成功===="
else
echo "====上传版本失败 ,退出升级 ===="
exit 1
fi

#获取需要修改的配置文件
echo "====开始获取和替换相关配置文件====="
cd /opt/back/temp/csms
lftp -u  sftp_csms,ZSE\$cft6999 sftp://172.18.214.24 <<EOF
cd /migudata/sftp_csms/deploy/csms
get BatchPageGenerateTemplate.xls
get cache.properties
get system-config.xml
.......
bye
EOF



#获取配置文件，备份需要替换的文件，替换配置文件
echo "====开始备份，上传， 替换 配置文件信息===="
list=`ls /opt/back/temp/csms `
if [ -z $list ]
then
echo "===本次升级，没有需要替换的配置文件==="
else
for sourcepath in $list
do
  cd /opt/back/temp/csms
  changefile=`find /opt/jboss-4.0.5.GA/bin/csmsconfig -name $sourcepath `
  echo -e "====开始备份 ${sourcepath}===="
  cp $changefile /opt/back/back_csms__${time}_${sourcepath}
  if [ -f /opt/back/back_csms__${time}_${sourcepath} ]
  then
  echo "====备份文件${sourcepath}成功====="
  else
  echo "====备份文件${sourcepath}失败====="
  exit 1
  fi
  echo "====开始替换${changefile}====="
  mv /opt/back/temp/csms/${sourcepath} $changefile
  echo "====替换${changefile}成功===="  
done
fi

#开始启动进程
echo "====开始版本升级===="
cd /opt/jboss-4.0.5.GA/bin
sh start.sh
echo "====后台进程正在启动中===="
sleep 25s
ps -ef |grep jboss-4.0.5.GA | grep -v grep
if [ $? -eq 0 ]
then
echo "===启动成功==="
else
echo "===进程启动失败==="
exit 1
fi
