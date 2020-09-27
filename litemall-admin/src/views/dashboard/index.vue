<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="40" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
          <div class="card-panel-icon-wrapper icon-people">
            <svg-icon icon-class="peoples" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">会员数量</div>
            <count-to :start-val="0" :end-val="userTotal" :duration="2600" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('shoppings')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="shopping" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">订单总量</div>
            <count-to :start-val="0" :end-val="orderTotal" :duration="3600" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('messages')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="star" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">商品总量</div>
            <count-to :start-val="0" :end-val="goodsTotal" :duration="3000" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="documentation" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">规格总量</div>
            <count-to :start-val="0" :end-val="productTotal" :duration="3200" class="card-panel-num"/>
          </div>
        </div>
      </el-col>


    </el-row>

    <el-row :gutter="40" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
          <div class="card-panel-icon-wrapper icon-people">
            <svg-icon icon-class="peoples" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日新增会员</div>
            <count-to :start-val="0" :end-val="todayUserTotal" :duration="2600" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
          <div class="card-panel-icon-wrapper icon-people">
            <svg-icon icon-class="shopping" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日新增订单</div>
            <count-to :start-val="0" :end-val="todayOrderTotal" :duration="2600" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
      <!--<el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">-->
        <!--<div class="card-panel" @click="handleSetLineChartData('newVisitis')">-->
          <!--<div class="card-panel-icon-wrapper icon-people">-->
            <!--<svg-icon icon-class="peoples" class-name="card-panel-icon" />-->
          <!--</div>-->
          <!--<div class="card-panel-description">-->
            <!--<div class="card-panel-text">今日UV</div>-->
            <!--<count-to :start-val="0" :end-val="userTotal" :duration="2600" class="card-panel-num"/>-->
          <!--</div>-->
        <!--</div>-->
      <!--</el-col>-->
      <!--<el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">-->
        <!--<div class="card-panel" @click="handleSetLineChartData('shoppings')">-->
          <!--<div class="card-panel-icon-wrapper icon-shoppingCard">-->
            <!--<svg-icon icon-class="money" class-name="card-panel-icon" />-->
          <!--</div>-->
          <!--<div class="card-panel-description">-->
            <!--<div class="card-panel-text">今日PV</div>-->
            <!--<count-to :start-val="0" :end-val="orderTotal" :duration="3600" class="card-panel-num"/>-->
          <!--</div>-->
        <!--</div>-->
      <!--</el-col>-->
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('shoppings')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="star" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日新增商品</div>
            <count-to :start-val="0" :end-val="todayGoodsToday" :duration="3600" class="card-panel-num"/>
          </div>
        </div>
      </el-col>

      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('messages')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="money" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日新增规格</div>
            <count-to :start-val="0" :end-val="todayProductTotal" :duration="3000" class="card-panel-num"/>
          </div>
        </div>
      </el-col>
    </el-row>

    <!--<el-row :gutter="20">-->
    <!--<el-col :span="24" class="echarts-item">-->
    <!--<div class="content-title"></div>-->
    <!--<ve-histogram :data="histogramChartData" :settings="histogramChartSettings" :mark-line="histogramChartMarkLine"></ve-histogram>-->
    <!--</el-col>-->
    <!--</el-row>-->
    <el-row :gutter="20">
      <el-col :span="24" class="echarts-item">
        <div class="content-title">变化趋势图</div>
        <ve-line :data="lineChartData" :settings="lineChartSettings" :mark-point="lineChartMarkPoint"/>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import { info } from '@/api/dashboard'
import CountTo from 'vue-count-to'
import { statHomeLineChartData } from '@/api/stat'
import Vue from 'vue'
import VCharts from 'v-charts'
Vue.use(VCharts)

export default {
  components: {
    CountTo
  },
  data() {
    return {
      userTotal: 0,
      todayUserTotal:0,
      goodsTotal: 0,
      todayGoodsToday:0,
      productTotal: 0,
      todayProductTotal:0,
      orderTotal: 0,
      todayOrderTotal:0,

      histogramChartData: {
        columns: [],
        rows: []
      },
      histogramChartSettings: {
        metrics: [],
        dimension: [],
        stack: {}
      },
      histogramChartMarkLine: {
        data: [
          [{ type: 'min' }, { type: 'max' }]
        ]
      },
      lineChartData: {
        columns: [],
        rows: []
      },
      lineChartSettings: {

        metrics: ['新增用户量','新增商品品种', '订单量','收款金额','订单商品数量合计','用户访问量','会员用户访问量','页面访问量','商品访问量'],
        dimension: ['日期']
      },
      lineChartMarkPoint: {
        data: [{
          name: '最大值',
          type: 'max'
        }]
      },

    }
  },
  created() {
    info().then(response => {
      this.userTotal = response.data.data.userTotal
      this.todayUserTotal=response.data.data.todayUserTotal
      this.goodsTotal = response.data.data.goodsTotal
      this.todayGoodsToday=response.data.data.todayGoodsToday
      this.productTotal = response.data.data.productTotal
      this.todayProductTotal=response.data.data.todayProductTotal
      this.orderTotal = response.data.data.orderTotal
      this.todayOrderTotal=response.data.data.todayOrderTotal
    })
    statHomeLineChartData().then(response => {
      this.lineChartData.rows = response.data.data.rows
    })
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .dashboard-editor-container {
    padding: 32px;
    background-color: rgb(240, 242, 245);
    .chart-wrapper {
      background: #fff;
      padding: 16px 16px 0;
      margin-bottom: 32px;
    }
  }

  .panel-group {
    margin-top: 18px;

    .card-panel-col{
      margin-bottom: 32px;
    }
    .card-panel {
      height: 108px;
      cursor: pointer;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);
      &:hover {
        .card-panel-icon-wrapper {
          color: #fff;
        }
        .icon-people {
          background: #40c9c6;
        }
        .icon-message {
          background: #36a3f7;
        }
        .icon-money {
          background: #f4516c;
        }
        .icon-shoppingCard {
          background: #34bfa3
        }
      }
      .icon-people {
        color: #40c9c6;
      }
      .icon-message {
        color: #36a3f7;
      }
      .icon-money {
        color: #f4516c;
      }
      .icon-shoppingCard {
        color: #34bfa3
      }
      .card-panel-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
      }
      .card-panel-icon {
        float: left;
        font-size: 48px;
      }
      .card-panel-description {
        float: right;
        font-weight: bold;
        margin: 26px;
        margin-left: 0px;
        .card-panel-text {
          line-height: 18px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 16px;
          margin-bottom: 12px;
        }
        .card-panel-num {
          font-size: 20px;
        }
      }
    }
  }
</style>
