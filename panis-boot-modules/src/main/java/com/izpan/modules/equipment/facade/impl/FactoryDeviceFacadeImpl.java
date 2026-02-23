package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import com.izpan.modules.equipment.domain.vo.DevicePartVO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;
import com.izpan.modules.equipment.facade.IFactoryDeviceFacade;
import com.izpan.modules.equipment.service.IDevicePartService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactoryDeviceFacadeImpl implements IFactoryDeviceFacade {

    private final IFactoryDeviceService factoryDeviceService;
    private final IDevicePartService devicePartService;

    @Override
    public RPage<FactoryDeviceVO> listFactoryDevicePage(PageQuery pageQuery, FactoryDeviceSearchDTO searchDTO) {
        IPage<FactoryDevice> devicePage = factoryDeviceService.listFactoryDevicePage(pageQuery, searchDTO);
        
        List<FactoryDeviceVO> voList = devicePage.getRecords().stream().map(device -> {
            FactoryDeviceVO vo = BeanUtil.copyProperties(device, FactoryDeviceVO.class);
            vo.setUniqueKey("device_" + device.getDeviceId());
            
            List<DevicePart> parts = devicePartService.listDevicePartByDeviceId(device.getDeviceId());
            List<DevicePartVO> partVOList = parts.stream()
                    .map(part -> {
                        DevicePartVO partVO = BeanUtil.copyProperties(part, DevicePartVO.class);
                        partVO.setUniqueKey("part_" + part.getPartId());
                        return partVO;
                    })
                    .collect(Collectors.toList());
            vo.setChildren(partVOList);
            return vo;
        }).collect(Collectors.toList());
        
        return new RPage<>(devicePage.getCurrent(), devicePage.getSize(), voList, devicePage.getPages(), devicePage.getTotal());
    }

    @Override
    public FactoryDeviceVO getFactoryDeviceById(Long id) {
        FactoryDevice device = factoryDeviceService.getFactoryDeviceById(id);
        FactoryDeviceVO vo = BeanUtil.copyProperties(device, FactoryDeviceVO.class);
        vo.setUniqueKey("device_" + device.getDeviceId());
        
        List<DevicePart> parts = devicePartService.listDevicePartByDeviceId(id);
        List<DevicePartVO> partVOList = parts.stream()
                .map(part -> {
                    DevicePartVO partVO = BeanUtil.copyProperties(part, DevicePartVO.class);
                    partVO.setUniqueKey("part_" + part.getPartId());
                    return partVO;
                })
                .collect(Collectors.toList());
        vo.setChildren(partVOList);
        return vo;
    }

    @Override
    public List<DevicePartTreeVO> getDevicePartTreeByLocationId(Long locationId) {
        return factoryDeviceService.getDevicePartTreeByLocationId(locationId);
    }

    @Override
    @Transactional
    public boolean addFactoryDevice(FactoryDeviceAddDTO addDTO) {
        return factoryDeviceService.addFactoryDevice(addDTO);
    }

    @Override
    @Transactional
    public boolean updateFactoryDevice(FactoryDeviceUpdateDTO updateDTO) {
        return factoryDeviceService.updateFactoryDevice(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryDevice(FactoryDeviceDeleteDTO deleteDTO) {
        return factoryDeviceService.deleteFactoryDevice(deleteDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryDeviceByIds(List<Long> ids) {
        return factoryDeviceService.deleteFactoryDeviceByIds(ids);
    }
}
