package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseBaseMapper;
import com.cwj.content.mapper.CourseCategoryMapper;
import com.cwj.content.mapper.CourseMarketMapper;
import com.cwj.content.model.po.CourseBase;
import com.cwj.content.model.po.CourseCategory;
import com.cwj.content.model.po.CourseMarket;
import com.cwj.content.service.CourseBaseService;
import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.content.model.po.dto.AddCourseDto;
import com.cwj.content.model.po.dto.CourseBaseInfoDto;
import com.cwj.content.model.po.dto.QueryCourseParamsDto;

import com.cwj.xccommon.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        LambdaQueryWrapper<CourseBase> lq = new LambdaQueryWrapper<>();
        lq.like(StringUtils.isNotEmpty(qdto.getCourseName()), CourseBase::getName, qdto.getCourseName());
        lq.eq(StringUtils.isNotEmpty(qdto.getAuditStatus()), CourseBase::getAuditStatus, qdto.getAuditStatus());


        IPage<CourseBase> p = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
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


    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            throw new RuntimeException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
//todo:向课程营销表保存课程营销信息


        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if (i <= 0) {
            throw new RuntimeException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);

//todo:查询课程基本信息及营销信息并返回


    }

    @Override
    public CourseBaseInfoDto gid(Long courseId) {
        //查询课程信息
       return getCourseBaseInfo(courseId);
    }

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Override
    public CourseBaseInfoDto upd(CourseBaseInfoDto editCourseDto) {
        CourseBase courseBase = new CourseBase();
        CourseMarket courseMarket = new CourseMarket();

        BeanUtils.copyProperties(editCourseDto,courseBase);
        BeanUtils.copyProperties(editCourseDto,courseMarket);

        updateById(courseBase);
//        courseMarketMapper.updateById(courseMarket);

        saveCourseMarket(courseMarket);

        return getCourseBaseInfo(editCourseDto.getId());
    }

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    private CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;


    }



    private int saveCourseMarket(CourseMarket courseMarketNew) {

        //收费规则
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new ParamException("收费规则没有选择");
        }
        //收费规则为收费
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                throw new ParamException("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if (courseMarketObj == null) {
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            BeanUtils.copyProperties(courseMarketNew, courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }
}
