<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query.id ? '修改' : '创建' }} {{ SPACE_TYPE_MAP[spaceType]}}
    </h2>
    <!-- 空间信息表单 -->
    <a-form name="spaceForm" layout="vertical" :model="spaceForm" @finish="handleSubmit">
      <a-form-item name="spaceName" label="空间名称">
        <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" />
      </a-form-item>
      <a-form-item label="空间级别" name="spaceLevel">
        <a-select
          v-model:value="spaceForm.spaceLevel"
          style="min-width: 180px"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请输入空间级别"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" :loading="loading" style="width: 100%">
          提交
        </a-button>
      </a-form-item>
    </a-form>
    <!-- 空间级别介绍 -->
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版，如需升级空间，请联系
        <a href="https://github.com/cvThefang" target="_blank">作者</a>。
      </a-typography-paragraph>
      <a-typography-paragraph v-for="(spaceLevel, index) in spaceLevelList" :key="index">
        <template v-if="spaceLevel.value === SPACE_LEVEL_ENUM.COMMON">
          ⭐ {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
          {{ spaceLevel.maxCount }}
        </template>
        <template v-else-if="spaceLevel.value === SPACE_LEVEL_ENUM.PROFESSIONAL">
          💎 {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
          {{ spaceLevel.maxCount }}
        </template>
        <template v-else-if="spaceLevel.value === SPACE_LEVEL_ENUM.FLAGSHIP">
          👑 {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
          {{ spaceLevel.maxCount }}
        </template>
        <template v-else>
          {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
          {{ spaceLevel.maxCount }}
        </template>
      </a-typography-paragraph>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  addSpaceUsingPost,
  getSpaceVoByIdUsingGet,
  listSpaceLevelUsingGet,
  updateSpaceUsingPost,
} from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import { useRoute } from 'vue-router'
import {
  SPACE_LEVEL_ENUM,
  SPACE_LEVEL_OPTIONS,
  SPACE_TYPE_ENUM,
  SPACE_TYPE_MAP,
} from '@/constants/space.ts'
import { formatSize } from '@/utils'

const space = ref<API.SpaceVO>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceEditRequest>({})
const loading = ref(false)
const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取页面信息
const route = useRoute()
// 空间类别，默认为私有空间
const spaceType = computed(() => {
  if (route.query?.type) {
    return Number(route.query.type)
  } else {
    return SPACE_TYPE_ENUM.PRIVATE
  }
})

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('加载空间级别失败，' + res.data.message)
  }
}
onMounted(() => {
  fetchSpaceLevelList()
})

const handleSubmit = async (values: API.SpaceAddRequest) => {
  // 根据space的id判断是新增还是编辑
  const spaceId = space.value?.id
  // 通过校验将按钮置为loading状态
  loading.value = true
  // 调用后端查询表单项
  let res
  // 如果spaceId存在，说明是更新操作
  if (spaceId) {
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...spaceForm,
    })
  } else {
    // 否则是新增操作
    res = await addSpaceUsingPost({
      spaceType: spaceType.value,
      ...spaceForm,
    })
  }
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('空间' + (spaceId ? '更新' : '创建') + '成功')
    // 跳转到空间详情页面
    const path = `/space/${spaceId ?? res.data.data}`
    await router.push({
      path,
    })
  } else {
    message.error('空间' + (spaceId ? '更新' : '创建') + '失败' + res.data.message)
  }
  // 按钮状态恢复
  loading.value = false
}

// 获取老数据
const getOldSpace = async () => {
  // 获取空间id
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id,
    })
    // 操作成功
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      // 表单赋值
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    } else {
      message.error('获取空间信息失败' + res.data.message)
    }
  }
}
// 页面加载完成后，获取老数据
onMounted(() => {
  getOldSpace()
})
</script>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
