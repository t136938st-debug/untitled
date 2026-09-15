package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.DiningTable;

/**
 * 餐桌Mapper接口
 */
@Mapper
public interface DiningTableMapper extends BaseMapper<DiningTable> {
}
