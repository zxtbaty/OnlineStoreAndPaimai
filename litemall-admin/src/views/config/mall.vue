<template>
  <div class="app-container">
    <el-form ref="dataForm" :model="dataForm" status-icon label-width="150px" style="width:600px">
      <!--<el-card class="box-card">-->
      <h3 class="title common-title left" >商城信息</h3>
      <el-row  >
        <el-col :span="12"  >
          <el-form-item label="商场名称" prop="litemall_mall_name">
            <el-input style="width:200px" v-model="dataForm.litemall_mall_name"/>
          </el-form-item>
        </el-col>
        <el-col :span="12"  style="padding-left: 50px;" >
          <el-form-item label="商场地址" prop="litemall_mall_address">
            <el-input  style="width:200px" v-model="dataForm.litemall_mall_address"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row  >
        <el-col :span="12"  >
          <el-form-item label="联系电话" prop="litemall_mall_phone">
            <el-input  style="width:200px" v-model="dataForm.litemall_mall_phone"/>
          </el-form-item>
        </el-col>
        <el-col :span="12"  style="padding-left: 50px;">
          <el-form-item label="联系QQ" prop="litemall_mall_qq">
            <el-input  style="width:200px" v-model="dataForm.litemall_mall_qq"/>
          </el-form-item>
        </el-col>
      </el-row>

      <h3>阿里短信设置</h3>
      <el-row>
        <el-col :span="24"  >
          <el-form-item label="阿里短信ID" prop="litemall_order_service_yunque">
            <el-input v-model="dataForm.sys_para_sms_access_key_id" class="input-width">
              <!--          <el-button slot="append" icon="el-icon-search"></el-button>-->
            </el-input>

          </el-form-item>
        </el-col>
        <el-col :span="24"  >
          <el-form-item label="阿里短信密码" prop="litemall_order_service_phone">
            <el-input v-model="dataForm.sys_para_sms_access_key_secret" class="input-width">
              <!--          <el-button slot="append" icon="el-icon-search"></el-button>-->
            </el-input>

          </el-form-item>
        </el-col>
        <el-col :span="24"  >
          <el-form-item label="短信模板编码" prop="litemall_order_service_phone">
            <el-input v-model="dataForm.sys_para_sms_template_code" class="input-width">
              <!--          <el-button slot="append" icon="el-icon-search"></el-button>-->
            </el-input>

          </el-form-item>
        </el-col>
      </el-row>
      <h3 class="title common-title left" >系统配置</h3>
      <el-row>
        <el-col :span="12"  >
          <el-form-item label="库存为零前端显示" prop="litemall_mall_goods_display_when_zero">
            <el-radio-group v-model="dataForm.litemall_mall_goods_display_when_zero">
              <el-radio :label="'false'">否</el-radio>
              <el-radio :label="'true'">是</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

      </el-row>
      <el-row>
        <el-form-item style="padding-left:150px">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="update">确定</el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { listMall, updateMall } from '@/api/config'

export default {
  name: 'ConfigMail',
  data() {
    return {
      dataForm: {
        litemall_mall_name: '',
        litemall_mall_address: '',
        litemall_mall_phone: '',
        litemall_mall_qq: '',
        litemall_mall_goods_display_when_zero:'true',
        sys_para_sms_access_key_id:'',
        sys_para_sms_access_key_secret:'',
        sys_para_sms_template_code:'',
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      listMall().then(response => {
        this.dataForm = response.data.data
      })
    },
    cancel() {
      this.init()
    },
    update() {
      updateMall(this.dataForm)
        .then(response => {
          this.$message.success('商场配置成功');
          this.init();
        })
        .catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
    }
  }
}
</script>
