package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.CustomException;
import org.example.dto.CartDTO;
import org.example.entity.Dish;
import org.example.entity.ShoppingCart;
import org.example.mapper.ShoppingCartMapper;
import org.example.service.DishService;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车Service实现类
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private DishService dishService;

    /**
     * 加入购物车（如已存在则数量+1）
     */
    @Override
    public void addCart(CartDTO cartDTO) {
        // 查询菜品信息
        Dish dish = dishService.getById(cartDTO.getDishId());
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        if (dish.getStatus() == 0) {
            throw new CustomException("菜品已下架，无法添加");
        }

        // 查询购物车中是否已存在该菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, cartDTO.getUserId())
                .eq(ShoppingCart::getDishId, cartDTO.getDishId());
        ShoppingCart existItem = this.getOne(queryWrapper);

        if (existItem != null) {
            // 已存在，数量+1
            existItem.setQuantity(existItem.getQuantity() + 1);
            this.updateById(existItem);
        } else {
            // 不存在，新增购物车记录
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(cartDTO.getUserId());
            cart.setDishId(cartDTO.getDishId());
            cart.setDishName(dish.getName());
            cart.setDishImage(dish.getImage());
            cart.setPrice(dish.getPrice());
            cart.setQuantity(1);
            this.save(cart);
        }
    }

    /**
     * 修改购物车中菜品数量
     */
    @Override
    public void updateQuantity(CartDTO cartDTO) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, cartDTO.getUserId())
                .eq(ShoppingCart::getDishId, cartDTO.getDishId());
        ShoppingCart cart = this.getOne(queryWrapper);

        if (cart == null) {
            throw new CustomException("购物车中无此菜品");
        }

        if (cartDTO.getQuantity() <= 0) {
            // 数量小于等于0，直接删除
            this.removeById(cart.getId());
        } else {
            cart.setQuantity(cartDTO.getQuantity());
            this.updateById(cart);
        }
    }

    /**
     * 清空用户购物车
     */
    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        this.remove(queryWrapper);
    }

    /**
     * 查询用户购物车列表
     */
    @Override
    public List<ShoppingCart> listCart(Long userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .orderByDesc(ShoppingCart::getCreateTime);
        return this.list(queryWrapper);
    }
}
