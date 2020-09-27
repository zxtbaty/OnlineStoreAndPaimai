<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select clearable multiple class="filter-item" style="width: 130px;" placeholder="应用范围" v-model="listQuery.usedRange" >
        <el-option v-for="item in goodsUsedRanges" :key="item.name" :label="item.name" :value="item.name"/>
      </el-select>
      <el-select v-model="listQuery.comId" clearable class="filter-item" style="width: 110px;" placeholder="公司名称"
                 @change="changeStoreList(listQuery.comId)" >
        <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="listQuery.storeId" clearable class="filter-item" style="width: 110px;" placeholder="选择店铺">
        <el-option v-for="item in storeList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 80px;" placeholder="编号"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 120px;" placeholder="商品名称"/>
      <el-select v-model="listQuery.brandId" clearable class="filter-item" style="width: 110px;" placeholder="选择品牌">
        <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-select v-model="listQuery.isOnSale" clearable class="filter-item" style="width: 80px;" placeholder="上架">
        <el-option :key="1" :label="'上架'" :value="1"/>
        <el-option :key="0" :label="'停售'" :value="0"/>
      </el-select>
      <el-select v-model="listQuery.ifXuni" clearable class="filter-item" style="width: 80px;" placeholder="虚拟">
        <el-option :key="1" :label="'是'" :value="1"/>
        <el-option :key="0" :label="'否'" :value="0"/>
      </el-select>
      <el-select v-model="listQuery.storeNum" clearable class="filter-item" style="width: 80px;" placeholder="库存">
        <el-option :key="0" :label="'未设置'" :value="'null'"/>
        <el-option :key="1" :label="'小于或等于0'" :value="'<=0'"/>
        <el-option :key="2" :label="'大于0'" :value="'>0'"/>
      </el-select>

      <el-select v-model="listQuery.storeNum" clearable class="filter-item" style="width: 80px;" placeholder="库存">
        <el-option :key="0" :label="'未设置'" :value="'null'"/>
        <el-option :key="1" :label="'小于或等于0'" :value="'<=0'"/>
        <el-option :key="2" :label="'大于0'" :value="'>0'"/>
      </el-select>



      <el-cascader class="filter-item" ref="belongClass"  style="width: 110px;"  placeholder="电商分类" :options="categoryList"
                     v-model="categoryIds" expand-trigger="hover" @change="handleCategoryChange"/>

      <el-select class="filter-item" style="width: 120px" clearable v-model="listQuery.authorId" placeholder="出品人"   >
        <el-option  v-for="item in authorList" :key="item.id"
                    :label="item.name" :value="item.id"/>
      </el-select>
      <el-select class="filter-item" style="width: 130px" clearable v-model="listQuery.dajiapaiCategoryId" placeholder="拍卖分类"   >
        <el-option  v-for="item in paimaiCategoryList" :key="item.id"
                    :label="item.name" :value="item.id"/>
      </el-select>
      <el-select class="filter-item" style="width: 130px" clearable v-model="listQuery.privateCategoryId" placeholder="私人定制分类"   >
        <el-option  v-for="item in privateCategoryList" :key="item.id"
                    :label="item.name" :value="item.id"/>
      </el-select>
      <el-button class="filter-item" type="primary" style="width: 80px;" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" style="width: 80px;" icon="el-icon-edit" @click="handleCreate">添加</el-button>

      <el-button :loading="downloadLoadingAll" title="全部" style="width: 80px;"  class="filter-item" type="primary"   @click="handleDownloadAll">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form  class="table-expand">

            <el-form-item label="宣传画廊">
              <img v-for="pic in props.row.gallery" :key="pic" :src="pic" class="gallery">
            </el-form-item>
            <el-form-item label="商品介绍">
              <span>{{ props.row.brief }}</span>
            </el-form-item>
            <el-form-item label="商品单位">
              <span>{{ props.row.unit }}</span>
            </el-form-item>
            <el-form-item label="关键字">
              <span>{{ props.row.keywords }}</span>
            </el-form-item>
            <el-form-item label="类目ID">
              <span>{{ props.row.categoryId }}</span>
            </el-form-item>
            <el-form-item label="品牌商ID">
              <span>{{ props.row.brandId }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column align="center" label="出品人" prop="authorName"/>
<!--      <el-table-column align="center" label="公司名称" prop="comName"/>-->
      <el-table-column align="center" label="品牌" prop="brandName"/>
      <el-table-column align="center" label="分类" prop="categoryName"/>
      <el-table-column align="center" label="商品编号" prop="goodsSn"/>

      <el-table-column align="center" min-width="100" label="名称" prop="name"/>

      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="分享图">
        <template slot-scope="scope">
          <img :src="scope.row.shareUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" label="详情" prop="detail">
        <template slot-scope="scope">
          <el-dialog :visible.sync="detailDialogVisible" :close-on-click-modal="false" title="商品详情">
            <div v-html="goodsDetail"/>
          </el-dialog>
          <el-button type="primary" size="mini" @click="showDetail(scope.row.detail)">查看</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="原价" prop="counterPrice"/>

      <el-table-column align="center" label="当前价格" prop="retailPrice"/>

<!--      <el-table-column align="center" label="是否新品" prop="isNew">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{ scope.row.isNew ? '新品' : '非新品' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->

<!--      <el-table-column align="center" label="是否热品" prop="isHot">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{ scope.row.isHot ? '热品' : '非热品' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->

      <el-table-column align="center" label="是否上架" prop="isOnSale">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

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

import { listGoods, deleteGoods,listCatAndBrand } from '@/api/goods'
import { allCompany } from '@/api/company'
import { listStore } from '@/api/store'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { listDiccode } from '@/api/diccode'
import { queryAllAuthororcorp } from '@/api/authororcorp'

export default {
  name: 'GoodsList',
  components: { BackToTop, Pagination },
  data() {
    return {
      authorList: [],//出品人
      goodsUsedRanges:[],//商品应用范围 电商专用、拍卖专用、私定专用、全场通用
      paimaiCategoryList:[],//大家拍类型列表
      privateCategoryList:[],//私人定制类型列表
      list: [],
      comList: [],
      storeList: [],
      brandList: [],
      categoryList: [],
      categoryIds: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        goodsSn: undefined,
        comId: undefined,
        storeId: undefined,
        brandId:undefined,
        categoryId:undefined,
        isOnSale:undefined,
        ifXuni:undefined,
        storeNum:undefined,
        name: undefined,
        usedRange:undefined,
        authorId:undefined,
        dajiapaiCategoryId:undefined,
        privateCategoryId:undefined,
        sort: 'add_time desc',

      },
      storQuery: {
        page: 1,
        limit: 2000,
        comId: undefined,
        sort: 'add_time desc',

      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false,
      downloadLoadingAll:false,
    }
  },
  created() {
    this.getComList()
    this.getCatL1()
    this.getStoreList()
    this.getList()
    this.getGoodsUsedRanges()
    this.getAllAuthororcorpList()
    this.getPaimaiCategoryList()
    this.getPrivateCategoryList()
  },
  methods: {
    getAllAuthororcorpList() {
      queryAllAuthororcorp()
        .then(response => {
          this.authorList = response.data.data.list;
        })
        .catch(() => {
        })
    },

    getPaimaiCategoryList(){
      listDiccode({dicmainName:"商品分类_大家拍"}).then(response => {
        this.paimaiCategoryList = response.data.data.list
      }).catch(() => {
        this.paimaiCategoryList = []
      })
    },

    getPrivateCategoryList(){
      listDiccode({dicmainName:"商品分类_私人定制"}).then(response => {
        this.privateCategoryList = response.data.data.list
      }).catch(() => {
        this.privateCategoryList = []
      })
    },

    getGoodsUsedRanges(){
      listDiccode({dicmainName:"商品应用范围"}).then(response => {
        this.goodsUsedRanges = response.data.data.list
      }).catch(() => {
        this.goodsUsedRanges = []
      })
    },
    getComList() {
      allCompany().then(
        response => {
          this.comList = response.data.data.list
        }
      )
    },
    handleCategoryChange(value) {
      this.listQuery.categoryId = value[value.length - 1]
      //this.goods.categoryName= value[value.length - 1]
    },
    getCatL1(){
      // listCatAndBrand(this.listQuery.comId).then(response => {
      //   this.categoryList = response.data.data.categoryList
      //   this.brandList = response.data.data.brandList
      // })
      listCatAndBrand().then(response => {
        this.categoryList = response.data.data.categoryList
        this.brandList = response.data.data.brandList
      })
    },
    getStoreList() {
      listStore(this.storQuery).then(
        response => {
          this.storeList = response.data.data.list
        }
      )
    },
    changeStoreList(data) {
      this.storeList = []
      listStore({
        page: 1,
        limit: 2000,
        comId: data,
        sort: 'add_time desc',

      }).then(response => {
        this.storeList = response.data.data.list
        //获取分类和品牌
        this.getCatL1();
      }
      )
    },
    getList() {
      this.listLoading = true
      listGoods(this.listQuery).then(response => {
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
    handleCreate() {
      //this.$router.push({ path: '/goods/create' })
      this.$router.push({ path: '/goods/edit' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/goods/edit', query: { id: row.id }})
    },
    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      if(confirm("确实要删除该商品吗？")==false){
        return;
      }
      deleteGoods(row).then(response => {
        this.$message.success('删除成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleDownloadAll() {
        this.downloadLoadingAll = true
        let allPagePara=this.listQuery
        allPagePara.page=1;
        allPagePara.limit=999999;
        listGoods(allPagePara).then(response => {
            let result = response.data.data.list
            import('@/vendor/Export2Excel').then(excel => {
                const tHeader = ['商品ID', '商品编号', '名称', '原价', '当前价格', '是否新品', '是否热品', '是否上架', '首页主图', '宣传图片列表', '商品介绍', '详细介绍', '商品图片', '商品单位', '关键字', '类目ID', '品牌商ID']
                const filterVal = ['id', 'goodsSn', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'brief', 'detail', 'picUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
                excel.export_json_to_excel2(tHeader, result, filterVal, '商品信息')

            })
            this.downloadLoadingAll = false
        }).catch(() => {
            this.downloadLoadingAll = false
        })


    }
  }
}
</script>
