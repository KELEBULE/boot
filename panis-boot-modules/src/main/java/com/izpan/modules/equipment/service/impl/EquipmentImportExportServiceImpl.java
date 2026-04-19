package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.izpan.modules.equipment.domain.dto.EquipmentImportDTO;
import com.izpan.modules.equipment.domain.dto.EquipmentImportResultDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.vo.EquipmentExportVO;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryAreaMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryDeviceMapper;
import com.izpan.modules.equipment.repository.mapper.FactoryInfoMapper;
import com.izpan.modules.equipment.service.IEquipmentImportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentImportExportServiceImpl implements IEquipmentImportExportService {

    private final FactoryInfoMapper factoryInfoMapper;
    private final FactoryAreaMapper factoryAreaMapper;
    private final FactoryDeviceMapper factoryDeviceMapper;
    private final DevicePartMapper devicePartMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EquipmentImportResultDTO importEquipment(List<EquipmentImportDTO> importList) {
        EquipmentImportResultDTO result = new EquipmentImportResultDTO();
        result.setTotalCount(importList.size());
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setFactoryCount(0);
        result.setAreaCount(0);
        result.setDeviceCount(0);
        result.setPartCount(0);
        result.setErrorMessages(new ArrayList<>());

        Map<String, FactoryInfo> factoryCache = new HashMap<>();
        Map<String, FactoryArea> areaCache = new HashMap<>();
        Map<String, FactoryDevice> deviceCache = new HashMap<>();
        Map<String, DevicePart> partCache = new HashMap<>();

        int rowNum = 2;
        for (EquipmentImportDTO dto : importList) {
            try {
                if (!validateImportData(dto, rowNum, result)) {
                    rowNum++;
                    continue;
                }

                FactoryInfo factory = processFactory(dto, factoryCache);
                if (factory.getFactoryId() == null) {
                    result.setFactoryCount(result.getFactoryCount() + 1);
                }

                FactoryArea area = processArea(dto, factory, areaCache);
                if (area.getAreaId() == null) {
                    result.setAreaCount(result.getAreaCount() + 1);
                }

                FactoryDevice device = processDevice(dto, area, deviceCache);
                if (device.getDeviceId() == null) {
                    result.setDeviceCount(result.getDeviceCount() + 1);
                }

                if (StringUtils.isNotBlank(dto.getPartCode())) {
                    DevicePart part = processPart(dto, device, partCache);
                    if (part.getPartId() == null) {
                        result.setPartCount(result.getPartCount() + 1);
                    }
                }

                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.setFailCount(result.getFailCount() + 1);
                result.getErrorMessages().add("第" + rowNum + "行数据导入失败: " + e.getMessage());
                log.error("导入第{}行数据失败", rowNum, e);
            }
            rowNum++;
        }

        return result;
    }

    private boolean validateImportData(EquipmentImportDTO dto, int rowNum, EquipmentImportResultDTO result) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(dto.getFactoryCode())) {
            errors.add("工厂编码不能为空");
        }
        if (StringUtils.isBlank(dto.getFactoryName())) {
            errors.add("工厂名称不能为空");
        }
        if (StringUtils.isBlank(dto.getAreaCode())) {
            errors.add("厂区编码不能为空");
        }
        if (StringUtils.isBlank(dto.getAreaName())) {
            errors.add("厂区名称不能为空");
        }
        if (StringUtils.isBlank(dto.getDeviceCode())) {
            errors.add("设备编码不能为空");
        }
        if (StringUtils.isBlank(dto.getDeviceName())) {
            errors.add("设备名称不能为空");
        }

        if (!errors.isEmpty()) {
            result.setFailCount(result.getFailCount() + 1);
            result.getErrorMessages().add("第" + rowNum + "行数据校验失败: " + String.join(", ", errors));
            return false;
        }
        return true;
    }

    private FactoryInfo processFactory(EquipmentImportDTO dto, Map<String, FactoryInfo> cache) {
        String cacheKey = dto.getFactoryCode();
        if (cache.containsKey(cacheKey)) {
            FactoryInfo cached = cache.get(cacheKey);
            updateFactoryFromDto(cached, dto);
            factoryInfoMapper.updateById(cached);
            return cached;
        }

        LambdaQueryWrapper<FactoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryInfo::getFactoryCode, dto.getFactoryCode());
        FactoryInfo factory = factoryInfoMapper.selectOne(queryWrapper);

        if (factory == null) {
            factory = FactoryInfo.builder()
                    .factoryCode(dto.getFactoryCode())
                    .factoryName(dto.getFactoryName())
                    .factoryAddress(dto.getFactoryAddress())
                    .contactPerson(dto.getContactPerson())
                    .contactPhone(dto.getContactPhone())
                    .status(parseStatus(dto.getFactoryStatus(), 1))
                    .build();
            factoryInfoMapper.insert(factory);
        } else {
            updateFactoryFromDto(factory, dto);
            factoryInfoMapper.updateById(factory);
        }

        cache.put(cacheKey, factory);
        return factory;
    }

    private void updateFactoryFromDto(FactoryInfo factory, EquipmentImportDTO dto) {
        if (StringUtils.isNotBlank(dto.getFactoryName())) {
            factory.setFactoryName(dto.getFactoryName());
        }
        factory.setFactoryAddress(dto.getFactoryAddress());
        factory.setContactPerson(dto.getContactPerson());
        factory.setContactPhone(dto.getContactPhone());
        if (dto.getFactoryStatus() != null) {
            factory.setStatus(parseStatus(dto.getFactoryStatus(), factory.getStatus()));
        }
    }

    private FactoryArea processArea(EquipmentImportDTO dto, FactoryInfo factory, Map<String, FactoryArea> cache) {
        String cacheKey = dto.getAreaCode();
        if (cache.containsKey(cacheKey)) {
            FactoryArea cached = cache.get(cacheKey);
            updateAreaFromDto(cached, dto, factory.getFactoryId());
            factoryAreaMapper.updateById(cached);
            return cached;
        }

        LambdaQueryWrapper<FactoryArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryArea::getLocationCode, dto.getAreaCode());
        FactoryArea area = factoryAreaMapper.selectOne(queryWrapper);

        if (area == null) {
            area = FactoryArea.builder()
                    .locationCode(dto.getAreaCode())
                    .areaName(dto.getAreaName())
                    .factoryId(factory.getFactoryId())
                    .parentId(0L)
                    .areaType(parseInteger(dto.getAreaType(), 1))
                    .areaStatus(parseStatus(dto.getAreaStatus(), 1))
                    .areaOrder(parseInteger(dto.getAreaOrder(), 0))
                    .build();
            factoryAreaMapper.insert(area);
        } else {
            updateAreaFromDto(area, dto, factory.getFactoryId());
            factoryAreaMapper.updateById(area);
        }

        cache.put(cacheKey, area);
        return area;
    }

    private void updateAreaFromDto(FactoryArea area, EquipmentImportDTO dto, Long factoryId) {
        if (StringUtils.isNotBlank(dto.getAreaName())) {
            area.setAreaName(dto.getAreaName());
        }
        area.setFactoryId(factoryId);
        if (dto.getAreaType() != null) {
            area.setAreaType(parseInteger(dto.getAreaType(), area.getAreaType()));
        }
        if (dto.getAreaStatus() != null) {
            area.setAreaStatus(parseStatus(dto.getAreaStatus(), area.getAreaStatus()));
        }
        if (dto.getAreaOrder() != null) {
            area.setAreaOrder(parseInteger(dto.getAreaOrder(), area.getAreaOrder()));
        }
    }

    private FactoryDevice processDevice(EquipmentImportDTO dto, FactoryArea area, Map<String, FactoryDevice> cache) {
        String cacheKey = dto.getDeviceCode();
        if (cache.containsKey(cacheKey)) {
            FactoryDevice cached = cache.get(cacheKey);
            updateDeviceFromDto(cached, dto, area);
            factoryDeviceMapper.updateById(cached);
            return cached;
        }

        LambdaQueryWrapper<FactoryDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryDevice::getDeviceCode, dto.getDeviceCode());
        FactoryDevice device = factoryDeviceMapper.selectOne(queryWrapper);

        if (device == null) {
            device = FactoryDevice.builder()
                    .deviceCode(dto.getDeviceCode())
                    .deviceName(dto.getDeviceName())
                    .deviceModel(dto.getDeviceModel())
                    .locationId(area.getAreaId())
                    .deviceStatus(parseStatus(dto.getDeviceStatus(), 1))
                    .scrapStatus(0)
                    .deviceSn(dto.getDeviceSn())
                    .manufacturer(dto.getManufacturer())
                    .installTime(parseDateTime(dto.getInstallTime()))
                    .startUseTime(parseDateTime(dto.getStartUseTime()))
                    .maintainCycle(parseInteger(dto.getMaintainCycle(), null))
                    .lastMaintainTime(parseDateTime(dto.getLastMaintainTime()))
                    .warrantyPeriod(parseInteger(dto.getWarrantyPeriod(), null))
                    .deviceNote(dto.getDeviceNote())
                    .modelUrl(dto.getModelUrl())
                    .imageUrl(dto.getImageUrl())
                    .build();
            factoryDeviceMapper.insert(device);
        } else {
            updateDeviceFromDto(device, dto, area);
            factoryDeviceMapper.updateById(device);
        }

        cache.put(cacheKey, device);
        return device;
    }

    private void updateDeviceFromDto(FactoryDevice device, EquipmentImportDTO dto, FactoryArea area) {
        if (StringUtils.isNotBlank(dto.getDeviceName())) {
            device.setDeviceName(dto.getDeviceName());
        }
        device.setDeviceModel(dto.getDeviceModel());
        device.setLocationId(area.getAreaId());
        device.setDeviceSn(dto.getDeviceSn());
        device.setManufacturer(dto.getManufacturer());
        if (dto.getDeviceStatus() != null) {
            device.setDeviceStatus(parseStatus(dto.getDeviceStatus(), device.getDeviceStatus()));
        }
        device.setInstallTime(parseDateTime(dto.getInstallTime()));
        device.setStartUseTime(parseDateTime(dto.getStartUseTime()));
        device.setMaintainCycle(parseInteger(dto.getMaintainCycle(), device.getMaintainCycle()));
        device.setLastMaintainTime(parseDateTime(dto.getLastMaintainTime()));
        device.setWarrantyPeriod(parseInteger(dto.getWarrantyPeriod(), device.getWarrantyPeriod()));
        device.setDeviceNote(dto.getDeviceNote());
        if (StringUtils.isNotBlank(dto.getModelUrl())) {
            device.setModelUrl(dto.getModelUrl());
        }
        if (StringUtils.isNotBlank(dto.getImageUrl())) {
            device.setImageUrl(dto.getImageUrl());
        }
    }

    private DevicePart processPart(EquipmentImportDTO dto, FactoryDevice device, Map<String, DevicePart> cache) {
        String cacheKey = dto.getPartCode() + "_" + device.getDeviceId();
        if (cache.containsKey(cacheKey)) {
            DevicePart cached = cache.get(cacheKey);
            updatePartFromDto(cached, dto);
            devicePartMapper.updateById(cached);
            return cached;
        }

        LambdaQueryWrapper<DevicePart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DevicePart::getPartCode, dto.getPartCode())
                .eq(DevicePart::getDeviceId, device.getDeviceId());
        DevicePart part = devicePartMapper.selectOne(queryWrapper);

        if (part == null) {
            part = DevicePart.builder()
                    .partCode(dto.getPartCode())
                    .partName(dto.getPartName())
                    .deviceId(device.getDeviceId())
                    .partType(dto.getPartType())
                    .monitorEnabled(parseStatus(dto.getMonitorEnabled(), 1))
                    .partStatus(parseStatus(dto.getPartStatus(), 1))
                    .build();
            devicePartMapper.insert(part);
        } else {
            updatePartFromDto(part, dto);
            devicePartMapper.updateById(part);
        }

        cache.put(cacheKey, part);
        return part;
    }

    private void updatePartFromDto(DevicePart part, EquipmentImportDTO dto) {
        if (StringUtils.isNotBlank(dto.getPartName())) {
            part.setPartName(dto.getPartName());
        }
        part.setPartType(dto.getPartType());
        if (dto.getMonitorEnabled() != null) {
            part.setMonitorEnabled(parseStatus(dto.getMonitorEnabled(), part.getMonitorEnabled()));
        }
        if (dto.getPartStatus() != null) {
            part.setPartStatus(parseStatus(dto.getPartStatus(), part.getPartStatus()));
        }
    }

    private Integer parseStatus(String value, Integer defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        if ("启用".equals(value) || "正常".equals(value)) {
            return 1;
        }
        if ("停用".equals(value) || "禁用".equals(value)) {
            return 0;
        }
        if ("维修".equals(value)) {
            return 2;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer parseInteger(String value, Integer defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            String trimmed = value.trim();
            if (trimmed.length() == 10) {
                return LocalDateTime.parse(trimmed + " 00:00:00", DATETIME_FORMATTER);
            } else if (trimmed.length() == 19) {
                return LocalDateTime.parse(trimmed, DATETIME_FORMATTER);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<EquipmentExportVO> exportEquipment(List<Long> deviceIds) {
        List<EquipmentExportVO> exportList = new ArrayList<>();

        if (deviceIds == null || deviceIds.isEmpty()) {
            return exportList;
        }

        Set<Long> processedDevices = new HashSet<>();

        for (Long deviceId : deviceIds) {
            if (processedDevices.contains(deviceId)) {
                continue;
            }
            processedDevices.add(deviceId);

            FactoryDevice device = factoryDeviceMapper.selectById(deviceId);
            if (device == null) {
                continue;
            }

            FactoryArea area = null;
            FactoryInfo factory = null;

            if (device.getLocationId() != null) {
                area = factoryAreaMapper.selectById(device.getLocationId());
                if (area != null && area.getFactoryId() != null) {
                    factory = factoryInfoMapper.selectById(area.getFactoryId());
                }
            }

            LambdaQueryWrapper<DevicePart> partQueryWrapper = new LambdaQueryWrapper<>();
            partQueryWrapper.eq(DevicePart::getDeviceId, deviceId);
            List<DevicePart> parts = devicePartMapper.selectList(partQueryWrapper);

            if (parts.isEmpty()) {
                EquipmentExportVO exportVO = buildExportVO(factory, area, device, null);
                exportList.add(exportVO);
            } else {
                for (DevicePart part : parts) {
                    EquipmentExportVO exportVO = buildExportVO(factory, area, device, part);
                    exportList.add(exportVO);
                }
            }
        }

        return exportList;
    }

    private EquipmentExportVO buildExportVO(FactoryInfo factory, FactoryArea area, FactoryDevice device, DevicePart part) {
        EquipmentExportVO vo = new EquipmentExportVO();
        
        if (factory != null) {
            vo.setFactoryCode(factory.getFactoryCode());
            vo.setFactoryName(factory.getFactoryName());
            vo.setFactoryAddress(factory.getFactoryAddress());
            vo.setContactPerson(factory.getContactPerson());
            vo.setContactPhone(factory.getContactPhone());
            vo.setFactoryStatus(factory.getStatus());
        }
        
        if (area != null) {
            vo.setAreaCode(area.getLocationCode());
            vo.setAreaName(area.getAreaName());
            vo.setAreaType(area.getAreaType());
            vo.setAreaStatus(area.getAreaStatus());
            vo.setAreaOrder(area.getAreaOrder());
        }
        
        if (device != null) {
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());
            vo.setDeviceModel(device.getDeviceModel());
            vo.setDeviceSn(device.getDeviceSn());
            vo.setManufacturer(device.getManufacturer());
            vo.setDeviceStatus(device.getDeviceStatus());
            vo.setInstallTime(formatDateTime(device.getInstallTime()));
            vo.setStartUseTime(formatDateTime(device.getStartUseTime()));
            vo.setMaintainCycle(device.getMaintainCycle());
            vo.setLastMaintainTime(formatDateTime(device.getLastMaintainTime()));
            vo.setWarrantyPeriod(device.getWarrantyPeriod());
            vo.setDeviceNote(device.getDeviceNote());
            vo.setModelUrl(device.getModelUrl());
            vo.setImageUrl(device.getImageUrl());
        }
        
        if (part != null) {
            vo.setPartCode(part.getPartCode());
            vo.setPartName(part.getPartName());
            vo.setPartType(part.getPartType());
            vo.setMonitorEnabled(part.getMonitorEnabled());
            vo.setPartStatus(part.getPartStatus());
        }
        
        return vo;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }
}
