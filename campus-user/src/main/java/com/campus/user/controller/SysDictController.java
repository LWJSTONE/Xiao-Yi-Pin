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
 * <p>
 * 注意：当前直接注入 Mapper 是有意为之的简化设计——字典表为只读配置数据，
 * 无复杂业务逻辑，省去 Service 层不会带来可维护性风险。
 * 若后续增加字典 CRUD 管理功能，应抽取 Service 层。
 * </p>
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
        wrapper.orderByAsc(SysDict::getSortOrder);
        List<SysDict> list = sysDictMapper.selectList(wrapper);
        return R.ok(list);
    }
}
