<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入活动名称"/>
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/huodongset/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/huodongset/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="活动ID" prop="id" sortable/>
      <el-table-column align="center" label="活动名称" prop="name" />
      <el-table-column align="left" label="开始时间" prop="beginDate" />
      <el-table-column align="left" label="截止时间" prop="endDate" />
      <el-table-column align="center" label="过期标志" prop="expireFlag">
        <template slot-scope="scope">

          <el-tag :type="scope.row.expireFlag ? 'true' : 'false' ">{{ scope.row.expireFlag ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" property="linkPicUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.linkPicUrl" width="40">
        </template>
      </el-table-column>
      <el-table-column align="center" label="排序" prop="sortOrder" sortable/>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/huodongset/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/huodongset/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 选择多选商品对话框 -->
    <el-dialog :title="'多选商品'" :close-on-click-modal="false"  :visible.sync="multiDialogGoodsVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container">
          <el-input v-model="goodsQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
          <el-input v-model="goodsQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
          <el-select v-model="goodsQuery.brandId" clearable class="filter-item" style="width: 150px;" placeholder="选择品牌">
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
          <el-cascader class="filter-item" ref="belongClass"  style="width: 150px;"  placeholder="分类" :options="categoryList"
                       v-model="categoryIds" expand-trigger="hover" @change="handleCategoryChange"/>

          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckSelGoodsData">确定添加</el-button>
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
          <!--<el-table-column type="selection"/>-->
          <el-table-column type="selection"/>
<!--          <el-table-column-->
<!--            label="操作"-->
<!--            width="55">-->
<!--            <template slot-scope="scope">-->
<!--              <el-checkbox v-model="scope.row.checked"></el-checkbox>-->
<!--            </template>-->
<!--          </el-table-column>-->

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
              <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '上架' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />

        <el-tooltip placement="top" content="返回顶部">

        </el-tooltip>

      </div>

    </el-dialog>
    <!-- 选择单选商品对话框 -->
    <el-dialog :title="'单选商品'" :close-on-click-modal="false"  :visible.sync="singleDialogGoodsVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container">
          <el-input v-model="goodsQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
          <el-input v-model="goodsQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
          <el-select v-model="goodsQuery.brandId" clearable class="filter-item" style="width: 150px;" placeholder="选择品牌">
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
          <el-cascader class="filter-item" ref="belongClass"  style="width: 150px;"  placeholder="分类" :options="categoryList"
                       v-model="categoryIds" expand-trigger="hover" @change="handleCategoryChange"/>

          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckSelGoodsData">确定添加</el-button>
        </div>
        <!-- 查询结果 -->
        <el-table
          v-loading="listLoading"
          :data="goodsList"
          element-loading-text="正在查询中。。。"
          border
          fit
          highlight-current-row
          @current-change="checkChange" >
          <el-table-column
            label="操作"
            width="55">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.checked"></el-checkbox>
            </template>
          </el-table-column>

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
              <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '上架' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />

        <el-tooltip placement="top" content="返回顶部">

        </el-tooltip>

      </div>

    </el-dialog>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" customClass="customWidth" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
      <el-card class="box-card">
        <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon  label-width="100px" >
          <el-col :span="12"  >
            <el-form-item label="活动名称"  prop="name">
              <el-input style="width: 200px" v-model="dataForm.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="开始时间" prop="beginDate">
              <el-date-picker style="width: 200px"
                              v-model="dataForm.beginDate"
                              type="datetime"
                              placeholder="选择日期"
                              value-format="yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="过期时间" prop="endDate">
              <el-date-picker style="width: 200px"
                              v-model="dataForm.endDate"
                              type="datetime"
                              placeholder="选择日期"
                              value-format="yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"    >
            <el-form-item label="活动排序" prop="sortOrder">
              <el-input style="width: 200px" v-model="dataForm.sortOrder"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="分享活动图(375*X)">
              <el-upload
                :action="uploadPath"
                :headers="headers"
                :show-file-list="false"
                :on-success="uploadLinkPicUrlPicUrl"
                :on-progress="uploadVideoProcess"
                class="avatar-uploader"
                accept=".jpg,.jpeg,.png,.gif">
                <img v-if="dataForm.linkPicUrl" :src="dataForm.linkPicUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"/>
                <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
              </el-upload>
            </el-form-item>
          </el-col>

          <el-col :span="24" >
            <el-form-item label="链接网址" prop="linkWebUrl">
              <el-input style="width: 550px" :value="'/pages/promotion/promotion?id='+dataForm.id" :disabled="true" />
            </el-form-item>
          </el-col>

        </el-form>
      </el-card>

      <el-card class="box-card">
        <div class="hot-header">
          <h3 class="title common-title left" >活动图片商品</h3>
          <div class="right" style="padding-top: 15px">
            <el-button  size="mini"   type="primary" @click="handlePicLinkShow">添加</el-button>
          </div>
        </div>
        <el-table :data="huodongPicGoodsList">
          <el-table-column align="center" property="picUrl" label="活动图片">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="40">
            </template>
          </el-table-column>
          <el-table-column property="goodsName"  label="商品名称" >
            <template slot-scope="scope"  >
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.goodsName}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.goodsName}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column align="center" property="goodsPicUrl" label="商品图片">
            <template slot-scope="scope">
              <img :src="scope.row.goodsPicUrl" width="40">
            </template>
          </el-table-column>

          <el-table-column property="sortOrder"  label="排序">
            <template slot-scope="scope"  >
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">
                  <el-input v-model="scope.row.sortOrder" style="width: 80px"/>
                </s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                <el-input v-model="scope.row.sortOrder" style="width: 80px"/>
              </slot>
            </template>
          </el-table-column>

          <el-table-column property="storeNum"  label="当前库存">
            <template slot-scope="scope"  >
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">
                  <el-input v-model="scope.row.storeNum" style="width: 80px"/>
                </s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                <el-input v-model="scope.row.storeNum" style="width: 80px"/>
              </slot>
            </template>
          </el-table-column>

          <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
            <template slot-scope="scope">

              <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handlePicGoodsDelete(scope.row)">删除</el-button>
              <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>

            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="box-card">
        <div class="hot-header">
          <h3 class="title common-title left" >列表商品</h3>
          <div class="right" style="padding-top: 15px">
            <el-button  size="mini"   type="primary" @click="handleHuodongAddGoods">添加</el-button>
          </div>
        </div>

        <el-table :data="huodongDisGoodsList">
          <el-table-column property="goodsName"  label="商品名称">
            <template slot-scope="scope"  >
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.goodsName}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.goodsName}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column align="center" property="goodsPicUrl" label="商品图片">
            <template slot-scope="scope">
              <img :src="scope.row.goodsPicUrl" width="40">
            </template>
          </el-table-column>

          <el-table-column property="sortOrder"  label="排序">
            <template slot-scope="scope"  >
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.sortOrder}}
                  <el-input v-model="scope.row.sortOrder" style="width: 80px"/>
                </s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                <el-input v-model="scope.row.sortOrder" style="width: 80px"/>
              </slot>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
            <template slot-scope="scope">

              <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleGoodsListDelete(scope.row)">删除</el-button>
              <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>

            </template>
          </el-table-column>
        </el-table>

      </el-card>


      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :disabled="dataForm.expireFlag==false" @click="handleStartHuodong" >活动手动启动</el-button>
        <el-button type="primary" :disabled="dataForm.expireFlag==true" @click="handleStopHuodong" >活动手动终止</el-button>
        <el-button type="primary" @click="previewHuodong" >预览</el-button>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogPicLinkVisible" title="修改活动图商品" :close-on-click-modal="false">
      <el-form ref="specForm"  :model="huodongPicGoodsForm" status-icon  label-width="100px" >
          <el-form-item label="活动图片(375*X)">
            <el-upload
              :action="uploadPath"
              :headers="headers"
              :show-file-list="false"
              :on-success="uploadHudongPicUrl"
              :on-progress="uploadVideoProcess"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif">
              <img v-if="huodongPicGoodsForm.picUrl" :src="huodongPicGoodsForm.picUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"/>
              <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
            </el-upload>
          </el-form-item>

          <el-form-item label="商品(可选)" prop="goodsName" >
            <el-input style="width: 220px" placeholder="请输入内容" v-model="huodongPicGoodsForm.goodsName"  clearable :readonly="true"  >
              <el-button slot="append" type="primary"  icon="el-icon-search" @click="handleSingleSelectGoods">选择</el-button>
            </el-input>
          </el-form-item>

          <el-form-item label="商品图片">
            <img :src="huodongPicGoodsForm.goodsPicUrl" width="80">
          </el-form-item>
          <el-form-item label="显示排序" prop="sortOrder">
            <el-input style="width: 200px" v-model="huodongPicGoodsForm.sortOrder"  />
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addPicGoods" >确定</el-button>
      </div>
    </el-dialog>



    <el-dialog :visible.sync="dialogPreviewVisible" title="查看活动列表" :close-on-click-modal="false">
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPreviewVisible=false" >关闭</el-button>
      </div>
      <div style="width: 500px">
        <div v-for="item in  huodongPicGoodsList">
          <img :key="item.picUrl" :src="item.picUrl"  >
        </div>
      </div>




    </el-dialog>

  </div>
</template>

<style>
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
.icon {
  margin-left: 8px;
}
.customWidth {
  width: 850px;
}

</style>

<script>
import { listHuodongset, createHuodongset, readHuodongset, deleteHuodongset,updateHuodongset } from '@/api/huodongset'
import { listGoods,listCatAndBrand} from '@/api/goods'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'hudongset',
  components: { Pagination },
  data() {
    return {
      selectSingleGoods:true,
      goodsList: undefined, // 可推荐数据列表
      goodsTotal: 0, // 可推荐的数据列表总计
      uploadPath,
      videoFlag:false,
      videoUploadPercent:0,
      list: [],
      huodongPicGoodsList:[],
      huodongDisGoodsList:[],
      categoryList:[],
      categoryIds: [],
      brandList:[],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        expireFlag: 0,
        sort: 'add_time desc',
      },
     checkBoxSelGoodsListData:[],//选择的商品数据
      // 可推荐列表查询
      goodsQuery: {
          page: 1,
          limit: 10,
          goodsSn: undefined,
          name: undefined,
          brandId: undefined,
          categoryId: undefined,
          comId: 1,
          isOnSale:1,
          sort: 'add_time desc',
      },
      dataForm: {
        id: undefined,
        name: undefined,
        beginDate: undefined,
        endDate: undefined,
        expireFlag: false,
        linkPicUrl: undefined,
        linkWebUrl: "/pages/promotion/promotion?id=",
        sortOrder: undefined,

      },
      huodongPicGoodsForm:{
        id: undefined,
        mainId: undefined,
        picUrl: undefined,
        goodsId: undefined,
        goodsName: undefined,
        goodsPicUrl: undefined,
        sortOrder: undefined,
        link: undefined,
      },
      huodongDisGoodsForm:{
        id: undefined,
        mainId: undefined,
        goodsId: undefined,
        goodsName: undefined,
        goodsPicUrl: undefined,
        sortOrder: undefined,
      },
      dialogFormVisible: false,
      dialogGoodsVisible:false,
      singleDialogGoodsVisible:false,
      multiDialogGoodsVisible:false,
      dialogPicLinkVisible:false,
      dialogPreviewVisible:false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '活动名称不能为空', trigger: 'blur' }
        ]

      },
      downloadLoading: false,
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
    this.getList()
  },
  methods: {
      handleStopHuodong(){
        if(this.dataForm.id==null||this.dataForm.id==undefined){
          this.$message.warning("该活动还未保存");
          return;
        }

        this.dataForm.expireFlag=true;
        this.updateData();
      },
      handleStartHuodong(){
        if(this.dataForm.id==null||this.dataForm.id==undefined){
          this.$message.warning("该活动还未保存");
          return;
        }

        this.dataForm.expireFlag=false;
        this.updateData();
      },
      previewHuodong(){
          this.dialogPreviewVisible=true;
      },
      // previewHuodong() {
      //     if(this.dataForm.id!=null){
      //         this.$router.push({ path: '/config/huodongsetpreview', query: { id: this.dataForm.id }})
      //     }
      // },
      addPicGoods() {
        this.dialogPicLinkVisible=false;
        // const index = this.huodongPicGoodsList.indexOf(this.huodongPicGoodsForm)
        this.huodongPicGoodsList.push(this.huodongPicGoodsForm)

    },
    checkChange(row) {
        if(row==null){
            return
        }


        this.huodongPicGoodsForm.goodsId=row.id
        this.huodongPicGoodsForm.goodsName=row.name
        this.huodongPicGoodsForm.goodsPicUrl=row.picUrl

        this.goodsList.forEach(item => {
            // 排他,每次选择时把其他选项都清除
            if (item.id !== row.id) {
                item.checked = false
            }
        })
    },
    handleSingleSelectGoods(){
        //this.selectSingleGoods=true;
        this.singleDialogGoodsVisible=true;
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

    changeSelGoodsList(val){
        this.checkBoxSelGoodsListData = val
    },
    handleHuodongAddGoods(){
        //this.selectSingleGoods=false;
        this.multiDialogGoodsVisible=true;
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
    uploadVideoProcess(event, file, fileList) {
        this.videoFlag = true;
        this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
    },
    uploadHudongPicUrl(response){
        this.videoFlag = false;
        this.videoUploadPercent = 0;
        this.huodongPicGoodsForm.picUrl = response.data.url
    },
    uploadLinkPicUrlPicUrl(response){
        this.videoFlag = false;
        this.videoUploadPercent = 0;
        this.dataForm.linkPicUrl = response.data.url

    },
    getList() {
      this.listLoading = true
      listHuodongset(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        name: undefined,
        beginDate: undefined,
        endDate: undefined,
        expireFlag: false,
        linkPicUrl: undefined,
        linkWebUrl: undefined,
        sortOrder: undefined
      };
      this.huodongPicGoodsList=[];
      this.huodongDisGoodsList=[];
    },

    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          const huodongset = {
              huodongMain: this.dataForm,
              huodongDetailPicLinks: this.huodongPicGoodsList,
              huodongDetailGoodsLists:this.huodongDisGoodsList
          }
            createHuodongset(huodongset)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$message.success('创建成功');
              this.getList();
            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      //this.storeBrandList=[];
      this. getHuodongsetMainAndDetail()
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    getHuodongsetMainAndDetail(){
      if(this.dataForm.id!=null){
        this.listLoading = true
          readHuodongset(this.dataForm.id).then(
          (response)=>{
            this.dataForm=response.data.data.huodongMain
            this.huodongPicGoodsList=response.data.data.huodongDetailPicLink
            this.huodongDisGoodsList=response.data.data.huodongDetailGoodsList
            this.listLoading = false
          }
        ).catch(() => {
          this.huodongPicGoodsList = []
          this.huodongDisGoodsList=[]
          this.listLoading = false
        })
      }
    },

    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
            const huodongset = {
                huodongMain: this.dataForm,
                huodongDetailPicLinks: this.huodongPicGoodsList,
                huodongDetailGoodsLists:this.huodongDisGoodsList
            }
            updateHuodongset(huodongset)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$message.success('更新活动成功');

            })
            .catch(response => {
              this.$message.error( '失败:'+response.data.errmsg);
            })
        }
      })
    },
    handleDelete(row) {

      if(confirm("确实要删除该记录吗？")==false){
        return;
      }
        deleteHuodongset(row)
        .then(response => {
          this.$message.success('删除成功');
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '公司ID',
          '活动名称',
          '活动起始时间',
          '活动截止时间',
          '是否过期',
          '活动头图',
          '排序'
        ]
        const filterVal = [
          'id',
          'name',
          'beginDate',
          'endDate',
          'expireFlag',
          'linkPicUrl',
          'sortOrder',
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '首页Icon信息')
        this.downloadLoading = false
      })
    },
    handlePicLinkShow() {
      this.dialogPicLinkVisible = true
      this.huodongPicGoodsForm={
          id: undefined,
          mainId: undefined,
          picUrl: undefined,
          goodsId: undefined,
          goodsName: undefined,
          goodsPicUrl: undefined,
          sortOrder: undefined,
          link: undefined,
      };
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

    addCheckData() {
      for (let selItem of  this.checkBoxData) {
        let boo = false;
        for (let storeBrand of  this.companyBrandList) {
          if (selItem.id == storeBrand.brandId) {
            boo = true;
            break;
          }
        }
        if (boo == false) {
          this.companyBrandList.push({
            comId: this.dataForm.id,
            comName: this.dataForm.name,
            brandId: selItem.id,
            brandName: selItem.name
          });
        }
      }
      this.dialogSelVisible=false
    },
    handlePicGoodsDelete(row) {
      const index = this.huodongPicGoodsList.indexOf(row)
      if (row.id == null) {
        this.huodongPicGoodsList.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },
    handleGoodsListDelete(row) {
        const index = this.huodongDisGoodsList.indexOf(row)
        if (row.id == null) {
            this.huodongDisGoodsList.splice(index, 1)
        } else {
            row.deleted = 1
        }
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
    handleCategoryChange(value) {
        this.goodsQuery.categoryId = value[value.length - 1]
        //this.goods.categoryName= value[value.length - 1]
    },
    /**
     * 进行检索
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
    handleGoodsFilter() {
        this.goodsQuery.page = 1
        this.getGoodsList()

    },
      addCheckSelGoodsData() {

          for (let selItem of  this.checkBoxSelGoodsListData) {
              let boo = false;
              for (let goods of  this.huodongDisGoodsList) {
                  if (selItem.id == goods.goodsId) {
                      boo = true;
                      break;
                  }
              }
              if (boo == false) {
                  this.huodongDisGoodsList.push({
                      mainId: this.dataForm.id,
                      goodsId:  selItem.id,
                      goodsName: selItem.name,
                      goodsPicUrl: selItem.picUrl,
                      sortOrder: undefined,
                  });
              }
          }
          this.singleDialogGoodsVisible=false;
          this.multiDialogGoodsVisible=false;

      },
  }
}

</script>
