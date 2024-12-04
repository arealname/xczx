package com.xuecheng.xccommon.dto;


import com.xuecheng.xccommon.po.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    private List<CourseCategoryTreeDto> childrenTreeNodes;


}
