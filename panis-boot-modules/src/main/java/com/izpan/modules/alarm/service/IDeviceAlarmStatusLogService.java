package com.izpan.modules.alarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.modules.alarm.domain.entity.DeviceAlarmStatusLog;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmStatusLogVO;

import java.util.List;

public interface IDeviceAlarmStatusLogService extends IService<DeviceAlarmStatusLog> {

    List<DeviceAlarmStatusLogVO> listByAlarmId(Long alarmId);

    void saveLog(Long alarmId, String alarmCode, Integer beforeStatus, Integer afterStatus,
                 Integer operateType, Long operateUserId, String operateUserName,
                 Integer operateSource, String operateRemark);
}
