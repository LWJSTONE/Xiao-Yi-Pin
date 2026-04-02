package com.campus.user.controller;

import com.campus.common.entity.SysDict;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.user.mapper.SysDictMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据字典控制器
 */
@RestController
@RequestMapping("/api/v1/dict")
public class SysDictController {

    @Resource
    private SysDictMapper sysDictMapper;

    /**
     * 查询字典列表
     * @param dictType 字典类型（可选）
     */
    @GetMapping("/list")
    public R<List<SysDict>> listDicts(
            @RequestHeader("X-Role-Type") String roleType,
            @RequestParam(required = false) String dictType) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权限访问");
        }
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null && !dictType.isEmpty()) {
            wrapper.eq(SysDict::getDictType, dictType);
        }
        wrapper.orderByAsc(SysDict::getSort);
        List<SysDict> list = sysDictMapper.selectList(wrapper);
        return R.ok(list);
    }
}
