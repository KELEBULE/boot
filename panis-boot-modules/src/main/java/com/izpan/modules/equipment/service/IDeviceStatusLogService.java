package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.modules.equipment.domain.dto.DeviceScrapDTO;
import com.izpan.modules.equipment.domain.dto.DeviceStatusChangeDTO;
import com.izpan.modules.equipment.domain.entity.DeviceStatusLog;
import com.izpan.modules.equipment.domain.vo.DeviceStatusLogVO;

import java.util.List;

public interface IDeviceStatusLogService extends IService<DeviceStatusLog> {

    List<DeviceStatusLogVO> getDeviceStatusLogsByDeviceId(Long deviceId);

    boolean changeDeviceStatus(DeviceStatusChangeDTO statusChangeDTO);

    boolean scrapDevices(DeviceScrapDTO scrapDTO);

    boolean saveStatusLog(DeviceStatusLog statusLog);
}
