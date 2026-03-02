package com.izpan.modules.detection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.detection.domain.bo.DeviceDetectionRecordBO;
import com.izpan.modules.detection.domain.entity.DeviceDetectionRecord;

public interface IDeviceDetectionRecordService extends IService<DeviceDetectionRecord> {

    IPage<DeviceDetectionRecord> listDeviceDetectionRecordPage(PageQuery pageQuery, DeviceDetectionRecordBO deviceDetectionRecordBO);
}
