package com.izpan.modules.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.DeviceAlarmBO;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmMapper;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeviceAlarmServiceImpl extends ServiceImpl<DeviceAlarmMapper, DeviceAlarm> implements IDeviceAlarmService {

    @Override
    public IPage<DeviceAlarm> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmBO deviceAlarmBO) {
        LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<DeviceAlarm>()
                .like(StringUtils.isNotBlank(deviceAlarmBO.getAlarmCode()), DeviceAlarm::getAlarmCode, deviceAlarmBO.getAlarmCode())
                .eq(deviceAlarmBO.getDeviceId() != null, DeviceAlarm::getDeviceId, deviceAlarmBO.getDeviceId())
                .eq(deviceAlarmBO.getPartId() != null, DeviceAlarm::getPartId, deviceAlarmBO.getPartId())
                .eq(deviceAlarmBO.getAlarmType() != null, DeviceAlarm::getAlarmType, deviceAlarmBO.getAlarmType())
                .eq(deviceAlarmBO.getAlarmLevel() != null, DeviceAlarm::getAlarmLevel, deviceAlarmBO.getAlarmLevel())
                .eq(deviceAlarmBO.getConfirmStatus() != null, DeviceAlarm::getConfirmStatus, deviceAlarmBO.getConfirmStatus())
                .eq(deviceAlarmBO.getClearStatus() != null, DeviceAlarm::getClearStatus, deviceAlarmBO.getClearStatus())
                .ge(deviceAlarmBO.getAlarmTimeStart() != null, DeviceAlarm::getAlarmTime, deviceAlarmBO.getAlarmTimeStart())
                .le(deviceAlarmBO.getAlarmTimeEnd() != null, DeviceAlarm::getAlarmTime, deviceAlarmBO.getAlarmTimeEnd())
                .orderByDesc(DeviceAlarm::getAlarmTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }
}
