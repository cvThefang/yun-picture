import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'

// 是否为首次获取登录用户信息
let firstFetchLoginUser = true
/**
 * 全局权限校验，每次切换页面时都会执行
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 确保页面刷新时，首次加载时，能等待后端返回用户信息后再做权限校验
  // 如果是首次获取登录用户信息，才调用后端接口获取用户信息 避免重复请求后端
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  // 拿到请求路径
  const toUrl = to.fullPath

  // 可以自己定义权限校验逻辑，比如管理员才能访问 /admin 开头的页面
  if (toUrl.startsWith('/admin')) {
    // 如果用户不存在，或者用户角色不是管理员，则跳转到登录页面
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('你没有权限访问该页面')
      next(`/user/login?redirect=${toUrl}`)
      return
    }
  }
  // 通过权限校验后，放行  next 不传参数，默认放行跳转到原本要去的页面
  next()
})
