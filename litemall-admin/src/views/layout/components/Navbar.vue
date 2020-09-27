<template>

 <div >
    <div class="navbar">
      <hamburger :toggle-click="toggleSideBar" :is-active="sidebar.opened" class="hamburger-container"/>

      <breadcrumb class="breadcrumb-container"/>

      <div class="right-menu">

        <slot >
          <el-badge :value="yuyueCountInfo"  style="margin-right: 10px" title="预约备货"  >
            <i style="cursor: pointer;font-size: small"  @click="linkYuyue()" > 预约备货</i>
          </el-badge>
        </slot>
        <slot>
          <el-badge :value="beihuoCountInfo_KuaiDi"  style="margin-right: 10px" title="快递备货"   >
            <i style="cursor: pointer;font-size: small"  @click="linkBeiHuo_KuaiDi()" >快递备货</i>
          </el-badge>
        </slot>
        <slot  >
          <el-badge :value="beihuoCountInfo_HangZhanLou"  style="margin-right: 10px" title="自提备货"   >
            <i style="cursor: pointer;font-size: small"  @click="linkBeiHuo_HangZhanLou()" >自提备货</i>
          </el-badge>
        </slot>
        <slot >
          <el-badge :value="kaiPiaoCountInfo"  style="margin-right: 10px" title="待开票"  >
            <i style="cursor: pointer;font-size: small"  @click="linkKaiPiao()" >待开票</i>
          </el-badge>
        </slot>
        <slot >
          <el-badge :value="tuikuanCountInfo"  style="margin-right: 10px" title="待退款"  >
            <i style="cursor: pointer;font-size: small"  @click="linkTuiKuan()" >待退款</i>
          </el-badge>
        </slot>

        <template v-if="device!=='mobile'">
          <el-tooltip content="全屏" effect="dark" placement="bottom">
            <screenfull class="screenfull right-menu-item"/>
          </el-tooltip>

          <el-tooltip content="布局大小" effect="dark" placement="bottom">
            <size-select class="international right-menu-item"/>
          </el-tooltip>

        </template>

        <el-dropdown class="avatar-container right-menu-item" trigger="click">
          <div class="avatar-wrapper">
            <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
            <i class="el-icon-caret-bottom"/>
          </div>
          <el-dropdown-menu slot="dropdown">
            <router-link to="/">
              <el-dropdown-item>
                首页
              </el-dropdown-item>
            </router-link>
            <!--<el-dropdown-item divided>-->
              <!--<a target="_blank" href="https://github.com/jinyuanjava/litemall">-->
                <!--GitHub-->
              <!--</a>-->
            <!--</el-dropdown-item>-->
            <!--<el-dropdown-item>-->
              <!--<a target="_blank" href="https://gitee.com/jinyuanjava/litemall">-->
                <!--码云-->
              <!--</a>-->
            <!--</el-dropdown-item>-->
            <el-dropdown-item divided>
              <router-link to="/profile/password">
                密码修改
              </router-link>
            </el-dropdown-item>
            <el-dropdown-item divided>
              <span style="display:block;" @click="logout">退出</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>



      </div>

    </div>

      <!-- 添加或修改对话框 -->
      <el-dialog :title="'查看后端消息详情'"  customClass="customWidth"  :close-on-click-modal="false" :visible.sync="dialogFormVisible">
        <!-- 查询和其他操作 -->
        <div class="filter-container" >
          <el-select clearable  class="filter-item"  v-model="listQuery.typeCode" style="width:150px;"   placeholder="请选择类型名称">
            <el-option  v-for=" item in typeIdList" :value="item.code"  :key="item.code" :label="item.name">
            </el-option>
          </el-select>
          <el-select clearable  class="filter-item"  v-model="listQuery.sourceCode" style="width:150px;"   placeholder="请选择来源名称">
            <el-option  v-for=" item in sourceIdList" :value="item.code"  :key="item.code" :label="item.name">
            </el-option>
          </el-select>
          <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 120px;" placeholder="消息主题"/>
          <el-input v-model="listQuery.content" clearable class="filter-item" style="width: 120px;" placeholder="消息内容"/>
          <el-button v-permission="['GET /admin/opadmininfo/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
          <el-button v-permission="['GET /admin/opadmininfo/haveview']" class="filter-item" type="primary" icon="el-icon-search" @click="handleViewClick(null)">已查看</el-button>
        </div>

        <!-- 查询结果 -->

          <el-table v-loading="listLoading"  :data="list" element-loading-text="正在查询中。。。"
                    border fit highlight-current-row @selection-change="changeSelOrderList">

            <el-table-column type="selection"/>
            <el-table-column align="center" label="操作" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-permission="['GET /admin/opadmininfo/haveview']" type="primary" size="mini" @click="handleViewClick(scope.row.id)">已查看</el-button>

              </template>
            </el-table-column>

            <el-table-column align="center" label="消息主题" prop="title"/>
            <el-table-column align="center"  width="500px"   label="消息内容" prop="content"/>
            <el-table-column align="center" label="消息类型" prop="typeName"/>
            <el-table-column align="center" label="消息来源" prop="sourceName"/>
            <el-table-column align="center" label="创建时间" prop="addTime"/>-->

          </el-table>



        <pagination style="margin: 0px;padding: 10px" v-show="total>0" :total="total"  :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList()" />

  <!--      <div slot="footer" class="dialog-footer">-->
  <!--        <el-button @click="dialogFormVisible = false">关闭</el-button>-->

  <!--      </div>-->
      </el-dialog>

 </div>


</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import {countOpadmininfo} from '@/api/opadmininfo'
import { listOpadmininfo,haveViewOpadmininfo } from '@/api/opadmininfo'
import { listDiccode } from '@/api/diccode'
import { getYuyueCountInfo,getBeihuoCountInfo,getBeihuoCountInfo_KuaiDi,
    getBeihuoCountInfo_HangZhanLou,
    getTuiKuanCountInfo,getKaipiaoCountInfo } from '@/api/order'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Navbar',

  components: {
    Breadcrumb,
    Hamburger,
    Screenfull,
    SizeSelect,
    Pagination
  },

  data() {
    return {
      list: [],
      systemCountInfo:0,
      tuikuanCountInfo:0,
      kaiPiaoCountInfo:0,
      beihuoCountInfo:0,
        beihuoCountInfo_KuaiDi:0,
        beihuoCountInfo_HangZhanLou:0,
      yuyueCountInfo:0,
      total: 0,
      listLoading: true,
      dialogFormVisible: false,
      checkBoxSelOrderListData:[],
      sourceIdList:[],
      typeIdList:[],
      listQuery: {
        page: 1,
        limit: 10,
        opadminName:this.$store.getters.name,
        typeCode:undefined,
        sourceCode:undefined,
        title:undefined,
        content:undefined,
        startDate:undefined,
        endDate:undefined,
        ifViewed:false,
        sort: 'add_time desc',
      },
    }

  },
  computed: {
    ...mapGetters([
      'sidebar',
      'name',
      'avatar',
      'device'
    ])
  },
  //正确的用法
  mounted() {
    // 如果不加 window ，则会使用 vue实例的方法，将无法清除定时器
    this.getSystemCountInfo();
    //2分钟刷新一次
    this.timer = window.setInterval(() => {
      this.getKaipiaoCountInfo();
      this.getTuiKuanCountInfo();
      this.getYuyueCountInfo();
      // this.getBeihuoCountInfo();
      this.getBeihuoCountInfo_KuaiDi();
      this.getBeihuoCountInfo_HangZhanLou();
      // this.date = new Date();
    }, 120000);
    // console.log(this.timer);//number

  },
  created() {
    this.getTypeIdList()
    this.getSourceIdList()

    //启动时加载
    this.getKaipiaoCountInfo();
    this.getTuiKuanCountInfo();
    this.getYuyueCountInfo();
    // this.getBeihuoCountInfo();
    this.getBeihuoCountInfo_KuaiDi();
    this.getBeihuoCountInfo_HangZhanLou();

  },


  methods: {

    handleViewClick(infoId) {
      var infoIds=[];
      if(infoId!=null){
        infoIds.push(infoId)
      } else
      {
        for (let selItem of  this.checkBoxSelOrderListData) {
          infoIds.push(selItem.id)
        }
        if(infoIds==null||infoIds.length==0){
          this.$message.error("请先勾选要设置消息");
          return;
        }
      }
      haveViewOpadmininfo({infoIds:infoIds}).then(res=> {
        this.getList();
        this.$message.success('更新成功');
      }).catch(
        error=>{
          console.log(error);
        }
      );

    },


    getTypeIdList(){
      listDiccode({dicmainName:"后端消息_类型"}).then(response => {
        this.typeIdList = response.data.data.list
      }).catch(() => {
        this.typeIdList = []
      })
    },
    getSourceIdList(){
      listDiccode({dicmainName:"后端消息_来源"}).then(response => {
        this.sourceIdList = response.data.data.list
      }).catch(() => {
        this.sourceIdList = []
      })
    },
    clearTimer() {
      window.clearInterval(this.timer);
      this.timer = null;
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    linkBeiHuo(){
      this.$router.push("/mall/orderlist?type=beihuo");
    },
    linkBeiHuo_KuaiDi(){
        this.$router.push("/mall/orderlist?type=beihuo&sendWay=快递");
    },
    linkBeiHuo_HangZhanLou(){
        this.$router.push("/mall/orderlist?type=beihuo&sendWay=自提取货");
    },
    linkTuiKuan(){
      this.$router.push("/mall/orderlist?type=tuikuan");
    },
    linkKaiPiao(){
      this.$router.push("/mall/orderlist?type=kaipiao");
    },
    linkYuyue(){
      this.$router.push("/o2o/order?type=beihuo");
    },
    //获取预约的数量
    getYuyueCountInfo(){
      getYuyueCountInfo().then(
        (response)=>{this.yuyueCountInfo=response.data.data}
      );
    },
    //获取备货的数量
    getBeihuoCountInfo(){
      getBeihuoCountInfo().then(
        (response)=>{this.beihuoCountInfo=response.data.data}
      );
    },
    //获取快递备货的数量
    getBeihuoCountInfo_KuaiDi(){
        getBeihuoCountInfo_KuaiDi().then(
            (response)=>{this.beihuoCountInfo_KuaiDi=response.data.data}
        );
    },
    //获取自提备货的数量
    getBeihuoCountInfo_HangZhanLou(){
        getBeihuoCountInfo_HangZhanLou().then(
            (response)=>{this.beihuoCountInfo_HangZhanLou=response.data.data}
        );
    },
    //获取退款的数量
    getTuiKuanCountInfo(){
      getTuiKuanCountInfo().then(
        (response)=>{this.tuikuanCountInfo=response.data.data}
      );
    },
    //获取开票的数量
    getKaipiaoCountInfo(){
      getKaipiaoCountInfo().then(
        (response)=>{this.kaiPiaoCountInfo=response.data.data}
      );
    },
    //获取系统消息的数量
    getSystemCountInfo(){
      countOpadmininfo({loginUserName:this.$store.getters.name}).then(
        (response)=>{this.systemCountInfo=response.data.data}
      );
    },
    showInfoDialog(){
      this.dialogFormVisible=true;
      this.getList();
    },
    changeSelOrderList(val){
      this.checkBoxSelOrderListData = val
    },
    toggleSideBar() {
      this.$store.dispatch('toggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload()// In order to re-instantiate the vue-router object to avoid bugs
      })
    },

    getList() {
      this.listLoading = true
      listOpadmininfo(this.listQuery)
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
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .item {
    margin-top: -0px;

    margin-right: 20px;
  }
  .customWidth {
    width: 1024px;
  }
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .breadcrumb-container{
    float: left;
  }
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }
  .right-menu {
    float: right;
    height: 100%;
    &:focus{
     outline: none;
    }
    .right-menu-item {
      display: inline-block;
      margin: 0 8px;
    }
    .screenfull {
      height: 20px;
    }
    .international{
      vertical-align: top;
    }
    .avatar-container {
      height: 50px;
      margin-right: 30px;
      .avatar-wrapper {
        cursor: pointer;
        margin-top: 5px;
        position: relative;
        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }
        .el-icon-caret-bottom {
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
