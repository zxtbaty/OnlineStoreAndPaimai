<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入出品人名称"/>

      <el-button v-permission="['GET /admin/authororcorp/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/authororcorp/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="出品人类型" prop="typeName"/>

      <el-table-column align="center" label="出品人名称" prop="name"/>

      <el-table-column align="center" label="联系人" prop="contactPerson"/>

      <el-table-column align="center" label="联系电话" prop="telphone"/>

      <el-table-column align="center" label="是否启用" prop="enabled">
        <template slot-scope="scope">
          <el-tag >{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/authororcorp/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/authororcorp/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
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
          label-position="left"
          label-width="100px"
          >
          <el-col :span="12" >
            <el-form-item label="出品人类型">
              <el-select style="width: 200px" clearable v-model="dataForm.typeCode" @change="changeType()">
                <el-option  v-for="item in userClass1" :key="item.code"
                            :label="item.name" :value="item.code"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="出品人名称" prop="name">
              <el-input style="width: 200px" v-model="dataForm.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"   >
            <el-form-item label="出品人简介" prop="desc">
              <el-input type="textarea" :rows="2" style="width: 550px" v-model="dataForm.desc"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="电话" prop="telphone">
              <el-input style="width: 200px" v-model="dataForm.telphone"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="电子邮件" prop="email">
              <el-input style="width: 200px" v-model="dataForm.email"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="联系人" prop="contactPerson">
              <el-input style="width: 200px" v-model="dataForm.contactPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="地址" prop="address">
              <el-input style="width: 200px" v-model="dataForm.address"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
          <el-form-item label="出品人代表作(375*364)">
            <el-upload
              :action="uploadPath"
              :headers="headers"
              :file-list="galleryFileList"
              :on-success="handleGalleryUrl"
              :on-remove="handleRemove"
              :limit="5"
              style="width:800px"
              multiple
              accept=".jpg,.jpeg,.png,.gif"
              list-type="picture-card">
              <i class="el-icon-plus"/>
<!--            <el-upload-->
<!--              :action="uploadPath"-->
<!--              :headers="headers"-->
<!--              :limit="5"-->
<!--              :file-list="galleryFileList"-->
<!--              :on-exceed="uploadOverrun"-->
<!--              :on-success="handleGalleryUrl"-->
<!--              :on-remove="handleRemove"-->
<!--              multiple-->
<!--              accept=".jpg,.jpeg,.png,.gif"-->
<!--              list-type="picture-card">-->
<!--              <i class="el-icon-plus"/>-->
              <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
            </el-upload>
          </el-form-item>
          </el-col>

          <el-col :span="12" >
            <el-form-item label="国家地区" prop="countryName">
              <el-input style="width: 200px" v-model="dataForm.countryName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="社会信用代码" prop="code">
              <el-input style="width: 200px" v-model="dataForm.code"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="简称" prop="jianName">
              <el-input style="width: 200px" v-model="dataForm.jianName"/>
            </el-form-item>
          </el-col>


          <el-col :span="12">
            <el-form-item label="微信号" prop="wx">
              <el-input style="width: 200px" v-model="dataForm.wx"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="QQ号" prop="qq">
              <el-input style="width: 200px" v-model="dataForm.qq"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微博主页" prop="weiboSite">
              <el-input style="width: 200px" v-model="dataForm.weiboSite"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" >
            <el-form-item label="开户行" prop="bank">
              <el-input style="width: 200px" v-model="dataForm.bank"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行帐号" prop="bankAccount">
              <el-input style="width: 200px" v-model="dataForm.bankAccount"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="银行户名" prop="bankAccountName">
              <el-input style="width: 200px" v-model="dataForm.bankAccountName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="网址" prop="webSite">
              <el-input style="width: 200px" v-model="dataForm.webSite"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="法人代表" prop="faren">
              <el-input style="width: 200px" v-model="dataForm.faren"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="身份证号" prop="farenIdCard">
              <el-input style="width: 200px" v-model="dataForm.farenIdCard"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" >
            <el-form-item label="排序" prop="orderNumber">
              <el-input style="width: 200px" v-model="dataForm.orderNumber"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="启动标记" prop="enabled">
              <el-radio-group style="width: 200px" v-model="dataForm.enabled">
                <el-radio :label="true">起用</el-radio>
                <el-radio :label="false">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-form>
        </el-card>

        <el-card class="box-card">
          <h3>供应商参数</h3>
          <el-button :plain="true" type="primary" @click="handleAttributeShow">添加</el-button>
          <el-table :data="attributes">
            <el-table-column property="attribute" label="供应商参数名称">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.attribute}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.attribute}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="供应商参数值">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.value}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.value}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="排序">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.ordernumber}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.ordernumber}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button  type="primary" size="mini" @click="handleAttributeEdit(scope.row)">修改</el-button>
                <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>


        <el-card class="box-card">
          <h3>提供的私人定制项目</h3>
          <el-button :plain="true" type="primary" @click="handleItemsShow">添加</el-button>
          <el-table :data="items">
            <el-table-column property="itemName" label="项目名称">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.itemName}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.itemName}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="项目起价">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.itemMinPrice}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.itemMinPrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="项目定金">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.itemDingMoney}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.itemDingMoney}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="运费金额">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.itemShipCost}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.itemShipCost}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="项目周期(天)">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.itemFinishDays}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.itemFinishDays}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="value" label="排序">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.ordernumber}}</s>
                </slot>
                <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.ordernumber}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button  type="primary" size="mini" @click="handleItemsEdit(scope.row)">修改</el-button>
                <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleItemsDelete(scope.row)">删除</el-button>
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
    <el-dialog :visible.sync="attributeVisiable" title="设置供应商参数" :close-on-click-modal="false">
    <el-form ref="attributeForm" :model="attributeForm" status-icon label-position="left" label-width="100px" >
      <el-form-item label="商品参数名称" prop="attribute">
        <el-input v-model="attributeForm.attribute"/>
      </el-form-item>
      <el-form-item label="商品参数值" prop="value">
        <el-input v-model="attributeForm.value"/>
      </el-form-item>
      <el-form-item label="排序" prop="ordernumber">
        <el-input v-model="attributeForm.ordernumber"/>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="attributeVisiable = false">取消</el-button>
      <el-button type="primary" @click="handleAttributeAdd">确定</el-button>
    </div>
  </el-dialog>
    <el-dialog :visible.sync="itemsVisiable" title="设置供应商定制项目" :close-on-click-modal="false">
      <el-form ref="itemForm" :model="itemsForm" status-icon label-position="left" label-width="100px" >
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="itemsForm.itemName"/>
        </el-form-item>
        <el-form-item label="项目起价" prop="itemMinPrice">
          <el-input v-model="itemsForm.itemMinPrice"/>
        </el-form-item>
        <el-form-item label="项目定金" prop="itemDingMoney">
          <el-input v-model="itemsForm.itemDingMoney"/>
        </el-form-item>
        <el-form-item label="项目运费" prop="itemShipCost">
          <el-input v-model="itemsForm.itemShipCost"/>
        </el-form-item>
        <el-form-item label="项目周期(天)" prop="itemFinishDays">
          <el-input v-model="itemsForm.itemFinishDays"/>
        </el-form-item>
        <el-form-item label="排序" prop="ordernumber">
          <el-input v-model="itemsForm.ordernumber"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="itemsVisiable = false">取消</el-button>
        <el-button type="primary" @click="handleItemsAdd">确定</el-button>
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
import { listAuthororcorp, createAuthororcorp, updateAuthororcorp, deleteAuthororcorp,readAuthororcorp } from '@/api/authororcorp'
import { getToken } from '@/utils/auth'
import  Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import {listDiccode} from '@/api/diccode'
import { createStorage, uploadPath } from '@/api/storage'
export default {
  name: 'Authororcorp',
  components: { Pagination },
  data() {
    return {
      uploadPath,
      videoFlag:false,
      videoUploadPercent:0,
      list: [],
      attributes:[],
      attributeForm: { attribute: '', value: '' },
      items:[],
      itemsForm: { itemName: '', itemMinPrice: '',itemDingMoney: '',itemShipCost:'0',itemFinishDays:'',ordernumber: '' },
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        sort: 'order_number asc',
      },
      userClass1: [],
      galleryFileList: [],
      dataForm: {
        id: undefined,
        typeCode: undefined,
        typeName: undefined,
        name: undefined,
        telphone: undefined,
        email: undefined,
        gallery: [],
        countryId:undefined,
        countryName:undefined,
        code:undefined,
        jianName:undefined,
        desc:undefined,
        address:undefined,
        contactPerson:undefined,
        wx:undefined,
        qq:undefined,
        weiboSite:undefined,
        bank:undefined,
        bankAccount:undefined,
        bankAccountName:undefined,
        webSite:undefined,
        faren:undefined,
        farenIdCard:undefined,
        province:undefined,
        city:undefined,
        xian:undefined,
        orderNumber:undefined,
        enabled: true
      },
      dialogFormVisible: false,
      attributeVisiable:false,
      itemsVisiable:false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '出品人名称不能为空', trigger: 'blur' }
        ],


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
    this.getList();
    this.getUserClass1();
  },

  methods: {
      handleItemsShow() {
          this.itemsForm = {}
          this.itemsVisiable = true
      },
      handleItemsEdit(row) {
          this.itemsForm = Object.assign({}, row);
          this.itemsVisiable = true

      },
      handleItemsDelete(row) {
          if(row.id==null) {
              const index = this.items.indexOf(row)
              this.items.splice(index, 1)
          } else
          {
              row.deleted=1
          }
      },
      handleItemsAdd() {
          let obj = this.items.find((item) => {
              return item.itemName == this.itemsForm.itemName
          })
          if(obj==undefined|| obj.itemName==null){
              this.items.unshift(this.itemsForm)
          } else
          {
              obj.itemName=this.itemsForm.itemName;
              obj.itemMinPrice=this.itemsForm.itemMinPrice;
              obj.itemDingMoney=this.itemsForm.itemDingMoney;
              obj.itemShipCost=this.itemsForm.itemShipCost;
              obj.ordernumber=this.itemsForm.ordernumber;
              //this.attributes.splice(index,1,this.attributeForm)
          }
          this.$forceUpdate();
          this.itemsVisiable = false
      },
    handleAttributeShow() {
        this.attributeForm = {}
        this.attributeVisiable = true
    },
    handleAttributeEdit(row) {
        this.attributeForm = Object.assign({}, row);
        this.attributeVisiable = true

    },
    handleAttributeDelete(row) {
        if(row.id==null) {
            const index = this.attributes.indexOf(row)
            this.attributes.splice(index, 1)
        } else
        {
            row.deleted=1
        }
    },

    handleAttributeAdd() {
        let obj = this.attributes.find((item) => {
            return item.attribute == this.attributeForm.attribute
        })
        if(obj==undefined||obj.attribute==null){
            this.attributes.unshift(this.attributeForm)
        } else
        {
            obj.attribute=this.attributeForm.attribute;
            obj.value=this.attributeForm.value;
            obj.ordernumber=this.attributeForm.ordernumber;
            //this.attributes.splice(index,1,this.attributeForm)
        }
        this.$forceUpdate();
        this.attributeVisiable = false
    },



    handleRemove: function(file, fileList) {
        for (var i = 0; i < this.dataForm.gallery.length; i++) {
            // 这里存在两种情况
            // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
            //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
            // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
            var url
            if (file.response === undefined) {
                url = file.url
            } else {
                url = file.response.data.url
            }

            if (this.dataForm.gallery[i] === url) {
                this.dataForm.gallery.splice(i, 1)
            }
        }
    },
    uploadVideoProcess(event, file, fileList) {
        this.videoFlag = true;
        this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
    },
    handleGalleryUrl(response, file, fileList) {
        this.videoFlag = false;
        this.videoUploadPercent = 0;
        if (response.errno === 0) {
            if(typeof(this.dataForm["gallery"])=="undefined"){
                this.dataForm["gallery"]=[];
            }

            this.dataForm.gallery.push(response.data.url)
        }
    },
    getUserClass1() {
        listDiccode({dicmainName: "出品人类型"}).then(response => {
            this.userClass1 = response.data.data.list
        }).catch(() => {
            this.userClass1 = []
        })
    },
    changeType() {
        if (this.userClass1 == null) {
            return
        }
        this.dataForm.class1Name = null;
        // 晕，搞了好长时间，重新赋值，才会刷新页面
        const b = this.userClass1;
        this.userClass1 = [];
        this.userClass1 = b;
        const obj = this.userClass1.find((item) => {
            return item.code == this.dataForm.typeCode
        })
        this.dataForm.typeName = obj.name;

        // this.$forceUpdate();

    },


    getList() {
      this.listLoading = true
        listAuthororcorp(this.listQuery)
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
          typeCode: undefined,
          typeName: undefined,
          name: undefined,
          desc:undefined,
          telphone: undefined,
          email: undefined,
          gallery: [],
          countryId:undefined,
          countryName:undefined,
          code:undefined,
          jianName:undefined,
          address:undefined,
          contactPerson:undefined,
          wx:undefined,
          qq:undefined,
          weiboSite:undefined,
          bank:undefined,
          bankAccount:undefined,
          bankAccountName:undefined,
          webSite:undefined,
          faren:undefined,
          farenIdCard:undefined,
          province:undefined,
          city:undefined,
          xian:undefined,
          orderNumber:undefined,
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

    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
            const authororcorpAllinone = {
                authororcorp: this.dataForm,
                authororcorpParas: this.attributes,
                authororcorpItems: this.items,
            }
            createAuthororcorp(authororcorpAllinone)
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
      readAuthororcorp(this.dataForm.id).then(
        (response)=>{
          this.dataForm=response.data.data.authororcorp;
          this.attributes=response.data.data.paras;
          this.items=response.data.data.items;

          this.galleryFileList = []
          for (var i = 0; i < this.dataForm.gallery.length; i++) {
              this.galleryFileList.push({
                  url: this.dataForm.gallery[i]
              })
          }
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
            const authororcorpAllinone = {
                authororcorp: this.dataForm,
                authororcorpParas: this.attributes,
                authororcorpItems: this.items,
            }
            updateAuthororcorp(authororcorpAllinone)
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
        deleteAuthororcorp(row)
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
          '出品人名称',
          '出品人类型',
          '电话',
          '电子邮件',
          '国家地区',
          '是否启用'
        ]
        const filterVal = [
          'name',
          'typeName',
          'telphone',
          'email',
          'countryName',
          'enabled'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '出品人列表')
        this.downloadLoading = false
      })
    }
  }
}
</script>
