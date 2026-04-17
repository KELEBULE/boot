package com.izpan.modules.equipment.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.exception.BizException;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceBatchStatusDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryDeviceMapper;
import com.izpan.modules.equipment.service.IFactoryDeviceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FactoryDeviceServiceImpl extends ServiceImpl<FactoryDeviceMapper, FactoryDevice> implements IFactoryDeviceService {

    private final DevicePartMapper devicePartMapper;

    @Override
    public IPage<FactoryDevice> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO) {
        LambdaQueryWrapper<FactoryDevice> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getDeviceCode()), FactoryDevice::getDeviceCode, searchDTO.getDeviceCode())
                .like(StringUtils.isNotBlank(searchDTO.getDeviceName()), FactoryDevice::getDeviceName, searchDTO.getDeviceName())
                .eq(searchDTO.getDeviceStatus() != null, FactoryDevice::getDeviceStatus, searchDTO.getDeviceStatus())
                .eq(searchDTO.getLocationId() != null, FactoryDevice::getLocationId, searchDTO.getLocationId())
                .like(StringUtils.isNotBlank(searchDTO.getManufacturer()), FactoryDevice::getManufacturer, searchDTO.getManufacturer())
                .orderByDesc(FactoryDevice::getCreateTime);

        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public FactoryDevice getFactoryDeviceById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<DevicePartTreeVO> getDevicePartTreeByLocationId(Long locationId) {
        return baseMapper.selectDevicePartTreeByLocationId(locationId);
    }

    @Override
    public boolean addFactoryDevice(FactoryDeviceAddDTO addDTO) {
        LambdaQueryWrapper<FactoryDevice> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(FactoryDevice::getDeviceCode, addDTO.getDeviceCode());
        if (baseMapper.selectCount(checkWrapper) > 0) {
            throw new BizException("设备编码已存在: " + addDTO.getDeviceCode());
        }

        FactoryDevice device = FactoryDevice.builder()
                .deviceCode(addDTO.getDeviceCode())
                .deviceName(addDTO.getDeviceName())
                .deviceModel(addDTO.getDeviceModel())
                .locationId(addDTO.getLocationId())
                .installTime(addDTO.getInstallTime())
                .startUseTime(addDTO.getStartUseTime())
                .deviceStatus(addDTO.getDeviceStatus())
                .deviceSn(addDTO.getDeviceSn())
                .manufacturer(addDTO.getManufacturer())
                .maintainCycle(addDTO.getMaintainCycle())
                .lastMaintainTime(addDTO.getLastMaintainTime())
                .warrantyPeriod(addDTO.getWarrantyPeriod())
                .deviceNote(addDTO.getDeviceNote())
                .modelUrl(addDTO.getModelUrl())
                .imageUrl(addDTO.getImageUrl())
                .build();
        return save(device);
    }

    @Override
    public boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO) {
        FactoryDevice device = FactoryDevice.builder()
                .deviceId(updateDTO.getDeviceId())
                .deviceCode(updateDTO.getDeviceCode())
                .deviceName(updateDTO.getDeviceName())
                .deviceModel(updateDTO.getDeviceModel())
                .locationId(updateDTO.getLocationId())
                .installTime(updateDTO.getInstallTime())
                .startUseTime(updateDTO.getStartUseTime())
                .scrapTime(updateDTO.getScrapTime())
                .deviceStatus(updateDTO.getDeviceStatus())
                .scrapStatus(updateDTO.getScrapStatus())
                .totalWorkHours(updateDTO.getTotalWorkHours())
                .deviceSn(updateDTO.getDeviceSn())
                .manufacturer(updateDTO.getManufacturer())
                .maintainCycle(updateDTO.getMaintainCycle())
                .lastMaintainTime(updateDTO.getLastMaintainTime())
                .warrantyPeriod(updateDTO.getWarrantyPeriod())
                .deviceNote(updateDTO.getDeviceNote())
                .modelUrl(updateDTO.getModelUrl())
                .imageUrl(updateDTO.getImageUrl())
                .build();
        return updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO) {
        List<Long> ids = deleteDTO.getIds();
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long deviceId : ids) {
            deleteDevicePartsByDeviceId(deviceId);
        }

        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFactoryDeviceByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long deviceId : ids) {
            deleteDevicePartsByDeviceId(deviceId);
        }

        return removeByIds(ids);
    }

    private void deleteDevicePartsByDeviceId(Long deviceId) {
        LambdaQueryWrapper<DevicePart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DevicePart::getDeviceId, deviceId);
        devicePartMapper.delete(queryWrapper);
    }

    @Override
    public Map<Integer, Long> getDeviceStatusDistribution() {
        List<FactoryDevice> allDevices = this.list();
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(1, 0L);
        distribution.put(2, 0L);
        distribution.put(0, 0L);

        for (FactoryDevice device : allDevices) {
            Integer status = device.getDeviceStatus();
            if (status != null && distribution.containsKey(status)) {
                distribution.put(status, distribution.get(status) + 1);
            }
        }
        return distribution;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchScrapDevice(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Long deviceId : ids) {
            FactoryDevice device = this.getById(deviceId);
            if (device == null) {
                continue;
            }

            LambdaUpdateWrapper<FactoryDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FactoryDevice::getDeviceId, deviceId);

            if (device.getDeviceStatus() == 1 && device.getLastOnlineTime() != null) {
                long hoursWorked = ChronoUnit.HOURS.between(device.getLastOnlineTime(), now);
                int currentTotalHours = device.getTotalWorkHours() != null ? device.getTotalWorkHours() : 0;
                updateWrapper.set(FactoryDevice::getTotalWorkHours, currentTotalHours + (int) hoursWorked);
            }

            updateWrapper.set(FactoryDevice::getDeviceStatus, 0)
                    .set(FactoryDevice::getScrapStatus, 1)
                    .set(FactoryDevice::getScrapTime, now)
                    .set(FactoryDevice::getLastOnlineTime, (LocalDateTime) null);

            this.update(updateWrapper);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateDeviceStatus(FactoryDeviceBatchStatusDTO batchStatusDTO) {
        if (batchStatusDTO.getIds() == null || batchStatusDTO.getIds().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        Integer targetStatus = batchStatusDTO.getDeviceStatus();

        for (Long deviceId : batchStatusDTO.getIds()) {
            FactoryDevice device = this.getById(deviceId);
            if (device == null) {
                continue;
            }

            Integer fromStatus = device.getDeviceStatus();
            LambdaUpdateWrapper<FactoryDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FactoryDevice::getDeviceId, deviceId);

            if (fromStatus == 1 && targetStatus != 1) {
                if (device.getLastOnlineTime() != null) {
                    long hoursWorked = ChronoUnit.HOURS.between(device.getLastOnlineTime(), now);
                    int currentTotalHours = device.getTotalWorkHours() != null ? device.getTotalWorkHours() : 0;
                    updateWrapper.set(FactoryDevice::getTotalWorkHours, currentTotalHours + (int) hoursWorked);
                }
                updateWrapper.set(FactoryDevice::getLastOnlineTime, (LocalDateTime) null);
            }

            if (targetStatus == 1) {
                updateWrapper.set(FactoryDevice::getLastOnlineTime, now);
            }

            if (targetStatus == 0) {
                updateWrapper.set(FactoryDevice::getScrapStatus, 1);
                updateWrapper.set(FactoryDevice::getScrapTime, now);
            } else {
                updateWrapper.set(FactoryDevice::getScrapStatus, 0);
            }

            updateWrapper.set(FactoryDevice::getDeviceStatus, targetStatus);
            this.update(updateWrapper);
        }

        return true;
    }
}
