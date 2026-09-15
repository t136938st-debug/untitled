package org.example.controller;

import org.example.common.Result;
import org.example.dto.CartDTO;
import org.example.entity.ShoppingCart;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车Controller
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加入购物车
     */
    @PostMapping
    public Result<String> addCart(@Valid @RequestBody CartDTO cartDTO) {
        shoppingCartService.addCart(cartDTO);
        return Result.success();
    }

    /**
     * 修改购物车中菜品数量
     */
    @PutMapping
    public Result<String> updateQuantity(@Valid @RequestBody CartDTO cartDTO) {
        shoppingCartService.updateQuantity(cartDTO);
        return Result.success();
    }

    /**
     * 清空用户购物车
     */
    @DeleteMapping("/clean")
    public Result<String> clearCart(@RequestParam Long userId) {
        shoppingCartService.clearCart(userId);
        return Result.success();
    }

    /**
     * 查询用户购物车列表
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> listCart(@RequestParam Long userId) {
        List<ShoppingCart> list = shoppingCartService.listCart(userId);
        return Result.success(list);
    }
}
