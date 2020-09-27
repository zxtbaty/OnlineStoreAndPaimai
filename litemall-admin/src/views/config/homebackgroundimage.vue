<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入背景标题"/>
      <el-input v-model="listQuery.remark" clearable class="filter-item" style="width: 200px;" placeholder="请输入备注"/>
      <el-button v-permission="['GET /admin/homebackgroundimage/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/homebackgroundimage/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="背景ID" prop="id" sortable/>

      <el-table-column align="center" label="背景标题" prop="name"/>

      <el-table-column align="center" label="背景图片" prop="url">
        <template slot-scope="scope">
          <img v-if="scope.row.url" :src="scope.row.url" width="80">
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
          <el-button v-permission="['POST /admin/homebackgroundimage/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/homebackgroundimage/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon

        label-width="100px"
        >
        <el-form-item label="背景标题" prop="name">
          <el-input v-model="dataForm.name"/>
        </el-form-item>

        <el-form-item label="背景图片" prop="url">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif">
            <img v-if="dataForm.url" :src="dataForm.url" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="排序" prop="ordernumber">

          <el-input v-model="dataForm.ordernumber"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark"/>
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-select clearable v-model="dataForm.enabled" placeholder="请选择">
            <el-option :value="true" label="启用"/>
            <el-option :value="false" label="不启用"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
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
  import { listHomeBackgroundImage, createHomeBackgroundImage, updateHomeBackgroundImage, deleteHomeBackgroundImage,readHomeBackgroundImage } from '@/api/homebackgroundimage'
  import { uploadPath } from '@/api/storage'
  import { getToken } from '@/utils/auth'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

  export default {
    name: 'BackgroundImage',
    components: { Pagination },
    data() {
      return {
        uploadPath,
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20,
          name: undefined,
          remark: undefined,
          sort: 'ordernumber asc',
        },
        dataForm: {
          id: undefined,
          name: undefined,
          url: undefined,
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
          name: [
            { required: true, message: '背景图名称不能为空', trigger: 'blur' }
          ],
          url: [{ required: true, message: '背景图片不能为空', trigger: 'blur' }]
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
        listHomeBackgroundImage(this.listQuery)
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
          content: undefined,
          url: undefined,
          position: 1,
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
        this.dataForm.url = response.data.url
      },
      createData() {
        this.$refs['dataForm'].validate(valid => {
          if (valid) {
            createHomeBackgroundImage(this.dataForm)
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
        readHomeBackgroundImage(this.dataForm.id).then(
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
            updateHomeBackgroundImage(this.dataForm)
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
        deleteHomeBackgroundImage(row)
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
            '背景图ID',
            '背景图名称',
            '背景图片',
            '排序',
            '备注',
            '是否启用'
          ]
          const filterVal = [
            'id',
            'name',
            'url',
            'ordernumber',
            'remark',
            'enabled'
          ]
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '背景图信息')
          this.downloadLoading = false
        })
      }
    }
  }
</script>
