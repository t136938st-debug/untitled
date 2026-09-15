package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建订单请求DTO
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 餐桌ID */
    @NotNull(message = "餐桌ID不能为空")
    private Long tableId;

    /** 备注 */
    private String remark;
}
