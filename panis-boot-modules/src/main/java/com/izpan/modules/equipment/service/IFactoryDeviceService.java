package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;

import java.util.List;

public interface IFactoryDeviceService extends IService<FactoryDevice> {

    IPage<FactoryDevice> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO);

    FactoryDevice getFactoryDeviceById(Long id);

    boolean addFactoryDevice(FactoryDeviceAddDTO addDTO);

    boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO);

    boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO);

    boolean deleteFactoryDeviceByIds(List<Long> ids);
}
