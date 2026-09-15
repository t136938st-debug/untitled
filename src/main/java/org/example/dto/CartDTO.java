package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车请求DTO
 */
@Data
public class CartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 菜品ID */
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    /** 数量（修改数量时使用） */
    private Integer quantity;
}
