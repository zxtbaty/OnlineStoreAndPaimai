<template>
  <div class="app-container">
    <!-- 订单详情对话框 -->
      <el-card class="box-card">
        <el-form :data="orderDetail" >
          <el-col :span="12" style="color: blue">
            <el-form-item label="订单编号：">
              <span>{{ orderDetail.order.orderSn }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="color: blue">
            <el-form-item label="订单状态：">
              <span>{{ orderDetail.order.orderStatusName}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="color: blue">
            <el-form-item label="订单用户：">
              <span>{{ orderDetail.user.nickname }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="color: blue">
            <el-form-item label="下单时间：">
              <span>{{ orderDetail.order.addTime }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="color: red">
            <el-form-item label="用户留言：">
              <span>{{ orderDetail.order.message }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12"  style="color: blue">
            <el-form-item label="用户手机号" >
              <span>{{ orderDetail.order.userPhone }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收货信息：">
              <span>（收货人）{{ orderDetail.order.consignee }}</span>
              <span>（手机号）{{ orderDetail.order.mobile }}</span>
              <span>（地址）{{ orderDetail.order.address }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="24" v-if="orderDetail.order.privateItemName!=null&&orderDetail.order.privateItemName!=''" >
            <el-form-item label="私人定制信息：">
              <span>（品项名称）{{ orderDetail.order.privateItemName }}</span>
              <span>（交付时间）{{ orderDetail.order.privateDeliverDate }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="orderDetail.order.privateItemName!=null&&orderDetail.order.privateItemName!=''" >
            <el-form-item label="私人定制内容描述：">
              <span>{{ orderDetail.order.privateContentDesc }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="orderDetail.order.privateItemName!=null&&orderDetail.order.privateItemName!=''" >
            <el-form-item label="私人定制特别备注：">
              <span>{{ orderDetail.order.privateRemark }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="orderDetail.order.privateItemName==null||orderDetail.order.privateItemName==''" >
            <el-form-item label="商品信息：">
              <el-table :data="orderDetail.orderGoods" border fit highlight-current-row>
                <el-table-column align="center" label="商品名称" prop="goodsName"/>
                <el-table-column align="center" label="商品编号" prop="goodsSn"/>
                <el-table-column align="center" label="货品规格" prop="specifications"/>
                <el-table-column align="center" label="货品价格" prop="price"/>
                <el-table-column align="center" label="货品数量" prop="number"/>
                <el-table-column align="center" label="货品图片" prop="picUrl">
                  <template slot-scope="scope">
                    <img :src="scope.row.picUrl" width="40">
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="费用信息：">
            <span>
              (实际费用){{ orderDetail.order.actualPrice }}元 =
              (商品总价){{ orderDetail.order.goodsPrice }}元 +
              (快递费用){{ orderDetail.order.freightPrice }}元 -
              (优惠减免){{ orderDetail.order.couponPrice }}元 -
              (积分减免){{ orderDetail.order.integralPrice }}元
            </span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付信息：">
              <span>（支付渠道）{{ orderDetail.order.payMethod==1 ? '支付宝' : '微信'}}</span>
              <span>（支付时间）{{ orderDetail.order.payTime }}</span>
              <span>（支付编号）{{ orderDetail.order.payId }}</span>
            </el-form-item>
          </el-col>

          <el-col :span="24" v-if="orderDetail.order.ifFapiao==true&&orderDetail.fapiao.length>0">
            <el-form-item label="发票信息：">
              <span>(发票类型)【{{ orderDetail.fapiao[0].fapiaoType }}】</span>
              <span>(单位名称)【{{ orderDetail.fapiao[0].name }}】</span>
              <span>(纳税人识别号)【{{ orderDetail.fapiao[0].taxno }}】</span>
              <span>(注册地址)【{{ orderDetail.fapiao[0].address }}】</span>
              <span>(注册电话)【{{ orderDetail.fapiao[0].telphone }}】</span>
              <span>(开户银行)【{{ orderDetail.fapiao[0].bank }}】</span>
              <span>(银行帐号)【{{ orderDetail.fapiao[0].bankNo }}】</span>
              <span>(开票金额)【{{ orderDetail.fapiao[0].money}}】</span>
              <span>(开票状态)【{{ orderDetail.fapiao[0].status}}】</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="orderDetail.order.sendWay=='快递'">
            <el-form-item label="快递信息：">
              <span>（快递公司）{{ orderDetail.order.shipChannel }}</span>
              <span>（快递单号）{{ orderDetail.order.shipSn }}</span>
              <span>（发货时间）{{ orderDetail.order.shipTime }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="orderDetail.order.sendWay=='自提取货'">
            <el-form-item label="快递信息：">
              <span>（自提货点名称）{{ orderDetail.order.pickSiteName }}</span>
              <span>（自提货日期时间）{{ orderDetail.order.pickTime }}</span>
              <span>（取货人真实姓名）{{ orderDetail.order.pickPerson }}</span>
              <span>（取货人手机号码）{{ orderDetail.order.pickMobile }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收货信息：">
              <span>（确认收货时间）{{ orderDetail.order.confirmTime }}</span>
            </el-form-item>
          </el-col>
        </el-form>
      </el-card>
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
import { detailOrder} from '@/api/order'
import { getToken } from '@/utils/auth'
import * as constIndex from '@/utils/index.js'

export default {

  name: 'ViewOrderInfo',
  components: { },

  props:{
    orderDetailId:{
      type:Number,
      default:null,
    }
  },

  data() {
    return {
      statusMap:constIndex.statusMap,
      orderDetail: {
          order: {},
          fapiao:{},//发票
          coupon:{},//优惠券
          user: {},
          orderGoods: []
      },
    }
  },
  watch:{
    orderDetailId:{
      immediate:true,
      // deep:true,
      handler(val){
         if(val!=null){
           this.handleDetail(val)
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

  },
  methods: {

    handleDetail(orderId) {
        detailOrder(orderId).then(response => {
            this.orderDetail = response.data.data
            console.log(this.orderDetail)
        })

    },

},

}
</script>
