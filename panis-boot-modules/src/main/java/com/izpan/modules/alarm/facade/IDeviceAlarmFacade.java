package com.izpan.modules.alarm.facade;

import java.util.List;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmConfirmDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmCreateWorkOrderDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmSearchDTO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;

public interface IDeviceAlarmFacade {

    RPage<DeviceAlarmVO> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmSearchDTO deviceAlarmSearchDTO);

    DeviceAlarmVO get(Long id);

    boolean confirm(DeviceAlarmConfirmDTO deviceAlarmConfirmDTO);

    boolean createWorkOrder(DeviceAlarmCreateWorkOrderDTO deviceAlarmCreateWorkOrderDTO);

    List<DeviceAlarmExportVO> queryExportList(List<Long> ids);
}
