package com.izpan.modules.alarm.facade;

import com.izpan.modules.alarm.domain.vo.DeviceAlarmStatusLogVO;

import java.util.List;

public interface IDeviceAlarmStatusLogFacade {

    List<DeviceAlarmStatusLogVO> listByAlarmId(Long alarmId);
}
