<template>
  <div id="picture-list">
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- 单张图片  -->
          <a-card hoverable @click="doClickPicture(picture)">
            <template #cover>
              <img
                :alt="picture.name"
                :src="picture.thumbnailUrl ?? picture.url"
                style="height: 180px; object-fit: cover"
              />
              <!-- object-fit: 让图片自动适应我们设置的宽高    cover:取图片中心区域     -->
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ picture.category ?? '默认' }}
                  </a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
            <template v-if="showOp" #actions>
              <a-tooltip placement="top" title="以图搜图" color="#6CB4EE">
                <search-outlined @click="(e) => doSearch(picture, e)" />
              </a-tooltip>
              <a-tooltip placement="top" title="分享图片" color="#FEC8D8">
                <share-alt-outlined @click="(e) => doShare(picture, e)" />
              </a-tooltip>
              <a-tooltip v-if="canEdit" placement="top" title="编辑图片" color="#D1D55C">
                <edit-outlined @click="(e) => doEdit(picture, e)" />
              </a-tooltip>
              <a-tooltip v-if="canDelete" placement="top" title="删除图片" color="#E63946">
                <delete-outlined @click="(e) => doDelete(picture, e)" />
              </a-tooltip>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  ShareAltOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModal.vue'
import { ref } from 'vue'

// 定义一个属性 用来接收dataList数据
interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  canEdit?: boolean
  canDelete?: boolean
  onReload?: () => void
}

// 设置一个默认值
const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
  canEdit: false,
  canDelete: false,
})

// 引入路由跳转组件
const router = useRouter()

// 点击图片跳转到详情页
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 搜索
const doSearch = (picture, e) => {
  // 阻止事件冒泡 防止事件向上传递 因为点击编辑按钮的时候 会触发点击图片事件
  e.stopPropagation()
  // 打开新的页面
  window.open(`/search_picture?pictureId=${picture.id}`)
}

// 编辑
const doEdit = (picture, e) => {
  // 阻止事件冒泡 防止事件向上传递 因为点击编辑按钮的时候 会触发点击图片事件
  e.stopPropagation()
  // 跳转到编辑页 跳转时一定要携带spaceId 不然就不会进入到私有空间的图片编辑页
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

// 删除
const doDelete = async (picture, e) => {
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 让外层刷新
    props?.onReload?.()
  } else {
    message.error('删除失败')
  }
}

// ------- 分享操作 -------
// 定义一个引用
const shareModalRef = ref()
// 定义分享链接
const shareLink = ref<string>('')

// 分享图片函数
const doShare = (picture: API.PictureVO, e: Event) => {
  // 阻止事件冒泡
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped></style>
