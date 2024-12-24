package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.media.model.po.MediaProcess;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-11
 */
public interface MediaProcessService extends IService<MediaProcess> {
   public void  saveProcessFinishStatus(Long id,String st,String  fileId, String url,String err);
}
