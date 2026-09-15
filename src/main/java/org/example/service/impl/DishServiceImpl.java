package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.DishDTO;
import org.example.entity.Dish;
import org.example.mapper.DishMapper;
import org.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 菜品Service实现类
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * 新增菜品
     */
    @Override
    public void addDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 默认上架状态
        dish.setStatus(1);
        this.save(dish);
    }

    /**
     * 修改菜品
     */
    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        this.updateById(dish);
    }

    /**
     * 菜品上下架
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        this.updateById(dish);
    }
}
