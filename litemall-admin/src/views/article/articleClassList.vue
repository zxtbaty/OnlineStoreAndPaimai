<template>
  <div class="app-container">


    <!-- 查询和其他操作 -->
    <div style="text-align: left;margin-bottom: 10px" >
      <el-input v-model="listQuery.id" clearable class="filter-item" style="width: 200px;" placeholder="请输入文章类别ID"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入文章类别名称"/>

      <el-button v-permission="['GET /admin/articleclass/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/articleclass/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreateOrderTest" >创建订单接口测试</el-button>
    </div>
    <div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list"  element-loading-text="正在查询中。。。" border fit highlight-current-row row-key="id" >

        <!--<el-table-column align="center" label="分类ID" prop="id"/>-->

        <el-table-column align="left" label="分类名称" prop="name"/>

        <el-table-column align="center" label="级别" prop="level">
          <template slot-scope="scope">
            <el-tag  >{{ scope.row.level }}级</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="排序" prop="sortOrder"/>

        <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/articleclass/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/articleclass/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon  label-width="100px" >

        <el-form-item v-if="dialogStatus=='create'" label="所属分类" prop="className">
             <el-tree  :highlight-current="true" :data="articleClassCascadeList" :props="defaultProps" @node-click="handleNodeClick"/>
        </el-form-item>
        <!--<el-form-item v-if="dialogStatus=='create'" label="所属分类" prop="className">-->
          <!--<el-cascader-->
                       <!--style="width:300px"  ref="belongClass"-->
                       <!--:options="articleClassCascadeList"-->
                       <!--:props="props"-->
                       <!--expand-trigger="hover"-->
                       <!--@change="handleArticleClassCascadeChange"-->
                       <!--placeholder="所属分类"/>-->

        <!--</el-form-item>-->
        <el-form-item v-if="dialogStatus=='update'" label="所属分类" prop="pPath">
          <el-input v-model="dataForm.pPath"  :disabled="true"/>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="dataForm.name"/>
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-input v-model="dataForm.level" :readonly="true" :disabled="true"/>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input v-model="dataForm.sortOrder"/>
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
  import { listArticleClass, listArticleClassCascade, listArticleClassL1, createArticleClass, readArticleClass,updateArticleClass,
    deleteArticleClass,interfaceArticleClass,postArticleClass,interfaceShouduCrm,modifyCrmUserInfo } from '@/api/articleclass'

  import { uploadPath } from '@/api/storage'
  import { getToken } from '@/utils/auth'

  export default {
    name: 'ArticleClassList',
    data() {
      return {

        defaultProps: {
          children: 'children',
          label: 'label',
          level:'level'
        },
        props: { checkStrictly:true },
        uploadPath,
        list: [],
        listLoading: false,
        catL1: {},
        articleClassCascadeList: [],
        dataForm: {
          id: undefined,
          pid: undefined,
          pPath:undefined,
          name: '',
          level: undefined,
          sortOrder: undefined,
        },
        listQuery: {
          id: undefined,
          page: 1,
          limit: 20,

          name: undefined,
          sort: 'add_time desc',

        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        rules: {
          name: [{ required: true, message: '文章分类名不能为空', trigger: 'blur' }]
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

      this.getList()

    },
    methods: {
      getArticleClassCascadeList() {
        listArticleClassCascade().then(response => {
          this.articleClassCascadeList = response.data.data.list

        })
      },

      handleFilter() {
        this.listQuery.page = 1
        this.getList()
      },
      getList() {
        this.listLoading = true
        listArticleClass(this.listQuery)
          .then(response => {
            this.list = response.data.data.list
            this.listLoading = false
          })
          .catch(() => {
            this.list = []
            this.listLoading = false
          })
      },

      resetForm() {
        this.dataForm = {
          id: undefined,
          pid: undefined,
          pPath:undefined,
          name: '',
          level: undefined,
          sortOrder: undefined,
        }
      },

      handleCreate() {
        this.getArticleClassCascadeList()
        this.resetForm()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate(valid => {
          if (valid) {
            createArticleClass(this.dataForm)
              .then(response => {
                this.getList()
                // 更新L1目录

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
        this.getArticleClassCascadeList()
        this.dataForm = Object.assign({}, row)
        readArticleClass(this.dataForm.id).then(
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
            updateArticleClass(this.dataForm)
              .then(() => {
                this.getList()
                // 更新L1目录
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
        deleteArticleClass(row)
          .then(response => {
            this.getList()
            // 更新L1目录
            this.$message.success('删除成功');

          })
          .catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
      },
      handleArticleClassCascadeChange(value) {
        this.dataForm.pid = value[value.length - 1]
        this.dataForm.pPath=this.$refs['belongClass'].currentLabels.toString()
        this.dataForm.level=value.length
      },
      handleNodeClick(data) {
        this.dataForm.pid = data.value
        this.dataForm.pPath = data.label
        if (data.level == null) {
          this.dataForm.level = 1
        } else {
          this.dataForm.level = data.level+1
        }

      },

      handleCreateOrderTest(){
        postArticleClass()
          .then(response => {
            //this.list.unshift(response.data.data)

            this.$message.success(response.data.data);

          })
          .catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
      },


    }
  }
</script>
