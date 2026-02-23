package com.izpan.modules.equipment.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.domain.entity.FactoryInfo;
import com.izpan.modules.equipment.domain.vo.FactoryAreaTreeVO;
import com.izpan.modules.equipment.domain.vo.FactoryInfoVO;
import com.izpan.modules.equipment.facade.IFactoryInfoFacade;
import com.izpan.modules.equipment.service.IFactoryInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactoryInfoFacadeImpl implements IFactoryInfoFacade {

    private final IFactoryInfoService factoryInfoService;

    @Override
    public RPage<FactoryInfoVO> listFactoryInfoPage(PageQuery pageQuery, FactoryInfoSearchDTO searchDTO) {
        IPage<FactoryInfo> factoryInfoPage = factoryInfoService.listFactoryInfoPage(pageQuery, searchDTO);
        
        List<FactoryInfoVO> voList = factoryInfoPage.getRecords().stream()
                .map(factoryInfo -> BeanUtil.copyProperties(factoryInfo, FactoryInfoVO.class))
                .collect(Collectors.toList());
        
        return new RPage<>(factoryInfoPage.getCurrent(), factoryInfoPage.getSize(), voList, factoryInfoPage.getPages(), factoryInfoPage.getTotal());
    }

    @Override
    public FactoryInfoVO getFactoryInfoById(Long id) {
        FactoryInfo factoryInfo = factoryInfoService.getFactoryInfoById(id);
        return BeanUtil.copyProperties(factoryInfo, FactoryInfoVO.class);
    }

    @Override
    public List<FactoryInfoVO> listAllFactoryInfo() {
        return factoryInfoService.listAllFactoryInfo().stream()
                .map(factoryInfo -> BeanUtil.copyProperties(factoryInfo, FactoryInfoVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FactoryAreaTreeVO> getFactoryAreaTree() {
        return factoryInfoService.getFactoryAreaTree();
    }

    @Override
    @Transactional
    public boolean addFactoryInfo(FactoryInfoAddDTO addDTO) {
        return factoryInfoService.addFactoryInfo(addDTO);
    }

    @Override
    @Transactional
    public boolean updateFactoryInfo(FactoryInfoUpdateDTO updateDTO) {
        return factoryInfoService.updateFactoryInfo(updateDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryInfo(FactoryInfoDeleteDTO deleteDTO) {
        return factoryInfoService.deleteFactoryInfo(deleteDTO);
    }

    @Override
    @Transactional
    public boolean deleteFactoryInfoByIds(List<Long> ids) {
        return factoryInfoService.deleteFactoryInfoByIds(ids);
    }
}
