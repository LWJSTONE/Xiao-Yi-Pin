package com.campus.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.entity.JobCategory;
import com.campus.common.vo.JobCategoryVO;
import com.campus.job.mapper.JobCategoryMapper;
import com.campus.job.service.JobCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 岗位分类服务实现
 */
@Service
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryMapper jobCategoryMapper;

    public JobCategoryServiceImpl(JobCategoryMapper jobCategoryMapper) {
        this.jobCategoryMapper = jobCategoryMapper;
    }

    @Override
    public List<JobCategoryVO> getCategoryTree() {
        // 查询所有启用的分类
        List<JobCategory> allCategories = jobCategoryMapper.selectList(
                new LambdaQueryWrapper<JobCategory>()
                        .eq(JobCategory::getStatus, 1)
                        .orderByAsc(JobCategory::getSortOrder)
        );

        // 转换为VO
        List<JobCategoryVO> allVOs = allCategories.stream().map(category -> {
            JobCategoryVO vo = new JobCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setParentId(category.getParentId());
            vo.setChildren(new ArrayList<JobCategoryVO>());
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        Map<Long, List<JobCategoryVO>> childrenMap = allVOs.stream()
                .filter(vo -> vo.getParentId() != null && vo.getParentId() != 0)
                .collect(Collectors.groupingBy(JobCategoryVO::getParentId));

        List<JobCategoryVO> tree = new ArrayList<>();
        for (JobCategoryVO vo : allVOs) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<JobCategoryVO>()));
                tree.add(vo);
            }
        }

        return tree;
    }
}
