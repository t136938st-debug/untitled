package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.CartDTO;
import org.example.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车Service接口
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 加入购物车
     *
     * @param cartDTO 购物车请求参数
     */
    void addCart(CartDTO cartDTO);

    /**
     * 修改购物车中菜品数量
     *
     * @param cartDTO 包含dishId、userId、quantity
     */
    void updateQuantity(CartDTO cartDTO);

    /**
     * 清空用户购物车
     *
     * @param userId 用户ID
     */
    void clearCart(Long userId);

    /**
     * 查询用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<ShoppingCart> listCart(Long userId);
}
