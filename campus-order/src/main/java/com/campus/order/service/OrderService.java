package com.campus.order.service;

import com.campus.common.dto.SettleDTO;
import com.campus.common.result.PageResult;
import com.campus.common.vo.OrderRecordVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 结算订单
     */
    void settleOrder(Long employerId, Long orderId, SettleDTO dto);

    /**
     * 查询我的订单
     */
    PageResult<OrderRecordVO> myOrders(Long userId, int page, int size);

    /**
     * 查询所有订单（管理员）
     */
    PageResult<OrderRecordVO> allOrders(int page, int size);
}
