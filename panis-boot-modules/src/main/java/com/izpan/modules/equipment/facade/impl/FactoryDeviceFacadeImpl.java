package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceBatchStatusDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.DeviceDetailStatsVO;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import com.izpan.modules.equipment.domain.vo.DevicePartVO;
import com.izpan.modules.equipment.domain.vo.DeviceStatusOverviewVO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;
import com.izpan.modules.equipment.facade.IFactoryDeviceFacade;
import com.izpan.modules.equipment.service.IDevicePartService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
import com.izpan.modules.workorder.service.IWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactoryDeviceFacadeImpl implements IFactoryDeviceFacade {

    private final IFactoryDeviceService factoryDeviceService;
    private final IDevicePartService devicePartService;
    private final IDeviceAlarmService deviceAlarmService;
    private final IWorkOrderService workOrderService;

    @Override
    public RPage<FactoryDeviceVO> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO) {
        IPage<FactoryDevice> devicePage = factoryDeviceService.listFactoryDevicePage(pageQuery, searchDTO);
        
        List<FactoryDeviceVO> voList = devicePage.getRecords().stream().map(device -> {
            FactoryDeviceVO vo = BeanUtil.copyProperties(device, FactoryDeviceVO.class);
            vo.setUniqueKey("device_" + device.getDeviceId());
            
            List<DevicePart> parts = devicePartService.listDevicePartByDeviceId(device.getDeviceId());
            List<DevicePartVO> partVOList = parts.stream()
                    .map(part -> {
                        DevicePartVO partVO = BeanUtil.copyProperties(part, DevicePartVO.class);
                        partVO.setUniqueKey("part_" + part.getPartId());
                        return partVO;
                    })
                    .collect(Collectors.toList());
            vo.setChildren(partVOList);
            return vo;
        }).collect(Collectors.toList());
        
        return new RPage<>(devicePage.getCurrent(), devicePage.getSize(), voList, devicePage.getPages(), devicePage.getTotal());
    }

    @Override
    public FactoryDeviceVO getFactoryDeviceById(Long id) {
        FactoryDevice device = factoryDeviceService.getFactoryDeviceById(id);
        FactoryDeviceVO vo = BeanUtil.copyProperties(device, FactoryDeviceVO.class);
        vo.setUniqueKey("device_" + device.getDeviceId());
        
        List<DevicePart> parts = devicePartService.listDevicePartByDeviceId(id);
        List<DevicePartVO> partVOList = parts.stream()
                .map(part -> {
                    DevicePartVO partVO = BeanUtil.copyProperties(part, DevicePartVO.class);
                    partVO.setUniqueKey("part_" + part.getPartId());
                    return partVO;
                })
                .collect(Collectors.toList());
        vo.setChildren(partVOList);
        return vo;
    }

    @Override
    public List<DevicePartTreeVO> getDevicePartTreeByLocationId(Long locationId) {
        return factoryDeviceService.getDevicePartTreeByLocationId(locationId);
    }

    @Override
    @Transactional
    public boolean addFactoryDevice(FactoryDeviceAddDTO addDTO) {
        return factoryDeviceService.addFactoryDevice(addDTO);
    }

    @Override
    @Transactional
    public boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO) {
        return factoryDeviceService.updateFactoryDevice(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO) {
        return factoryDeviceService.deleteFactoryDevice(deleteDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryDeviceByIds(List<Long> ids) {
        return factoryDeviceService.deleteFactoryDeviceByIds(ids);
    }

    @Override
    public DeviceStatusOverviewVO getDeviceStatusOverview() {
        Map<Integer, Long> distribution = factoryDeviceService.getDeviceStatusDistribution();
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();
        
        List<DeviceStatusOverviewVO.StatusItem> items = distribution.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 
                            ? Math.round(entry.getValue() * 1000.0 / total) / 10.0 
                            : 0.0;
                    String statusName = switch (entry.getKey()) {
                        case 1 -> "正常";
                        case 2 -> "维修";
                        case 0 -> "停用";
                        default -> "未知";
                    };
                    return DeviceStatusOverviewVO.StatusItem.builder()
                            .status(entry.getKey())
                            .statusName(statusName)
                            .count(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return DeviceStatusOverviewVO.builder()
                .statusDistribution(items)
                .build();
    }

    @Override
    @Transactional
    public boolean batchScrapDevice(List<Long> ids) {
        return factoryDeviceService.batchScrapDevice(ids);
    }

    @Override
    @Transactional
    public boolean batchUpdateDeviceStatus(FactoryDeviceBatchStatusDTO batchStatusDTO) {
        return factoryDeviceService.batchUpdateDeviceStatus(batchStatusDTO);
    }

    @Override
    public DeviceDetailStatsVO getDeviceDetailStats(Long deviceId) {
        FactoryDevice device = factoryDeviceService.getFactoryDeviceById(deviceId);
        if (device == null) {
            return null;
        }
        
        long alarmCount = deviceAlarmService.lambdaQuery()
                .eq(com.izpan.modules.alarm.domain.entity.DeviceAlarm::getDeviceId, deviceId)
                .eq(com.izpan.modules.alarm.domain.entity.DeviceAlarm::getClearStatus, 0)
                .count();
        
        long workOrderCount = workOrderService.lambdaQuery()
                .eq(com.izpan.modules.workorder.domain.entity.WorkOrder::getDeviceId, deviceId)
                .in(com.izpan.modules.workorder.domain.entity.WorkOrder::getOrderStatus, 0, 1, 2)
                .count();
        
        return DeviceDetailStatsVO.builder()
                .deviceId(device.getDeviceId())
                .deviceCode(device.getDeviceCode())
                .deviceName(device.getDeviceName())
                .deviceStatus(device.getDeviceStatus())
                .imageUrl(device.getImageUrl())
                .alarmCount(alarmCount)
                .workOrderCount(workOrderCount)
                .totalWorkHours(device.getTotalWorkHours())
                .build();
    }
}
