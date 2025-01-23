package com.thefang.yunpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thefang.yunpicture.domain.picture.repository.SpaceUserRepository;
import com.thefang.yunpicture.domain.space.entity.SpaceUser;
import com.thefang.yunpicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

/**
 * 空间用户仓储实现类
 */
@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}
