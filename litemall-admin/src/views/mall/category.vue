<template>
  <div class="app-container">


    <!-- 查询和其他操作 -->
    <div class="filter-container hot-header" style="text-align: left" >

      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品类别名称"/>
      <el-input v-model="listQuery.comName" clearable class="filter-item" style="width: 200px;" placeholder="请输入公司名称"/>
      <el-button v-permission="['GET /admin/category/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/category/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>

    </div>
    <div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row row-key="id">

        <el-table-column align="center" label="类目ID" prop="id"/>

        <el-table-column align="center" label="类目名" prop="name"/>
        <el-table-column align="center" label="公司名称" prop="comName" />
        <el-table-column align="center" label="类目图标" property="iconUrl" >
          <template slot-scope="scope">
            <img v-if="scope.row.iconUrl" :src="scope.row.iconUrl" width="40">
          </template>
        </el-table-column>

        <el-table-column align="center" property="picUrl" label="类目图片">
          <template slot-scope="scope">
            <img v-if="scope.row.picUrl" :src="scope.row.picUrl" width="80">
          </template>
        </el-table-column>

        <el-table-column align="center" label="关键字" prop="keywords"/>

        <el-table-column align="center"  label="简介" prop="desc"/>
        <el-table-column align="center"  label="排序" prop="sortOrder"/>

        <el-table-column align="center" label="级别" prop="level">
          <template slot-scope="scope">
            <el-tag :type="scope.row.level === 'L1' ? 'primary' : 'info' ">{{ scope.row.level === 'L1' ? '一级类目' : '二级类目' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/category/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/category/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" >
        <el-form-item label="公司名称" property="comId">
          <el-select style="width: 350px" clearable  v-model="dataForm.comId" @change="changeComID(dataForm.comId)">
            <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="类目名称" prop="name">
          <el-input v-model="dataForm.name" style="width: 350px"/>
        </el-form-item>
        <el-form-item label="关键字" prop="keywords">
          <el-input v-model="dataForm.keywords" style="width: 350px"/>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select style="width: 350px" v-model="dataForm.level" @change="onLevelChange">
            <el-option label="一级类目" value="L1"/>
            <el-option label="二级类目" value="L2"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dataForm.level === 'L2'" label="父类目" prop="pid">
          <el-select style="width: 350px" v-model="dataForm.pid">
            <el-option v-for="item in catL1" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="类目简介" prop="desc">
          <el-input style="width: 350px" v-model="dataForm.desc"/>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input style="width: 350px" v-model="dataForm.sortOrder"/>
        </el-form-item>
        <el-form-item label="类目图标" prop="iconUrl">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadIconUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif">
            <img v-if="dataForm.iconUrl" :src="dataForm.iconUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="类目图片" prop="picUrl">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadPicUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif">
            <img v-if="dataForm.picUrl" :src="dataForm.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
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

<style >
.filter-item{
  margin-left: 10px;
}
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

.hot-header{
  display: flex;
  flex-flow: row nowrap;
  justify-content: flex-start;
}
</style>

<script>
import { listCategory, listCatL1, createCategory, updateCategory, deleteCategory,readCategory } from '@/api/category'
import { allCompany } from '@/api/company'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'

export default {
  name: 'Category',
  data() {
    return {
      uploadPath,
      list: [],
      listLoading: true,
      comList:[],
      catL1: {},
      dataForm: {
        id: undefined,
        name: '',
        comId:undefined,
        comName:undefined,
        keywords: '',
        level: 'L1',
        pid: undefined,
        desc: '',
        iconUrl: '',
        picUrl: '',
        sortOrder:undefined
      },
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        comName:undefined,
        sort: 'sort_order',

      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [{ required: true, message: '类目名不能为空', trigger: 'blur' }],
        comId: [{ required: true, message: '所属公司信息', trigger: 'blur' }]
      }
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
    this.getComList()
    this.getList()
    this.getCatL1()
  },
  methods: {
    getComList() {
      allCompany().then(
        response => {
          this.comList = response.data.data.list
          if (this.comList.length > 0) {
            this.selComId = this.comList[0].id
            this.selComName = this.comList[0].name
          }
        }
      )
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    getList() {
      this.listLoading = true
      listCategory(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.listLoading = false
        })
    },
    getCatL1() {
      listCatL1(this.dataForm.comId).then(response => {
        this.catL1 = response.data.data.list
      })
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        name: '',
        comId:undefined,
        comName:undefined,
        keywords: '',
        level: 'L1',
        pid: undefined,
        desc: '',
        iconUrl: '',
        picUrl: '',
        sortOrder:undefined
      }
    },
    onLevelChange: function(value) {
      if (value === 'L1') {
        this.dataForm.pid = 0
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
    uploadIconUrl: function(response) {
      this.dataForm.iconUrl = response.data.url
    },
    uploadPicUrl: function(response) {
      this.dataForm.picUrl = response.data.url
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createCategory(this.dataForm)
            .then(response => {
              this.getList()
              // 更新L1目录
              this.getCatL1()
              this.dialogFormVisible = false
              this.$message.success('创建成功');

            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      readCategory(this.dataForm.id).then(
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
          updateCategory(this.dataForm)
            .then(() => {
              this.getList()
              // 更新L1目录
              this.getCatL1()
              this.dialogFormVisible = false
              this.$message.success('更新成功');

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
      deleteCategory(row)
        .then(response => {
          this.getList()
          // 更新L1目录
          this.getCatL1()
          this.$message.success('删除成功');

        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    },
    changeComID(comId){

      const obj = this.comList.find((item) => {
        return item.id === comId
      })
      this.dataForm.comName=obj.name

    },
  }
}
</script>
