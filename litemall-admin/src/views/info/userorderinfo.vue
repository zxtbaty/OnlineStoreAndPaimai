<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select clearable  class="filter-item"  v-model="listQuery.statusCode" style="width:150px;"   placeholder="请选择订单状态">
        <el-option  v-for=" item in statusIdList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>

      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 120px;" placeholder="订单编号"/>
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 120px;" placeholder="用户名称"/>
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 120px;" placeholder="消息内容"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.startDate"
                      type="datetime"
                      placeholder="大等于起始日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.endDate"
                      type="datetime"
                      placeholder="小等于结束日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-button v-permission="['GET /admin/userinfodef/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>

    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="订单编号" prop="orderSn"/>
      <el-table-column align="center" label="状态编码" prop="statusCode"/>
      <el-table-column align="center" label="状态名称" prop="statusName"/>
      <el-table-column align="center" label="会员名称" prop="username"/>
      <el-table-column align="center" label="发生时间" prop="happenDate"/>
      <el-table-column align="center" label="消息内容" prop="content"/>
      <el-table-column align="center" label="站内提醒" prop="webHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.webHint ? 'success' : 'error' ">{{ scope.row.webHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="短信标志" prop="smsHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.smsHint ? 'success' : 'error' ">{{ scope.row.smsHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="邮件标志" prop="mailHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.mailHint ? 'success' : 'error' ">{{ scope.row.mailHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="通知客户" prop="ifSendUser">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifSendUser ? 'success' : 'error' ">{{ scope.row.ifSendUser ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="查看标志" prop="ifViewed">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifViewed ? 'success' : 'error' ">{{ scope.row.ifViewed ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/userinfodef/read']" type="primary" size="mini" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="'查看消息详情'" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :model="dataForm"
        status-icon

        label-width="100px"
        >
        <el-col :span="12"  >
          <el-form-item label="订单编号" prop="orderSn">
            <el-input v-model="dataForm.orderSn"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态编码" prop="statusCode">
            <el-input v-model="dataForm.statusCode"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="状态名称" prop="statusName">
            <el-input v-model="dataForm.statusName"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="会员名称" prop="username">
            <el-input type="textarea"
                      :rows="5" v-model="dataForm.username"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发生时间" prop="happenDate">
            <el-input type="textarea"
                      :rows="5" v-model="dataForm.happenDate"/>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="消息内容" prop="content">
            <el-input  v-model="dataForm.content"/>
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
          <el-form-item label="通知客户" prop="ifSendUser">
            <el-tag :type="dataForm.ifSendUser ? 'success' : 'error' ">{{ dataForm.ifSendUser ? '是' : '否' }}</el-tag>
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
  import { listUserOrderinfo,readUserOrderinfo  } from '@/api/userorderinfo'
  import { listDiccode } from '@/api/diccode'
  import { uploadPath } from '@/api/storage'
  import { getToken } from '@/utils/auth'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

  export default {
    name: 'UserOrderInfo',
    components: { Pagination },
    data() {
      return {
        uploadPath,
        statusIdList:[],
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20,
          statusCode:undefined,
          orderSn:undefined,
          username:undefined,
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
      this.getStatusIdList()
      this.getList()
    },

    methods: {
      getStatusIdList(){
        listDiccode({dicmainName:"订单状态"}).then(response => {
          this.statusIdList = response.data.data.list
        }).catch(() => {
          this.statusIdList = []
        })
      },
      getList() {
        this.listLoading = true
        listUserOrderinfo(this.listQuery)
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
        readUserOrderinfo(this.dataForm.id).then(
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
