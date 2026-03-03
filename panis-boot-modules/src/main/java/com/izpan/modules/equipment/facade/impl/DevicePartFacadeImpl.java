package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.izpan.modules.equipment.domain.dto.DevicePartAddDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartDeleteDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.vo.DevicePartVO;
import com.izpan.modules.equipment.facade.IDevicePartFacade;
import com.izpan.modules.equipment.service.IDevicePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevicePartFacadeImpl implements IDevicePartFacade {

    private final IDevicePartService devicePartService;

    @Override
    public List<DevicePartVO> listDevicePartByDeviceId(Long deviceId) {
        List<DevicePart> parts = devicePartService.listDevicePartByDeviceId(deviceId);
        return parts.stream()
                .map(part -> BeanUtil.copyProperties(part, DevicePartVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DevicePartVO getDevicePartById(Long id) {
        DevicePart part = devicePartService.getDevicePartById(id);
        return BeanUtil.copyProperties(part, DevicePartVO.class);
    }

    @Override
    public DevicePartVO getDevicePartByPartCode(String partCode) {
        DevicePart part = devicePartService.getDevicePartByPartCode(partCode);
        return BeanUtil.copyProperties(part, DevicePartVO.class);
    }

    @Override
    @Transactional
    public boolean addDevicePart(DevicePartAddDTO addDTO) {
        return devicePartService.addDevicePart(addDTO);
    }

    @Override
    @Transactional
    public boolean updateDevicePart(DevicePartUpdateDTO updateDTO) {
        return devicePartService.updateDevicePart(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteDevicePart(DevicePartDeleteDTO deleteDTO) {
        return devicePartService.deleteDevicePart(deleteDTO);
    }

    @Override
    @Transactional
    public boolean deleteDevicePartByIds(List<Long> ids) {
        return devicePartService.deleteDevicePartByIds(ids);
    }
}
