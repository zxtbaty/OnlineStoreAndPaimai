<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select clearable  class="filter-item"  v-model="listQuery.typeCode" style="width:150px;"   placeholder="请选择消息类型">
        <el-option  v-for=" item in messageTypesList" :value="item.code"  :key="item.code" :label="item.name">
        </el-option>
      </el-select>

      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 150px;" placeholder="请输入消息主题"/>

      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/userinfodef/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/userinfodef/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      size="small"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row>
      <el-table-column align="center" label="消息类型" prop="typeName"/>
      <!--<el-table-column align="center" label="商品品类" prop="categoryName"/>-->
      <el-table-column align="center" label="消息主题" prop="title"/>

      <el-table-column align="center" label="生效时间" prop="beginDate"/>
      <el-table-column align="center" label="失效时间" prop="endDate"/>
      <el-table-column align="center" label="过期标志" prop="expireFlag">
        <template slot-scope="scope">
          <el-tag :type="scope.row.expireFlag ? 'success' : 'error' ">{{ scope.row.expireFlag ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="站内提醒" prop="webHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.webHint ? 'success' : 'error' ">{{ scope.row.webHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="短信标志" prop="smsHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.smsHint ? 'success' : 'error' ">{{ scope.row.smsHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="邮件标志" prop="mailHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.mailHint ? 'success' : 'error' ">{{ scope.row.mailHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="弹窗提醒" prop="popHint">
        <template slot-scope="scope">
          <el-tag :type="scope.row.popHint ? 'success' : 'error' ">{{ scope.row.popHint ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="链接网址" prop="dealWebLink"/>

      <el-table-column align="center" label="操作" width="150px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/userinfodef/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/userinfodef/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 选择会员对话框 -->
    <el-dialog :title="'选择会员'" :close-on-click-modal="false" :visible.sync="dialogSelUserVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->

        <div class="filter-container">

          <el-input v-model="userQuery.username" clearable class="filter-item" style="width: 150px;" placeholder="请输入用户名称"/>
          <el-input v-model="userQuery.mobile" clearable class="filter-item" style="width: 150px;" placeholder="请输入手机号"/>
          <el-input v-model="userQuery.userClassAttr1Name" clearable class="filter-item" style="width: 150px;" placeholder="请输入分类属性1"/>
          <el-input v-model="userQuery.userClassAttr2Name" clearable class="filter-item" style="width: 150px;" placeholder="请输入分类属性2"/>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleUserFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckSelUserData">确定添加</el-button>
        </div>
        <!-- 查询结果 -->
        <el-table
          v-loading="listLoading"
          :data="userList"
          element-loading-text="正在查询中。。。"
          border
          fit
          highlight-current-row
          @selection-change="changeSelUserList">

          <el-table-column type="selection"/>

          <el-table-column align="center" label="用户名称" prop="username"/>
          <el-table-column align="center" label="网络名称" prop="nickname"/>
          <el-table-column align="center" label="手机号码" prop="mobile"/>
          <el-table-column align="center" label="微信OpenId" prop="weixinOpenid"/>
          <el-table-column align="center" label="用户分类1名称" prop="userClassAttr1Name"/>
<!--          <el-table-column align="center" label="用户分类2名称" prop="userClassAttr2Name"/>-->

        </el-table>
        <pagination v-show="userTotal>0" :total="userTotal" :page.sync="userQuery.page" :limit.sync="userQuery.limit" @pagination="getUserList()" />
      </div>

    </el-dialog>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" customClass="customWidth" :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm" :rules="rules" :model="dataForm"
                   status-icon  label-width="100px" >
            <el-col :span="12"  >
              <el-form-item label="消息类型" prop="typeCode">
                <el-select clearable    v-model="dataForm.typeCode" style="width:200px;"   placeholder="请选择消息类型" @change="changeInfoType(dataForm.typeCode)" >
                  <el-option  v-for=" item in messageTypesList" :value="item.code"  :key="item.code" :label="item.name">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="消息主题" prop="title" >
                  <el-input style="width: 200px" v-model="dataForm.title"  />
              </el-form-item>
            </el-col>

            <el-col :span="12" >
              <el-form-item label="生效时间" prop="beginDate">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.beginDate"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="失效时间" prop="seckillEndDate">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.endDate"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>


            <el-col :span="12">
              <el-form-item label="站内提醒" prop="webHint">
                <el-radio-group style="width: 200px" v-model="dataForm.webHint">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="短信标志" prop="smsHint">
                <el-radio-group style="width: 200px" v-model="dataForm.smsHint">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="邮件标志" prop="mailHint">
                <el-radio-group style="width: 200px" v-model="dataForm.mailHint">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="弹窗提醒" prop="popHint">
                <el-radio-group style="width: 200px" v-model="dataForm.popHint">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="链接网址" prop="remark">
                <el-input style="width: 200px"   v-model="dataForm.dealWebLink"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label=""  >
                <el-button  type="info"  icon="el-icon-search" @click=" handleDicCodeParams()">查看可用参数列表</el-button>
              </el-form-item>
            </el-col>
            <el-col :span="24"   >
              <el-form-item label="消息内容" >
                <editor :init="editorInit" style="width:600px;height: 500px" v-model="dataForm.content"/>
<!--                <el-form-item label="消息内容" style="width:700px" >-->
<!--                  <textarea class="content" focus="true" style="width:600px;height: 200px" v-model="dataForm.content" maxlength="1023"  />-->
              </el-form-item>
            </el-col>
          </el-form>
        </el-card>


        <el-card class="box-card">
          <div class="hot-header">
            <h3 class="title common-title left" >消息通知会员</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini"   type="primary" @click="handleUserModiShow">添加</el-button>
              <el-button v-show="!modiUserVisiable"     size="mini"   type="primary" @click="handleUserModiDisplay">显示</el-button>
              <el-button v-show="modiUserVisiable"    size="mini"  type="primary" @click="handleUserModiHide">隐藏</el-button>
            </div>
          </div>

          <el-form
            v-show="modiUserVisiable"
            ref="userinfoPubForm"
            :model="userinfoPubForm"
            :rules="userModiRules"
            status-icon

            label-width="100px"
            style=" margin-left:50px;">

              <el-form-item label="全部用户" prop="ifAllVisit">
                <el-radio-group style="width: 400px" v-model="userinfoPubForm.ifAllVisit">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>


              <el-form-item label="全部会员用户" prop="ifAllUser">
                <el-radio-group style="width: 400px" v-model="userinfoPubForm.ifAllUser">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>


            <el-form-item label="会员分类1">
              <el-select clearable v-model="userinfoPubForm.userGroupCode1" style="width:400px;" @change="changeUserClass1" >
                <el-option  v-for=" item in userClass1" :value="item.code"  :key="item.code" :label="item.name">
                </el-option>
                <!--<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>-->
              </el-select>
            </el-form-item>

<!--            <el-form-item label="会员分类2"   prop="name">-->
<!--              <el-select clearable v-model="userinfoPubForm.userGroupCode2" style="width:400px;" @change="changeUserClass2" >-->
<!--                <el-option  v-for=" item in userClass2" :value="item.code"  :key="item.code" :label="item.name">-->
<!--                </el-option>-->
<!--                &lt;!&ndash;<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>&ndash;&gt;-->
<!--              </el-select>-->
<!--            </el-form-item>-->
            <el-form-item label="选择特定会员" prop="goodsSn" >
              <el-input style="width: 400px" v-model="userinfoPubForm.userId"  clearable :readonly="true"  >
                <el-button slot="append" type="primary"  icon="el-icon-search" @click=" handleSelectUser">选择</el-button>
              </el-input>
            </el-form-item>


          </el-form>

          <el-table :data="userinfoPubList">
            <!--<template slot-scope="scope">-->
            <!--<s v-show="scope.row.deleted!=null&&scope.row.deleted==1">-->
            <!--</s>-->
            <!--</template>-->

            <el-table-column align="center" label="全部用户" prop="ifAllVisit">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    <el-tag style="color:red" :type="scope.row.ifAllVisit ? 'success' : 'error' ">{{ scope.row.ifAllVisit ? '是' : '否' }}</el-tag>
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  <el-tag :type="scope.row.ifAllVisit ? 'success' : 'error' ">{{ scope.row.ifAllVisit ? '是' : '否' }}</el-tag>
                </slot>
              </template>
            </el-table-column>
            <el-table-column align="center" label="全部会员" prop="ifAllUser">
              <template slot-scope="scope">
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    <el-tag style="color:red" :type="scope.row.ifAllUser ? 'success' : 'error' ">{{ scope.row.ifAllUser ? '是' : '否' }}</el-tag>
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  <el-tag :type="scope.row.ifAllUser ? 'success' : 'error' ">{{ scope.row.ifAllUser ? '是' : '否' }}</el-tag>
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="userGroupName1" label="分类一名称">
              <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.userGroupName1}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.userGroupName1}}
              </slot>
            </template>
            </el-table-column>
            <el-table-column property="userGroupName2" label="分类二名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userGroupName2}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userGroupName2}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="userName" label="会员名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userName}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userName}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button   type="primary" size="mini" @click="handleUserinfoPubUpdate(scope.row)">编辑</el-button>
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleUserinfoPubDelete(scope.row)">删除</el-button>
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

    <!-- 查看可用变量 -->
    <el-dialog  title="查看可用变量" :close-on-click-modal="false" customClass="customWidth" :visible.sync="dialogListParasVisible"  >
      <el-table :data="diccodes">
        <el-table-column property="code" label="代码编码"/>
        <el-table-column property="name" label="代码名称"/>
        <el-table-column property="ordernumber" label="代码排序"/>
        <el-table-column property="remark" label="代码备注"/>
        <el-table-column property="systemed" label="系统标记" >
          <template slot-scope="scope">
            <el-tag :type="scope.row.systemed ? 'success' : 'error' ">{{ scope.row.systemed ? '系统' : '用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="enabled" label="停用标记">
          <template slot-scope="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>
<style>
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
  .customWidth {
    width: 850px;
  }
</style>
<script>
  import { listUserinfoDef, deleteUserinfoDef, createUserinfoDef, updateUserinfoDef,readUserinfoDef } from '@/api/userinfodef'
  import { listDiccode } from '@/api/diccode'
  import { fetchList } from '@/api/user'
  import Editor from '@tinymce/tinymce-vue'
  import BackToTop from '@/components/BackToTop'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import { createStorage, uploadPath } from '@/api/storage'

  export default {
    name: 'UserInfoDef',
    components: { BackToTop, Pagination, Editor },
    data() {
      return {
        uploadPath,
        modiUserVisiable:false,
        isRouteActive:true,

        userList:undefined,// 可选会员列表
        userTotal: 0, // 可推荐的数据列表总计
        checkBoxSelUserListData:[],//选择的用户数据

        messageTypesList:[],

        list: [],
        diccodes:[],
        userinfoPubList:[],//会员活动用户列表

        userClass1:[],
        userClass2:[],

        total: 0,
        listLoading: true,

        listQuery: {
          page: 1,
          limit: 20,
          typeCode: undefined,
          title:undefined,
          expireFlag:0,
          sort: 'add_time desc',
        },
        // 会员列表查询
        userQuery: {
          page: 1,
          limit: 10,
          username: undefined,
          mobile: undefined,
          userClassAttr1Name: undefined,
          userClassAttr2Name: undefined,
          sort: 'add_time desc',

        },
        downloadLoading: false,
        dataForm: {
          id: undefined,
          typeCode:undefined,
          typeName:undefined,
          title:undefined,
          content:undefined,
          beginDate:undefined,
          endDate:undefined,
          expireFlag:undefined,
          webHint:true,
          smsHint:false,
          mailHint: false,
          popHint: false,
          dealWebLink: undefined,
        },
        userinfoPubForm: {
          id: undefined,
          infoId: undefined,
          ifAllVisit: undefined,
          ifAllUser: undefined,
          userGroupCode1: undefined,
          userGroupName1: undefined,
          userGroupCode2: undefined,
          userGroupName2: undefined,
          userId: undefined,
          userName: undefined,
          ifViewed: undefined,
        },
        dialogFormVisible: false,
        dialogSelUserVisible:false,
        dialogListParasVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        rules: {
          // secKillGoodsId : [{ required: true, message: '商品不能为空', trigger: 'blur' }]
        },
        userModiRules: {
          // name: [{ required: true, message: '代码名称不能为空', trigger: 'blur' }]
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
        }
      }
    },
    created() {
      this.userinfoPubList=[];
      this.getMessageTypesList();
      this.getUserClass1()
      this.getUserClass2()
      this.getList()
    },
    activated(){
      this.resetForm();
    },
    methods: {
      handleDicCodeParams(){
        this.getDicCodeParamsList();
        this.dialogListParasVisible=true;
      },
      getDicCodeParamsList(){
        listDiccode({dicmainName:"内置消息字段"}).then(response => {
          this.diccodes = response.data.data.list
        }).catch(() => {
          this.diccodes = []
        })
      },
      getMessageTypesList(){
        listDiccode({dicmainName:"前端消息_类型"}).then(response => {
          this.messageTypesList = response.data.data.list
        }).catch(() => {
          this.messageTypesList = []
        })
      },
      changeInfoType(typeCode){

        const obj = this.messageTypesList.find((item) => {
          return item.code === typeCode
        })
        this.dataForm.typeName=obj.name

      },
      getUserClass1(){
        listDiccode({dicmainName:"会员分类1"}).then(response => {
          this.userClass1 = response.data.data.list
        }).catch(() => {
          this.userClass1 = []
        })
      },
      getUserClass2(){
        listDiccode({dicmainName:"会员分类2"}).then(response => {
          this.userClass2= response.data.data.list
        }).catch(() => {
          this.userClass2 = []
        })
      },
      getList() {
        this.listLoading = true
        listUserinfoDef(this.listQuery).then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        }).catch(() => {
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
          typeCode:undefined,
          typeName:undefined,
          title:undefined,
          content:"",
          beginDate:undefined,
          endDate:undefined,
          expireFlag:undefined,
          webHint:true,
          smsHint:false,
          mailHint: false,
          popHint: false,
          dealWebLink: undefined,
        };
        this.userinfoPubForm={
              id: undefined,
              infoId: undefined,
              ifAllVisit: undefined,
              ifAllUser: undefined,
              userGroupCode1: undefined,
              userGroupName1: undefined,
              userGroupCode2: undefined,
              userGroupName2: undefined,
              userId: undefined,
              userName: undefined,
              ifViewed: undefined,
          }

      },
      handleCreate() {
        this.userinfoPubList=[];
        this.resetForm()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const userInfoDef = {
              userinfoDef: this.dataForm,
              userinfoPubs: this.userinfoPubList
            }
            createUserinfoDef(userInfoDef).then(response => {
              // this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$message.success('创建成功');

              this.goodsProduct=[];
              this.getList()
            }).catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
          }
        })
      },
      handleUpdate(row) {
        this.dataForm = Object.assign({}, row)

        this.getUserinfoDefDetail();
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const userInfoDef = {
              userinfoDef: this.dataForm,
              userinfoPubs: this.userinfoPubList
            }
            updateUserinfoDef(userInfoDef).then(() => {
              this.dialogFormVisible = false
              this.$message.success('更新成功');
              this.getList();
            }).catch(response => {
              //this.$message.error( '失败:'+response.data.errmsg);

            })
          }
        })
      },
      handleDelete(row) {
        if(confirm('确实要删除当前记录吗?')==false){
          return
        }
        deleteUserinfoDef(row).then(response => {
          this.$message.success('删除会员消息定义成功');

          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        }).catch(response => {
          //this.$message.error( '失败:'+response.data.errmsg);
        })
      },


      handleUserFilter () {
        this.userQuery.page = 1
        this.getUserList()
      },
      getUserList(){
        this.listLoading = true
        fetchList(this.userQuery).then(response => {
          this.userList = response.data.data.list
          this.userTotal = response.data.data.total
          this.listLoading = false
        }).catch(() => {
          this.userList = []
          this.userTotal = 0
          this.listLoading = false
        })
      },
      handleUserModiShow() {
        this.modiUserVisiable = true
        this.userinfoPubForm = { ifAllVisit:false,ifAllUser:false }
        this.userinfoPubList.push(this.userinfoPubForm)
      },
      handleUserModiDisplay() {
        this.modiUserVisiable = true
      },
      handleUserModiHide() {
        this.modiUserVisiable = false
      },
      handleUserinfoPubDelete(row) {
        const index = this.userinfoPubList.indexOf(row)
        if (row.id == null) {
          this.userinfoPubList.splice(index, 1)
        } else {
          row.deleted = 1
        }
      },
      changeUserClass1(){

        if(this.userClass1==null){
          return
        }
        this.userinfoPubForm.userGroupName1=null;
        // 晕，搞了好长时间，重新赋值，才会刷新页面
        const b =this.userClass1;
        this.userClass1=[];
        this.userClass1=b;
        const obj = this.userClass1.find((item) => {
          return item.code == this.userinfoPubForm.userGroupCode1
        })
        this.userinfoPubForm.userGroupName1 = obj.name

      },
      changeUserClass2(){
        if(this.userClass2==null){
          return
        }
        this.userinfoPubForm.userGroupName2=null
        const obj = this.userClass2.find((item) => {
          return item.code == this.userinfoPubForm.userGroupCode2
        })
        this.userinfoPubForm.userGroupName2 = obj.name
      },
      handleUserinfoPubUpdate(row){
        //this.userinfoPubForm = Object.assign({}, row)
        this.userinfoPubForm=row;
        this.modiUserVisiable=true;
      },
      handleSelectUser(){
        this.dialogSelUserVisible=true;
        this.handleUserFilter()
      },

      getUserinfoDefDetail(){
        if(this.dataForm.id==null){
          this.dataForm = { onlyOne: true, expireFlag: false }
          this.userinfoPubList = []
        } else {
          readUserinfoDef(this.dataForm.id).then(response => {
            this.dataForm = response.data.data.userinfoDef
            this.userinfoPubList = response.data.data.userinfoPubs
          })
        }
      },
      changeSelUserList(val){
        this.checkBoxSelUserListData = val
      },

      addCheckSelUserData() {
        if(this.userinfoPubForm.userClassAttr1Code==null&&this.userinfoPubForm.userClassAttr2Code==null&&this.userinfoPubForm.userId==null){
          const index = this.userinfoPubList.indexOf(this.userinfoPubForm)

          this.userinfoPubList.splice(index,1)
          this.userinfoPubForm={}
        }
        for (let selItem of  this.checkBoxSelUserListData) {
          let boo = false;
          for (let user of  this.userinfoPubList) {
            if (selItem.id == user.userId) {
              boo = true;
              break;
            }
          }
          if (boo == false) {
            this.userinfoPubList.push({
              infoId:this.dataForm.id,
              userId: selItem.id,
              userName: selItem.username,
              userGroupCode1: selItem.userClassAttr1Code,
              userGroupName1: selItem.userClassAttr1Name,
              userGroupId2: selItem.userClassAttr2Code,
              userGroupName2: selItem.userClassAttr2Name,
            });
          }
        }
        this.dialogSelUserVisible=false
      },

    },

  }
</script>
