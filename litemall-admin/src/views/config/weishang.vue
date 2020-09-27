<template>
  <div class="app-container">
    <el-form
      ref="dataForm"

      :model="dataForm"
      status-icon
      label-width="300px"
    >
      <el-tabs tab-position="left" >
        <el-tab-pane label="首页配置">
          <el-form-item label="会员专享栏目商品显示数量" prop="litemall_weishang_index_huiyuan">
            <el-input v-model="dataForm.litemall_weishang_index_huiyuan"/>
          </el-form-item>
          <el-form-item label="秒杀栏目商品显示数量" prop="litemall_weishang_index_miaosha">
            <el-input v-model="dataForm.litemall_weishang_index_miaosha"/>
          </el-form-item>
          <el-form-item label="团购栏目品牌商显示数量" prop="litemall_weishang_index_tuan">
            <el-input v-model="dataForm.litemall_weishang_index_tuan"/>
          </el-form-item>
          <el-form-item label="特产栏目显示数量" prop="litemall_weishang_index_buy">
            <el-input v-model="dataForm.litemall_weishang_index_buy"/>
          </el-form-item>
          <el-form-item label="首都机场精品栏目显示数量" prop="litemall_weishang_index_actstore">
            <el-input v-model="dataForm.litemall_weishang_index_actstore"/>
          </el-form-item>
          <el-form-item label="大兴机场精品栏目显示数量" prop="litemall_weishang_index_daxingstore">
            <el-input v-model="dataForm.litemall_weishang_index_daxingstore"/>
          </el-form-item>
<!--          <el-form-item label="猜你喜欢栏目商品显示数量" prop="litemall_weishang_index_guessyoulike">-->
<!--            <el-input v-model="dataForm.litemall_weishang_index_guessyoulike"/>-->
<!--          </el-form-item>-->
        </el-tab-pane>
<!--        <el-tab-pane label="其他配置">-->
<!--          <el-form-item label="商品分享功能" prop="litemall_wx_share">-->
<!--            <el-switch v-model="dataForm.litemall_wx_share"/>-->
<!--          </el-form-item>-->
<!--        </el-tab-pane>-->
      </el-tabs>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import { listWeishang, updateWx } from '@/api/config'

export default {
  name: 'ConfigWeishang',
  data() {
    return {
      dataForm: { }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      listWeishang().then(response => {
        this.dataForm = response.data.data
      })
    },
    cancel() {
      this.init()
    },
    update() {
      updateWx(this.dataForm)
        .then(response => {
          this.$message.success('首页配置成功');

        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    }
  }
}
</script>
