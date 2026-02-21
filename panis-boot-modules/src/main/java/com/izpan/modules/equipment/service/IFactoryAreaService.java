package com.izpan.modules.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.equipment.domain.entity.FactoryArea;
import com.izpan.modules.equipment.domain.dto.FactoryAreaAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaUpdateDTO;

import java.util.List;

public interface IFactoryAreaService extends IService<FactoryArea> {

    IPage<FactoryArea> listFactoryAreaPage(PageQuery pageQuery, FactoryAreaSearchDTO searchDTO);

    FactoryArea getFactoryAreaById(Long id);

    List<FactoryArea> listAllFactoryArea();

    boolean addFactoryArea(FactoryAreaAddDTO addDTO);

    boolean updateFactoryArea(FactoryAreaUpdateDTO updateDTO);

    boolean deleteFactoryArea(FactoryAreaDeleteDTO deleteDTO);

    boolean deleteFactoryAreaByIds(List<Long> ids);
}
