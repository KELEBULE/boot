package com.izpan.modules.alarm.facade;

import java.util.List;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmConfirmDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmCreateWorkOrderDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmSearchDTO;
import com.izpan.modules.alarm.domain.vo.AlarmLevelDistributionVO;
import com.izpan.modules.alarm.domain.vo.DailyAlarmTrendVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmLevelStatsVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmTopVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmPartVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmTimeVO;
import com.izpan.modules.alarm.domain.vo.TemperatureTrendVO;

public interface IDeviceAlarmFacade {

    RPage<DeviceAlarmVO> listDeviceAlarmPage(PageQuery pageQuery, DeviceAlarmSearchDTO deviceAlarmSearchDTO);

    DeviceAlarmVO get(Long id);

    boolean confirm(DeviceAlarmConfirmDTO deviceAlarmConfirmDTO);

    boolean createWorkOrder(DeviceAlarmCreateWorkOrderDTO deviceAlarmCreateWorkOrderDTO);

    List<DeviceAlarmExportVO> queryExportList(List<Long> ids);

    AlarmLevelDistributionVO getAlarmLevelDistribution();

    DeviceAlarmTopVO getDeviceAlarmTop(int limit);

    FrequentAlarmPartVO getFrequentAlarmParts(Long deviceId, String startTime, String endTime);

    FrequentAlarmTimeVO getFrequentAlarmTime(Long deviceId, String startTime, String endTime);

    DeviceAlarmLevelStatsVO getAlarmLevelStats(Long deviceId, String startTime, String endTime);

    TemperatureTrendVO getTemperatureTrend(Long deviceId, String startTime, String endTime);

    DailyAlarmTrendVO getDailyAlarmTrend(Long deviceId, String startTime, String endTime);

    TemperatureTrendVO getPartTemperatureTrend(Long partId, String startTime, String endTime);

    TemperatureTrendVO getPartAlarmTemperatureTrend(Long partId, String startTime, String endTime);

    FrequentAlarmTimeVO getPartHourlyAlarmDistribution(Long partId, String startTime, String endTime);

    DeviceAlarmLevelStatsVO getPartAlarmLevelDistribution(Long partId, String startTime, String endTime);
}
