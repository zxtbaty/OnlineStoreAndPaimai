<template>
  <div class="app-container">

    <el-form  status-icon  label-width="100px" >
      <el-col :span="24">
        <el-form-item label="执行语句" >
        <el-input type="textarea" v-model="listQuery.strSql"
                  :rows="8" style="width:900px" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="驼峰命名" >
          <el-radio-group label="是否驼峰命名" v-model="listQuery.ifCamel">
            <el-radio :label="false">否</el-radio>
            <el-radio :label="true">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>


    </el-form>
    <!-- 查询和其他操作 -->

    <el-col :span="24" style="display: flex;justify-content: center">

      <el-button :loading="execLoading" class="filter-item" type="primary" icon="el-icon-search" @click="handleExecSql">执行更新/创建语句</el-button>
      <el-button :loading="selectPageLoading" class="filter-item" type="primary" icon="el-icon-search" @click="handleSelectPageSql">查询分页</el-button>
      <el-button :loading="insertLoading" class="filter-item" type="primary" icon="el-icon-search" @click="handleInsertSql">执行插入语句</el-button>
      <el-button :loading="selectLoading"  class="filter-item" type="primary" icon="el-icon-search" @click="handleSelectSql">查询不分页</el-button>
      <el-button :loading="excelDownloadLoading_All" class="filter-item" type="primary" icon="el-icon-edit" @click="handleExportData">导出执行结果</el-button>
    </el-col>
    <!-- 查询结果 -->
    <el-table :data="list" v-if="list.length>0" @selection-change="changeSelIdList">
      <el-table-column type="selection"/>
      <el-table-column align="center" show-overflow-tooltip  v-for="(item,index) in listHeader"
                       :width="item.columnWidth" :formatter="dateForma"
                       :prop="item" :label="item">
<!--      <el-table-column v-for="(item, index) in list[0]" :key="Date.now()">-->
<!--        <template slot="header" slot-scope="scope">-->
<!--          {{item.name}}-->
<!--        </template>-->
<!--        <template slot-scope="scope">{{scope.row[index].scores}}</template>-->
<!--      </el-table-column>-->
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="handleSelectPageSql" />

  </div>
</template>

<script>
import { execSql,insertSql,selectSql,selectPageSql } from '@/api/commonDB'

import Pagination from '@/components/Pagination'
import moment from 'moment'

export default {
  name: 'SqlExecute',
  components: { Pagination },
  data() {
    return {
      execLoading:false,
      insertLoading:false,
      selectLoading:false,
      selectPageLoading:false,
      excelDownloadLoading_All:false,
      list: [],
      checkBoxSelIdList:[],
      total: 0,
      listLoading: false,

      listQuery: {
        page: 1,
        limit: 10,
        strSql:'',
        ifCamel:false,
        ids:[],

      },
    }
  },
  created() {

  },
  methods: {
    changeSelIdList(val){
      this.checkBoxSelIdList = val
    },
    handleExecSql(){
      // if(this.$store.getters.name!='AdminSys'){
      //   this.$message.error("您没有执行权限");
      //   return;
      // }

      if(this.listQuery.strSql==""){
          this.$message.error("请输入要执行的语句");
          return;
      }

      if(this.listQuery.strSql.toLowerCase().indexOf("create")>=0){
        if(confirm("您正在执行创建语句,会覆盖原表、视图、存储过程，确认要执行吗？")==false)
        {
          return;
        }
      }

      if(this.listQuery.strSql.toLowerCase().indexOf("drop")>=0){
        if(confirm("您正在执行删除语句,会删除原表、视图、存储过程，确认要执行吗？")==false)
        {
          return;
        }
      }

      if(this.listQuery.strSql.toLowerCase().indexOf("update")>=0){
        if(confirm("您正在执行更新语句,会更新数据，确认要执行吗？")==false)
        {
          return;
        }
      }

      this.list=[];
      this.listHeader=[];
      this.total=0;
      this.execLoading=true;
      execSql({
        strSql:this.listQuery.strSql
      }).then((res)=>{
        this.execLoading=false;
        let result=res.data.data;
        if(result==null||result==""){
            this.$message.success("执行成功");
        } else
        {
          this.$message.error(result);
        }
      }).catch(error=>{
        this.execLoading=false;
        this.$message.error(error);
      });
    },
    handleSelectPageSql(){

      if(this.listQuery.strSql==""){
        this.$message.error("请输入要执行的语句");
        return;
      }
      if(this.listQuery.strSql.toLowerCase().startsWith("select",0)==false){
        this.$message.error("此语句不是查询语句");
        return;
      }
      this.selectPageLoading=true;
      selectPageSql({
        strSql:this.listQuery.strSql,
        ifCamel:this.listQuery.ifCamel,
        page:this.listQuery.page,
        limit:this.listQuery.limit}
        ).then((res)=>{

        let result=res.data.data;
        if(result.list==null&&result.substr(0,2)=="错误"){
          this.list=[];
          this.total=0;
          this.listLoading=false;
          this.$message.error(result);
        } else
        {
          this.list=result.list;
          this.getListHeader();
          this.total=result.total;
          this.listLoading=false;

        }
        this.selectPageLoading=false;
      }).catch(error=>{
        this.selectPageLoading=false;
        this.$message.error(error);
      });
    },

    handleSelectSql(){
      // if(this.$store.getters.name!='AdminSys'){
      //   this.$message.error("您没有执行权限");
      //   return;
      // }
      if(this.listQuery.strSql==""){
        this.$message.error("请输入要执行的语句");
        return;
      }
      if(this.listQuery.strSql.toLowerCase().startsWith("select",0)==false){
        this.$message.error("此语句不是查询语句");
        return;
      }
      this.selectLoading=true;
      selectSql({
        strSql:this.listQuery.strSql,
        ifCamel:this.listQuery.ifCamel}
        ).then((res)=>{
        let result=res.data.data;
        this.selectLoading=false;
        if(result.list==null&& result.substr(0,2)=="错误"){
          this.list=[];
          this.total=0;
          this.selectLoading=false;
          this.$message.error(result);
        } else
        {

          this.list=result.list;
          this.getListHeader();
          this.total=result.total;
          this.selectLoading=false;

        }
      }).catch(error=>{
        this.$message.error(error);
      });
    },


    handleInsertSql(){
      // if(this.$store.getters.name!='AdminSys'){
      //   this.$message.error("您没有执行权限");
      //   return;
      // }
      if(this.listQuery.strSql==""){
        this.$message.error("请输入要执行的语句");
        return;
      }
      if(this.listQuery.strSql.toLowerCase().startsWith("insert",0)==false){
        this.$message.error("此语句不是插入语句");
        return;
      }
      this.list=[];
      this.total=0;
      this.insertLoading=true;
      insertSql({
        strSql:this.listQuery.strSql}
        ).then((res)=>{
        let result=res.data.data;
        this.insertLoading=false;
        if(result==0){
          this.$message.error("插入语句执行失败，没有返回主键id");
        } else
        {
          this.listLoading=false;
          this.$message.success("执行成功,返回id值【"+result+"】");
        }
      }).catch(error=>{
        this.insertLoading=false;
        this.$message.error(error);
      });
    },

    dateForma:function(row,column){
      var data = row[column.property];
      if(data === undefined||data==='')
      {return ''};

      //判断日期 布尔值 如果不是以上，则直接返回原值
      if(isNaN(data)&&!isNaN(Date.parse(data))){
        return moment(data).format("YYYY-MM-DD HH:mm:ss")
      }
      else if(data===true){
        return '1'
      } else if(data===false){
        return '0'
      }
      else
      {
        return data;
      }
    },
    handleExportData(){
      // if(this.list.length==0){
      //   this.$message.warning("没有要导出的数据，请先查询");
      //   return;
      // }
      var ids=[];
      for (let selItem of  this.checkBoxSelIdList) {
        ids.push(selItem.id)
      }
      if(ids.length>0){
        if(this.listQuery.strSql.toLowerCase().indexOf("where")<0){
          this.$message.error("勾选结果必须要在语句中写where条件语句,可以写入where 1=1")
          return;
        }
      }
      //查询结果
      this.excelDownloadLoading_All=true;
      selectSql({
        strSql:this.listQuery.strSql,
        ifCamel:this.listQuery.ifCamel,
        ids:ids}
        ).then((res)=>{
        let result=res.data.data;
        if(result.list==null&&result.substr(0,2)=="错误"){
          this.list=[];
          this.total=0;
          this.excelDownloadLoading_All=false;
          this.$message.error(result);
        } else
        {
          this.list=result.list;
          this.getListHeader();
          this.total=result.total;
          import('@/vendor/Export2Excel').then(excel => {
            //获取第一列的值与字段
            excel.export_json_to_excel2(this.listHeader, this.list, this.listHeader, '数据信息')
            this.excelDownloadLoading_All = false

          })
        }
      }).catch(error=>{
        this.$message.error(error);
      });
    },
    getListHeader(){
      this.listHeader=[];
      for(let item in this.list[0]){
        this.listHeader.push(item)
        // console.log(item)
      }

      // console.log(this.listHeader)
    },
  }
}
</script>
