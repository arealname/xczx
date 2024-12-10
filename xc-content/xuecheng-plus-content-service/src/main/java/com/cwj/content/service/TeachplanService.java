package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.Teachplan;
import com.cwj.content.model.po.dto.SaveTeachplanDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.xccommon.Result;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface TeachplanService extends IService<Teachplan> {

    List<TeachplanDto> tn(Long courseId);

    void su(SaveTeachplanDto teachplan);

    Result de(Long courseId);

    Result mv(Long planId, int i);
}
