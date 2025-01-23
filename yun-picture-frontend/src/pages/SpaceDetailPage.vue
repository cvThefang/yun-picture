<template>
  <div id="spaceDetailPage">
    <!-- 空间的基本信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}（{{ SPACE_TYPE_MAP[space.spaceType] }}）</h2>
      <a-space size="middle">
        <a-button
          v-if="canUploadPicture"
          type="primary"
          :href="`/add_picture?spaceId=${id}`"
          target="_blank"
        >
          + 创建图片
        </a-button>
        <a-button
          v-if="canManageSpaceUser"
          type="primary"
          ghost
          :icon="h(TeamOutlined)"
          :href="`/spaceUserManage/${id}`"
          target="_blank"
        >
          成员管理
        </a-button>
        <a-button
          v-if="canManageSpaceUser"
          type="primary"
          ghost
          :icon="h(BarChartOutlined)"
          :href="`/space_analyze?spaceId=${id}`"
          target="_blank"
        >
          空间分析
        </a-button>
        <a-button v-if="canEditPicture" :icon="h(EditOutlined)" @click="doBatchEdit" target="_blank"> 批量编辑</a-button>
        <a-tooltip
          :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`"
        >
          <a-progress type="circle" :size="42" :percent="percentUsed" />
        </a-tooltip>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px"></div>
    <!-- 搜索表单 -->
    <PictureSearchForm :onSearch="onSearch" />
    <!-- 按颜色搜索 -->
    <a-form-item label="按颜色搜索" style="margin-top: 16px">
      <color-picker format="hex" @pureColorChange="onColorChange" />
    </a-form-item>
    <div style="margin-bottom: 16px"></div>
    <!-- 图片列表 -->
    <PictureList
      :data-list="dataList"
      :loading="loading"
      :showOp="true"
      :onReload="fetchData"
      :canEdit="canEditPicture"
      :canDelete="canDeletePicture"
    />
    <!-- 分页器 -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
    <!-- 批量编辑图片 -->
    <BatchEditPictureModal
      ref="batchEditPictureModalRef"
      :spaceId="id"
      :pictureList="dataList"
      :onSuccess="onBatchEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import {
  listPictureVoByPageUsingPost,
  searchPictureByColorUsingPost,
} from '@/api/pictureController.ts'
import { formatSize } from '@/utils'
import PictureList from '@/components/PictureList.vue'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import { ColorPicker } from 'vue3-colorpicker'
import 'vue3-colorpicker/style.css'
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'
import { EditOutlined, BarChartOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { SPACE_PERMISSION_ENUM, SPACE_TYPE_MAP } from '../constants/space.ts'

interface Props {
  id: number | string
}

const props = defineProps<Props>()
// 定义一个用来存储空间详情数据的对象
const space = ref<API.SpaceVO>({
  // 初始化空间默认值防止除0错误
  totalSize: 0,
  maxSize: 1, // 避免除以0的情况，可以根据实际情况调整
})

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (space.value.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canManageSpaceUser = createPermissionChecker(SPACE_PERMISSION_ENUM.SPACE_USER_MANAGE)
const canUploadPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_UPLOAD)
const canEditPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDeletePicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)

// ------------------ 获取空间详情 --------------------
const fetchSpaceDetail = async () => {
  loading.value = true
  try {
    const res = await getSpaceVoByIdUsingGet({
      id: props.id,
    })
    // 操作成功
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取空间详情失败' + e.message)
  }
  loading.value = false
}
// 页面加载完成后，获取老数据
onMounted(() => {
  fetchSpaceDetail()
})

// ------------------ 获取图片详情 -------------------

// 定义数据 - 请求后端接口获取数据 - 需要展示的图片列表数据
const dataList = ref<API.PictureVO[]>([])
// 分页总数
const total = ref(0)
// 设置loading状态
const loading = ref(true)
// 搜索条件
const searchParams = ref<API.PictureQueryRequest>({
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
    spaceId: props.id,
    ...searchParams.value,
  }

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
  searchParams.value.current = page
  searchParams.value.pageSize = pageSize
  // 更改完参数后，重新获取数据
  fetchData()
}

// 计算百分比的方法
const percentUsed = computed(() => {
  if (!space.value.maxSize || space.value.maxSize <= 0) return 0
  const percent = ((space.value.totalSize ?? 0) * 100) / space.value.maxSize
  return parseFloat(percent.toFixed(1))
})

// 触发搜索
const onSearch = (newSearchParams: API.PictureQueryRequest) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1,
  }
  // 重新获取数据
  fetchData()
}

// 按颜色搜索
const onColorChange = async (color: string) => {
  const res = await searchPictureByColorUsingPost({
    picColor: color,
    spaceId: props.id,
  })
  if (res.data.code === 0 && res.data.data) {
    const data = res.data.data ?? []
    dataList.value = data
    total.value = data.length
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// ----------- 批量编辑图片 ------------
// 定义一个引用
const batchEditPictureModalRef = ref()

// 批量编辑成功的回调
const onBatchEditSuccess = () => {
  // 刷新数据
  fetchData()
}

// 打开批量编辑弹窗
const doBatchEdit = () => {
  if (batchEditPictureModalRef.value) {
    batchEditPictureModalRef.value.openModal()
  }
}

// 监听props变化 监听id， 切换空间时，重新获取数据
watch(
  () => props.id,
  (newSpaceId) => {
    fetchSpaceDetail()
    fetchData()
  },
)
</script>

<style scoped>
#spaceDetailPage {
  margin-bottom: 16px;
}
</style>
