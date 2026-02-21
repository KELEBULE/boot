package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryAreaAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaUpdateDTO;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.domain.vo.FactoryAreaVO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;
import com.izpan.modules.equipment.facade.IFactoryAreaFacade;
import com.izpan.modules.equipment.service.IFactoryAreaService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactoryAreaFacadeImpl implements IFactoryAreaFacade {

    private final IFactoryAreaService factoryAreaService;
    private final IFactoryDeviceService factoryDeviceService;

    @Override
    public RPage<FactoryAreaVO> listFactoryAreaPage(PageQuery pageQuery, FactoryAreaSearchDTO searchDTO) {
        IPage<FactoryArea> areaPage = factoryAreaService.listFactoryAreaPage(pageQuery, searchDTO);
        
        List<FactoryAreaVO> voList = areaPage.getRecords().stream().map(area -> {
            FactoryAreaVO vo = BeanUtil.copyProperties(area, FactoryAreaVO.class);
            vo.setUniqueKey("area_" + area.getAreaId());
            
            LambdaQueryWrapper<FactoryDevice> deviceWrapper = new LambdaQueryWrapper<>();
            deviceWrapper.eq(FactoryDevice::getLocationId, area.getAreaId());
            List<FactoryDevice> devices = factoryDeviceService.list(deviceWrapper);
            
            List<FactoryDeviceVO> deviceVOList = devices.stream()
                    .map(device -> {
                        FactoryDeviceVO deviceVO = BeanUtil.copyProperties(device, FactoryDeviceVO.class);
                        deviceVO.setUniqueKey("device_" + device.getDeviceId());
                        return deviceVO;
                    })
                    .collect(Collectors.toList());
            vo.setChildren(deviceVOList);
            return vo;
        }).collect(Collectors.toList());
        
        return new RPage<>(areaPage.getCurrent(), areaPage.getSize(), voList, areaPage.getPages(), areaPage.getTotal());
    }

    @Override
    public FactoryAreaVO getFactoryAreaById(Long id) {
        FactoryArea area = factoryAreaService.getFactoryAreaById(id);
        FactoryAreaVO vo = BeanUtil.copyProperties(area, FactoryAreaVO.class);
        vo.setUniqueKey("area_" + area.getAreaId());
        return vo;
    }

    @Override
    public List<FactoryAreaVO> listAllFactoryArea() {
        List<FactoryArea> areaList = factoryAreaService.listAllFactoryArea();
        return areaList.stream()
                .map(area -> {
                    FactoryAreaVO vo = BeanUtil.copyProperties(area, FactoryAreaVO.class);
                    vo.setUniqueKey("area_" + area.getAreaId());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean addFactoryArea(FactoryAreaAddDTO addDTO) {
        return factoryAreaService.addFactoryArea(addDTO);
    }

    @Override
    @Transactional
    public boolean updateFactoryArea(FactoryAreaUpdateDTO updateDTO) {
        return factoryAreaService.updateFactoryArea(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryArea(FactoryAreaDeleteDTO deleteDTO) {
        return factoryAreaService.deleteFactoryArea(deleteDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryAreaByIds(List<Long> ids) {
        return factoryAreaService.deleteFactoryAreaByIds(ids);
    }
}
