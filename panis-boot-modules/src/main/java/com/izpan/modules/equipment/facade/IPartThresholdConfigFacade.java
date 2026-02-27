package com.izpan.modules.equipment.facade;

import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDTO;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;

public interface IPartThresholdConfigFacade {

    PartThresholdConfigVO getByPartId(Long partId);

    boolean saveOrUpdateConfig(PartThresholdConfigDTO dto);
}
