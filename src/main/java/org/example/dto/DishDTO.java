package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 菜品请求DTO
 */
@Data
public class DishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜品ID（修改时必填） */
    private Long id;

    /** 菜品名称 */
    @NotBlank(message = "菜品名称不能为空")
    private String name;

    /** 分类ID */
    @NotNull(message = "菜品分类不能为空")
    private Long categoryId;

    /** 菜品价格 */
    @NotNull(message = "菜品价格不能为空")
    private BigDecimal price;

    /** 菜品图片URL */
    private String image;

    /** 菜品描述 */
    private String description;

    /** 排序字段 */
    private Integer sort;
}
