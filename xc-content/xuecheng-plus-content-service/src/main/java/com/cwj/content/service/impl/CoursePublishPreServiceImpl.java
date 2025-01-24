package com.cwj.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseBaseMapper;
import com.cwj.content.mapper.CourseMarketMapper;
import com.cwj.content.mapper.CoursePublishPreMapper;
import com.cwj.content.mapper.TeachplanMapper;
import com.cwj.content.model.po.CourseBase;
import com.cwj.content.model.po.CourseMarket;
import com.cwj.content.model.po.CoursePublishPre;
import com.cwj.content.model.po.dto.CourseBaseInfoDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.content.service.CourseBaseService;
import com.cwj.content.service.CoursePublishPreService;
import com.cwj.xccommon.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {

    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        /*1、查询课程基本信息、课程营销信息、课程计划信息等课程相关信息，整合为课程预发布信息。
            2、向课程预发布表course_publish_pre插入一条记录，如果已经存在则更新，审核状态为：已提交。
            3、更新课程基本表course_base课程审核状态为：已提交。
            约束：
            1、对已提交审核的课程不允许提交审核。
            2、本机构只允许提交本机构的课程。
            3、没有上传图片不允许提交审核。
            4、没有添加课程计划不允许提交审核。*/

        CourseBaseInfoDto courseBase = courseBaseService.getCourseBaseInfo(courseId);
        if(courseBase.getAuditStatus().equals("202003"))throw new ParamException("已提交，不可重复提交");

       if(! courseBase.getCompanyId().equals(companyId))throw new ParamException("不允许提交其它机构的课程。");
        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            throw new ParamException  ("提交失败，请上传课程图片");
        }
        //查询课程计划信息
        List<TeachplanDto> teachplanTree = teachplanMapper.getp(courseId);
        if(teachplanTree.size()<=0){
            throw new ParamException("提交失败，还没有添加课程计划");
        }

        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //课程基本信息加部分营销信息
//        CourseBase courseBaseInfo = courseBaseService.gid(courseId);
        BeanUtils.copyProperties(courseBase,coursePublishPre);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);

        String jsonString = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(jsonString);

        //设置预发布记录状态,已提交
        coursePublishPre.setStatus("202003");
        //教学机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseService.updateById(courseBase);

    }
}
