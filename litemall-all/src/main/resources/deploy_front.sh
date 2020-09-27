#！/bin/sh
export LANG=en_US.UTF-8
echo "======开始下载文件======="
#$1参数是从上传的文件附件中传过来的上传文件网址
cp -r /opt/storage/$1 /opt/qianduanH5.tar.gz
echo "====下载文件成功====开始解压===="
mkdir /opt/quanduanH5 && tar -zxvf /opt/qianduanH5.tar.gz -C /opt/quanduanH5 --strip-components 1
echo "=====解压完成=====移动改名开始===="
mv /opt/quanduanH5 /opt/ACT
echo "====改名结束======="
