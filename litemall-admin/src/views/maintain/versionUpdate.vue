<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-upload style="margin-bottom: 10px"
        class="upload-demo"
        ref="upload"
        :headers="headers"
        :action="uploadPath"
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :on-progress="uploadVideoProcess"
        :on-success="uploadVersionForm"
        :multiple="false"
        :file-list="fileList"
        accept=".jar,war"
        :auto-upload="false">
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
        <div slot="tip" class="el-upload__tip">只能上传.jar或者.war文件，且不超过1G</div>
      </el-upload>

      <el-input v-model="listQuery.fileName" clearable class="filter-item" style="width: 150px;" placeholder="文件名称"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.updateBeginDate"
                      type="date"
                      placeholder="更新起始日期"
                      value-format="yyyy-MM-dd"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.updateEndDate"
                      type="date"
                      placeholder="更新截至日期"
                      value-format="yyyy-MM-dd"/>
      <el-select v-model="listQuery.ifBushu" clearable="" style="width: 120px"
                 class="filter-item"  placeholder="已经部署">
        <el-option  :key="1" :label="'是'" :value="1"/>
        <el-option  :key="0" :label="'否'" :value="0"/>
      </el-select>
      <el-select v-model="listQuery.ifCurrent" clearable="" style="width: 120px"
                 class="filter-item"  placeholder="最新版本">
        <el-option  :key="1" :label="'是'" :value="1"/>
        <el-option  :key="0" :label="'否'" :value="0"/>
      </el-select>

      <el-button  class="filter-item" type="primary"  @click="handleFilter">查询</el-button>
      <el-button  class="filter-item" type="primary"  @click="handleShellCommand">命令行工具</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading"
              :data="list" element-loading-text="正在查询中。。。"
              border fit highlight-current-row >

      <el-table-column align="center" label="操作" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
         <el-button :loading="downLoading" type="primary" size="mini"
                     title="下载" @click="downLoadFile(scope.row)">下载</el-button>
         <el-button  :loading="execLoading" type="primary" size="mini"
                   title="发布" @click="handleExec(scope.row)">发布</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="200" label="网址" prop="fileName"/>
      <el-table-column align="center" label="文件名称" prop="sourceFileName"/>
      <el-table-column align="center" label="上传日期" prop="updateDate"/>
      <el-table-column align="center" label="文件版本" prop="fileVersion"/>

      <el-table-column align="center" label="是否部署" prop="ifBushu" >
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifBushu ? 'success' : 'error' ">{{ scope.row.ifBushu ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="是否在用" prop="ifCurrent">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifCurrent ? 'success' : 'error' ">{{ scope.row.ifCurrent ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


    <el-dialog :title="'发布版本'" :close-on-click-modal="false" :visible.sync="dialogFormVisible"
               :before-close="closeDialog">
      <div style="margin-bottom: 20px">
        Windows说明：
        1、cmd /c dir 是执行完dir命令后关闭命令窗口。

        2、cmd /k dir 是执行完dir命令后不关闭命令窗口。

        3、cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。

        4、cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
      </div>
      <!-- 查询结果 -->
      <div style="margin-bottom: 20px">
       <div>下载网址：{{dataForm.fileName}} &nbsp;&nbsp;&nbsp; 文件名称： {{dataForm.sourceFileName}} </div>

       <div style=" margin-bottom: 20px">上传日期:{{dataForm.updateDate}}&nbsp;&nbsp;&nbsp;当天版本:{{dataForm.fileVersion}}</div>

        <el-button  class="filter-item" type="primary"  v-if="dataForm.fileName!=undefined"  @click="handleExecShell">执行部署</el-button>
<!--        <el-button  class="filter-item" type="primary"   @click="handleExecShellCommand">执行Shell命令</el-button>-->
        <el-button  class="filter-item" type="primary"   @click="execLog='$>'">清空提示</el-button>
      </div>
      <div>

        <el-input ref="execFormText"  type="textarea" v-model="execLog" @keyup.enter.native="handleExecShellLastCommand"

                  :rows="17" style="width:900px"   />
      </div>

    </el-dialog>

  </div>
</template>

<script>
import { listVersion,createVersion,deleteVersion,execVersion,downLoadVersion,shellCommand } from '@/api/updateVersion'
import requestblob from '@/utils/requestblob'
import Pagination from '@/components/Pagination'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'

import moment from 'moment'
import {saveAs} from 'file-saver';
var FileSaver = require('file-saver');

export default {
  name: 'versionUpdate',
  components: { Pagination },
  data() {
    return {
      auto:true,
      uploadPath,
      execLog:"$>",//执行结果字符串
      videoFlag:false,
      videoUploadPercent:0,
      list:[],
      total: 0,
      listLoading: false,
      downLoading:false,
      execLoading:false,
      dialogFormVisible:false,
      fileList:[],
      listQuery: {
        versionType:'后台',
        sourceFileName:"",
        updateBeginDate:undefined,
        updateEndDate:undefined,
        ifBushu:undefined,
        ifCurrent:undefined,
        page:1,
        limit:20
      },
      dataForm:{
        id:undefined,
        versionType:'后台',
        fileName:undefined,
        sourceFileName:undefined,
        updateDate:undefined,
        fileVersion:undefined,
        filePath:undefined,
        ifBushu:0,
        ifCurrent:0
      },
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },



  updated(){
    console.log('aa')
    if(this.$refs.execFormText!=null){
      this.$refs.execFormText.focus();
    }

  },


  mounted() {

  },
  created() {
    this.getList();

  },
  methods: {
    closeDialog(){
      this.dialogFormVisible=false;
      this.getList();
    },
    uploadVersionForm(response){
      this.videoFlag = false;
      this.videoUploadPercent = 0;
      this.dataForm.fileName = response.data.url
      this.dataForm.sourceFileName= response.data.name
      this.dataForm.versionType="后台",
      createVersion(this.dataForm).then(
        (res)=>{
          this.dataForm=res.data.data;
          this.getList();
        }
      );
    },
    uploadVideoProcess(event, file, fileList) {
      this.videoFlag = true;
      this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    submitUpload() {
      this.$refs.upload.submit();

    },
    getList(){
      this.listLoading=true;
      listVersion(this.listQuery).then(
        (res)=>{
          this.list=res.data.data.list;
          this.total=res.data.data.total;
          this.listLoading=false
        }
      ).catch((error)=>{
        this.list=[];
        this.total=0;
        this.listLoading=false;
      });
    },
    handleFilter(){
      this.listQuery.page=1;
      this.getList();
    },
    handleExec(row){
      this.dataForm=row;
      this.dialogFormVisible=true;
    },

    handleShellCommand(){
      this.dataForm={
        id:undefined,
        versionType:'后台',
        fileName:undefined,
        sourceFileName:undefined,
        updateDate:undefined,
        fileVersion:undefined,
        filePath:undefined,
        ifBushu:0,
        ifCurrent:0
      },
      this.dialogFormVisible=true;
    },

    handleExecShell(){
      if(confirm("要用当前的文件替换正在运行的实例吗？")==false){
        return;
      }
      execVersion({id:this.dataForm.id}).then(
        (res)=>{
          this.execLog=res.data.data;
          this.$message.success("发布成功")
          this.execLoading=false
        }
      ).catch((error)=>{
        this.$message.error("发布失败:"+error)
        this.execLog=error.data.data;
        this.execLoading=false;
      });
    },

    // handleExecShellCommand() {
    //   if(this.execLog==null||this.execLog==undefined){
    //     this.$message.error("请输入要执行的Shell命令");
    //     return;
    //   }
    //   // this.execLog = this.execLog.replace(/[\r\n]/g,"");
    //   var commands=  this.execLog.toString().split(/\n/)
    //
    //   shellCommand({commands:commands}).then(
    //     (res)=>{
    //       this.execLog=res.data.data;
    //       this.$message.success("执行成功")
    //     }
    //   )
    // },

    handleExecShellLastCommand() {
      if(this.execLog==null||this.execLog==undefined){
        this.$message.error("请输入要执行的Shell命令");
        return;
      }

      // this.execLog = this.execLog.replace(/[\r\n]/g,"");
      var commands=  this.execLog.toString().split(/\n/)
      var lastCommand=[];
      for(var ii=commands.length-1;ii>=0;ii--)
      {
        if(commands[ii]==""){
          continue;
        } else
        {
          lastCommand.push(commands[ii])
          break;
        }
      }
      if(lastCommand[0]=="$>clear"){
        this.execLog="$>"
        return;
      }
      if(this.execLog.length<2){
        this.execLog+="$>";
        return;
      }
      if(!this.execLog.startsWith("$>")){
        this.execLog+="$>";
        return;
      }

      shellCommand({commands:lastCommand}).then(
        (res)=>{
          if(res.data.errno!=0){
            this.execLog= this.execLog+" \n "+res.data.errmsg+"\n";
            this.execLog+="$>";
          } else
          {
            this.execLog= this.execLog+" \n "+res.data.data+"\n";
            this.execLog+="$>";
          }


          this.$nextTick(() => {
            this.setCss();

          })
            // this.$message.success("执行成功")
        }
      ).catch(error=>{
        this.execLog+="$>";
        this.$nextTick(() => {
          this.setCss();

        })
      })
    },

     setCss(opt) {
      var srValue = this.$refs.execFormText;
      var sr = this.$refs.execFormText.$el.children[0];

      // sr.scrollTop = sr.scrollHeight;   // 内容滚动到最后一行
      sr.scrollTop= sr.scrollHeight;
       var len = srValue.value.length;
      this.setSelectionRange(sr, len, len); //将光标定位到文本最后
    },

     setSelectionRange(input, selectionStart, selectionEnd) {
      if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
      }
      else if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
      }
    },

    downLoadFile(row){
      FileSaver.saveAs(row.fileName, row.sourceFileName);
    },

    handleDownLoad(row){
      this.downLoading=true;
      requestblob({
        url: '/updateVersion/downLoad',
        method: 'get',
        params: {id:row.id},
        responseType: 'blob'
      }).then(res=> {
        console.log(res)
        if(res.data.size==0){
          this.$message.warning("没有要导出的数据");
          return;
        }
        var blob = res.data;
        //此处不要加扩展名称，如果加上就会报错
        var fileName="litemall.jar";
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

<style>
  .el-dialog__body{
    padding: 5px 30px;
  }
</style>
