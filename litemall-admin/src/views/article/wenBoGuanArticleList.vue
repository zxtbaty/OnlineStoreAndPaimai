<template>

    <div class="app-container">
      <el-tabs id="currentTab" v-model="activeTabName" tab-position="top"   @click="changeCurrentTab(activeTabName)" @change="changeCurrentTab(activeTabName)">
        <el-tab-pane name="文章管理" label="文章管理" >
          <el-row :gutter="40" class="panel-group">

            <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">

              <!-- 查询和其他操作 -->
              <div class="filter-container">

                <el-select clearable class="filter-item" style="width: 130px;" placeholder="文章分类" v-model="listQuery.classId" >
                  <el-option v-for="item in classList" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" placeholder="请输入文章标题"/>
                <el-input v-model="listQuery.author" clearable class="filter-item" style="width: 200px;" placeholder="请输入作者"/>
                <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter" >查找</el-button>
                <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate" >添加</el-button>
              </div>

              <!-- 查询结果 -->
              <el-table :data="list" size="small" element-loading-text="正在查询中。。。" width="960px" border fit highlight-current-row>

                <el-table-column align="center" label="文章ID" prop="id"/>

                <el-table-column align="center" min-width="100" label="文章分类" prop="className"/>

                <el-table-column align="center" min-width="100" label="文章标题" prop="title"/>

                <el-table-column align="center" min-width="100" label="作者" prop="author"/>

                <el-table-column align="center" property="iconUrl" label="作者头像">
                  <template slot-scope="scope">
                    <img :src="scope.row.picHead" width="100">
                  </template>
                </el-table-column>
                <el-table-column align="center" property="iconUrl" label="卡片配图">
                  <template slot-scope="scope">
                    <img :src="scope.row.cardPic" width="200">
                  </template>
                </el-table-column>
                <el-table-column align="center" min-width="120" label="发表日期" prop="pubDate"/>

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
        </el-tab-pane>
        <el-tab-pane name="首页推荐" label="首页推荐">
          <div class="app-container">
            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddWenBoGuanArticle()">添加推荐文章</el-button>
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleSaveRecommendOrdernumber()">批量保存</el-button>
            </div>
            <!-- 查询结果 -->
            <el-table v-loading="listRecommendLoading" :data="listRecommend" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="50px" label="文章分类" prop="className"/>

              <el-table-column align="center" min-width="50px" label="文章ID" prop="id"/>

              <el-table-column align="center" min-width="200px" label="文章标题" prop="title"/>

              <el-table-column align="center" min-width="100px" label="文章作者" prop="author"/>

              <el-table-column align="center" min-width="50px" label="发表日期" prop="pubDate"/>

              <el-table-column align="center" min-width="150px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdateRecommend(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDeleteRecommend(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="listRecommendTotal>0" :total="listRecommendTotal" :page.sync="listRecommendQuery.page" :limit.sync="listRecommendQuery.limit" @pagination="getListRecommend" />

          </div>
        </el-tab-pane>


      </el-tabs>

      <!-- 添加或修改对话框 -->
      <el-dialog  customClass="customWidth"  :title="textMap[dialogStatus]"
                  v-if="dialogFormVisible" :before-close="closeAndRefresh"
                  :close-on-click-modal="false"   :visible.sync="dialogFormVisible">
        <el-card>
          <el-form
            ref="dataForm"
            :rules="rules"
            :model="dataForm"
            status-icon
            label-width="100px">
            <el-col :span="12">
              <el-form-item label="文章分类" prop="title">
              <el-select clearable  style="width: 200px;" placeholder="文章分类"
                         v-model="dataForm.classId" @change="changeClass(dataForm.classId)" >
                <el-option v-for="item in classList" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
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
            <el-col :span="12">
              <el-form-item label="浏览量" prop="browseCount">
                <el-input :disabled="true"  style="width:200px" v-model="dataForm.browseCount"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="点赞数" prop="zanCount">
                <el-input :disabled="true"  style="width:200px" v-model="dataForm.zanCount"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="分享数" prop="shareCount">
                <el-input :disabled="true"  style="width:200px" v-model="dataForm.shareCount"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="作者头像(64*64)">
                <el-upload
                  :action="uploadPath"
                  :headers="headers"
                  :show-file-list="false"
                  :on-success="uploadPicHead"
                  :on-progress="uploadVideoProcess"
                  class="avatar-uploader"
                  accept=".jpg,.jpeg,.png,.gif">
                  <img v-if="dataForm.picHead" :src="dataForm.picHead" class="avatar">
                  <i v-else class="el-icon-plus avatar-uploader-icon"/>
                  <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="12">

                <el-form-item label="卡片配图(750*375)">
                  <el-upload
                    :action="uploadPath"
                    :headers="headers"
                    :show-file-list="false"
                    :on-success="uploadCardPic"
                    :on-progress="uploadVideoProcess"
                    class="avatar-uploader"
                    accept=".jpg,.jpeg,.png,.gif">
                    <img v-if="dataForm.cardPic" :src="dataForm.cardPic" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"/>
                    <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>

                  </el-upload>
                </el-form-item>

            </el-col>
            <el-col :span="24">
              <el-form-item label="文章封面" style="width:700px" >
                <editor ref="tinymceFengmian" :init="editorInit"  v-model="dataForm.fengmian"/>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="文章内容" style="width:700px" >
                <editor ref="tinymceContent" :init="editorInit"  v-model="dataForm.content"/>
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


      <el-dialog :title="'选择文博馆文章'" :close-on-click-modal="false" :visible.sync="dialogSelArticleFormVisible" width="80%">
        <div class="app-container">
          <!-- 查询和其他操作 -->
          <div class="filter-container">

            <el-select clearable class="filter-item" style="width: 130px;" placeholder="文章分类" v-model="selArticleListQuery.classId" >
              <el-option v-for="item in classList" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
            <el-input v-model="selArticleListQuery.title" clearable class="filter-item" style="width: 200px;" placeholder="请输入文章标题"/>
            <el-input v-model="selArticleListQuery.author" clearable class="filter-item" style="width: 200px;" placeholder="请输入作者"/>
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleSelectFilter" >查找</el-button>
            <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCheckAdd" >填加选中的文章</el-button>
          </div>
          <!-- 查询结果 -->
          <el-table
            v-loading="selArticleListLoading"
            :data="selArticleList"
            element-loading-text="正在查询中。。。"
            border
            fit
            highlight-current-row
            @selection-change="changeSelArticleList">

            <el-table-column type="selection"/>

            <el-table-column align="center" label="文章ID" prop="id"/>

            <el-table-column align="center" min-width="100" label="文章分类" prop="className"/>

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

          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

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

import { listArticleList,listRecommendArticleList,createArticleList,readArticleList,
  updateArticleList,deleteArticleList,addRecommendArticle,listUnRecommendArticleList,
  updateRecommend,deleteRecommend,updateRecommendBatch
  } from '@/api/wenBoGuanArticlelist'
import BackToTop from '@/components/BackToTop'
import Editor from '@tinymce/tinymce-vue'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { listDiccode } from '@/api/diccode'
import { createStorage, uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'

export default {
  name: 'articlelist',
  components: { BackToTop, Pagination, Editor },
  data() {
    return {
      checkBoxSelArticleListData:[],//选择的文博馆文章
      uploadPath,
      videoFlag:false,
      videoUploadPercent:0,
      activeTabName:'文章管理',
      classList:[],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        classId: undefined,
        title: undefined,
        author:undefined,
      },

      listRecommend: [],
      listRecommendTotal: 0,
      listRecommendLoading: false,
      listRecommendQuery: {
        page: 1,
        limit: 20
      },

      selArticleList: [],
      selArticleListTotal: 0,
      selArticleListLoading: false,
      selArticleListQuery: {
        page: 1,
        limit: 20,
        classId: undefined,
        title: undefined,
        author:undefined,
      },


      dataForm: {
        id:undefined,
        classId: undefined,
        className: undefined,
        title: undefined,
        fengmian:undefined,
        content:undefined,
        author:undefined,
        picHead:undefined,
        pubDate:undefined,
        sortOrder:undefined,
        browseCount:undefined,
        zanCount:undefined,
        shareCount:undefined,
        cardPic:undefined,
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },

      dialogSelArticleFormVisible: false,

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
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  watch: {
    'activeTabName': function(val) { // 监听切换状态-计划单
      this.activeTabName = val
      this.changeCurrentTab(this.activeTabName)
    }
  },
  created() {
    this.getWenBoGuanClassList();
    this.getList()
    this.$refs['tinymceFengmian'].setContent("")
    this.$refs['tinymceContent'].setContent("")

  },
  methods: {
    closeAndRefresh(){
      this.dialogFormVisible = false

      this.getList()
    },
    handleSaveRecommendOrdernumber(){
      updateRecommendBatch(this.listRecommend).then((res)=>{
        this.$message.success("更新成功")
      })
    },
    handleUpdateRecommend(row){
      updateRecommend(row).then((res)=>{
        this.$message.success("更新成功")
      })
    },
    handleDeleteRecommend(row){
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deleteRecommend(row).then((res)=>{
        this.getListRecommend();
        this.$message.success("删除成功")
      })
    },
    changeCurrentTab(activeTabName){
      if(activeTabName=="文章管理"){
        this.getList()
      } else
        if(activeTabName=="首页推荐"){
          this.getListRecommend();
     }
    },
    handleAddWenBoGuanArticle(){
      this.dialogSelArticleFormVisible=true;
      this.handleSelectFilter();
    },
    handleCheckAdd() {
      if(this.checkBoxSelArticleListData.length==0){
        this.$message.error("请选择要添加的文章");
        return;
      }
      addRecommendArticle(this.checkBoxSelArticleListData).then(response => {
         this.dialogSelArticleFormVisible=false;
         this.getListRecommend();

       })

    },
    changeSelArticleList(val){
      this.checkBoxSelArticleListData = val
    },
    uploadPicHead: function(response) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;
      this.dataForm.picHead = response.data.url
    },
    uploadCardPic: function(response) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;
      this.dataForm.cardPic = response.data.url
    },
    uploadVideoProcess(event, file, fileList) {
      this.videoFlag = true;
      this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
    },
    changeClass(classId) {
      const obj = this.classList.find((item) => {
        return item.id === classId
      })
      this.dataForm.className = obj.name

    },
    getWenBoGuanClassList(){
      listDiccode({dicmainName:"文博馆分类"}).then(response => {
        this.classList = response.data.data.list
      }).catch(() => {
        this.classList = []
      })
    },
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
    getSelArticleList() {
      this.selArticleListLoading = true
      listUnRecommendArticleList(this.selArticleListQuery)
        .then(response => {
          this.selArticleList = response.data.data.list
          this.selArticleListTotal = response.data.data.total
          this.selArticleListLoading = false
        })
        .catch(() => {
          this.selArticleList = []
          this.selArticleListTotal = 0
          this.selArticleListLoading = false
        })
    },
    getListRecommend() {
      this.listRecommendLoading = true
      listRecommendArticleList(this.listRecommendQuery)
        .then(response => {
          this.listRecommend = response.data.data.list
          this.listRecommendTotal = response.data.data.total
          this.listRecommendLoading = false
        })
        .catch(() => {
          this.listRecommend = []
          this.listRecommendTotal = 0
          this.listRecommendLoading = false
        })
    },

    resetForm() {
      this.dataForm = {
        id:undefined,
        classId: undefined,
        className: undefined,
        title: undefined,
        fengmian:undefined,
        content:undefined,
        author:undefined,
        picHead:undefined,
        pubDate:undefined,
        sortOrder:undefined,
        browseCount:undefined,
        zanCount:undefined,
        shareCount:undefined,
        cardPic:undefined,
      }
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

    handleSelectFilter() {
      this.selArticleListQuery.page = 1
      this.getSelArticleList()
    },


    handleCreate() {

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
