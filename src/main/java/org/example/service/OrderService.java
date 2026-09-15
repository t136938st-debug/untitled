package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.OrderDTO;
import org.example.entity.Orders;

/**
 * 订单Service接口
 */
public interface OrderService extends IService<Orders> {

    /**
     * 创建订单（从购物车生成）
     *
     * @param orderDTO 创建订单请求参数
     * @return 订单ID
     */
    Long createOrder(OrderDTO orderDTO);

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param status  目标状态
     */
    void updateStatus(Long orderId, Integer status);

    /**
     * 模拟支付
     * 直接将订单状态修改为已支付
     *
     * @param orderId 订单ID
     */
    void simPay(Long orderId);

    /**
     * 取消超时未支付订单
     * 将超过30分钟未支付的订单自动取消
     */
    void cancelTimeoutOrders();
}
