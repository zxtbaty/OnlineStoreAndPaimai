<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>商品介绍
        <el-button :plain="true" type="primary" @click="copyFromOtherGoods">从其它商品复制</el-button>
        <slot v-if="goods.id==null">
          <el-button type="primary" :loading="exeUpdateLoading" @click="handleEdit(true)">保存</el-button>
        </slot>
        <slot v-else-if="goods.id!=null"  >
          <el-button type="primary"  :loading="exeUpdateLoading"   @click="handleEdit(true)">更新</el-button>
        </slot>
        <!-- 选择商品对话框 -->
        <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogGoodsVisible" width="80%">
          <div class="app-container">
            <!-- 查询和其他操作 -->
            <div class="filter-container">
              <el-select v-model="goodsQuery.comId" clearable class="filter-item" style="width: 180px;" placeholder="公司名称" @change="getCatL1(goodsQuery.comId)" >
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
              @current-change="checkChange">
              <!--<el-table-column type="selection"/>-->

              <el-table-column
                label="操作"
                width="55">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.checked"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column align="center" label="公司名称" prop="comName"/>

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

              <el-table-column align="center" label="原价" prop="counterPrice"/>

              <el-table-column align="center" label="当前价格" prop="retailPrice"/>

              <!--          <el-table-column align="center" label="是否新品" prop="isNew">-->
              <!--            <template slot-scope="scope">-->
              <!--              <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{ scope.row.isNew ? '新品' : '非新品' }}</el-tag>-->
              <!--            </template>-->
              <!--          </el-table-column>-->

              <!--          <el-table-column align="center" label="是否热品" prop="isHot">-->
              <!--            <template slot-scope="scope">-->
              <!--              <el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{ scope.row.isHot ? '热品' : '非热品' }}</el-tag>-->
              <!--            </template>-->
              <!--          </el-table-column>-->

              <el-table-column align="center" label="是否上架" prop="isOnSale">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '下架' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />

            <el-tooltip placement="top" content="返回顶部">
              <back-to-top :visibility-height="100" />
            </el-tooltip>

          </div>

        </el-dialog>


      </h3>

      <el-form ref="goods" :rules="rules" :model="goods" label-width="150px">
        <el-form-item label="应用范围">
          <el-select clearable v-model="goods.usedRange" >
            <el-option v-for="item in goodsUsedRanges" :key="item.name" :label="item.name" :value="item.name"/>
          </el-select>

<!--          <el-select clearable v-model="goods.usedRange" >-->
<!--            <el-option  key="电商专用" label="电商专用" value="电商专用"/>-->
<!--            <el-option  key="拍卖专用" label="拍卖专用" value="拍卖专用"/>-->
<!--            <el-option  key="私定专用" label="私定专用" value="私定专用"/>-->
<!--            <el-option  key="全场通用" label="全场通用" value="全场通用"/>-->
<!--          </el-select>-->
        </el-form-item>

        <el-form-item label="公司名称">
          <el-select clearable v-model="goods.comId" @change="changeComID(goods.comId)">
            <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>

        <el-form-item label="电商分类" v-if="goods.usedRange=='电商专用'" >
          <el-cascader ref="belongClass"
                       :options="categoryList" v-model="categoryIds"
                       expand-trigger="hover" @change="handleCategoryChange"/>
        </el-form-item>
        <el-form-item label="大家拍分类"  v-if="goods.usedRange=='拍卖专用'" >
          <el-select clearable v-model="goods.dajiapaiCategoryId" @change="handleCategoryDajiaPaiChange">
            <el-option v-for="item in categoryDajiaPaiList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="私人定制分类" v-if="goods.usedRange=='私定专用'">
          <el-select clearable v-model="goods.privateCategoryId" @change="handleCategoryPrivateMakeChange">
            <el-option v-for="item in categoryPrivateMakeList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>

        <el-form-item label="专栏/品牌">
          <el-select clearable v-model="goods.brandId" @change="handleBrandChange">
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="预约店铺" v-if="goods.comId!=1" prop="storeId">
          <el-select clearable v-model="goods.storeId" @change="changeStoreID(goods.storeId)">
            <el-option v-for="item in storeList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="选择出品人">
          <el-select style="width: 200px" clearable v-model="goods.authorId" @change="changeAuthorID(goods.authorId)" >
            <el-option  v-for="item in authorList" :key="item.id"
                        :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="商品编号" prop="goodsSn">
          <el-input v-model="goods.goodsSn"/>
        </el-form-item>
        <el-form-item label="商品69码" prop="goodsBarcode">
          <el-input v-model="goods.goodsBarcode"/>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="goods.name"/>
        </el-form-item>
        <el-form-item label="原价" prop="counterPrice">
          <el-input v-model="goods.counterPrice" placeholder="0.00" :disabled="true">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="默认价格" prop="retailPrice">
          <el-input v-model="goods.retailPrice" placeholder="0.00" :disabled="true">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="价格描述" prop="priceDesc">
          <el-input v-model="goods.priceDesc"   :disabled="true">

          </el-input>
        </el-form-item>

<!--        <el-form-item label="是否新品" prop="isNew">-->
<!--          <el-radio-group v-model="goods.isNew">-->
<!--            <el-radio :label="true">新品</el-radio>-->
<!--            <el-radio :label="false">非新品</el-radio>-->
<!--          </el-radio-group>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="是否热卖" prop="isHot">-->
<!--          <el-radio-group v-model="goods.isHot">-->
<!--            <el-radio :label="false">普通</el-radio>-->
<!--            <el-radio :label="true">热卖</el-radio>-->
<!--          </el-radio-group>-->
<!--        </el-form-item>-->
        <el-form-item label="是否上架" prop="isOnSale">
          <el-radio-group v-model="goods.isOnSale">
            <el-radio :label="true">上架</el-radio>
            <el-radio :label="false">上架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否虚拟商品" prop="ifXuni">
          <el-radio-group v-model="goods.ifXuni" @change="goods.ifTicket=false">
            <el-radio :label="false">否</el-radio>
            <el-radio :label="true">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否CRM购券" prop="ifTicket" v-if="goods.ifXuni">
          <el-radio-group v-model="goods.ifTicket">
            <el-radio :label="false">否</el-radio>
            <el-radio :label="true">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否列表商品" prop="ifXuni">
          <el-radio-group v-model="goods.ifListGoods">
            <el-radio :label="false">否</el-radio>
            <el-radio :label="true">是</el-radio>
          </el-radio-group>
        </el-form-item>
<!--        <el-form-item label="可用优惠券" prop="ifUseCoupon">-->
<!--          <el-radio-group v-model="goods.ifUseCoupon">-->
<!--            <el-radio :label="true">是</el-radio>-->
<!--            <el-radio :label="false">否</el-radio>-->
<!--          </el-radio-group>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="是否可用积分" prop="ifUseBonus">-->
<!--          <el-radio-group v-model="goods.ifUseBonus">-->
<!--            <el-radio :label="true">是</el-radio>-->
<!--            <el-radio :label="false">否</el-radio>-->
<!--          </el-radio-group>-->
<!--        </el-form-item>-->

        <el-form-item label="商品列表页头图(375*364)">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            :show-file-list="false"
            :on-success="uploadPicUrl"
            :on-progress="uploadVideoProcess"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif">
            <img v-if="goods.picUrl" :src="goods.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
            <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>

          </el-upload>
        </el-form-item>
        <el-form-item label="商品详情页大图(1125*1092)">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            :limit="5"
            :file-list="galleryBigFileList"
            :on-exceed="uploadOverrun"
            :on-success="handleGalleryBigUrl"
            :on-remove="handleRemoveGalaryBig"
            multiple
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture-card">
            <i class="el-icon-plus"/>
            <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品详情页头图(375*364)">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            :limit="5"
            :file-list="galleryFileList"
            :on-exceed="uploadOverrun"
            :on-success="handleGalleryUrl"
            :on-remove="handleRemove"
            multiple
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture-card">
            <i class="el-icon-plus"/>
            <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品单位">
          <el-input v-model="goods.unit" placeholder="件 / 个 / 盒"/>
        </el-form-item>

<!--        <el-form-item label="关键字">-->
<!--          <el-tag v-for="tag in keywords" :key="tag" closable type="primary" @close="handleClose(tag)">-->
<!--            {{ tag }}-->
<!--          </el-tag>-->
<!--          <el-input v-if="newKeywordVisible" ref="newKeywordInput" v-model="newKeyword" class="input-new-keyword" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"/>-->
<!--          <el-button v-else class="button-new-keyword" type="primary" @click="showInput">+ 增加</el-button>-->
<!--        </el-form-item>-->



        <el-form-item label="商品简介">
          <el-input v-model="goods.brief"/>
        </el-form-item>

        <el-form-item label="商品详细介绍(750*?)">
          <editor style="height: 500px" :init="editorInit" v-model="goods.detail"/>
        </el-form-item>
      </el-form>
    </el-card>

<!--    <el-card class="box-card">-->
<!--      <h3>商品店铺</h3>-->
<!--      <el-button :plain="true" type="primary" @click="handleStoreShow">添加</el-button>-->

<!--      <el-table :data="goodsStoreList">-->

<!--        <el-table-column property="storeId" label="店铺ID" >-->
<!--        <template slot-scope="scope">-->
<!--          <slot v-if="scope.row.deleted==1">-->
<!--            <s style="color: red">{{scope.row.storeId}}</s>-->
<!--          </slot>-->
<!--          <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >-->
<!--            {{scope.row.storeId}}-->
<!--          </slot>-->
<!--        </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column property="storeName" label="店铺名称" >-->
<!--        <template slot-scope="scope">-->
<!--          <slot v-if="scope.row.deleted==1">-->
<!--            <s style="color: red">{{scope.row.storeName}}</s>-->
<!--          </slot>-->
<!--          <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >-->
<!--            {{scope.row.storeName}}-->
<!--          </slot>-->
<!--        </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column align="center" min-width="50px" label="商品店铺码" prop="goodsPosKey">-->
<!--          <template slot-scope="scope">-->
<!--            <el-input v-model="scope.row.goodsPosKey"/>-->
<!--          </template>-->
<!--        </el-table-column>-->

<!--        <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="handleGoodsStoreUpdate(scope.row)">更新</el-button>-->

<!--            <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleGoodsStoreDelete(scope.row)">删除</el-button>-->
<!--            <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>-->

<!--          </template>-->
<!--        </el-table-column>-->
<!--      </el-table>-->

<!--      <el-dialog :visible.sync="storeVisiable" title="选择店铺">-->
<!--        <div class="app-container">-->
<!--          &lt;!&ndash; 查询和其他操作 &ndash;&gt;-->
<!--          <div class="filter-container">-->
<!--            <el-select-->
<!--              v-model="selComId"-->
<!--              :disabled="true"-->
<!--              clearable-->
<!--              class="filter-item">-->
<!--              <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>-->
<!--            </el-select>-->
<!--            &lt;!&ndash;<el-input v-model="selComName" :disabled="true" clearable class="filter-item" style="width: 150px;" />&ndash;&gt;-->

<!--            <el-input v-model="storeQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入店铺名称"/>-->
<!--            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleStoreFilter">查找</el-button>-->
<!--            <el-button type="primary" class="filter-item" @click="addStoreCheckData">确定添加</el-button>-->
<!--          </div>-->
<!--          &lt;!&ndash; 查询结果 &ndash;&gt;-->
<!--          <el-table-->
<!--            v-loading="listLoading"-->
<!--            :data="storeList"-->
<!--            element-loading-text="正在查询中。。。"-->
<!--            border-->
<!--            fit-->
<!--            highlight-current-row-->
<!--            @selection-change="checkStoreChange">-->

<!--            <el-table-column type="selection"/>-->

<!--            <el-table-column align="center" label="商贸类型" prop="comName"/>-->
<!--            <el-table-column align="center" min-width="100" label="店铺ID" prop="id"/>-->

<!--            <el-table-column align="center" min-width="100" label="店铺名称" prop="name"/>-->

<!--          </el-table>-->

<!--          <pagination v-show="storeTotal>0" :total="storeTotal" :page.sync="storeQuery.page" :limit.sync="storeQuery.limit" @pagination="getStoreList" />-->

<!--          <el-tooltip placement="top" content="返回顶部">-->
<!--            <back-to-top :visibility-height="100" />-->
<!--          </el-tooltip>-->

<!--        </div>-->

<!--      </el-dialog>-->
<!--    </el-card>-->


    <el-card class="box-card">
      <h3>商品规格</h3>
      <el-row>
        <el-button :plain="true" type="primary" @click="handleSpecificationShow">添加</el-button>
        <el-button :plain="true" type="primary" @click="specToProduct">刷新</el-button>
      </el-row>

      <el-table :data="specifications">
        <el-table-column property="specification" label="规格名" >
        <template slot-scope="scope">

            <slot v-if="scope.row.deleted==1">
              <s style="color: red">{{ scope.row.specification }}</s>
            </slot>
            <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
              {{ scope.row.specification }}
            </slot>

        </template>
        </el-table-column>
        <el-table-column property="value" label="规格值" >
          <template slot-scope="scope">
            <el-tag type="primary">
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{ scope.row.value }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                 {{ scope.row.value }}
              </slot>
            </el-tag>
          </template>
        </el-table-column>
<!--        <el-table-column property="picUrl" label="规格图片">-->
<!--          <template slot-scope="scope">-->
<!--            <img v-if="scope.row.picUrl" :src="scope.row.picUrl" width="40">-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
          <template slot-scope="scope">

            <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleSpecificationDelete(scope.row)">删除</el-button>
            <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="handleSpecificationDeleteUndo(scope.row)">撤销</el-button>

          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="specVisiable" width="600px" title="设置规格" :close-on-click-modal="false">
        <el-form ref="specForm" :rules="rules" :model="specForm" status-icon label-position="right" label-width="100px" >
          <el-form-item label="规格名" prop="specification">
            <el-input style="width: 250px"  v-model="specForm.specification"/>
          </el-form-item>
          <el-form-item label="规格值(多值用,分割)" prop="value">
            <el-input style="width: 250px"  v-model="specForm.value"/>
          </el-form-item>
<!--          <el-form-item label="规格图片" prop="picUrl">-->
<!--            <el-upload-->
<!--              :headers="headers"-->
<!--              :action="uploadPath"-->
<!--              :show-file-list="false"-->
<!--              :on-success="uploadSpecPicUrl"-->
<!--              class="avatar-uploader"-->
<!--              accept=".jpg,.jpeg,.png,.gif">-->
<!--              <img v-if="specForm.picUrl" :src="specForm.picUrl" class="avatar">-->
<!--              <i v-else class="el-icon-plus avatar-uploader-icon"/>-->
<!--            </el-upload>-->
<!--          </el-form-item>-->
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="specVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleSpecificationAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品库存</h3>
      <el-table :data="products">
        <el-table-column property="specifications" label="货品规格" >
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.specifications" :key="tag">
              <slot v-if="scope.row.deleted==1">
                <s style="color: red"> {{ tag }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{ tag }}
              </slot>
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column property="goodsPosKey" width="80" label="商品的店铺编码" v-if="goods.comId!=1">
          <template slot-scope="scope">

              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{ scope.row.goodsPosKey }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{ scope.row.goodsPosKey }}
              </slot>

          </template>
        </el-table-column>
        <el-table-column property="price" width="80" label="货品售价">
          <template slot-scope="scope">
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{ scope.row.price }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{ scope.row.price }}
              </slot>
          </template>
        </el-table-column>
        <el-table-column property="price" width="80" label="描述价格">
          <template slot-scope="scope">
            <slot v-if="scope.row.deleted==1">
              <s style="color: red">{{ scope.row.priceDesc }}</s>
            </slot>
            <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
              {{ scope.row.priceDesc }}
            </slot>
          </template>
        </el-table-column>
        <el-table-column property="number" width="80" label="总库存">
          <template slot-scope="scope">

              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{ scope.row.number }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{ scope.row.number }}
              </slot>
          </template>
        </el-table-column>
        <el-table-column property="number" width="80" label="预约数量">
          <template slot-scope="scope">

            <slot v-if="scope.row.deleted==1">
              <s style="color: red">{{ scope.row.yuyueNumber }}</s>
            </slot>
            <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
              {{ scope.row.yuyueNumber }}
            </slot>
          </template>
        </el-table-column>
        <el-table-column property="number" width="80" label="可用库存">
          <template slot-scope="scope">

            <slot v-if="scope.row.deleted==1">
              <s style="color: red">{{ scope.row.remainNumber }}</s>
            </slot>
            <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
              {{ scope.row.remainNumber }}
            </slot>
          </template>
        </el-table-column>
        <el-table-column property="minStorenum" width="80" label="最低库存">
          <template slot-scope="scope">
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{ scope.row.minStorenum }}</s>
              </slot>
              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{ scope.row.minStorenum }}
              </slot>
          </template>
        </el-table-column>
<!--        <el-table-column property="storeIds" width="200" label="预约店铺"  v-if="goods.comId!=1" >-->
<!--          <template slot-scope="scope">-->
<!--            <el-tag v-for="tag in scope.row.storeNames" :key="tag">-->
<!--              <slot v-if="scope.row.deleted==1">-->
<!--                <s style="color: red"> {{ tag }}</s>-->
<!--              </slot>-->
<!--              <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >-->
<!--                {{ tag }}-->
<!--              </slot>-->
<!--            </el-tag>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column property="url" width="100" label="货品图片">
          <template slot-scope="scope">
            <img v-if="scope.row.url" :src="scope.row.url" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作"   width="250" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleGoodsProductDelete(scope.row)">删除</el-button>
            <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
            <el-button type="primary" size="mini" @click="handleProductShow(scope.row)">设置</el-button>
          </template>
        </el-table-column>
      </el-table>

<!--      货品库存-->
      <el-dialog :visible.sync="productVisiable" width="600px"  title="设置货品" :close-on-click-modal="false">
        <el-form ref="productForm" :rules="rulesProduct" :model="productForm" status-icon label-position="right" label-width="130px" >
          <el-form-item label="商品ID" prop="id">
           {{productForm.id}}
          </el-form-item>
          <el-form-item label="货品规格列" prop="specifications">
            <el-tag v-for="tag in productForm.specifications" :key="tag">
              {{ tag }}
            </el-tag>
          </el-form-item>
          <el-form-item label="商品的店铺编码" prop="goodsPosKey"  v-if="goods.comId!=1">
            <el-input style="width: 250px" v-model="productForm.goodsPosKey"/>
          </el-form-item>
          <el-form-item label="货品售价" prop="price">
            <el-input style="width: 250px" v-model="productForm.price"/>
          </el-form-item>
          <el-form-item label="描述价格" prop="priceDesc">
            <el-input style="width: 250px" v-model="productForm.priceDesc"/>
          </el-form-item>
          <el-form-item label="总库存" prop="number">
            <el-input style="width: 250px" v-model="productForm.number"/>
          </el-form-item>
          <el-form-item label="预约数量" prop="yuyueNumber">
            <el-input style="width: 250px" :disabled="true" v-model="productForm.yuyueNumber"/>
          </el-form-item>
          <el-form-item label="可用库存" prop="remainNumber">
            <el-input style="width: 250px" :disabled="true" v-model="productForm.remainNumber"/>
          </el-form-item>
          <el-form-item label="库存报警数量" prop="minStorenum">
            <el-input style="width: 250px" v-model="productForm.minStorenum"/>
          </el-form-item>
<!--          <el-form-item label="可预约店铺" prop="storeIds"  v-if="goods.comId!=1">-->
<!--            <el-select clearable  style="width: 300px" @change="changeProductStoreID($event)" v-model="productForm.storeIds" multiple placeholder="请选择">-->
<!--              <el-option-->
<!--                v-for="item in storeList"-->
<!--                :key="item.id"-->
<!--                :label="item.name"-->
<!--                :value="item.id">-->
<!--              </el-option>-->
<!--            </el-select>-->
<!--          </el-form-item>-->

          <el-form-item label="货品图片" prop="url">
            <el-upload
              :headers="headers"
              :action="uploadPath"
              :show-file-list="false"
              :on-success="uploadProductUrl"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif">
              <img v-if="productForm.url" :src="productForm.url" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"/>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="productVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleProductEdit">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品参数</h3>
      <el-button :plain="true" type="primary" @click="handleAttributeShow">添加</el-button>
      <el-table :data="attributes">
        <el-table-column property="attribute" label="商品参数名称">
        <template slot-scope="scope">
          <slot v-if="scope.row.deleted==1">
            <s style="color: red">{{scope.row.attribute}}</s>
          </slot>
          <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
            {{scope.row.attribute}}
          </slot>
        </template>
        </el-table-column>
        <el-table-column property="value" label="商品参数值">

        <template slot-scope="scope">
          <slot v-if="scope.row.deleted==1">
            <s style="color: red">{{scope.row.value}}</s>
          </slot>
          <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
            {{scope.row.value}}
          </slot>
        </template>
        </el-table-column>

        <el-table-column property="ordernumber" label="排序">
          <template slot-scope="scope">
            <slot v-if="scope.row.deleted==1">
              <s style="color: red">{{scope.row.ordernumber}}</s>
            </slot>
            <slot v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
              {{scope.row.ordernumber}}
            </slot>
          </template>
        </el-table-column>

        <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button  type="primary" size="mini" @click="handleAttributeEdit(scope.row)">修改</el-button>
            <el-button  v-if="scope.row.deleted==0||scope.row.deleted==null"  type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
            <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
<!--      商品参数-->
      <el-dialog :visible.sync="attributeVisiable" width="600px" title="设置商品参数" :close-on-click-modal="false">
        <el-form ref="attributeForm" :model="attributeForm" status-icon label-position="right" label-width="100px" >
          <el-form-item label="商品参数名称" prop="attribute">
            <el-input style="width: 250px"  v-model="attributeForm.attribute"/>
          </el-form-item>
          <el-form-item label="商品参数值" prop="value">
            <el-input style="width: 250px"  v-model="attributeForm.value"/>
          </el-form-item>
          <el-form-item label="排序" prop="ordernumber">
            <el-input style="width: 250px"  v-model="attributeForm.ordernumber"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="attributeVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleAttributeAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <div class="op-container" style="text-align: center">

      <el-button type="primary" :loading="exeNewLoading" @click="handleAddNew">新增</el-button>
      <el-button @click="handleCancel">取消</el-button>

      <slot v-if="goods.id==null">
        <el-button type="primary" :loading="exeUpdateLoading" @click="handleEdit(true)">保存</el-button>
      </slot>
      <slot v-else-if="goods.id!=null"  >
        <el-button type="primary"  :loading="exeUpdateLoading"   @click="handleEdit(true)">更新</el-button>
      </slot>




    </div>

  </div>
</template>

<style>
.el-card {
  margin-bottom: 10px;
}
.el-tag + .el-tag {
  margin-left: 10px;
}
.input-new-keyword {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { detailGoods, editGoods, listCatAndBrand,listGoods } from '@/api/goods'
import { createStorage, uploadPath } from '@/api/storage'
import { listStore } from '@/api/store'
import { queryAllAuthororcorp } from '@/api/authororcorp'
import { allCompany } from '@/api/company'
import Editor from '@tinymce/tinymce-vue'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { listDiccode } from '@/api/diccode'

export default {
  name: 'GoodsEdit',
  components: { Editor, BackToTop, Pagination },
  data() {
    return {
      uploadPath,
      goodsUsedRanges:[],//商品应用范围 电商专用、拍卖专用、私定专用、全场通用
      videoFlag:false,
      videoUploadPercent:0,
      copyFromOtherGoodsId:undefined,
      goodsList: undefined, // 可推荐数据列表
      goodsTotal: 0, // 可推荐的数据列表总计
      listLoading:false,
      exeLoading:false,
      exeNewLoading:false,
      exeUpdateLoading:false,
      newKeywordVisible: false,
      newKeyword: '',
      authorList: [],//出品人
      keywords: [],
      storeList:[], //店铺列表
      checkBoxData: [], // 选择的店铺列表数据
      storeQuery:{
        page: 1,
        limit: 10,
        comId:undefined,
        name: undefined,
        sort: 'add_time desc',

      },
      storeTotal:undefined,
      storeVisiable:false,
      galleryBigFileList: [],
      galleryFileList: [],
      categoryList: [],
      categoryDajiaPaiList: [],
      categoryPrivateMakeList: [],
      brandList: [],
      comList:[],
      categoryIds: [],
      goods: {
        isNew:true,
        isHot:false,
        isOnSale:true,
        ifXuni:false,
        ifTicket:false,
        ifListGoods:true,
        ifUseCoupon:true,
        ifUseBonus:true,
        usedRange:"电商专用",
        galleryBig: [],
        gallery: []
      },
      goodsStoreList:[],
      specVisiable: false,

      selComId: undefined, // 选择的公司Id
      selComName: undefined, // 选择的公司名称
      specForm: {  specification: '', value: '', picUrl: '' },
      //specifications: [{ specification: '规格', value: '标准', picUrl: '' }],
      specifications: [],
      productVisiable: false,
      productForm: {
        id: 0,
        // specificationIds: [],
        specifications: [],
        goodsPosKey:undefined,
        price: 0.0,
        number: 0,
        yuyueNumber:0,
        remainNumber:0,
        minStorenum: 0,
        storeIds:[],
        storeNames:[],
        url: ''
      },
      // 可推荐列表查询
      goodsQuery: {
          page: 1,
          limit: 10,
          comId:undefined,
          brandId:undefined,
          goodsSn: undefined,
          storeId: undefined,
          isOnSale:1,
          name: undefined,
          ifNotIncludeHuodong:false,
          sort: 'add_time desc',

      },
      // products: [
      //   { id: 0, specifications: ['标准'], price: 0.0, number: 0, url: '' }
      // ],
      products: [],
      attributeVisiable: false,
      dialogGoodsVisible:false,
      attributeForm: {id:undefined, attribute: '', value: '',ordernumber:'' },
      attributes: [],
      rules: {
        goodsSn: [
          { required: true, message: '商品编号不能为空', trigger: 'blur' }
        ],
        storeId: [
            { required: true, message: '预约店铺不能为空', trigger: 'blur' }
        ],
        name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
        // retailPrice: [{ required: true, message: '商品当前价不能为空', trigger: 'blur' }]
      },
      rulesProduct: {
          goodsPosKey: [
              { required: true, message: '店铺商品编码不能为空', trigger: 'blur' }
          ],
          storeIds: [
              { required: true, message: '预约店铺不能为空', trigger: 'blur' }
          ]
      },
      editorInit: {
        language: 'zh_CN',
        convert_urls: false,
        plugins: [
          'advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'
        ],
        toolbar: [
          'searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample',
          'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'
        ],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData)
            .then(res => {
              success(res.data.data.url)
            })
            .catch(() => {
              failure('上传失败，请重新上传')
            })
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
    this.init()
  },
  methods: {
      getGoodsUsedRanges(){
        listDiccode({dicmainName:"商品应用范围"}).then(response => {
          this.goodsUsedRanges = response.data.data.list
        }).catch(() => {
          this.goodsUsedRanges = []
        })
      },
      getCategoryDajiaPaiList(){
          listDiccode({dicmainName:"商品分类_大家拍"}).then(response => {
              this.categoryDajiaPaiList = response.data.data.list
          }).catch(() => {
              this.categoryDajiaPaiList = []
          })
      },
      getCategoryPrivateMakeList(){
          listDiccode({dicmainName:"商品分类_私人定制"}).then(response => {
              this.categoryPrivateMakeList = response.data.data.list
          }).catch(() => {
              this.categoryPrivateMakeList = []
          })
      },

      getAllAuthororcorpList() {
          queryAllAuthororcorp()
              .then(response => {
                  this.authorList = response.data.data.list;
              })
              .catch(() => {
              })
      },
    uploadVideoProcess(event, file, fileList) {
        this.videoFlag = true;
        this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
    },
    getComList() {
      allCompany().then(
        response => {
          this.comList = response.data.data.list
          if (this.comList.length > 0) {
            this.selComId = this.comList[0].id
            this.selComName = this.comList[0].name
          }
        }
      )
    },
    init: function() {
      this.getComList()
      this.getAllAuthororcorpList();
      this.getCategoryDajiaPaiList();
      this.getCategoryPrivateMakeList();
      this.getGoodsUsedRanges();
      // listCatAndBrand(goods.comId).then(response => {
      //   this.categoryList = response.data.data.categoryList
      //   this.brandList = response.data.data.brandList
      // })
      if (this.$route.query.id == null) {
        return
      }
      const goodsId = this.$route.query.id
      detailGoods(goodsId).then(response => {
        this.goods = response.data.data.goods
        this.selComId=this.goods.comId
        this.getCatL1(this.selComId);
        console.log(response.data.data)
        //获取该分公司下的店铺信息
        this.getStoreList();
        this.specifications = response.data.data.specifications
        this.products = response.data.data.products
        this.attributes = response.data.data.attributes
        this.categoryIds = response.data.data.categoryIds
        this.goodsStoreList=response.data.data.stores
        this.galleryFileList = []
        for (var i = 0; i < this.goods.gallery.length; i++) {
          this.galleryFileList.push({
            url: this.goods.gallery[i]
          })
        }
        this.galleryBigFileList = []
        for (var i = 0; i < this.goods.galleryBig.length; i++) {
          this.galleryBigFileList.push({
            url: this.goods.galleryBig[i]
          })
        }
        const keywords = response.data.data.goods.keywords
        if (keywords !== null) {
          this.keywords = keywords.split(',')
        }

        this.brandId=this.goods.brandId;
        this.getStoreList();
      })


    },
    validateForm(){
      if(this.goods.comId!=1&&this.goods.storeId==null){
        return "当选择首都机场或大兴机场时必须要选择预约店铺";
      }
      if(this.specifications==null||this.specifications.length==0){
        return "商品必须至少有一种规格型号";
      }
      // if(this.goods.comId!=1&&this.goods.storeId==null){
      //   return "预约商品必须至少设置一个店铺";
      // }
      return null;
    },


      validateGoodsStoreForm(){
          if(this.productForm.goodsPosKey==null&&this.goods.comId!=1){
              return "线下预约商品必须设置在店铺对应的商品码";
          }

          return null;
      },

    handleCategoryChange(value) {
      this.goods.categoryId = value[value.length - 1]
      this.goods.categoryName=this.$refs['belongClass'].currentLabels
    },
    handleCategoryDajiaPaiChange(value) {

      this.goods.dajiapaiCategoryCode=undefined;
      this.goods.dajiapaiCategoryName=undefined;
      if(this.categoryDajiaPaiList==null){
          return
      }
      // 晕，搞了好长时间，重新赋值，才会刷新页面
      const b =this.categoryDajiaPaiList;
      this.categoryDajiaPaiList=[];
      this.categoryDajiaPaiList=b;
      const obj = this.categoryDajiaPaiList.find((item) => {
          return item.id == this.goods.dajiapaiCategoryId
      })
      if(obj==null){
        return;
      }
      this.goods.dajiapaiCategoryCode = obj.code
      this.goods.dajiapaiCategoryName = obj.name

    },
    handleCategoryPrivateMakeChange() {
        this.goods.privateCategoryCode = undefined
        this.goods.privateCategoryName = undefined
        if(this.categoryPrivateMakeList==null){
            return
        }
        // 晕，搞了好长时间，重新赋值，才会刷新页面
        const b =this.categoryPrivateMakeList;
        this.categoryPrivateMakeList=[];
        this.categoryPrivateMakeList=b;
        const obj = this.categoryPrivateMakeList.find((item) => {
            return item.id == this.goods.privateCategoryId
        })
        if(obj==null){
          return;
        }
        this.goods.privateCategoryCode = obj.code
        this.goods.privateCategoryName = obj.name
    },
    handleBrandChange(){
        if(this.brandList==null){
          return
        }
        // 晕，搞了好长时间，重新赋值，才会刷新页面
        const b =this.brandList;
        this.brandList=[];
        this.brandList=b;
        const obj = this.brandList.find((item) => {
          return item.value == this.goods.brandId
        })
        this.goods.brandName = obj.label

        //当商品品牌发生变化，更新一下相应的店铺信息
        this.brandId=this.goods.brandId;
        var that=this;
        that.goods.storeId=null;
        that.getStoreList();
    },
    handleCancel: function() {
      this.$router.push({ path: '/goods/list' })
    },
    handleEdit(ifReturn){
      var errMsg=this.validateForm();
      if(errMsg!=null){
        this.$message.error(errMsg);
        return;
      }
      this.exeUpdateLoading=true;
      const finalGoods = {
        goods: this.goods,
        specifications: this.specifications,
        products: this.products,
        attributes: this.attributes,
        stores:this.goodsStoreList
      }
      editGoods(finalGoods)
        .then(response => {
          this.exeUpdateLoading=false;
          if(this.goods.id==null)
          {
            if(ifReturn=null||ifReturn==true)
            {
              this.$message.success('创建成功');
              this.$router.push({ path: '/goods/list' })
            }
          } else
          {
            if(ifReturn=null||ifReturn==true) {
              this.$message.success('修改成功');
              //this.$router.push({ path: '/goods/list' })
              this.init()
            }
          }

          //this.$router.push({ path: '/goods/list' })
        })
        .catch(response => {
          this.exeUpdateLoading=false;
          MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
            confirmButtonText: '确定',
            type: 'error'
          })
        })
    },

    handleAddNew(){
      if(confirm("是否保存当前记录?")==true){
        var errMsg=this.validateForm();
        if(errMsg!=null){
          this.$message.error(errMsg);
          return;
        }
        this.exeNewLoading=true;
        const finalGoods = {
          goods: this.goods,
          specifications: this.specifications,
          products: this.products,
          attributes: this.attributes,
          stores:this.goodsStoreList
        }
        editGoods(finalGoods)
          .then(response => {
            this.exeNewLoading=false;
            this.goods={isNew:true,isHot:false,isOnSale:true,ifXuni:false,ifUseCoupon:true,ifUseBonus:true, gallery: [] };
            this.galleryFileList=[];
            this.goods.detail=null;
            this.comList=[];
            this.storeList=[];
            this.brandList=[];
            this.categoryList=[];
            this.specifications=[];
            this.specForm={  specification: '', value: '', picUrl: '' };
            this.attributes=[];
            this.attributeForm= { attribute: '', value: '' };
            this.products=[];
            this.productForm={
              id: 0,
              // specificationIds: [],
              specifications: [],
              goodsPosKey:undefined,
              price: 0.0,
              number: 0,
              minStorenum: 0,
              url: ''
            };

            //this.$router.push({ path: '/goods/list' })
          })
          .catch(response => {
            this.exeNewLoading=false;
            MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
              confirmButtonText: '确定',
              type: 'error'
            })
          })
      }
      this.goods={isNew:true,isHot:false,isOnSale:true,ifXuni:false,ifUseCoupon:true,ifUseBonus:true, gallery: [] };
      this.galleryFileList=[];
      this.goods.detail=null;
      this.comList=[];
      this.storeList=[];
      this.brandList=[];
      this.categoryList=[];
      this.specifications=[];
      this.specForm={  specification: '', value: '', picUrl: '' };
      this.attributes=[];
      this.attributeForm= { attribute: '', value: '' };
      this.products=[];
      this.productForm={
        id: 0,
          // specificationIds: [],
          specifications: [],
          goodsPosKey:undefined,
          price: 0.0,
          number: 0,
          minStorenum: 0,
          storeIds: [],
          url: ''
      };

    },
    handleClose(tag) {
      this.keywords.splice(this.keywords.indexOf(tag), 1)
      this.goods.keywords = this.keywords.toString()
    },
    showInput() {
      this.newKeywordVisible = true
      this.$nextTick(_ => {
        this.$refs.newKeywordInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const newKeyword = this.newKeyword
      if (newKeyword) {
        this.keywords.push(newKeyword)
        this.goods.keywords = this.keywords.toString()
      }
      this.newKeywordVisible = false
      this.newKeyword = ''
    },
    uploadPicUrl: function(response) {
        this.videoFlag = false;
        this.videoUploadPercent = 0;

        this.goods.picUrl = response.data.url


    },
    uploadOverrun: function() {
      this.$message({
        type: 'error',
        message: '上传文件个数超出限制!最多上传5张图片!'
      })
    },
    handleGalleryBigUrl(response, file, fileList) {
        this.videoFlag = false;
        this.videoUploadPercent = 0;

        if (response.errno === 0) {
            this.goods.galleryBig.push(response.data.url)
        }
    },
    handleGalleryUrl(response, file, fileList) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;

      if (response.errno === 0) {
        this.goods.gallery.push(response.data.url)
      }
    },
    handleRemoveGalaryBig: function(file, fileList) {
        for (var i = 0; i < this.goods.galleryBig.length; i++) {
            // 这里存在两种情况
            // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
            //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
            // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
            var url
            if (file.response === undefined) {
                url = file.url
            } else {
                url = file.response.data.url
            }
            if (this.goods.galleryBig[i] === url) {
                this.goods.galleryBig.splice(i, 1)
            }
        }
    },
    handleRemove: function(file, fileList) {
      for (var i = 0; i < this.goods.gallery.length; i++) {
        // 这里存在两种情况
        // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
        //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
        // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
        var url
        if (file.response === undefined) {
          url = file.url
        } else {
          url = file.response.data.url
        }
        if (this.goods.gallery[i] === url) {
          this.goods.gallery.splice(i, 1)
        }
      }
    },
    specChanged: function(label) {
      if (label === false) {
        this.specifications = [
          { specification: '规格', value: '标准', picUrl: '' }
        ]
        this.products = [
          { id: 0, specifications: ['标准'], price: 0.0, number: 0, url: '' }
        ]
      } else {
        this.specifications = []
        this.products = []
      }
    },
    uploadSpecPicUrl: function(response) {
      this.specForm.picUrl = response.data.url
    },
    handleSpecificationShow() {
      this.specForm = { specification: '', value: '', picUrl: '' }
      this.specVisiable = true
    },
      /**
       * 进行检索
       */
    copyFromOtherGoods() {
        this.goodsQuery.page = 1
        this.getGoodsList()
        this.dialogGoodsVisible=true;
    },
    /**
     * 获取检索的商品列表
     */
    getGoodsList() {
        this.listLoading = true
        listGoods(this.goodsQuery).then(response => {
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
        this.dialogGoodsVisible=true;
    },
    addCheckData() {
        this.dialogGoodsVisible=false;
        if(this.copyFromOtherGoodsId!=null){

            detailGoods(this.copyFromOtherGoodsId).then(response => {
                this.goods = response.data.data.goods
                this.selComId=this.goods.comId
                this.getCatL1(this.selComId);
                console.log(response.data.data)
                //获取该分公司下的店铺信息
                this.getStoreList();
                this.specifications = response.data.data.specifications
                this.products = response.data.data.products
                this.attributes = response.data.data.attributes
                this.categoryIds = response.data.data.categoryIds
                this.goodsStoreList=response.data.data.stores
                this.galleryFileList = []
                for (var i = 0; i < this.goods.gallery.length; i++) {
                    this.galleryFileList.push({
                        url: this.goods.gallery[i]
                    })
                }
                const keywords = response.data.data.goods.keywords
                if (keywords !== null) {
                    this.keywords = keywords.split(',')
                }

                this.brandId=this.goods.brandId;
                this.getStoreList();

                //设置商品的Id为null值
                this.goods.id=null;

            })

        }
    },
    checkChange(row) {
        if(row==null){
            return
        }
        this.copyFromOtherGoodsId=row.id
        this.goodsList.forEach(item => {
            // 排他,每次选择时把其他选项都清除
            if (item.id !== row.id) {
                item.checked = false
            }
        })
    },

    handleSpecificationAdd() {
      //将已经在商品规格中涉及到该规格的商品全部删除
      var valueArray=  this.specForm.value.split(",").reverse();
      var index = this.specifications.length - 1
      for (var valueIndex = 0; valueIndex < valueArray.length; valueIndex++) {
          //值
          var valuseSpecForm={  specification: this.specForm.specification, value: valueArray[valueIndex], picUrl: this.specForm.picUrl }
          for (var i = 0; i < this.specifications.length; i++) {
              const v = this.specifications[i]
              if (v.specification === valuseSpecForm.specification &&v.value=== valuseSpecForm.value) {
                  index = i
              }
          }
          this.specifications.splice(index + 1, 0, valuseSpecForm)
      }


      this.specVisiable = false
      //重新计算
      this.specToProduct()
    },
    handleSpecificationDelete(row) {
      //判断在商品库存表中，是否已经存在该规格型号，如果存在则先进行删除
      for(var ii=0;ii<this.products.length;ii++){
        var strSpecifications=this.products[ii].specifications.toString()+',';
        if(strSpecifications.indexOf(row.value.toString()+",")>=0){
          if(this.products[ii].id==null){
            this.products.splice(ii, 1)
          } else
          {
            this.products[ii].deleted=1
          }
        }
      }

      if(row.id==null){
        const index = this.specifications.indexOf(row)
        this.specifications.splice(index, 1)
      } else
      {
        row.deleted=1
      }

        this.specToProduct()
    },

      handleSpecificationDeleteUndo(row) {

          //判断在商品库存表中，是否已经存在该规格型号，如果存在则先进行删除
          row.deleted=0
          for(var ii=0;ii<this.products.length;ii++){
              var strSpecifications=this.products[ii].specifications.toString()+',';
              if(strSpecifications.indexOf(row.value.toString()+",")>=0){
                  this.products[ii].deleted=0

              }
          }

          //重新计算
          this.specToProduct()
      },

    handleGoodsProductDelete(row) {
      if(row.id==null){
        const index = this.products.indexOf(row)
        this.products.splice(index, 1)
      } else
      {
        row.deleted=1
      }
      // this.specToProduct()
    },
    specToProduct() {
      if (this.specifications.length === 0) {
        return
      }
      // 根据specifications创建临时规格列表
      var specValues = []
      var spec = this.specifications[0].specification
      var values = []
      values.push(0)

      for (var i = 1; i < this.specifications.length; i++) {
        const aspec = this.specifications[i].specification
        if (aspec === spec) {
          values.push(i)
        } else {
          specValues.push(values)
          spec = aspec
          values = []
          values.push(i)
        }
      }
      specValues.push(values)

      // 根据临时规格列表生产货品规格
      // 算法基于 https://blog.csdn.net/tyhj_sf/article/details/53893125
      var productsIndex = 0
      var products = []
      var combination = []
      var n = specValues.length
      for (var s = 0; s < n; s++) {
        combination[s] = 0
      }
      var index = 0
      var isContinue = false
      do {
        var specifications = []
        // var specificationIds=[]
        for (var x = 0; x < n; x++) {
          var z = specValues[x][combination[x]]
          specifications.push(this.specifications[z].value)
          // specificationIds.push(this.specifications[z].id)
        }
        //判断规格型号是否重复，如果重复，则不新增，如果不重复，则新增
        var hasFind=false;
        var itemIndex = 0;

        for ( itemIndex = 0; itemIndex < this.products.length; itemIndex++) {
          if(this.products[itemIndex].specifications.toString()==specifications.toString()){
            hasFind=true;
            break;
          }
        }
        if(hasFind==false){
          var newProduct = {
            id: undefined,
            // specificationIds: specificationIds,
            specifications: specifications,
            price: 0.0,
            number: 0,
            url: ''
          }
          products.push(newProduct);
        }else {

          for (var temp = 0; temp < this.specifications.length; temp++) {
              // if(this.products[itemIndex].specifications.toString().indexOf(this.specifications[temp].value.toString())>=0){
              if(this.products[itemIndex].specifications.toString()==this.specifications[temp].value.toString()){
                  this.products[itemIndex].deleted=this.specifications[temp].deleted;
                  break;
              }
          }
          products.push(this.products[itemIndex]);
          //删除已经复制的
          this.products.splice(itemIndex,1);
          //Object.assign(products[productsIndex],this.products[itemIndex]);
        }

        productsIndex++

        index++
        combination[n - 1] = index
        for (var j = n - 1; j >= 0; j--) {
          if (combination[j] >= specValues[j].length) {
            combination[j] = 0
            index = 0
            if (j - 1 >= 0) {
              combination[j - 1] = combination[j - 1] + 1
            }
          }
        }
        isContinue = false
        for (var p = 0; p < n; p++) {
          if (combination[p] !== 0) {
            isContinue = true
          }
        }
      } while (isContinue)
      //将那些没有复制，但id不为空的，将删除标记设置成1，再复制到新的
      for(var ii=0;ii<this.products.length;ii++){
        if(this.products[ii].id==null){
          this.products.splice(ii)
        } else
        {
          this.products[ii].deleted=1;
          products.push(this.products[ii]);
        }
      }

      this.products = products
    },
    handleProductShow(row) {
       // this.productForm={
       //      id: 0,
       //          // specificationIds: [],
       //          specifications: [],
       //          goodsPosKey:undefined,
       //          price: 0.0,
       //          number: 0,
       //          minStorenum: 0,
       //          storeIds:[],
       //          url: ''
       // }

      this.productForm = Object.assign({}, row);

      this.productVisiable = true
    },
    // changeProductStoreID(e){
    //     this.productForm.storeNames=[];
    //
    //     for(var i=0;i<e.length;i++){
    //         var storeId=e[i];
    //         var storeList = this.storeList.filter(item => item.id==storeId);
    //         console.log(storeList)
    //         if(storeList.length>0){
    //             this.productForm.storeNames.push(storeList[0].name);
    //         }
    //     }
    // },
    uploadProductUrl: function(response) {
      this.productForm.url = response.data.url
    },
    handleProductEdit() {
      var errMsg=this.validateGoodsStoreForm();
      if(errMsg!=null){
          this.$message.error(errMsg);
          return;
      }
      for (var i = 0; i < this.products.length; i++) {
        const v = this.products[i]
        if (v.specifications === this.productForm.specifications) {
          this.products.splice(i, 1, this.productForm)
          break
        }
      }
      this.productVisiable = false
    },
    handleAttributeShow() {
      this.attributeForm = {}
      this.attributeVisiable = true
    },
    handleAttributeAdd() {
        if(this.attributeForm.id!=null){
          //改变店铺id时修改名称
          let obj = this.attributes.find((item) => {
            return item.id == this.attributeForm.id
          })
          if((obj==undefined||obj.attribute==null)){
            this.attributes.unshift(this.attributeForm)
          } else
          {
            obj.attribute=this.attributeForm.attribute;
            obj.value=this.attributeForm.value;
            obj.ordernumber=this.attributeForm.ordernumber;
            //this.attributes.splice(index,1,this.attributeForm)
          }
          this.$forceUpdate();
          this.attributeVisiable = false
          return;
        }
        //改变店铺id时修改名称
        let obj = this.attributes.find((item) => {
            return item.attribute == this.attributeForm.attribute
        })
        if((obj==undefined||obj.attribute==null)){
            this.attributes.unshift(this.attributeForm)
        } else
        {
            obj.attribute=this.attributeForm.attribute;
            obj.value=this.attributeForm.value;
            obj.ordernumber=this.attributeForm.ordernumber;
            //this.attributes.splice(index,1,this.attributeForm)
        }
        this.$forceUpdate();
      this.attributeVisiable = false
    },
    handleAttributeEdit(row) {
        this.attributeForm = Object.assign({}, row);
        this.attributeVisiable = true

    },
    handleAttributeDelete(row) {
      if(row.id==null) {
        const index = this.attributes.indexOf(row)
        this.attributes.splice(index, 1)
      } else
      {
        row.deleted=1
      }
    },

    //************************************
    changeComID(comId){


      this.selComId=comId;

      const obj = this.comList.find((item) => {
        return item.id === comId
      })

      this.selComName=obj.name
      this.goods.comName=obj.name

      //添加店铺信息

      this.getStoreList();

      //添加品牌信息
      this.getCatL1(comId);

    },
    changeStoreID(storeId){

      //改变店铺id时修改名称
      const obj = this.storeList.find((item) => {
        return item.id === storeId
      })
      this.goods.storeName=obj.name
        this.$forceUpdate();
    },
      changeAuthorID(authorId){
          //改变店铺id时修改名称
          const obj = this.authorList.find((item) => {
              return item.id === authorId
          })
          if(obj==null){
            return;
          }
          this.goods.authorName=obj.name

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
    getStoreList() {
      this.storeList=[];
      // this.goods.storeId=null;
      listStore({ page: 1,
        limit: 10,
        comId:this.selComId,
        name: this.storeQuery.name,
        brandId:this.brandId,
        sort: 'add_time desc',
        }).then(
        response => {
          this.storeList = response.data.data.list;
          // if(this.storeList.length>0){
          //     if(this.brandId!=null){
          //         this.goods.storeId=this.storeList[0].id;
          //         this.goods.storeName=this.storeList[0].name;
          //     }
          //
          // }
        }
      )
    },
    handleStoreFilter() {
      this.storeQuery.page = 1
      this.getStoreList()
    },
    checkStoreChange(val) {
      this.checkBoxData = val
    },
    addStoreCheckData() {
      for (let selItem of  this.checkBoxData) {
        let boo = false;
        if(this.goodsStoreList!=null){
          for (let goodsStore of  this.goodsStoreList) {
            if (selItem.id == goodsStore.storeId) {
              boo = true;
              break;
            }
          }
        } else
        {
          this.goodsStoreList=[]
        }

        if (boo == false) {
          this.goodsStoreList.push({
            goodsId:this.goods.id,
            storeId: selItem.id,
            storeName: selItem.name,
          });
        }
      }
      this.storeVisiable=false
    },
    handleStoreShow() {
      this.storeVisiable = true
      this.storeQuery.page = 1
      this.getStoreList()

    },
    handleGoodsStoreDelete(row){
      if(row.id==null){
        const index = this.goodsStoreList.indexOf(row)
        this.goodsStoreList.splice(index, 1)

      } else
      {
        row.deleted=1
      }
    }
  },
  handleGoodsStoreUpdate(row){
    //if(this.goods.id!=null){
      this.handleEdit()
    //}
  },

}
</script>
