package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.exception.BizException;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigAddDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDeleteDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigUpdateDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.PartThresholdConfigMapper;
import com.izpan.modules.equipment.service.IPartThresholdConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartThresholdConfigServiceImpl extends ServiceImpl<PartThresholdConfigMapper, PartThresholdConfig> implements IPartThresholdConfigService {

    @Autowired
    private DevicePartMapper devicePartMapper;

    @Override
    public List<PartThresholdConfig> listByPartId(Long partId) {
        LambdaQueryWrapper<PartThresholdConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartThresholdConfig::getPartId, partId)
                .orderByDesc(PartThresholdConfig::getCreateTime);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public PartThresholdConfig getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean addConfig(PartThresholdConfigAddDTO addDTO) {
        DevicePart part = devicePartMapper.selectById(addDTO.getPartId());
        if (part == null) {
            throw new BizException("关联的部件不存在，部件ID: " + addDTO.getPartId());
        }

        LambdaQueryWrapper<PartThresholdConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartThresholdConfig::getPartId, addDTO.getPartId())
                .eq(PartThresholdConfig::getConfigName, addDTO.getConfigName());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new BizException("该部件已存在同名配置");
        }

        PartThresholdConfig config = PartThresholdConfig.builder()
                .partId(addDTO.getPartId())
                .configName(addDTO.getConfigName())
                .tempMin(addDTO.getTempMin())
                .tempMax(addDTO.getTempMax())
                .warningMin(addDTO.getWarningMin())
                .warningMax(addDTO.getWarningMax())
                .checkInterval(addDTO.getCheckInterval() != null ? addDTO.getCheckInterval() : 60)
                .configStatus(addDTO.getConfigStatus() != null ? addDTO.getConfigStatus() : 1)
                .build();
        return save(config);
    }

    @Override
    public boolean updateConfig(PartThresholdConfigUpdateDTO updateDTO) {
        DevicePart part = devicePartMapper.selectById(updateDTO.getPartId());
        if (part == null) {
            throw new BizException("关联的部件不存在，部件ID: " + updateDTO.getPartId());
        }

        LambdaQueryWrapper<PartThresholdConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartThresholdConfig::getPartId, updateDTO.getPartId())
                .eq(PartThresholdConfig::getConfigName, updateDTO.getConfigName())
                .ne(PartThresholdConfig::getId, updateDTO.getId());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new BizException("该部件已存在同名配置");
        }

        PartThresholdConfig config = PartThresholdConfig.builder()
                .id(updateDTO.getId())
                .partId(updateDTO.getPartId())
                .configName(updateDTO.getConfigName())
                .tempMin(updateDTO.getTempMin())
                .tempMax(updateDTO.getTempMax())
                .warningMin(updateDTO.getWarningMin())
                .warningMax(updateDTO.getWarningMax())
                .checkInterval(updateDTO.getCheckInterval())
                .configStatus(updateDTO.getConfigStatus())
                .build();
        return updateById(config);
    }

    @Override
    public boolean deleteConfig(PartThresholdConfigDeleteDTO deleteDTO) {
        return removeByIds(deleteDTO.getIds());
    }
}
