<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入自提货点名称"/>

      <el-button v-permission="['GET /admin/picksite/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/picksite/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="提货点Id" prop="id" sortable/>

      <el-table-column align="center" label="提货点名称" prop="siteName"/>

      <el-table-column align="center" label="提货点位置" prop="sitePos"/>

      <el-table-column align="center" label="联系电话" prop="siteTel"/>

      <el-table-column align="center" label="提货时间" prop="siteTime"/>

      <el-table-column align="center" property="siteLink" label="地址链接">
        <template slot-scope="scope">
          <img :src="scope.row.siteLink" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" label="排序" prop="ordernumber"/>

      <el-table-column align="center" label="备注" prop="remark"/>

      <el-table-column align="center" label="是否启用" prop="enabled">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/picksite/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/picksite/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <div>
        <el-card class="box-card">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="dataForm"
          status-icon

          label-width="100px"
          >
          <el-col :span="12"  >
            <el-form-item label="提货点名称" prop="siteName">
              <el-input style="width: 200px" v-model="dataForm.siteName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提货点位置" prop="sitePos">
              <el-input style="width: 200px" v-model="dataForm.sitePos"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="联系电话" prop="siteTel">
              <el-input style="width: 200px" v-model="dataForm.siteTel"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提货时间描述" prop="siteTime">
              <el-input style="width: 200px" v-model="dataForm.siteTime"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="地址链接" prop="siteLink">
              <el-upload
                :headers="headers"
                :action="uploadPath"
                :show-file-list="false"
                :on-success="uploadUrl"
                class="avatar-uploader"
                accept=".jpg,.jpeg,.png,.gif">
                <img v-if="dataForm.siteLink" :src="dataForm.siteLink" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"/>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
          <el-form-item label="排序" prop="ordernumber">
            <el-input style="width: 200px" v-model="dataForm.ordernumber"/>
          </el-form-item>
          </el-col>
          <el-col :span="24" >
          <el-form-item label="备注" prop="remark">
            <el-input style="width: 550px" v-model="dataForm.remark"/>
          </el-form-item>
          </el-col>
          <el-col :span="24" >
            <el-form-item label="提货星期" prop="sitePickWeek">
              <el-row style="width: 570px" >
                <el-select clearable style="width: 480px"  v-model="dataForm.sitePickWeek"   multiple placeholder="请选择">
                  <el-option
                    v-for="item in weeks"
                    :key="item.value"
                    :label="item.value"
                    :value="item.value">
                  </el-option>
                </el-select>
                <el-button style="width: 70px" @click="setDefaultWeeks">全选</el-button>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="24" >
            <el-form-item label="预约时间" prop="sitePickTime" >
              <el-row style="width: 550px" >
                <el-time-picker
                  is-range
                  style="width: 400px"
                  value-format="HH:mm"
                  v-model="dataForm.sitePickTime"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  placeholder="选择时间范围">
                </el-time-picker>
                <el-button style="width: 130px" @click="setDefaultTime">设置默认</el-button>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="24" >
          <el-form-item label="是否启用" prop="enabled">
            <el-select  clearable v-model="dataForm.enabled" placeholder="请选择">
              <el-option :value="true" label="启用"/>
              <el-option :value="false" label="不启用"/>
            </el-select>
          </el-form-item>
          </el-col>
        </el-form>
        </el-card>
<!--        <div slot="footer" class="dialog-footer">-->
        <div class="op-container" style="text-align: center;padding-top: 10px">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
          <el-button v-else type="primary" @click="updateData">确定</el-button>
        </div>
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
import { listPickSite, createPickSite, updatePickSite, deletePickSite,readPickSite } from '@/api/picksite'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'PickSite',
  components: { Pagination },
  data() {
    return {
      weeks: [{value: '周一',label: '周一'}, {value: '周二',label: '周二'}, {value: '周三',label: '周三'},
        {value: '周四',label: '周四'}, {value: '周五',label: '周五'},{value: '周六',label: '周六'}, {value: '周日',label: '周日'}
      ],
      uploadPath,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        sort: 'ordernumber asc',

      },
      dataForm: {
        id: undefined,
        siteName: undefined,
        sitePos: undefined,
        siteTel: undefined,
        siteTime: undefined,
        siteLink: undefined,
        sitePickWeek: ['周一', '周二','周三','周四','周五','周六','周日'],
        sitePickTime: ['09:00','17:00'],
        ordernumber: 1,
        remark: undefined,
        enabled: true
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        siteName: [
          { required: true, message: '提货点名称不能为空', trigger: 'blur' }
        ],
        sitePickWeek: [
          { required: true, message: '提货点星期设置不能为空', trigger: 'blur' }
        ],
        sitePickTime: [
          { required: true, message: '提货点预约时间不能为空', trigger: 'blur' }
        ]
      },
      downloadLoading: false
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
    this.getList()
  },

  methods: {
    setDefaultTime(){
      this.dataForm.sitePickTime=['09:00','17:00']
    },
    setDefaultWeeks(){
      this.dataForm.sitePickWeek=['周一', '周二','周三','周四','周五','周六','周日']
    },
    getList() {
      this.listLoading = true
      listPickSite(this.listQuery)
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
    resetForm() {
      this.dataForm = {
        id: undefined,
        siteName: undefined,
        sitePos: undefined,
        siteTel: undefined,
        siteTime: undefined,
        siteLink: undefined,
        sitePickWeek: ['周一', '周二','周三','周四','周五','周六','周日'],
        sitePickTime: ['09:00','17:00'],
        ordernumber: 1,
        remark: undefined,
        enabled: true
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    uploadUrl: function(response) {
      this.dataForm.siteLink = response.data.url
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createPickSite(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$message.success('创建成功');

              this.getList()
            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      readPickSite(this.dataForm.id).then(
        (response)=>{
          this.dataForm=response.data.data;
        }
      )
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updatePickSite(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$message.success('更新成功');

              this.getList()
            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
    handleDelete(row) {
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deletePickSite(row)
        .then(response => {
          this.$message.success('删除成功');

          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '提货点ID',
          '提货点名称',
          '提货点位置',
          '联系电话',
          '提货时间',
          '提货点地址链接',
          '排序',
          '备注',
          '是否启用'
        ]
        const filterVal = [
          'id',
          'siteName',
          'sitePos',
          'siteTel',
          'siteTime',
          'siteLink',
          'ordernumber',
          'remark',
          'enabled'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '提货点信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
