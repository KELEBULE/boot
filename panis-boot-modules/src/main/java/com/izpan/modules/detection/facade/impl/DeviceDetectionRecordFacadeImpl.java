package com.izpan.modules.detection.facade.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
import com.izpan.modules.detection.domain.bo.DeviceDetectionRecordBO;
import com.izpan.modules.detection.domain.dto.devicedetection.DeviceDetectionRecordSearchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataBatchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataDTO;
import com.izpan.modules.detection.domain.entity.DeviceDetectionRecord;
import com.izpan.modules.detection.domain.vo.DeviceDetectionRecordVO;
import com.izpan.modules.detection.facade.IDeviceDetectionRecordFacade;
import com.izpan.modules.detection.service.IDeviceDetectionRecordService;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;
import com.izpan.modules.equipment.service.IDevicePartService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import com.izpan.modules.equipment.service.IPartThresholdConfigService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDetectionRecordFacadeImpl implements IDeviceDetectionRecordFacade {

    private final IDeviceDetectionRecordService deviceDetectionRecordService;
    private final IFactoryDeviceService factoryDeviceService;
    private final IDevicePartService devicePartService;
    private final IPartThresholdConfigService partThresholdConfigService;
    private final IDeviceAlarmService deviceAlarmService;

    @Override
    public RPage<DeviceDetectionRecordVO> listDeviceDetectionRecordPage(PageQuery pageQuery, DeviceDetectionRecordSearchDTO deviceDetectionRecordSearchDTO) {
        DeviceDetectionRecordBO deviceDetectionRecordBO = CglibUtil.convertObj(deviceDetectionRecordSearchDTO, DeviceDetectionRecordBO::new);
        IPage<DeviceDetectionRecord> deviceDetectionRecordIPage = deviceDetectionRecordService.listDeviceDetectionRecordPage(pageQuery, deviceDetectionRecordBO);

        List<Long> deviceIds = deviceDetectionRecordIPage.getRecords().stream()
                .map(DeviceDetectionRecord::getDeviceId)
                .distinct()
                .toList();
        List<Long> partIds = deviceDetectionRecordIPage.getRecords().stream()
                .map(DeviceDetectionRecord::getPartId)
                .distinct()
                .toList();

        Map<Long, FactoryDevice> deviceMap = deviceIds.isEmpty() ? Map.of()
                : factoryDeviceService.listByIds(deviceIds).stream()
                        .collect(Collectors.toMap(FactoryDevice::getDeviceId, d -> d));
        Map<Long, DevicePart> partMap = partIds.isEmpty() ? Map.of()
                : devicePartService.listByIds(partIds).stream()
                        .collect(Collectors.toMap(DevicePart::getPartId, p -> p));

        List<DeviceDetectionRecordVO> voList = deviceDetectionRecordIPage.getRecords().stream()
                .map(record -> convertToVO(record, deviceMap, partMap))
                .collect(Collectors.toList());

        return new RPage<>(deviceDetectionRecordIPage.getCurrent(), deviceDetectionRecordIPage.getSize(), voList,
                deviceDetectionRecordIPage.getPages(), deviceDetectionRecordIPage.getTotal());
    }

    @Override
    public DeviceDetectionRecordVO get(Long id) {
        DeviceDetectionRecord deviceDetectionRecord = deviceDetectionRecordService.getById(id);
        if (deviceDetectionRecord == null) {
            return null;
        }

        Map<Long, FactoryDevice> deviceMap = Map.of();
        Map<Long, DevicePart> partMap = Map.of();

        if (deviceDetectionRecord.getDeviceId() != null) {
            FactoryDevice device = factoryDeviceService.getById(deviceDetectionRecord.getDeviceId());
            if (device != null) {
                deviceMap = Map.of(device.getDeviceId(), device);
            }
        }

        if (deviceDetectionRecord.getPartId() != null) {
            DevicePart part = devicePartService.getById(deviceDetectionRecord.getPartId());
            if (part != null) {
                partMap = Map.of(part.getPartId(), part);
            }
        }

        return convertToVO(deviceDetectionRecord, deviceMap, partMap);
    }

    private DeviceDetectionRecordVO convertToVO(DeviceDetectionRecord deviceDetectionRecord,
            Map<Long, FactoryDevice> deviceMap,
            Map<Long, DevicePart> partMap) {
        DeviceDetectionRecordVO vo = CglibUtil.convertObj(deviceDetectionRecord, DeviceDetectionRecordVO::new);

        FactoryDevice device = deviceMap.get(deviceDetectionRecord.getDeviceId());
        if (device != null) {
            vo.setDeviceName(device.getDeviceName());
            vo.setDeviceCode(device.getDeviceCode());
        }

        DevicePart part = partMap.get(deviceDetectionRecord.getPartId());
        if (part != null) {
            vo.setPartName(part.getPartName());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveDetectionData(DetectionDataDTO dto) {
        PartThresholdConfig threshold = partThresholdConfigService.getByPartId(dto.getPartId());

        int detectStatus = 0;
        int alarmLevel = 0;

        if (threshold != null) {
            int[] statusAndLevel = determineDetectStatusAndLevel(dto.getDetectValue(), threshold);
            detectStatus = statusAndLevel[0];
            alarmLevel = statusAndLevel[1];
        }

        DeviceDetectionRecord record = DeviceDetectionRecord.builder()
                .deviceId(dto.getDeviceId())
                .partId(dto.getPartId())
                .thresholdId(threshold != null ? threshold.getId() : null)
                .detectValue(dto.getDetectValue())
                .detectStatus(detectStatus)
                .level1Value(threshold != null ? threshold.getLevel1Value() : null)
                .level2Value(threshold != null ? threshold.getLevel2Value() : null)
                .level3Value(threshold != null ? threshold.getLevel3Value() : null)
                .isFalseAlarm(0)
                .detectTime(dto.getDetectTime() != null ? dto.getDetectTime() : LocalDateTime.now())
                .sensorCode(dto.getSensorCode())
                .dataSource(dto.getDataSource() != null ? dto.getDataSource() : "simulator")
                .build();

        deviceDetectionRecordService.save(record);

        if (detectStatus == 2 && alarmLevel > 0) {
            DeviceAlarm alarm = createAlarm(dto.getDeviceId(), dto.getPartId(), dto.getDetectValue(),
                    threshold, alarmLevel, dto.getDetectTime());
            deviceAlarmService.save(alarm);

            record.setAlarmId(alarm.getAlarmId());
            deviceDetectionRecordService.updateById(record);

            log.info("检测到报警: 设备ID={}, 部件ID={}, 报警级别={}, 检测值={}",
                    dto.getDeviceId(), dto.getPartId(), alarmLevel, dto.getDetectValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveDetectionDataBatch(DetectionDataBatchDTO batchDTO) {
        if (batchDTO == null || batchDTO.getDataList() == null || batchDTO.getDataList().isEmpty()) {
            return;
        }

        for (DetectionDataDTO dto : batchDTO.getDataList()) {
            receiveDetectionData(dto);
        }

        log.info("批量接收检测数据完成, 共{}条", batchDTO.getDataList().size());
    }

    private int[] determineDetectStatusAndLevel(BigDecimal detectValue, PartThresholdConfig threshold) {
        if (threshold == null) {
            return new int[]{0, 0};
        }

        String level1Op = threshold.getLevel1Operator();
        BigDecimal level1Val = threshold.getLevel1Value();
        String level2Op = threshold.getLevel2Operator();
        BigDecimal level2Val = threshold.getLevel2Value();
        String level3Op = threshold.getLevel3Operator();
        BigDecimal level3Val = threshold.getLevel3Value();

        if (level1Val != null && level1Op != null) {
            if (checkThreshold(detectValue, level1Op, level1Val)) {
                return new int[]{2, 1};
            }
        }

        if (level2Val != null && level2Op != null) {
            if (checkThreshold(detectValue, level2Op, level2Val)) {
                return new int[]{2, 2};
            }
        }

        if (level3Val != null && level3Op != null) {
            if (checkThreshold(detectValue, level3Op, level3Val)) {
                return new int[]{2, 3};
            }
        }

        return new int[]{0, 0};
    }

    private boolean checkThreshold(BigDecimal value, String operator, BigDecimal thresholdValue) {
        int comparison = value.compareTo(thresholdValue);
        return switch (operator) {
            case ">=" ->
                comparison >= 0;
            case ">" ->
                comparison > 0;
            case "<=" ->
                comparison <= 0;
            case "<" ->
                comparison < 0;
            case "==" ->
                comparison == 0;
            default ->
                false;
        };
    }

    private DeviceAlarm createAlarm(Long deviceId, Long partId, BigDecimal detectValue,
            PartThresholdConfig threshold, int alarmLevel, LocalDateTime alarmTime) {
        BigDecimal thresholdValue = null;
        if (threshold != null) {
            thresholdValue = switch (alarmLevel) {
                case 1 ->
                    threshold.getLevel1Value();
                case 2 ->
                    threshold.getLevel2Value();
                case 3 ->
                    threshold.getLevel3Value();
                default ->
                    null;
            };
        }

        String alarmCode = generateAlarmCode();

        return DeviceAlarm.builder()
                .alarmCode(alarmCode)
                .deviceId(deviceId)
                .partId(partId)
                .alarmType(1)
                .alarmLevel(alarmLevel)
                .alarmTime(alarmTime != null ? alarmTime : LocalDateTime.now())
                .currentValue(detectValue)
                .thresholdValue(thresholdValue)
                .confirmStatus(0)
                .clearStatus(0)
                .build();
    }

    private String generateAlarmCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        DeviceAlarm lastAlarm = deviceAlarmService.lambdaQuery()
                .select(DeviceAlarm::getAlarmId)
                .orderByDesc(DeviceAlarm::getAlarmId)
                .last("LIMIT 1")
                .one();
        long nextId = (lastAlarm != null && lastAlarm.getAlarmId() != null) ? lastAlarm.getAlarmId() + 1 : 1;
        return String.format("ALM%s%06d", timestamp, nextId);
    }
}
