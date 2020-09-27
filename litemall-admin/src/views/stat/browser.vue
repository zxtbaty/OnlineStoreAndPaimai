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

    <ve-histogram :extend="chartExtend" :data="chartData" :settings="chartSettings"/>
  </div>
</template>

<script>
import { statBrowse } from '@/api/stat'
import VeHistogram from 'v-charts/lib/histogram'

export default {
  components: { VeHistogram },
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
      xAxis: { boundaryGap: true },
      series: {
        label: { show: true, position: 'top' }
      }
    }
    this.queryData()
  },
  methods:{
    handleFind() {
      this.queryData();
    },
    queryData(){
      this.findLoading = true
      statBrowse(this.listQuery).then(response => {
        this.chartData = response.data.data
        this.chartSettings = {
          labelMap: {
            'cusVisitCount': '用户访问数量',
            'goodsVisitCount': '商品访问数量',
            'huiYuanVisitCount': '会员用户访问数量',
            'pageVisitCount': '页面访问量'
          }
        }
        this.findLoading = false
      }).catch(() => {
        this.findLoading = false
      })
    },
    handleDownload() {
      this.downloadLoading = true
      statBrowse(this.listQuery)
        .then(response => {
          var exportList = response.data.data.rows
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['日期', '用户访问数量','商品访问数量','会员用户访问数量','页面访问量']
            const filterVal = ['day', 'cusVisitCount','goodsVisitCount','huiYuanVisitCount','pageVisitCount']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '用户访问情况统计'
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
