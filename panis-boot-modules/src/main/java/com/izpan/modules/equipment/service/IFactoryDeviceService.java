package com.izpan.modules.equipment.service;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceBatchStatusDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;

import java.util.List;

public interface IFactoryDeviceService extends IService<FactoryDevice> {

    IPage<FactoryDevice> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO);

    FactoryDevice getFactoryDeviceById(Long id);

    List<DevicePartTreeVO> getDevicePartTreeByLocationId(Long locationId);

    boolean addFactoryDevice(FactoryDeviceAddDTO addDTO);

    boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO);

    boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO);

    boolean deleteFactoryDeviceByIds(List<Long> ids);

    Map<Integer, Long> getDeviceStatusDistribution();

    boolean batchScrapDevice(List<Long> ids);

    boolean batchUpdateDeviceStatus(FactoryDeviceBatchStatusDTO batchStatusDTO);
}
