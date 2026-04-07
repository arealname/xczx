package com.cwj.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.message.po.MqMessage;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-30
 */
public interface MqMessageService extends IService<MqMessage> {


    public MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3);

    public List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count);

    /**
     * @param id 消息id
     * @return int 更新成功：1
     * @description 完成任务
     * @author Mr.M
     * @date 2022/9/21 20:49
     */
    public int completed(long id);

    /**
     * @param id 消息id
     * @return int 更新成功：1
     * @description 完成阶段任务
     * @author Mr.M
     * @date 2022/9/21 20:49
     */
    public int completedStageOne(long id);

    public int completedStageTwo(long id);

    public int completedStageThree(long id);

    public int completedStageFour(long id);

    /**
     * @param id
     * @return int
     * @description 查询阶段状态
     * @author Mr.M
     * @date 2022/9/21 20:54
     */
    public int getStageOne(long id);

    public int getStageTwo(long id);

    public int getStageThree(long id);

    public int getStageFour(long id);
}
