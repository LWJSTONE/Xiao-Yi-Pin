package com.campus.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.dto.SettleDTO;
import com.campus.common.entity.OrderRecord;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.OrderRecordVO;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRecordMapper orderRecordMapper;

    public OrderServiceImpl(OrderRecordMapper orderRecordMapper) {
        this.orderRecordMapper = orderRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settleOrder(Long employerId, Long orderId, SettleDTO dto) {
        OrderRecord order = orderRecordMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getEmployerId().equals(employerId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getSettlementStatus() != 0) {
            throw new BusinessException("该订单已结算");
        }

        order.setAmount(dto.getAmount());
        order.setSettlementStatus(1); // 已结算
        orderRecordMapper.updateById(order);
    }

    @Override
    public PageResult<OrderRecordVO> myOrders(Long userId, int page, int size) {
        Page<OrderRecordVO> pageParam = new Page<>(page, size);
        IPage<OrderRecordVO> result = orderRecordMapper.selectMyOrderPage(pageParam, userId);
        return PageResult.of(result);
    }

    @Override
    public PageResult<OrderRecordVO> allOrders(int page, int size) {
        Page<OrderRecordVO> pageParam = new Page<>(page, size);
        IPage<OrderRecordVO> result = orderRecordMapper.selectAllOrderPage(pageParam);
        return PageResult.of(result);
    }
}
