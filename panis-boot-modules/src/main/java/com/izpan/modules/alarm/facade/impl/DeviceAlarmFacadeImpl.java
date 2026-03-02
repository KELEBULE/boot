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
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;
import com.izpan.modules.alarm.facade.IDeviceAlarmFacade;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
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

    public DeviceAlarmFacadeImpl(
            IDeviceAlarmService deviceAlarmService,
            IFactoryDeviceService factoryDeviceService,
            IDevicePartService devicePartService,
            @Lazy ISysUserService sysUserService,
            IWorkOrderService workOrderService,
            IDeviceDetectionRecordService deviceDetectionRecordService) {
        this.deviceAlarmService = deviceAlarmService;
        this.factoryDeviceService = factoryDeviceService;
        this.devicePartService = devicePartService;
        this.sysUserService = sysUserService;
        this.workOrderService = workOrderService;
        this.deviceDetectionRecordService = deviceDetectionRecordService;
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
        DeviceAlarm deviceAlarm = deviceAlarmService.getById(deviceAlarmConfirmDTO.getAlarmId());
        if (deviceAlarm == null) {
            return false;
        }

        Integer isFalseAlarm = deviceAlarmConfirmDTO.getIsFalseAlarm() != null
                ? deviceAlarmConfirmDTO.getIsFalseAlarm() : 0;

        LambdaUpdateWrapper<DeviceAlarm> updateWrapper = new LambdaUpdateWrapper<DeviceAlarm>()
                .eq(DeviceAlarm::getAlarmId, deviceAlarmConfirmDTO.getAlarmId())
                .set(DeviceAlarm::getConfirmUserId, currentUserId)
                .set(DeviceAlarm::getConfirmTime, LocalDateTime.now())
                .set(DeviceAlarm::getConfirmStatus, 1)
                .set(DeviceAlarm::getIsFalseAlarm, isFalseAlarm);

        boolean updated = deviceAlarmService.update(updateWrapper);

        if (updated && isFalseAlarm == 1) {
            LambdaUpdateWrapper<DeviceDetectionRecord> recordUpdateWrapper = new LambdaUpdateWrapper<DeviceDetectionRecord>()
                    .eq(DeviceDetectionRecord::getAlarmId, deviceAlarmConfirmDTO.getAlarmId())
                    .set(DeviceDetectionRecord::getIsFalseAlarm, 1);
            deviceDetectionRecordService.update(recordUpdateWrapper);
        }

        return updated;
    }

    @Override
    @Transactional
    public boolean createWorkOrder(DeviceAlarmCreateWorkOrderDTO deviceAlarmCreateWorkOrderDTO) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
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
            LambdaUpdateWrapper<DeviceAlarm> updateWrapper = new LambdaUpdateWrapper<DeviceAlarm>()
                    .eq(DeviceAlarm::getAlarmId, deviceAlarmCreateWorkOrderDTO.getAlarmId())
                    .set(DeviceAlarm::getHandleUserId, currentUserId)
                    .set(DeviceAlarm::getHandleTime, LocalDateTime.now())
                    .set(DeviceAlarm::getWorkOrderId, workOrder.getOrderId())
                    .set(DeviceAlarm::getIsFalseAlarm, 0);
            deviceAlarmService.update(updateWrapper);
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
}
