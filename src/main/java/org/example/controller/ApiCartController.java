package org.example.controller;

import org.example.common.Result;
import org.example.entity.ShoppingCart;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序购物车 Controller
 * 所有接口从 JWT token 中获取 userId，前端无需传 userId
 */
@RestController
@RequestMapping("/cart")
public class ApiCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加入购物车
     * POST /api/cart/add  { dishId, quantity? }
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Long dishId = Long.valueOf(params.get("dishId").toString());
        Integer quantity = params.containsKey("quantity")
                ? Integer.valueOf(params.get("quantity").toString()) : 1;

        // 复用现有 addCart 逻辑：构建 CartDTO
        org.example.dto.CartDTO cartDTO = new org.example.dto.CartDTO();
        cartDTO.setUserId(userId);
        cartDTO.setDishId(dishId);
        // 如果传了 quantity > 1，先加一次再改数量
        shoppingCartService.addCart(cartDTO);

        // 如果指定数量 > 1，调整数量
        if (quantity > 1) {
            cartDTO.setQuantity(quantity);
            shoppingCartService.updateQuantity(cartDTO);
        }

        return Result.success("添加成功");
    }

    /**
     * 查询购物车列表
     * GET /api/cart/list
     * 返回 { items: [...], totalCount, totalAmount }
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<ShoppingCart> items = shoppingCartService.listCart(userId);

        int totalCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShoppingCart item : items) {
            totalCount += item.getQuantity();
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("totalCount", totalCount);
        result.put("totalAmount", totalAmount);
        return Result.success(result);
    }

    /**
     * 修改购物车菜品数量
     * POST /api/cart/update  { cartId, quantity }
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Long cartId = Long.valueOf(params.get("cartId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());

        // 校验购物车记录属于当前用户
        ShoppingCart cart = shoppingCartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.error("购物车记录不存在");
        }

        if (quantity <= 0) {
            shoppingCartService.removeById(cartId);
        } else {
            cart.setQuantity(quantity);
            shoppingCartService.updateById(cart);
        }
        return Result.success("更新成功");
    }

    /**
     * 删除购物车中某条记录
     * POST /api/cart/remove  { cartId }
     */
    @PostMapping("/remove")
    public Result<String> remove(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Long cartId = Long.valueOf(params.get("cartId").toString());

        ShoppingCart cart = shoppingCartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.error("购物车记录不存在");
        }

        shoppingCartService.removeById(cartId);
        return Result.success("删除成功");
    }

    /**
     * 清空购物车
     * POST /api/cart/clear
     */
    @PostMapping("/clear")
    public Result<String> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        shoppingCartService.clearCart(userId);
        return Result.success("清空成功");
    }
}
