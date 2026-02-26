package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigAddDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDeleteDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigUpdateDTO;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;

import java.util.List;

public interface IPartThresholdConfigService extends IService<PartThresholdConfig> {

    List<PartThresholdConfig> listByPartId(Long partId);

    PartThresholdConfig getById(Long id);

    boolean addConfig(PartThresholdConfigAddDTO addDTO);

    boolean updateConfig(PartThresholdConfigUpdateDTO updateDTO);

    boolean deleteConfig(PartThresholdConfigDeleteDTO deleteDTO);
}
