<template>
  <div id="picture-search-form">
    <!-- 搜索框 -->
    <a-form name="searchForm" layout="inline" :model="searchParams" @finish="doSearch">
      <!--
        关键词：文本输入框
        标签：下拉选择框
        分类：下拉选择框
        编辑时间：日期选择器
        图片名称：文本输入框
        图片简介：文本输入框
        图片宽度：数字输入框
        图片高度：数字输入框
        图片格式：文本输入框 / 下拉选择框
       -->
      <a-form-item name="searchText" label="关键词">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从名称和简介中搜索"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete
          v-model:value="searchParams.category"
          style="min-width: 180px"
          placeholder="请输入图片分类"
          :options="categoryOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          v-model:value="searchParams.tags"
          style="min-width: 180px"
          mode="tags"
          placeholder="请输入图片标签"
          :options="tagOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item label="日期" name="">
        <a-range-picker
          style="width: 400px"
          show-time
          v-model:value="dataRange"
          :placeholder="['编辑开始时间', '编辑结束时间']"
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item label="名称" name="name">
        <a-input v-model:value="searchParams.name" placeholder="请输入图片名称" allow-clear />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-input
          v-model:value="searchParams.introduction"
          placeholder="请输入图片简介"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="宽度" name="picWidth">
        <a-input-number v-model:value="searchParams.picWidth" />
      </a-form-item>
      <a-form-item label="高度" name="picHeight">
        <a-input-number v-model:value="searchParams.picHeight" />
      </a-form-item>
      <a-form-item label="图片格式" name="picFormat">
        <a-select
          v-model:value="searchParams.picFormat"
          :options="imageFormatOptions"
          placeholder="请输入图片格式"
          style="min-width: 180px"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" style="width: 96px">搜索</a-button>
          <a-button @click="doClear" html-type="reset">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message, type SelectProps } from 'ant-design-vue'
import dayjs from 'dayjs'
import type { RangeValue } from 'ant-design-vue/es/vc-picker/interface'
import { listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'

// 定义搜索组件所接收的属性
interface Props {
  // 实际就是填写完搜索条件后，点击搜索按钮触发的事件
  onSearch?: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({})

// 搜索数据
const doSearch = () => {
  // 触发父组件的 onSearch 事件
  props.onSearch?.(searchParams)
}

// 定义一个分类选项的数组
const categoryOptions = ref<{ value: string; label: string }[]>([])
// 定义一个标签选项的数组
const tagOptions = ref<{ value: string; label: string }[]>([])

/**
 * 获取标签和分类选项
 */
const getTagAndCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取标签和分类选项失败' + res.data.message)
  }
}

// 首次加载页面时，获取标签和分类选项
onMounted(() => {
  getTagAndCategoryOptions()
})

// 定义图片格式选项
const imageFormatOptions = ref<SelectProps['options']>([
  { value: 'jpg', label: 'JPG' },
  { value: 'jpeg', label: 'JPEG' },
  { value: 'png', label: 'PNG' },
  { value: 'webp', label: 'WEBP' },
])

// 定义日期范围选择器
const dataRange = ref<[]>([])

/**
 * 日期范围选择器 更改时触发
 * @param dates
 * @param dateStrings
 */
const onRangeChange = (dates: RangeValue, dateStrings: string[]) => {
  if (dates?.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }
}

// 日期范围选择器预设选项
const rangePresets = ref([
  { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

// 重置搜索条件
const doClear = () => {
  // 取消所有对象的值 遍历这个对象并设置为
  Object.keys(searchParams).forEach((key) => {
    searchParams[key as keyof API.PictureQueryRequest] = undefined
  })
  // 日期筛选项单独清空，必须定义为空数组
  dataRange.value = []
  // 清空之后重新触发搜索 触发父组件的 onSearch 事件 将最新的搜索条件传给父组件 让父组件根据这个搜索条件进行数据查询
  props.onSearch?.(searchParams)
}
</script>

<style scoped>
#picture-search-form .ant-form-item {
  margin-top: 16px;
}
</style>
