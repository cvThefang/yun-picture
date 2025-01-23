package com.thefang.yunpicture.application.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thefang.yunpicture.domain.space.service.SpaceDomainService;
import com.thefang.yunpicture.infrastructure.exception.BusinessException;
import com.thefang.yunpicture.infrastructure.exception.ErrorCode;
import com.thefang.yunpicture.infrastructure.exception.ThrowUtils;
import com.thefang.yunpicture.infrastructure.mapper.SpaceMapper;
import com.thefang.yunpicture.interfaces.dto.space.SpaceAddRequest;
import com.thefang.yunpicture.interfaces.dto.space.SpaceLevel;
import com.thefang.yunpicture.interfaces.dto.space.SpaceQueryRequest;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.domain.space.valueobject.SpaceLevelEnum;
import com.thefang.yunpicture.domain.space.valueobject.SpaceRoleEnum;
import com.thefang.yunpicture.domain.space.valueobject.SpaceTypeEnum;
import com.thefang.yunpicture.interfaces.vo.space.SpaceVO;
import com.thefang.yunpicture.interfaces.vo.user.UserVO;
import com.thefang.yunpicture.application.service.SpaceApplicationService;
import com.thefang.yunpicture.application.service.SpaceUserApplicationService;
import com.thefang.yunpicture.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Thefang
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-01-03 16:09:22
 */
@Service
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceApplicationService {

    @Resource
    private SpaceDomainService spaceDomainService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private TransactionTemplate transactionTemplate;

    // ShardingSphereStart  按需开启 ShardingSphere 因为后续改动公共空间逻辑较大，暂时不开启
//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

    // 我们是对字符串常量池（intern）进行加锁的，数据并不会及时释放。如果还要使用本地锁，可以采用 ConcurrentHashMap 来存储锁对象
    Map<Long, Object> lockMap = new ConcurrentHashMap<>();

    /**
     * 创建空间
     *
     * @param spaceAddRequest 创建空间请求对象
     * @param loginUser       登录用户对象
     * @return 创建的空间 id
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 1. 填充参数默认值
        // 转换实体类和 DTO 对象
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 如果空间名称为空，则使用默认空间
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        // 如果空间级别为空，则使用普通版
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 补充一下空间类型
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // 填充空间容量和最大条数
        this.fillSpaceBySpaceLevel(space);
        // 2. 校验参数
        space.validSpace(true);
        // 3. 权限校验 非管理员只能创建普通级别的空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (SpaceLevelEnum.COMMON.getValue() != space.getSpaceLevel() && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 4. 控制同一个用户只能创建一个私有空间 或者 一个团队空间
        // 定义一个锁 每个用户得到的锁是不同的 但同一个用户要得到的是同一把锁
        // intern 字符串常量池 相同的字符串常量在常量池中只有一个引用（相同值的字符串有一个同样的固定的存储空间）
//        String lock = String.valueOf(userId).intern();
        Object lock = lockMap.computeIfAbsent(userId, key -> new Object());
        // 加锁
        synchronized (lock) {
            try {
                // 这边使用了编程式事务管理器 TransactionTemplate
                // 事务管理器的 execute 方法可以传入一个回调函数，在回调函数中执行事务操作
                // 如果回调函数抛出异常，则事务回滚，否则提交事务
                Long newSpaceId = transactionTemplate.execute(status -> {
                    // 判断是否已有空间
                    // 这里使用 exists 和 count 谁的效率更高  是exists 因为 exists只要找到符合条件的数据就会返回，而count 则是遍历所有数据
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, space.getSpaceType())
                            .exists();
                    // 如果已有空间，就不能再创建
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个类别的空间只能创建一个");
                    // 否则就创建空间
                    boolean result = this.save(space);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存空间到数据库失败");
                    // 创建成功后，如果是团队空间，关联新增团队成员记录
                    if (SpaceTypeEnum.TEAM.getValue() == space.getSpaceType()) {
                        SpaceUser spaceUser = new SpaceUser();
                        spaceUser.setSpaceId(space.getId());
                        spaceUser.setUserId(userId);
                        spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                        result = spaceUserApplicationService.save(spaceUser);
                        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                    }
                    // 创建分表 仅对团队空间生效
                    // ShardingSphereStart  按需开启 ShardingSphere 因为后续改动公共空间逻辑较大，暂时不开启
//                    dynamicShardingManager.createSpacePictureTable(space);
                    // 5. 保存成功 返回空间 id
                    return space.getId();
                });
                return Optional.ofNullable(newSpaceId).orElse(-1L);
            } finally {
                // 防止内存泄漏
                lockMap.remove(userId);
            }
        }
    }


    /**
     * 根据空间对象，获取空间封装类
     *
     * @param space   空间对象
     * @param request 请求对象
     * @return 空间封装类
     */
    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    /**
     * 分页查询空间对象，并关联查询用户信息
     *
     * @param spacePage 空间分页对象
     * @param request   请求对象
     * @return 分页查询结果
     */
    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        // userIdSet 查出来是 1,2,3,4
        // 这样的集合 这里需要把集合转换成 userId => List<User> 的映射
        // 然后再根据 userId 取出用户信息 根据map对应的 userId 填充到 SpaceVO 中
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    /**
     * 查询对象转换成  QueryWrapper 查询条件
     *
     * @param spaceQueryRequest 空间查询请求
     * @return QueryWrapper 查询条件
     */
    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    /**
     * 根据空间级别，填充空间对象的容量和最大条数
     * 管理员在创建空间时，可以设置空间的容量最大值和最大条数，如果没有设置，则使用空间类别的默认值
     *
     * @param space 空间对象
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        spaceDomainService.fillSpaceBySpaceLevel(space);
    }

    /**
     * 校验空间权限
     *
     * @param user  登录用户对象
     * @param space 空间对象
     */
    @Override
    public void checkSpaceAuth(User user, Space space) {
        spaceDomainService.checkSpaceAuth(user, space);
    }

    /**
     * 获取空间级别列表
     *
     * @return 空间级别列表
     */
    @Override
    public List<SpaceLevel> listSpaceLevel() {
        return spaceDomainService.listSpaceLevel();
    }
}




