<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select clearable  class="filter-item"  v-model="listQuery.typeCode" style="width:150px;"   placeholder="请选择类型名称">
        <el-option  v-for=" item in typeIdList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>
      <el-select clearable  class="filter-item"  v-model="listQuery.sourceCode" style="width:150px;"   placeholder="请选择来源名称">
        <el-option  v-for=" item in sourceIdList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>
      <el-input v-model="listQuery.opadminName" clearable class="filter-item" style="width: 150px;" placeholder="接收者"/>
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 150px;" placeholder="消息主题"/>
      <el-input v-model="listQuery.content" clearable class="filter-item" style="width: 150px;" placeholder="消息内容"/>
<!--      <el-date-picker class="filter-item" style="width: 150px"-->
<!--                      v-model="listQuery.startDate"-->
<!--                      type="datetime"-->
<!--                      placeholder="大等于起始日期"-->
<!--                      value-format="yyyy-MM-dd HH:mm:ss"/>-->
<!--      <el-date-picker class="filter-item" style="width: 150px"-->
<!--                      v-model="listQuery.endDate"-->
<!--                      type="datetime"-->
<!--                      placeholder="小等于结束日期"-->
<!--                      value-format="yyyy-MM-dd HH:mm:ss"/>-->
      <el-button v-permission="['GET /admin/opadmininfo/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>

    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="通知人员" prop="opadminName"/>

      <el-table-column align="center" label="消息类型" prop="typeName"/>

      <el-table-column align="center" label="消息来源" prop="sourceName"/>

      <el-table-column align="center" label="消息主题" prop="title"/>
      <el-table-column align="center"  width="500px"   label="消息内容" prop="content"/>
      <el-table-column align="center" label="创建时间" prop="addTime"/>-->
<!--      <el-table-column align="center" label="站内提醒" prop="webHint">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.webHint ? 'success' : 'error' ">{{ scope.row.webHint ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column align="center" label="短信标志" prop="smsHint">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.smsHint ? 'success' : 'error' ">{{ scope.row.smsHint ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column align="center" label="邮件标志" prop="mailHint">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.mailHint ? 'success' : 'error' ">{{ scope.row.mailHint ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column align="center" label="弹窗提醒" prop="popHint">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.popHint ? 'success' : 'error' ">{{ scope.row.popHint ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--
<!--      <el-table-column align="center" label="停显标志" prop="displayFlag">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.displayFlag ? 'success' : 'error' ">{{ scope.row.displayFlag ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column align="center" label="查看标志" prop="ifViewed">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.ifViewed ? 'success' : 'error' ">{{ scope.row.ifViewed ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button v-permission="['POST /admin/opadmininfo/read']" type="primary" size="mini" @click="handleView(scope.row)">查看</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="'查看后端消息详情'" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :model="dataForm"
        status-icon

        label-width="100px"
        >
        <el-col :span="12"  >
          <el-form-item label="消息类型" prop="typeName">
            <el-input v-model="dataForm.typeName"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="消息来源" prop="sourceName">
            <el-input v-model="dataForm.sourceName"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="消息主题" prop="title">
            <el-input v-model="dataForm.title"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="消息内容" prop="content">
            <el-input type="textarea"
                      :rows="5" v-model="dataForm.content"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="站内提醒" prop="webHint">
            <el-tag :type="dataForm.webHint ? 'success' : 'error' ">{{ dataForm.webHint ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="短信标志" prop="smsHint">
            <el-tag :type="dataForm.smsHint ? 'success' : 'error' ">{{dataForm.smsHint ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12"  >
          <el-form-item label="邮件标志" prop="mailHint">
            <el-tag :type="dataForm.mailHint ? 'success' : 'error' ">{{ dataForm.mailHint ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12"  >
          <el-form-item label="弹窗提醒" prop="mailHint">
            <el-tag :type="dataForm.popHint ? 'success' : 'error' ">{{ dataForm.popHint ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12"  >
          <el-form-item label="停显标志" prop="mailHint">
            <el-tag :type="dataForm.displayFlag ? 'success' : 'error' ">{{ dataForm.displayFlag ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12"  >
          <el-form-item label="查看标志" prop="mailHint">
            <el-tag :type="dataForm.ifViewed ? 'success' : 'error' ">{{ dataForm.ifViewed ? '是' : '否' }}</el-tag>
          </el-form-item>
        </el-col>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">关闭</el-button>

      </div>
    </el-dialog>

  </div>
</template>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
  }
  .avatar {
    width: 145px;
    height: 145px;
    display: block;
  }
</style>

<script>
  import { listOpadmininfo,readOpadmininfo  } from '@/api/opadmininfo'
  import { listDiccode } from '@/api/diccode'
  import { uploadPath } from '@/api/storage'
  import { getToken } from '@/utils/auth'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

  export default {
    name: 'opadmininfo',
    components: { Pagination },
    data() {
      return {
        uploadPath,
        sourceIdList:[],
        typeIdList:[],
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20,
          opadminName:undefined,
          typeCode:undefined,
          sourceCode:undefined,
          title:undefined,
          content:undefined,
          startDate:undefined,
          endDate:undefined,
          sort: 'add_time desc',
        },
        dataForm:{},
        dialogFormVisible: false,
      }
    },
    computed: {
      headers() {
        return {
          'X-Litemall-Admin-Token': getToken()
        }
      }
    },
    created() {
      this.getTypeIdList()
      this.getSourceIdList()
      this.getList()
    },

    methods: {
      getTypeIdList(){
        listDiccode({dicmainName:"后端消息_类型"}).then(response => {
          this.typeIdList = response.data.data.list
        }).catch(() => {
          this.typeIdList = []
        })
      },
      getSourceIdList(){
        listDiccode({dicmainName:"后端消息_来源"}).then(response => {
          this.sourceIdList = response.data.data.list
        }).catch(() => {
          this.sourceIdList = []
        })
      },

      getList() {
        this.listLoading = true
        listOpadmininfo(this.listQuery)
          .then(response => {
            this.list = response.data.data.list
            this.total = response.data.data.total
            this.listLoading = false
          })
          .catch(() => {
            this.list = []
            this.total = 0
            this.listLoading = false
          })
      },
      handleFilter() {
        this.listQuery.page = 1
        this.getList()
      },

      handleView(row) {
        this.dataForm = Object.assign({}, row)
        readOpadmininfo(this.dataForm.id).then(
          (response)=>{
            this.dataForm=response.data.data;
          }
        )
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },

    }
  }
</script>
