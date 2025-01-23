<template>
  <div id="userManagerPage">
    <!-- 搜索框 -->
    <a-form layout="inline" :model="searchParams" @finish="doShearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
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
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120"></a-image>
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

// 表格配置 - 表头及数据字段映射
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义数据 - 请求后端接口获取数据
const dataList = ref<API.UserVO[]>([])
// 分页总数
const total = ref(0)
// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
})

// 请求数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    // 这里因为searchParams是响应式的变量，
    // 这里建议展开，传给一个新的对象，保证searchParams的值不会被污染
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    // ?? : 空值合并运算符，如果前面有值就取前面，否则取后面 相当于 ?? 后面的值是默认值
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取用户列表失败,' + res.data.message)
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
const doShearch = () => {
  // 当searchParams变化时，动态更新分页器的值（重置页码）
  searchParams.current = 1
  // 更改完参数后，重新获取数据
  fetchData()
}

// 删除用户
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  // 这里调用后端接口删除用户
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除用户成功')
    // 刷新页面
    await fetchData()
  } else {
    message.error('删除用户失败,' + res.data.message)
  }
}
</script>
