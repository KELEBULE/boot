package com.izpan.modules.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.AlarmRuleBO;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import com.izpan.modules.alarm.repository.mapper.AlarmRuleMapper;
import com.izpan.modules.alarm.service.IAlarmRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AlarmRuleServiceImpl extends ServiceImpl<AlarmRuleMapper, AlarmRule> implements IAlarmRuleService {

    @Override
    public IPage<AlarmRule> listAlarmRulePage(PageQuery pageQuery, AlarmRuleBO alarmRuleBO) {
        LambdaQueryWrapper<AlarmRule> queryWrapper = new LambdaQueryWrapper<AlarmRule>()
                .like(StringUtils.isNotBlank(alarmRuleBO.getRuleName()), AlarmRule::getRuleName, alarmRuleBO.getRuleName())
                .eq(alarmRuleBO.getRuleStatus() != null, AlarmRule::getRuleStatus, alarmRuleBO.getRuleStatus())
                .orderByDesc(AlarmRule::getRuleId);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }
}
