package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.dto.DevicePartAddDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartDeleteDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartUpdateDTO;

import java.util.List;

public interface IDevicePartService extends IService<DevicePart> {

    List<DevicePart> listDevicePartByDeviceId(Long deviceId);

    DevicePart getDevicePartById(Long id);

    boolean addDevicePart(DevicePartAddDTO addDTO);

    boolean updateDevicePart(DevicePartUpdateDTO updateDTO);

    boolean deleteDevicePart(DevicePartDeleteDTO deleteDTO);

    boolean deleteDevicePartByIds(List<Long> ids);
}
