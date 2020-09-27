<template>

    <el-container>
        <el-form :data="orderDetail" >
          <el-form-item label="订单编号">
            <span>{{ orderDetail.order.orderSn }}</span>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-tag>{{ orderDetail.order.orderStatusName}}</el-tag>
          </el-form-item>
          <el-form-item label="订单用户">
            <span>{{ orderDetail.user.nickname }}</span>
          </el-form-item>
          <el-form-item label="用户留言">
            <span>{{ orderDetail.order.message }}</span>
          </el-form-item>
          <el-form-item label="用户手机号">
            <span>{{ orderDetail.order.userPhone }}</span>
          </el-form-item>
          <el-form-item label="收货信息">
            <span>（收货人）{{ orderDetail.order.consignee }}</span>
            <span>（手机号）{{ orderDetail.order.mobile }}</span>
            <span>（地址）{{ orderDetail.order.address }}</span>
          </el-form-item>
          <el-form-item label="商品信息">
            <el-table :data="orderDetail.orderGoods" border fit highlight-current-row>
              <el-table-column align="center" label="商品名称" prop="goodsName" />
              <el-table-column align="center" label="商品编号" prop="goodsSn" />
              <el-table-column align="center" label="货品规格" prop="specifications" />
              <el-table-column align="center" label="货品价格" prop="price" />
              <el-table-column align="center" label="货品数量" prop="number" />
              <el-table-column align="center" label="货品图片" prop="picUrl">
                <template slot-scope="scope">
                  <img :src="scope.row.picUrl" width="40">
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="费用信息">
            <span>
              (实际费用){{ orderDetail.order.actualPrice }}元 =
              (商品总价){{ orderDetail.order.goodsPrice }}元 +
              (快递费用){{ orderDetail.order.freightPrice }}元 -
              (优惠减免){{ orderDetail.order.couponPrice }}元 -
              (积分减免){{ orderDetail.order.integralPrice }}元
            </span>
          </el-form-item>
          <el-form-item label="支付信息">
            <span>（支付渠道）{{ orderDetail.order.payMethod==1?'支付宝':'微信'}}</span>
            <span>（支付时间）{{ orderDetail.order.payTime }}</span>
            <span>（支付编号）{{ orderDetail.order.payId }}</span>
          </el-form-item>
          <el-form-item label="快递信息">
            <span>（快递公司）{{ orderDetail.order.shipChannel }}</span>
            <span>（快递单号）{{ orderDetail.order.shipSn }}</span>
            <span>（发货时间）{{ orderDetail.order.shipTime }}</span>
          </el-form-item>
          <el-form-item label="收货信息">
          <span>（确认收货时间）{{ orderDetail.order.confirmTime }}</span>
         </el-form-item>

        </el-form>
    </el-container>


</template>

<style>
</style>

<script>
import { listOrder, shipOrder, refundOrder, detailOrder } from '@/api/order'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import checkPermission from '@/utils/permission' // 权限判断函数
import * as constIndex from '@/utils/index.js'


export default {
  props: {
    orderId: 0
  },
  name: 'Order',
  components: { Pagination },
  data() {
    return {
      statusMap:constIndex.statusMap,
      orderDetail: {
        order: {},
        user: {},
        orderGoods: []
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
    orderId: function() {
      // your code
      if (this.orderId == null) {
        this.orderDetail = []
      } else {
        detailOrder(this.orderId).then(response => {
          this.orderDetail = response.data.data
        })
      }
    }
  },

  methods: {
    checkPermission,

  }
}
</script>
