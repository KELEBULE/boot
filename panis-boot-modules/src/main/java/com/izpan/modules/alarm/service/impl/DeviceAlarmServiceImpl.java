package com.izpan.modules.alarm.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.DeviceAlarmBO;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmMapper;
import com.izpan.modules.alarm.service.IDeviceAlarmService;

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

    @Override
    public Map<Integer, Long> getAlarmLevelDistribution() {
        List<DeviceAlarm> allAlarms = this.list();
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(1, 0L);
        distribution.put(2, 0L);
        distribution.put(3, 0L);

        for (DeviceAlarm alarm : allAlarms) {
            Integer level = alarm.getAlarmLevel();
            if (level != null && distribution.containsKey(level)) {
                distribution.put(level, distribution.get(level) + 1);
            }
        }
        return distribution;
    }

    @Override
    public List<Map<String, Object>> getDeviceAlarmTop(int limit) {
        return baseMapper.selectDeviceAlarmTop(limit);
    }

    @Override
    public List<Map<String, Object>> getFrequentAlarmParts(Long deviceId, String startTime, String endTime) {
        return baseMapper.selectFrequentAlarmParts(deviceId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getFrequentAlarmTime(Long deviceId, String startTime, String endTime) {
        return baseMapper.selectFrequentAlarmTime(deviceId, startTime, endTime);
    }

    @Override
    public Map<Integer, Long> getAlarmLevelStatsByDevice(Long deviceId, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<DeviceAlarm>()
                .eq(deviceId != null, DeviceAlarm::getDeviceId, deviceId)
                .ge(DeviceAlarm::getAlarmTime, start)
                .le(DeviceAlarm::getAlarmTime, end);

        List<DeviceAlarm> alarms = this.list(queryWrapper);
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(1, 0L);
        distribution.put(2, 0L);
        distribution.put(3, 0L);

        for (DeviceAlarm alarm : alarms) {
            Integer level = alarm.getAlarmLevel();
            if (level != null && distribution.containsKey(level)) {
                distribution.put(level, distribution.get(level) + 1);
            }
        }
        return distribution;
    }

    @Override
    public List<Map<String, Object>> getTemperatureTrend(Long deviceId, String startTime, String endTime) {
        return baseMapper.selectTemperatureTrend(deviceId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getDailyAlarmTrend(Long deviceId, String startTime, String endTime) {
        return baseMapper.selectDailyAlarmTrend(deviceId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getPartTemperatureTrend(Long partId, String startTime, String endTime) {
        return baseMapper.selectPartTemperatureTrend(partId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getPartAlarmTemperatureTrend(Long partId, String startTime, String endTime) {
        return baseMapper.selectPartAlarmTemperatureTrend(partId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getPartHourlyAlarmDistribution(Long partId, String startTime, String endTime) {
        return baseMapper.selectPartHourlyAlarmDistribution(partId, startTime, endTime);
    }

    @Override
    public Map<Integer, Long> getPartAlarmLevelStats(Long partId, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<DeviceAlarm>()
                .eq(partId != null, DeviceAlarm::getPartId, partId)
                .ge(DeviceAlarm::getAlarmTime, start)
                .le(DeviceAlarm::getAlarmTime, end);

        List<DeviceAlarm> alarms = this.list(queryWrapper);
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(1, 0L);
        distribution.put(2, 0L);
        distribution.put(3, 0L);

        for (DeviceAlarm alarm : alarms) {
            Integer level = alarm.getAlarmLevel();
            if (level != null && distribution.containsKey(level)) {
                distribution.put(level, distribution.get(level) + 1);
            }
        }
        return distribution;
    }
}
