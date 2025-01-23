<template>
  <div id="app">
    <BasicLayout></BasicLayout>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
// import { healthUsingGet } from '@/api/mainController.ts'
// import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

// 已经改为从权限校验文件中获取权限
// const loginUserStore = useLoginUserStore()
// // 获取登录用户信息
// loginUserStore.fetchLoginUser();


// healthUsingGet().then((res) => {
//   console.log(res)
// })
</script>

<style scoped></style>
