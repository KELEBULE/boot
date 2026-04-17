package com.izpan.modules.equipment.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.domain.LoginUser;
import com.izpan.common.exception.BizException;
import com.izpan.modules.equipment.domain.dto.DeviceScrapDTO;
import com.izpan.modules.equipment.domain.dto.DeviceStatusChangeDTO;
import com.izpan.modules.equipment.domain.entity.DeviceStatusLog;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.DeviceStatusLogVO;
import com.izpan.modules.equipment.repository.mapper.DeviceStatusLogMapper;
import com.izpan.modules.equipment.service.IDeviceStatusLogService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import com.izpan.modules.workorder.service.IWorkOrderService;
import com.izpan.starter.oss.manage.OssManager;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceStatusLogServiceImpl extends ServiceImpl<DeviceStatusLogMapper, DeviceStatusLog> implements IDeviceStatusLogService {

    private final IFactoryDeviceService factoryDeviceService;
    private final OssManager ossManager;

    private IWorkOrderService workOrderService;

    @Autowired
    @Lazy
    public void setWorkOrderService(IWorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @Override
    public List<DeviceStatusLogVO> getDeviceStatusLogsByDeviceId(Long deviceId) {
        List<DeviceStatusLogVO> logs = baseMapper.selectDeviceStatusLogsByDeviceId(deviceId);
        for (DeviceStatusLogVO log : logs) {
            log.setFromStatusName(getStatusName(log.getFromStatus()));
            log.setToStatusName(getStatusName(log.getToStatus()));
            if (StringUtils.isNotBlank(log.getImageUrls())) {
                List<String> urls = Arrays.stream(log.getImageUrls().split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(url -> ossManager.service().preview(url.trim()))
                        .collect(Collectors.toList());
                log.setImageUrls(String.join(",", urls));
            }
        }
        return logs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeDeviceStatus(DeviceStatusChangeDTO statusChangeDTO) {
        List<Long> deviceIds = statusChangeDTO.getDeviceIds();
        if (deviceIds == null || deviceIds.isEmpty()) {
            return false;
        }

        List<String> devicesWithWorkOrder = new ArrayList<>();
        for (Long deviceId : deviceIds) {
            if (workOrderService.hasUnfinishedWorkOrder(deviceId)) {
                FactoryDevice device = factoryDeviceService.getById(deviceId);
                if (device != null) {
                    devicesWithWorkOrder.add(device.getDeviceName());
                }
            }
        }

        if (!devicesWithWorkOrder.isEmpty()) {
            throw new BizException("以下设备存在未完成的工单，无法切换状态：" + String.join("、", devicesWithWorkOrder));
        }

        Long currentUserId = getCurrentUserId();
        String currentUserName = getCurrentUserName();
        LocalDateTime now = LocalDateTime.now();
        String imageUrls = statusChangeDTO.getImageUrls() != null && !statusChangeDTO.getImageUrls().isEmpty()
                ? String.join(",", statusChangeDTO.getImageUrls())
                : null;

        for (Long deviceId : deviceIds) {
            FactoryDevice device = factoryDeviceService.getById(deviceId);
            if (device == null) {
                continue;
            }

            Integer fromStatus = device.getDeviceStatus();
            Integer toStatus = statusChangeDTO.getTargetStatus();

            LambdaUpdateWrapper<FactoryDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FactoryDevice::getDeviceId, deviceId);

            if (fromStatus == 1 && toStatus != 1) {
                if (device.getLastOnlineTime() != null) {
                    long hoursWorked = ChronoUnit.HOURS.between(device.getLastOnlineTime(), now);
                    int currentTotalHours = device.getTotalWorkHours() != null ? device.getTotalWorkHours() : 0;
                    updateWrapper.set(FactoryDevice::getTotalWorkHours, currentTotalHours + (int) hoursWorked);
                }
                updateWrapper.set(FactoryDevice::getLastOnlineTime, (LocalDateTime) null);
            }

            if (toStatus == 1) {
                updateWrapper.set(FactoryDevice::getLastOnlineTime, now);
            }

            if (toStatus == 0) {
                updateWrapper.set(FactoryDevice::getScrapStatus, 1);
                updateWrapper.set(FactoryDevice::getScrapTime, now);
            } else {
                updateWrapper.set(FactoryDevice::getScrapStatus, 0);
            }

            updateWrapper.set(FactoryDevice::getDeviceStatus, toStatus);
            factoryDeviceService.update(updateWrapper);

            DeviceStatusLog statusLog = DeviceStatusLog.builder()
                    .deviceId(deviceId)
                    .deviceCode(device.getDeviceCode())
                    .deviceName(device.getDeviceName())
                    .fromStatus(fromStatus)
                    .toStatus(toStatus)
                    .changeReason(statusChangeDTO.getChangeReason())
                    .imageUrls(imageUrls)
                    .relatedOrderId(statusChangeDTO.getRelatedOrderId())
                    .relatedOrderCode(statusChangeDTO.getRelatedOrderCode())
                    .operatorId(currentUserId)
                    .operatorName(currentUserName)
                    .createTime(now)
                    .build();
            save(statusLog);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean scrapDevices(DeviceScrapDTO scrapDTO) {
        List<Long> deviceIds = scrapDTO.getDeviceIds();
        if (deviceIds == null || deviceIds.isEmpty()) {
            return false;
        }

        List<String> devicesWithWorkOrder = new ArrayList<>();
        for (Long deviceId : deviceIds) {
            if (workOrderService.hasUnfinishedWorkOrder(deviceId)) {
                FactoryDevice device = factoryDeviceService.getById(deviceId);
                if (device != null) {
                    devicesWithWorkOrder.add(device.getDeviceName());
                }
            }
        }

        if (!devicesWithWorkOrder.isEmpty()) {
            throw new BizException("以下设备存在未完成的工单，无法报废：" + String.join("、", devicesWithWorkOrder));
        }

        Long currentUserId = getCurrentUserId();
        String currentUserName = getCurrentUserName();
        LocalDateTime now = LocalDateTime.now();
        String imageUrls = scrapDTO.getImageUrls() != null && !scrapDTO.getImageUrls().isEmpty()
                ? String.join(",", scrapDTO.getImageUrls())
                : null;

        for (Long deviceId : deviceIds) {
            FactoryDevice device = factoryDeviceService.getById(deviceId);
            if (device == null) {
                continue;
            }

            Integer fromStatus = device.getDeviceStatus();

            LambdaUpdateWrapper<FactoryDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FactoryDevice::getDeviceId, deviceId);

            if (fromStatus == 1 && device.getLastOnlineTime() != null) {
                long hoursWorked = ChronoUnit.HOURS.between(device.getLastOnlineTime(), now);
                int currentTotalHours = device.getTotalWorkHours() != null ? device.getTotalWorkHours() : 0;
                updateWrapper.set(FactoryDevice::getTotalWorkHours, currentTotalHours + (int) hoursWorked);
            }

            updateWrapper.set(FactoryDevice::getDeviceStatus, 0)
                    .set(FactoryDevice::getScrapStatus, 1)
                    .set(FactoryDevice::getScrapTime, now)
                    .set(FactoryDevice::getLastOnlineTime, (LocalDateTime) null);
            factoryDeviceService.update(updateWrapper);

            DeviceStatusLog statusLog = DeviceStatusLog.builder()
                    .deviceId(deviceId)
                    .deviceCode(device.getDeviceCode())
                    .deviceName(device.getDeviceName())
                    .fromStatus(fromStatus)
                    .toStatus(0)
                    .changeReason(scrapDTO.getChangeReason())
                    .imageUrls(imageUrls)
                    .operatorId(currentUserId)
                    .operatorName(currentUserName)
                    .createTime(now)
                    .build();
            save(statusLog);
        }

        return true;
    }

    @Override
    public boolean saveStatusLog(DeviceStatusLog statusLog) {
        return save(statusLog);
    }

    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return 1L;
        }
    }

    private String getCurrentUserName() {
        try {
            LoginUser loginUser = (LoginUser) StpUtil.getSession().get("user");
            if (loginUser != null && StringUtils.isNotBlank(loginUser.getRealName())) {
                return loginUser.getRealName();
            }
            return "系统";
        } catch (Exception e) {
            return "系统";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "-";
        }
        switch (status) {
            case 0:
                return "停用";
            case 1:
                return "正常";
            case 2:
                return "维修";
            case 3:
                return "故障";
            default:
                return "未知";
        }
    }
}
