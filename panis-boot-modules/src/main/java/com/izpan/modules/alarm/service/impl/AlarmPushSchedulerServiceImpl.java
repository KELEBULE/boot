package com.izpan.modules.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import com.izpan.modules.alarm.job.AlarmPushJob;
import com.izpan.modules.alarm.service.IAlarmPushSchedulerService;
import com.izpan.modules.alarm.service.IAlarmRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报警推送任务调度服务实现
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.alarm.service.impl.AlarmPushSchedulerServiceImpl
 * @CreateTime 2026-03-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushSchedulerServiceImpl implements IAlarmPushSchedulerService {

    private static final String JOB_GROUP = "ALARM_PUSH";
    private static final String TRIGGER_GROUP = "ALARM_PUSH_TRIGGER";

    private final Scheduler scheduler;
    private final IAlarmRuleService alarmRuleService;

    @Override
    public void createJob(Long ruleId, Integer pushInterval) throws SchedulerException {
        if (ruleId == null || pushInterval == null || pushInterval <= 0) {
            log.warn("Invalid ruleId or pushInterval: ruleId={}, pushInterval={}", ruleId, pushInterval);
            return;
        }

        JobDetail jobDetail = JobBuilder.newJob(AlarmPushJob.class)
                .withIdentity(getJobKey(ruleId))
                .usingJobData("ruleId", String.valueOf(ruleId))
                .storeDurably()
                .build();

        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(pushInterval)
                .repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(ruleId))
                .forJob(jobDetail)
                .startNow()
                .withSchedule(scheduleBuilder)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Created alarm push job: ruleId={}, interval={}min", ruleId, pushInterval);
    }

    @Override
    public void updateJob(Long ruleId, Integer pushInterval) throws SchedulerException {
        if (ruleId == null || pushInterval == null || pushInterval <= 0) {
            return;
        }

        TriggerKey triggerKey = getTriggerKey(ruleId);
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(pushInterval)
                .repeatForever();

        Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder)
                .startNow()
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
        log.info("Updated alarm push job: ruleId={}, interval={}min", ruleId, pushInterval);
    }

    @Override
    public void deleteJob(Long ruleId) throws SchedulerException {
        if (ruleId == null) {
            return;
        }

        JobKey jobKey = getJobKey(ruleId);
        TriggerKey triggerKey = getTriggerKey(ruleId);

        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(jobKey);
        log.info("Deleted alarm push job: ruleId={}", ruleId);
    }

    @Override
    public boolean checkExists(Long ruleId) throws SchedulerException {
        if (ruleId == null) {
            return false;
        }
        return scheduler.checkExists(getJobKey(ruleId));
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void initAllJobs() {
        log.info("Initializing alarm push jobs...");

        try {
            if (scheduler.isInStandbyMode()) {
                log.info("Scheduler is in standby mode, starting...");
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error("Failed to start scheduler", e);
        }

        List<AlarmRule> enabledRules = alarmRuleService.list(
                new LambdaQueryWrapper<AlarmRule>()
                        .eq(AlarmRule::getRuleStatus, 1)
        );

        int successCount = 0;
        for (AlarmRule rule : enabledRules) {
            try {
                if (!checkExists(rule.getRuleId())) {
                    Integer pushInterval = rule.getPushInterval();
                    if (pushInterval != null && pushInterval > 0) {
                        createJob(rule.getRuleId(), pushInterval);
                        successCount++;
                    }
                }
            } catch (SchedulerException e) {
                log.error("Failed to initialize job for rule: ruleId={}", rule.getRuleId(), e);
            }
        }

        log.info("Initialized {}/{} alarm push jobs", successCount, enabledRules.size());
    }

    private JobKey getJobKey(Long ruleId) {
        return JobKey.jobKey("ALARM_PUSH_" + ruleId, JOB_GROUP);
    }

    private TriggerKey getTriggerKey(Long ruleId) {
        return TriggerKey.triggerKey("ALARM_PUSH_TRIGGER_" + ruleId, TRIGGER_GROUP);
    }
}
