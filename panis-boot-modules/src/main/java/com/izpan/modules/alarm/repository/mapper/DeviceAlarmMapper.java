package com.izpan.modules.alarm.repository.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;

@Mapper
public interface DeviceAlarmMapper extends BaseMapper<DeviceAlarm> {

    @Select("SELECT da.device_id as deviceId, fd.device_name as deviceName, " +
            "SUM(CASE WHEN da.alarm_level = 1 THEN 1 ELSE 0 END) as level1Count, " +
            "SUM(CASE WHEN da.alarm_level = 2 THEN 1 ELSE 0 END) as level2Count, " +
            "SUM(CASE WHEN da.alarm_level = 3 THEN 1 ELSE 0 END) as level3Count, " +
            "COUNT(*) as totalCount " +
            "FROM device_alarm da " +
            "LEFT JOIN factory_device fd ON da.device_id = fd.device_id " +
            "GROUP BY da.device_id, fd.device_name " +
            "ORDER BY totalCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectDeviceAlarmTop(int limit);

    @Select("SELECT dp.part_id as partId, dp.part_name as partName, dp.part_code as partCode, " +
            "COUNT(*) as alarmCount " +
            "FROM device_alarm da " +
            "LEFT JOIN device_part dp ON da.part_id = dp.part_id " +
            "WHERE da.device_id = #{deviceId} " +
            "AND da.alarm_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY dp.part_id, dp.part_name, dp.part_code " +
            "ORDER BY alarmCount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectFrequentAlarmParts(@Param("deviceId") Long deviceId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT HOUR(da.alarm_time) as hour, COUNT(*) as alarmCount " +
            "FROM device_alarm da " +
            "WHERE da.device_id = #{deviceId} " +
            "AND da.alarm_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY HOUR(da.alarm_time) " +
            "ORDER BY hour")
    List<Map<String, Object>> selectFrequentAlarmTime(@Param("deviceId") Long deviceId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT ddr.detect_time as detectTime, ddr.detect_value as detectValue, " +
            "ddr.level1_value as level1Value, ddr.level2_value as level2Value, ddr.level3_value as level3Value " +
            "FROM device_detection_record ddr " +
            "WHERE ddr.device_id = #{deviceId} " +
            "AND ddr.detect_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY ddr.detect_time")
    List<Map<String, Object>> selectTemperatureTrend(@Param("deviceId") Long deviceId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT DATE(da.alarm_time) as date, COUNT(*) as alarmCount " +
            "FROM device_alarm da " +
            "WHERE da.device_id = #{deviceId} " +
            "AND da.alarm_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY DATE(da.alarm_time) " +
            "ORDER BY date")
    List<Map<String, Object>> selectDailyAlarmTrend(@Param("deviceId") Long deviceId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT ddr.detect_time as detectTime, ddr.detect_value as detectValue, " +
            "ddr.level1_value as level1Value, ddr.level2_value as level2Value, ddr.level3_value as level3Value " +
            "FROM device_detection_record ddr " +
            "WHERE ddr.part_id = #{partId} " +
            "AND ddr.detect_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY ddr.detect_time")
    List<Map<String, Object>> selectPartTemperatureTrend(@Param("partId") Long partId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT ddr.detect_time as detectTime, ddr.detect_value as detectValue " +
            "FROM device_detection_record ddr " +
            "INNER JOIN device_alarm da ON ddr.alarm_id = da.alarm_id " +
            "WHERE da.part_id = #{partId} " +
            "AND ddr.detect_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY ddr.detect_time")
    List<Map<String, Object>> selectPartAlarmTemperatureTrend(@Param("partId") Long partId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT HOUR(da.alarm_time) as hour, COUNT(*) as alarmCount " +
            "FROM device_alarm da " +
            "WHERE da.part_id = #{partId} " +
            "AND da.alarm_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY HOUR(da.alarm_time) " +
            "ORDER BY hour")
    List<Map<String, Object>> selectPartHourlyAlarmDistribution(@Param("partId") Long partId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("SELECT da.alarm_level as alarmLevel, COUNT(*) as alarmCount " +
            "FROM device_alarm da " +
            "WHERE da.part_id = #{partId} " +
            "AND da.alarm_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY da.alarm_level " +
            "ORDER BY da.alarm_level")
    List<Map<String, Object>> selectPartAlarmLevelDistribution(@Param("partId") Long partId, 
            @Param("startTime") String startTime, @Param("endTime") String endTime);
}
