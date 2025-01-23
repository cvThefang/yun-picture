<template>
  <div class="share-modal">
    <a-modal v-model:visible="visible" :title="title" :footer="false" @cancel="closeModal">
      <h4>复制分享链接</h4>
      <a-typography-link copyable>
        {{ link }}
      </a-typography-link>
      <div style="margin-top: 16px" />
      <h4>手机扫码查看</h4>
      <a-qrcode :value="link" />
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'

// 父组件传递的参数
interface Props {
  title: string
  link: string
}

// 组件的默认值
const props = withDefaults(defineProps<Props>(), {
  title: '分享图片',
  link: 'https://img2.baidu.com/it/u=698097851,3188720460&fm=253&fmt=auto&app=138&f=JPEG?w=418&h=358',
})

// 这边将这个modal组件定义为受控组件，即父组件通过props控制modal的显示和隐藏
const visible = ref<boolean>(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

// 暴露函数给父组件
defineExpose({
  openModal,
})
</script>
