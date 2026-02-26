package com.izpan.modules.equipment.facade;

import com.izpan.modules.equipment.domain.dto.PartThresholdConfigAddDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDeleteDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigUpdateDTO;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;

import java.util.List;

public interface IPartThresholdConfigFacade {

    List<PartThresholdConfigVO> listByPartId(Long partId);

    PartThresholdConfigVO getById(Long id);

    boolean addConfig(PartThresholdConfigAddDTO addDTO);

    boolean updateConfig(PartThresholdConfigUpdateDTO updateDTO);

    boolean deleteConfig(PartThresholdConfigDeleteDTO deleteDTO);
}
