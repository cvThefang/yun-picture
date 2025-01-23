import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建 Axios实例
const myAxios = axios.create({
  baseURL: 'http://localhost:10086',
  timeout: 60000, // 请求超时时间 60000ms  60秒
  withCredentials: true, // 是否携带登录凭证 允许跨域请求带cookies
})

// 全局请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error)
  },
)

// 全局响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // 未登录
    if (data.code === 40100) {
      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('请先登录')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error)
  },
)

// 导出 Axios实例 这样的话可以在其他地方使用
export default myAxios
