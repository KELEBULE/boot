package com.izpan.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.repository.mapper.PartThresholdConfigMapper;
import com.izpan.modules.equipment.service.IPartThresholdConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartThresholdConfigServiceImpl extends ServiceImpl<PartThresholdConfigMapper, PartThresholdConfig> implements IPartThresholdConfigService {

    @Autowired
    private DevicePartMapper devicePartMapper;

    @Override
    public PartThresholdConfig getByPartId(Long partId) {
        LambdaQueryWrapper<PartThresholdConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartThresholdConfig::getPartId, partId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateConfig(PartThresholdConfigDTO dto) {
        DevicePart part = devicePartMapper.selectById(dto.getPartId());
        if (part == null) {
            throw new RuntimeException("关联的部件不存在，部件ID: " + dto.getPartId());
        }

        PartThresholdConfig existingConfig = getByPartId(dto.getPartId());
        
        PartThresholdConfig config = PartThresholdConfig.builder()
                .partId(dto.getPartId())
                .level1Operator(dto.getLevel1Operator())
                .level1Value(dto.getLevel1Value())
                .level2Operator(dto.getLevel2Operator())
                .level2Value(dto.getLevel2Value())
                .level3Operator(dto.getLevel3Operator())
                .level3Value(dto.getLevel3Value())
                .checkInterval(dto.getCheckInterval() != null ? dto.getCheckInterval() : 60)
                .configStatus(dto.getConfigStatus() != null ? dto.getConfigStatus() : 1)
                .build();

        if (existingConfig != null) {
            config.setId(existingConfig.getId());
            return updateById(config);
        }
        return save(config);
    }
}
