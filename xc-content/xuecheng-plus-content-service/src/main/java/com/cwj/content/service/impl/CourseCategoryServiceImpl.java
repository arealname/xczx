package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseCategoryMapper;
import com.cwj.content.model.po.CourseCategory;
import com.cwj.content.service.CourseCategoryService;
import com.cwj.content.model.po.dto.CourseCategoryTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> tn(String root) {

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(root);//暂时得到的是都只有一层的
        //怎么构建树的形状？

        List<CourseCategoryTreeDto> tree = new ArrayList<>();//最终结果

        Map<String, CourseCategoryTreeDto> mapTemp =
                courseCategoryTreeDtos.stream().filter(item -> !root.equals(item.getId()))
                        .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));

        courseCategoryTreeDtos.stream().filter(item -> !root.equals(item.getId())).forEach(ele -> {

            if (root.equals(ele.getParentid())) {   //如果父节点是root那么直接加入结果列表
                tree.add(ele);
            }
            //找到当前点的父节点
            CourseCategoryTreeDto fa = mapTemp.get(ele.getParentid());
            if (fa != null) {
                List<CourseCategoryTreeDto> childrenTreeNodes = fa.getChildrenTreeNodes();
                if (childrenTreeNodes == null) {
                    childrenTreeNodes = new ArrayList<>();
                    fa.setChildrenTreeNodes(childrenTreeNodes);
                }
                childrenTreeNodes.add(ele);
            }


        });
        return tree;

    }
}
