package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DiningTable;
import org.example.mapper.DiningTableMapper;
import org.example.service.DiningTableService;
import org.springframework.stereotype.Service;

/**
 * 餐桌Service实现类
 */
@Service
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {
}
