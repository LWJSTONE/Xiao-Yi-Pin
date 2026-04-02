package com.campus.job.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.entity.JobCategory;
import com.campus.common.vo.JobCategoryVO;
import com.campus.job.mapper.JobCategoryMapper;
import com.campus.job.service.impl.JobCategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * JobCategoryServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("岗位分类服务测试")
class JobCategoryServiceImplTest {

    @Mock
    private JobCategoryMapper jobCategoryMapper;

    @InjectMocks
    private JobCategoryServiceImpl jobCategoryService;

    // ==================== getCategoryTree 测试 ====================

    @Test
    @DisplayName("获取分类树成功 - 返回树形结构")
    void getCategoryTree_success() {
        // given - 构建分类数据
        List<JobCategory> categories = new ArrayList<>();
        // 父分类
        JobCategory parent1 = new JobCategory();
        parent1.setId(1L);
        parent1.setName("服务类");
        parent1.setParentId(0L);
        parent1.setSortOrder(1);
        parent1.setStatus(1);
        categories.add(parent1);

        // 子分类
        JobCategory child1 = new JobCategory();
        child1.setId(2L);
        child1.setName("餐饮服务");
        child1.setParentId(1L);
        child1.setSortOrder(1);
        child1.setStatus(1);
        categories.add(child1);

        JobCategory child2 = new JobCategory();
        child2.setId(3L);
        child2.setName("前台接待");
        child2.setParentId(1L);
        child2.setSortOrder(2);
        child2.setStatus(1);
        categories.add(child2);

        // 另一个父分类
        JobCategory parent2 = new JobCategory();
        parent2.setId(4L);
        parent2.setName("教育类");
        parent2.setParentId(0L);
        parent2.setSortOrder(2);
        parent2.setStatus(1);
        categories.add(parent2);

        when(jobCategoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(categories);

        // when
        List<JobCategoryVO> tree = jobCategoryService.getCategoryTree();

        // then
        assertNotNull(tree);
        assertEquals(2, tree.size());

        // 验证第一个父分类
        JobCategoryVO serviceCategory = tree.stream()
                .filter(vo -> "服务类".equals(vo.getName()))
                .findFirst().orElse(null);
        assertNotNull(serviceCategory);
        assertEquals(1L, serviceCategory.getId());
        assertNotNull(serviceCategory.getChildren());
        assertEquals(2, serviceCategory.getChildren().size());

        // 验证子分类
        JobCategoryVO catering = serviceCategory.getChildren().stream()
                .filter(vo -> "餐饮服务".equals(vo.getName()))
                .findFirst().orElse(null);
        assertNotNull(catering);
        assertEquals(1L, catering.getParentId());
    }

    @Test
    @DisplayName("获取分类树 - 无分类数据返回空列表")
    void getCategoryTree_empty() {
        // given
        when(jobCategoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // when
        List<JobCategoryVO> tree = jobCategoryService.getCategoryTree();

        // then
        assertNotNull(tree);
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("获取分类树 - 只有父分类无子分类")
    void getCategoryTree_onlyParents() {
        // given
        List<JobCategory> categories = new ArrayList<>();
        JobCategory parent = new JobCategory();
        parent.setId(1L);
        parent.setName("服务类");
        parent.setParentId(0L);
        parent.setSortOrder(1);
        parent.setStatus(1);
        categories.add(parent);

        when(jobCategoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(categories);

        // when
        List<JobCategoryVO> tree = jobCategoryService.getCategoryTree();

        // then
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertNotNull(tree.get(0).getChildren());
        assertTrue(tree.get(0).getChildren().isEmpty());
    }

    @Test
    @DisplayName("获取分类树 - null的parentId视为顶级分类")
    void getCategoryTree_nullParentId() {
        // given
        List<JobCategory> categories = new ArrayList<>();
        JobCategory top = new JobCategory();
        top.setId(1L);
        top.setName("顶级分类");
        top.setParentId(null); // null父ID
        top.setSortOrder(1);
        top.setStatus(1);
        categories.add(top);

        when(jobCategoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(categories);

        // when
        List<JobCategoryVO> tree = jobCategoryService.getCategoryTree();

        // then
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("顶级分类", tree.get(0).getName());
    }

    @Test
    @DisplayName("获取分类树 - 三层嵌套结构(只构建两层)")
    void getCategoryTree_threeLevel() {
        // given
        List<JobCategory> categories = new ArrayList<>();
        JobCategory level1 = new JobCategory();
        level1.setId(1L);
        level1.setName("一级分类");
        level1.setParentId(0L);
        level1.setSortOrder(1);
        level1.setStatus(1);
        categories.add(level1);

        JobCategory level2 = new JobCategory();
        level2.setId(2L);
        level2.setName("二级分类");
        level2.setParentId(1L);
        level2.setSortOrder(1);
        level2.setStatus(1);
        categories.add(level2);

        JobCategory level3 = new JobCategory();
        level3.setId(3L);
        level3.setName("三级分类");
        level3.setParentId(2L);
        level3.setSortOrder(1);
        level3.setStatus(1);
        categories.add(level3);

        when(jobCategoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(categories);

        // when
        List<JobCategoryVO> tree = jobCategoryService.getCategoryTree();

        // then
        assertNotNull(tree);
        assertEquals(1, tree.size());
        // 第一层有1个子节点
        assertEquals(1, tree.get(0).getChildren().size());
        // 第二层的子节点在实现中会被包含在childrenMap中
        // 但不会递归挂到第二层节点下(只做两层树)
        assertNotNull(tree.get(0).getChildren().get(0).getChildren());
    }
}
