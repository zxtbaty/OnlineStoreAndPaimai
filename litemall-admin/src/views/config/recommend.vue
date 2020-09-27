<template>
  <div class="app-container">
    <el-form

      status-icon
      label-width="300px">
      <!-- 添加或修改对话框 -->
      <el-dialog :title="'选择商品'" :close-on-click-modal="false" :visible.sync="dialogFormVisible" width="80%">
        <div class="app-container">
          <!-- 查询和其他操作 -->
          <div class="filter-container">
            <el-select
              v-model="goodsQuery.comId"
              :readonly="true"
              :disabled="true"
              clearable
              class="filter-item"
              placeholder="请选择公司"
              @change="changeStoreList(goodsQuery.comId)" >
              <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>

            <el-select v-if="goodsQuery.comId!=1" v-model="goodsQuery.storeId" clearable class="filter-item" placeholder="请选择店铺">
              <el-option v-for="item in storeList" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
            <el-input v-model="goodsQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
            <el-input v-model="goodsQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>
            <el-button type="primary" class="filter-item" @click="addCheckData">确定添加</el-button>
          </div>
          <!-- 查询结果 -->
          <el-table
            v-loading="listLoading"
            :data="goodsList"
            element-loading-text="正在查询中。。。"
            border
            fit
            highlight-current-row
            @selection-change="checkChange">

            <el-table-column type="selection"/>

            <el-table-column align="center" label="商品编号" prop="goodsSn"/>


            <el-table-column v-if="this.activeTabName=='首页秒杀'||this.activeTabName=='首页团购'||this.activeTabName=='列表秒杀'||this.activeTabName=='列表团购'" align="center" label="商品规格" prop="goodsProductSpecifications"/>

            <el-table-column align="center" min-width="100" label="名称" prop="name"/>

            <el-table-column align="center" property="iconUrl" label="图片">
              <template slot-scope="scope">
                <img :src="scope.row.picUrl" width="40">
              </template>
            </el-table-column>

            <!--<el-table-column align="center" property="iconUrl" label="分享图">-->
              <!--<template slot-scope="scope">-->
                <!--<img :src="scope.row.shareUrl" width="40">-->
              <!--</template>-->
            <!--</el-table-column>-->

            <!--<el-table-column align="center" label="详情" prop="detail">-->
              <!--<template slot-scope="scope">-->
                <!--<el-dialog :visible.sync="detailDialogVisible" title="商品详情">-->
                  <!--<div v-html="goodsDetail"/>-->
                <!--</el-dialog>-->
                <!--<el-button type="primary" size="mini" @click="showDetail(scope.row.detail)">查看</el-button>-->
              <!--</template>-->
            <!--</el-table-column>-->

            <el-table-column align="center" label="原价" prop="counterPrice"/>

            <el-table-column align="center" label="当前价格" prop="retailPrice"/>

<!--            <el-table-column align="center" label="是否新品" prop="isNew">-->
<!--              <template slot-scope="scope">-->
<!--                <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{ scope.row.isNew ? '新品' : '非新品' }}</el-tag>-->
<!--              </template>-->
<!--            </el-table-column>-->

<!--            <el-table-column align="center" label="是否热品" prop="isHot">-->
<!--              <template slot-scope="scope">-->
<!--                <el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{ scope.row.isHot ? '热品' : '非热品' }}</el-tag>-->
<!--              </template>-->
<!--            </el-table-column>-->

            <el-table-column align="center" label="是否上架" prop="isOnSale">
              <template slot-scope="scope">
                <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '下架' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>

          <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />


        </div>

      </el-dialog>

      <el-tabs id="currentTab" v-model="activeTabName" tab-position="left">

        <el-tab-pane name="首页秒杀" label="首页秒杀" >
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('首页秒杀')">添加秒杀商品</el-button>
            </div>
            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="首页团购" label="首页团购">
          <div class="app-container">
            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('首页团购')">添加团购商品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="首页特产" label="首页特产">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('首页特产')">添加首页特产商品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>

        <el-tab-pane name="列表秒杀" label="列表秒杀">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('列表秒杀')">添加列表秒杀商品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="列表团购" label="列表团购">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('列表团购')">添加列表团购商品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="列表特产" label="列表特产">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('列表特产')">添加列表特产商品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="goodsId"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="列表首都" label="列表首都">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('列表首都')">添加列表首都精品</el-button>
            </div>

            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="id"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>

                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
        <el-tab-pane name="列表大兴" label="列表大兴">
          <div class="app-container">

            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAddGoods('列表大兴')">添加列表大兴精品</el-button>
            </div>
            <!-- 查询结果 -->
            <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>

              <el-table-column align="center" min-width="100px" label="商贸类型" prop="comName"/>

              <el-table-column align="center" min-width="50px" label="商品ID" prop="id"/>

              <el-table-column align="center" min-width="50px" label="商品编号" prop="goodsSn"/>

              <el-table-column align="center" min-width="200px" label="商品名称" prop="name"/>

              <el-table-column align="center" min-width="50px" label="图片" prop="iconUrl"/>

              <el-table-column align="center" min-width="50px" label="显示排序" prop="ordernumber">
                <template slot-scope="scope">
                  <el-input v-if="scope!=null&&scope.row!=null&&scope.row.ordernumber!=null" v-model="scope.row.ordernumber"/>
                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">保存</el-button>
                  <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

          </div>
        </el-tab-pane>
      </el-tabs>

    </el-form>

  </div>
</template>

<script>
import { listRecommend, addRecommend, updateRecommend, deleteRecommend } from '@/api/recommend'
import { listGoods } from '@/api/goods'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { allCompany } from '@/api/company'
import { listStore } from '@/api/store'

export default {
  name: 'Keyword',
  components: { Pagination },
  data() {
    return {
      activeTabName: '首页秒杀', // 当前选择的Tab名
      comList: [], // 公司列表
      storeList: [], // 店铺列表
      checkBoxData: [], // 选择的推荐数据
      postSaveData: [], // 要提交的选择数据

      list: undefined, // 已经设置的列表数据
      goodsList: undefined, // 可推荐数据列表
      total: 0, // 已经设置的列表数据总计
      goodsTotal: 0, // 可推荐的数据列表总计
      listLoading: true, // 加载状态
      selComId: undefined, // 选择的公司Id
      selComName: undefined, // 选择的公司名称
      // 可推荐列表查询
      goodsQuery: {
        page: 1,
        limit: 10,
        goodsSn: undefined,
        comId: undefined,
        storeId: undefined,
        name: undefined,
        sort: 'add_time desc',

      },
      // 已设置推荐查询
      listQuery: {
        page: 1,
        limit: 10,
        posType: this.activeTabName,
        comId: this.selComId,
        sort: 'add_time desc',

      },
      // 推荐列表是否显示
      dialogFormVisible: false,

      goodsDetail: '',
      detailDialogVisible: false

    }
  },
  watch: {
    'activeTabName': function(val) { // 监听切换状态-计划单
      this.activeTabName = val
      this.getList()
    }
  },
  created() {
    this.getComList()
    this.getList()
  },
  methods: {

    getComList() {
      allCompany().then(
        response => {
          this.comList = response.data.data.list
          if (this.comList.length > 0) {
            this.selComId = this.comList[0].id
            this.selComName = this.comList[0].name
            this.getStoreList;
          }
        }
      )
    },
    getStoreList() {
      listStore({ page: 1,
        limit: 100,
        comId:this.selComId,
        sort: 'add_time desc',
      }).then(
        response => {
          this.storeList = response.data.data.list
        }
      )
    },

    checkChange(val) {
       this.checkBoxData = val
    },

    changeStoreList(data) {
      this.storeList = []
      this.selComId = data
      this.selComName = this.getSelComNameById(data)
    },
    getSelComNameById(id){
      const obj = this.comList.find((item) => {
        return item.id === id
      })
      return  obj.name
    },


    getList() {
      this.listLoading = true
      listRecommend({

        posType: this.activeTabName,
        comId: this.selComId,
        sort: 'add_time desc',

      }
      ).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    /**
       * 获取检索的商品列表
       */
    getGoodsList() {
      this.listLoading = true
      listGoods({
        page:this.goodsQuery.page,
        limit:this.goodsQuery.limit,
        posType: this.activeTabName,
        goodsSn: this.goodsQuery.goodsSn,
        comId: this.goodsQuery.comId,
        storeId: this.goodsQuery.storeId,
        isOnSale:1,
        name: this.goodsQuery.name,
        sort: 'add_time desc',

      }).then(response => {
        this.goodsList = response.data.data.list
        this.goodsTotal = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.goodsList = []
        this.goodsTotal = 0
        this.listLoading = false
      })
    },
    /**
       * 进行检索
       */
    handleGoodsFilter() {
      this.goodsQuery.page = 1
      this.getGoodsList()
    },
    /**
       * 显示商品明细
       * @param detail
       */
    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    /**
       * 处理添加推荐商品
       * @param type
       */
    handleAddGoods(type) {
      this.goodsQuery={page:1,limit:10};
      this.dialogFormVisible = true
      if(type=='首页秒杀'||type=='首页团购'||type=='列表秒杀'||type=='列表团购'||type=='首页特产'||type=='列表特产')
      {
        this.goodsQuery.comId = 1
        this.selComId=1
        this.selComName= this.getSelComNameById(this.selComId)

      }
      this.getStoreList()
      this.getGoodsList()
    },

    addCheckData() {
      var ii = 1
      this.postSaveData=[]
      if(this.checkBoxData!=null&& this.checkBoxData.length>0)
      {
          this.checkBoxData.forEach(
              (item) => {
                  this.postSaveData.push({ posType: this.activeTabName, comId: this.selComId,
                      comName: this.selComName, goodsId: item.id, ordernumber: this.total + ii })
                  ii++
              }
          )
      }

      addRecommend(this.postSaveData).then(response => {
        this.list.unshift(response.data.data)
        this.dialogFormVisible = false
        this.$message.success('创建成功');

        this.getList();
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleUpdate(row) {
      this.postSaveData = []
      this.postSaveData.push({ id: row.goodsId, posType: this.activeTabName, comId: row.comId,
        comName: row.comName, goodsId: row.goodsId, ordernumber: row.ordernumber })

      updateRecommend(this.postSaveData).then(response => {
        // this.list.unshift(response.data.data)
        this.$message.success('保存成功');

        this.getList();
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleDelete(row) {
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deleteRecommend(row).then(response => {
        this.$message.success('删除成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    }
  }
}
</script>
