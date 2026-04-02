package com.campus.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价VO
 */
@Data
public class ReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 评价人姓名 */
    private String reviewerName;

    /** 被评价人姓名 */
    private String targetName;

    /** 评分 */
    private Integer rating;

    /** 评价内容 */
    private String comment;

    /** 评价类型 */
    private String type;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
