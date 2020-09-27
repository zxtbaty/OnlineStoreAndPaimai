<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入公司名称"/>
      <el-button v-permission="['GET /admin/company/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/company/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="公司ID" prop="id" sortable/>

      <el-table-column align="left" label="公司名称" prop="name" />

      <el-table-column align="center" label="排序" prop="ordernumber" sortable/>

      <el-table-column align="center" label="是否自营" prop="owned" >
        <template slot-scope="scope">
          <el-tag :type="scope.row.owned ? 'success' : 'error' ">{{ scope.row.owned ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否启用" prop="enabled">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/company/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/company/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
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
    <el-dialog :title="textMap[dialogStatus]"  width="900px"
               :before-close="closeAndReturn"
               :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-card class="box-card">
        <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px"  >
<!--          <el-col :span="24" style="padding: 0;margin: 0"  >-->
<!--            <el-form-item label="上级公司"  >-->
<!--              &lt;!&ndash;<el-input v-model="form.parentId" auto-complete="off"></el-input>&ndash;&gt;-->
<!--              <el-select-tree v-model="dataForm.pid" :treeData="list" :propNames="defaultProps" clearable-->
<!--                              placeholder="请选择上级公司">-->
<!--              </el-select-tree>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
          <el-col :span="12" style="padding: 0;margin: 0"  >
            <el-form-item label="公司编号"  prop="poskey">
              <el-input style="width: 200px" v-model="dataForm.poskey"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"   >
            <el-form-item label="公司名称" prop="name">
              <el-input style="width: 200px" v-model="dataForm.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="公司简介" prop="airportdesc">
              <el-input style="width: 200px" v-model="dataForm.airportdesc"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="AppId" prop="airportdesc">
              <el-input style="width: 200px" v-model="dataForm.appId"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="App密钥" prop="airportdesc">
              <el-input style="width: 200px" v-model="dataForm.appSecret"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="ordernumber">
              <el-input style="width: 200px" v-model="dataForm.ordernumber"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="是否自营" prop="owned">
              <el-select style="width: 200px" clearable v-model="dataForm.owned" placeholder="请选择">
                <el-option :value="true" label="是"/>
                <el-option :value="false" label="否"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="enabled">
              <el-select style="width: 200px" clearable v-model="dataForm.enabled" placeholder="请选择">
                <el-option :value="true" label="启用"/>
                <el-option :value="false" label="不启用"/>
              </el-select>
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
        <el-table :data="companyBrandList">

          <el-table-column property="brandName"  label="品牌">
            <template slot-scope="scope"  >

              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.brandName}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.brandName}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column property="ordernumber"  label="排序">
            <template slot-scope="scope"  >

              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.ordernumber}}

                  <el-input v-model="scope.row.ordernumber"/>

                </s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                <el-input v-model="scope.row.ordernumber" style="width: 150px"/>

              </slot>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <!--<el-button type="danger" size="mini" @click="handleBrandDelete(scope.row)">删除</el-button>-->
              <el-button type="primary" size="mini" @click="exeUpdateCompanyBrand(scope.row)">保存</el-button>
              <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleBrandDelete(scope.row)">删除</el-button>
              <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>

            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="box-card">
        <div class="hot-header">
          <h3 class="title common-title left" >公司取货点设置</h3>
          <div class="right" style="padding-top: 15px">
            <el-button  size="mini"   type="primary" @click="handleHangzhanlouShow">添加</el-button>
            <el-button v-show="!dialogHangzhanlouFormVisible"     size="mini"   type="primary" @click="dialogHangzhanlouFormVisible = true">显示</el-button>
            <el-button v-show="dialogHangzhanlouFormVisible"    size="mini"  type="primary" @click="dialogHangzhanlouFormVisible = false">隐藏</el-button>
          </div>
        </div>

        <el-card v-show="dialogHangzhanlouFormVisible">
          <el-form ref="dataHangzhanlouForm"
                   :rules="rules" :model="dataHangzhanlouForm"   status-icon  label-width="100px" >

            <el-form-item label="取货点名称" prop="poskey">
              <el-input style="width: 200px" v-model="dataHangzhanlouForm.hangzhanlouName"/>
            </el-form-item>

            <el-form-item label="排序" prop="ordernumber">
              <el-input style="width: 200px" v-model="dataHangzhanlouForm.ordernumber"/>
            </el-form-item>

          </el-form>
        </el-card>
        <el-table :data="companyHangzhanlous">
          <el-table-column property="hangzhanlouName"  label="取货点名称">
            <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.hangzhanlouName}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.hangzhanlouName}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column property="ordernumber"  label="排序">
            <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.ordernumber}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.ordernumber}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button   type="primary" size="mini" @click="handleHangzhanlouUpdate(scope.row)">编辑</el-button>
              <!--<el-button type="danger" size="mini" @click="handleBrandDelete(scope.row)">删除</el-button>-->
              <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleHangzhanlouDelete(scope.row)">删除</el-button>
              <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>

            </template>
          </el-table-column>
        </el-table>

      </el-card>


      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style scoped>
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
  .el-dialog__body {
    padding: 0px 10px;
    color: #606266;
    font-size: 14px;
  }
</style>

<script>
  import { listCompany, createCompany, updateCompany, deleteCompany,readCompany,listCompanyBrands,updateCompanyBrand } from '@/api/company'
  import { listBrand } from '@/api/brand'
  import { uploadPath } from '@/api/storage'
  import { getToken } from '@/utils/auth'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

  export default {
    name: 'Company',
    components: { Pagination },
    data() {
      return {
        uploadPath,
        list: [],
        companyBrandList:[],
        companyHangzhanlous:[],
        brandList:[],
        brandTotal: 0, // 可推荐的数据列表总计
        total: 0,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20,
          name: undefined,
          sort: 'add_time desc',

        },
        // 可推荐列表查询
        brandQuery: {
          page: 1,
          limit: 10,
          sn: undefined,
          name: undefined,
          sort: 'add_time desc',

        },
        brandTotal: 0, // 可推荐的数据列表总计
        checkBoxData: [], // 选择的品牌数据
        dataForm: {
          id: undefined,
          poskey: undefined,
          name: undefined,
          airportdesc: undefined,
          appId: undefined,
          appSecret: undefined,
          ordernumber: undefined,
          owned: false,
          enabled: true
        },
        dataHangzhanlouForm:{
          id: undefined,
          comId: undefined,
          comName: undefined,
          hangzhanlouName: undefined,
          ordernumber: undefined,
        },
        dialogFormVisible: false,
        dialogSelVisible:false,
        dialogHangzhanlouFormVisible:false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        rules: {
          name: [
            { required: true, message: '公司名称不能为空', trigger: 'blur' }
          ]

        },
        downloadLoading: false,

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
      closeAndReturn(){
        this.dataForm = {owned: false, enabled: true}
        this.companyBrandList=[],
        this.companyHangzhanlous=[],

        this.dialogFormVisible=false;
      },
      exeUpdateCompanyBrand(row) {
        updateCompanyBrand(row).then(response => {
          // this.list.unshift(response.data.data)
          this.$message.success('保存成功');

          this.getList();
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
      },
      getList() {
        this.listLoading = true
        listCompany(this.listQuery)
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
          poskey: undefined,
          name: undefined,
          airportdesc: undefined,
          ordernumber: 0,
          owned: false,
          enabled: true
        };
        this.companyBrandList=[];
        this.companyHangzhanlous=[];
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
            const company = {
              company: this.dataForm,
              companyBrands: this.companyBrandList,
              companyHangzhanlous:this.companyHangzhanlous
            }
            createCompany(company)
              .then(response => {
                this.list.unshift(response.data.data)
                this.dialogFormVisible = false
                this.$message.success('创建成功');
                this.getList();
              })
              .catch(response => {
                this.$message.error( '失败:'+response.data.errmsg);
              })
          }
        })
      },
      handleUpdate(row) {
        this.dataForm = Object.assign({}, row)
        this.dialogStatus = 'update'
        //this.storeBrandList=[];
        this. getListCompanyBrands()
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },

      getListCompanyBrands(){
        if(this.dataForm.id!=null){
          this.listLoading = true
          readCompany(this.dataForm.id).then(
            (response)=>{
              this.dataForm=response.data.data.company
              this.companyBrandList=response.data.data.companyBrands
              this.companyHangzhanlous=response.data.data.companyHangzhanlous
              this.listLoading = false
            }
          ).catch(() => {
            this.companyBrandList = []
            this.companyHangzhanlous=[]
            this.listLoading = false
          })
        }
      },

      updateData() {
        this.$refs['dataForm'].validate(valid => {
          if (valid) {
            const company = {
              company: this.dataForm,
              companyBrands: this.companyBrandList,
              companyHangzhanlous:this.companyHangzhanlous
            }
            updateCompany(company)
              .then(() => {
                for (const v of this.list) {
                  if (v.id === this.dataForm.id) {
                    const index = this.list.indexOf(v)
                    this.list.splice(index, 1, this.dataForm)
                    break
                  }
                }
                this.dialogFormVisible = false
                this.$message.success('更新公司成功');

              })
              .catch(response => {
                this.$message.error( '失败:'+response.data.errmsg);
              })
          }
        })
      },
      handleDelete(row) {

        if(confirm("确实要删除该记录吗？")==false){
          return;
        }
        deleteCompany(row)
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
            '公司ID',
            '线下Pos关联主键',
            '公司名称',
            '机场描述',
            '排序',
            '是否微商城母公司',
            '是否启用'
          ]
          const filterVal = [
            'id',
            'poskey',
            'name',
            'airportdesc',
            'ordernumber',
            'owned',
            'enabled'
          ]
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '公司信息')
          this.downloadLoading = false
        })
      },
      handleBrandShow() {
        this.dialogSelVisible = true
        this.brandQuery.page = 1
        this.getBrandList()

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
      /**
       * 进行检索
       */
      handleBransFilter() {
        this.brandQuery.page = 1
        this.getBrandList()
      },
      addCheckData() {
        for (let selItem of  this.checkBoxData) {
          let boo = false;
          for (let storeBrand of  this.companyBrandList) {
            if (selItem.id == storeBrand.brandId) {
              boo = true;
              break;
            }
          }
          if (boo == false) {
            this.companyBrandList.push({
              comId: this.dataForm.id,
              comName: this.dataForm.name,
              brandId: selItem.id,
              brandName: selItem.name
            });
          }
        }
        this.dialogSelVisible=false
      },
      handleBrandDelete(row) {
        const index = this.companyBrandList.indexOf(row)
        if (row.id == null) {
          this.companyBrandList.splice(index, 1)
        } else {
          row.deleted = 1
        }
      },
      checkChange(val) {
        this.checkBoxData = val
      },

      handleHangzhanlouShow(){
        this.dialogHangzhanlouFormVisible=true;
        this.dataHangzhanlouForm = { }
        this.companyHangzhanlous.push(this.dataHangzhanlouForm)
      },
      handleHangzhanlouUpdate(row){
        //this.goodsRuleUserForm = Object.assign({}, row)
        this.dataHangzhanlouForm=row;
        this.dialogHangzhanlouFormVisible=true;
      },
      handleHangzhanlouDelete(row) {
        const index = this.companyHangzhanlous.indexOf(row)
        if (row.id == null) {
          this.companyHangzhanlous.splice(index, 1)
        } else {
          row.deleted = 1
        }
      },
    }
  }
</script>
