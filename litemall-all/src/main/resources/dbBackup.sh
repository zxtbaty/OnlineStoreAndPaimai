#!/bin/sh

# 备份数据库

# Mysql 用户名密码
MYSQL_USER=root
MYSQL_PASS=Citic12345
BACKUP_DIR=/opt/dbbackup

# 当前日期
#DATE=$(date -d '+0 days' +%Y%m%d%H%M)
#当前分钟
DATE=$(date –date='1 minutes ago' +%Y%m%d%H%M%S)


# 备份数据库
if [ ! -d ${BACKUP_DIR} ]; then
    mkdir -p ${BACKUP_DIR}
fi
#SQL_FILE="liteamll_"$DATE".sql"
/usr/bin/mysqldump -u$MYSQL_USER -p$MYSQL_PASS $DBNAME > $BACKUP_DIR/$1




