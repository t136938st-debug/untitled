package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.Result;
import org.example.dto.DishDTO;
import org.example.entity.Dish;
import org.example.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 菜品Controller
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody DishDTO dishDTO) {
        dishService.addDish(dishDTO);
        return Result.success();
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public Result<String> update(@Valid @RequestBody DishDTO dishDTO) {
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success();
    }

    /**
     * 菜品上下架
     *
     * @param id     菜品ID
     * @param status 状态：0-下架，1-上架
     */
    @PostMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 根据ID查询菜品详情
     */
    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id) {
        Dish dish = dishService.getById(id);
        return Result.success(dish);
    }

    /**
     * 根据分类ID查询菜品列表（仅上架菜品）
     */
    @GetMapping("/list")
    public Result<java.util.List<Dish>> listByCategoryId(@RequestParam Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort);
        return Result.success(dishService.list(queryWrapper));
    }

    /**
     * 分页查询菜品（管理端，包含所有状态）
     */
    @GetMapping("/page")
    public Result<Page<Dish>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) String name) {
        Page<Dish> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 按分类筛选
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        // 按名称模糊搜索
        queryWrapper.like(name != null && !name.isEmpty(), Dish::getName, name);
        queryWrapper.orderByAsc(Dish::getSort);
        dishService.page(page, queryWrapper);
        return Result.success(page);
    }
}
