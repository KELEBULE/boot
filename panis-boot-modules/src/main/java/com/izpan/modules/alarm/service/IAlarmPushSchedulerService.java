package com.izpan.modules.alarm.service;

import org.quartz.SchedulerException;

/**
 * 报警推送任务调度服务接口
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.alarm.service.IAlarmPushSchedulerService
 * @CreateTime 2026-03-12
 */
public interface IAlarmPushSchedulerService {

    /**
     * 创建定时任务
     *
     * @param ruleId       规则ID
     * @param pushInterval 推送间隔(分钟)
     * @throws SchedulerException 调度异常
     */
    void createJob(Long ruleId, Integer pushInterval) throws SchedulerException;

    /**
     * 更新定时任务
     *
     * @param ruleId       规则ID
     * @param pushInterval 推送间隔(分钟)
     * @throws SchedulerException 调度异常
     */
    void updateJob(Long ruleId, Integer pushInterval) throws SchedulerException;

    /**
     * 删除定时任务
     *
     * @param ruleId 规则ID
     * @throws SchedulerException 调度异常
     */
    void deleteJob(Long ruleId) throws SchedulerException;

    /**
     * 检查任务是否存在
     *
     * @param ruleId 规则ID
     * @return 是否存在
     * @throws SchedulerException 调度异常
     */
    boolean checkExists(Long ruleId) throws SchedulerException;

    /**
     * 初始化所有启用的规则任务
     * 应用启动时自动调用
     */
    void initAllJobs();
}
