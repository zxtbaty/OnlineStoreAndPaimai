<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-input v-model="listQuery.fileName" clearable class="filter-item" style="width: 150px;" placeholder="文件名称"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.updateBeginDate"
                      type="date"
                      placeholder="备份起始日期"
                      value-format="yyyy-MM-dd"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.updateEndDate"
                      type="date"
                      placeholder="备份截至日期"
                      value-format="yyyy-MM-dd"/>

      <el-button  class="filter-item" type="primary"  @click="handleFilter">查询</el-button>
      <el-button :loading="execLoading"  class="filter-item" type="primary"  @click="handleFilter">执行备份</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading"
              :data="list" element-loading-text="正在查询中。。。"
              border fit highlight-current-row >

      <el-table-column align="center" label="操作" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button :loading="downLoading" type="primary" size="mini"
                     title="下载" @click="handleDownLoad(scope.row)">下载</el-button>

        </template>
      </el-table-column>
      <el-table-column align="center" min-width="300" label="文件名称" prop="fileName"/>
      <el-table-column align="center" min-width="100" label="备份日期" prop="backupDate"/>

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


  </div>
</template>

<script>
  import { listBackDb,createBackDb,deleteBackDb,backupBackDb,downLoadDb } from '@/api/backupDatabase'
  import requestblob from '@/utils/requestblob'
  import Pagination from '@/components/Pagination'

  export default {
    name: 'dbBackup',
    components: { Pagination },
    data() {
      return {
        list:[],
        total: 0,
        listLoading: false,
        downLoading:false,
        execLoading:false,

        listQuery: {
          fileName:"",
          backupBeginDate:undefined,
          backupEndDate:undefined,
          page:1,
          limit:20
        },
        dataForm:{
          id:undefined,
          fileName:undefined,
          backupDate:undefined,
          backupVersion:undefined,
          backupPath:undefined,
        },
      }
    },
    created() {
      this.getList();
    },
    methods: {

      getList(){
        this.listLoading=true;
        listBackDb(this.listQuery).then(
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

      handleDownLoad(row){
        this.downLoading=true;
        requestblob({
          url: '/backupDb/downLoad',
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
          var fileName="litemallDB_"+(new Date())+".jar";
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
