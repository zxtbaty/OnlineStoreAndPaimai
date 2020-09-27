<template>
  <div class="app-container">
    <el-form ref="dataForm"  :model="dataForm" status-icon label-width="300px">
      <h3>客服设置</h3>
      <el-form-item label="云雀客户端网址" prop="litemall_order_service_yunque">
        <el-input v-model="dataForm.litemall_order_service_yunque" class="input-width">
          <template slot="append"></template>
        </el-input>
        <span class="info"></span>
      </el-form-item>
      <el-form-item label="客服手机号" prop="litemall_order_service_phone">
        <el-input v-model="dataForm.litemall_order_service_phone" class="input-width">
          <template slot="append"></template>
        </el-input>
        <span class="info">用于预约单短信提醒</span>
      </el-form-item>

      <h3>特产</h3>
      <el-form-item label="用户下单后超时" prop="litemall_order_unpaid">
        <el-input v-model="dataForm.litemall_order_unpaid" class="input-width">
          <template slot="append">分钟</template>
        </el-input>
        <span class="info">用户未付款，则订单自动取消</span>
      </el-form-item>
      <el-form-item label="订单发货后超期" prop="litemall_order_unconfirm">
        <el-input v-model="dataForm.litemall_order_unconfirm" class="input-width">
          <template slot="append"> 天</template>
        </el-input>
        <span class="info">未确认收货，则订单自动确认收货</span>
      </el-form-item>
<!--      <el-form-item label="确认收货后超期" prop="litemall_order_comment">-->
<!--        <el-input v-model="dataForm.litemall_order_comment" class="input-width">-->
<!--          <template slot="append">天</template>-->
<!--        </el-input>-->
<!--        <span class="info">未评价商品，则取消评价资格</span>-->
<!--      </el-form-item>-->
      <el-form-item label="提货备货N小时" prop="litemall_order_tihuo_beihuo_hour">
        <el-input v-model="dataForm.litemall_order_tihuo_beihuo_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">用户按当前操作时间可提货N小时后订单,默认12小时</span>
      </el-form-item>
      <el-form-item label="提货最长天数" prop="litemall_order_tihuo_max_days">
        <el-input v-model="dataForm.litemall_order_tihuo_max_days" class="input-width">
          <template slot="append">天数</template>
        </el-input>
        <span class="info">用户可以提货N天后的单子,默认1天</span>
      </el-form-item>
      <el-form-item label="提货提醒提前N小时" prop="litemall_order_tihuo_hint_first_hour">
        <el-input v-model="dataForm.litemall_order_tihuo_hint_first_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">提货单首次提醒提前小时,默认4小时</span>
      </el-form-item>
      <el-form-item label="提货复提醒前N小时" prop="litemall_order_tihuo_second_hour">
        <el-input v-model="dataForm.litemall_order_tihuo_second_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">提货单再次提醒提前小时,默认1小时</span>
      </el-form-item>


      <h3>精品</h3>
      <el-form-item label="预约备货N小时" prop="litemall_order_yuyue_beihuo_hour">
        <el-input v-model="dataForm.litemall_order_yuyue_beihuo_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">用户按当前操作时间可预约N小时后订单,默认24小时</span>
      </el-form-item>
      <el-form-item label="预约最长天数" prop="litemall_order_yuyue_max_days">
        <el-input v-model="dataForm.litemall_order_yuyue_max_days" class="input-width">
          <template slot="append">天数</template>
        </el-input>
        <span class="info">用户可以预约N天后的单子,默认7天</span>
      </el-form-item>
      <el-form-item label="预约超时最长N小时" prop="litemall_yuyue_delay_hour">
        <el-input v-model="dataForm.litemall_order_yuyue_delay_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">预约单按预约时间超时后N小时系统自动取消,默认2小时</span>
      </el-form-item>
      <el-form-item label="预约提醒提前N小时" prop="litemall_yuyue_hint_first_hour">
        <el-input v-model="dataForm.litemall_order_yuyue_hint_first_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">预约单首次提醒提前小时,默认4小时</span>
      </el-form-item>
      <el-form-item label="预约复提醒前N小时" prop="litemall_yuyue_second_hour">
        <el-input v-model="dataForm.litemall_order_yuyue_second_hour" class="input-width">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">预约单再次提醒提前小时,默认1小时</span>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listOrder, updateOrder } from '@/api/config'

export default {
  name: 'ConfigOrder',
  data() {
    return {
      dataForm: {}
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      listOrder().then(response => {
        this.dataForm = response.data.data
      })
    },
    cancel() {
      this.init()
    },
    update() {
      updateOrder(this.dataForm)
        .then(response => {
          this.$message.success('订单参数配置成功');

        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    }
  }
}
</script>
<style scoped>
  .input-width {
    width: 50%;
  }
  .info {
    margin-left: 15px;
  }
</style>
