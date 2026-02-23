package com.izpan.modules.equipment.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FactoryDeviceMapper extends BaseMapper<FactoryDevice> {
    
    List<DevicePartTreeVO> selectDevicePartTreeByLocationId(@Param("locationId") Long locationId);
}
