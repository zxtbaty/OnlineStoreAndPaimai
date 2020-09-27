<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输活动名称"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
<!--      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 150px;" placeholder="请输入会员名称"/>-->
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/userGoodsRule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/userGoodsRule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      size="small"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row>
      <el-table-column align="center" label="操作" width="250px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/userGoodsRule/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/userGoodsRule/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-permission="['GET /admin/userGoodsOrders/list']" type="primary" size="mini" @click="findOrders(scope.row.id)">订单</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="活动名称" prop="name"/>
      <!--<el-table-column align="center" label="商品品类" prop="categoryName"/>-->
<!--      <el-table-column align="center" label="商品编号" prop="goodsSn"/>-->
<!--      <el-table-column align="center" min-width="100" label="名称" prop="goodsName"/>-->
<!--      <el-table-column align="center" min-width="100" label="货品规格" prop="goodsProductSpecifications"/>-->
<!--      <el-table-column align="center" label="原价" prop="sourcePrice"/>-->
<!--      <el-table-column align="center" label="会员价" prop="userPrice"/>-->
<!--      <el-table-column align="center" label="活动库存" prop="storeNum"/>-->
<!--      <el-table-column align="center" label="剩余库存" prop="remainNum"/>-->
<!--      <el-table-column align="center" label="单人单次" prop="onlyOne">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.onlyOne ? 'success' : 'error' ">{{ scope.row.onlyOne ? '是' : '否' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column align="center" label="商品种数" prop="goodsCount"/>
      <el-table-column align="center" label="开始时间" prop="beginDate"/>
      <el-table-column align="center" label="结束时间" prop="endDate"/>
      <el-table-column align="center" label="过期标志" prop="expireFlag">
          <template slot-scope="scope">
            <el-tag :type="scope.row.expireFlag ? 'success' : 'error' ">{{ scope.row.expireFlag ? '是' : '否' }}</el-tag>
          </template>
      </el-table-column>
      <el-table-column align="center" label="可用积分" prop="ifUseBonus">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifUseBonus ? 'success' : 'error' ">{{ scope.row.ifUseBonus ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="只能用积分购" prop="ifOnlyUseBonus">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifOnlyUseBonus ? 'success' : 'error' ">{{ scope.row.ifOnlyUseBonus ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="执行备注" prop="remark"/>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :rules="rules" :close-on-click-modal="false" width="1024px"  :visible.sync="dialogFormVisible"  >
      <el-tabs v-model="activeTabName" tab-position="top" type="border-card" >
        <el-tab-pane label="活动规则" name="活动规则">
          <div>
            <el-form ref="dataForm" :rules="rules" :model="dataForm"
                     status-icon  label-width="100px" >
              <el-col :span="12"  >
                <el-form-item label="活动名称" prop="name">
                  <el-input style="width: 200px" v-model="dataForm.name"/>
                </el-form-item>
              </el-col>
<!--              <el-col :span="12"  >-->
<!--                <el-form-item label="可用优惠券" prop="ifUseCoupon">-->
<!--                  <el-radio-group style="width: 200px" v-model="dataForm.ifUseCoupon" >-->
<!--                    <el-radio :label="false">否</el-radio>-->
<!--                    <el-radio :label="true">是</el-radio>-->
<!--                  </el-radio-group>-->
<!--                </el-form-item>-->
<!--              </el-col>-->
<!--              <el-col :span="12" >-->
<!--                <el-form-item label="可用积分" prop="ifUseBonus">-->
<!--                  <el-radio-group style="width: 200px" v-model="dataForm.ifUseBonus" @change="dataForm.ifOnlyUseBonus=false">-->
<!--                    <el-radio :label="false">否</el-radio>-->
<!--                    <el-radio :label="true">是</el-radio>-->
<!--                  </el-radio-group>-->
<!--                </el-form-item>-->
<!--              </el-col>-->

<!--              <el-col :span="12" >-->
<!--                <el-form-item label="只能积分购" prop="ifOnlyUseBonus"  v-if="dataForm.ifUseBonus">-->
<!--                  <el-radio-group style="width: 200px" v-model="dataForm.ifOnlyUseBonus">-->
<!--                    <el-radio :label="false">否</el-radio>-->
<!--                    <el-radio :label="true">是</el-radio>-->
<!--                  </el-radio-group>-->
<!--                </el-form-item>-->
<!--              </el-col>-->
              <el-col :span="12"  >
                <el-form-item label="开始时间" prop="beginDate">
                  <el-date-picker style="width: 200px"
                                  v-model="dataForm.beginDate"
                                  type="datetime"
                                  placeholder="选择日期"
                                  value-format="yyyy-MM-dd HH:mm:ss"/>
                </el-form-item>
              </el-col>
              <el-col :span="12" >
                <el-form-item label="过期时间" prop="endDate">
                  <el-date-picker style="width: 200px"
                                  v-model="dataForm.endDate"
                                  type="datetime"
                                  placeholder="选择日期"
                                  value-format="yyyy-MM-dd HH:mm:ss"/>
                </el-form-item>
              </el-col>
              <el-col :span="12" >
                <el-form-item label="执行备注" prop="remark">
                  <el-input style="width: 200px" :disabled="true" v-model="dataForm.remark"/>
                </el-form-item>
              </el-col>
            </el-form>
          </div>
        </el-tab-pane>
        <el-tab-pane label="活动商品" name="活动商品" v-if="dataForm.id!=undefined&&dataForm.id!=null">
          <div class="hot-header">
            <h3 class="title common-title left" >参与活动商品</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini"   type="primary" @click="handleGoodsModiShow">添加</el-button>
              <el-button v-show="!modiGodsVisiable"  size="mini"   type="primary" @click="handleGoodsModiDisplay">显示</el-button>
              <el-button v-show="modiGodsVisiable"  size="mini"  type="primary" @click="handleGoodsModiHide">隐藏</el-button>
            </div>
          </div>
          <el-form
            v-show="modiGodsVisiable"
            ref="refGoodsRuleGoodsForm"
            :model="goodsRuleGoodsForm"
            :rules="goodsModiRules"
            status-icon
            label-width="100px"
            style=" margin-left:50px;">

            <el-col :span="12">
              <el-form-item label="商品编号" prop="goodsSn" >
                <el-input style="width: 290px" v-model="goodsRuleGoodsForm.goodsSn"  clearable :readonly="true"  >
                  <el-button slot="append" type="primary"  icon="el-icon-search" @click="handleSelectGoods">选择</el-button>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="商品名称" prop="goodsName">
                <el-input style="width: 200px" v-model="goodsRuleGoodsForm.goodsName"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="商品规格" prop="goodsProductId">
                <el-select clearable v-model="goodsRuleGoodsForm.goodsProductId" style="width:200px;" @change="changeGoodsProduct" >
                  <el-option v-for="(item,index) in goodsProduct" :value="item.id"  :key="index" :label="item.specifications.toString()">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="剩余库存" prop="remainNum">
                <el-input style="width: 200px" :disabled="true" v-model="goodsRuleGoodsForm.remainNum"/>
              </el-form-item>
            </el-col>

            <el-col :span="24"  >
              <el-form-item label="商品图片" >
                <el-upload
                  :action="uploadPath"
                  :headers="headers"
                  :show-file-list="false"
                  :on-success="uploadPicUrl"
                  class="avatar-uploader"
                  accept=".jpg,.jpeg,.png,.gif">
                  <img style="width: 200px;height: 200px" v-if="goodsRuleGoodsForm.picUrl" :src="goodsRuleGoodsForm.picUrl" class="avatar">
                  <i v-else class="el-icon-plus avatar-uploader-icon"/>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="原价" prop="sourcePrice" >
                <el-input style="width: 200px" v-model="goodsRuleGoodsForm.sourcePrice" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="会员价" prop="huiYuanPrice">
                <el-input style="width: 200px" v-model="goodsRuleGoodsForm.huiYuanPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="活动库存" prop="storeNum">
                <el-input style="width: 180px"
                          :disabled="goodsRuleGoodsForm.id!=null&&goodsRuleGoodsForm.id!=undefined"
                          v-model="goodsRuleGoodsForm.storeNum"/>
              </el-form-item>
            </el-col>

            <el-col :span="12" >
              <el-form-item label="单人单次" prop="onlyOne">
                <el-radio-group style="width: 180px" v-model="goodsRuleGoodsForm.onlyOne">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="限购一件" prop="onlyBuyOne">
                <el-radio-group style="width: 200px" v-model="goodsRuleGoodsForm.onlyBuyOne">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="已销售数量" prop="havePayNum">
                <el-input style="width: 200px" :disabled="true" v-model="goodsRuleGoodsForm.havePayNum"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="未支付数量" prop="unPayNum">
                <el-input style="width: 200px" :disabled="true" v-model="goodsRuleGoodsForm.unPayNum"/>
              </el-form-item>
            </el-col>
            <!--            <el-col :span="12" style="text-align: center"  >-->
            <!--              <el-button  type="primary"   icon="el-icon-save"    @click="handleAddGoodsRow()">保存</el-button>-->

            <!--            </el-col>-->
          </el-form>
          <el-table ref="goodsRuleGoodsListTable" :data="goodsRuleGoodsList" height="450" border>
            <el-table-column
              sortable
              :default-sort = "{prop: 'index', order: 'descending'}"
              type="index"
              label="行号"
              width="50">
            </el-table-column>
            <el-table-column align="center" label="操作" width="140" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-if="scope.row.id!=null"  type="primary" size="mini" icon="el-icon-plus" style="width: 30px" title="加库存" @click="btnAddStoreNum(scope.row)"></el-button>
                <el-button  type="primary" size="mini" icon="el-icon-edit" style="width: 30px" title="修改" @click="handleGoodsRuleGoodsUpdate(scope.row)"></el-button>
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"
                            icon="el-icon-delete"  type="danger" size="mini" style="width: 30px" title="删除"  @click="handleGoodsRuleGoodsDelete(scope.row)"></el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" icon="el-icon-undo" style="width: 30px" title="撤销" @click="scope.row.deleted=0"></el-button>
              </template>
            </el-table-column>

            <el-table-column property="goodsName" label="品名">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsName}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsName}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="goodsProductSpecifications" label="规格">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsProductSpecifications}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsProductSpecifications}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="userId" label="原价">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.sourcePrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.sourcePrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="username" label="会员价">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.huiYuanPrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.huiYuanPrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="username" label="活动库存">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.storeNum}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.storeNum}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="username" label="已销数量">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.havePayNum}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.havePayNum}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="username" label="未支付数量">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.unPayNum}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.unPayNum}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="username" label="剩余库存">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.remainNum}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.remainNum}}
                </slot>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="活动用户" name="活动用户"  v-if="dataForm.id!=undefined&&dataForm.id!=null">
          <div class="hot-header">
            <h3 class="title common-title left" >参与活动会员</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini"   type="primary" @click="handleUserModiShow">添加</el-button>
              <el-button v-show="!modiUserVisiable"     size="mini"   type="primary" @click="handleUserModiDisplay">显示</el-button>
              <el-button v-show="modiUserVisiable"    size="mini"  type="primary" @click="handleUserModiHide">隐藏</el-button>
            </div>
          </div>
          <el-form
            v-show="modiUserVisiable"
            ref="goodsRuleUserForm"
            :model="goodsRuleUserForm"
            :rules="userModiRules"
            status-icon

            label-width="100px"
            style=" margin-left:50px;">

            <el-form-item label="会员分类1">
              <el-select clearable v-model="goodsRuleUserForm.userClassAttr1Code" style="width:400px;" @change="changeUserClass1" >
                <el-option  v-for=" item in userClass1" :value="item.code"  :key="item.code" :label="item.name">
                </el-option>
                <!--<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>-->
              </el-select>
            </el-form-item>

            <!--                  <el-form-item label="会员分类2"   prop="userClassAttr2Code">-->
            <!--                    <el-select clearable v-model="goodsRuleUserForm.userClassAttr2Code" style="width:400px;" @change="changeUserClass2" >-->
            <!--                      <el-option  v-for=" item in userClass2" :value="item.code"  :key="item.code" :label="item.name">-->
            <!--                      </el-option>-->
            <!--                      &lt;!&ndash;<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>&ndash;&gt;-->
            <!--                    </el-select>-->
            <!--                  </el-form-item>-->
            <el-form-item label="选择特定会员" prop="userId" >
              <el-input style="width: 400px" v-model="goodsRuleUserForm.userId"  clearable :readonly="true"  >
                <el-button slot="append" type="primary"  icon="el-icon-search" @click=" handleSelectUser">选择</el-button>
              </el-input>
            </el-form-item>


          </el-form>
          <el-table ref="goodsRuleUserListTable" :data="goodsRuleUserList" height="450" border>
            <el-table-column
              type="index"
              label="行号"
              width="50">
            </el-table-column>
            <el-table-column align="center" label="操作" width="170" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button   type="primary" size="mini" @click="handleGoodsRuleUserUpdate(scope.row)">编辑</el-button>
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleGoodsRuleUserDelete(scope.row)">删除</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
              </template>
            </el-table-column>
            <!--<template slot-scope="scope">-->
            <!--<s v-show="scope.row.deleted!=null&&scope.row.deleted==1">-->
            <!--</s>-->
            <!--</template>-->
            <el-table-column property="userClassAttr1Name" label="分类一名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userClassAttr1Name}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userClassAttr1Name}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="userClassAttr2Name" label="分类二名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userClassAttr2Name}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userClassAttr2Name}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="userId" label="会员Id">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userId}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userId}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="userNickname" label="会员名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.userNickname}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.userNickname}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="crmId" label="CRM会员卡">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.crmId}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.crmId}}
                </slot>
              </template>
            </el-table-column>

          </el-table>
        </el-tab-pane>

      </el-tabs>
      <div class="op-container" style="text-align: center;padding-top: 10px">
        <el-button type="primary" :disabled="dataForm.expireFlag==false" @click="updateExpireFlagFalse">正常可用</el-button>
        <el-button type="primary" :disabled="dataForm.expireFlag==true" @click="updateExpireFlagTrue">强制过期</el-button>
        <!--          <el-button v-if="dataForm.id!=null&&dataForm.id!=undefined" type="primary" @click="btnAddStoreNum">活动加库存</el-button>-->
        <el-button v-if="dataForm.id==null||dataForm.id==undefined" :loading="saveButtonStatus" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>

    </el-dialog>

    <!-- 选择商品对话框 -->
    <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogGoodsVisible"
               width="80%">
        <find-goods-single :listGoodsQuery="goodsQuery"
                           :inComdId="goodsQuery.comId" :goodsOrProduct="'goods'"
          @closeAndReturn="closeAndReturn"  ></find-goods-single>
<!--      <find-goods-multi :listGoodsQuery="goodsQuery"-->
<!--                         :inComdId="goodsQuery.comId" :goodsOrProduct="'goods'"-->
<!--                         @closeAndReturn="closeAndReturn"  ></find-goods-multi>-->
    </el-dialog>


    <!-- 选择会员对话框 -->
    <el-dialog :title="'选择会员'" :close-on-click-modal="false" :visible.sync="dialogSelUserVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->

        <div class="filter-container">

          <el-input v-model="userQuery.username" clearable class="filter-item" style="width: 150px;" placeholder="请输入用户名称"/>
          <el-input v-model="userQuery.nickname" clearable class="filter-item" style="width: 150px;" placeholder="请输入微信名称"/>
          <el-input v-model="userQuery.mobile" clearable class="filter-item" style="width: 150px;" placeholder="请输入手机号"/>
          <el-input v-model="userQuery.userClassAttr1Name" clearable class="filter-item" style="width: 150px;" placeholder="请输入分类属性1"/>
          <el-input v-model="userQuery.userClassAttr2Name" clearable class="filter-item" style="width: 150px;" placeholder="请输入分类属性2"/>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleUserFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckSelUserData">确定添加</el-button>
        </div>
        <!-- 查询结果 -->
        <el-table
          v-loading="listLoading"
          :data="userList"
          element-loading-text="正在查询中。。。"
          border
          fit
          highlight-current-row
          @selection-change="changeSelUserList">

          <el-table-column type="selection"/>

          <el-table-column align="center" label="用户名称" prop="username"/>
          <el-table-column align="center" label="网络名称" prop="nickname"/>
          <el-table-column align="center" label="手机号码" prop="mobile"/>
          <el-table-column align="center" label="微信OpenId" prop="weixinOpenid"/>
          <el-table-column align="center" label="用户分类1名称" prop="userClassAttr1Name"/>
<!--          <el-table-column align="center" label="用户分类2名称" prop="userClassAttr2Name"/>-->

        </el-table>
        <pagination v-show="userTotal>0" :total="userTotal" :page.sync="userQuery.page" :limit.sync="userQuery.limit" @pagination="getUserList()" />
      </div>

    </el-dialog>


    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
      <!-- 查询和其他操作 -->
      <div class="filter-container">
        <!--<el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>-->
        <el-input v-model="listOrderQuery.orderSn" clearable class="filter-item" style="width: 120px;"
                  placeholder="订单编号"/>
        <el-input v-model="listOrderQuery.username" clearable class="filter-item" style="width: 120px;"
                  placeholder="用户名称"/>
        <el-select v-model="listOrderQuery.orderStatusArray" multiple style="width: 150px" class="filter-item"
                   placeholder="请选择订单状态">
          <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
        </el-select>


        <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleFilterUserGoodsOrder">查找
        </el-button>
      </div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading"
                :data="listOrder" element-loading-text="正在查询中。。。"
                border fit highlight-current-row>
        <el-table-column type="selection"/>
        <el-table-column align="left" label="操作"  class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini"
                       @click="handleDetail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="200" label="订单编号" prop="orderSn"/>
        <!--          <el-table-column align="center" label="用户ID" prop="userId"/>-->
        <el-table-column align="center" min-width="90" label="订单状态" prop="orderStatusName">
        </el-table-column>
        <!--          <el-table-column align="center" label="订单金额" prop="orderPrice"/>-->
        <el-table-column align="center" label="支付金额" prop="actualPrice"/>
        <el-table-column align="center" label="支付时间" prop="payTime"/>
        <el-table-column align="center" label="物流单号" prop="shipSn"/>
        <el-table-column align="center" label="物流渠道" prop="shipChannel"/>

      </el-table>

      <pagination v-show="totalOrder>0" :total="totalOrder" :page.sync="listOrderQuery.page"
                  :limit.sync="listOrderQuery.limit" @pagination="handleFilterUserGoodsOrder"/>

    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 活动加库存 -->
    <el-dialog :title="'活动加库存'" customClass="customWidth" :close-on-click-modal="false"
               :visible.sync="addStoreNumVisible">
      <el-card class="box-card">
        <el-form ref="huoDongStoreForm"
                 status-icon  label-width="200px" >
          <el-col :span="24"  >
            <el-form-item label="活动库存"  >
              <el-input :disabled="true" style="width: 200px" v-model="goodsRuleGoodsForm.storeNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="剩余库存" prop="remainNum">
              <el-input :disabled="true" style="width: 200px" v-model="goodsRuleGoodsForm.remainNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="待支付数量" prop="unPayNum">
              <el-input :disabled="true" style="width: 200px" v-model="goodsRuleGoodsForm.unPayNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="增加库存数量(为负数为减少)"  >
              <el-input  style="width: 200px" v-model="addStoreNum"/>
            </el-form-item>
          </el-col>
        </el-form>
      </el-card>
      <div class="op-container" style="text-align: center;padding-top: 10px">
        <el-button type="primary"  @click="btnConfirmAddStoreNum">增加库存</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<style scoped>
  .el-dialog__body{padding:5px 30px}
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
import { listUsergoodsrule, deleteUsergoodsrule, addUsergoodsrule,
  updateUsergoodsrule,detailUsergoodsrule,addGoodsStoreNum,queryByName } from '@/api/userGoodsRule'
import { listGoods,getGoodsProduct,listGoodsProduct,goodsMain,goodsProductById } from '@/api/goods'
import { listMainOrder,listUserGoodsOrders } from '@/api/userGoodsOrder'
import { detailOrder} from '@/api/order'
import { listDiccode } from '@/api/diccode'
import { fetchList } from '@/api/user'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'
import { createStorage, uploadPath } from '@/api/storage'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'
import * as constIndex from '@/utils/index.js'

export default {

  name: 'UserGoodsRule',
  components: { BackToTop, Pagination,ViewOrderInfo,FindGoodsSingle,FindGoodsMulti },

  data() {
    return {
      statusMap:constIndex.statusMap,
      saveButtonStatus:false,
      uploadPath,
      activeTabName:'活动规则',

      modiGodsVisiable:false,
      modiUserVisiable:false,

      isRouteActive:true,
      goodsList: undefined, // 可推荐数据列表
      goodsTotal: 0, // 可推荐的数据列表总计
      goodsStoreNum:undefined,//当前规格型号库存
      userList:undefined,// 可选会员列表
      userTotal: 0, // 可推荐的数据列表总计
      checkBoxSelUserListData:[],//选择的用户数据
      postSaveData: [], // 要提交的选择数据

      selGoodsId:undefined,//选择的商品ID
      selGoodsSn:undefined,//选择的商品编号
      selGoodsName:undefined,//选择的商品名称
      selGoodsPrice:undefined,//选择的商品单价
      list: [],
      goodsRuleUserList:[],//会员活动用户列表
      goodsRuleGoodsList:[],//会员活动商品列表

      addStoreNum:undefined,//本次要增加的库存数量

      listOrder: [],
      totalOrder: 0,


      userClass1:[],
      userClass2:[],

      total: 0,
      listLoading: true,
      goodsProduct:[],
      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsSn:undefined,
        goodsName:undefined,
        name:undefined,
        username:undefined,
        expireFlag:0,
        sort: 'add_time desc',

      },
      listOrderQuery: {
          page: 1,
          limit: 10,
          id: undefined,
          name: undefined,
          username: undefined,
          consignee: undefined,
          sourceCode: undefined,
          typeCode: undefined,
          orderStatusArray: [],
          ifFapiao: undefined,
          fapiaoStatus: undefined,
          sendWay: undefined,
          orderType: "",
          userId:undefined,
          rulesId:undefined,
          sort: 'add_time desc',
      },
      orderDetail: {
          order: {},
          fapiao:{},//发票
          coupon:{},//优惠券
          user: {},
          orderGoods: []
      },
      // 可推荐列表查询
      goodsQuery: {
        goodsOrProduct:"goods",
        page: 1,
        limit: 10,
        goodsSn: undefined,
        comId: 1,
        storeId: undefined,
        isOnSale:1,
        name: undefined,
        ifNotIncludeHuodong:true,
        sort: 'add_time desc',

      },
      // 会员列表查询
      userQuery: {
        page: 1,
        limit: 10,
        username: undefined,
        nickname: undefined,
        mobile: undefined,
        userClassAttr1Name: undefined,
        userClassAttr2Name: undefined,
        sort: 'add_time desc',

      },
      downloadLoading: false,
      dataForm: {
        name:undefined,
        // comId:undefined,
        // comName:undefined,
        id: undefined,
        // productId:undefined,
        // goodsId:undefined,
        // goodsSn:undefined,
        // goodsName:undefined,
        // goodsProductId:undefined,
        // goodsProductSpecifications:[],
        // sourcePrice: undefined,
        // userPrice: undefined,
        // storeNum: undefined,
        // remainNum: undefined,
        // unPayNum: undefined,
        // havePayNum: undefined,
        beginDate: undefined,
        endDate: undefined,
        expireFlag:false,
        onlyOne:false,
        onlyBuyOne:false,
        remark: undefined,
        // picUrl: undefined,
        // freePost:false,
        ifOnlyUseBonus:false,
        ifUseCoupon:false,
        ifUseBonus:true,
        // goodsPosKey:undefined,
        goodsCount: undefined,
      },
      goodsRuleUserForm: {
        id: undefined,
        ruleId: undefined,
        userClassAttr1Code: undefined,
        userClassAttr1Name: undefined,
        userClassAttr2Code: undefined,
        userClassAttr2Name: undefined,
        userId: undefined,
        username: undefined,
        userNickname:undefined,
        crmId:undefined,
      },
      goodsRuleGoodsForm: {
        id: undefined,
        ruleId: undefined,
        comId:undefined,
        comName:undefined,
        goodsId: undefined,
        goodsProductId: undefined,
        goodsSn: undefined,
        goodsName: undefined,
        goodsProductSpecifications: undefined,
        picUrl: undefined,
        sourcePrice: undefined,
        huiYuanPrice: undefined,
        storeNum: undefined,
        remainNum: undefined,
        unPayNum: undefined,
        havePayNum: undefined,
        onlyOne: false,
        onlyBuyOne: false,
        goodsPosKey: undefined,
      },
      dialogFormVisible: false,
      dialogSelUserVisible:false,
      dialogGoodsVisible:false,
      orderFormVisible: false, //是否显示订单窗口
      orderDialogVisible: false, //是否显示订单详情
      orderDetailId:undefined,
      addStoreNumVisible: false, //是否显示增加活动库存
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {

        beginDate : [{ required: true, message: '必填', trigger: 'blur' }],
        endDate : [{ required: true, message: '必填', trigger: 'blur' }],
        userPrice: [{ required: true, message: '必填', trigger: 'blur' }],
      },
      userModiRules: {
         // name: [{ required: true, message: '代码名称不能为空', trigger: 'blur' }]
      },
      goodsModiRules: {
          // goodsSn: [{ required: true, message: '活动商品不能为空', trigger: 'blur' }],
          huiYuanPrice: [{ required: true, message: '会员价不能为空', trigger: 'blur' }],
          storeNum: [{ required: true, message: '活动库存不能为空', trigger: 'blur' }]
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
   // var a = a=this.goodsProduct;
    this.goodsProduct=[];
    this.goodsRuleUserList=[];
    //this.goodsProduct=a;
    this.getUserClass1()
    this.getUserClass2()
    this.getList()
  },
  methods: {
    closeAndReturn(val){
      console.log(val);
      this.dialogGoodsVisible=false;
      //返回的单一会员商品进行赋值
      if(val==null){
        return
      }
      this.goodsRuleGoodsForm.goodsProductId=null
      this.goodsRuleGoodsForm.goodsId=val.goodsId
      this.goodsRuleGoodsForm.goodsSn=val.goodsSn
      this.goodsRuleGoodsForm.goodsName=val.goodsName
      this.goodsRuleGoodsForm.sourcePrice=val.retailPrice
      this.goodsRuleGoodsForm.comId=val.comId
      this.goodsRuleGoodsForm.comName=val.comName
      this.goodsRuleGoodsForm.picUrl=val.picUrl
      this.dataForm.goodsProductSpecifications=val.specifications
      this.getListGoodsProduct()
    },
    handleAddGoodsRow(){
      this.$refs['refGoodsRuleGoodsForm'].validate((valid) => {
          if (valid) {
            // this.goodsRuleGoodsList.push(this.goodsRuleGoodsForm)
          }
        }
      )
    },

    btnConfirmAddStoreNum(){
      if(this.addStoreNum==null||this.addStoreNum==undefined){
        this.$message.error("请输入要增加的活动库存");
        return;
      }
      if(typeof this.addStoreNum === 'number' && this.addStoreNum%1 === 0)
      {
        this.$message.error("要增加的库存必须为整数");
        return;
      }
      if( (Number(this.goodsRuleGoodsForm.remainNum)+Number(this.addStoreNum))<0){
        this.$message.error("要增加的库存合计必须大于零");
        return;
      }
      addGoodsStoreNum({
        id:this.goodsRuleGoodsForm.id,
        addStoreNum:this.addStoreNum
      }).then(
        (res)=>{
          if(res.data.errno==0){
            this.addStoreNumVisible=false;
            this.dialogGoodsVisible=false;
            //返回调整后的活动规则重新加载
            // this.getListGoodsProduct();
            this.getGoodsRuleDetail();
          }
        }
      );
    },
    btnAddStoreNum(row){
      this.goodsRuleGoodsForm=row;
      // this.goodsRuleGoodsForm.id=row.id;
      // this.goodsRuleGoodsForm.storeNum=row.storeNum;
      // this.goodsRuleGoodsForm.remainNum=row.remainNum;
      // this.goodsRuleGoodsForm.unPayNum=row.unPayNum;
      this.addStoreNumVisible=true;
    },
    updateExpireFlagTrue(){
      if(confirm("确实要强制更新过期吗?")==false){
        return;
      }
      this.dataForm.expireFlag=true;
      this.updateData();
    },
    updateExpireFlagFalse(){
      if(confirm("确实要启动活动吗?")==false){
        return;
      }
      this.dataForm.expireFlag=false;
      this.updateData();
    },
    handleFilterUserGoodsOrder() {
        this.listOrderQuery.page = 1
        this.handleDetail(this.listOrderQuery.rulesId)
    },
    handleDetail(row) {
        // detailOrder(row.orderId).then(response => {
        //     this.orderDetail = response.data.data
        //     console.log(this.orderDetail)
        // })
      this.orderDetailId=row.orderId;
      this.orderDialogVisible = true
    },
    findOrders(rulesId) {
        this.orderFormVisible=true;
        this.listLoading = true
        this.listOrderQuery.rulesId=rulesId
        listMainOrder(this.listOrderQuery).then(response => {
            this.listOrder = response.data.data.list
            this.totalOrder = response.data.data.total
            this.listLoading = false
        }).catch(() => {
            this.listOrder = []
            this.totalOrder = 0
            this.listLoading = false
        })
    },

    changeGoodsProduct(){
      if(this.goodsProduct==null){
        return
      }
      // 晕，搞了好长时间，重新赋值，才会刷新页面
      var a =this.goodsProduct;
      this.goodsProduct=[];
      this.goodsProduct=a;
      const obj = this.goodsProduct.find((item) => {
        return item.id == this.goodsRuleGoodsForm.goodsProductId

      })
      if(obj==null){
        return;
      }
      this.changeSpecification(obj);

    },
    getUserClass1(){
      listDiccode({dicmainName:"会员分类1"}).then(response => {
        this.userClass1 = response.data.data.list
      }).catch(() => {
        this.userClass1 = []
      })
    },
    getUserClass2(){
      listDiccode({dicmainName:"会员分类2"}).then(response => {
        this.userClass2= response.data.data.list
      }).catch(() => {
        this.userClass2 = []
      })
    },
    getList() {
      this.listLoading = true
      listUsergoodsrule(this.listQuery).then(response => {
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
    resetForm() {
      this.dataForm = {
        name:undefined,
        comId:undefined,
        comName:undefined,
        id: undefined,
        productId:undefined,
        goodsId:undefined,
        goodsSn:undefined,
        goodsName:undefined,
        goodsProductId:undefined,
        goodsProductSpecifications:[],
        sourcePrice: undefined,
        userPrice: undefined,
        storeNum: undefined,
        remainNum: undefined,
        beginDate: undefined,
        endDate: undefined,
        expireFlag:false,
        onlyOne:false,
        onlyBuyOne:false,
        remark: undefined,
        picUrl: undefined,
        freePost:false,
        ifOnlyUseBonus:false,
        ifUseCoupon:false,
        ifUseBonus:false,
        goodsPosKey:undefined
      }
      this.goodsRuleUserForm= {
        id: undefined,
          ruleId: undefined,
          userClassAttr1Code: undefined,
          userClassAttr1Name: undefined,
          userClassAttr2Code: undefined,
          userClassAttr2Name: undefined,
          userId: undefined,
          username: undefined,
      }
      this.goodsRuleGoodsForm= {
          id: undefined,
          ruleId: undefined,
          comId:undefined,
          comName:undefined,
          goodsId: undefined,
          goodsProductId: undefined,
          goodsSn: undefined,
          goodsName: undefined,
          goodsProductSpecifications: undefined,
          sourcePrice: undefined,
          huiYuanPrice: undefined,
          storeNum: undefined,
          remainNum: undefined,
          unPayNum: undefined,
          havePayNum: undefined,
          onlyOne: false,
          onlyBuyOne: false,
          goodsPosKey: undefined,
      }
    },
    handleCreate() {
      this.activeTabName='活动规则'
      this.resetForm()
      this.goodsRuleUserList=[];
      this.goodsRuleUserForm=[];
      this.goodsRuleGoodsList=[];
      this.goodsRuleGoodsForm=[];
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    getRepeatVerify(){
      //提交前判断一下会员商品品项是否重复
      var repeatError='';
      var notNull='';
      if(this.goodsRuleGoodsList!=null&&this.goodsRuleGoodsList.length>0){
        for (var i = 0; i < this.goodsRuleGoodsList.length; i++) {
          if(this.goodsRuleGoodsList[i].goodsProductId==null||this.goodsRuleGoodsList[i].goodsProductId==undefined){
              notNull+='第'+(i+1)+'行记录的品项ID为空;\n';
          }
          if(this.goodsRuleGoodsList[i].storeNum==null||this.goodsRuleGoodsList[i].storeNum==undefined){
            notNull+='第'+(i+1)+'行活动库存为空;\n';
          }
          if(this.goodsRuleGoodsList[i].huiYuanPrice==null||this.goodsRuleGoodsList[i].huiYuanPrice==undefined){
            notNull+='第'+(i+1)+'行会员价格为空;\n';
          }
          var find = false;
          var findRow=i+1;
          for (var j = i + 1; j < this.goodsRuleGoodsList.length; j++) {
            if (this.goodsRuleGoodsList[i].goodsProductId == this.goodsRuleGoodsList[j].goodsProductId
              &&
              (this.goodsRuleGoodsList[i].deleted==0||this.goodsRuleGoodsList[i].deleted==undefined)
               &&
              (this.goodsRuleGoodsList[j].deleted==0||this.goodsRuleGoodsList[j].deleted==undefined) ) {
              findRow=(j+1);
              find = true;
              break;
            }
          }
          if (find) {
            repeatError+='第'+(i+1)+"行商品:【"+this.goodsRuleGoodsList[i].goodsName+"】，规格：【"+this.goodsRuleGoodsList[i].goodsProductSpecifications+"】"+
              '和第'+findRow+'行重复\n';
          }
        }
      }
      return repeatError+notNull;
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var repeatError= this.getRepeatVerify()
          if(repeatError!=''){
            alert(repeatError);
            // this.$message.error(repeatError);
            return;
          }

          this.saveButtonStatus=true;
          const goodsRuleData = {
            goodsRule: this.dataForm,
            goodsRuleUsers: this.goodsRuleUserList,
            goodsDetails:this.goodsRuleGoodsList
          }
          addUsergoodsrule(goodsRuleData).then(response => {
            // this.list.unshift(response.data.data)
            // this.dialogFormVisible = false
            this.dataForm=response.data.data;

            queryByName({ruleName:this.dataForm.name}).then(
              (res)=>{
                this.dataForm=res.data.data;
                this.activeTabName='活动商品';
                this.$message.success('创建会员专享活动规则成功,请配置商品和会员');
                this.saveButtonStatus=false;
              }
            ).catch((err)=>{
                this.saveButtonStatus=false
            } )



            //this.goodsProduct=[];
            //this.getList()
          }).catch(response => {
            this.saveButtonStatus=false;
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },

    handleUpdate(row) {
      this.resetForm()
      this.dataForm = Object.assign({}, row)
      // this.getListGoodsProduct();

      this.getGoodsRuleDetail();
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var repeatError= this.getRepeatVerify()
          if(repeatError!=''){
            alert(repeatError);
            // this.$message.error(repeatError);
            return;
          }
          const goodsRuleData = {
            goodsRule: this.dataForm,
            goodsRuleUsers: this.goodsRuleUserList,
            goodsDetails:this.goodsRuleGoodsList
          }
          updateUsergoodsrule(goodsRuleData).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新会员专享活动规则成功');
            this.goodsProduct=[];
            this.getList();
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);

          })
        }
      })
    },
    handleDelete(row) {
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deleteUsergoodsrule(row).then(response => {
        this.$message.success('删除会员专享活动规则成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleSelectGoods(){
      //this.dialogFormVisible=false;
      this.goodsList=[]
      this.selGoodsId=null
      this.selGoodsSn=null
      this.selGoodsName=null
      this.selGoodsPrice=null;
      this.dialogGoodsVisible=true;
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
    //以下处理选择商品事件

    changeSpecification(goodsProduct){
      this.goodsRuleGoodsForm.goodsProductId=goodsProduct.id
      this.goodsRuleGoodsForm.goodsProductSpecifications=goodsProduct.specifications
      //如果商品图片为空，则取商品基本信息表中的图片,不更新
      if(goodsProduct.url!=null&&goodsProduct.url!=""){
        this.goodsRuleGoodsForm.picUrl=goodsProduct.url
      }
      this.goodsRuleGoodsForm.goodsPosKey=goodsProduct.goodsPosKey
      this.goodsRuleGoodsForm.sourcePrice=goodsProduct.price
    },

    getListGoodsProduct(){
      if(this.goodsRuleGoodsForm.goodsId!=null){
          getGoodsProduct(this.goodsRuleGoodsForm.goodsId).then(
          (response)=>{
            this.goodsProduct=response.data.data.list;
            if(this.goodsProduct!=null&&this.goodsProduct.length>0)
            {
              this.changeSpecification(this.goodsProduct[0]);
            }
          }
        ).catch(() => {
            this.goodsProduct=[]
        })
      }
    },
    handleUserFilter () {
      this.userQuery.page = 1
      this.getUserList()
    },
    getUserList(){
     this.listLoading = true
     fetchList(this.userQuery).then(response => {
       this.userList = response.data.data.list
       this.userTotal = response.data.data.total
       this.listLoading = false
     }).catch(() => {
       this.userList = []
       this.userTotal = 0
       this.listLoading = false
     })
   },

    handleGoodsModiShow() {
      this.modiGodsVisiable = true
      this.goodsRuleGoodsForm = { onlyOne: false, onlyBuyOne: false, }
      this.goodsRuleGoodsList.push(this.goodsRuleGoodsForm)
      this.$nextTick(() => {
        // this.$refs.editTable.bodyWrapper.scrollTop = this.$refs.editTable.bodyWrapper.scrollHeight;
        this.$refs.goodsRuleGoodsListTable.bodyWrapper.scrollTop =this.$refs.goodsRuleGoodsListTable.bodyWrapper.scrollHeight;

      })
    },

    handleUserModiShow() {
      this.modiUserVisiable = true
      this.goodsRuleUserForm = { }
      this.goodsRuleUserList.push(this.goodsRuleUserForm)
      this.$nextTick(() => {
        // this.$refs.editTable.bodyWrapper.scrollTop = this.$refs.editTable.bodyWrapper.scrollHeight;
        this.$refs.goodsRuleUserListTable.bodyWrapper.scrollTop =this.$refs.goodsRuleUserListTable.bodyWrapper.scrollHeight;

      })
    },
    handleUserModiDisplay() {
      this.modiUserVisiable = true
    },
    handleGoodsModiDisplay() {
      this.modiGodsVisiable = true
    },
    handleUserModiHide() {
      this.modiUserVisiable = false
    },
    handleGoodsModiHide() {
      this.modiGodsVisiable = false
    },
    handleGoodsRuleUserDelete(row) {
      const index = this.goodsRuleUserList.indexOf(row)
      if (row.id == null) {
        this.goodsRuleUserList.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },
    handleGoodsRuleGoodsDelete(row) {
      const index = this.goodsRuleGoodsList.indexOf(row)
      if (row.id == null) {
        this.goodsRuleGoodsList.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },
    changeUserClass1(){

      if(this.userClass1==null){
        return
      }
      this.goodsRuleUserForm.userClassAttr1Name=null;
      // 晕，搞了好长时间，重新赋值，才会刷新页面
      const b =this.userClass1;
      this.userClass1=[];
      this.userClass1=b;
      const obj = this.userClass1.find((item) => {
        return item.code == this.goodsRuleUserForm.userClassAttr1Code
      })
      this.goodsRuleUserForm.userClassAttr1Name = obj.name

    },
    changeUserClass2(){
      if(this.userClass2==null){
        return
      }
      this.goodsRuleUserForm.userClassAttr2Name=null
      const obj = this.userClass2.find((item) => {
        return item.code == this.goodsRuleUserForm.userClassAttr2Code
      })
      this.goodsRuleUserForm.userClassAttr2Name = obj.name
     },
    handleGoodsRuleUserUpdate(row){
      //this.goodsRuleUserForm = Object.assign({}, row)
      this.goodsRuleUserForm=row;
      this.modiUserVisiable=true;
    },
    handleGoodsRuleGoodsUpdate(row){
      //this.goodsRuleUserForm = Object.assign({}, row)
      this.goodsRuleGoodsForm=row;
      this.modiGodsVisiable=true;
    },
    handleSelectUser(){
       this.dialogSelUserVisible=true;
       this.handleUserFilter()
    },

    getGoodsRuleDetail(){
      if(this.dataForm.id==null){
          this.dataForm = { onlyOne: true, expireFlag: false }
          this.goodsRuleUserList = []
        } else {
          detailUsergoodsrule({id:this.dataForm.id}).then(response => {
              this.dataForm = response.data.data.goodsRule
              this.goodsRuleUserList = response.data.data.goodsRuleUser,
              this.goodsRuleGoodsList = response.data.data.goodsDetails
            })
        }
      },
    changeSelUserList(val){
      this.checkBoxSelUserListData = val
    },

    addCheckSelUserData() {
      if(this.goodsRuleUserForm.userClassAttr1Code==null&&
        this.goodsRuleUserForm.userClassAttr2Code==null&&this.goodsRuleUserForm.userId==null){
        const index = this.goodsRuleUserList.indexOf(this.goodsRuleUserForm)

        this.goodsRuleUserList.splice(index,1)
        this.goodsRuleUserForm={}
      }
      for (let selItem of  this.checkBoxSelUserListData) {
        let boo = false;
        for (let user of  this.goodsRuleUserList) {
          if (selItem.id == user.userId) {
            boo = true;
            break;
          }
        }
        if (boo == false) {
          this.goodsRuleUserList.push({
            ruleId:this.dataForm.id,
            userId: selItem.id,
            username: selItem.username,
            userNickname: selItem.nickname,
            crmId: selItem.crmId,
            userClassAttr1Id: selItem.userClassAttr1Code,
            userClassAttr1Name: selItem.userClassAttr1Name,
            userClassAttr2Id: selItem.userClassAttr2Code,
            userClassAttr2Name: selItem.userClassAttr2Name,
          });
        }
      }
      this.dialogSelUserVisible=false
    },
    uploadPicUrl: function(response) {
      this.dataForm.picUrl = response.data.url
    },

},

}
</script>
