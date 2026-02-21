package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.exception.BizException;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.dto.DevicePartAddDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartDeleteDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartUpdateDTO;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryDeviceMapper;
import com.izpan.modules.equipment.service.IDevicePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevicePartServiceImpl extends ServiceImpl<DevicePartMapper, DevicePart> implements IDevicePartService {

    @Autowired
    private FactoryDeviceMapper factoryDeviceMapper;

    @Override
    public List<DevicePart> listDevicePartByDeviceId(Long deviceId) {
        LambdaQueryWrapper<DevicePart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DevicePart::getDeviceId, deviceId)
                .orderByAsc(DevicePart::getPartCode);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public DevicePart getDevicePartById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean addDevicePart(DevicePartAddDTO addDTO) {
        // 验证设备是否存在
        FactoryDevice device = factoryDeviceMapper.selectById(addDTO.getDeviceId());
        if (device == null) {
            throw new BizException("关联的设备不存在，设备ID: " + addDTO.getDeviceId());
        }
        
        DevicePart part = DevicePart.builder()
                .partCode(addDTO.getPartCode())
                .partName(addDTO.getPartName())
                .deviceId(addDTO.getDeviceId())
                .partType(addDTO.getPartType())
                .monitorEnabled(addDTO.getMonitorEnabled())
                .installPosition(addDTO.getInstallPosition())
                .partStatus(addDTO.getPartStatus())
                .build();
        return save(part);
    }

    @Override
    public boolean updateDevicePart(DevicePartUpdateDTO updateDTO) {
        // 验证设备是否存在
        FactoryDevice device = factoryDeviceMapper.selectById(updateDTO.getDeviceId());
        if (device == null) {
            throw new BizException("关联的设备不存在，设备ID: " + updateDTO.getDeviceId());
        }
        
        DevicePart part = DevicePart.builder()
                .partId(updateDTO.getPartId())
                .partCode(updateDTO.getPartCode())
                .partName(updateDTO.getPartName())
                .deviceId(updateDTO.getDeviceId())
                .partType(updateDTO.getPartType())
                .monitorEnabled(updateDTO.getMonitorEnabled())
                .installPosition(updateDTO.getInstallPosition())
                .partStatus(updateDTO.getPartStatus())
                .build();
        return updateById(part);
    }

    @Override
    public boolean deleteDevicePart(DevicePartDeleteDTO deleteDTO) {
        return removeByIds(deleteDTO.getIds());
    }

    @Override
    public boolean deleteDevicePartByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
