package com.cwj.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cwj.tenant.po.R;
import com.cwj.tenant.po.RequestParam;
import com.cwj.tenant.po.T1212;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface T1212Mapper extends BaseMapper<T1212> {

    List<T1212> getmap(RequestParam requestParam);
}
