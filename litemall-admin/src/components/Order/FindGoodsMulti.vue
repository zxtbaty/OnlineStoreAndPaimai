<template>
  <div>
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select :disabled="inComdId!=null" v-model="goodsQuery.comId" clearable class="filter-item" style="width: 180px;" placeholder="商贸类型" @change="getCatL1(goodsQuery.comId)" >
        <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-select v-model="goodsQuery.brandId" clearable class="filter-item" style="width: 180px;" placeholder="选择品牌">
        <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
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
      @selection-change="changeSelGoodsList">

      <el-table-column type="selection"/>

      <el-table-column align="center" label="商贸类型" prop="comName"/>

      <el-table-column align="center" label="商品编号" prop="goodsSn"/>

      <el-table-column align="center" min-width="100" label="名称" prop="name"/>

      <el-table-column v-if="goodsOrProduct=='goods'" align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column v-if="goodsOrProduct!='goods'" align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          {{scope.row.url}}
          <img :src="scope.row.url==null||scope.row.url==''?scope.row.picUrl:scope.row.url" width="40">
        </template>
      </el-table-column>

      <el-table-column v-if="goodsOrProduct=='goods'" align="center" label="原价" prop="counterPrice"/>
      <el-table-column v-if="goodsOrProduct=='goods'" align="center" label="当前价格" prop="retailPrice"/>

      <el-table-column  v-if="goodsOrProduct!='goods'" align="center" label="规格型号" prop="specifications"/>
      <el-table-column  v-if="goodsOrProduct!='goods'" align="center" label="货品价格" prop="price"/>
      <el-table-column  v-if="goodsOrProduct!='goods'" align="center" label="库存数量" prop="number"/>
      <el-table-column  v-if="goodsOrProduct!='goods'" align="center" label="预约数量" prop="yuyueNumber"/>
      <el-table-column  v-if="goodsOrProduct!='goods'" align="center" label="可用数量" prop="remainNumber"/>

      <el-table-column align="center" label="是否上架" prop="isOnSale">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '上架' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />

  </div>

</template>
<style scoped>
  .el-dialog__body {
    padding: 0px 20px;
    color: #606266;
    font-size: 14px;
  }
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
  import { allCompany } from '@/api/company'
  import { detailGoods, listCatAndBrand,listGoods,listGoodsByIdList,listProductByIdList,getGoodsProduct,
    listGoodsProduct,goodsMain,getGoodsProductViewById } from '@/api/goods'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

  export default {

    name: 'FindGoodsSingle',
    components: {Pagination},

    props:{
      listGoodsQuery: {

      },
      inComdId:{
        type:Number,
        default:null,
      },
      goodsOrProduct:{
        type:String,
        default:'goods',
      }
    },

    watch:{
      listGoodsQuery:{
        immediate:true,
        // deep:true,
        handler(val){
          this.goodsQuery=val;
          this.getGoodsList();
        }
      }
    },

    data() {
      return {
        checkBoxSelGoodsListData:[],//选择的商品数据
        listLoading:false,
        goodsTotal: 0, // 可推荐的数据列表总计
        goodsQuery:{
          page: 1,
          limit: 10,
        },
        selectGoodsId:undefined,
        // 可推荐列表查询
        // goodsQuery: {
        //   page: 1,
        //   limit: 10,
        //   comId:undefined,
        //   brandId:undefined,
        //   goodsSn: undefined,
        //   storeId: undefined,
        //   isOnSale:0,
        //   name: undefined,
        //   sort: 'add_time desc',
        // },

        comList:[],
        brandList:[],
        goodsList:[], // 可推荐数据列表
        returnGoods:[],
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
      this.getComList();
      this.getCatL1(this.goodsQuery.comId)
    },
    methods: {
      changeSelGoodsList(val){
        this.checkBoxSelGoodsListData = val
      },
      getComList() {
        allCompany().then(
          response => {
            this.comList = response.data.data.list
          }
        )
      },
      getCatL1(comId){
        this.categoryList=[];
        // this.goods.categoryId=null;
        this.brandList=[];
        // this.goods.brandId=null;
        listCatAndBrand(comId).then(response => {
          this.categoryList = response.data.data.categoryList
          this.brandList = response.data.data.brandList
        })
      },
      handleGoodsFilter() {
        this.goodsQuery.page = 1
        this.getGoodsList()
      },

      /**
       * 获取检索的商品列表
       */
      getGoodsList() {
        this.goodsList=[];
        // this.goodsList.forEach(item => {
        //   // 排他,每次选择时把其他选项都清除
        //     item.checked = false
        // })
        this.listLoading = true
        if(this.goodsOrProduct=="goods") {
          listGoods(this.goodsQuery).then(response => {
            this.goodsList = response.data.data.list
            this.goodsTotal = response.data.data.total
            this.listLoading = false
          }).catch(() => {
            this.goodsList = []
            this.goodsTotal = 0
            this.listLoading = false
          })
        } else
        {
          listGoodsProduct(this.goodsQuery).then(response => {
            this.goodsList = response.data.data.list
            this.goodsTotal = response.data.data.total
            this.listLoading = false
          }).catch(() => {
            this.goodsList = []
            this.goodsTotal = 0
            this.listLoading = false
          })
        }
      },

      addCheckData() {
        this.returnGoods=[];
        var listId=[];
        for (let selItem of  this.checkBoxSelGoodsListData) {
          listId.push(selItem.id);
         }
        if(listId.length==0){
          this.$message.error("请选择要返回的商品信息");
          return;
        }
        //单选按钮，增加时返回商品的信息
        if(this.goodsOrProduct=="goods"){
          listGoodsByIdList({ids:listId}).then(response => {
            var goodsData = response.data.data.list;
            if(goodsData!=null&&goodsData.length>0){
               goodsData.forEach(item=>{
                  var tempGoods={
                    goodsId: item.id,
                    goodsSn: item.goodsSn,
                    goodsName: item.name,
                    brandId: item.brandId,
                    brandName: item.brandName,
                    isOnSale: item.isOnSale,
                    picUrl: item.picUrl,
                    counterPrice: item.counterPrice,
                    retailPrice: item.retailPrice,
                    defaultProductId: item.defaultProductId,
                    comId: item.comId,
                    comName: item.comName,
                    ifXuni: item.ifXuni,
                    ifTicket: item.ifTicket,
                    ifListGoods: item.ifListGoods,
                    ifStoreBiggerZero: item.ifListGoods,
                    gallery:item.gallery,
                    galleryBig: item.galleryBig,
                    dajiapaiCategoryId: item.dajiapaiCategoryId,
                    dajiapaiCategoryCode: item.dajiapaiCategoryCode,
                    dajiapaiCategoryName: item.dajiapaiCategoryName,
                    privateCategoryId: item.privateCategoryId,
                    privateCategoryCode: item.privateCategoryCode,
                    privateCategoryName: item.privateCategoryName,
                    categoryId: item.categoryId,
                    brief:item.brief
                  }
                  this.returnGoods.push(tempGoods)
               })
              this.$emit("closeAndReturn",this.returnGoods);
            }

          })
        } else {
          listProductByIdList({ids:listId}).then(response => {
            var goodsData = response.data.data.list;
            if(goodsData!=null&&goodsData.length>0) {
              goodsData.forEach(item => {
                var tempProduct = {
                  goodsId: item.id,
                  goodsSn: item.goodsSn,
                  goodsName: item.name,
                  brandId: item.brandId,
                  brandName: item.brandName,
                  isOnSale: item.isOnSale,
                  picUrl: item.url == null ? item.picUrl : item.url,
                  counterPrice: item.counterPrice,
                  retailPrice: item.retailPrice,
                  defaultProductId: item.defaultProductId,
                  comId: item.comId,
                  comName: item.comName,
                  ifXuni: item.ifXuni,
                  ifTicket: item.ifTicket,
                  ifListGoods: item.ifListGoods,
                  ifStoreBiggerZero: item.ifListGoods,
                  specifications: item.specifications,
                  price: item.price,
                  number: item.number,
                  goodsPosKey: item.goodsPosKey,
                  yuyueNumber: item.yuyueNumber,
                  remainNumber: item.remainNumber,
                  gallery:item.gallery,
                  galleryBig: item.galleryBig,
                  dajiapaiCategoryId: item.dajiapaiCategoryId,
                  dajiapaiCategoryCode: item.dajiapaiCategoryCode,
                  dajiapaiCategoryName: item.dajiapaiCategoryName,
                  privateCategoryId: item.privateCategoryId,
                  privateCategoryCode: item.privateCategoryCode,
                  privateCategoryName: item.privateCategoryName,
                  categoryId: item.categoryId,
                  brief:item.brief
                }
                this.returnGoods.push(tempProduct);
              })
              this.$emit("closeAndReturn",this.returnGoods);
            }
          }).catch(() => {
          })
        }


      }
    },

  }
</script>
