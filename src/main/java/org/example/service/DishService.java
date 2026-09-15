package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.DishDTO;
import org.example.entity.Dish;

/**
 * 菜品Service接口
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品
     *
     * @param dishDTO 菜品请求参数
     */
    void addDish(DishDTO dishDTO);

    /**
     * 修改菜品
     *
     * @param dishDTO 菜品请求参数
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 菜品上下架
     *
     * @param id     菜品ID
     * @param status 状态：0-下架，1-上架
     */
    void updateStatus(Long id, Integer status);
}
