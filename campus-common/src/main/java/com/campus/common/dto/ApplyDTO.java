package com.campus.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报名申请DTO
 */
@Data
public class ApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 简历URL（选填） */
    private String resumeUrl;
}
