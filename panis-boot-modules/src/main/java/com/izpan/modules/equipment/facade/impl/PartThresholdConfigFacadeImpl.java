package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDTO;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.PartThresholdConfig;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;
import com.izpan.modules.equipment.facade.IPartThresholdConfigFacade;
import com.izpan.modules.equipment.repository.mapper.DevicePartMapper;
import com.izpan.modules.equipment.service.IPartThresholdConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartThresholdConfigFacadeImpl implements IPartThresholdConfigFacade {

    private final IPartThresholdConfigService partThresholdConfigService;
    private final DevicePartMapper devicePartMapper;

    @Override
    public PartThresholdConfigVO getByPartId(Long partId) {
        PartThresholdConfig config = partThresholdConfigService.getByPartId(partId);
        if (config == null) {
            PartThresholdConfigVO vo = new PartThresholdConfigVO();
            vo.setPartId(partId);
            vo.setCheckInterval(60);
            vo.setConfigStatus(1);
            DevicePart part = devicePartMapper.selectById(partId);
            if (part != null) {
                vo.setPartName(part.getPartName());
                vo.setPartCode(part.getPartCode());
            }
            return vo;
        }
        PartThresholdConfigVO vo = BeanUtil.copyProperties(config, PartThresholdConfigVO.class);
        DevicePart part = devicePartMapper.selectById(partId);
        if (part != null) {
            vo.setPartName(part.getPartName());
            vo.setPartCode(part.getPartCode());
        }
        return vo;
    }

    @Override
    @Transactional
    public boolean saveOrUpdateConfig(PartThresholdConfigDTO dto) {
        return partThresholdConfigService.saveOrUpdateConfig(dto);
    }
}
