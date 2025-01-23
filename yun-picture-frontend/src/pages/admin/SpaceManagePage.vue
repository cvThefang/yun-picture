<template>
  <div id="spaceManagerPage">
    <a-flex justify="space-between">
      <h2>空间管理</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ 创建空间</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank">+ 分析公共图库</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank">+ 分析全部空间</a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- 搜索框 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="空间名称">
        <a-input v-model:value="searchParams.spaceName" placeholder="输入空间名称" allow-clear />
      </a-form-item>
      <a-form-item label="空间级别" name="spaceLevel">
        <a-select
          v-model:value="searchParams.spaceLevel"
          style="min-width: 180px"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请选择空间级别"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="空间类别" name="spaceType">
        <a-select
          v-model:value="searchParams.spaceType"
          :options="SPACE_TYPE_OPTIONS"
          placeholder="请输入空间类别"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="用户 id">
        <a-input v-model:value="searchParams.userId" placeholder="请输入用户 id" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-top: 16px" />

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- 空间级别 -->
        <template v-if="column.dataIndex === 'spaceLevel'">
          <div>{{ SPACE_LEVEL_MAP[record.spaceLevel] }}</div>
        </template>
        <!-- 空间类别 -->
        <template v-if="column.dataIndex === 'spaceType'">
          <a-tag>{{ SPACE_TYPE_MAP[record.spaceType] }}</a-tag>
        </template>

        <!-- 空间信息 -->
        <template v-if="column.dataIndex === 'spaceUseInfo'">
          <div>大小：{{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
          <div>数量：{{ record.totalCount }} / {{ record.maxCount }}</div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" :href="`/space_analyze?spaceId=${record.id}`" target="_blank">
              分析
            </a-button>
            <a-button type="link" :href="`/add_space?id=${record.id}`" target="_blank">
              编辑
            </a-button>
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteSpaceUsingPost, listSpaceByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { SPACE_LEVEL_MAP, SPACE_LEVEL_OPTIONS, SPACE_TYPE_MAP, SPACE_TYPE_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '@/utils'

// 表格配置 - 表头及数据字段映射
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '空间名称',
    dataIndex: 'spaceName',
  },
  {
    title: '空间级别',
    dataIndex: 'spaceLevel',
  },
  {
    title: '空间类别',
    dataIndex: 'spaceType',
  },
  {
    title: '使用情况',
    dataIndex: 'spaceUseInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义数据 - 请求后端接口获取数据
const dataList = ref<API.Space[]>([])
// 分页总数
const total = ref(0)
// 搜索条件
const searchParams = reactive<API.SpaceQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 请求数据
const fetchData = async () => {
  const res = await listSpaceByPageUsingPost({
    // 这里因为searchParams是响应式的变量，
    // 这里建议展开，传给一个新的对象，保证searchParams的值不会被污染
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    // ?? : 空值合并运算符，如果前面有值就取前面，否则取后面 相当于 ?? 后面的值是默认值
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取空间列表失败,' + res.data.message)
  }
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
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 分页参数变化时触发的函数
const doTableChange = (page: any) => {
  // 当page变化时，动态更新searchParams的值
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
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

// 删除空间
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  // 这里调用后端接口删除空间
  const res = await deleteSpaceUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除空间成功')
    // 刷新页面
    await fetchData()
  } else {
    message.error('删除空间失败,' + res.data.message)
  }
}
</script>
