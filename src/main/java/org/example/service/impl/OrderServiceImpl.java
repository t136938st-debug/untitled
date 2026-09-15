package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.CustomException;
import org.example.dto.OrderDTO;
import org.example.entity.Dish;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.entity.ShoppingCart;
import org.example.mapper.OrderDetailMapper;
import org.example.mapper.OrderMapper;
import org.example.service.DishService;
import org.example.service.OrderService;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单Service实现类
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishService dishService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /** 订单号自增序列（简单实现，生产环境建议使用Redis或数据库序列） */
    private static final AtomicLong ORDER_SEQ = new AtomicLong(1);

    /**
     * 创建订单（从购物车生成）
     */
    @Override
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        Long userId = orderDTO.getUserId();

        // 1. 查询用户购物车
        List<ShoppingCart> cartItems = shoppingCartService.listCart(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new CustomException("购物车为空，无法创建订单");
        }

        // 2. 计算订单总金额 & 构建订单明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long orderId = null;

        // 3. 创建订单
        Orders order = new Orders();
        order.setUserId(userId);
        order.setTableId(orderDTO.getTableId());
        order.setStatus(0); // 待付款
        order.setRemark(orderDTO.getRemark());
        order.setOrderNo(generateOrderNo());

        // 先计算总金额
        for (ShoppingCart item : cartItems) {
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        order.setTotalAmount(totalAmount);

        // 保存订单
        this.save(order);
        orderId = order.getId();

        // 4. 保存订单明细
        for (ShoppingCart item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setDishId(item.getDishId());
            detail.setDishName(item.getDishName());
            detail.setDishImage(item.getDishImage());
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());
            orderDetailMapper.insert(detail);
        }

        // 5. 清空购物车
        shoppingCartService.clearCart(userId);

        return orderId;
    }

    /**
     * 修改订单状态
     */
    @Override
    public void updateStatus(Long orderId, Integer status) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }

        order.setStatus(status);
        // 如果状态变更为已支付，记录支付时间
        if (status == 1) {
            order.setPayTime(LocalDateTime.now());
        }
        this.updateById(order);
    }

    /**
     * 模拟支付
     * 直接将订单状态修改为已支付，并记录支付时间
     */
    @Override
    public void simPay(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new CustomException("订单状态异常，无法支付");
        }

        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);
        log.info("模拟支付成功，订单ID: {}", orderId);
    }

    /**
     * 定时任务：取消超时未支付订单
     * 每5分钟执行一次，将超过30分钟未支付的订单自动取消
     */
    @Override
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void cancelTimeoutOrders() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);

        // 查询所有待付款且创建时间超过30分钟的订单
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getStatus, 0) // 待付款
                .lt(Orders::getCreateTime, timeoutThreshold); // 创建时间早于30分钟前

        List<Orders> timeoutOrders = this.list(queryWrapper);

        if (!timeoutOrders.isEmpty()) {
            for (Orders order : timeoutOrders) {
                order.setStatus(3); // 已取消
                this.updateById(order);
                log.info("订单超时自动取消，订单ID: {}, 订单号: {}", order.getId(), order.getOrderNo());
            }
            log.info("本次共取消超时订单 {} 个", timeoutOrders.size());
        }
    }

    /**
     * 生成订单号：年月日时分秒 + 4位序列号
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long seq = ORDER_SEQ.getAndIncrement();
        return dateStr + String.format("%04d", seq % 10000);
    }
}
