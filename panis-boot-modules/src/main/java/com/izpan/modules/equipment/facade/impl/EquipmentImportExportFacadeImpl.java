package com.izpan.modules.equipment.facade.impl;

import com.izpan.modules.equipment.domain.dto.EquipmentImportDTO;
import com.izpan.modules.equipment.domain.dto.EquipmentImportResultDTO;
import com.izpan.modules.equipment.domain.vo.EquipmentExportVO;
import com.izpan.modules.equipment.facade.IEquipmentImportExportFacade;
import com.izpan.modules.equipment.service.IEquipmentImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentImportExportFacadeImpl implements IEquipmentImportExportFacade {

    private final IEquipmentImportExportService equipmentImportExportService;

    @Override
    public EquipmentImportResultDTO importEquipment(List<EquipmentImportDTO> importList) {
        return equipmentImportExportService.importEquipment(importList);
    }

    @Override
    public List<EquipmentExportVO> exportEquipment(List<Long> deviceIds) {
        return equipmentImportExportService.exportEquipment(deviceIds);
    }
}
