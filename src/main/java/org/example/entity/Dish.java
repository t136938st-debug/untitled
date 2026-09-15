package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Data
@TableName("dish")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 菜品名称 */
    private String name;

    /** 分类ID */
    private Long categoryId;

    /** 菜品价格 */
    private BigDecimal price;

    /** 菜品图片URL */
    private String image;

    /** 菜品描述 */
    private String description;

    /** 状态：0-下架（停售），1-上架（起售） */
    private Integer status;

    /** 排序字段 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人ID */
    private Long createUserId;

    /** 更新人ID */
    private Long updateUserId;

    /** 逻辑删除标志：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
