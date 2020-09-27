<template>

    <div class="app-container">
      <el-row :gutter="40" class="panel-group">
        <el-col :xs="4" :sm="4" :lg="4" class="card-panel-col">
          <el-tree :highlight-current="true" :data="treeData" :props="defaultProps" @node-click="handleNodeClick"/>
        </el-col>
        <el-col :xs="20" :sm="20" :lg="20" class="card-panel-col">

          <!-- 查询和其他操作 -->
          <div class="filter-container">

            <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" placeholder="请输入文章标题"/>
            <el-input v-model="listQuery.author" clearable class="filter-item" style="width: 200px;" placeholder="请输入作者"/>
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter" >查找</el-button>
            <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate" >添加</el-button>
           </div>

          <!-- 查询结果 -->
          <el-table :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

            <el-table-column align="center" label="文章ID" prop="id"/>

            <el-table-column align="center" min-width="100" label="文章分类" prop="classPath"/>

            <el-table-column align="center" min-width="100" label="文章标题" prop="title"/>

            <el-table-column align="center" min-width="100" label="作者" prop="author"/>

            <el-table-column align="center" min-width="100" label="发表日期" prop="pubDate"/>

            <el-table-column align="center" min-width="100" label="排序" prop="sortOrder"/>

            <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-permission="['POST /admin/articlelist/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
                <el-button v-permission="['POST /admin/articlelist/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-tooltip placement="top" content="返回顶部">
            <back-to-top :visibility-height="100" />
          </el-tooltip>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
          <!--</div>-->
        </el-col>
      </el-row>

      <!-- 添加或修改对话框 -->
      <el-dialog  customClass="customWidth"  :title="textMap[dialogStatus]" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
        <el-card>
          <el-form
            ref="dataForm"
            :rules="rules"
            :model="dataForm"
            status-icon

            label-width="100px"
            >
            <el-col :span="12">
            <el-form-item label="所属分类" prop="classPath">
              <el-input style="width:200px" v-model="dataForm.classPath" :disabled="true"/>
            </el-form-item>
            </el-col>
            <el-col :span="12"  >
            <el-form-item label="文章标题" prop="title">
              <el-input  style="width:200px" v-model="dataForm.title"/>
            </el-form-item>
            </el-col>
            <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input  style="width:200px" v-model="dataForm.author"/>
            </el-form-item>
            </el-col>
            <el-col :span="12"  >
            <el-form-item label="发表日期" prop="pubDate">
              <el-date-picker style="width: 200px"
                              v-model="dataForm.pubDate"
                              type="date"
                              placeholder="选择日期"
                              value-format="yyyy-MM-dd"/>
            </el-form-item>
            </el-col>
            <el-col :span="12">
            <el-form-item label="排序" prop="ordernumber">
              <el-input  style="width:200px" v-model="dataForm.sortOrder"/>
            </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="文章内容" style="width:700px" >
                <editor :init="editorInit"  v-model="dataForm.content"/>
              </el-form-item>
            </el-col>
          </el-form>
        </el-card>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
          <el-button v-else type="primary" @click="updateData">确定</el-button>
        </div>
      </el-dialog>

    </div>


</template>

<style>
  .table-expand {
    font-size: 0;
  }
  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }
  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }
  .gallery {
    width: 80px;
    margin-right: 10px;
  }
  .customWidth {
    width: 850px;
  }
</style>

<script>
import { listArticleClassCascade } from '@/api/articleclass'
import { listArticleList,createArticleList,readArticleList,updateArticleList,deleteArticleList } from '@/api/articlelist'
import BackToTop from '@/components/BackToTop'
import Editor from '@tinymce/tinymce-vue'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'articlelist',
  components: { BackToTop, Pagination, Editor },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        classId: undefined,
        className: undefined,
        title: undefined,
        author:undefined,
      },
      dataForm: {
        id:undefined,
        classId: undefined,
        className: undefined,
        classPath: undefined,
        title: undefined,
        content:undefined,
        author:undefined,
        pubDate:undefined,
        sortOrder:undefined,

      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      editorInit: {
        language: 'zh_CN',
        convert_urls: false,
        plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
        toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      },
      rules: {
        title: [
          { required: true, message: '文章标题不能为空', trigger: 'blur' }
        ]
      },
    }
  },
  created() {
    this.getArticleClassCascadeList()
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listArticleList(this.listQuery)
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
    getArticleClassCascadeList() {
      listArticleClassCascade().then(response => {
        this.treeData = response.data.data.list

      })
    },
    resetForm() {
      this.dataForm = {
        id:undefined,
        classId: this.dataForm.classId,
        className: this.dataForm.className,
        classPath: this.dataForm.classPath,
        title: undefined,
        content:undefined,
        author:undefined,
        pubDate:undefined,
        sortOrder:undefined,
      }
    },
    handleNodeClick(data) {
      this.dataForm.classId=data.value
      this.dataForm.className=data.label
      this.dataForm.classPath=data.label
      this.listQuery.page = 1
      this.listQuery.classId=data.value
      this.getList()
      console.log(data)
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      readArticleList(this.dataForm.id).then(
        (response)=>{
          this.dataForm=response.data.data
      }),
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleDelete(row) {
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deleteArticleList(row).then(response => {
          this.$message.success('删除成功');
          //this.getList()
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      if(this.dataForm.classId==null){
        alert("请先选择要填加的文章类别");
        return;
      }
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
          createArticleList(this.dataForm)
            .then(response => {
              //this.list.unshift(response.data.data)
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
    updateData(){
      this.$refs['dataForm'].validate(valid => {
        if (valid) {

          updateArticleList(this.dataForm)
            .then(() => {
              this.dialogFormVisible = false
              this.$message.success('更新文章成功');
              this.getList()
            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
  }
}
</script>
