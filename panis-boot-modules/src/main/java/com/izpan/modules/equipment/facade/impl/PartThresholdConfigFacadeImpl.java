package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigAddDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDeleteDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigUpdateDTO;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;
import com.izpan.modules.equipment.facade.IPartThresholdConfigFacade;
import com.izpan.modules.equipment.service.IPartThresholdConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartThresholdConfigFacadeImpl implements IPartThresholdConfigFacade {

    private final IPartThresholdConfigService partThresholdConfigService;

    @Override
    public List<PartThresholdConfigVO> listByPartId(Long partId) {
        List<PartThresholdConfig> configs = partThresholdConfigService.listByPartId(partId);
        return configs.stream()
                .map(config -> BeanUtil.copyProperties(config, PartThresholdConfigVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PartThresholdConfigVO getById(Long id) {
        PartThresholdConfig config = partThresholdConfigService.getById(id);
        return BeanUtil.copyProperties(config, PartThresholdConfigVO.class);
    }

    @Override
    @Transactional
    public boolean addConfig(PartThresholdConfigAddDTO addDTO) {
        return partThresholdConfigService.addConfig(addDTO);
    }

    @Override
    @Transactional
    public boolean updateConfig(PartThresholdConfigUpdateDTO updateDTO) {
        return partThresholdConfigService.updateConfig(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteConfig(PartThresholdConfigDeleteDTO deleteDTO) {
        return partThresholdConfigService.deleteConfig(deleteDTO);
    }
}
