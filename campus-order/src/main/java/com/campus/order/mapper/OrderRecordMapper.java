package com.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.entity.OrderRecord;
import com.campus.common.vo.OrderRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单记录Mapper
 */
@Mapper
public interface OrderRecordMapper extends BaseMapper<OrderRecord> {

    /**
     * 分页查询我的订单
     */
    IPage<OrderRecordVO> selectMyOrderPage(Page<OrderRecordVO> page,
                                            @Param("userId") Long userId);

    /**
     * 分页查询所有订单（管理员）
     */
    IPage<OrderRecordVO> selectAllOrderPage(Page<OrderRecordVO> page);
}
