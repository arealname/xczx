package com.xuecheng.media.mapper;

import com.xuecheng.media.model.po.MediaFiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 媒资信息 Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface MediaFilesMapper extends BaseMapper<MediaFiles> {

}
