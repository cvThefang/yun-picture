<template>
  <!-- 该组件为受控组件，由父组件（图片创建页面）来管理，需要定义属性 -->
  <div id="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px">
      <a-input
        v-model:value="fileUrl"
        style="width: calc(100% - 120px)"
        placeholder="请输入图片 Url"
      />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px"
        >提交
      </a-button>
    </a-input-group>
    <div class="image-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController.ts'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

// 定义 props 属性
const props = defineProps<Props>()
// 定义url上传地址
const fileUrl = ref<string>()
// 定义图片上传loading状态
const loading = ref<boolean>(false)

/**
 * url 上传图片
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    params.spaceId = props.spaceId
    if (props.picture) {
      // 如果有图片信息，则使用编辑图片接口
      params.id = props.picture.id
    }

    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    console.error('图片上传失败', error)
    message.error('图片上传失败，' + error.message)
  }
  loading.value = false
}
</script>
<style scoped>
#url-picture-upload {
  margin-bottom: 16px;
}

#url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

#url-picture-upload .image-wrapper {
  text-align: center;
  margin-top: 16px;
}
</style>
