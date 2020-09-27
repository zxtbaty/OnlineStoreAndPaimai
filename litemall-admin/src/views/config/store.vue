<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select v-model="listQuery.comId" clearable class="filter-item" style="width: 180px;" placeholder="公司名称" @change="getCatL1(listQuery.comId)"  >
        <el-option v-for="item in companyList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="listQuery.brandId" clearable class="filter-item" style="width: 180px;" placeholder="选择品牌">
        <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入店铺名称"/>
      <el-button v-permission="['GET /admin/store/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/store/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="店铺ID" prop="id" sortable/>
      <el-table-column align="left" label="店铺名称" prop="name" />
      <el-table-column align="center" label="公司名称" prop="comName" />
      <el-table-column align="center" label="航站楼" prop="hangzhanlouName" />
      <el-table-column align="center" label="线下主键" prop="poskey" />
      <el-table-column align="left" label="店铺位置" prop="posdes" />
      <el-table-column align="left" label="门店电话" prop="phone" />
      <el-table-column align="center" label="排序" prop="ordernumber"/>
      <el-table-column align="center" label="店铺类型" prop="ownType">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ownType ? 'success' : 'error' ">{{ scope.row.ownType ? '自营' : '三方' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否启用" prop="enabled">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/store/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/store/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 选择品牌对话框 -->
    <el-dialog :title="'选择品牌'" :close-on-click-modal="false" :visible.sync="dialogSelVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container">

          <el-input v-model="brandQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleBransFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckData">确定添加</el-button>
        </div>
        <!-- 查询结果 -->
          <el-table
            v-loading="listLoading"
            :data="brandList"
            element-loading-text="正在查询中。。。"
            border
            fit
            highlight-current-row
            @selection-change="checkChange">
              <el-table-column type="selection"/>

              <el-table-column align="center" label="品牌ID" prop="id"/>

              <el-table-column align="center" label="品牌编号" prop="sn"/>

              <el-table-column align="center" min-width="100" label="品牌名称" prop="name"/>
          </el-table>
           <pagination v-show="brandTotal>0" :total="brandTotal" :page.sync="brandQuery.page" :limit.sync="brandQuery.limit" @pagination="getBrandList()" />
         </div>

    </el-dialog>


    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" customClass="customWidth" :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">

          <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon  label-width="100px" >
              <el-col :span="12"  >
                <el-form-item label="店铺名称" prop="name">
                  <el-input style="width: 200px" v-model="dataForm.name"/>
                </el-form-item>
              </el-col>
              <el-col :span="12" >
                <el-form-item label="公司名称">
                  <el-select clearable style="width: 200px" v-model="dataForm.comId" @change="changeCompany(dataForm.comId)" >
                    <el-option v-for="item in companyList" :key="item.id" :label="item.name" :value="item.id" />
                  </el-select>
                </el-form-item>
              </el-col>
            <el-col :span="12"   >
              <el-form-item label="航站楼">
                <el-select clearable style="width: 200px" v-model="dataForm.hangzhanlouId"  @change="changeHangZhanLou(dataForm.hangzhanlouId)"  >
                  <el-option v-for="item in hangzhanlouList" :key="item.id" :label="item.hangzhanlouName" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>

              <el-col :span="12" >
                <el-form-item label="店铺类型" prop="type">
                  <el-select clearable style="width: 200px" v-model="dataForm.ownType" placeholder="请选择">
                    <el-option :value="true" label="自营"/>
                    <el-option :value="false" label="三方"/>
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :span="12" >
                <el-form-item label="店铺编码" prop="poskey">
                  <el-input style="width: 200px" v-model="dataForm.poskey"/>
                </el-form-item>
              </el-col>
              <el-col :span="12"  >
                <el-form-item label="店铺位置" prop="posdes">
                  <el-input style="width: 200px" v-model="dataForm.posdes"/>
                </el-form-item>

              </el-col>
              <el-col :span="12"  >
                <el-form-item label="门店电话" prop="posdes">
                  <el-input style="width: 200px" v-model="dataForm.phone"/>
                </el-form-item>
              </el-col>
            <el-col :span="24"  >

              <el-form-item label="营业星期" prop="posdes">
                <!--<el-select  style="width: 550px" v-model="valueCheckWorkWeek" multiple placeholder="请选择">-->
                <el-select  clearable  style="width: 550px" v-model="dataForm.workWeek" multiple placeholder="请选择">
                  <el-option
                    v-for="item in weeks"
                    :key="item.value"
                    :label="item.value"
                    :value="item.value">
                  </el-option>
                </el-select>
                <!--<el-input style="width: 200px" v-model="dataForm.workWeek"/>-->
              </el-form-item>
            </el-col>
              <el-col :span="24"  >
                <el-form-item label="营业时间" prop="posdes" >
                  <!--<el-input style="width: 200px" v-model="dataForm.workTime" placeholder="格式如:9:00-17:00"/>-->
                  <el-time-picker
                    is-range
                    v-model="dataForm.workTime"
                    value-format="HH:mm"
                    range-separator="至"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    placeholder="选择时间范围">
                  </el-time-picker>

                </el-form-item>
              </el-col>
            <el-col :span="24"  >
              <el-form-item label="预约星期" prop="yuyueWeek">
                <el-row style="width: 570px" >
                  <el-select clearable   style="width: 480px" v-model="dataForm.yuyueWeek"   multiple placeholder="请选择">
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
              <el-form-item label="预约时间" prop="yuyueTime"  >
                <el-row style="width: 550px" >
                  <el-time-picker
                    is-range
                    style="width: 400px"
                    value-format="HH:mm"
                    v-model="dataForm.yuyueTime"
                    range-separator="至"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    placeholder="选择时间范围">
                  </el-time-picker>
                  <el-button style="width: 130px" @click="setDefaultTime">设置默认</el-button>
                </el-row>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="预约时间描述" prop="yuyueTimeDesc">
                <el-input style="width: 200px" v-model="dataForm.yuyueTimeDesc"/>
              </el-form-item>
            </el-col>
              <el-col :span="12"   >
                <el-form-item label="是否启用" prop="enabled">
                  <el-select clearable style="width: 200px" v-model="dataForm.enabled" placeholder="请选择">
                    <el-option :value="true" label="启用"/>
                    <el-option :value="false" label="不启用"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12" >
                <el-form-item label="排序" prop="ordernumber">
                  <el-input style="width: 200px" v-model="dataForm.ordernumber"/>
                </el-form-item>
              </el-col>
              <el-col :span="12"  >
                <el-form-item label="备注" prop="remark">
                  <el-input style="width: 200px" v-model="dataForm.remark"/>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="店铺介绍" style="width:700px" >
                  <editor :init="editorInit"  v-model="dataForm.storedesc"/>
                </el-form-item>
              </el-col>
         </el-form>
        </el-card>

        <el-card class="box-card">

          <div class="hot-header">
            <h3 class="title common-title left" >经营品牌</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini"   type="primary" @click="handleBrandShow">添加</el-button>
            </div>
          </div>

          <el-table :data="storeBrandList">

            <el-table-column property="brandName" label="品牌">
            <template slot-scope="scope">
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.brandName}}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.brandName}}
              </slot>
            </template>
            </el-table-column>

            <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <!--<el-button type="danger" size="mini" @click="handleBrandDelete(scope.row)">删除</el-button>-->
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleBrandDelete(scope.row)">删除</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
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
  .hot-header{
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-between;
  }
  .left {
    height: 30px;
  }
  .right {
    height: 30px;
    line-height: 30px;
    font-size: 14px;
    color: #9E9E9E;
  }
  .icon {
    margin-left: 8px;
  }
  .customWidth {
    width: 850px;
  }
</style>

<script>
import { allCompany } from '@/api/company'
import { listStore, createStore, updateStore, deleteStore,readStore,listStoreBrands } from '@/api/store'
import { listCatAndBrand } from '@/api/goods'
import { listBrand } from '@/api/brand'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Editor from '@tinymce/tinymce-vue'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Store',
  components: { Pagination, Editor },

  data() {
    return {
      weeks: [{value: '周一',label: '周一'}, {value: '周二',label: '周二'}, {value: '周三',label: '周三'},
        {value: '周四',label: '周四'}, {value: '周五',label: '周五'},{value: '周六',label: '周六'}, {value: '周日',label: '周日'}
       ],
      valueCheckWorkWeek: [],
      // valueCheckWorkTime:['14:58:54','15:55:54'],
      valueCheckYuyueWeek: [],
      // valueCheckYuyueTime:['14:58:54','15:55:54'],
      uploadPath,
      list: [],

      storeBrandList:[],
      brandList:[],
      brandTotal: 0, // 可推荐的数据列表总计
      checkBoxData: [], // 选择的品牌数据
      postSaveData: undefined, // 要提交的选择数据

      total: 0,
      companyList: [],
      hangzhanlouList:[],
      listLoading: true,
      // 可推荐列表查询
      brandQuery: {
        page: 1,
        limit: 10,
        sn: undefined,
        name: undefined,
        sort: 'add_time desc',

      },
      listQuery: {
        page: 1,
        limit: 20,
        comId:undefined,
        name: undefined,
        sort: 'add_time desc',

      },
      dataForm: {
        id: undefined,
        comId: undefined,
        comName: undefined,
        hangzhanlouId: undefined,
        hangzhanlouName: undefined,
        ownType: true,
        poskey: undefined,
        name: undefined,
        posdes: undefined,
        workWeek: [],
        workTime: [],
        yuyueWeek:['周一', '周二','周三','周四','周五','周六','周日'],
        yuyueTime:  ['09:00','17:00'],
        yuyueTimeDesc:undefined,
        ordernumber: 0,
        remark: undefined,
        enabled: true,
        storedesc: undefined
      },
      dialogSelVisible:false,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },

      rules: {
        name: [
          { required: true, message: '店铺名称不能为空', trigger: 'blur' }
        ],
        poskey: [
            { required: true, message: '店铺编码不能为空', trigger: 'blur' }
        ],
        yuyueWeek: [
          { required: true, message: '预约星期不能为空', trigger: 'blur' }
        ],
        yuyueTime: [
          { required: true, message: '预约时间不能为空', trigger: 'blur' }
        ]
      },
      downloadLoading: false,
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
    this.init()

    this.getList()
  },
  methods: {
      //获取分类和品牌
    getCatL1(){
        listCatAndBrand(this.listQuery.comId).then(response => {
            this.categoryList = response.data.data.categoryList
            this.brandList = response.data.data.brandList
        })
    },
    setDefaultTime(){
      this.dataForm.yuyueTime=['09:00','17:00']
    },
    setDefaultWeeks(){
      this.dataForm.yuyueWeek=['周一', '周二','周三','周四','周五','周六','周日']
    },
    init: function() {
      allCompany().then(response => {
        this.companyList = response.data.data.list
      })
    },
    changeCompany(comId) {
      const obj = this.companyList.find((item) => {
        return item.id === comId
      })
      this.dataForm.comName = obj.name
      this.dataForm.hangzhanlouId=null
      this.hangzhanlouList=[];

    },
    changeHangZhanLou(hangzhanlouId) {
      const obj = this.hangzhanlouList.find((item) => {
        return item.id === hangzhanlouId
      })
      this.dataForm.hangzhanlouName=obj.hangzhanlouName
    },

    getList() {
      this.listLoading = true
      listStore(this.listQuery)
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
        comId: undefined,
        comName: undefined,
        hangzhanlouId: undefined,
        hangzhanlouName: undefined,
        ownType: true,
        poskey: undefined,
        name: undefined,
        posdes: undefined,
        workWeek: [],
        workTime: undefined,
        yuyueWeek:['周一', '周二','周三','周四','周五','周六','周日'],
        yuyueTime:  ['09:00','17:00'],
        yuyueTimeDesc:undefined,
        ordernumber: 0,
        remark: undefined,
        enabled: true,
        storedesc: undefined
      }
      this.storeBrandList=[];
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
          const store = {
            store: this.dataForm,
            storeBrands: this.storeBrandList
          }
          createStore(store)
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
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.storeBrandList=[];
      this.getListStoreBrands()
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    getListStoreBrands(){
        if(this.dataForm.id!=null){
          this.downloadLoading = true
          readStore(this.dataForm.id).then(
            (response)=>{
              this.dataForm=response.data.data.store;
              this.storeBrandList=response.data.data.storebrands;
              this.listLoading = false
            }
          ).catch(() => {
            this.storeBrandList = []
            this.listLoading = false
          })
        }
      },


    updateData(){
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          const store = {
            store: this.dataForm,
            storeBrands: this.storeBrandList
          }
          updateStore(store)
            .then(() => {
              this.dialogFormVisible = false
              this.$message.success('更新店铺信息成功');

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

      deleteStore(row)
        .then(response => {
          this.$message.success('删除成功');

          //this.getList()
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    },
    handleDownload() {
      this.downloadLoading = true
      this.$set(this.listQuery, "limit", 999999)
      listStore(this.listQuery)
        .then(response => {
          var exportList = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['店铺ID', '所属商贸类型名称', '航站楼位置', '线下关联主键', '店铺名称','店铺位置','门店电话',
              '营业时间','营业星期','预约时间','预约星期','排序', '店铺类型', '是否启用']
            const filterVal = ['id', 'comName', 'hangzhanlouName', 'poskey', 'name', 'posdes','phone',
              'workTime','workWeek','yuyueTime','yuyueWeek','ordernumber', 'ownType', 'enabled']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '店铺信息'
            )
            this.downloadLoading = false
          })
          this.downloadLoading = false
          this.$set(this.listQuery, "limit", 20)
        })
      .catch(() => {
          this.downloadLoading = false
          this.$set(this.listQuery, "limit", 20)
      })
    },

    //***************************************************************
    checkChange(val) {
      this.checkBoxData = val
    },

    /**
     * 获取检索的品牌列表
     */
    getBrandList() {
      this.listLoading = true
      listBrand({
        page: 1,
        limit: 20,
        sn: this.brandQuery.sn,
        name: this.brandQuery.name,
        sort: 'add_time desc',

      }).then(response => {
        this.brandList = response.data.data.list
        this.brandTotal = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.brandList = []
        this.brandTotal = 0
        this.listLoading = false
      })
    },
      addCheckData() {
        for (let selItem of  this.checkBoxData) {
          let boo = false;
          for (let storeBrand of  this.storeBrandList) {
            if (selItem.id == storeBrand.brandId) {
              boo = true;
              break;
            }
          }
          if (boo == false) {
            this.storeBrandList.push({
              storeId: this.dataForm.id,
              storeName: this.dataForm.name,
              brandId: selItem.id,
              brandName: selItem.name
            });
          }
        }
        this.dialogSelVisible=false
      },

    /**
     * 进行检索
     */
    handleBransFilter() {
      this.brandQuery.page = 1
      this.getBrandList()
    },

    handleBrandDelete(row) {
      const index = this.storeBrandList.indexOf(row)
      if (row.id == null) {
        this.storeBrandList.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },
    handleBrandShow() {
      this.dialogSelVisible = true
      this.brandQuery.page = 1
      this.getBrandList()
    },
  }
}
</script>
