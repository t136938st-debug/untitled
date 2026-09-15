package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.common.Result;
import org.example.dto.OrderDTO;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.mapper.OrderDetailMapper;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 小程序订单 Controller
 * userId 从 JWT token 中获取，前端无需传 userId
 */
@RestController
@RequestMapping("/order")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 创建订单
     * POST /api/order/create  { tableId, remark }
     * 返回 { id, orderNo }
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        if (params.containsKey("tableId") && params.get("tableId") != null) {
            orderDTO.setTableId(Long.valueOf(params.get("tableId").toString()));
        }
        if (params.containsKey("remark")) {
            orderDTO.setRemark((String) params.get("remark"));
        }

        Long orderId = orderService.createOrder(orderDTO);
        Orders order = orderService.getById(orderId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        data.put("orderNo", order.getOrderNo());
        return Result.success(data);
    }

    /**
     * 模拟支付
     * POST /api/order/simPay  { orderId }
     */
    @PostMapping("/simPay")
    public Result<String> simPay(@RequestBody Map<String, Object> params,
                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Long orderId = Long.valueOf(params.get("orderId").toString());

        // 校验订单属于当前用户
        Orders order = orderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }

        orderService.simPay(orderId);
        return Result.success("支付成功");
    }

    /**
     * 取消订单（仅待付款可取消）
     * POST /api/order/cancel  { orderId }
     */
    @PostMapping("/cancel")
    public Result<String> cancel(@RequestBody Map<String, Object> params,
                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Long orderId = Long.valueOf(params.get("orderId").toString());

        Orders order = orderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("仅待付款订单可取消");
        }

        orderService.updateStatus(orderId, 3);
        return Result.success("取消成功");
    }

    /**
     * 订单列表
     * GET /api/order/list  ?status=-1 (全部) / 0 / 1 / 2 / 3
     * 返回 [{ id, orderNo, status, tableId, totalAmount, createTime, itemCount }]
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(defaultValue = "-1") Integer status,
                                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        if (status != null && status >= 0) {
            queryWrapper.eq(Orders::getStatus, status);
        }
        queryWrapper.orderByDesc(Orders::getCreateTime);
        List<Orders> orders = orderService.list(queryWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Orders order : orders) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", order.getId());
            item.put("orderNo", order.getOrderNo());
            item.put("status", order.getStatus());
            item.put("tableId", order.getTableId());
            item.put("totalAmount", order.getTotalAmount());
            item.put("createTime", order.getCreateTime());
            // 统计菜品数量
            LambdaQueryWrapper<OrderDetail> dw = new LambdaQueryWrapper<>();
            dw.eq(OrderDetail::getOrderId, order.getId());
            List<OrderDetail> details = orderDetailMapper.selectList(dw);
            int itemCount = details.stream().mapToInt(OrderDetail::getQuantity).sum();
            item.put("itemCount", itemCount);
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 订单详情
     * GET /api/order/detail/{id}
     * 返回 { id, orderNo, status, tableId, remark, totalAmount, createTime, items: [...] }
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        Orders order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        data.put("orderNo", order.getOrderNo());
        data.put("status", order.getStatus());
        data.put("tableId", order.getTableId());
        data.put("remark", order.getRemark());
        data.put("totalAmount", order.getTotalAmount());
        data.put("createTime", order.getCreateTime());
        data.put("payTime", order.getPayTime());

        // 订单明细
        LambdaQueryWrapper<OrderDetail> dw = new LambdaQueryWrapper<>();
        dw.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> details = orderDetailMapper.selectList(dw);

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderDetail d : details) {
            Map<String, Object> m = new HashMap<>();
            m.put("dishId", d.getDishId());
            m.put("dishName", d.getDishName());
            m.put("price", d.getPrice());
            m.put("quantity", d.getQuantity());
            m.put("image", d.getDishImage());
            items.add(m);
        }
        data.put("items", items);
        return Result.success(data);
    }
}
