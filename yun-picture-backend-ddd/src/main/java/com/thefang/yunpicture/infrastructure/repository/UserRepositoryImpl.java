package com.thefang.yunpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thefang.yunpicture.domain.user.repository.UserRepository;
import com.thefang.yunpicture.domain.user.entity.User;
import com.thefang.yunpicture.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户仓储实现
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}
