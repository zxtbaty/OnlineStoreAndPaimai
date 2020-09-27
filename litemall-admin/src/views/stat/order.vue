<template>
  <div class="app-container">
    <div style="margin-bottom: 15px">
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.beginDate"
                      type="date"
                      placeholder="查询起始日期"
                      value-format="yyyy-MM-dd"/>
      <el-date-picker class="filter-item" style="width: 200px"
                      v-model="listQuery.endDate"
                      type="date"
                      placeholder="查询截至日期"
                      value-format="yyyy-MM-dd"/>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-find"
        @click="handleFind">查询
      </el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload">导出
      </el-button>
    </div>
    <div style="margin-bottom: 15px">
       说明：订单数据只包含用户已经付款的有效订单(包含退款)
    </div>
    <ve-line :extend="chartExtend" :data="chartData" :settings="chartSettings"/>
  </div>
</template>

<script>
import { statOrder } from '@/api/stat'
import VeLine from 'v-charts/lib/line'
export default {
  components: { VeLine },
  data() {
    return {
      downloadLoading:false,
      findLoading:false,
      listQuery:{
        beginDate:undefined,
        endDate:undefined
      },
      chartData: {},
      chartSettings: {},
      chartExtend: {}
    }
  },
  created() {
    this.chartExtend = {
      xAxis: { boundaryGap: true }
    }
    this.queryData();
  },
  methods:{
    handleFind() {
      this.queryData();
    },
    queryData(){
      this.findLoading = true
      statOrder(this.listQuery).then(response => {
        this.chartData = response.data.data
        this.chartSettings = {
          title: 'test',
          labelMap: {
            'orders': '订单量',
            'customers': '下单用户',
            'actualPrice': '实收总额',
            'pcr': '客单价'
          }
        }
        this.downloadLoading = false
      }).catch(() => {
        this.findLoading = false
      })
    },
    handleDownload() {
      this.downloadLoading = true
      statOrder(this.listQuery)
        .then(response => {
          var exportList = response.data.data.rows
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['日期', '订单量','下单用户','实收总额','客单价']
            const filterVal = ['day', 'orders','customers','actualPrice','pcr']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '订单信息统计'
            )
            this.downloadLoading = false
          })
          this.downloadLoading = false

        })
        .catch(() => {
          this.downloadLoading = false

        })
    }
  }


}
</script>
