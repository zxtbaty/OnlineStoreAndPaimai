<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-select v-model="listQuery.comId" clearable class="filter-item" style="width: 150px;" placeholder="请选择公司"  >
        <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="listQuery.brandId" clearable class="filter-item" style="width: 150px;" placeholder="请选择品牌">
        <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-cascader :options="categoryList" class="filter-item"  expand-trigger="hover" @change="handleCategoryChange" placeholder="所属分类"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-input v-model="listQuery.specifications" clearable class="filter-item" style="width: 150px;" placeholder="请输入规格型号"/>
      <el-input v-model="listQuery.goodsPosKey" clearable class="filter-item" style="width: 150px;" placeholder="请输入POS店码"/>
      <el-input v-model="listQuery.minAmount" clearable class="filter-item" style="width: 150px;" placeholder="库存数量低等于"/>
      <el-input v-model="listQuery.maxAmount" clearable class="filter-item" style="width: 150px;" placeholder="库存数量高等于"/>
      <el-select v-model="listQuery.isOnSale" clearable class="filter-item" placeholder="请选择上架标志">
        <el-option  label="上架" :value="1"/>
        <el-option  label="停售"  :value="0"/>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFindWarning">报警查询</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFindStore">库存查找</el-button>

      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="操作"   width="100" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleProductShow(scope.row)">设置</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="商品ID" prop="id"/>

      <el-table-column align="center" label="商品编号" prop="goodsSn"/>
      <el-table-column align="center" min-width="100" label="名称" prop="name"/>
      <el-table-column align="center" min-width="100" label="规格型号" prop="specifications"/>
      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" label="货品价格" prop="price"/>

      <el-table-column align="center" label="库存数量" prop="number">
      </el-table-column>
      <el-table-column align="center" label="预约数量" prop="yuyueNumber">
      </el-table-column>
      <el-table-column align="center" label="可用库存" prop="remainNumber">
      </el-table-column>
      <el-table-column align="center" label="预警数量" prop="minStorenum">
      </el-table-column>

      <el-table-column align="center" label="是否上架" prop="isOnSale">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '上架' }}</el-tag>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getListPage" />

<!--    <el-tooltip placement="top" content="返回顶部">-->
<!--      <back-to-top :visibility-height="100" />-->
<!--    </el-tooltip>-->


    <el-dialog :visible.sync="productVisiable"  title="设置货品" :close-on-click-modal="false">
      <el-form ref="productForm"   :model="productForm" status-icon label-position="right" label-width="130px" >
        <el-form-item label="商品ID" prop="id">
          {{productForm.id}}
        </el-form-item>
        <el-form-item label="货品规格列" prop="specifications">
          <el-tag >
            {{productForm.specifications}}
          </el-tag>
        </el-form-item>

        <el-form-item label="货品售价"  prop="price">
          <el-input :disabled="true" v-model="productForm.price"/>
        </el-form-item>
        <el-form-item label="货品数量" prop="number">
          <el-input v-model="productForm.number"/>
        </el-form-item>
        <el-form-item label="预约数量"  prop="yuyueNumber">
          <el-input :disabled="true" v-model="productForm.yuyueNumber"/>
        </el-form-item>

        <el-form-item label="可用库存"    prop="remainNumber">
          <el-input :disabled="true" v-model="productForm.remainNumber"/>
        </el-form-item>
        <el-form-item label="库存报警数量" prop="minStorenum">
          <el-input v-model="productForm.minStorenum"/>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="productVisiable = false">取消</el-button>
        <el-button type="primary" @click="handleProductEdit">确定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<style>
  .table-expand {
    font-size: 0;
  }
  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }
  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }
  .gallery {
    width: 80px;
    margin-right: 10px;
  }
</style>

<script>
import { statWarningQuery, statStoreQuery } from '@/api/stat'
import { listCatAndBrand } from '@/api/goods'
import { allCompany } from '@/api/company'
// import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { updateGoodsProduct } from '@/api/goods'

export default {
  name: 'StoreWarning',
  components: {  Pagination },
  data() {
    return {
      productVisiable:false,
      list: [],
      comList: [],
      categoryList: [],
      brandList:[],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 20,
        comId: undefined,
        brandId:undefined,
        goodsSn: undefined,
        name: undefined,
        specifications:undefined,
        goodsPosKey:undefined,
        minAmount:undefined,
        maxAmount:undefined,
        isOnSale:undefined,
        sort: 'add_time desc',
      },
      productForm: {
          id: 0,
          // specificationIds: [],
          number: 0,
          yuyueNumber:0,
          remainNumber:0,
          minStorenum: 0,
      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false,
      warningQueryOrStoreQuery:undefined
    }
  },
  created() {
    this.getComList()
    this.getBrandList()
    //this.getList()
  },
  methods: {
    getComList() {
      allCompany().then(
        response => {
          this.comList = response.data.data.list
        }
      )
    },
    handleProductShow(row) {

        this.productForm = Object.assign({}, row);

        this.productVisiable = true
    },
    handleProductEdit() {
        let  submitProductForm={
                id: this.productForm.id,
                // specificationIds: [],
                number: this.productForm.number,
                yuyueNumber:this.productForm.yuyueNumber,
                remainNumber:this.productForm.remainNumber,
                minStorenum: this.productForm.minStorenum
            };
        updateGoodsProduct(submitProductForm).then(response => {
            this.$message.success('修改成功');
            this.productVisiable=false;
            this.getListPage()
        }) .catch(response => {
            this.$message.success('更新失败');
        });
    },
    getBrandList() {
      listCatAndBrand().then(response => {
        this.categoryList = response.data.data.categoryList
        this.brandList = response.data.data.brandList
      })
    },
    handleFindWarning(){

      this.warningQueryOrStoreQuery='warning';
      this.listLoading = true
      statWarningQuery(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },

    handleFindStore(){
      this.warningQueryOrStoreQuery='store';
      this.listLoading = true
      statStoreQuery(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },

    getListPage() {
      //this.listQuery.page = 1

      if(this.warningQueryOrStoreQuery=='warning'){
        this.handleFindWarning()
      } else if(this.warningQueryOrStoreQuery=='store'){
        this.handleFindStore()
      }
    },

    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['商品货品Id','商品ID','商品分类', '商品编号', '名称', '原价', '当前价格', '是否新品', '是否热品', '是否上架', '首页主图', '宣传图片列表', '商品介绍', '详细介绍', '商品图片', '商品单位', '关键字', '类目ID', '品牌商ID']
        const filterVal = ['id','goodsId','', 'goodsSn', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'brief', 'detail', 'picUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '库存报警信息')
        this.downloadLoading = false
      })
    },
    handleCategoryChange(value) {
      this.goods.categoryId = value[value.length - 1]
    },
  }
}
</script>
