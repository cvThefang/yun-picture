import { generateService } from '@umijs/openapi'

generateService({
  // 生成的代码都会添加一个引入的代码
  requestLibPath: "import request from '@/request'",
  // 参考哪个接口文档地址生成代码
  schemaPath: 'http://localhost:10086/api/v2/api-docs',
  // 生成的代码放到哪个目录
  serversPath: './src',
})
