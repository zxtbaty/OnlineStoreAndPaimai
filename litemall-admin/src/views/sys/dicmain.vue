<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入代码表名称"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="字典表ID" prop="id" sortable/>

      <el-table-column align="center" label="字典表名称" prop="name"/>

      <el-table-column align="center" label="字典表描述" prop="desp"/>

      <el-table-column align="center" label="排序" prop="ordernumber"/>

      <el-table-column align="center" label="是否系统" prop="systemed">
        <template slot-scope="scope">
          <el-tag :type="scope.row.systemed ? 'success' : 'error' ">{{ scope.row.systemed ? '系统' : '用户' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否启用" prop="enabled">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!--仔细阅读Dialog的各个属性参数，会影响到布局排版，例如遇到了一个大坑就是没有设置:append-to-body='true'，导致遮罩遮盖整个页面，:lock-scroll="false"没有设置的话，点击前后会感觉到头部导航栏的移动，体验性很不好！！还有设置dialog的宽度width="40%"前面不用加冒号：-->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible" width="54%">

      <!--<login-name-test></login-name-test>-->
      <dicmain-edit :main-id="mainId" :changeDispaly="dialogFormVisible" @lisenChildCloseEvent="handleChildClose"/>

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
import DicmainEdit from './editdicmain.vue'

import { listDicmain, createDicmain, updateDicmain, deleteDicmain } from '@/api/dicmain'
import { listDiccode, createDiccode, updateDiccode, deleteDiccode } from '@/api/diccode'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import LoginNameTest from './login.vue'

export default {
  name: 'DicMain',
  components: { DicmainEdit, LoginNameTest, Pagination },

  data() {
    return {
      mainId: undefined,
      uploadPath,
      list: [],
      total: 0,
      listLoading: true,

      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        remark: undefined,
        sort: 'ordernumber asc,add_time desc',
        order: 'asc'
      },
      dataForm: {
        id: undefined,
        name: undefined,
        desp: undefined,
        ordernumber: 1,
        systemed: false,
        enabled: true
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '字典表名称不能为空', trigger: 'blur' }
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
    getList() {
      this.listLoading = true
      listDicmain(this.listQuery)
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
        name: undefined,
        desp: undefined,
        ordernumber: 1,
        systemed: false,
        enabled: true
      }
    },
    // handleCreate() {
    //   this.$router.push({ path: '/sys/dicmain/create' })
    // },
    handleCreate() {
      // this.$router.push({ path: '/dicmain/create' })
      this.dataForm={
          id: undefined,
          name: undefined,
          desp: undefined,
          ordernumber: 1,
          systemed: false,
          enabled: true
      },
      this.dialogStatus = 'create'
      this.mainId = null
      this.dialogFormVisible = true
    },
    handleUpdate(row) {
      // this.$router.push({ path: '/sys/dicmain/edit', query: { id: row.id }})

      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.mainId = null
      this.mainId = row.id
    },

    uploadUrl: function(response) {
      this.dataForm.url = response.data.url
    },
    // createData() {
    //   this.$refs['dataForm'].validate(valid => {
    //     if (valid) {
    //       createDicmain(this.dataForm)
    //         .then(response => {
    //           this.list.unshift(response.data.data)
    //           this.dialogFormVisible = false
    //           this.$notify.success({
    //             title: '成功',
    //             message: '创建成功'
    //           })
    //         })
    //         .catch(response => {
    //           this.$notify.error({
    //             title: '失败',
    //             message: response.data.errmsg
    //           })
    //         })
    //     }
    //   })
    // },

    handleChildClose: function(data) {
      this.dialogFormVisible = false
      if (data == 'save') {
        this.getList()
      }
    },

    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateDicmain(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$message.success('更新代码表成功');

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
      deleteDicmain(row)
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
          '字典表ID',
          '字典表名称',
          '描述',
          '排序',
          '是否系统',
          '是否启用'
        ]
        const filterVal = [
          'id',
          'name',
          'desp',
          'ordernumber',
          'systemed',
          'enabled'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '字典表信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
