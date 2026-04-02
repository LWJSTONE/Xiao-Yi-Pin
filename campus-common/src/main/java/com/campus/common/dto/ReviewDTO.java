package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 评价DTO
 */
@Data
public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 被评价人ID */
    @NotNull(message = "被评价人ID不能为空")
    private Long targetId;

    /** 评分: 1-5 */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;

    /** 评价内容 */
    private String comment;

    /** 评价类型 */
    @NotBlank(message = "评价类型不能为空")
    private String type;
}
