package com.izpan.modules.equipment.facade;

import com.izpan.modules.equipment.domain.dto.DevicePartAddDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartDeleteDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartUpdateDTO;
import com.izpan.modules.equipment.domain.vo.DevicePartVO;

import java.util.List;

public interface IDevicePartFacade {

    List<DevicePartVO> listDevicePartByDeviceId(Long deviceId);

    DevicePartVO getDevicePartById(Long id);

    boolean addDevicePart(DevicePartAddDTO addDTO);

    boolean updateDevicePart(DevicePartUpdateDTO updateDTO);

    boolean deleteDevicePart(DevicePartDeleteDTO deleteDTO);

    boolean deleteDevicePartByIds(List<Long> ids);
}
