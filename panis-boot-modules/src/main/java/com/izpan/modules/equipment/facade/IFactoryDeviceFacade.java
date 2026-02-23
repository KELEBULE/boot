package com.izpan.modules.equipment.facade;

import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;

import java.util.List;

public interface IFactoryDeviceFacade {

    RPage<FactoryDeviceVO> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO);

    FactoryDeviceVO getFactoryDeviceById(Long id);

    List<DevicePartTreeVO> getDevicePartTreeByLocationId(Long locationId);

    boolean addFactoryDevice(FactoryDeviceAddDTO addDTO);

    boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO);

    boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO);

    boolean deleteFactoryDeviceByIds(List<Long> ids);
}
