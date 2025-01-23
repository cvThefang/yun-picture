package com.thefang.yunpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thefang.yunpicture.domain.space.entity.Space;
import com.thefang.yunpicture.domain.space.repository.SpaceRepository;
import com.thefang.yunpicture.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

/**
 * 空间仓储实现类
 */
@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}
