package com.izpan.modules.alarm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceAlarmMapper extends BaseMapper<DeviceAlarm> {
}
