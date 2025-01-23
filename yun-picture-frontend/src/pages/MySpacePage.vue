<template>
  <div id="mySpacePage">
    <p>正在跳转，请稍候...</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceByPageUsingPost, listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 检查用户是否有个人空间
const checkUserSpace = async () => {
  // 判断用户是否登录
  const loginUser = loginUserStore.loginUser
  if (!loginUser.id) {
    // 未登录，重定向到登录页面 这边要替换成登录页面的路由 不让用户回退到中间页
    router.replace('/user/login')
    return
  }
  // 如果用户已登录，会获取该用户已创建的空间
  const res = await listSpaceVoByPageUsingPost({
    userId: loginUser.id,
    current: 1,
    pageSize: 1,
    spaceType: SPACE_TYPE_ENUM.PRIVATE,
  })
  if (res.data.code === 0) {
    // 返回成功 判断空间是否存在
    if (res.data.data?.records?.length > 0) {
      // 存在，进入第一个空间
      const space = res.data.data?.records[0]
      await router.replace(`/space/${space.id}`)
    } else {
      // 不存在，跳转到创建空间页面
      await router.replace('/add_space')
      message.warn('请先创建空间！')
    }
  } else {
    // 接口调用失败
    message.error('加载我的空间失败，' + res.data.message)
  }
}

// 页面加载完成后，检查用户是否有个人空间
onMounted(() => {
  checkUserSpace()
})
</script>
