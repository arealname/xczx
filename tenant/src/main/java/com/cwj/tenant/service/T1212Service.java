package com.cwj.tenant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.tenant.po.R;
import com.cwj.tenant.po.RequestParam;
import com.cwj.tenant.po.T1212;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-13
 */
public interface T1212Service extends IService<T1212> {

    R getmap(RequestParam requestParam);
}
