package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.repository.mapper.FactoryInfoMapper;
import com.izpan.modules.equipment.service.IFactoryInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactoryInfoServiceImpl extends ServiceImpl<FactoryInfoMapper, FactoryInfo> implements IFactoryInfoService {

    @Override
    public IPage<FactoryInfo> listFactoryInfoPage(PageQuery pageQuery, FactoryInfoSearchDTO searchDTO) {
        LambdaQueryWrapper<FactoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getFactoryCode()), FactoryInfo::getFactoryCode, searchDTO.getFactoryCode())
                .like(StringUtils.isNotBlank(searchDTO.getFactoryName()), FactoryInfo::getFactoryName, searchDTO.getFactoryName())
                .eq(searchDTO.getStatus() != null, FactoryInfo::getStatus, searchDTO.getStatus())
                .orderByDesc(FactoryInfo::getCreateTime);

        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public FactoryInfo getFactoryInfoById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<FactoryInfo> listAllFactoryInfo() {
        LambdaQueryWrapper<FactoryInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FactoryInfo::getStatus, 1);
        return list(queryWrapper);
    }

    @Override
    public boolean addFactoryInfo(FactoryInfoAddDTO addDTO) {
        FactoryInfo factoryInfo = FactoryInfo.builder()
                .factoryCode(addDTO.getFactoryCode())
                .factoryName(addDTO.getFactoryName())
                .factoryAddress(addDTO.getFactoryAddress())
                .contactPerson(addDTO.getContactPerson())
                .contactPhone(addDTO.getContactPhone())
                .status(addDTO.getStatus())
                .build();
        return save(factoryInfo);
    }

    @Override
    public boolean updateFactoryInfo(FactoryInfoUpdateDTO updateDTO) {
        FactoryInfo factoryInfo = FactoryInfo.builder()
                .factoryId(updateDTO.getFactoryId())
                .factoryCode(updateDTO.getFactoryCode())
                .factoryName(updateDTO.getFactoryName())
                .factoryAddress(updateDTO.getFactoryAddress())
                .contactPerson(updateDTO.getContactPerson())
                .contactPhone(updateDTO.getContactPhone())
                .status(updateDTO.getStatus())
                .build();
        return updateById(factoryInfo);
    }

    @Override
    public boolean deleteFactoryInfo(FactoryInfoDeleteDTO deleteDTO) {
        return removeByIds(deleteDTO.getIds());
    }

    @Override
    public boolean deleteFactoryInfoByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
