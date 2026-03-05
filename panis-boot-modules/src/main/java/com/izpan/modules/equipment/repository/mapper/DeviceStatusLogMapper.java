package com.izpan.modules.equipment.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.equipment.domain.entity.DeviceStatusLog;
import com.izpan.modules.equipment.domain.vo.DeviceStatusLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceStatusLogMapper extends BaseMapper<DeviceStatusLog> {

    List<DeviceStatusLogVO> selectDeviceStatusLogsByDeviceId(@Param("deviceId") Long deviceId);
}
