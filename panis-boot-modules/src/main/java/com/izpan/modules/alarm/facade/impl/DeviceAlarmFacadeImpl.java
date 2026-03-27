package com.izpan.modules.alarm.facade.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.bo.DeviceAlarmBO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmConfirmDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmCreateWorkOrderDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmSearchDTO;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.domain.vo.AlarmLevelDistributionVO;
import com.izpan.modules.alarm.domain.vo.DailyAlarmTrendVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmLevelStatsVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmTopVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmPartVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmTimeVO;
import com.izpan.modules.alarm.domain.vo.TemperatureTrendVO;
import com.izpan.modules.alarm.facade.IDeviceAlarmFacade;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
import com.izpan.modules.alarm.service.IDeviceAlarmStatusLogService;
import com.izpan.modules.detection.domain.entity.DeviceDetectionRecord;
import com.izpan.modules.detection.service.IDeviceDetectionRecordService;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.service.IDevicePartService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import com.izpan.modules.system.domain.entity.SysUser;
import com.izpan.modules.system.service.ISysUserService;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.service.IWorkOrderService;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeviceAlarmFacadeImpl implements IDeviceAlarmFacade {

    private final IDeviceAlarmService deviceAlarmService;
    private final IFactoryDeviceService factoryDeviceService;
    private final IDevicePartService devicePartService;
    private final ISysUserService sysUserService;
    private final IWorkOrderService workOrderService;
    private final IDeviceDetectionRecordService deviceDetectionRecordService;
    private final IDeviceAlarmStatusLogService deviceAlarmStatusLogService;

    public DeviceAlarmFacadeImpl(
            IDeviceAlarmService deviceAlarmService,
            IFactoryDeviceService factoryDeviceService,
            IDevicePartService devicePartService,
            @Lazy ISysUserService sysUserService,
            IWorkOrderService workOrderService,
            IDeviceDetectionRecordService deviceDetectionRecordService,
            IDeviceAlarmStatusLogService deviceAlarmStatusLogService) {
        this.deviceAlarmService = deviceAlarmService;
        this.factoryDeviceService = factoryDeviceService;
        this.devicePartService = devicePartService;
        this.sysUserService = sysUserService;
        this.workOrderService = workOrderService;
        this.deviceDetectionRecordService = deviceDetectionRecordService;
        this.deviceAlarmStatusLogService = deviceAlarmStatusLogService;
    }

    @Override
    public RPage<DeviceAlarmVO> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmSearchDTO deviceAlarmSearchDTO) {
        DeviceAlarmBO deviceAlarmBO = CglibUtil.convertObj(deviceAlarmSearchDTO, DeviceAlarmBO::new);
        IPage<DeviceAlarm> deviceAlarmIPage = deviceAlarmService.listDeviceAlarmPage(pageQuery, deviceAlarmBO);

        List<Long> deviceIds = deviceAlarmIPage.getRecords().stream()
                .map(DeviceAlarm::getDeviceId)
                .distinct()
                .toList();
        List<Long> partIds = deviceAlarmIPage.getRecords().stream()
                .map(DeviceAlarm::getPartId)
                .distinct()
                .toList();
        List<Long> userIds = deviceAlarmIPage.getRecords().stream()
                .flatMap(a -> java.util.stream.Stream.of(
                a.getConfirmUserId(), a.getHandleUserId(), a.getClearUserId()))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        List<Long> workOrderIds = deviceAlarmIPage.getRecords().stream()
                .map(DeviceAlarm::getWorkOrderId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, FactoryDevice> deviceMap = deviceIds.isEmpty() ? Map.of()
                : factoryDeviceService.listByIds(deviceIds).stream()
                        .collect(Collectors.toMap(FactoryDevice::getDeviceId, d -> d));
        Map<Long, DevicePart> partMap = partIds.isEmpty() ? Map.of()
                : devicePartService.listByIds(partIds).stream()
                        .collect(Collectors.toMap(DevicePart::getPartId, p -> p));
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                : sysUserService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, WorkOrder> workOrderMap = workOrderIds.isEmpty() ? Map.of()
                : workOrderService.listByIds(workOrderIds).stream()
                        .collect(Collectors.toMap(WorkOrder::getOrderId, w -> w));

        List<DeviceAlarmVO> voList = deviceAlarmIPage.getRecords().stream()
                .map(alarm -> convertToVO(alarm, deviceMap, partMap, userMap, workOrderMap))
                .collect(Collectors.toList());

        return new RPage<>(deviceAlarmIPage.getCurrent(), deviceAlarmIPage.getSize(), voList,
                deviceAlarmIPage.getPages(), deviceAlarmIPage.getTotal());
    }

    @Override
    public DeviceAlarmVO get(Long id) {
        DeviceAlarm deviceAlarm = deviceAlarmService.getById(id);
        if (deviceAlarm == null) {
            return null;
        }

        Map<Long, WorkOrder> workOrderMap = Map.of();
        if (deviceAlarm.getWorkOrderId() != null) {
            WorkOrder workOrder = workOrderService.getById(deviceAlarm.getWorkOrderId());
            if (workOrder != null) {
                workOrderMap = Map.of(workOrder.getOrderId(), workOrder);
            }
        }

        return convertToVO(deviceAlarm, Map.of(), Map.of(), Map.of(), workOrderMap);
    }

    @Override
    @Transactional
    public boolean confirm(DeviceAlarmConfirmDTO deviceAlarmConfirmDTO) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        SysUser currentUser = sysUserService.getById(currentUserId);
        String currentUserName = currentUser != null 
                ? (currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUserName()) 
                : null;
        
        DeviceAlarm deviceAlarm = deviceAlarmService.getById(deviceAlarmConfirmDTO.getAlarmId());
        if (deviceAlarm == null) {
            return false;
        }

        Integer isFalseAlarm = deviceAlarmConfirmDTO.getIsFalseAlarm() != null
                ? deviceAlarmConfirmDTO.getIsFalseAlarm() : 0;

        Integer beforeStatus = deviceAlarm.getConfirmStatus() != null ? deviceAlarm.getConfirmStatus() : 0;
        Integer afterStatus = 1;
        Integer operateType = isFalseAlarm == 1 ? 4 : 1;

        LambdaUpdateWrapper<DeviceAlarm> updateWrapper = new LambdaUpdateWrapper<DeviceAlarm>()
                .eq(DeviceAlarm::getAlarmId, deviceAlarmConfirmDTO.getAlarmId())
                .set(DeviceAlarm::getConfirmUserId, currentUserId)
                .set(DeviceAlarm::getConfirmTime, LocalDateTime.now())
                .set(DeviceAlarm::getConfirmStatus, 1)
                .set(DeviceAlarm::getIsFalseAlarm, isFalseAlarm);

        boolean updated = deviceAlarmService.update(updateWrapper);

        if (updated) {
            deviceAlarmStatusLogService.saveLog(
                    deviceAlarm.getAlarmId(),
                    deviceAlarm.getAlarmCode(),
                    beforeStatus,
                    afterStatus,
                    operateType,
                    currentUserId,
                    currentUserName,
                    1,
                    isFalseAlarm == 1 ? "标记为误报" : "确认报警"
            );
            
            if (isFalseAlarm == 1) {
                LambdaUpdateWrapper<DeviceDetectionRecord> recordUpdateWrapper = new LambdaUpdateWrapper<DeviceDetectionRecord>()
                        .eq(DeviceDetectionRecord::getAlarmId, deviceAlarmConfirmDTO.getAlarmId())
                        .set(DeviceDetectionRecord::getIsFalseAlarm, 1);
                deviceDetectionRecordService.update(recordUpdateWrapper);
            }
        }

        return updated;
    }

    @Override
    @Transactional
    public boolean createWorkOrder(DeviceAlarmCreateWorkOrderDTO deviceAlarmCreateWorkOrderDTO) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        SysUser currentUser = sysUserService.getById(currentUserId);
        String currentUserName = currentUser != null 
                ? (currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUserName()) 
                : null;
        
        DeviceAlarm deviceAlarm = deviceAlarmService.getById(deviceAlarmCreateWorkOrderDTO.getAlarmId());
        if (deviceAlarm == null) {
            return false;
        }

        String orderCode = "WO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", IdUtil.getSnowflakeNextId() % 10000);

        String faultTimeStr = deviceAlarmCreateWorkOrderDTO.getFaultTime() != null
                ? deviceAlarmCreateWorkOrderDTO.getFaultTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : (deviceAlarm.getAlarmTime() != null
                ? deviceAlarm.getAlarmTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null);

        WorkOrder workOrder = WorkOrder.builder()
                .orderCode(orderCode)
                .deviceId(deviceAlarm.getDeviceId())
                .orderType(deviceAlarmCreateWorkOrderDTO.getOrderType() != null
                        ? deviceAlarmCreateWorkOrderDTO.getOrderType() : 1)
                .orderSource(2)
                .faultTime(faultTimeStr)
                .faultDescription(deviceAlarmCreateWorkOrderDTO.getFaultDescription())
                .repairRequirement(deviceAlarmCreateWorkOrderDTO.getRepairRequirement())
                .priority(deviceAlarmCreateWorkOrderDTO.getPriority() != null
                        ? deviceAlarmCreateWorkOrderDTO.getPriority() : 2)
                .assigneeId(deviceAlarmCreateWorkOrderDTO.getAssigneeId())
                .orderStatus(0)
                .creatorId(currentUserId)
                .build();

        boolean saved = workOrderService.save(workOrder);

        if (saved) {
            if (deviceAlarm.getDeviceId() != null) {
                updateDeviceStatusToMaintenance(deviceAlarm.getDeviceId());
            }
            
            LambdaUpdateWrapper<DeviceAlarm> updateWrapper = new LambdaUpdateWrapper<DeviceAlarm>()
                    .eq(DeviceAlarm::getAlarmId, deviceAlarmCreateWorkOrderDTO.getAlarmId())
                    .set(DeviceAlarm::getHandleUserId, currentUserId)
                    .set(DeviceAlarm::getHandleTime, LocalDateTime.now())
                    .set(DeviceAlarm::getWorkOrderId, workOrder.getOrderId())
                    .set(DeviceAlarm::getIsFalseAlarm, 0);
            deviceAlarmService.update(updateWrapper);
            
            deviceAlarmStatusLogService.saveLog(
                    deviceAlarm.getAlarmId(),
                    deviceAlarm.getAlarmCode(),
                    1,
                    2,
                    2,
                    currentUserId,
                    currentUserName,
                    1,
                    "创建工单：" + orderCode
            );
        }

        return saved;
    }

    @Override
    public List<DeviceAlarmExportVO> queryExportList(List<Long> ids) {
        List<DeviceAlarm> alarmList = deviceAlarmService.listByIds(ids);
        if (alarmList.isEmpty()) {
            return List.of();
        }

        List<Long> deviceIds = alarmList.stream().map(DeviceAlarm::getDeviceId).distinct().toList();
        List<Long> partIds = alarmList.stream().map(DeviceAlarm::getPartId).distinct().toList();
        List<Long> userIds = alarmList.stream()
                .flatMap(a -> java.util.stream.Stream.of(
                a.getConfirmUserId(), a.getHandleUserId(), a.getClearUserId()))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        List<Long> workOrderIds = alarmList.stream()
                .map(DeviceAlarm::getWorkOrderId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, FactoryDevice> deviceMap = deviceIds.isEmpty() ? Map.of()
                : factoryDeviceService.listByIds(deviceIds).stream()
                        .collect(Collectors.toMap(FactoryDevice::getDeviceId, d -> d));
        Map<Long, DevicePart> partMap = partIds.isEmpty() ? Map.of()
                : devicePartService.listByIds(partIds).stream()
                        .collect(Collectors.toMap(DevicePart::getPartId, p -> p));
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                : sysUserService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, WorkOrder> workOrderMap = workOrderIds.isEmpty() ? Map.of()
                : workOrderService.listByIds(workOrderIds).stream()
                        .collect(Collectors.toMap(WorkOrder::getOrderId, w -> w));

        return alarmList.stream()
                .map(alarm -> convertToExportVO(alarm, deviceMap, partMap, userMap, workOrderMap))
                .collect(Collectors.toList());
    }

    private DeviceAlarmVO convertToVO(DeviceAlarm deviceAlarm,
            Map<Long, FactoryDevice> deviceMap,
            Map<Long, DevicePart> partMap,
            Map<Long, SysUser> userMap,
            Map<Long, WorkOrder> workOrderMap) {
        DeviceAlarmVO vo = CglibUtil.convertObj(deviceAlarm, DeviceAlarmVO::new);

        FactoryDevice device = deviceMap.get(deviceAlarm.getDeviceId());
        if (device != null) {
            vo.setDeviceName(device.getDeviceName());
            vo.setDeviceCode(device.getDeviceCode());
        }

        DevicePart part = partMap.get(deviceAlarm.getPartId());
        if (part != null) {
            vo.setPartName(part.getPartName());
        }

        SysUser confirmUser = deviceAlarm.getConfirmUserId() != null ? userMap.get(deviceAlarm.getConfirmUserId()) : null;
        if (confirmUser != null) {
            vo.setConfirmUserName(confirmUser.getRealName() != null
                    ? confirmUser.getRealName() : confirmUser.getUserName());
        }

        SysUser handleUser = deviceAlarm.getHandleUserId() != null ? userMap.get(deviceAlarm.getHandleUserId()) : null;
        if (handleUser != null) {
            vo.setHandleUserName(handleUser.getRealName() != null
                    ? handleUser.getRealName() : handleUser.getUserName());
        }

        SysUser clearUser = deviceAlarm.getClearUserId() != null ? userMap.get(deviceAlarm.getClearUserId()) : null;
        if (clearUser != null) {
            vo.setClearUserName(clearUser.getRealName() != null
                    ? clearUser.getRealName() : clearUser.getUserName());
        }

        WorkOrder workOrder = deviceAlarm.getWorkOrderId() != null ? workOrderMap.get(deviceAlarm.getWorkOrderId()) : null;
        if (workOrder != null) {
            vo.setWorkOrderCode(workOrder.getOrderCode());
            vo.setWorkOrderStatus(workOrder.getOrderStatus());
        }

        return vo;
    }

    private DeviceAlarmExportVO convertToExportVO(DeviceAlarm deviceAlarm,
            Map<Long, FactoryDevice> deviceMap,
            Map<Long, DevicePart> partMap,
            Map<Long, SysUser> userMap,
            Map<Long, WorkOrder> workOrderMap) {
        DeviceAlarmExportVO vo = CglibUtil.convertObj(deviceAlarm, DeviceAlarmExportVO::new);

        FactoryDevice device = deviceMap.get(deviceAlarm.getDeviceId());
        if (device != null) {
            vo.setDeviceName(device.getDeviceName());
        }

        DevicePart part = partMap.get(deviceAlarm.getPartId());
        if (part != null) {
            vo.setPartName(part.getPartName());
        }

        vo.setAlarmLevelText(switch (deviceAlarm.getAlarmLevel()) {
            case 1 ->
                "一级(严重)";
            case 2 ->
                "二级(中等)";
            case 3 ->
                "三级(轻微)";
            default ->
                "-";
        });

        vo.setConfirmStatusText(deviceAlarm.getConfirmStatus() == 1 ? "已确认" : "未确认");
        vo.setClearStatusText(deviceAlarm.getClearStatus() == 1 ? "已清除" : "未清除");
        vo.setIsFalseAlarmText(deviceAlarm.getIsFalseAlarm() != null && deviceAlarm.getIsFalseAlarm() == 1 ? "是" : "否");

        SysUser confirmUser = deviceAlarm.getConfirmUserId() != null ? userMap.get(deviceAlarm.getConfirmUserId()) : null;
        if (confirmUser != null) {
            vo.setConfirmUserName(confirmUser.getRealName() != null ? confirmUser.getRealName() : confirmUser.getUserName());
        }

        SysUser clearUser = deviceAlarm.getClearUserId() != null ? userMap.get(deviceAlarm.getClearUserId()) : null;
        if (clearUser != null) {
            vo.setClearUserName(clearUser.getRealName() != null ? clearUser.getRealName() : clearUser.getUserName());
        }

        WorkOrder workOrder = deviceAlarm.getWorkOrderId() != null ? workOrderMap.get(deviceAlarm.getWorkOrderId()) : null;
        if (workOrder != null) {
            vo.setWorkOrderCode(workOrder.getOrderCode());
        }

        if (deviceAlarm.getAlarmDuration() != null) {
            int duration = deviceAlarm.getAlarmDuration();
            int hours = duration / 3600;
            int minutes = (duration % 3600) / 60;
            int seconds = duration % 60;
            StringBuilder sb = new StringBuilder();
            if (hours > 0) {
                sb.append(hours).append("小时");
            }
            if (minutes > 0) {
                sb.append(minutes).append("分钟");
            }
            if (seconds > 0 || sb.length() == 0) {
                sb.append(seconds).append("秒");
            }
            vo.setAlarmDurationText(sb.toString());
        } else {
            vo.setAlarmDurationText("-");
        }

        return vo;
    }

    @Override
    public AlarmLevelDistributionVO getAlarmLevelDistribution() {
        Map<Integer, Long> distribution = deviceAlarmService.getAlarmLevelDistribution();
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();
        
        List<AlarmLevelDistributionVO.LevelItem> items = distribution.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 
                            ? Math.round(entry.getValue() * 1000.0 / total) / 10.0 
                            : 0.0;
                    String levelName = switch (entry.getKey()) {
                        case 1 -> "一级报警";
                        case 2 -> "二级报警";
                        case 3 -> "三级报警";
                        default -> "未知";
                    };
                    return AlarmLevelDistributionVO.LevelItem.builder()
                            .level(entry.getKey())
                            .levelName(levelName)
                            .count(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return AlarmLevelDistributionVO.builder()
                .levelDistribution(items)
                .build();
    }

    @Override
    public DeviceAlarmTopVO getDeviceAlarmTop(int limit) {
        List<Map<String, Object>> topDevices = deviceAlarmService.getDeviceAlarmTop(limit);
        
        List<DeviceAlarmTopVO.DeviceAlarmItem> items = topDevices.stream()
                .map(map -> DeviceAlarmTopVO.DeviceAlarmItem.builder()
                        .deviceId(((Number) map.get("deviceId")).longValue())
                        .deviceName((String) map.get("deviceName"))
                        .level1Count(((Number) map.get("level1Count")).longValue())
                        .level2Count(((Number) map.get("level2Count")).longValue())
                        .level3Count(((Number) map.get("level3Count")).longValue())
                        .totalCount(((Number) map.get("totalCount")).longValue())
                        .build())
                .collect(Collectors.toList());
        
        return DeviceAlarmTopVO.builder()
                .deviceAlarmList(items)
                .build();
    }

    @Override
    public FrequentAlarmPartVO getFrequentAlarmParts(Long deviceId, String startTime, String endTime) {
        List<Map<String, Object>> parts = deviceAlarmService.getFrequentAlarmParts(deviceId, startTime, endTime);
        
        if (parts.isEmpty()) {
            return FrequentAlarmPartVO.builder().partList(List.of()).build();
        }
        
        long total = parts.stream()
                .mapToLong(m -> ((Number) m.get("alarmCount")).longValue())
                .sum();
        
        List<FrequentAlarmPartVO.PartItem> items = parts.stream()
                .map(map -> {
                    long count = ((Number) map.get("alarmCount")).longValue();
                    double percentage = total > 0 ? Math.round(count * 1000.0 / total) / 10.0 : 0.0;
                    return FrequentAlarmPartVO.PartItem.builder()
                            .partId(((Number) map.get("partId")).longValue())
                            .partName((String) map.get("partName"))
                            .partCode((String) map.get("partCode"))
                            .alarmCount(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return FrequentAlarmPartVO.builder()
                .partList(items)
                .build();
    }

    @Override
    public FrequentAlarmTimeVO getFrequentAlarmTime(Long deviceId, String startTime, String endTime) {
        List<Map<String, Object>> hourlyData = deviceAlarmService.getFrequentAlarmTime(deviceId, startTime, endTime);
        
        List<FrequentAlarmTimeVO.HourlyItem> items = new java.util.ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            final int h = hour;
            Map<String, Object> hourData = hourlyData.stream()
                    .filter(m -> ((Number) m.get("hour")).intValue() == h)
                    .findFirst()
                    .orElse(null);
            
            items.add(FrequentAlarmTimeVO.HourlyItem.builder()
                    .hour(h)
                    .alarmCount(hourData != null ? ((Number) hourData.get("alarmCount")).longValue() : 0L)
                    .build());
        }
        
        return FrequentAlarmTimeVO.builder()
                .hourlyDistribution(items)
                .build();
    }

    @Override
    public DeviceAlarmLevelStatsVO getAlarmLevelStats(Long deviceId, String startTime, String endTime) {
        Map<Integer, Long> distribution = deviceAlarmService.getAlarmLevelStatsByDevice(deviceId, startTime, endTime);
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();
        
        List<DeviceAlarmLevelStatsVO.LevelStatsItem> items = distribution.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 
                            ? Math.round(entry.getValue() * 1000.0 / total) / 10.0 
                            : 0.0;
                    String levelName = switch (entry.getKey()) {
                        case 1 -> "一级报警";
                        case 2 -> "二级报警";
                        case 3 -> "三级报警";
                        default -> "未知";
                    };
                    return DeviceAlarmLevelStatsVO.LevelStatsItem.builder()
                            .level(entry.getKey())
                            .levelName(levelName)
                            .count(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return DeviceAlarmLevelStatsVO.builder()
                .levelStats(items)
                .build();
    }

    @Override
    public TemperatureTrendVO getTemperatureTrend(Long deviceId, String startTime, String endTime) {
        List<Map<String, Object>> trendData = deviceAlarmService.getTemperatureTrend(deviceId, startTime, endTime);
        
        List<TemperatureTrendVO.TrendItem> items = trendData.stream()
                .map(map -> TemperatureTrendVO.TrendItem.builder()
                        .detectTime(map.get("detectTime") != null ? map.get("detectTime").toString() : null)
                        .detectValue(map.get("detectValue") != null ? new java.math.BigDecimal(map.get("detectValue").toString()) : null)
                        .level1Value(map.get("level1Value") != null ? new java.math.BigDecimal(map.get("level1Value").toString()) : null)
                        .level2Value(map.get("level2Value") != null ? new java.math.BigDecimal(map.get("level2Value").toString()) : null)
                        .level3Value(map.get("level3Value") != null ? new java.math.BigDecimal(map.get("level3Value").toString()) : null)
                        .build())
                .collect(Collectors.toList());
        
        return TemperatureTrendVO.builder()
                .trendData(items)
                .build();
    }

    @Override
    public DailyAlarmTrendVO getDailyAlarmTrend(Long deviceId, String startTime, String endTime) {
        List<Map<String, Object>> dailyData = deviceAlarmService.getDailyAlarmTrend(deviceId, startTime, endTime);
        
        List<DailyAlarmTrendVO.DailyItem> items = dailyData.stream()
                .map(map -> DailyAlarmTrendVO.DailyItem.builder()
                        .date(map.get("date") != null ? map.get("date").toString() : null)
                        .alarmCount(((Number) map.get("alarmCount")).longValue())
                        .build())
                .collect(Collectors.toList());
        
        return DailyAlarmTrendVO.builder()
                .dailyData(items)
                .build();
    }

    @Override
    public TemperatureTrendVO getPartTemperatureTrend(Long partId, String startTime, String endTime) {
        List<Map<String, Object>> trendData = deviceAlarmService.getPartTemperatureTrend(partId, startTime, endTime);
        
        List<TemperatureTrendVO.TrendItem> items = trendData.stream()
                .map(map -> TemperatureTrendVO.TrendItem.builder()
                        .detectTime(map.get("detectTime") != null ? map.get("detectTime").toString() : null)
                        .detectValue(map.get("detectValue") != null ? new java.math.BigDecimal(map.get("detectValue").toString()) : null)
                        .level1Value(map.get("level1Value") != null ? new java.math.BigDecimal(map.get("level1Value").toString()) : null)
                        .level2Value(map.get("level2Value") != null ? new java.math.BigDecimal(map.get("level2Value").toString()) : null)
                        .level3Value(map.get("level3Value") != null ? new java.math.BigDecimal(map.get("level3Value").toString()) : null)
                        .build())
                .collect(Collectors.toList());
        
        return TemperatureTrendVO.builder()
                .trendData(items)
                .build();
    }

    @Override
    public TemperatureTrendVO getPartAlarmTemperatureTrend(Long partId, String startTime, String endTime) {
        List<Map<String, Object>> trendData = deviceAlarmService.getPartAlarmTemperatureTrend(partId, startTime, endTime);
        
        List<TemperatureTrendVO.TrendItem> items = trendData.stream()
                .map(map -> TemperatureTrendVO.TrendItem.builder()
                        .detectTime(map.get("detectTime") != null ? map.get("detectTime").toString() : null)
                        .detectValue(map.get("detectValue") != null ? new java.math.BigDecimal(map.get("detectValue").toString()) : null)
                        .build())
                .collect(Collectors.toList());
        
        return TemperatureTrendVO.builder()
                .trendData(items)
                .build();
    }

    @Override
    public FrequentAlarmTimeVO getPartHourlyAlarmDistribution(Long partId, String startTime, String endTime) {
        List<Map<String, Object>> hourlyData = deviceAlarmService.getPartHourlyAlarmDistribution(partId, startTime, endTime);
        
        List<FrequentAlarmTimeVO.HourlyItem> items = new java.util.ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            final int h = hour;
            Map<String, Object> hourData = hourlyData.stream()
                    .filter(m -> ((Number) m.get("hour")).intValue() == h)
                    .findFirst()
                    .orElse(null);
            
            items.add(FrequentAlarmTimeVO.HourlyItem.builder()
                    .hour(h)
                    .alarmCount(hourData != null ? ((Number) hourData.get("alarmCount")).longValue() : 0L)
                    .build());
        }
        
        return FrequentAlarmTimeVO.builder()
                .hourlyDistribution(items)
                .build();
    }

    @Override
    public DeviceAlarmLevelStatsVO getPartAlarmLevelDistribution(Long partId, String startTime, String endTime) {
        Map<Integer, Long> distribution = deviceAlarmService.getPartAlarmLevelStats(partId, startTime, endTime);
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();
        
        List<DeviceAlarmLevelStatsVO.LevelStatsItem> items = distribution.entrySet().stream()
                .map(entry -> {
                    double percentage = total > 0 
                            ? Math.round(entry.getValue() * 1000.0 / total) / 10.0 
                            : 0.0;
                    String levelName = switch (entry.getKey()) {
                        case 1 -> "一级报警";
                        case 2 -> "二级报警";
                        case 3 -> "三级报警";
                        default -> "未知";
                    };
                    return DeviceAlarmLevelStatsVO.LevelStatsItem.builder()
                            .level(entry.getKey())
                            .levelName(levelName)
                            .count(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return DeviceAlarmLevelStatsVO.builder()
                .levelStats(items)
                .build();
    }
    
    private void updateDeviceStatusToMaintenance(Long deviceId) {
        FactoryDevice device = factoryDeviceService.getById(deviceId);
        if (device != null && device.getDeviceStatus() != null && device.getDeviceStatus() == 1) {
            LambdaUpdateWrapper<FactoryDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FactoryDevice::getDeviceId, deviceId)
                    .set(FactoryDevice::getDeviceStatus, 2);
            factoryDeviceService.update(updateWrapper);
        }
    }
}
