<template>

  <el-container>
    <div style="width:850px;"  >
      <el-card class="box-card">

        <el-form ref="dataForm" :rules="rules" :model="dicmain" label-width="120px">
          <el-col :span="12">
          <el-form-item label="字典名称" prop="name">
            <el-input  style="width: 200px" v-model="dicmain.name"/>
          </el-form-item>
          </el-col>
          <el-col :span="12" >
          <el-form-item label="字典描述" prop="desp">
            <el-input style="width: 200px"  v-model="dicmain.desp" />
          </el-form-item>
          </el-col>
          <el-col :span="12">
          <el-form-item label="字典排序值" prop="ordernumber">
            <el-input style="width: 200px"  v-model="dicmain.ordernumber" />
          </el-form-item>
          </el-col>
          <el-col :span="12">
          <el-form-item label="是否系统" prop="systemed">
            <el-radio-group style="width: 200px"  v-model="dicmain.systemed">
              <el-radio :label="true">系统</el-radio>
              <el-radio :label="false">用户</el-radio>
            </el-radio-group>
          </el-form-item>
          </el-col>
          <el-col :span="12">
          <el-form-item label="是否启用" prop="enabled">
            <el-radio-group style="width: 200px"  v-model="dicmain.enabled">
              <el-radio :label="true">启用</el-radio>
              <el-radio :label="false">不启用</el-radio>
            </el-radio-group>
          </el-form-item>
          </el-col>

        </el-form>
      </el-card>

      <el-card class="box-card">

        <div class="hot-header">
          <h3 class="title common-title left" >代码列表</h3>
          <div class="right" style="padding-top: 15px">
            <el-button  size="mini"   type="primary" @click="handleDiccodeShow">添加</el-button>
            <el-button v-show="!dicCodeVisiable"     size="mini"   type="primary" @click="handleDiccodeDisplay">显示</el-button>
            <el-button v-show="dicCodeVisiable"    size="mini"  type="primary" @click="handleDiccodeHide">隐藏</el-button>
           </div>
        </div>


        <!--<el-dialog :visible.sync="dicCodeVisiable" title="设置字典代码">-->
        <el-form
          v-show="dicCodeVisiable"
          ref="dicCodeForm"
          :model="dicCodeForm"
          :rules="dialogrules"
          status-icon

          label-width="100px"
          >

          <el-col :span="12">
            <el-form-item label="代码编码" prop="code">
              <el-input v-model="dicCodeForm.code" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="代码名称" style="padding-left: 70px" prop="name">
              <el-input v-model="dicCodeForm.name" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码排序" prop="ordernumber">
              <el-input v-model="dicCodeForm.ordernumber" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码备注" style="padding-left: 70px" prop="remark">
              <el-input v-model="dicCodeForm.remark" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="系统标记" prop="systemed">
              <el-radio-group v-model="dicCodeForm.systemed" style="width: 140px">
                <el-radio :label="true">系统</el-radio>
                <el-radio :label="false">用户</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="停用标记" prop="enabled">
              <el-radio-group v-model="dicCodeForm.enabled" style="width: 150px">
                <el-radio :label="true">启用</el-radio>
                <el-radio :label="false">不启用</el-radio>
              </el-radio-group>

            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性1" prop="attr1">
              <el-input v-model="dicCodeForm.attr1" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性2" style="padding-left: 70px" prop="attr2">
              <el-input v-model="dicCodeForm.attr2" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性3" prop="attr3">
              <el-input v-model="dicCodeForm.attr3" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性4" style="padding-left: 70px" prop="attr4">
              <el-input v-model="dicCodeForm.attr4" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性5" prop="attr5">
              <el-input v-model="dicCodeForm.attr5" style="width: 150px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码属性6" style="padding-left: 70px" prop="attr6">
              <el-input v-model="dicCodeForm.attr6" style="width: 150px"/>
            </el-form-item>
          </el-col>

        </el-form>

        <el-table :data="diccodes">
          <el-table-column property="code" label="代码编码">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.code}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.code}}
                </slot>
              </template>
          </el-table-column>

          <el-table-column property="name" label="代码名称">
            <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.name}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.name}}
              </slot>
            </template>
          </el-table-column>

          <el-table-column property="ordernumber" label="代码排序">
            <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.ordernumber}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.ordernumber}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column property="remark" label="代码备注">
            <template slot-scope="scope"  >
              <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
              <!--{{scope.row.brandName}}-->
              <!--</slot>-->
              <slot v-if="scope.row.deleted==1">
                <s style="color: red">{{scope.row.remark}}</s>
              </slot>
              <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                {{scope.row.remark}}
              </slot>
            </template>
          </el-table-column>
          <el-table-column property="systemed" label="系统标记" >
            <template slot-scope="scope">
              <el-tag :type="scope.row.systemed ? 'success' : 'error' ">{{ scope.row.systemed ? '系统' : '用户' }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column property="enabled" label="停用标记">
            <template slot-scope="scope">
              <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleDicodeEdit(scope.row)">编辑</el-button>

              <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleDicodeDelete(scope.row)">删除</el-button>
              <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>

            </template>
          </el-table-column>
        </el-table>

      </el-card>

      <div class="op-container" style="text-align: center">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存字典</el-button>
      </div>

    </div>
  </el-container>

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
import { createDicmain, updateDicmain, detailDicmain } from '@/api/dicmain'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'

export default {
  name: 'DicmainEdit',
  // props: ['mainId'],
  props: {
    mainId: 0,
    changeDispaly:true
  },
  data() {
    return {
      dicmain: {
        id: undefined,
        name: undefined,
        desp: undefined,
        ordernumber: 0,
        systemed: false,
        enabled: true
      },
      diccodes: [],
      dicCodeVisiable: false,
      dicCodeForm: {
        id: undefined,
        mainid: undefined,
        mainname: undefined,
        code: undefined,
        name: undefined,
        ordernumber: undefined,
        remark: undefined,
        systemed: false,
        enabled: true,
        attr1: undefined,
        attr2: undefined,
        attr3: undefined,
        attr4: undefined,
        attr5: undefined,
        attr6: undefined
      },
      rules: {
        name: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }]
      },
      dialogrules: {
        name: [{ required: true, message: '代码名称不能为空', trigger: 'blur' }]
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

  watch: {
    mainId: function() {
      // // your code
      if (this.mainId == null) {
        this.dicmain = { enabled: true, systemed: false }
        this.diccodes = []
      } else {
        detailDicmain(this.mainId).then(response => {
          this.dicmain = response.data.data.dicmain
          this.diccodes = response.data.data.diccodes
        })
      }
    },
    changeDispaly: function() {
        this.dicmain={};
        this.diccodes=[];
        this.dicCodeForm={};
        if(this.mainId==null){
            return
        }
        detailDicmain(this.mainId).then(response => {
            this.dicmain = response.data.data.dicmain
            this.diccodes = response.data.data.diccodes
        })
    }
  },

  created() {
    this.dicmain={};
    this.diccodes=[];
    this.dicCodeForm={};
    detailDicmain(this.mainId).then(response => {
      this.dicmain = response.data.data.dicmain
      this.diccodes = response.data.data.diccodes
    })

    // if (this.mainId == null) {
    //   this.dicmain = { enabled: true, systemed: false }
    //   this.diccodes = []
    //
    // } else {
    //   detailDicmain(this.mainId).then(response => {
    //     this.dicmain = response.data.data.dicmain
    //     this.diccodes = response.data.data.diccodes
    //   })
    // }
  },

  methods: {
    resetForm() {
      this.dicCodeForm = {
        id: undefined,
        mainid: undefined,
        mainname: undefined,
        code: undefined,
        name: undefined,
        ordernumber: undefined,
        remark: undefined,
        systemed: false,
        enabled: true,
        attr1: undefined,
        attr2: undefined,
        attr3: undefined,
        attr4: undefined,
        attr5: undefined,
        attr6: undefined
      }
    },
    handleCancel: function() {
      // this.$router.push({ path: '/sys/dicmain/list' })
      this.$emit('lisenChildCloseEvent', 'cancel')
    },
    handleEdit: function() {
      const dicmainAllinone = {
        dicmain: this.dicmain,
        diccodes: this.diccodes
      }
      if (this.mainId == null) {
        createDicmain(dicmainAllinone)
          .then(response => {
            this.$message.success('创建成功');

            // this.$router.push({ path: '/sys/dicmain/list' })
            this.$emit('lisenChildCloseEvent', 'save')
          })
          .catch(response => {
            MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
              confirmButtonText: '确定',
              type: 'error'
            })
          })
      } else {
        updateDicmain(dicmainAllinone)
          .then(response => {
            this.$message.success('修改成功');

            // this.$router.push({ path: '/sys/dicmain/list' })
            this.$emit('lisenChildCloseEvent', 'save')
          })
          .catch(response => {
            MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
              confirmButtonText: '确定',
              type: 'error'
            })
          })
      }
    },

    handleDiccodeShow() {
      this.dicCodeVisiable = true
      this.dicCodeForm = { systemed: false, enabled: true }
      this.diccodes.push(this.dicCodeForm)
    },
    handleDiccodeDisplay() {
      this.dicCodeVisiable = true
    },
    handleDiccodeHide() {
      this.dicCodeVisiable = false
    },

    handleDicodeEdit(row) {
      this.dicCodeVisiable = true
      this.dicCodeForm = row
    },
    handleDicodeDelete(row) {
      const index = this.diccodes.indexOf(row)
      if (row.id==null) {
        this.diccodes.splice(index, 1)
      } else {
        row.deleted = 1

      }
    }
  }
}
</script>
