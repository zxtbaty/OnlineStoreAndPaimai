<template>
  <section>
    <el-container>

      <el-form ref="User" label-width="120px" >
        <el-row>
          <el-col>
            <el-form-item label="账号" >
              <el-input />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="密码" >
              <el-input type="password" auto-complete="new-password"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="记住密码" prop="delivery">
              <el-switch />
            </el-form-item>
          </el-col>
        </el-row>
        <span class="pwwd">
          <a href="">忘记密码？</a>
          <a href="">还没账号,注册一个</a>
        </span>
      </el-form>

    </el-container>
    <div slot="footer" class="dialog-footer">
      <el-button >取 消</el-button>
      <el-button type="primary" @click="submitForm()">登 录</el-button>
    </div>
  </section>
</template>
<script>
export default {
  name: 'LoginNameTest'// 这个LoginName最好和引入的vue的LoginName相同
}
</script>

