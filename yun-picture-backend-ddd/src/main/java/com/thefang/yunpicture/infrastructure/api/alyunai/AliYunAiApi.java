package com.thefang.yunpicture.infrastructure.api.alyunai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreateOutPaintingTaskRequest;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.CreateOutPaintingTaskResponse;
import com.thefang.yunpicture.infrastructure.api.alyunai.model.GetOutPaintingTaskResponse;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description 阿里云 AI API 调用类
 * 参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/image-scaling-api?utm_content=m_1000400274
 * @Author Thefang
 * @Create 2025/1/18
 */
@Slf4j
@Component
public class AliYunAiApi {

    // 读取配置文件中的 API Key
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    // 创建任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // 查询任务状态
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建扩图任务
     *
     * @param createOutPaintingTaskRequest 任务请求参数
     * @return 创建扩图任务响应类
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "扩图参数不能为空");
        }
        // 发送请求
        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
                .header(Header.AUTHORIZATION, "Bearer" + apiKey)
                // 必须开启异步处理，设置为enable。
                .header("X-DashScope-Async", "enable")
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));
        // 这里使用jdk7提供的方法，使用 try-with-resources 来自动关闭连接
        // 就是在try里面写要释放的资源的对象的创建逻辑 有一个前提是这个资源对象必须要实现Closeable接口 因为Closeable继承了AutoCloseable接口  AutoCloseable接口中有close方法
        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                log.error("请求异常：{}", httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
            }
            // 接口响应正常 解析响应 toBean
            CreateOutPaintingTaskResponse createOutPaintingTaskResponse = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = createOutPaintingTaskResponse.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                String errorMessage = createOutPaintingTaskResponse.getMessage();
                log.error("AI 扩图失败，errorCode:{}, errorMessage:{}", errorCode, errorMessage);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图接口响应异常");
            }
            return createOutPaintingTaskResponse;
        }
    }

    /**
     * 查询创建的任务状态
     *
     * @param taskId 任务 ID
     * @return 查询任务状态响应类
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
        }
        try (HttpResponse httpResponse = HttpRequest.get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
                .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                .execute()) {
            if (!httpResponse.isOk()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务结果失败");
            }
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        }
    }
}
