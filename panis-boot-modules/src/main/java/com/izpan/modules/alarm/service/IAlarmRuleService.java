package com.izpan.modules.alarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.AlarmRuleBO;
import com.izpan.modules.alarm.domain.entity.AlarmRule;

public interface IAlarmRuleService extends IService<AlarmRule> {

    IPage<AlarmRule> listAlarmRulePage(PageQuery pageQuery, AlarmRuleBO alarmRuleBO);
}
