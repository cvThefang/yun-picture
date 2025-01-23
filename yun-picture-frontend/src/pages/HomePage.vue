<template>
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchParams.searchText"
        placeholder="从海量图片中，找出你想要的一张"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>

    <!-- 标签分类和标签筛选 -->
    <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
      <a-tab-pane tab="全部" key="all" />
      <a-tab-pane v-for="category in categoryList" :tab="category" :key="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- 图片列表 -->
    <PictureList :data-list="dataList" :loading="loading" />
    <!-- 分页器 -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import PictureList from '@/components/PictureList.vue'

// 定义数据 - 请求后端接口获取数据 - 需要展示的图片列表数据
const dataList = ref<API.PictureVO[]>([])
// 分页总数
const total = ref(0)
// 设置loading状态
const loading = ref(true)
// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 请求数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    // 这里因为searchParams是响应式的变量，
    // 这里建议展开，传给一个新的对象，保证searchParams的值不会被污染
    ...searchParams,
    tags: [] as string[],
  }
  // 选中all分类时，不传分类参数 没选中all时，传分类参数
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  // 选中的标签列表转换成我们后端能识别的字符串数组
  // 列表的结构可能是[true, false, true] =>['tag1', 'tag3']
  selectedTagList.value.forEach((useTag, index) => {
    // 如果useTag为true，说明这个标签被选中了就将它添加到params的tags数组中
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })

  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.code === 0 && res.data.data) {
    // ?? : 空值合并运算符，如果前面有值就取前面，否则取后面 相当于 ?? 后面的值是默认值
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取图片列表失败,' + res.data.message)
  }
  loading.value = false
}

// 那我们什么时候请求数据呢？
// 这里可以使用Vue的生命周期函数 mounted 在页面加载时请求数据
// 在页面加载时执行一次里面的函数
onMounted(() => {
  // 请求数据
  fetchData()
})

// 分页参数 定义一个分页器 注意：这里不是一个响应式的变量
// 所以要使用Vue的一个语法 computed 计算属性 他接收一个渲染函数 (当searchParams变化时，computed函数会重新计算返回结果)
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  // 更改完参数后，重新获取数据
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 当searchParams变化时，动态更新分页器的值（重置页码）
  searchParams.current = 1
  // 更改完参数后，重新获取数据
  fetchData()
}

// 定义一个分类选项的数组
const categoryList = ref<{ value: string; label: string }[]>([])
// 定义被选中的分类选项的选项数组
const selectedCategory = ref<string>('all')
// 定义一个标签选项的数组
const tagList = ref<{ value: string; label: string }[]>([])
// 定义被选中的标签选项的选项数组
const selectedTagList = ref<boolean[]>([])

/**
 * 获取标签和分类选项
 */
const getTagAndCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagList.value = res.data.data.tagList ?? []
    categoryList.value = res.data.data.categoryList ?? []
  } else {
    message.error('获取标签和分类选项失败' + res.data.message)
  }
}

// 首次加载页面时，获取标签和分类选项
onMounted(() => {
  getTagAndCategoryOptions()
})
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}

#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}

#homePage .tag-bar {
  margin-bottom: 16px;
}
</style>
