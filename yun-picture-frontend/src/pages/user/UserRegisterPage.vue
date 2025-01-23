<template>
  <div id="userRegisterPage">
    <h2 class="tittle">Shark Pic - 用户注册</h2>
    <div class="desc">这是一个关于图片的网站</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入您的账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码长度至少8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入您的密码" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请再次输入密码' },
          { min: 8, message: '确认密码长度至少8位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请再次输入密码" />
      </a-form-item>
      <div class="tips">
        已有账号？
        <router-link to="/user/login">去登录</router-link>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost, userRegisterUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  // 表单验证 校验两次输入的密码是否一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  const res = await userRegisterUsingPost(values)
  // 注册成功，跳转到登录页
  if (res.data.code === 0 && res.data.data) {
    // 这里必须加await，否则可能会异步执行代码拿不到注册用户信息
    await loginUserStore.fetchLoginUser()
    message.success('注册成功')
    // 跳转到登录页
    router.push({
      path: '/user/login',
      // 为什么要替换页面：如果用户注册成功了再点击后退，此时不应该回到注册页而是登录页。
      replace: true,
    })
  } else {
    message.error('注册失败：' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
}

.tittle {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbbbbb;
  margin-bottom: 16px;
}

.tips {
  text-align: right;
  color: #bbbbbb;
  font-size: 14px;
  margin-bottom: 16px;
}
</style>
