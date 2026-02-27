package com.izpan.modules.alarm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmRuleMapper extends BaseMapper<AlarmRule> {

    @Select("SELECT type_id, type_code, type_name FROM device_type WHERE status = 1")
    List<DeviceTypeSimple> selectAllDeviceTypes();
}
