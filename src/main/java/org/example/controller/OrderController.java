package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.Result;
import org.example.dto.OrderDTO;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.mapper.OrderDetailMapper;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Long> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Long orderId = orderService.createOrder(orderDTO);
        return Result.success(orderId);
    }

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param status  状态：0-待付款，1-已支付，2-已完成，3-已取消
     */
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam Long orderId, @RequestParam Integer status) {
        orderService.updateStatus(orderId, status);
        return Result.success();
    }

    /**
     * 模拟支付接口（已迁移到 ApiOrderController /order/simPay）
     */

    /**
     * 查询订单详情（包含订单明细）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        Orders order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> details = orderDetailMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("details", details);
        return Result.success(result);
    }

    /**
     * 分页查询订单列表
     *
     * @param userId 用户ID（可选，管理端不传则查全部）
     * @param status 订单状态（可选）
     */
    @GetMapping("/page")
    public Result<Page<Orders>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, Orders::getUserId, userId);
        queryWrapper.eq(status != null, Orders::getStatus, status);
        queryWrapper.orderByDesc(Orders::getCreateTime);
        orderService.page(page, queryWrapper);
        return Result.success(page);
    }
}
