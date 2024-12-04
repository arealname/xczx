package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseBaseMapper;
import com.cwj.content.model.po.CourseBase;
import com.cwj.content.service.CourseBaseService;
import com.xuecheng.xccommon.PageParams;
import com.xuecheng.xccommon.PageResult;
import com.xuecheng.xccommon.dto.QueryCourseParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public PageResult<CourseBase> list(PageParams pageParams, QueryCourseParamsDto qdto) {

        LambdaQueryWrapper<CourseBase> lq=new LambdaQueryWrapper<>();
        lq.like(StringUtils.isNotEmpty(qdto.getCourseName()),CourseBase::getName,qdto.getCourseName());
        lq.eq(StringUtils.isNotEmpty(qdto.getAuditStatus()),CourseBase::getAuditStatus,qdto.getAuditStatus());


        IPage<CourseBase> p= new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        IPage<CourseBase> courseBaseIPage = courseBaseMapper.selectPage(p, lq);


        List<CourseBase> records = courseBaseIPage.getRecords();

        PageResult<CourseBase> courseBasePageResult = new PageResult<>();

        courseBasePageResult.setCounts(courseBaseIPage.getTotal());//总记录数
        courseBasePageResult.setItems(records);
        courseBasePageResult.setPages(courseBaseIPage.getPages()); //总页数
        courseBasePageResult.setCurrentpage(courseBaseIPage.getCurrent());
        courseBasePageResult.setPageSize(courseBaseIPage.getSize());


        return courseBasePageResult;
    }
}
