package com.izpan.modules.alarm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.util.CglibUtil;
import com.izpan.modules.alarm.domain.entity.DeviceAlarmStatusLog;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmStatusLogVO;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmStatusLogMapper;
import com.izpan.modules.alarm.service.IDeviceAlarmStatusLogService;
import com.izpan.modules.system.domain.entity.SysUser;
import com.izpan.modules.system.service.ISysUserService;

import lombok.RequiredArgsConstructor;

@Service
public class DeviceAlarmStatusLogServiceImpl extends ServiceImpl<DeviceAlarmStatusLogMapper, DeviceAlarmStatusLog> implements IDeviceAlarmStatusLogService {

    private final ISysUserService sysUserService;

    public DeviceAlarmStatusLogServiceImpl(@Lazy ISysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public List<DeviceAlarmStatusLogVO> listByAlarmId(Long alarmId) {
        LambdaQueryWrapper<DeviceAlarmStatusLog> queryWrapper = new LambdaQueryWrapper<DeviceAlarmStatusLog>()
                .eq(DeviceAlarmStatusLog::getAlarmId, alarmId)
                .orderByDesc(DeviceAlarmStatusLog::getOperateTime);
        
        List<DeviceAlarmStatusLog> logs = this.list(queryWrapper);
        
        if (logs.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = logs.stream()
                .map(DeviceAlarmStatusLog::getOperateUserId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                : sysUserService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));

        return logs.stream()
                .map(log -> {
                    DeviceAlarmStatusLogVO vo = CglibUtil.convertObj(log, DeviceAlarmStatusLogVO::new);
                    if (log.getOperateUserId() != null) {
                        SysUser user = userMap.get(log.getOperateUserId());
                        if (user != null && log.getOperateUserName() == null) {
                            vo.setOperateUserName(user.getRealName() != null ? user.getRealName() : user.getUserName());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void saveLog(Long alarmId, String alarmCode, Integer beforeStatus, Integer afterStatus,
                        Integer operateType, Long operateUserId, String operateUserName,
                        Integer operateSource, String operateRemark) {
        DeviceAlarmStatusLog log = DeviceAlarmStatusLog.builder()
                .alarmId(alarmId)
                .alarmCode(alarmCode)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .operateType(operateType)
                .operateUserId(operateUserId)
                .operateUserName(operateUserName)
                .operateTime(LocalDateTime.now())
                .operateSource(operateSource != null ? operateSource : 1)
                .operateRemark(operateRemark)
                .createTime(LocalDateTime.now())
                .build();
        this.save(log);
    }
}
