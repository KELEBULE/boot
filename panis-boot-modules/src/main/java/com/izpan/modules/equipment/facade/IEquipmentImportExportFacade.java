package com.izpan.modules.equipment.facade;

import com.izpan.modules.equipment.domain.dto.EquipmentImportDTO;
import com.izpan.modules.equipment.domain.dto.EquipmentImportResultDTO;
import com.izpan.modules.equipment.domain.vo.EquipmentExportVO;

import java.util.List;

public interface IEquipmentImportExportFacade {

    EquipmentImportResultDTO importEquipment(List<EquipmentImportDTO> importList);

    List<EquipmentExportVO> exportEquipment(List<Long> deviceIds);
}
