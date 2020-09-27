<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input class="filter-item" v-model="listQuery.logFilePath"
                style="width:300px" placeholder="请输入文件路径" />

      <el-input class="filter-item"  v-model="listQuery.logFileRows" placeholder="请输入显示行数"
               style="width:150px" />
<!--      <el-date-picker class="filter-item" style="width: 200px"-->
<!--                      v-model="listQuery.startDate"-->
<!--                      type="datetime"-->
<!--                      placeholder="大等于起始日期"-->
<!--                      value-format="yyyy-MM-dd HH:mm:ss"/>-->
<!--      <el-date-picker class="filter-item" style="width: 200px"-->
<!--                      v-model="listQuery.endDate"-->
<!--                      type="datetime"-->
<!--                      placeholder="大等于起始日期"-->
<!--                      value-format="yyyy-MM-dd HH:mm:ss"/>-->
      <el-button  class="filter-item" type="primary"   @click="handleSaveSeting">保存配置</el-button>
      <el-button  :loading="queryLoading"  class="filter-item" type="primary"  @click="handleGetLog">查询日志</el-button>
      <el-button  :loading="downLoading" class="filter-item" type="primary"  @click="handleDownLoadLog">下载日志</el-button>
      <el-button  :loading="clearLogLoading" class="filter-item" type="primary"  @click="handleClearLog">清理日志</el-button>
    </div>

    <!-- 查询结果 -->
    <div>

        <el-input type="textarea" v-model="logData"
                  :rows="20" style="width:900px"  />

    </div>

  </div>
</template>

<script>
import { getLog,downLoadLog,getLogSetting,saveLogSetting,clearLog } from '@/api/commonLog'
import requestblob from '@/utils/requestblob'
import Pagination from '@/components/Pagination'
import moment from 'moment'

export default {
  name: 'SqlExecute',
  components: { Pagination },
  data() {
    return {
      logData: undefined,
      total: 0,
      listLoading: false,
      downLoading:false,
      queryLoading:false,
      clearLogLoading:false,
      listQuery: {
        logFilePath:"",
        logFileRows:2000,
        startDate:undefined,
        endDate:undefined,
      },
    }
  },
  created() {
    this.handleGetLogFilePath();
    this.handleGetLogFileRows();
  },
  methods: {
    handleGetLogFilePath(){
      getLogSetting({strKey:"logFilePath"}).then(
        (res)=>{
          this.listQuery.logFilePath=res.data.data;
        }
      );
    },
    handleGetLogFileRows(){
      getLogSetting({strKey:"logFileRows"}).then(
        (res)=>{
          this.listQuery.logFileRows=res.data.data;
        }
      );
    },
    handleSaveSeting(){
       //保存logFilePath
      saveLogSetting({strKey:"logFilePath",strValue:this.listQuery.logFilePath}).then(
        (resLogFilePath)=>{
            var result=resLogFilePath.data.data;
            if(result){
              saveLogSetting({strKey:"logFileRows",strValue:this.listQuery.logFileRows}).then(
                (resLogFileRows)=>{
                   var booResult=resLogFileRows.data.data;
                   if(booResult){
                      this.$message.success("保存成功")
                   } else
                   {
                     this.$message.error("保存【日志文件显示行数】失败")
                   }
                });
            } else{
              this.$message.error("保存【日志文件路径】失败")
            }
        }
      );
    },
    handleGetLog(){
      if(this.listQuery.logFileRows==null){
        this.$message.error("请输入要返回的行数");
        return;
      };
      getLog(this.listQuery.logFilePath,this.listQuery.startDate,this.listQuery.endDate,this.listQuery.logFileRows).then((res)=>{
        this.logData=res.data.data;

      }).catch(error=>{
        this.$message.error(error);
      });
    },
    handleClearLog(){
      this.clearLogLoading=true;
      clearLog().then((res)=>{
        this.logData= res.data.data;
        this.clearLogLoading=false;
        if(this.logData.toString().startsWith('错误')){
          this.$message.success("清理失败，查看反馈信息")
        } else
        {
          this.$message.success("清理成功")
        }

      }).catch(error=>{
        this.logData= res.data.data;
        this.clearLogLoading=false;
        this.$message.error("清理失败")
      });
    },
    handleDownLoadLog(){
      this.downLoading=true;
      requestblob({
        url: '/commonLog/downLoadLog',
        method: 'get',
        params: {path:this.listQuery.logFilePath},
        responseType: 'blob'
      }).then(res=> {
        console.log(res)
        if(res.data.size==0){
          this.downLoading=false;
          this.$message.warning("没有要导出的数据");
          return;
        }
        var blob = res.data;
        //此处不要加扩展名称，如果加上就会报错
        var fileName="日志文件.out";
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
          this.downLoading = false
        } else {
          var blobURL = window.URL.createObjectURL(blob);
          var tempLink = document.createElement('a');
          tempLink.style.display = 'none';
          tempLink.href = blobURL;
          tempLink.setAttribute('download', fileName);
          if (typeof tempLink.download === 'undefined') {
            tempLink.setAttribute('target', '_blank');
          }
          document.body.appendChild(tempLink);
          tempLink.click();
          document.body.removeChild(tempLink);
          window.URL.revokeObjectURL(blobURL);
          this.downLoading=false;
        }
      }).catch(
        error=>{
          console.log(error);
          this.downLoading = false
        }
      );
    },
  }
}
</script>
