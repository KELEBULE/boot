package com.izpan.modules.alarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.alarm.domain.bo.DeviceAlarmBO;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;

public interface IDeviceAlarmService extends IService<DeviceAlarm> {

    IPage<DeviceAlarm> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmBO deviceAlarmBO);
}
