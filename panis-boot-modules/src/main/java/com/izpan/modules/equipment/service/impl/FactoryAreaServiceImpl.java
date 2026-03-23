package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.dto.FactoryAreaAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaUpdateDTO;
import com.izpan.modules.equipment.repository.mapper.FactoryAreaMapper;
import com.izpan.modules.equipment.service.IFactoryAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactoryAreaServiceImpl extends ServiceImpl<FactoryAreaMapper, FactoryArea> implements IFactoryAreaService {

    @Override
    public IPage<FactoryArea> listFactoryAreaPage(PageQuery pageQuery, FactoryAreaSearchDTO searchDTO) {
        LambdaQueryWrapper<FactoryArea> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getLocationCode()), FactoryArea::getLocationCode, searchDTO.getLocationCode())
                .like(StringUtils.isNotBlank(searchDTO.getAreaName()), FactoryArea::getAreaName, searchDTO.getAreaName())
                .eq(searchDTO.getFactoryId() != null, FactoryArea::getFactoryId, searchDTO.getFactoryId())
                .eq(searchDTO.getAreaStatus() != null, FactoryArea::getAreaStatus, searchDTO.getAreaStatus())
                .orderByAsc(FactoryArea::getAreaOrder)
                .orderByDesc(FactoryArea::getCreateTime);

        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public FactoryArea getFactoryAreaById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<FactoryArea> listAllFactoryArea() {
        LambdaQueryWrapper<FactoryArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryArea::getAreaStatus, 1);
        return list(queryWrapper);
    }

    @Override
    public boolean addFactoryArea(FactoryAreaAddDTO addDTO) {
        FactoryArea area = FactoryArea.builder()
                .locationCode(addDTO.getLocationCode())
                .areaName(addDTO.getAreaName())
                .factoryId(addDTO.getFactoryId())
                .parentId(addDTO.getParentId())
                .areaType(addDTO.getAreaType())
                .areaStatus(addDTO.getAreaStatus())
                .areaOrder(addDTO.getAreaOrder())
                .build();
        return save(area);
    }

    @Override
    public boolean updateFactoryArea(FactoryAreaUpdateDTO updateDTO) {
        FactoryArea area = FactoryArea.builder()
                .areaId(updateDTO.getAreaId())
                .locationCode(updateDTO.getLocationCode())
                .areaName(updateDTO.getAreaName())
                .factoryId(updateDTO.getFactoryId())
                .parentId(updateDTO.getParentId())
                .areaType(updateDTO.getAreaType())
                .areaStatus(updateDTO.getAreaStatus())
                .areaOrder(updateDTO.getAreaOrder())
                .build();
        return updateById(area);
    }

    @Override
    public boolean deleteFactoryArea(FactoryAreaDeleteDTO deleteDTO) {
        return removeByIds(deleteDTO.getIds());
    }

    @Override
    public boolean deleteFactoryAreaByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
