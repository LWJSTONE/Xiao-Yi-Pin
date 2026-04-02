package com.campus.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.dto.SettleDTO;
import com.campus.common.entity.OrderRecord;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.OrderRecordVO;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OrderServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务测试")
class OrderServiceImplTest {

    @Mock
    private OrderRecordMapper orderRecordMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRecord existingOrder;
    private SettleDTO settleDTO;

    @BeforeEach
    void setUp() {
        existingOrder = new OrderRecord();
        existingOrder.setId(1L);
        existingOrder.setApplicationId(100L);
        existingOrder.setStudentId(10L);
        existingOrder.setEmployerId(20L);
        existingOrder.setJobId(30L);
        existingOrder.setAmount(new BigDecimal("500.00"));
        existingOrder.setPayStatus(1);
        existingOrder.setSettlementStatus(0); // 待结算

        settleDTO = new SettleDTO();
        settleDTO.setAmount(new BigDecimal("500.00"));
    }

    // ==================== settleOrder 测试 ====================

    @Test
    @DisplayName("结算订单成功 - 更新结算状态为已结算")
    void settleOrder_success() {
        // given
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(orderRecordMapper.updateById(any(OrderRecord.class))).thenReturn(1);

        // when
        orderService.settleOrder(20L, 1L, settleDTO);

        // then
        verify(orderRecordMapper).updateById(argThat(order -> {
            assertEquals(new BigDecimal("500.00"), order.getAmount());
            assertEquals(1, order.getSettlementStatus()); // 已结算
            return true;
        }));
    }

    @Test
    @DisplayName("结算订单失败 - 订单不存在")
    void settleOrder_notFound() {
        // given
        when(orderRecordMapper.selectById(999L)).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.settleOrder(20L, 999L, settleDTO));
        assertEquals("订单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("结算订单失败 - 不是订单的雇主")
    void settleOrder_notOwner() {
        // given
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.settleOrder(99L, 1L, settleDTO));
        assertEquals("无权操作此订单", exception.getMessage());
    }

    @Test
    @DisplayName("结算订单失败 - 订单已结算")
    void settleOrder_alreadySettled() {
        // given
        existingOrder.setSettlementStatus(1); // 已结算
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.settleOrder(20L, 1L, settleDTO));
        assertEquals("该订单已结算", exception.getMessage());
    }

    // ==================== myOrders 测试 ====================

    @Test
    @DisplayName("查询我的订单 - 返回分页结果")
    @SuppressWarnings("unchecked")
    void myOrders_success() {
        // given
        OrderRecordVO vo = new OrderRecordVO();
        vo.setId(1L);
        vo.setJobTitle("校园兼职");
        vo.setAmount(new BigDecimal("500.00"));
        vo.setSettlementStatus(0);

        Page<OrderRecordVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(vo));
        page.setTotal(1);
        when(orderRecordMapper.selectMyOrderPage(any(Page.class), eq(20L))).thenReturn(page);

        // when
        PageResult<OrderRecordVO> result = orderService.myOrders(20L, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("校园兼职", result.getRecords().get(0).getJobTitle());
    }

    // ==================== allOrders 测试 ====================

    @Test
    @DisplayName("查询所有订单(管理员) - 返回分页结果")
    @SuppressWarnings("unchecked")
    void allOrders_success() {
        // given
        OrderRecordVO vo = new OrderRecordVO();
        vo.setId(1L);
        vo.setJobTitle("测试岗位");
        vo.setSettlementStatus(1);

        Page<OrderRecordVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(vo));
        page.setTotal(1);
        when(orderRecordMapper.selectAllOrderPage(any(Page.class))).thenReturn(page);

        // when
        PageResult<OrderRecordVO> result = orderService.allOrders(1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("测试岗位", result.getRecords().get(0).getJobTitle());
    }
}
