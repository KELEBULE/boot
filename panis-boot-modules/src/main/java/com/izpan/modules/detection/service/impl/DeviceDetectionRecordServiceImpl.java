package com.izpan.modules.detection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.detection.domain.bo.DeviceDetectionRecordBO;
import com.izpan.modules.detection.domain.entity.DeviceDetectionRecord;
import com.izpan.modules.detection.repository.mapper.DeviceDetectionRecordMapper;
import com.izpan.modules.detection.service.IDeviceDetectionRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeviceDetectionRecordServiceImpl extends ServiceImpl<DeviceDetectionRecordMapper, DeviceDetectionRecord> implements IDeviceDetectionRecordService {

    @Override
    public IPage<DeviceDetectionRecord> listDeviceDetectionRecordPage(PageQuery pageQuery, DeviceDetectionRecordBO deviceDetectionRecordBO) {
        LambdaQueryWrapper<DeviceDetectionRecord> queryWrapper = new LambdaQueryWrapper<DeviceDetectionRecord>()
                .eq(deviceDetectionRecordBO.getDeviceId() != null, DeviceDetectionRecord::getDeviceId, deviceDetectionRecordBO.getDeviceId())
                .eq(deviceDetectionRecordBO.getPartId() != null, DeviceDetectionRecord::getPartId, deviceDetectionRecordBO.getPartId())
                .eq(deviceDetectionRecordBO.getDetectStatus() != null, DeviceDetectionRecord::getDetectStatus, deviceDetectionRecordBO.getDetectStatus())
                .eq(deviceDetectionRecordBO.getIsFalseAlarm() != null, DeviceDetectionRecord::getIsFalseAlarm, deviceDetectionRecordBO.getIsFalseAlarm())
                .like(StringUtils.isNotBlank(deviceDetectionRecordBO.getSensorCode()), DeviceDetectionRecord::getSensorCode, deviceDetectionRecordBO.getSensorCode())
                .ge(deviceDetectionRecordBO.getDetectTimeStart() != null, DeviceDetectionRecord::getDetectTime, deviceDetectionRecordBO.getDetectTimeStart())
                .le(deviceDetectionRecordBO.getDetectTimeEnd() != null, DeviceDetectionRecord::getDetectTime, deviceDetectionRecordBO.getDetectTimeEnd())
                .orderByDesc(DeviceDetectionRecord::getDetectTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }
}
