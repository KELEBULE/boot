package com.izpan.modules.alarm.facade.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.izpan.modules.alarm.domain.vo.DeviceAlarmStatusLogVO;
import com.izpan.modules.alarm.facade.IDeviceAlarmStatusLogFacade;
import com.izpan.modules.alarm.service.IDeviceAlarmStatusLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceAlarmStatusLogFacadeImpl implements IDeviceAlarmStatusLogFacade {

    private final IDeviceAlarmStatusLogService deviceAlarmStatusLogService;

    @Override
    public List<DeviceAlarmStatusLogVO> listByAlarmId(Long alarmId) {
        return deviceAlarmStatusLogService.listByAlarmId(alarmId);
    }
}
