package com.izpan.modules.alarm.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.DeviceAlarmBO;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;

public interface IDeviceAlarmService extends IService<DeviceAlarm> {

    IPage<DeviceAlarm> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmBO deviceAlarmBO);

    Map<Integer, Long> getAlarmLevelDistribution();

    List<Map<String, Object>> getDeviceAlarmTop(int limit);

    List<Map<String, Object>> getFrequentAlarmParts(Long deviceId, String startTime, String endTime);

    List<Map<String, Object>> getFrequentAlarmTime(Long deviceId, String startTime, String endTime);

    Map<Integer, Long> getAlarmLevelStatsByDevice(Long deviceId, String startTime, String endTime);

    List<Map<String, Object>> getTemperatureTrend(Long deviceId, String startTime, String endTime);

    List<Map<String, Object>> getDailyAlarmTrend(Long deviceId, String startTime, String endTime);

    List<Map<String, Object>> getPartTemperatureTrend(Long partId, String startTime, String endTime);

    List<Map<String, Object>> getPartAlarmTemperatureTrend(Long partId, String startTime, String endTime);

    List<Map<String, Object>> getPartHourlyAlarmDistribution(Long partId, String startTime, String endTime);

    Map<Integer, Long> getPartAlarmLevelStats(Long partId, String startTime, String endTime);
}
