package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDTO;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;

public interface IPartThresholdConfigService extends IService<PartThresholdConfig> {

    PartThresholdConfig getByPartId(Long partId);

    boolean saveOrUpdateConfig(PartThresholdConfigDTO dto);
}
