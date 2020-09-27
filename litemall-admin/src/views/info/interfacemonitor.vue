<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select clearable  class="filter-item"  v-model="listQuery.logSourceCode" style="width:120px;"   placeholder="来源类型">
        <el-option  v-for=" item in logSourceIdList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>
      <el-select clearable  class="filter-item"  v-model="listQuery.logDirectionCode" style="width:120px;"   placeholder="调用类型">
        <el-option  v-for=" item in logDirectionIdList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>
      <el-select clearable  class="filter-item"  v-model="listQuery.logTypeCode" style="width:120px;"   placeholder="日志类型">
        <el-option  v-for=" item in logTypeList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>
      <el-input v-model="listQuery.logContent" clearable class="filter-item" style="width: 120px;" placeholder="同步内容描述"/>
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
      <el-button v-permission="['GET /admin/interfacemonitor/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>

    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="来源类型" prop="logSource" />

      <el-table-column align="center" label="调用类型" prop="logDirection"/>

      <el-table-column align="center" label="日志类型" prop="logType"/>

      <el-table-column align="center" label="内容描述" prop="logContent"/>
      <el-table-column align="center" label="状态描述" prop="logStateDesc"/>
      <el-table-column align="center" label="起始时间" prop="logStartTime"/>
      <el-table-column align="center" label="截止时间" prop="logEndTime"/>

      <el-table-column align="center" label="错误标识" prop="enabled">
        <template slot-scope="scope">
          <el-tag :type="scope.row.errorFlag ? 'success' : 'error' ">{{ scope.row.errorFlag ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/interfacemonitor/read']" type="primary" size="mini" @click="handleView(scope.row)">查看</el-button>
         </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="'查看日志详情'" customClass="customWidth"  :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-card>
        <el-form
          ref="dataForm"
          :model="dataForm"
          status-icon

          label-width="100px"
          >
          <el-col :span="12"  >
          <el-form-item label="来源类型" prop="logSource">
            <el-input style="width:200px" v-model="dataForm.logSource"/>
          </el-form-item>
          </el-col>
          <el-col :span="12">
          <el-form-item label="调用类型" prop="logDirection">
            <el-input style="width:200px" v-model="dataForm.logDirection"/>
          </el-form-item>
          </el-col>
          <el-col :span="12" >
          <el-form-item label="日志类型" prop="logType">
            <el-input style="width:200px" v-model="dataForm.logType"/>
          </el-form-item>
          </el-col>

          <el-col :span="12"  >
          <el-form-item label="状态描述" prop="logStateDesc">
            <el-input style="width:200px" v-model="dataForm.logStateDesc"/>
          </el-form-item>
          </el-col>
          <el-col :span="24" >
            <el-form-item label="内容描述" prop="logContent">
              <el-input type="textarea"
                        :rows="2" style="width:550px" v-model="dataForm.logContent"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
          <el-form-item label="起始时间" prop="logStartTime">
            <el-input style="width:200px" v-model="dataForm.logStartTime"/>
          </el-form-item>
          </el-col>
          <el-col :span="12"   >
          <el-form-item label="截止时间" prop="logEndTime">
            <el-input style="width:200px" v-model="dataForm.logEndTime"/>
          </el-form-item>
          </el-col>
          <el-col :span="12"  >
          <el-form-item label="同步方式" prop="logSyncType">
            <el-input style="width:200px" v-model="dataForm.logSyncType"/>
          </el-form-item>
          </el-col>
          <!--<el-col :span="12"  >-->
          <!--<el-form-item label="插入N条" prop="logInsertSum">-->
            <!--<el-input style="width:200px" v-model="dataForm.logInsertSum"/>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--<el-col :span="12">-->
          <!--<el-form-item label="更新N条" prop="logUpdateSum">-->
            <!--<el-input style="width:200px" v-model="dataForm.logUpdateSum"/>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--<el-col :span="12" >-->
          <!--<el-form-item label="正确N条" prop="logUpdateSum">-->
            <!--<el-input style="width:200px" v-model="dataForm.logUpdateSum"/>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--<el-col :span="12">-->
          <!--<el-form-item label="失败N条" prop="logErrorSum">-->
            <!--<el-input style="width:200px" v-model="dataForm.logRightSum"/>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <el-col :span="12"   >
          <el-form-item label="错误发生时间" prop="errorDate">
            <el-input style="width:200px" v-model="dataForm.errorDate"/>
          </el-form-item>
          </el-col>
          <!--<el-col :span="12">-->
          <!--<el-form-item label="错误主键" prop="errorIdDesc">-->
            <!--<el-input style="width:200px" v-model="dataForm.errorIdDesc"/>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <el-col :span="24"  style="width:650px"  >
            <el-form-item label="错误消息" prop="errorMsg">
              <el-input  type="textarea"
                         :rows="2" v-model="dataForm.errorMsg"/>
            </el-form-item>
          </el-col>

        </el-form>

      </el-card>
      <!--<div slot="footer" class="dialog-footer">-->
        <!--<el-button @click="dialogFormVisible = false">关闭</el-button>-->
      <!--</div>-->
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
.customWidth {
  width: 850px;
}
</style>

<script>
import { listInterfaceMonitor,readInterfaceMonitor  } from '@/api/interfacemonitor'
import { listDiccode } from '@/api/diccode'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'InterfaceMonitor',
  components: { Pagination },
  data() {
    return {
      uploadPath,
      logSourceIdList:[],
      logDirectionIdList:[],
      logTypeList:[],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        logSourceCode:undefined,
        logDirectionCode:undefined,
        logTypeCode:undefined,
        logContent:undefined,
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
    this.getLogTypeList()
    this.getLogSourceIdList()
    this.getLogDirectionIdList()
    this.getList()
  },

  methods: {
    getLogTypeList(){
      listDiccode({dicmainName:"接口消息_类型"}).then(response => {
        this.logTypeList = response.data.data.list
      }).catch(() => {
        this.logTypeList = []
      })
    },
    getLogSourceIdList(){
      listDiccode({dicmainName:"接口消息_来源"}).then(response => {
        this.logSourceIdList = response.data.data.list
      }).catch(() => {
        this.logSourceIdList = []
      })
    },
    getLogDirectionIdList(){
      listDiccode({dicmainName:"接口消息_调用类型"}).then(response => {
        this.logDirectionIdList = response.data.data.list
      }).catch(() => {
        this.logDirectionIdList = []
      })
    },
    getList() {
      this.listLoading = true
      listInterfaceMonitor(this.listQuery)
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
      readInterfaceMonitor(this.dataForm.id).then(
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
